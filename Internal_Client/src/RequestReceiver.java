
import java.net.* ;
import java.io.* ;
import java.util.HashMap ;
import Call.* ;
import Response.* ;
/**
 * 
 * @author Parth Patel
 * A <code>RequestReceiver</code> represents a receiver of the client request through a sequencer.
 * It then call the appropriate method usig <code>RequestDispatcher</code>.
 *
 */
public class RequestReceiver implements Runnable {
	
	/**
	 * Sequence Number to guarantee total ordering
	 */
	private int sequenceNumber = 0; 
	
	/**
	 * A socket through which requests are accepted and response sended back
	 */
	private DatagramSocket socket = null ;
	
	/**
	 * A <code>RequestDispatcher</code> has methods to dispatch the client request to the CORBA Object
	 */
	private RequestDispatcher dispatcher ;
	
	/**
	 * Queue - the Queue of client Request.The requests that arrive out of order are queued it the HashMap with sequence number
	 * as key
	 */
	private HashMap< Integer, ClientCall > queue = new HashMap< Integer, ClientCall > ();
	
	/**
	 * Constructor
	 * @param portNo - The port number through which requests shall arrive
	 * @param name - Name of the replica 
	 * @param args - Consists of the location of NameService to be used to locate CORBA Objects
	 */
	public RequestReceiver ( int portNo, String name, String[] args ) {
		try {		
			socket = new DatagramSocket ( portNo ) ;
		} catch ( SocketException e ) {
			System.out.println ( "Exception: " + e.getMessage() ) ;
			System.exit(1);
		}
		dispatcher = new RequestDispatcher ( args, name ) ;
	}
	
	/**
	 * The thread runs infinitely waiting for requests and serving them as they arrive
	 */
	public void run () {
		try {
			byte[] receiveBuffer = new byte[512] ;
			DatagramPacket receivePacket = new DatagramPacket ( receiveBuffer, receiveBuffer.length ) ;

			// Infinite Loop
			while ( true ) {
				
				socket.receive( receivePacket );
				
				// Deserialize Request
				ByteArrayInputStream bs = new ByteArrayInputStream ( receivePacket.getData() ) ;
				ObjectInputStream os = new ObjectInputStream ( bs ) ;
				ClientCall request = (ClientCall) os.readObject() ;
				
				// Check the sequence Number before dispatching the request furthur
				if ( request.getSequenceNumber() == sequenceNumber ) {
					Object result = callMethod ( request ) ;		// Call Method
					sendResponse ( result, request ) ;			// Send Response
					++sequenceNumber ;							
					flushQueue () ;									// If possible clear any waiting requests
				} 
				// If the request is out of order Queue it
				else if ( request.getSequenceNumber() > sequenceNumber ) {
					queue.put( request.getSequenceNumber(), request ) ;
				}
			}
		} catch ( IOException e) {
			System.out.println ( "Exception: " + e.getMessage () ) ;
		} catch ( ClassNotFoundException e ) {
			System.out.println ( "Exception: " + e.getMessage () ) ;
		}
	}
	
	/**
	 * Dispatch the client requests that are waiting in the queue. 
	 */
	private void flushQueue () {
		
		// Check if the request with the current value of sequence number is in the queue
		while ( queue.containsKey(sequenceNumber)) {
			Object result = callMethod ( queue.get(sequenceNumber)) ;
			sendResponse ( result, queue.get(sequenceNumber) ) ;
			++sequenceNumber ;
		}
	}
	
	
	/**
	 * 
	 * @param result - The result to be sended to FE
	 * @param request - The request object that represents the call
	 */
	private void sendResponse ( Object result, ClientCall request ) {
		
		try {
			// Serialize the response object
			ByteArrayOutputStream bs = new ByteArrayOutputStream () ;
			ObjectOutputStream os = new ObjectOutputStream ( bs ) ;
			os.writeObject(result);
			
			byte[] sendBuffer = bs.toByteArray() ;
			DatagramPacket sendPacket = new DatagramPacket ( sendBuffer, sendBuffer.length, 
					request.getFEIPAddress(), request.getFEPortNumber() ) ;
			socket.send ( sendPacket ) ;			
		} catch ( IOException e ) {
			System.out.println ( "Exception: " + e.getMessage() ) ;
		}

	}
	
	/**
	 * Performs teh request on behalf of the client
	 * @param request - The request object that represents the call
	 * @return Object - The response Object
	 */
	private Object callMethod ( ClientCall request ) {
		if ( request instanceof createAccountCall ) {
			createAccountCall call = ( createAccountCall ) request ;
			 return dispatcher.dispatch ( call ) ;
		} else if ( request instanceof reserveBookCall ) {
			reserveBookCall call = ( reserveBookCall ) request ;
			return dispatcher.dispatch ( call ) ;
		} else if ( request instanceof reserveInterLibraryCall ) {
			reserveInterLibraryCall call = ( reserveInterLibraryCall ) request ;
			return dispatcher.dispatch ( call ) ;
		} else if ( request instanceof setDurationCall ) {
			setDurationCall call = ( setDurationCall ) request ;
			return dispatcher.dispatch ( call ) ;
		} else if ( request instanceof getNonReturnersCall ) {
			getNonReturnersCall call = ( getNonReturnersCall ) request ;
			return dispatcher.dispatch ( call ) ;
		}
		return null ;
	}
}
