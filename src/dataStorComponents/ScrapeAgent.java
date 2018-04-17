package dataStorComponents;

import java.util.Iterator;

public class ScrapeAgent implements Runnable {
	private shared.DataStor dataStor;
	
	private boolean waitState = true;
  
    // standard constructors
    public ScrapeAgent (shared.DataStor dataStor) {
    	this.dataStor = dataStor;
    }
  
    public void run() {
    	
    	while (true) {
			
			// Check for messages
			waitThread();
			waitForSignal();
			
			boolean done = false;
			while (!done) {
				// Prove it's not done
				done = true;
				// Scraper is done when downloads = 0
				int downloadsActive=0;
				Iterator<TransferInfoInFile> iter = dataStor.getTransferDB().getDownloadSlots().iterator();
				// Check if the session id and datagram port and IP are enlisted.
				// The iterator makes sure FUP is achieved
				
				long backOffGlobalLowest = 100000; // 100ms
				while (iter.hasNext()) {
					TransferInfoInFile current = iter.next();		
					
					// Scrape at the current time
					
					long backoffTime = current.scrape(current.getLongTimeEpochSecond());
					// Check if the backoff is lower then 10ms
					if (backoffTime < backOffGlobalLowest) {
						backOffGlobalLowest = backoffTime;
					}
					
					done = false;
					downloadsActive++;
				 }
				
				
				
				// Nano to millis
				try {
					// From nano to ms
					Thread.sleep(1000000/1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			System.out.println("Scrape OK");
			
			
		} // end while true server loop

    }
	
	public void waitForSignal() {
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