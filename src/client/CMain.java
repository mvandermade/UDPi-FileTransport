package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import shared.InSktUDP;

public class CMain {
	private int datagramSize = 256;
	private int listnerPort = 4446;
	private InSktUDP inSktUDP;
	private InetAddress serverAddr = InetAddress.getByName("localhost");
	private int serverPort = 4445;
	private final Thread sender;
	
	private final Queue<String> keyboardInputQueue = new ConcurrentLinkedQueue<String>();
	private final Queue<DatagramPacket> inboundQueue = new ConcurrentLinkedQueue<DatagramPacket>();
	
	private Thread watchdog;

	public CMain() throws IOException {
		// TODO Auto-generated method stub
	    
		setInSktUDP(new InSktUDP(datagramSize, listnerPort));
	    sender = new Thread(new Sender(this));
	    System.out.println("booted sender");
	    sender.start();
	    
	    Thread keyboardInputListner = new Thread(new KeyBoardInputListner(this));
	    System.out.println("booted keyboard listner");
	    keyboardInputListner.start();
	    
		// Start Watchdog that will process the inboundQueue
		// Watchdog can send messages
	    watchdog = new Thread(new Watchdog(this));
	    System.out.println("booted watchdog");
	    watchdog.start();
	    
		// Receiver gets the watchdog thread reference to toggle on new message
	    Thread receiver = new Thread(new Receiver(this));
	    System.out.println("Client recv booted");
	    receiver.start();
	    
	    System.out.println("Begin typing in the console and hit enter to send: ");
	    
	}

	public InSktUDP getInSktUDP() {
		return inSktUDP;
	}

	public void setInSktUDP(InSktUDP inSktUDP) {
		this.inSktUDP = inSktUDP;
	}

	public Queue<String> getKeyboardInputQueue() {
		return keyboardInputQueue;
	}

	public InetAddress getServerAddr() {
		return serverAddr;
	}

	public void setServerAddr(InetAddress serverAddr) {
		this.serverAddr = serverAddr;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public Thread getSender() {
		return sender;
	}

	public Thread getWatchdog() {
		return watchdog;
	}

	public void setWatchdog(Thread watchdog) {
		this.watchdog = watchdog;
	}

	public Queue<DatagramPacket> getInboundQueue() {
		return inboundQueue;
	}

}
