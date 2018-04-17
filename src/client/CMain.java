package client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import shared.DataStor;

public class CMain {
	
	private DataStor dataStor;
	private Thread senderThread;
	private Thread keyboardInputListnerThread;
	private String mostRecentKeyboardInput;
	private InetAddress serverAddr = InetAddress.getByName("localhost");
	private int serverPort;
	
	private final Queue<String> keyboardInputQueue = new ConcurrentLinkedQueue<>();
	private KeyBoardInputListner keyboardInputListner;
	private KeyboardSender keyboardSender;

	public KeyboardSender getKeyboardSender() {
		return keyboardSender;
	}

	public KeyBoardInputListner getKeyboardInputListner() {
		return keyboardInputListner;
	}

	public CMain() throws IOException {
		// dataStor handles all shared components
		// As well as booting threads, so reference is easier.
		this.serverPort = 4445;
		dataStor = new DataStor(4448, "clientroot", new InboundClientUtil(this));
	    
		this.keyboardSender = new KeyboardSender(this);
	    senderThread = new Thread(keyboardSender);
	    senderThread.start();
	    
	    System.out.println("booted keyBoardsender");
	    // Interrupted also after watchdog finishes
	    this.keyboardInputListner = new KeyBoardInputListner(this);
	    this.keyboardInputListnerThread = new Thread(keyboardInputListner);
	    keyboardInputListnerThread.start();
	    
	    
	    System.out.println("booted keyboard listner");
	    
		// Start Watchdog that will process the inboundQueue
		// Watchdog can send messages
	    dataStor.getWatchdogThread().start();
	    System.out.println("booted watchdog");
	    
		// Receiver gets the watchdog thread reference to toggle on new message
	    dataStor.getUDPreceiver().start();
	    System.out.println("Client recv booted");
	    
	    dataStor.getScrapeAgentThread().start();
	    System.out.println("Scrape Agent booted");
	    
	    
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

}
