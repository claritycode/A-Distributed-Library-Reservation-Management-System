
import java.io.* ;
import java.net.*;
import java.util.ArrayList ;

/**
 * @author Parth Patel
 * A <code>Registry</codE> represents a database of library server addresses. 
 * It consist of the IP Address and port number of the library servers.
 * This adresses can be used by peers to send UDP messages to the server
 * A registry allows servers to register themselves with it and get the address of other servers.
 * */
public class Registry implements Runnable {

	private File regFile ;
	private int portNo ;
	private ArrayList<LibraryAddress> regDatabase = new ArrayList<LibraryAddress> () ;
	
	/**
	 * Constructor-
	 * Generates a new Registry on the specified port number and uses the specified fileName to store th database
	 * @param- fileName - the name of the file to be used as database
	 * @param newPortNo - the port number of the Registry
	 * */
	public Registry ( String fileName, int newPortNo ) {
			regFile = new File ( fileName ) ;
			portNo = newPortNo ;	
	}
	
	/**
	 * Overridden method from Runnable interface.
	 * It runs through an infinite loop, receiving UDP messages and starting a new <code>RegistryServer</code> 
	 * thread for each message. Hence, it is a concurrent server.
	 * */
	public void run () {
		DatagramSocket socket = null ;
		try {
			socket = new DatagramSocket ( portNo ) ;
			// Infinite Loop
			while ( true ) {
				byte[] recvBuffer = new byte[512] ;
				DatagramPacket recvPack = new DatagramPacket ( recvBuffer, recvBuffer.length ) ;
				socket.receive(recvPack);
				
				// Create a new RequestServer for each request
				// The request server will then serve the requests
				RequestServer rs = new RequestServer ( recvPack, this ) ;
				Thread t = new Thread ( rs ) ;
				t.start() ;
			}		
			
		} catch ( SocketException e ) {
			System.out.println ( "A socket Exception happened" + e.getMessage() ) ;
		} catch ( IOException e ) {
			System.out.println ( "An Io Exception happened" + e.getMessage() ) ;
		} finally {
			if ( socket != null ) {
				socket.close() ;
			}
		}
	}
	
	/**
	 * Add a new <code>LibraryAddress</code> to the Registry.
	 * It also adds the address to the associated file as a serialized object of type <code>LibraryAddress</code>
	 * @param newAddress - A <code>LibraryAddress/code> object to be added to the Registry
	 * */
	public synchronized void addAddress ( LibraryAddress newAddress ) {
		regDatabase.add( newAddress ) ;
		try {
			// Serialize the object and add it to the file
			FileOutputStream fout = new FileOutputStream ( regFile ) ;
			ObjectOutputStream os = new ObjectOutputStream ( fout ) ;
			os.writeObject(newAddress);
			os.close();
		} catch ( IOException e ) {
			System.out.println ( "An Exception happened" + e.getMessage() ) ;
		} 
	}
	
	/**
	 * Returns the database consisting of Registry address to the caller
	 * @return Arraylist of all the <code>LibraryAddress</code> with the Registry
	 * */
	public ArrayList<LibraryAddress> getAddress () {
		return regDatabase ;
	}
}