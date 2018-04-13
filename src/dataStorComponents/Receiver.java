package dataStorComponents;

import java.io.IOException;

import shared.DataStor;
import srv.SMain;

public class Receiver implements Runnable {
	private DataStor srv;
  
    public Receiver(DataStor dataStor) {
    	// Inherit all functionallity from main
    	this.srv = dataStor;
	}

	public void run() {
    	// Listen forever
    	while (true) {
	        try {
	        	// Grab a datagram, put it in the queue
				srv.getInboundQueue().add(srv.getInSktUDP().receive());
			    // wake the watchdog
				srv.getWatchdogThread().interrupt();

			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
}