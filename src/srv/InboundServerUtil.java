package srv;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.util.Arrays;

import dataStorComponents.InboundDatagramUtil;

public class InboundServerUtil implements InboundDatagramUtil {

	private SMain srv;

	public InboundServerUtil(SMain sMain) {
		// TODO Auto-generated constructor stub
		this.srv = sMain;
	}

	@Override
	public void handleTextDatagram(DatagramPacket datagramPacket)
			throws UnknownHostException, IOException, StringIndexOutOfBoundsException {
		// TODO Auto-generated method stub
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
			responseOut += srv.getDataStor().getFileMan().listFiles();
		} else if (stringIn.length() >= 8 && stringIn.substring(0, 8).equals("download")) {
			responseOut = stringIn+";";
			if (stringIn.length() > 9) {
				String filename = stringIn.substring(9, stringIn.length());
				// Allocate download slot
				responseOut += srv.getDataStor().getTransferDB().newOutboundTransfer(filename,datagramPacket.getPort(),datagramPacket.getAddress());
				// Response contains session, totalblocks, blocksize, hash
			} else {
				responseOut+= "Too little arguments for download: download <filename>\nShow filenames with command \"ls\"";
			}
			
		} else if (stringIn.length() >= 4 && stringIn.substring(0, 4).equals("hash")) {
			responseOut = stringIn+";";
			if (stringIn.length() > 6) {
				// Ommitting the space
				String filename = stringIn.substring(5, stringIn.length());
				
				File f = new File(srv.getDataStor().getBasePath()+"/"+filename);
				if(f.exists() && !f.isDirectory()) { 
					String hash= "";
					hash = srv.getDataStor().getFileMan().getHash(f);
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
			srv.getDataStor().getInSktUDP().sendStrReplyTo(responseOut, datagramPacket);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("error Watchdog tried to send but packet size too big");
		}
	}

	@Override
	public void handleFileDatagram(DatagramPacket datagramPacket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleReqestFileDatagram(DatagramPacket datagramPacket) {
		
		byte ses = datagramPacket.getData()[1];
		byte[] reqChunck = Arrays.copyOfRange(datagramPacket.getData(), 2, 5);
		int x = java.nio.ByteBuffer.wrap(reqChunck).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
		
		// Fix me
		srv.getDataStor().getTransferDB().enqueueInitialDownload();
		
	}

}