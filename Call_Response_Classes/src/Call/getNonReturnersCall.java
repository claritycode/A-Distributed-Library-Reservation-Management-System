package Call;

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
		
	private int days ;
	
	public getNonReturnersCall ( InetAddress ip, int port, 
			String us, String ps, String edu, int d ) {
		
		super ( ip, port, us, ps, edu ) ;
		days = d ;
	}
	
	/**
	 * @return the days
	 */
	public int getDays() {
		return days;
	}
	
}
