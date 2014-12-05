package parth;

import java.io.* ;
import java.net.* ;

/**
 * @author Parth Patel
 * A <code>LibraryAddress</code> represents the IP Address and 
 * Port number of the library server where it can receive UDP requests
 * */
public class LibraryAddress implements Serializable {
	
	private String name ;
	private InetAddress ipAddress ;
	private Integer portNo ;
	
	public String getName() {
		return name ;
	}
	
	public InetAddress getIPAddress() {
		return ipAddress ;
	}
	
	public Integer getPortNo() {
		return portNo ;
	}
	
	public LibraryAddress ( String newName, InetAddress newIPAddress, Integer newPortNo ) {
		name = newName ;
		ipAddress = newIPAddress ;
		portNo = newPortNo ;
	}
}
