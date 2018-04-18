package srv;

import shared.DataStor;

public class SMain {

	private DataStor dataStor;
	private final int listenPort;
	private final String serverRootFolder;

	public SMain() {

		System.out.println("SERVER");
		// dataStor handles all shared components
		// As well as booting threads, so reference is easier.

		listenPort = 4445;
		serverRootFolder = "home/pi/fileroot";
		setDataStor(new DataStor(listenPort, serverRootFolder, new InboundServerUtil(this)));

		System.out.println("LISTEN  AT PORT: " + listenPort);
		System.out.println("SERVING FROM/TO: " + serverRootFolder);

		// Start Watchdog that will process the inboundQueue
		// Watchdog can send messages

		dataStor.getWatchdogThread().start();
		// System.out.println("booted watchdog");
		// Receiver gets the watchdog thread reference to toggle on new message

		dataStor.getUDPreceiver().start();
		// System.out.println("Server recv booted");

		dataStor.getUploadSlotThread().start();
		// System.out.println("Server upload thread booted");

		dataStor.getScrapeAgentThread().start();
		// System.out.println("Server Scrape Agent booted");
	}

	public DataStor getDataStor() {
		return dataStor;
	}

	public void setDataStor(DataStor dataStor) {
		this.dataStor = dataStor;
	}

}
