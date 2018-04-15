package dataStorComponents;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.UnknownHostException;

public interface InboundDatagramUtil {
	void handleTextDatagram(DatagramPacket datagramPacket) throws UnknownHostException, IOException, StringIndexOutOfBoundsException;
}