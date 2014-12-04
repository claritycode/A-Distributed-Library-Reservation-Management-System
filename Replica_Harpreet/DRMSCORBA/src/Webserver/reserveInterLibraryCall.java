package Webserver;
import java.io.Serializable ;
import java.net.*;

/**
 * 
 * @author Parth Patel
 * A <code>reserveInterLibraryCall</code> represents a client request to 
 * reserve a book using inter library facility. It is forwarded
 * by the Front End & interpreted by sequencer as well as the replica.
 *
 */

public class reserveInterLibraryCall extends ClientCall implements Serializable {

	private String bookName ;
	private String authorName ;
	
	public reserveInterLibraryCall ( InetAddress ip, int port, String us, String ps,
			String bk, String au, String edu ) {
		
		super ( ip, port, us, ps, edu ) ;
		bookName = bk ;
		authorName = au ;
	}
	
	/**
	 * @return the bookName
	 */
	public String getBookName() {
		return bookName;
	}

	/**
	 * @return the authorName
	 */
	public String getAuthorName() {
		return authorName;
	}

}
