package dataStorComponents;

import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import shared.DataStor;

public class TransferDB {
	
	// Thread safe needed ? volatile in principle watchdog handles all.
	private final ArrayList<TransferInfoOutFile> outboundTransferList;
	private DataStor dataStor;
	private final Queue<DatagramPacket> outboundDatagramQueue = new ConcurrentLinkedQueue<>();

	
	public TransferDB(DataStor dataStor) {
		this.dataStor = dataStor;
		outboundTransferList = new ArrayList<>();
	}

	private byte sessionCount = 0x00; 

	public String newOutboundTransfer(String filename, int port, InetAddress address) {
		// Actually write this to the console
		String response = "";
		
		File f = new File(dataStor.getBasePath()+"/"+filename);
		
		if(f.exists() && !f.isDirectory()) {
			try {
				TransferInfoOutFile temp = new TransferInfoOutFile(f, port, address, dataStor);
				response += temp.strReport();
				sessionCount++;
				outboundTransferList.add(temp);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		} else {
			response = "404FILENOTFOUND";
		}
		
		return response;
		
	}
	
	public void enqueueInitialDownload() {
		//outboundDatagramQueue.add(arg0);
	}
	
	public byte getSessionCount() {
		return sessionCount;
	}

	public Queue<DatagramPacket> getOutboundDatagramQueue() {
		return outboundDatagramQueue;
	}

}
