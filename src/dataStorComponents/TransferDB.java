package dataStorComponents;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import shared.DataStor;

public class TransferDB {
	
	// Thread safe needed ? volatile in principle watchdog handles all.
	private final Queue<TransferInfoOutFile> uploadSlots;
	private final DataStor dataStor;
	private final Queue<DatagramPacket> outboundDatagramQueue;
	private final Queue<TransferInfoOutFile> awaitingStartTransferList;
	private final Queue<TransferInfoInFile> downloadSlots;

	
	public Queue<TransferInfoInFile> getDownloadSlots() {
		return downloadSlots;
	}

	public TransferDB(DataStor dataStor) {
		this.dataStor = dataStor;
		// awaiting start transfer (bell) symbol queue.
		awaitingStartTransferList = new ConcurrentLinkedQueue<>();
		// Thread safety
		uploadSlots = new ConcurrentLinkedQueue<>();
		downloadSlots = new ConcurrentLinkedQueue<>();
		// 
		outboundDatagramQueue = new ConcurrentLinkedQueue<>();
	}

	public Queue<TransferInfoOutFile> getUploadSlots() {
		return uploadSlots;
	}

	private byte sessionCount = 0x00; 

	public String newOutboundTransfer(String filename, DatagramPacket datagramPacket) {
		// Actually write this to the console
		String response = "";
		
		File f = new File(dataStor.getBasePath()+"/"+filename);
		
		if(f.exists() && !f.isDirectory()) {
			try {
				TransferInfoOutFile temp = new TransferInfoOutFile(f, datagramPacket.getPort(), datagramPacket.getAddress(), dataStor);
				response += temp.strReport();
				// Client needs to report first, enqueue the outbound file object already
				awaitingStartTransferList.add(temp);
				sessionCount++;
				
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
	
	public void enqueueWholeFile(DatagramPacket datagramPacket) {
		// BELL CONTAINS [0]<BELL>[1]<SESSION ID>
		
		// First check if the client really requested this file
		
		Iterator<TransferInfoOutFile> iter = awaitingStartTransferList.iterator();
		TransferInfoOutFile fileToEnqueue = null;
		// Check if the session id and datagram port and IP are enlisted.
		while (iter.hasNext()) {
			TransferInfoOutFile current = iter.next();
			if (current.getReqAddress().equals(datagramPacket.getAddress())
					&& current.getReqPort()==datagramPacket.getPort()
					&& current.getSessionId()==datagramPacket.getData()[1]) {
				// Keep this entry from the queue
				fileToEnqueue = current;
				break;
			}
		}

		// If the packet was enqueued, remove it from the awaitingStartTransferList
		if (null!=fileToEnqueue) {
			
			
			
			if (countUploadSlots() > 50) {
				System.out.println("SERVER BUSY, request BELL again!");
				
				
			} else {
				System.out.println("allocate uploadslot");
				allocateUploadslot(datagramPacket, fileToEnqueue);

			}
		}

	}

	public void allocateUploadslot(DatagramPacket datagramPacket, TransferInfoOutFile fileToEnqueue) {
		// Clean the concurrentlyLinkedList of the found item.
		boolean canStop = false;
		
		while (!canStop) {
			// Iterate over upload slots
			Iterator<TransferInfoOutFile> iter = awaitingStartTransferList.iterator();
			// Check if the session id and datagram port and IP are enlisted.
			// The iterator makes sure FUP is achieved
			
			// Prove the truth, or stop looping
			canStop = false;
			while (iter.hasNext()) {
				TransferInfoOutFile current = iter.next();
				
				//System.out.println("Port: "+current.getReqPort()+"__"+datagramPacket.getPort());
				//System.out.println("Session: "+current.getSessionId()+"__"+datagramPacket.getData()[1]);
				
				if (current.getReqAddress().equals(datagramPacket.getAddress())
						&& current.getReqPort() == datagramPacket.getPort()
						&& current.getSessionId() == datagramPacket.getData()[1]) {
					// Add the file to the uploadslot
					uploadSlots.add(fileToEnqueue);
					System.out.println("uploadslots.add true");
					canStop = true;
				}
			}
		}
		
		//TODO HERE SHOULD BE FUNCTION THAT REMOVES THE SESSION FROM LIST

		//System.out.println("uploadslots.add");
		// Put it in the active queue
		
		// Interrupt uploader

		try {
			//System.out.println("emitting");
			dataStor.getInSktUDP().sendStrReplyTo("Uploadslot emitting"+fileToEnqueue.getChunckTotal(), datagramPacket);
			for (int i = 0; i < fileToEnqueue.getChunckTotal(); i++) {
				fileToEnqueue.uploadEnqueueChunckId(i);
			}
			dataStor.getUploadSlot().unwaitThread();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte getSessionCount() {
		return sessionCount;
	}

	public Queue<DatagramPacket> getOutboundDatagramQueue() {
		return outboundDatagramQueue;
	}

	public void prepareDownloadSlot(TransferInfoInFile transferInfoInFile) {
		// TODO Auto-generated method stub
		downloadSlots.add(transferInfoInFile);
		
	}
	
	public void handleInboundDownloadChunck(DatagramPacket datagramPacket) {
		
		Iterator<TransferInfoInFile> iter = dataStor.getTransferDB().getDownloadSlots().iterator();
		// Check if the session id and datagram port and IP are enlisted.
		// The iterator makes sure FUP is achieved
		while (iter.hasNext()) {
			TransferInfoInFile current = iter.next();		
			
			if (current.getReqAddress().equals(datagramPacket.getAddress())
					&& current.getReqPort() == datagramPacket.getPort()
					&& current.getSessionId() == datagramPacket.getData()[1]) {
				// Add the chunck, it's okay
				current.writeDatagramToDisk(datagramPacket);
				break;
			}
		}
	}

	public void handleUploadRequest(DatagramPacket datagramPacket) {
		// TODO Auto-generated method stub
		Iterator<TransferInfoOutFile> iter = dataStor.getTransferDB().getUploadSlots().iterator();
		// Check if the session id and datagram port and IP are enlisted.
		// The iterator makes sure FUP is achieved
		
		while (iter.hasNext()) {
			TransferInfoOutFile current = iter.next();
			if (current.getReqAddress().equals(datagramPacket.getAddress())
					&& current.getReqPort() == datagramPacket.getPort()
					&& current.getSessionId() == datagramPacket.getData()[1]) {
				// Add the chunck, it's okay
				current.addUploadRequestChunckFromDatagramPacket(datagramPacket);
				break;
			}
		}
	}
	
	
	public int countUploadSlots() {
		Iterator<TransferInfoOutFile> iter = this.getUploadSlots().iterator();
		// Check if the session id and datagram port and IP are enlisted.
		// The iterator makes sure FUP is achieved
		int count = 0;
		
		while (iter.hasNext()) {
			iter.next();		
			count++;
			
		}
		return count;
	}
	
	

}
