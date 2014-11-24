
import java.io.Serializable;
import java.net.*;

/**
 * 
 * @author Parth Patel
 * A <code>createAccountCall</code> represents a client request to create a new account. It is forwarded
 * by the Front End & interpreted by sequencer as well as the replica.
 *
 */

public class createAccountCall extends ClientCall implements Serializable{

	private String firstName ;
	private String lastName ;
	private String email ;
	private String phoneNumber ;
	private String educationalInstitute ;
	
	public createAccountCall ( InetAddress ip, int port, String f, String l, String e,
			String p, String u, String ps, String edu ) {
		
		super ( ip, port, u, ps ) ;
		
		firstName = f ;
		lastName = l ;
		email = e ;
		phoneNumber = p ;
		educationalInstitute = edu ;
	}
	
	
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	/**
	 * @return the educationalInstitute
	 */
	public String getEducationalInstitute() {
		return educationalInstitute;
	}
}