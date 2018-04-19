package dataStorComponents;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import shared.ByteCalculator;
import shared.DataStor;

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
	private int lastCountOK;
	private long retransmissionTimeStamp;
	

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
		this.lastCountOK=0;
		// highest OK count, to determine stalls
		this.deltaSum = 0;
		this.timerLastScrape = getLongTimeEpochSecond();
		this.timerLastPkt = getLongTimeEpochSecond();
		
		this.retransmissionTimeStamp = getLongTimeEpochSecond();
		
		// Ceil, the last chunck needs to be padded
		this.chunckTotal = Integer.parseInt(parts[2]);
		// Initialises to 0
		this.chuncksRetransmitTimes = new int[chunckTotal];
		this.chunckOK = new boolean[chunckTotal];
		// Calculate number of chuncksize
		this.timeStamp = this.getLongTimeEpochSecond();
		
		this.file = dataStor.getFileMan().makefile(filename, fileSizeBytes);
		
		try {
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
		//System.out.println("write to disk"+countOK+"/"+chunckTotal);
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
				//System.out.println("Already have:"+chunckId);
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
			updateSpeedElapsedTime();
			
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
			// Packet loss prevention
			dataStor.getInSktUDP().sendStr("finish "+parts[5], this.getReqAddress(), this.getReqPort());
			dataStor.getInSktUDP().sendStr("finish "+parts[5], this.getReqAddress(), this.getReqPort());
			dataStor.getInSktUDP().sendStr("finish "+parts[5], this.getReqAddress(), this.getReqPort());
			System.out.println("ending:"+parts[5]);

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void updateSpeedElapsedTime() {
		this.fileTimeElapsedSeconds = deltaSum/1000000000;
		this.fileSpeed = (int)((float)fileSizeBytes/( fileTimeElapsedSeconds ));
	}
	
	public long scrape(long scrapeTimer) {
		//System.out.println("im scraped"+file.getName());
		// Used by the scraper in time intervals. If the scrape time is much more than the mean delta, perform check on missing packets.
		int countOC = 1;
		// Protect divide 0
		if (countOK !=0) {
			countOC = countOK;
		}
		
		long theoreticalNextDeltaPoint = 3*(this.timerLastScrape-this.timeStamp)/(long)countOC;
		//System.out.println("nextdeltapoint");
		//System.out.println(10*(deltaSum/countOC));
		//System.out.println("scrapetimer");
		//System.out.println(scrapeTimer);
		//System.out.println("my wish:");
		//System.out.println(theoreticalNextDeltaPoint+this.timerLastScrape);
		
		//System.out.println((theoreticalNextDeltaPoint+this.timerLastScrape) - scrapeTimer);
		
		// Check if the scrape for this file OK (could be in queue)
		if (scrapeTimer > theoreticalNextDeltaPoint+this.timerLastScrape) {
			
			this.timerLastScrape = getLongTimeEpochSecond();
			// yes it is time for a scrape
			// Report ANY false to the server
			//System.out.println((this.retransmissionTimeStamp+theoreticalNextDeltaPoint*2) < scrapeTimer);
			// This is because 0 is most likely to receive the initial udp packets
			if (this.lastCountOK == this.countOK && this.countOK!=0 && (this.retransmissionTimeStamp+theoreticalNextDeltaPoint*2) < scrapeTimer) {
				manualScrape();
			} else {
				// Record
				//System.out.println("no scrape!");
				this.lastCountOK = this.countOK;
			}
			
		}
		
		this.timerLastScrape = getLongTimeEpochSecond();
		// Return the timer for estimation of next poll
		
		return theoreticalNextDeltaPoint;
		
	}


	public void manualScrape() {
		System.out.println("retransmissions requested by scraper");
		this.retransmissionTimeStamp = getLongTimeEpochSecond();
		for (int i = 0; i < chunckOK.length; i++) {
			if (!chunckOK[i]) {
				dataStor.getInSktUDP().sendChunckRequestTo(this.getSessionId(), i, this.getReqAddress(), this.getReqPort());
				chuncksRetransmitTimes[i]++;
				
				//System.out.println("retransmission request for"+i);
			}
		}
	}
	
	public String reportDownloadInfo() {
		// TODO Auto-generated method stub
		updateSpeedElapsedTime();
		
		String response = "\n"+this.file.getName()+" - "+dataStor.getFileMan().sizeHumanReadableStr(this.fileSizeBytes)+"\t"+((float)countOK/(float)chunckTotal)*100+"%\n"
				+"BlocksOK:"+countOK+"/"+chunckTotal+"\n"+
				"totalSpeed:"+dataStor.getFileMan().sizeHumanReadableStr(fileSpeed)+"/s, elapsed time: "+fileTimeElapsedSeconds+" total"+"\n"
				+"Retransmissions: "+IntStream.of(chuncksRetransmitTimes).sum()+"\n--";
		return response;
	}



	

}
