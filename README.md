# UDPi-FileTransport
The purpose of this git is to demonstrate the use of UDP for data transport.
Here a transmission method is displayed suitable for transferring files (verified by sha-1 hash).

The system is designed to work on Wifi networks. Therefore a wifiscanner.exe is included (via University of Twente) to monitor RSSI.

How to run ?


Download, configure CMain.java and SMain.java (IP adress, ports, packetsize).
javac on ClientMain.java and ServerMain.java (or use eclipse)
Run the .jar created by javac or eclipse.

It works on a raspberry pi 3B.
You can setup a daemon for example (see below)


When up and running
Then use 
#commandhelp
lls		 			list local files
ls	 				list server files
ld	 				list downloads
lu	 				list uploads
download <filename>	download <filename> from server
upload <filename> 	upload <filename> to server
hash <filename> 	get hash from server for <filename>
lhash <filename> 	grab hash from local file (use upload <filename>. Then type: 'lu' to get this for now)
wifi		 		grab wifi MAC and dBm from Wlanscanner.exe
scrapeall	 		manual scrape downloading files (forces retransmission of missing chuncks)

# Have fun!

# Linux service
In order to start and stop our service when the Pi starts, we need a service wrapper. Create a
new wrapper: sudo vi /lib/systemd/system/udpi.service and paste this
contents into it:
[Unit]
Description=UDPi
After=multi-user.agent
[Service]
Type=simple
ExecStart=/usr/bin/java -jar /home/pi/udpi.jar
Restart=on-abort
TimeoutStopSec=30
[Install]
WantedBy=multi-user.target

After doing a reload with sudo systemctl daemon-reload, the service can be started
by invoking sudo systemctl start udpi.service and stopped with sudo
systemctl stop udpi.service.
use systemctl status udpi.service to view console output.