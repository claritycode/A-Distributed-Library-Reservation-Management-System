package parth;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import DRMSServices.lateStudent;
import DRMSServices.nonReturners;


/**
 * A <code>LibraryUDPServer</code> assists the <code>Library</code> in serving requests arriving form peer libraries
 * For every request coming from peer <code>Library</code> the library creates a new LibraryUDPServer which then
 * performs the work on behalf of the library
 * Two main services provided by the library through <code>LibraryUDPServer</code> are 
 * 1. getNonReturners
 * 2. reserveInterLibrary
 * */
public class LibraryUDPServer implements Runnable {
	
	private Library lib ;
	private DatagramPacket control ;
	private DatagramSocket sendData ;
	
	/**
	 * Constructor
	 * @param newLib - the <code>Library</code> on behalf of whom it is going to serve
	 * @param newControl - DatagramPacket (UDP message ) consisting of request and address of requestor
	 * */
	public LibraryUDPServer ( Library newLib, DatagramPacket newControl ) {
		lib = newLib ;
		control = newControl ;
	}
	
	/**
	 * Services peer servers by interacting with lib and sending UDP message as the reply
	 * It is an overriden method from Runnable interface
	 * */
	public void run () {
		try {
			sendData = new DatagramSocket();
			String controlString= new String(control.getData(), 0, control.getLength()) ;
			String[] msgParts = controlString.split(";;") ;
			
			// Get operation code and determine operation to be performed
			int operationCode = Integer.parseInt(msgParts[0]) ;
			
			// reserveInterLibrary book
			if ( operationCode == 0 ) {
				reserveBook ( msgParts[1], msgParts[2] ) ;
			}
			
			// get non returners
			else if ( operationCode == 1 ) {
				int days = Integer.parseInt(msgParts[1]) ;
				getNonReturners ( days ) ;
			}
			
		} catch ( SocketException e ) {
			String message = "A socket exception happened " +  e.getMessage() ;
			lib.writeLog(message);
		} catch ( IOException e ) {
			String message = "An IOException happened " + e.getMessage() ;
			lib.writeLog(message);
		}finally {
		
			if ( sendData != null ) {
				sendData.close();
			}
		}
	}
	
	// Helper method
	// It checks for the book in the library lib 
	// If the book is available it reserves it and sends the reply to client as UDP message
	private void reserveBook ( String bookName, String authorName ) throws IOException {
		
		boolean result = false ;		// Initially false
		Book demandedBook = new Book ( bookName, authorName ) ;
		// returns null if the book is not available
		Book availableBook = lib.getBookDatabase().get(demandedBook) ;
		
		// order the book if it is available
		if ( availableBook != null ) {
			// the book is shared among all students of library. Hence, Synchronization is mandatory
			synchronized ( availableBook ) {
				if ( availableBook.order() ) {
					// return true if order successful
					result = true ;
				} 
			}
		} 

		String resultString = Integer.toString( result ? 1 : 0 ) ; // result string
		byte[] replyData = resultString.getBytes() ; // result byte either 1 or 0
		
		// get client address and port from the UDP message client sended initially
		DatagramPacket sendPack = new DatagramPacket ( replyData, replyData.length, control.getAddress(),
			control.getPort() ) ;
		sendData.send(sendPack);
		
		String message ;
		// Generate appropriate message and write it to log
		if ( result ) {
			message = "A book named " + bookName + " by " + authorName + 
					" was reserved at " + Calendar.getInstance().getTime() + " (Copies Left: " + availableBook.getCopies() + ")" ;	
		} else {
			message = "An unsucessful attempt to reserve " + bookName + " by " +
					authorName + " was made at " + Calendar.getInstance().getTime() ;
		}
		
		lib.writeLog ( message ) ;
		return ;
		
	}
	
	
	// Helper method
	// Calculates non returners in the library lib
	// Sends the result as UDP message to the requestor
	private void getNonReturners ( int days ) throws IOException {
		// ArrayList to hold values
		ArrayList<lateStudent> nonReturners = new ArrayList<lateStudent>() ;
		// Loop through student account database
		for ( Map.Entry<String, ArrayList<Student>> studentList : lib.getAccountDatabase().entrySet() ) {
			for ( Student student : studentList.getValue() ) {
				synchronized (student) {
					if ( student.isNonReturner(days)) {
						// If the student is non returner add the entry to the nonReturner array
						nonReturners.add(new lateStudent ( student.getFirstName(), student.getLastName(),
								student.getPhoneNumber() ) ) ;
					}
				}
			}
		}
		

		// Convert the ArrayList into an array
		// It is necessary as the nonReturner class has an array of lateStudent
		// Note: This has to be done because IDL does not have a mapping for Java ArrayList.
		lateStudent[] studentList = new lateStudent[nonReturners.size()] ;
		studentList = nonReturners.toArray(studentList) ;
		
		nonReturners result = new nonReturners( lib.getName(), studentList ) ;	// Final Result
		
		// The UDP Server sends a Serialized object of type nonReturner
		
		ByteArrayOutputStream bs = new ByteArrayOutputStream () ;
		ObjectOutputStream os = new ObjectOutputStream ( bs ) ;
		
		os.writeObject(result);		// Serialize Object
		
		byte[] data = bs.toByteArray() ;
		DatagramPacket dataPacket = new DatagramPacket ( data, data.length, 
				control.getAddress(), control.getPort() ) ;
		sendData.send(dataPacket);

		// Write the log of the server
		String message = "A successful call to get the non returners for " + lib.getName() + " was made at " + Calendar.getInstance().getTime() ;
		lib.writeLog(message) ;
		
		return ;
	}
}
