package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

	public static final int BYTES = 1024;
	public static final int TIMEOUT = 1000;
	
	public static String sendUdpRequest(final String host, final int serverPort, final String clientMsg) {
		String message = null;
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(TIMEOUT);
			InetAddress iPAddress = InetAddress.getByName(host);
			byte[] receiveData = new byte[BYTES];
			byte[] sendData = new byte[BYTES];
			sendData = clientMsg.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, iPAddress, serverPort);
			clientSocket.send(sendPacket);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			message = new String(receivePacket.getData());
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			message = "\nERROR on UDP call on " + host + ":" + serverPort + "\n......";
		}
		return message;
	}
}
