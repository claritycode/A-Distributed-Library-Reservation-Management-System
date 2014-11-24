import java.io.Serializable ;
import java.net.* ;

/**
 * 
 * @author Parth Patel
 * A <code>getNonReturnersCall</code> represents an admin request to 
 * get the list of non returners from all the libraries. It is forwarded
 * by the Front End & interpreted by sequencer as well as the replica.
 *
 */
public class getNonReturnersCall extends ClientCall implements Serializable {
		
	private String educationalInstitute ;
	private long days ;
	
	public getNonReturnersCall ( InetAddress ip, int port, 
			String us, String ps, String edu, long d ) {
		
		super ( ip, port, us, ps ) ;
		educationalInstitute = edu ;
		days = d ;
	}
	
	/**
	 * @return the educationalInstitute
	 */
	public String getEducationalInstitute() {
		return educationalInstitute;
	}

	/**
	 * @return the days
	 */
	public long getDays() {
		return days;
	}
	
}
