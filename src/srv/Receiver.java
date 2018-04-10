package srv;

import java.io.IOException;

public class Receiver implements Runnable {
	private SMain srv;
  
    public Receiver(SMain sMain) {
    	// Inherit all functionallity from main
    	this.srv = sMain;
	}

	public void run() {
    	// Listen forever
    	while (true) {
	        try {
	        	// Grab a datagram, put it in the queue
				srv.getInboundQueue().add(srv.getInSktUDP().receive());
			    // wake the watchdog
				srv.getWatchdog().interrupt();

			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
}