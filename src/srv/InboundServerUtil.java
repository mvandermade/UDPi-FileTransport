package srv;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.util.Arrays;

import dataStorComponents.InboundDatagramUtil;
import dataStorComponents.TransferInfoInFile;

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
				responseOut += srv.getDataStor().getTransferDB().newOutboundTransfer(filename,datagramPacket.getPort(),datagramPacket.getAddress() );
				// Response contains session, totalblocks, blocksize, hash
				// The client should repeat to this using BELL
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
			
		} else if (stringIn.length() >= 6 && stringIn.substring(0, 6).equals("finish")) {
			responseOut = stringIn+";i observed packet loss from my side : requests RxTx";
			if (stringIn.length() > 7) {
				// Ommitting the space
				int sessionIdin = Integer.parseInt(stringIn.substring(7, stringIn.length()));
				//byte sessionIdin = (byte) stringIn.charAt(8);
				
				// Remove from queue
				srv.getDataStor().getTransferDB().getUploadSlots().removeIf((c)->{
					if (sessionIdin==c.getSessionId() &&
							c.getReqAddress().equals(datagramPacket.getAddress()) &&
									c.getReqPort() == datagramPacket.getPort()) {
						System.out.println("uploadremoved from queue");
						return true;
						
					} else {
						return false;
					}
				});
			} else {
				responseOut+= "Too little arguments for finish: finish <(char)sessionId>";
			}
		
		} else	if (stringIn.length() >= 10) {
			
			if (stringIn.split(";").length == 2) {
				responseOut+="len is 2";
				if(stringIn.split(";")[1].substring(0, 6).equals("upload")) {
			
					// Check if got OK
					String answ = stringIn.split(";")[1].substring(7,stringIn.split(";")[1].length());
					responseOut+=answ;
					if (answ.substring(0,2).equals("OK")) {
						System.out.println("Upload Initializing (for client)");
						String[] parts = answ.split(",");
						// part 0 = OK
						if (parts.length == 6) {
							System.out.println("Prepare download slot for upload");
							int integerSessId = Integer.parseInt(parts[5]);
							// omit download<space> to get the filename
							String filename = stringIn.split(";")[0];
							
							srv.getDataStor().getTransferDB().prepareDownloadSlot(
									new TransferInfoInFile(parts, filename, datagramPacket, srv.getDataStor())
									);
							// The server might in the future be programmed to start broadcasting ...
							System.out.println("Sending BELL to client");
							srv.getDataStor().getInSktUDP().sendBellReplyTo((byte)integerSessId, datagramPacket);
							srv.getDataStor().getScrapeAgent().unwaitThread();
							
						}
					}
				}
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

}
