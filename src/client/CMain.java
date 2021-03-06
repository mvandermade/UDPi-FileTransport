package client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import shared.DataStor;

public class CMain {
	
	private DataStor dataStor;
	private Thread senderThread;
	private Thread keyboardInputListnerThread;
	private String mostRecentKeyboardInput;
	private InetAddress serverAddr;
	private int serverPort;
	
	private final Queue<String> keyboardInputQueue = new ConcurrentLinkedQueue<>();
	private KeyBoardInputListner keyboardInputListner;
	private KeyboardSender keyboardSender;
	private int listenPort;
	private String clientRootFolder;
	private LinkedBlockingQueue<client.MacRssiPair[]> data;
	private String serverIp;

	public KeyboardSender getKeyboardSender() {
		return keyboardSender;
	}

	public KeyBoardInputListner getKeyboardInputListner() {
		return keyboardInputListner;
	}

	public CMain() throws IOException {
		System.out.println("CLIENT");
		// dataStor handles all shared components
		// As well as booting threads, so reference is easier.
		//this.serverIp = "192.168.1.1";
		this.serverIp = "localhost";
		this.serverAddr = InetAddress.getByName(serverIp);
		this.serverPort = 4445;
		this.listenPort = 4448;
		this.clientRootFolder = "clientroot";
		dataStor = new DataStor(listenPort, clientRootFolder, new InboundClientUtil(this));
		System.out.println("CONNECTING  TO IP  : "+serverIp);
		System.out.println("CONNECTING  TO PORT: "+serverPort);
		System.out.println("LISTEN      AT PORT: "+listenPort);
		System.out.println("WorkingDir         : "+clientRootFolder);
		
	    
		this.keyboardSender = new KeyboardSender(this);
	    senderThread = new Thread(keyboardSender);
	    senderThread.start();
	    
	    //System.out.println("booted keyBoardsender");
	    // Interrupted also after watchdog finishes
	    this.keyboardInputListner = new KeyBoardInputListner(this);
	    
		// Start Watchdog that will process the inboundQueue
		// Watchdog can send messages
	    dataStor.getWatchdogThread().start();
	    //System.out.println("booted watchdog");
	    
		// Receiver gets the watchdog thread reference to toggle on new message
	    dataStor.getUDPreceiver().start();
	    //System.out.println("Client recv booted");
	    
	    dataStor.getUploadSlotThread().start();
	    //System.out.println("Client upload thread booted");
	    
	    dataStor.getScrapeAgentThread().start();
	    //System.out.println("Scrape Agent booted");
	    
	    // Wifi scanner from uTwente
	    String targetIP = "127.0.0.1";
	    int port = 2548;
	    setData(new LinkedBlockingQueue<MacRssiPair[]>()); // Queue between communicator thread and this thread
	    Thread communicator = new Thread(new DataReceiver(targetIP, port, getWifiData()));
		communicator.start();		
	    
		
		// Keyboard scanner
	    this.keyboardInputListnerThread = new Thread(keyboardInputListner);
	    keyboardInputListnerThread.start();
	    //System.out.println("booted keyboard listner");
	    
	    
	    
	    
	}

	public Thread getKeyboardSenderThread() {
		return senderThread;
	}

	public String getMostRecentKeyboardInput() {
		return mostRecentKeyboardInput;
	}

	public void setMostRecentKeyboardInput(String mostRecentKeyboardInput) {
		this.mostRecentKeyboardInput = mostRecentKeyboardInput;
	}

	public Thread getKeyboardInputListnerThread() {
		return keyboardInputListnerThread;
	}

	public void setKeyboardInputListnerThread(Thread keyboardInputListnerThread) {
		this.keyboardInputListnerThread = keyboardInputListnerThread;
	}

	public DataStor getDataStor() {
		return dataStor;
	}

	public void setDataStor(DataStor dataStor) {
		this.dataStor = dataStor;
	}

	public Queue<String> getKeyboardInputQueue() {
		// TODO Auto-generated method stub
		return keyboardInputQueue;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public InetAddress getServerAddr() {
		return serverAddr;
	}

	public void setServerAddr(InetAddress serverAddr) {
		this.serverAddr = serverAddr;
	}

	public LinkedBlockingQueue<client.MacRssiPair[]> getWifiData() {
		return data;
	}

	public void setData(LinkedBlockingQueue<client.MacRssiPair[]> data) {
		this.data = data;
	}

}
