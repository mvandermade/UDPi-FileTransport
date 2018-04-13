package shared;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import dataStorComponents.FileMan;
import dataStorComponents.InSktUDP;
import dataStorComponents.InboundDatagramUtil;
import dataStorComponents.Receiver;
import dataStorComponents.TransferDB;
import dataStorComponents.Watchdog;
import srv.InboundServerUtil;

public class DataStor {
	private String basePath;
	private FileMan fileMan;
	private int datagramSize = 256;
	private int headerSize = 6; // Header is FS|4-byte-chunckID|1-byte-session|<>
	private int chuncksize;
	private TransferDB transferDB;
	private Thread watchdog; 
	private int listnerPort = 4445;
	private InSktUDP inSktUDP = null;
	private Thread UDPreceiver;
	private final Queue<DatagramPacket> inboundQueue = new ConcurrentLinkedQueue<>();
	private InboundDatagramUtil inboundDatagramUtil;

	
	public DataStor(int listnerPort, String relativePath, InboundDatagramUtil inboundDatagramUtil) {
		this.listnerPort = listnerPort;
		this.setInboundDatagramUtil(inboundDatagramUtil);
		// Filemanager in root
		this.basePath = new File("").getAbsolutePath() + "/" + relativePath;
		this.setFileMan(new FileMan(basePath));
		
		// TransferDB for keeping the transfers
		this.setTransferDB(new TransferDB(this));
		// chucksize is reduced by 9 due to header
		setChuncksize(datagramSize - headerSize);
		
	    setWatchdog(new Thread(new Watchdog(this)));
	    
	    
	    try {
			this.setInSktUDP(new InSktUDP(datagramSize, listnerPort));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    setUDPreceiver(new Thread(new Receiver(this)));
	}

	public int getChuncksize() {
		return chuncksize;
	}

	public void setChuncksize(int chuncksize) {
		this.chuncksize = chuncksize;
	}

	public TransferDB getTransferDB() {
		return transferDB;
	}

	public void setTransferDB(TransferDB transferDB) {
		this.transferDB = transferDB;
	}

	public Thread getWatchdogThread() {
		return watchdog;
	}

	public void setWatchdog(Thread watchdog) {
		this.watchdog = watchdog;
	}

	public InSktUDP getInSktUDP() {
		return inSktUDP;
	}

	public void setInSktUDP(InSktUDP inSktUDP) {
		this.inSktUDP = inSktUDP;
	}

	public Thread getUDPreceiver() {
		return UDPreceiver;
	}

	public void setUDPreceiver(Thread uDPreceiver) {
		UDPreceiver = uDPreceiver;
	}

	public FileMan getFileMan() {
		return fileMan;
	}

	public void setFileMan(FileMan fileMan) {
		this.fileMan = fileMan;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public Queue<DatagramPacket> getInboundQueue() {
		return inboundQueue;
	}

	public InboundDatagramUtil getInboundDatagramUtil() {
		return inboundDatagramUtil;
	}

	public void setInboundDatagramUtil(InboundDatagramUtil inboundDatagramUtil) {
		this.inboundDatagramUtil = inboundDatagramUtil;
	}

}
