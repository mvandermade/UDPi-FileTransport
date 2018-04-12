package srv;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Watchdog implements Runnable {
	
	int pollQueueTime = 9000;
	private SMain srv;
	
	public Watchdog(SMain sMain) {
		this.srv = sMain;
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
				
				DatagramPacket datagramPacket = srv.getInboundQueue().poll();
				
				
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

	private void handleTextDatagram(DatagramPacket datagramPacket) throws UnknownHostException, IOException, StringIndexOutOfBoundsException {
		// TODO Auto-generated method stub
		System.out.println(">>CMD>>");
		
		//+1 remove the sorting byte
		String stringIn = new String(datagramPacket.getData(), datagramPacket.getOffset()+1, datagramPacket.getLength()-1);
		stringIn = stringIn.replace("\u0000", "");
		
		System.out.println(stringIn);
		System.out.println(">>RESPONSE>>");
		
		String responseOut = "";
		
		if (stringIn.equals("ls")) {
			// Repeat the command so the sender knows its an answer to it
			responseOut = stringIn+";";
			responseOut += "--FILE LIST--";
			responseOut += srv.getFileMan().listFiles();
		} else if (stringIn.length() >= 8 && stringIn.substring(0, 8).equals("download")) {
			responseOut = stringIn+";";
			if (stringIn.length() > 9) {
				String filename = stringIn.substring(9, stringIn.length());
				// Allocate download slot
				responseOut += srv.getTransferDB().newOutboundTransfer(filename,datagramPacket.getPort(),datagramPacket.getAddress());
				// Response contains session, totalblocks, blocksize, hash
			} else {
				responseOut+= "Too little arguments for download: download <filename>";
			}
			
		} else if (stringIn.length() >= 4 && stringIn.substring(0, 4).equals("hash")) {
			responseOut = stringIn+";";
			if (stringIn.length() > 6) {
				// Ommitting the space
				String filename = stringIn.substring(5, stringIn.length());
				
				File f = new File(srv.getBasePath()+"/"+filename);
				if(f.exists() && !f.isDirectory()) { 
					String hash= "";
					hash = srv.getFileMan().getHash(f);
					System.out.print(hash);
					responseOut+=hash;
					
				} else {
					responseOut += "File: "+filename+"\ndoesn't exist";
				}
			} else {
				responseOut+= "Too little arguments for hash: hash <filename>";
			}
			
		} else {
			responseOut = "unknown, try: ls";
		}
		
		System.out.println(responseOut);
		try {
			srv.getInSktUDP().sendStrReplyTo(responseOut, datagramPacket);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("error Watchdog tried to send but packet size too big");
		}
		
	}

}
