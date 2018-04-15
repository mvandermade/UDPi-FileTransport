package dataStorComponents;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import shared.ByteCalculator;

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
		sendDatagramPacket(packet);
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
	
	public void sendChunckRequestTo(byte sessId, int chunckIdInt, InetAddress address, int portDest) {
		byte[] buf = new byte[bufSize];
		
		// FF char
		buf[0] = 0x0C;
		buf[1] = sessId;
		// Convert int to little endian
		byte[] chunckId = ByteCalculator.intToLeByteArray(chunckIdInt);
		// Fill the header with the bytes.
		buf[2] = chunckId[0]; buf[3] = chunckId[1]; buf[4] = chunckId[2]; buf[5] = chunckId[3];
		
		sendByte(buf, address, portDest);
		
	}
	
	public void sendBellReplyTo(byte sessId, DatagramPacket replyTo) throws UnknownHostException, IOException {
				
		byte[] buf = new byte[bufSize];
		
		// BELL char
		buf[0] = 0x07;
		buf[1] = sessId;
		
		sendByteReplyTo(buf, replyTo);
		
	}
	
	public void sendByte(byte[] buf, InetAddress address, int portDest) {
		
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portDest);
		sendDatagramPacket(packet);
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
	
	public void sendDatagramPacket(DatagramPacket datagramPacket) {
		try {
			socket.send(datagramPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void closeSkt() {
		socket.close();
	}

	public void sendChunckFromDisk(byte sessionId, byte[] chunckId, byte[] grabChunckFromDisk,
			InetAddress reqAddress, int reqPort) {
		// TODO Auto-generated method stub
		byte[] buf = new byte[bufSize];
		
		// FS char
		buf[0] = 0x1C;
		buf[1] = sessionId;
		// Convert int to little endian
		// Fill the header with the bytes.
		buf[2] = chunckId[0]; buf[3] = chunckId[1]; buf[4] = chunckId[2]; buf[5] = chunckId[3];
		System.out.println(Arrays.toString(grabChunckFromDisk));
		// Next fill out the buffer with the chunck
		for (int i = 0; i < grabChunckFromDisk.length; i++) {
			  buf[i+6] = grabChunckFromDisk[i];
			}
		
		// And send it
		sendByte(buf, reqAddress, reqPort);
		
		
	}
	


}
