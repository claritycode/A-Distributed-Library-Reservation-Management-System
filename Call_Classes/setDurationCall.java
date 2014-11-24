import java.io.Serializable ;
import java.net.* ;

/**
 * 
 * @author Parth Patel
 * A <code>setDurationCall</code> represents an admin request to 
 * set the loan duration for a book issued by a particular student to a particular value. It is forwarded
 * by the Front End & interpreted by sequencer as well as the replica.
 *
 */

public class setDurationCall extends ClientCall implements Serializable {
		
	private String studentUsername ;
	private String bookName ;
	private String authorName ;
	private long days ;
	
	public setDurationCall ( InetAddress ip, int port, 
			String us, String ps, String stu, String bk, String au, long d, String edu ) {
		
		super ( ip, port, us, ps, edu ) ;
		studentUsername = stu ;
		bookName = bk ;
		authorName = au ;
		days = d ;
	}
	
	/**
	 * @return the studentUsername
	 */
	public String getStudentUsername() {
		return studentUsername;
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

	/**
	 * @return the days
	 */
	public long getDays() {
		return days;
	}
}
