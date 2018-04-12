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
	private int headerSize = 6; // Header is FS|4-byte-chunckID|1-byte-session|<>
	private int chuncksize;
	private int listnerPort = 4445;
	private FileMan fileMan;
	private TransferDB transferDB; 
	private String basePath;
	
	
	public SMain() throws IOException {
		
		// Filemanager in root
		setBasePath(new File("").getAbsolutePath()+"/fileroot");
		this.fileMan = new FileMan(basePath);
		
		// TransferDB for keeping the transfers
		this.setTransferDB(new TransferDB(this));
		// chucksize is reduced by 9 due to header
		chuncksize = datagramSize - headerSize;
		
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

	public TransferDB getTransferDB() {
		return transferDB;
	}

	public void setTransferDB(TransferDB transferDB) {
		this.transferDB = transferDB;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public int getChuncksize() {
		return chuncksize;
	}

	public void setChuncksize(int chuncksize) {
		this.chuncksize = chuncksize;
	}

}
