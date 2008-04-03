package com.cannontech.sensus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

public class GpuffMessageMux {
	private Thread worker;
    private Logger log = Logger.getLogger(GpuffMessageMux.class);
	private Set< URL > udpTargetAddressSet;
	private int listenPort = 62626;
	private byte[] buf = new byte[2048];
	private DatagramSocket listenSocket;
	private DatagramSocket outSocket;
	private boolean ready = false;

	public void startup() {
		if (worker != null) {		// Make certain the worker is not already running.
			throw new RuntimeException("Worker has already been started");
		}
		log.info("GPUFFMessageMux worker thread startup.");
		System.out.print("GPUFFMessageMux worker thread startup.\n");
		Runnable runner = new Runnable() {
			public void run() {
				while(!Thread.currentThread().isInterrupted()) {
					
					if(!ready) {	// Set/reset things here						
						try {									
							System.out.print("Creating sockets\n");
							listenSocket = new DatagramSocket(getListenPort());
							outSocket = new DatagramSocket();
							ready = true;
						} catch (SocketException e) {
							log.info("caught SocketException creating listenSocket & outSocket", e);
						}
					}
					
					while(ready) {
						try {
							try {								
								// this should only block for 2.5 seconds
								DatagramPacket pkt = readPacket();
								if(pkt.getLength() > 0) {	
									String report = "UDP packet received: " + pkt.getAddress()+ ":" + 
									pkt.getPort() + " ["+ pkt.getLength() + "] bytes.";

									Formatter hexFormatter = new Formatter();
									for( int i=0; i < pkt.getLength(); i++) {
										hexFormatter.format(" %02X", (0x000000ff & (int)(pkt.getData()[i])));
									}
									report += hexFormatter.out().toString();
																		
									// System.out.print(report + "\n");
									log.info(report);
									
									// loop over outgoing sockets and send
									writePacket(pkt.getData(), pkt.getLength());			        	
								}
							} catch (SocketException se) {
								System.out.print(se);
							}
						} catch (IOException ioe) {
							log.warn("caught io exp in while loop", ioe);
							System.out.print(ioe);
							cleanupSockets();
						}
						if (Thread.currentThread().isInterrupted()) {
							log.info("Shutting down main loop");
							break;
						}
					}
				}
				log.info("Main loop has completed");
			}
		};
		worker = new Thread(runner);
		worker.start();
	}

	public void destroy() {
		worker.interrupt(); // might need to check something or catch something to make this work in the loop
	}

	public DatagramPacket readPacket() throws IOException {
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		listenSocket.setSoTimeout(2500);	// 2.5 second timeout on the read?
		
		try {			
			listenSocket.receive(packet);
		} catch (SocketTimeoutException ste) {
			// This is not an issue.... we expect this.	
			packet.setLength(0);  // Did not get a message!
		}

		return packet;		
	}
	
	public void writePacket(byte[] writebuf, int writeLen) {

		Formatter hexFormatter = new Formatter();
		for( int i=0; i < writeLen; i++) {
			hexFormatter.format(" %02X", (0x000000ff & (int)writebuf[i]));
		}

		String clientList = "UDP send [" + writeLen + "] bytes to: ";		
        for (URL itr : udpTargetAddressSet) {
			try {
				// send request
				InetAddress address = InetAddress.getByName(itr.getHost());
				DatagramPacket packet = new DatagramPacket(writebuf, writeLen, address, itr.getPort());

				clientList += packet.getAddress()+ ":" + packet.getPort() + ", ";
				String report = "UDP packet     send: " + packet.getAddress()+ ":" + 
				packet.getPort() + " ["+ packet.getLength() + "] bytes." + hexFormatter.out().toString();				
								
				// System.out.print(report + "\n");
				log.debug(report);				
				outSocket.send(packet);				
			} catch (IOException e) {
				System.out.print(e);
				log.warn("caught IOException in writePacket", e);
				cleanupSockets();
			}
		}
        log.info(clientList);
	}
	
	public void setUdpTargetAddressSet(Set<URL> udpTargetAddressSet) {
		this.udpTargetAddressSet = udpTargetAddressSet;
	}

	public int getListenPort() {
		return listenPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}
	
	public void cleanupSockets() {
		listenSocket.close();
		outSocket.close();
		ready = false;		
	}
	
	public static void main(String[] args) {
		try {
			GpuffMessageMux mux = new GpuffMessageMux();

			mux.setListenPort(9000);
			URL url;
			url = new URL("http://127.0.0.1:51515/");			
			URL url2 = new URL("http://127.0.0.1:15151/");
			Set<URL> singleton = new HashSet<URL>();			
			singleton.add(url);
			singleton.add(url2);
			
			mux.setUdpTargetAddressSet(singleton);
			mux.startup();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
