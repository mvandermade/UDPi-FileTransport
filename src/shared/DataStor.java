package shared;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import dataStorComponents.FileMan;
import dataStorComponents.InSktUDP;
import dataStorComponents.InboundDatagramUtil;
import dataStorComponents.ScrapeAgent;
import dataStorComponents.UDPReceiver;
import dataStorComponents.TransferDB;
import dataStorComponents.UploadSlot;
import dataStorComponents.Watchdog;

public class DataStor {
	private String basePath;
	private FileMan fileMan;
	private int datagramSize = 1400; // MTU of the network, ad hoc was ~1500MTU
	private int headerSize = 14; // also set as beginning of the content block. currently: <1cmdtype><1sessionid><4byte chunckId><8byte CR32>
	private int chuncksize;
	private TransferDB transferDB;
	private Thread watchdogThread; 
	private InSktUDP inSktUDP = null;
	private Thread UDPreceiverThread;
	private final Queue<DatagramPacket> inboundQueue = new ConcurrentLinkedQueue<>();
	private InboundDatagramUtil inboundDatagramUtil;
	private int listnerPort;
	private Thread uploadSlotThread;
	private Watchdog watchdog;
	private UploadSlot uploadSlot;
	private ScrapeAgent scrapeAgent;
	private Thread scrapeAgentThread;

	
	public UploadSlot getUploadSlot() {
		return uploadSlot;
	}

	public DataStor(int listnerPort, String relativePath, InboundDatagramUtil inboundDatagramUtil) {
		this.setListnerPort(listnerPort);
		this.setInboundDatagramUtil(inboundDatagramUtil);
		// Filemanager in root
		this.basePath = new File("").getAbsolutePath() + "/" + relativePath;
		this.setFileMan(new FileMan(basePath));
		
		// TransferDB for keeping the transfers
		this.setTransferDB(new TransferDB(this));
		// chucksize is reduced by 6 due to header
		setChuncksize(datagramSize - headerSize);
		
		this.watchdog = new Watchdog(this);
	    setWatchdogThread(new Thread(watchdog));
	    
	    this.uploadSlot = new UploadSlot(this);
	    this.setUploadSlotThread(new Thread(uploadSlot));
	    
	    this.setScrapeAgent(new ScrapeAgent(this));
	    this.setScrapeAgentThread(new Thread(scrapeAgent));
	    
	    try {
			this.setInSktUDP(new InSktUDP(this, datagramSize, listnerPort));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    setUDPreceiver(new Thread(new UDPReceiver(this)));
	}

	public Watchdog getWatchdog() {
		return watchdog;
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
		return watchdogThread;
	}

	public void setWatchdogThread(Thread watchdog) {
		this.watchdogThread = watchdog;
	}

	public InSktUDP getInSktUDP() {
		return inSktUDP;
	}

	public void setInSktUDP(InSktUDP inSktUDP) {
		this.inSktUDP = inSktUDP;
	}

	public Thread getUDPreceiver() {
		return UDPreceiverThread;
	}

	public void setUDPreceiver(Thread uDPreceiver) {
		UDPreceiverThread = uDPreceiver;
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

	public Thread getUploadSlotThread() {
		return uploadSlotThread;
	}

	public void setUploadSlotThread(Thread uploadSlotThread) {
		this.uploadSlotThread = uploadSlotThread;
	}

	public int getPacketPointerContents() {
		// TODO Auto-generated method stub
		return this.headerSize;
	}
	
	public int getPacketPointerCRC() {
		// TODO Auto-generated method stub
		return this.headerSize-8;
	}

	public int getPacketPointerChunckId() {
		// TODO Auto-generated method stub
		return this.headerSize-8-4;
	}

	public Thread getScrapeAgentThread() {
		return scrapeAgentThread;
	}

	public void setScrapeAgentThread(Thread scrapeAgentThread) {
		this.scrapeAgentThread = scrapeAgentThread;
	}

	public ScrapeAgent getScrapeAgent() {
		return scrapeAgent;
	}

	public void setScrapeAgent(ScrapeAgent scrapeAgent) {
		this.scrapeAgent = scrapeAgent;
	}

	public int getListnerPort() {
		return listnerPort;
	}

	public void setListnerPort(int listnerPort) {
		this.listnerPort = listnerPort;
	}

}
