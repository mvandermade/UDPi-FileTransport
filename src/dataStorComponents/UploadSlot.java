package dataStorComponents;

import java.util.Iterator;

import shared.DataStor;

public class UploadSlot implements Runnable {
	private DataStor dataStor;
	// Awake it, maybe it benefits speed if a thread was active at time of interrupt
	
	private boolean waitState = true;
  
    // standard constructors
    public UploadSlot (DataStor dataStor) {
    	this.dataStor = dataStor;
    }
  
    public void run() {
    	
    	while (true) {
			waitThread();
			waitForSignal();
			
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
						//System.out.println("REQUEST FOR PKT:"+chunckId);
						current.uploadChunckDatagram(chunckId);
						//System.out.println("SENT FOR PKT:"+chunckId);
						// Send back FS
						somePacketsInQueueLeft = true;
					}
				}
			}
				
			
			
			
		} // end while true server loop

        

    }
    
	public void waitForSignal() {
		//System.out.println("uploader waits");
		synchronized(this) {
			while(waitState) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//System.out.println("uploader active");
		
	}
	
	public void unwaitThread() {
		synchronized(this) {
			if (this.waitState == true) {
				this.waitState = false;
				notifyAll();
			}
		}
	}
	
	public void waitThread() {
		synchronized(this) {
			this.waitState = true;
		}
	}
}