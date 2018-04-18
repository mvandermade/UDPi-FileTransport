package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import dataStorComponents.TransferInfoInFile;
import dataStorComponents.TransferInfoOutFile;

public class KeyBoardInputListner implements Runnable {
	private CMain cl;
	
	public KeyBoardInputListner(CMain cMain) {
    	// Inherit all functionallity from main
    	this.cl = cMain;
	}

	public void run() {
    	// Listen forever
		handleTUI(new BufferedReader(new InputStreamReader(System.in)));
    }
	
    /**
     * Handler for the attached system.in for console inputs.
     */
	private void handleTUI(BufferedReader bufferedReaderTextInput) {
		while (true) {
			try {
				System.out.print("\ntype command or \"help\">");
				// Each line is seen as a command, and put into the queue
				String curBuf = bufferedReaderTextInput.readLine();
				
				if (curBuf.equals("lls")) {
					System.out.print("LOCAL\n"+cl.getDataStor().getFileMan().listFiles());
					
				} else if (curBuf.equals("help")) {
					System.out.print("how can I help? try:\nlls, list local files\nls , list server files\nld, list downloads\nlu, list uploads\ndownload<filename>, download <filename> from server\nupload<filename>, upload <filename> to server\nhash <filename>, get hash from server for <filename>\nlhash <filename>, grab hash from local file (use upload <filename>. Then type: 'lu' to get this for now)\nwifi, grab wifi MAC and dBm from Wlanscanner.exe\nscrapeall, manual scrape downloading files (forces retransmission of missing chuncks)");
				} else if (curBuf.equals("ld")) {
					System.out.println("List of download");
					System.out.print(listdownloads());
				} else if (curBuf.equals("scrapeall")) {
					System.out.print("Scraping...");
					System.out.print(scrapedownloads());
				} else if (curBuf.equals("lu")) {
					System.out.println("List of uploads^");
					System.out.print(listuploads());
				} else if (curBuf.equals("wifi")) {
					try {
						 MacRssiPair[] wifiData = cl.getWifiData().take();
						for(int i=0; i<wifiData.length; i++) {
							//System.out.println(wifiData[i].getMacAsString()+"->:"+wifiData[i].getRssi()+"dBm");
							// !! 1E:70:C7:05:C7:D4 is sometimes volatile
							// https://raspberrypi.stackexchange.com/questions/68513/pi-using-a-random-mac-address-after-every-reboot-how-do-i-stop-this-behavior/79941
							if (wifiData[i].getMacAsString().equals("1E:70:C7:05:C7:D4")) {
								System.out.println("Wi-Fi strength to UDPI_MM: "+wifiData[i].getRssi()+"dBm");
							} else {
								System.out.println("Wi-Fi strength to: "+ wifiData[i].getMacAsString()+" " +wifiData[i].getRssi()+"dBm");
							}
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else if (curBuf.length() >= 6 && curBuf.substring(0, 6).equals("upload")) {
					if (curBuf.length() > 7) {
						String filename = curBuf.substring(7, curBuf.length());
						// Allocate download slot
						
						String responseOut = filename+";upload "+cl.getDataStor().getTransferDB().newOutboundTransfer(filename, cl.getServerPort(), cl.getServerAddr());
						if (!responseOut.equals("404FILENOTFOUND")) {
							System.out.println("sending:"+responseOut);
							cl.getDataStor().getInSktUDP().sendStr(responseOut, cl.getServerAddr(), cl.getServerPort());
						} else {
							System.out.println("Sorry, file "+filename+" not found");
						}
							// Response contains session, totalblocks, blocksize, hash
						// The client should repeat to this using BELL
					} else {
						System.out.print("Too little arguments for upload: upload <filename>\nShow local (uploadable) filenames with command \"lls\"");
					}
					
				} else {
					cl.getKeyboardInputQueue().add(curBuf);
					// First ✓
					
					cl.getKeyboardSender().unwaitThread();
				}
				
			} catch (IOException e) {
				// ignore
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//
			}
		}
	}
	
	private String scrapedownloads() {
		// TODO Auto-generated method stub
		// Generates a user view of active downloads
		
		Iterator<TransferInfoInFile> iter = cl.getDataStor().getTransferDB().getDownloadSlots().iterator();
		// Check if the session id and datagram port and IP are enlisted.
		// The iterator makes sure FUP is achieved
		int i = 0;
		while (iter.hasNext()) {
			i++;
			TransferInfoInFile current = iter.next();		
			
			current.manualScrape();
			
		}
		String response = "\nTOTAL: "+i;
		return response;
	}

	private String listdownloads() {
		// TODO Auto-generated method stub
		// Generates a user view of active downloads
		String response ="ACTIVE DOWNLOADS:\n\n";
		
		Iterator<TransferInfoInFile> iter = cl.getDataStor().getTransferDB().getDownloadSlots().iterator();
		// Check if the session id and datagram port and IP are enlisted.
		// The iterator makes sure FUP is achieved
		int i = 0;
		while (iter.hasNext()) {
			i++;
			TransferInfoInFile current = iter.next();		
			
			response +=current.reportDownloadInfo()+"\n";
			
		}
		response += "\nTOTAL: "+i;
		return response;
	}
	
	private String listuploads() {
		// TODO Auto-generated method stub
		// Generates a user view of active downloads
		String response ="ACTIVE Uploads:\n\n";
		
		Iterator<TransferInfoOutFile> iter = cl.getDataStor().getTransferDB().getUploadSlots().iterator();
		// Check if the session id and datagram port and IP are enlisted.
		// The iterator makes sure FUP is achieved
		int i = 0;
		while (iter.hasNext()) {
			i++;
			TransferInfoOutFile current = iter.next();		
			
			response +=current.reportUploadInfo()+"\n";
			
		}
		response += "\nTOTAL: "+i;
		return response;
	}
	
}