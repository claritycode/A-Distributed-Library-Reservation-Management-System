import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.* ;

import DRMSServices.nonReturners;
/**
 * @author Parth Patel
 * A <code>LibraryUDPClient</code> is created by the <code>Library</code> to assist it in parallel processing 
 * multiple Non returner requests to multiple peer libraries
 * A <code>LibraryUDPClient</code>knows the port number on which it is supposed to contact other library
 * It implements runnable and have only the run method
 * */
public class LibraryUDPClient implements Runnable {

	private nonReturners result ;
	int portNo ;
	int days ;
	
	/**
	 * Constructor
	 * @param newPortNo - port number of the peer library
	 * @param newDays - the threshold value for the days for non returners
	 * */
	public LibraryUDPClient ( int newPortNo, int newDays ) {
		portNo = newPortNo ;
		days = newDays ;
	}
	
	public nonReturners getResult () {
		return result ;
	}
	
	/**
	 * Sends Non returner request on the portNo
	 * Receives reply from the peer and puts the reply in the arraylist that can be accessed by the calling library
	 * */
	public void run() {
		DatagramSocket otherServer = null ;
		try {
			otherServer = new DatagramSocket () ;
			// The UDP message is formatted as follows
			// Operation Code (0 or 1) ;; (delimiter) number of days
			String daysString = "1" + ";;" + Integer.toString(days) ;
			byte[] message = daysString.getBytes() ;
			InetAddress ipAddress = InetAddress.getLocalHost() ;
			DatagramPacket packet = new DatagramPacket(message, daysString.length(), ipAddress, portNo) ;
			otherServer.send(packet);
			
			try {
				Thread.sleep(400);
			}catch (InterruptedException e ) {
				
			}
			
			byte[] replyBuffer = new byte[1024] ;
			DatagramPacket replyPacket = new DatagramPacket ( replyBuffer, replyBuffer.length ) ;
			otherServer.receive(replyPacket);
			
			ByteArrayInputStream bs = new ByteArrayInputStream ( replyPacket.getData() ) ;
			ObjectInputStream os = new ObjectInputStream ( bs ) ;
			result = (nonReturners) os.readObject() ;
			// Initially the peer sends size packet indicating the number of peer non returners from that library.
//			// Then the library sends size number of packets one for each non returner.
//			byte[] size = new byte[512] ;
//			DatagramPacket sizePacket = new DatagramPacket(size, size.length) ;
//			otherServer.receive(sizePacket);
//			String dataSizeString = new String(sizePacket.getData(),0,sizePacket.getLength());
//			int dataSize = Integer.parseInt(dataSizeString);
//			result = new ArrayList<String>(dataSize) ;
//			
//			// loop until all the packets arrive
//
//			while ( dataSize != 0 ) {
//				byte[] reply = new byte[512] ;
//				DatagramPacket data = new DatagramPacket ( reply, reply.length ) ;
//				otherServer.receive(data);
//				
//				// Convert packets into string
//				String nonReturner = new String(data.getData(), 0, data.getLength());
//			
//				result.add(nonReturner);		// Add to arraylist
//				--dataSize ;
//			}			
		} catch ( SocketException e ) {
			System.out.println ( "A socket exception happened while requesting non returners from the server at port number " + portNo + " "+e.getMessage() ) ;
		} catch ( IOException e ) {
			System.out.println ( "A IOException exception happened while requesting non returners from the server at port number " + portNo + " " + e.getMessage() ) ;
		} catch ( ClassNotFoundException e ) {
			System.out.println ( "A class not found exception happened while requesting non returners from the server at port number " + portNo + " "+e.getMessage() ) ;
		} finally {
			if ( otherServer != null ) {
				otherServer.close() ;
			}
		}
	}
}
