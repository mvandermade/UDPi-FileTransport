package dataStorComponents;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import shared.DataStor;
import srv.SMain;

public class TransferInfoOutFile {
	private DataStor srv;
	byte sessionId; // To be set by sender
	File file;
	int reqPort;
	InetAddress reqAddress;
	int chunckSize; // For the file pointer
	int fileSizeBytes;	
	String shaChecksum;
	
	int chunckTotal; // Total chuncks
	int chuncks[]; // Contains a number which represents transmission count
	long timeStamp;
	
	
	public TransferInfoOutFile(File file, int port, InetAddress address, DataStor dataStor) throws NoSuchAlgorithmException, IOException {
		this.srv = dataStor;
		this.file = file;
		this.reqPort = port;
		this.reqAddress = address;
		this.chunckSize = srv.getChuncksize();
		this.fileSizeBytes = (int) file.length();
		this.shaChecksum = srv.getFileMan().getHash(file);
		
		// Ceil, the last chunck needs to be padded
		this.chunckTotal = (int) Math.ceil((double)fileSizeBytes / (double)chunckSize);
		// Initialises to 0
		chuncks = new int[chunckTotal];
		// Calculate number of chuncksize
		this.timeStamp = ZonedDateTime.now().toInstant().toEpochMilli();
	}


	public String strReport() {
		// TODO Auto-generated method stub
		String response = "";
		response+= "OK,"+fileSizeBytes+","+chunckTotal+","+chunckSize+","+shaChecksum+","+srv.getTransferDB().getSessionCount();
		return response;
	}
	

}
