package client;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import shared.OutSktUDP;

public class Sender implements Runnable {
    private OutSktUDP data;
  
    // standard constructors
    public Sender (OutSktUDP outSktUDP) {
    	this.data = outSktUDP;
    }
  
    public void run() {
        String packets[] = {
          "First packet",
          "Second packet",
          "Third packet",
          "Fourth packet",
          "End"
        };
  
        for (String packet : packets) {
            try {
				data.sendStr(packet);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
 
            // Thread.sleep() to mimic heavy server-side processing
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
            } catch (InterruptedException e)  {
                Thread.currentThread().interrupt(); 
                System.out.println("Thread interrupted"+ e);
            }
        }
    }
}