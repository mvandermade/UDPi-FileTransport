package client;

import java.io.IOException;

import shared.OutSktUDP;

public class CMain {

	public CMain() throws IOException {
		// TODO Auto-generated method stub
	    
		// Block size
	    OutSktUDP outSktUDP = new OutSktUDP(256);
	    Thread sender = new Thread(new Sender(outSktUDP));
	    System.out.println("booted sender");
	    sender.start();
	    
	}

}
