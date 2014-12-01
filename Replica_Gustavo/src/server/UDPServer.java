package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer implements Runnable {

	private final String name;
	private final int port;
	private final LibraryPOAImpl poa;
	DatagramSocket serverSocket = null;
	private boolean isRunning = false;
	
	public UDPServer(final String name, final int port, final LibraryPOAImpl poa) {
		this.name = name;
		this.port = port;
		this.poa = poa;
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new DatagramSocket(port);
			isRunning = true;
			System.out.println("Started UDP server for library [" + name + "] on port [" + port + "]");
			byte[] receiveData = new byte[1024];
			while(isRunning) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				DatagramPacket sendPacket = processReceivedPacket(receivePacket);
				if (sendPacket != null) {
					serverSocket.send(sendPacket);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Existing udp server: " + name);
			if (serverSocket != null) {
				serverSocket.close();
			}
		}
	}

	private DatagramPacket processReceivedPacket(final DatagramPacket receivePacket)
			throws IOException {
		DatagramPacket sendPacket = null;
		
		InetAddress clientIP = receivePacket.getAddress();
		int clientPort = receivePacket.getPort();
		
		String clientMsg = new String(receivePacket.getData());
		System.out.println(this.name + " received [" + clientMsg + "] from [" + clientIP + ":" + clientPort);
		
		String message = poa.processUdpClientMsg(clientMsg);
		if (message != null && message.length() > 0) {
			byte[] sendData = message.getBytes();
			sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, clientPort);
		}
		return sendPacket;
	}
	
	/**
	 * Close socket to release port used by server.
	 */
	public void unbindUdp() {
		serverSocket.close();
		isRunning = false;
	}

}
