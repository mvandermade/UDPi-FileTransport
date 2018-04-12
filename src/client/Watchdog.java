package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Watchdog implements Runnable {
	
	int pollQueueTime = 9000;
	private CMain cl;
	
	public Watchdog(CMain cMain) {
		this.cl = cMain;
	}
	
	public void run() {
	
		while (true) {
			try {
				Thread.sleep(pollQueueTime);
			} catch (InterruptedException e) {
				//e.printStackTrace()		
				// AWAKE!!
			}
			
			// Check for messages
			
			boolean done = false;
			while (!done) {
				
				//System.out.print("poll");
				
				DatagramPacket datagramPacket = cl.getInboundQueue().poll();
				
				
				//System.out.print(polledObject);
				
				if (datagramPacket != null) {
					
					try {
						handleInboundDatagram(datagramPacket);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
				} else {
					
					done = true;
				}
			}
			
			
		} // end while true server loop
	
	}

	public void handleInboundDatagram(DatagramPacket datagramPacket) throws UnknownHostException, IOException {
		byte firstByte = datagramPacket.getData()[0];
		
		// Check what is the content based on the first byte
		switch (firstByte) {
			// STX
			case(0x02): {
				handleTextDatagram(datagramPacket);
				break;
			// FS
			} case (0x1C): {
				handleFileDatagram(datagramPacket);
				break;
			} default: {
				System.out.println("bark");
			}
		}
		
	} // end run

	private void handleFileDatagram(DatagramPacket datagramPacket) {
		// TODO Auto-generated method stub
		
	}

	private void handleTextDatagram(DatagramPacket datagramPacket) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		System.out.println("!");
		
		//+1 remove the sorting byte
		String stringIn = new String(datagramPacket.getData(), datagramPacket.getOffset()+1, datagramPacket.getLength()-1);
		stringIn = stringIn.replace("\u0000", "");
		
		System.out.println(stringIn);
		
//		String responseOut = "";
//		switch (stringIn) {
//			
//			case("ls"): {
//				responseOut = "--FILE LIST--";
//				responseOut += cl.getFileMan().listFiles();
//				break;
//			} default: {
//				responseOut = "unknown, try: ls";
//			}
//		}
//		
//		System.out.println(responseOut);
//		try {
//			cl.getInSktUDP().sendStrReplyTo(responseOut, datagramPacket);
//		} catch (ArrayIndexOutOfBoundsException e) {
//			System.out.println("error Watchdog tried to send but packet size too big");
//		}
		
	}

}
