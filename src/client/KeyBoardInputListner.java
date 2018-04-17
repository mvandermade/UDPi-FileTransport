package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class KeyBoardInputListner implements Runnable {
	private CMain cl;
	
	private boolean waitState = true;
  
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
					System.out.print("how can I help? try: lls, ld, lu, download, upload, hash");
				} else if (curBuf.equals("ld")) {
					System.out.print("List of download (+speeds etc. %)");
				} else if (curBuf.equals("lu")) {
					System.out.print("List of uploads^ (+speeds etc. %)");
					
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
					System.out.print(".");
					
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
	
}