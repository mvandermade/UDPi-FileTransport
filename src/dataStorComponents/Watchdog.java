package dataStorComponents;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.UnknownHostException;

import shared.DataStor;

public class Watchdog implements Runnable {
	
	private DataStor dataStor;
	
	private boolean waitState = true;
	
	public Watchdog(DataStor dataStor) {
		this.dataStor = dataStor;
	}
	
	

	public void run() {
		while (true) {
			waitThread();
			waitForSignal();
			
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
				//System.out.println("TEXT IN");
				dataStor.getInboundDatagramUtil().handleTextDatagram(datagramPacket);
				//System.out.println(">>TEXT SENT");
				break;
			// FS 0x1C = inbound data from uploadslot. FS<sessionId<4byte chunck int><data>
			} case (0x1C): {
				handleBufInDownloadSlotDatagram(datagramPacket);
				//System.out.println("FS");
				break;
			// BELL 0x07 = response of client to: OK, initialises uploadslot
			} case (0x07): {
				System.out.println("BELL IN");
				handleRequestWholeFileDatagram(datagramPacket);
				System.out.println(">>BELL SENT");
				break;
				// Form feed, it is a request to an uploadslot to send an FS.
			} case (0x0C):{
				handleUploadslotRequest(datagramPacket);
				//System.out.println("request for 1 pkt");
				break;
			} default: {
				System.out.println("bark");
			}
		}
		
	} // end handleInboundDatagram
	
	private void handleUploadslotRequest(DatagramPacket datagramPacket) {
		dataStor.getTransferDB().handleUploadRequest(datagramPacket);
		
	}

	public void handleBufInDownloadSlotDatagram(DatagramPacket datagramPacket) {
		dataStor.getTransferDB().handleInboundDownloadChunck(datagramPacket);
		
	}

	public void handleRequestWholeFileDatagram(DatagramPacket datagramPacket) {
				
		// The whole file is enqueued (all parts are enqueued for first broadcast)
		dataStor.getTransferDB().enqueueWholeFile(datagramPacket);
		
	}
	
	public void waitForSignal() {
		//System.out.println("watchdog wait");
		synchronized(this) {
			while(waitState) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//System.out.println("watchdog awake");
		
	}
	
	public void unwaitThread() {
		synchronized(this) {
			if (this.waitState == true) {
				this.waitState = false;
				notifyAll();
			}
		}
		
	}
	
	public void waitThread() {
		synchronized(this) {
			this.waitState = true;
		}
	}
	
}
