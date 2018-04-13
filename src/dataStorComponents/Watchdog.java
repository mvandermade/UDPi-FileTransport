package dataStorComponents;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import shared.DataStor;

public class Watchdog implements Runnable {
	
	int pollQueueTime = 9000;
	private DataStor dataStor;
	
	public Watchdog(DataStor dataStor) {
		this.dataStor = dataStor;
	}

	public void run() {
	
		while (true) {
			try {
				Thread.sleep(pollQueueTime);
			} catch (InterruptedException e) {
				//e.printStackTrace()		
				// AWAKE!!
			}
			
			// Check for messages, i'm awake
			boolean done = false;
			while (!done) {
				
				//System.out.print("poll");
				
				DatagramPacket datagramPacket = dataStor.getInboundQueue().poll();

				//System.out.print(polledObject);
				
				if (datagramPacket != null) {
					
					try {
						handleInboundDatagram(datagramPacket);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (StringIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
					
				} else {
					
					done = true;
				}
			}
			
		} // end while true server loop
	
	}

	public void handleInboundDatagram(DatagramPacket datagramPacket) throws UnknownHostException, IOException, StringIndexOutOfBoundsException {
		byte firstByte = datagramPacket.getData()[0];
		
		// Check what is the content based on the first byte
		switch (firstByte) {
			// STX
			case(0x02): {
				dataStor.getInboundDatagramUtil().handleTextDatagram(datagramPacket);
				break;
			// FS 0x1C = inbound data (meaning: response after I sent BELL)
			} case (0x1C): {
				dataStor.getInboundDatagramUtil().handleFileDatagram(datagramPacket);
				break;
			// BELL 0x07 = request for data
			} case (0x07): {
				dataStor.getInboundDatagramUtil().handleReqestFileDatagram(datagramPacket);
			} default: {
				System.out.println("bark");
			}
		}
		
	} // end run

}
