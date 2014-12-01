package rm.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import rm.ReplicaManager;

public class RMUDPServer implements Runnable {
	
	private final int port;
	private final ReplicaManager rm;
	DatagramSocket serverSocket = null;

	public RMUDPServer(final int port, final ReplicaManager rm) {
		this.port = port;
		this.rm = rm;
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new DatagramSocket(port);
			System.out.println("Started UDP server for RM [" + rm.getRmId() + "] on port [" + port + "]");
			byte[] receiveData = new byte[1024];
			while(true) {
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
			System.out.println("Existing udp server: " + rm.getRmId());
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
		System.out.println(rm.getRmId() + " received [" + clientMsg + "] from [" + clientIP + ":" + clientPort);
		
		String message = rm.processUdpClientMsg(clientMsg);
		if (message != null && message.length() > 0) {
			byte[] sendData = message.getBytes();
			sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, clientPort);
		}
		return sendPacket;
	}
}
