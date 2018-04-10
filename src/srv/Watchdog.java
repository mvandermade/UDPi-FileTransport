package srv;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

public class Watchdog implements Runnable {
	
	int pollQueueTime = 9000;
	private SMain srv;
	
	public Watchdog(SMain sMain) {
		this.srv = sMain;
	}
	
	public void run() {
	
		while (true) {
			try {
				Thread.sleep(pollQueueTime);
			} catch (InterruptedException e) {
				//e.printStackTrace()				
				DatagramPacket receivedMessage = srv.getInboundQueue().poll();
				byte firstByte = receivedMessage.getData()[0];
				
				if (firstByte == 0x02) {
					handleTextDatagram(receivedMessage);
				} else if (firstByte == 0x1C){
					handleFileDatagram(receivedMessage);
				}
				
			}
			
			
		} // end while true server loop
	
	} // end run

	private void handleFileDatagram(DatagramPacket receivedMessage) {
		// TODO Auto-generated method stub
		
	}

	private void handleTextDatagram(DatagramPacket receivedMessage) {
		// TODO Auto-generated method stub
		System.out.println(">>CMD>>");
		
		String stringIn = new String(receivedMessage.getData(), StandardCharsets.UTF_8);
		stringIn = stringIn.substring(1, stringIn.length());
		System.out.println(stringIn);
		System.out.println(">>RESPONSE>>");
		
	}

}
