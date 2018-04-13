package dataStorComponents;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class InSktUDP {
	
	// TODO Auto-generated constructor stub
	int portIn;
	DatagramSocket socket;
	int bufSize;
	
	public InSktUDP(int bufSize, int portIn) throws IOException {
		this.bufSize=bufSize;
		this.portIn = portIn;
		socket = new DatagramSocket(portIn);
		
		// Already initialized
	}
	
	public DatagramPacket receive() throws IOException {
		byte[] buf = new byte[bufSize];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		// Blocking
		socket.receive(packet);
		
		return packet;

	}
	
	public void sendByteReplyTo(byte[] buf, DatagramPacket replyTo) throws UnknownHostException, IOException {
		DatagramPacket packet = new DatagramPacket(buf, buf.length, replyTo.getAddress(), replyTo.getPort());
		socket.send(packet);
	}

	public void sendStrReplyTo(String response, DatagramPacket replyTo) throws UnknownHostException, IOException {
		
		byte[] b = response.getBytes();
		
		byte[] buf = new byte[bufSize];
		
		buf[0] = 0x02;
		
		for (int i = 0; i < b.length; i++) {
			  buf[i+1] = b[i];
			}
		
		sendByteReplyTo(buf, replyTo);
		
	}
	
	public void sendByte(byte[] buf, InetAddress address, int portDest) throws UnknownHostException, IOException {
		
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portDest);
		socket.send(packet);
	}

	public void sendStr(String strMessage, InetAddress address, int portDest) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		
		
		byte[] b = strMessage.getBytes();
		
		byte[] buf = new byte[bufSize];
		
		buf[0] = 0x02;
		
		for (int i = 0; i < b.length; i++) {
			  buf[i+1] = b[i];
			}
		
		sendByte(buf, address, portDest);
		
	}
	
	public void closeSkt() {
		socket.close();
	}
	


}
