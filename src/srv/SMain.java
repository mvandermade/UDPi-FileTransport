package srv;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import client.Sender;
import shared.FileMan;
import shared.InSktUDP;

public class SMain {
	
	private final Queue<DatagramPacket> inboundQueue = new ConcurrentLinkedQueue<DatagramPacket>();
	private final Thread watchdog;
	private final InSktUDP inSktUDP;
	private int datagramSize = 256;
	private int listnerPort = 4445;
	private FileMan fileMan;
	
	public SMain() throws IOException {
		
		// Filemanager in root
		String basePath = new File("").getAbsolutePath()+"/fileroot";
		this.fileMan = new FileMan(basePath);
		
		// Start Watchdog that will process the inboundQueue
		// Watchdog can send messages
	    watchdog = new Thread(new Watchdog(this));
	    System.out.println("booted watchdog");
	    watchdog.start();
	    
	    // Socket is managed by SMain
		inSktUDP = new InSktUDP(datagramSize, listnerPort);
		
		// Receiver gets the watchdog thread reference to toggle on new message
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

	public int getDatagramSize() {
		return datagramSize;
	}

	public void setDatagramSize(int datagramSize) {
		this.datagramSize = datagramSize;
	}

	public FileMan getFileMan() {
		return fileMan;
	}

	public void setFileMan(FileMan fileMan) {
		this.fileMan = fileMan;
	}

}
