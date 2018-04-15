package dataStorComponents;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.util.Iterator;

import shared.DataStor;

public class UploadSlotThread implements Runnable {
	private DataStor dataStor;
	// Awake it, maybe it benefits speed if a thread was active at time of interrupt
	private int pollQueueTime = 9000;
  
    // standard constructors
    public UploadSlotThread (DataStor dataStor) {
    	this.dataStor = dataStor;
    }
  
    public void run() {
    	
    	while (true) {
			try {
				Thread.sleep(pollQueueTime);
			} catch (InterruptedException e) {
				// AWAKE!!
			}
			// Check for messages
			//System.out.print("poll");
			// Concurrent, so it might be that the queues fill after iterator
			// While loop till all queues are empty (return null)
			Boolean somePacketsInQueueLeft = true;
			while (somePacketsInQueueLeft) {
				// Iterate over upload slots
				Iterator<TransferInfoOutFile> iter = dataStor.getTransferDB().getUploadSlots().iterator();
				// Check if the session id and datagram port and IP are enlisted.
				// The iterator makes sure FUP is achieved
				
				// Prove the truth, or stop looping
				somePacketsInQueueLeft = false;
				while (iter.hasNext()) {
					TransferInfoOutFile current = iter.next();
					Integer chunckId = current.getUploadSlotChuncks().poll();
					if (null!=chunckId) {
						System.out.println("REQUEST FOR PKT:"+chunckId);
						current.uploadChunckDatagram(chunckId);
						System.out.println("SENT FOR PKT:"+chunckId);
						// Send back FS
						somePacketsInQueueLeft = true;
					}
				}
			}
				
			
			
			
		} // end while true server loop

        

    }
}