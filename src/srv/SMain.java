package srv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import client.Sender;
import shared.InSktUDP;

public class SMain {
	
	private final Queue<DatagramPacket> inboundQueue = new ConcurrentLinkedQueue<DatagramPacket>();
	private final Thread watchdog;
	private final InSktUDP inSktUDP;
	

	public SMain() throws IOException {
		
	    watchdog = new Thread(new Watchdog(this));
	    System.out.println("booted watchdog");
	    watchdog.start();
	    
	    // Socket is managed by SMain
	    
		inSktUDP = new InSktUDP(256);
		
		// Receiver gets the watchdog thread reference
		
	    Thread receiver = new Thread(new Receiver(this));
	    System.out.println("Server recv booted");
	    receiver.start();
	}

	public Queue<DatagramPacket> getInboundQueue() {
		return inboundQueue;
	}

	public InSktUDP getInSktUDP() {
		return inSktUDP;
	}

	public Thread getWatchdog() {
		return watchdog;
	}

}
