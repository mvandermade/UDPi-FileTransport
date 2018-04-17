package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.UnknownHostException;

import dataStorComponents.InboundDatagramUtil;
import dataStorComponents.TransferInfoInFile;

public class InboundClientUtil implements InboundDatagramUtil {

	private CMain cl;

	public InboundClientUtil(CMain cMain) {
		// TODO Auto-generated constructor stub
		this.cl = cMain;
	}

	@Override
	public void handleTextDatagram(DatagramPacket datagramPacket)
			throws UnknownHostException, IOException, StringIndexOutOfBoundsException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		
		//+1 remove the sorting byte
		String stringIn = new String(datagramPacket.getData(), datagramPacket.getOffset()+1, datagramPacket.getLength()-1);
		stringIn = stringIn.replace("\u0000", "");
		
		try {
			// Check if I understand this ?
			if (null!=cl.getMostRecentKeyboardInput() && cl.getMostRecentKeyboardInput().length()>2 && cl.getMostRecentKeyboardInput().equals(
					stringIn.substring(0, cl.getMostRecentKeyboardInput().length())
							)
					) {
				// Remove ; from server
				//stringIn = stringIn.substring(cl.getMostRecentKeyboardInput().length()+1,stringIn.length());
				
				
				// 10, including the OK
				if (stringIn.length() >= 20 && stringIn.substring(0, 8).equals("download")) {
					// Check if got OK
					String answ = stringIn.substring(cl.getMostRecentKeyboardInput().length()+1,stringIn.length());
					if (answ.substring(0,2).equals("OK")) {
						System.out.println("DOWNLOAD Initializing (client)");
						String[] parts = answ.split(",");
						// part 0 = OK
						if (parts.length == 6) {
							System.out.println("Prepare download slot");
							int integerSessId = Integer.parseInt(parts[5]);
							// omit download<space> to get the filename
							String filename = stringIn.substring(9, cl.getMostRecentKeyboardInput().length());
							
							cl.getDataStor().getTransferDB().prepareDownloadSlot(
									new TransferInfoInFile(parts, filename, datagramPacket, cl.getDataStor())
									);
							// The server might in the future be programmed to start broadcasting ...
							System.out.println("Sending BELL");
							cl.getDataStor().getInSktUDP().sendBellReplyTo((byte)integerSessId, datagramPacket);
							cl.getDataStor().getScrapeAgent().unwaitThread();
							
						}
					} else {
						System.out.println(answ);
					}
				
				
				}

			} else if (stringIn.length() >= 6 && stringIn.substring(0, 6).equals("finish")) {
				if (stringIn.length() > 7) {
					if (stringIn.split(";").length == 2) {
						// Ommitting the space
						String stringFinish = stringIn.split(";")[0];
						int sessionIdin = Integer.parseInt(stringFinish.substring(7, stringFinish.length()));
						//byte sessionIdin = (byte) stringIn.charAt(8);
						System.out.println(stringIn.split(";")[1]);
						// Remove from queue
						cl.getDataStor().getTransferDB().getUploadSlots().removeIf((c)->{
							if (sessionIdin==c.getSessionId() &&
									c.getReqAddress().equals(datagramPacket.getAddress()) &&
											c.getReqPort() == datagramPacket.getPort()) {
								System.out.println("uploadremoved from queue");
								return true;
								
							} else {
								return false;
							}
						});
					}
				}
			} else {
				// INFO message or something?
				if (stringIn.split(";").length == 2) {
					System.out.println("\t\t"+stringIn.split(";")[1]);
				} else {
					System.out.println("\t\t"+stringIn);
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			// This may occur when the server randomly 
			//e.printStackTrace();
			System.out.println("\t\t\t\t"+stringIn);
		}
		
		
	}

}
