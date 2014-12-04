
/**
 * This is the Main class that starts the Internal Client
 * @author Parth Patel
 *
 */
public class InternalClientMain {
	
	public static void main ( String[] args ) {
		// Create A RequestReceiver
		RequestReceiver internalClient = new RequestReceiver ( 5122, "rm1", args ) ;
		
		// Start Receiver in infinite thread
		Thread serveRequest = new Thread ( internalClient ) ;
		serveRequest.start();
	}
}
