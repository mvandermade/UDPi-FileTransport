package srv;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class TransferDB {
	
	private ArrayList<TransferInfoOutbound> outboundTransferList;
	private SMain srv;
	
	public TransferDB(SMain sMain) {
		this.srv = sMain;
		outboundTransferList = new ArrayList<>();
	}
	
	private byte sessionCount = 0x00; 

	public String newOutboundTransfer(String filename, int port, InetAddress address) {
		// Actually write this to the console
		String response = "";
		
		File f = new File(srv.getBasePath()+"/"+filename);
		
		if(f.exists() && !f.isDirectory()) {
			try {
				TransferInfoOutbound temp = new TransferInfoOutbound(f, port, address, srv);
				response += temp.strReport();
				sessionCount++;
				outboundTransferList.add(temp);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} else {
			response = "404FILENOTFOUND";
		}
		
		return response;
		
	}
	
	public byte getSessionCount() {
		return sessionCount;
	}

}
