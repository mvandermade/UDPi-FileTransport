package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class KeyBoardInputListnerThread implements Runnable {
	private CMain cl;
  
    public KeyBoardInputListnerThread(CMain cMain) {
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
				Thread.sleep(102400);
			} catch (InterruptedException e) {
				//e.printStackTrace()		
				// AWAKE!!
			}
			try {
				System.out.print("\ntype command or \"help\">");
				// Each line is seen as a command, and put into the queue
				cl.getKeyboardInputQueue().add(bufferedReaderTextInput.readLine());
				// First ✓
				System.out.print(".");
				
				cl.getKeyboardSender().interrupt();
				
			} catch (IOException e) {
				// ignore
				//e.printStackTrace();
			}
		}
	}
}