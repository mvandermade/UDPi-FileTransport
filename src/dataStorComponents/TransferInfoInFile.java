package dataStorComponents;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

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
	int[] chuncks; // Contains a number which represents transmission count
	long timeStamp;
	private DataStor dataStor;
	

	public TransferInfoInFile(String[] parts, String filename, DatagramPacket datagramPacket, DataStor dataStor) {
		this.dataStor = dataStor;
		
		this.reqPort = datagramPacket.getPort();
		this.reqAddress = datagramPacket.getAddress();
		this.chunckSize = Integer.parseInt(parts[3]);
		this.fileSizeBytes = Integer.parseInt(parts[1]);
		this.shaChecksumRemote = parts[4];
		
		// Ceil, the last chunck needs to be padded
		this.chunckTotal = Integer.parseInt(parts[2]);
		// Initialises to 0
		this.chuncks = new int[chunckTotal];
		// Calculate number of chuncksize
		this.timeStamp = ZonedDateTime.now().toInstant().toEpochMilli();
		
		this.file = dataStor.getFileMan().makefile(filename, fileSizeBytes);
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
	

}
