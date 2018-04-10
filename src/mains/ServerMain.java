package mains;

import java.io.IOException;

public class ServerMain {

	public static void main(String[] args) {
		try {
			new srv.SMain();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
