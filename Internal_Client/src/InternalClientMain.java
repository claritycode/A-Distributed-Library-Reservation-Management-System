
/**
 * This is the Main class that starts the Internal Client
 * @author Parth Patel
 *
 */
public class InternalClientMain {
	
	public static void main ( String[] args ) {
		// Create A RequestReceiver
		RequestReceiver internalClient = null ;
		if ( args[0].equals("rm1")) {
			internalClient = new RequestReceiver ( 5222, "rm1", args ) ;
		}
		if ( args[0].equals("rm2")) {
			internalClient = new RequestReceiver ( 5223, "rm2", args ) ;
		}
		if ( args[0].equals("rm3")) {
			internalClient = new RequestReceiver ( 5224, "rm3", args ) ;
		}
		
		
		// Start Receiver in infinite thread
		Thread serveRequest = new Thread ( internalClient ) ;
		serveRequest.start();
	}
}
