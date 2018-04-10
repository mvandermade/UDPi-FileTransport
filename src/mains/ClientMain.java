package mains;

import java.io.IOException;

public class ClientMain {

	public static void main(String[] args) {
		try {
			new client.CMain();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
