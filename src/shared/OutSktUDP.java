package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class OutSktUDP {
	int portOut = 4445;
	DatagramSocket socket = new DatagramSocket();
	int bufSize;
	

	public OutSktUDP(int bufSize) throws IOException {
		this.bufSize = bufSize;
		// Already initialized
		
	}
	
	public DatagramPacket receive() throws IOException {
		byte[] buf = new byte[bufSize];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		// Blocking
		socket.receive(packet);
		
		return packet;

	}
	
	public void closeSkt() {
		socket.close();
	}

	public void sendByte(byte[] buf) throws UnknownHostException, IOException {
		InetAddress address = InetAddress.getByName("localhost");
		DatagramPacket packet = new DatagramPacket(buf, buf.length, 
		                                address, portOut);
		socket.send(packet);
	}

	public void sendStr(String packet) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		
		byte[] b = packet.getBytes();
		
		byte[] buf = new byte[256];
		
		buf[0] = 0x02;
		
		for (int i = 0; i < b.length; i++) {
			  buf[i+1] = b[i];
			}
		
		sendByte(buf);
		
	}
}
