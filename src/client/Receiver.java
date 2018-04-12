package client;

import java.io.IOException;

public class Receiver implements Runnable {
	private CMain cl;
  
    public Receiver(CMain cMain) {
    	// Inherit all functionallity from main
    	this.cl = cMain;
	}

	public void run() {
    	// Listen forever
    	while (true) {
	        try {
	        	// Grab a datagram, put it in the queue
				cl.getInboundQueue().add(cl.getInSktUDP().receive());
			    // wake the watchdog
				cl.getWatchdog().interrupt();

			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
}