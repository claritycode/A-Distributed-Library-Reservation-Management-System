import java.net.InetAddress;
import java.io.Serializable ;

/**
 * 
 * @author Parth Patel
 * A <code> ClientCall </code> is a generic representation of a client's request. This class is inherited by specific serivce calls
 *
 */
public class ClientCall implements Serializable{
	
	private int sequenceNumber ;
	private InetAddress FEIPAddress ;
	private int FEPortNumber ;
	
	private String username ;
	private String password ;

	public ClientCall (  InetAddress ip, int port, String us, String ps ) {
		FEIPAddress = ip ;
		FEPortNumber = port ;
		username = us ;
		password = ps ;
	}
	
	/**
	 * @return the sequenceNumber
	 */
	public int getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * @param sequenceNumber the sequenceNumber to set
	 */
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * @return the fEIPAddress
	 */
	public InetAddress getFEIPAddress() {
		return FEIPAddress;
	}

	/**
	 * @return the fEPortNumber
	 */
	public int getFEPortNumber() {
		return FEPortNumber;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
}
