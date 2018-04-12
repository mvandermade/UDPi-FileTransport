package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

import shared.InSktUDP;
import shared.OutSktUDP;

public class Sender implements Runnable {
    private InSktUDP inSktUDP;
	private CMain cl;
	private int pollQueueTime = 9000;
  
    // standard constructors
    public Sender (CMain cMain) {
    	this.cl = cMain;
    	this.inSktUDP = cl.getInSktUDP();
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
				
				String keyboardInput = cl.getKeyboardInputQueue().poll();
				//System.out.print(polledObject);
				if (keyboardInput != null) {
					try {
						handleKeyboardInput(keyboardInput);
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
		cl.getInSktUDP().sendStr(keyboardInput, cl.getServerAddr(), cl.getServerPort());
		
	}
}