package dataStorComponents;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import shared.ByteCalculator;
import shared.DataStor;
import srv.SMain;

public class TransferInfoOutFile {
	private DataStor dataStor;
	File file;
	int reqPort;
	InetAddress reqAddress;
	int chunckSize; // For the file pointer
	int fileSizeBytes;	
	String shaChecksum;
	
	int chunckTotal; // Total chuncks
	public int getFileSizeBytes() {
		return fileSizeBytes;
	}


	public int getChunckTotal() {
		return chunckTotal;
	}

	int[] chuncks; // Contains a number which represents transmission count
	long timeStamp;
	private byte sessionId;
	
	private final Queue<Integer> uploadSlotChuncks;
	private FileChannel readChannel;
	private RandomAccessFile aFile;
	
	public TransferInfoOutFile(File file, int port, InetAddress address, DataStor dataStor) throws NoSuchAlgorithmException, IOException {
		this.dataStor = dataStor;
		this.file = file;
		this.reqPort = port;
		this.reqAddress = address;
		this.chunckSize = dataStor.getChuncksize();
		this.fileSizeBytes = (int) file.length();
		this.shaChecksum = dataStor.getFileMan().getHash(file);
		this.sessionId = dataStor.getTransferDB().getSessionCount();
		
		// Ceil, the last chunck needs to be padded
		this.chunckTotal = (int) Math.ceil((double)fileSizeBytes / (double)chunckSize);
		// Initialises to 0
		chuncks = new int[chunckTotal];
		// Calculate number of chuncksize
		this.timeStamp = ZonedDateTime.now().toInstant().toEpochMilli();
		// To be filled by Receiver
		this.uploadSlotChuncks = new ConcurrentLinkedQueue<>();
		
		// assign a disk access hook
		this.aFile = new RandomAccessFile(file.getPath(), "r");
		this.readChannel = aFile.getChannel();
		
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


	public String strReport() {
		// TODO Auto-generated method stub
		String response = "";
		response+= "OK,"+fileSizeBytes+","+chunckTotal+","+chunckSize+","+shaChecksum+","+sessionId;
		return response;
	}


	public Queue<Integer> getUploadSlotChuncks() {
		return uploadSlotChuncks;
	}

	public void addUploadRequestChunckFromDatagramPacket(DatagramPacket datagramPacket) {
		
		uploadEnqueueChunckId(shared.ByteCalculator.byteArrayToLeInt(Arrays.copyOfRange(datagramPacket.getData(), dataStor.getPacketPointerChunckId(), dataStor.getPacketPointerCRC())));
		
	}
		
	public void uploadEnqueueChunckId(int chunckId) {
		chuncks[chunckId] = chuncks[chunckId]+1;
		uploadSlotChuncks.add(chunckId);
		dataStor.getUploadSlot().unwaitThread();
		// These packets should be picked up automatically
	}
	
	public byte[] grabChunckFromDisk(int chunckId) {
		ByteBuffer buf = ByteBuffer.allocate(chunckSize);
		

		// Output
		byte[] byteArray = null;
		
		try {
			readChannel.position(chunckId*chunckSize);
			int bytesRead = readChannel.read(buf);
			// Assume the buffer is filled up (only the last chunck isn't the same size, potentially)
			byteArray = Arrays.copyOfRange(ByteCalculator.byteBufferToArray(buf), 0, bytesRead);

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/// TEST
		
		
				
		return byteArray;
		
	}
	
	public void ceaseTransmission() {
		try {
			this.readChannel.close();
			this.aFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void uploadChunckDatagram(Integer chunckId) {
		// TODO Auto-generated method stub
		
		dataStor.getInSktUDP().sendChunckFromDisk(this.getSessionId(), ByteCalculator.intToLeByteArray(chunckId), grabChunckFromDisk(chunckId),
				this.getReqAddress(), this.getReqPort());
	}

}
