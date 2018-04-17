package srv;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import client.KeyboardSender;
import dataStorComponents.FileMan;
import dataStorComponents.InSktUDP;
import dataStorComponents.UDPReceiver;
import dataStorComponents.TransferDB;
import shared.DataStor;

public class SMain {
	
	private DataStor dataStor;
	private int listenPort;
	private String serverRootFolder;
	
	public SMain() {
		
		System.out.println("SERVER");
		// dataStor handles all shared components
		// As well as booting threads, so reference is easier.
		
		this.listenPort = 4445;
		this.serverRootFolder = "home/pi/fileroot";
		setDataStor(new DataStor(listenPort, serverRootFolder, new InboundServerUtil(this)));
		
		System.out.println("LISTEN  AT PORT: "+listenPort);
		System.out.println("SERVING FROM/TO: "+serverRootFolder);
		
		// Start Watchdog that will process the inboundQueue
		// Watchdog can send messages
		
		dataStor.getWatchdogThread().start();
		//System.out.println("booted watchdog");
		// Receiver gets the watchdog thread reference to toggle on new message
	    
	    dataStor.getUDPreceiver().start();
	    //System.out.println("Server recv booted");
	    
	    dataStor.getUploadSlotThread().start();
	    //System.out.println("Server upload thread booted");
	    
	    dataStor.getScrapeAgentThread().start();
	    //System.out.println("Server Scrape Agent booted");
	}

	public DataStor getDataStor() {
		return dataStor;
	}

	public void setDataStor(DataStor dataStor) {
		this.dataStor = dataStor;
	}

}
