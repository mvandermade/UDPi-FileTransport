package client;

import java.io.IOException;
import java.net.UnknownHostException;

public class KeyboardSenderThread implements Runnable {
	private CMain cl;
	// Not relevant unit, it is interrupted
	private int pollQueueTime = 9000;
  
    // standard constructors
    public KeyboardSenderThread (CMain cMain) {
    	this.cl = cMain;
    }
  
    public void run() {
    	
    	while (true) {
			
			// Check for messages
			
			boolean done = false;
			while (!done) {
				try {
					Thread.sleep(pollQueueTime);
				} catch (InterruptedException e) {
					//e.printStackTrace()		
					// AWAKE!!
				}
				//System.out.print("poll");
				
				String keyboardInput = cl.getKeyboardInputQueue().poll();
				//System.out.print(polledObject);
				if (keyboardInput != null) {
					try {
						handleKeyboardInput(keyboardInput);
						cl.setMostRecentKeyboardInput(keyboardInput);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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

	private void handleKeyboardInput(String keyboardInput) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		System.out.print(".");
		cl.getDataStor().getInSktUDP().sendStr(keyboardInput, cl.getServerAddr(), cl.getServerPort());
		
	}
}