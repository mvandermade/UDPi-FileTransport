package dataStorComponents;

import java.io.IOException;

import shared.DataStor;
import srv.SMain;

public class UDPReceiver implements Runnable {
	private DataStor dataStor;
  
    public UDPReceiver(DataStor dataStor) {
    	// Inherit all functionallity from main
    	this.dataStor = dataStor;
	}

	public void run() {
    	// Listen forever
    	while (true) {
	        try {
	        	// Grab a datagram, put it in the queue
				dataStor.getInboundQueue().add(dataStor.getInSktUDP().receive());
			    //System.out.println("udp waking watchdog...");
			    
				// wake the watchdog
				dataStor.getWatchdog().unwaitThread();

			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
}