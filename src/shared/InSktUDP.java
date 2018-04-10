package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class InSktUDP {
	
	// TODO Auto-generated constructor stub
	int portIn = 4445;
	DatagramSocket socket = new DatagramSocket(portIn);
	int bufSize = 256;
	
	public InSktUDP(int bufSize) throws IOException {
		this.bufSize=bufSize;
		// Already initialized
		
	}
	
	public DatagramPacket receive() throws IOException {
		byte[] buf = new byte[bufSize];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		// Blocking
		socket.receive(packet);
		
		return packet;
		
//		byte[] b = packet.getData();
//		
//		for (int i = 0; i < b.length; i++) {
//			  buf[i] = b[i];
//			}
//
//		return new String(buf, StandardCharsets.UTF_8);
		
		// Reply
//		InetAddress address = packet.getAddress();
//		int port = packet.getPort();
//		packet = new DatagramPacket(buf, buf.length, address, port);
//		socket.send(packet);

	}
	
	public void closeSkt() {
		socket.close();
	}
	


}
