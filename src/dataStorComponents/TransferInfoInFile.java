package dataStorComponents;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import shared.ByteCalculator;
import shared.DataStor;
import srv.SMain;

public class TransferInfoInFile {
	byte sessionId; // To be set by sender
	File file;
	int reqPort;
	InetAddress reqAddress;
	int chunckSize; // For the file pointer
	int fileSizeBytes;	
	String shaChecksumRemote;
	
	int chunckTotal; // Total chuncks
	int[] chuncksRetransmitTimes; // Contains a number which represents transmission count
	boolean[] chunckOK;
	int countOK;
	
	
	long timeStamp;
	private DataStor dataStor;
	private RandomAccessFile raf;
	private long timerLastScrape;
	private long timerLastPkt;
	private long deltaSum;
	private String[] parts;
	private int fileSpeed;
	private float fileTimeElapsedSeconds;
	

	public TransferInfoInFile(String[] parts, String filename, DatagramPacket datagramPacket, DataStor dataStor) {
		this.parts = parts;
		this.dataStor = dataStor;
		this.reqPort = datagramPacket.getPort();
		this.reqAddress = datagramPacket.getAddress();
		this.chunckSize = Integer.parseInt(parts[3]);
		this.fileSizeBytes = Integer.parseInt(parts[1]);
		this.shaChecksumRemote = parts[4];
		this.sessionId = (byte) Integer.parseInt(parts[5]);
		this.countOK = 0;
		// highest OK count, to determine stalls
		this.deltaSum = 0;
		this.timerLastScrape = getLongTimeEpochSecond();
		this.timerLastPkt = getLongTimeEpochSecond();
		
		// Ceil, the last chunck needs to be padded
		this.chunckTotal = Integer.parseInt(parts[2]);
		// Initialises to 0
		this.chuncksRetransmitTimes = new int[chunckTotal];
		this.chunckOK = new boolean[chunckTotal];
		// Calculate number of chuncksize
		this.timeStamp = this.getLongTimeEpochSecond();
		
		this.file = dataStor.getFileMan().makefile(filename, fileSizeBytes);
		
		try {
			if (file.exists()) {
				Random r = new Random();
				String c = (char)(r.nextInt(26) + 'a')+""+(char)(r.nextInt(26) + 'a')+(char)(r.nextInt(26) + 'a');
				String fp = file.getAbsolutePath();
				
				
				file = new File(fp.substring(0,fp.length()-5)+"copy"+c+fp.substring(fp.length()-5,fp.length()));
			}
			raf = new RandomAccessFile(this.file.getAbsoluteFile(), "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public long getLongTimeEpochSecond() {
		// TODO Auto-generated method stub
		return System.nanoTime();
	}


	public byte getSessionId() {
		return sessionId;
	}

	public int getReqPort() {
		return reqPort;
	}

	public InetAddress getReqAddress() {
		return reqAddress;
	}
	
	public void writeDatagramToDisk(DatagramPacket datagramPacket) {
		// TODO Auto-generated method stub
		int chunckId = shared.ByteCalculator.byteArrayToLeInt(Arrays.copyOfRange(datagramPacket.getData(), dataStor.getPacketPointerChunckId(), dataStor.getPacketPointerCRC()));
		byte [] contents = Arrays.copyOfRange(datagramPacket.getData(), dataStor.getPacketPointerContents(), datagramPacket.getData().length);
		
		if(chunckId+1 == chunckTotal) {
			// This is the last chunck of the file, beware ! trailing zero's
			// Calculate the theoretical leftover bytes
			int leftoverbytes = chunckSize - ((chunckTotal*chunckSize)-fileSizeBytes);
			// Trim the UDP packet
			contents = Arrays.copyOfRange(contents, 0, leftoverbytes);
		}
		
		byte[] checksumBytesRecv = Arrays.copyOfRange(datagramPacket.getData(), dataStor.getPacketPointerCRC(), dataStor.getPacketPointerContents());

		// Calculate recv crc
		Checksum checksum = new CRC32();
		checksum.update(contents, 0, contents.length);
		byte[] checksumBytes = ByteCalculator.longToBytes(checksum.getValue());
		
		// Check if i don't already have this one
		if (ByteCalculator.bytesToLong(checksumBytes) == ByteCalculator.bytesToLong(checksumBytesRecv)) {
			
			// should be false
			if (!chunckOK[chunckId]) {
				chunckOK[chunckId] = true;
				this.countOK ++;
				long deltaT = (getLongTimeEpochSecond() - this.timerLastPkt);
				this.deltaSum = this.deltaSum + deltaT;
				this.timerLastPkt = getLongTimeEpochSecond();
				writeChunckToDisk(chunckId, contents);
				//
			} else {
				System.out.println("Already have:"+chunckId);
			}
			
			
		} else {
			chunckOK[chunckId] = false;
			System.out.println("CRC WRONG FOR:"+chunckId);
		}
		
		
		
		
		if (this.countOK == chunckTotal) {
			this.ceaseDownload();
			
			
		}
		
	}
	
	public void writeChunckToDisk(int chunckId, byte[] contents) {

		try {
			// set the file pointer at 0 position
			raf.seek(chunckSize*chunckId);
			// write
			raf.write(contents);

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
	
	public void ceaseDownload() {
		try {
			System.out.println("download OK. Report for: "+file.getName());
			this.fileTimeElapsedSeconds = deltaSum/1000000000;
			this.fileSpeed = (int)((float)fileSizeBytes/( fileTimeElapsedSeconds ));
			
			System.out.println(dataStor.getFileMan().sizeHumanReadableStr(fileSpeed)+"/s, took "+fileTimeElapsedSeconds+" seconds total"); //mb/s
			this.raf.close();
			
			//System.out.println("HASHDISK  :"+dataStor.getFileMan().getHash(this.file));
			//System.out.println("HASHSERVER:"+this.shaChecksumRemote);
			
			if (this.shaChecksumRemote.equals(dataStor.getFileMan().getHash(this.file))) {
				System.out.println("HASH OK");
			} else {
				System.out.println("Hash failed :(");
			}
			System.out.println("Client retransmit count: "+IntStream.of(chuncksRetransmitTimes).sum());
			
			// Remove from queue
			dataStor.getTransferDB().getDownloadSlots().removeIf((c)->{
				if (c.file.getAbsolutePath().equals(this.file.getAbsolutePath())) {
					//System.out.println("removed from queue");
					return true;
					
				} else {
					return false;
				}
			});
			
			// Notify the server
			dataStor.getInSktUDP().sendStr("finish "+parts[5], this.getReqAddress(), this.getReqPort());

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public long scrape(long scrapeTimer) {
		//System.out.println("im scraped"+file.getName());
		// Used by the scraper in time intervals. If the scrape time is much more than the mean delta, perform check on missing packets.
		int countOC = 1;
		// Protect divide 0
		if (countOK !=0) {
			countOC = countOK;
		}
		
		long theoreticalNextDeltaPoint = 10000*(deltaSum/countOC);
		
		// Check if the scrape is 'late'
		if (scrapeTimer > theoreticalNextDeltaPoint+this.timerLastScrape) {
			// yes it is time for a scrape
			// Report ANY false to the server
			
			// 5 sec retransmission window
			// 10MB/s should be sufficient ?
			if ((float)countOK/(float)chunckTotal > 0.99 || (this.getLongTimeEpochSecond() - this.timeStamp)/1000000000 > (this.fileSizeBytes/10000000) || (deltaSum/countOC) > 1000000000) {
				this.timeStamp = this.getLongTimeEpochSecond();
				for (int i = 0; i < chunckOK.length; i++) {
					if (!chunckOK[i]) {
						dataStor.getInSktUDP().sendChunckRequestTo(this.getSessionId(), i, this.getReqAddress(), this.getReqPort());
						chuncksRetransmitTimes[i]++;
						
						//System.out.println("retransmission request for"+i);
					}
				}
			} else {
				//System.out.println("too small");
			}
			// Make adjustments for RTT
			theoreticalNextDeltaPoint = 30000*(deltaSum/countOC);
			
		} else {
			theoreticalNextDeltaPoint = 10000*(deltaSum/countOC);
		}
		
		this.timerLastScrape = getLongTimeEpochSecond();
		// Return the timer for estimation of next poll
		
		return theoreticalNextDeltaPoint;
		
	}



	

}
