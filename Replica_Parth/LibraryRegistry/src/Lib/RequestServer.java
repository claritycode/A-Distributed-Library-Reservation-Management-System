package Lib;

import java.io.*;
import java.net.* ;
import java.util.ArrayList ;

/**
 * @author Parth Patel
 * It is a helper object that supports concurrency mechanism for the <code>Registry</code>.
 * The registry generates a new RequestServer for every request it receives
 * */
public class RequestServer implements Runnable {
	
	private DatagramPacket recvPack ;
	private Registry reg ;
	
	public RequestServer ( DatagramPacket newPack, Registry newReg ) {
		recvPack = newPack ;
		reg = newReg ;
	}
	
	/**
	 * Overridden method from Runnable interface that provides primary services to the users
	 * */
	public void run () {
		// Receive formatted message
		// Format OperationCode(0 or 1) ;; (delimiter) data
		String receivedData = new String ( recvPack.getData(), 0, recvPack.getLength() ) ;
		String[] msgParts = receivedData.split(";;") ;
		int operationCode = Integer.parseInt(msgParts[0]) ;
		
		// Uses the received Datagram to get the sender library address
		if ( operationCode == 0 ) {
			LibraryAddress newLib = new LibraryAddress ( msgParts[1], recvPack.getAddress(), recvPack.getPort() ) ;
			reg.addAddress( newLib );
		} 
		
		// Retrieve the Library Address of other servers
		else if ( operationCode == 1 ) {
			ArrayList<LibraryAddress> result = reg.getAddress() ;
			DatagramSocket socket = null ;
		
			try {
				socket = new DatagramSocket () ;
				
				// Result will contain all the library addresses except the sender
				String sizeString = Integer.toString(result.size() - 1) ;
				byte[] dataSize = sizeString.getBytes() ;
				DatagramPacket sizePacket = new DatagramPacket(dataSize, sizeString.length(), recvPack.getAddress(), 
						recvPack.getPort() );
				socket.send(sizePacket);
				
				try {
					Thread.sleep(400);
				}catch (InterruptedException e ) {
					
				}
				
				
				for ( LibraryAddress l : result ) {
					// If the LibraryAddress is not of the client library then send it to client
					if ( ! l.getName().equals(msgParts[1]) ){
						// Serialized object of type LibraryAddress
						ByteArrayOutputStream bs = new ByteArrayOutputStream () ;
						ObjectOutputStream os = new ObjectOutputStream ( bs ) ;
						os.writeObject(l);
						byte[] message = bs.toByteArray() ;
						DatagramPacket sendRecv = new DatagramPacket ( message, message.length, 
								recvPack.getAddress(), recvPack.getPort()) ;
						socket.send(sendRecv);
						try {
							Thread.sleep(400);
						}catch (InterruptedException e ) {
							
						}
						
					}
					else {
						continue ;
					}
				}
					
				
			} catch ( SocketException e ) {
				System.out.println ("Exception: " + e.getMessage() ) ;
			} catch ( IOException e ) {
				System.out.println ("Exception: " + e.getMessage() ) ;
			} finally {
				if ( socket != null ) {
					socket.close();
				}
			}
		}
	}
}
