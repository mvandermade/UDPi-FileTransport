package client;

import java.io.IOException;
import java.net.UnknownHostException;

public class KeyboardSender implements Runnable {
	private CMain cl;
	
	private boolean waitState = true;
  
    // standard constructors
    public KeyboardSender (CMain cMain) {
    	this.cl = cMain;
    }
  
    public void run() {
    	
    	while (true) {
			
			// Check for messages
			waitThread();
			waitForSignal();
			
			boolean done = false;
			while (!done) {

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
		System.out.println("(c)->{cmd sent.}");
		cl.getDataStor().getInSktUDP().sendStr(keyboardInput, cl.getServerAddr(), cl.getServerPort());
		
	}
	
	public void waitForSignal() {
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