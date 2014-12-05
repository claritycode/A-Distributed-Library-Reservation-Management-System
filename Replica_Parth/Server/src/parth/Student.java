package parth;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.io.* ;

/**
 * @author Parth Patel
 * A <code>Student</code> is a registered member of the library. 
 * A <code>Student</code> is the primary guest/customer of the library.
 * A <code>Student</code> can register with the library only if he is a registered student of the university.
 * A <code>Student</code> provides his name, email address and phone number.
 * However, the <code>Student</code> is mostly identified by a combination of username and password
 * A <code>Student</code> can also have reserved <code>Books</code> and may have some fine.
 * A <code>Student</code> can also view their respective activity log. 
 * */
public class Student {
	
	private String firstName ;
	private String lastName ;
	private String emailAddress ;
	private String phoneNumber ;
	private String username ;
	private String password ;
	private String educationalInstitute ;
	private int fines ;
	private HashMap<Book,Integer> books ;
	private FileWriter logFile ;
	
	/**
	 * Constructor- Creates a new <code>Student</code>
	 * @param newFName- First name of the <code>Student</code>
	 * @param newLname - Last name of the <code>Student</code>
	 * @param newEMail - Email Address of the <code>Student</code>
	 * @param newPNo - Phone Number of the <code>Student</code>
	 * @param newUName - Username of the <code>Student</code>
	 * @param newPass - Password of the <code>Student</code>
	 * @param newEI - Educational Institue of the <code>Student</code>
	 * @throws Exception - Indicates the invalidity of some input information or failure to develop appropriate environment
	 * */
	public Student ( String newFName, String newLName, String newEMail, String newPNo, String newUName, 
			String newPass, String newEI ) throws Exception {
		firstName = newFName ;
		lastName = newLName ;
		emailAddress = newEMail ;
		phoneNumber = newPNo ;
		setUserName ( newUName ) ;
		setPassword ( newPass ) ;
		educationalInstitute = newEI ;
		fines = 0 ;
		books = new HashMap<Book, Integer> () ;
		logFile = new FileWriter ( educationalInstitute + "_Student_Files/" + username +".txt" ) ;
		String message = username + " account created at " + Calendar.getInstance().getTime () ;
		writeLog(message) ;

	}
	
	/**
	 * Set the username of the student.
	 * The username should be more than 6 and less than 16 characters
	 * @throws Exception - Indicates an invalid username
	 * */
	public void setUserName ( String newUserName ) throws Exception {
		if ( newUserName.length() > 5 && newUserName.length() < 16 ) {
			username = newUserName ;
			return ;
		}
		else {
			throw new Exception ( "Invalid user name" ) ;
		}
	}
	
	/**
	 * Set the password of the student.
	 * The password should be more than 6 elements long.
	 * @throws Exception - Indicates an invalid password
	 * */
	public void setPassword ( String newPassword ) throws Exception {
		if ( newPassword.length() > 5 ) {
			password = newPassword ;
			return ;
		}
		else {
			throw new Exception ( "Invalid Password " ) ;
		}
	}
	
	public String getFirstName () {
		return firstName ;
	}
	
	public String getLastName () {
		return lastName ;
	}
	
	public String getPhoneNumber () {
		return phoneNumber ;
	}
	
	public String getUserName () {
		return username ;
	}
	
	public String getPassword () {
		return password ;
	}
	
	public String getEmailAddress () {
		return emailAddress ;
	}
	
	/**
	 * Adds the <code>Book</code> to the list of books issued by the <code>Student</code>.
	 * The default reservation period is 14 days.
	 * @param - newBook - the <code>Book</code> to be added to the reserved list.
	 * */
	public void reserve ( Book newBook ) {
		
		books.put(newBook, 14 ) ;
		String message = "Reserved a book titled " + newBook.getName() + " at " + Calendar.getInstance().getTime();
		writeLog(message) ;

	}
	
	/**
	 * Calulates the total fine for this student.
	 * The fine is the summation of fine for all the books reserved by this <code>Student</code>
	 * @param days - The number of days after which fine is incurred on the book
	 * @return fines - the total fine for the <code>Student</code>
	 * */
	public int calculateFines ( int days ) {
		
		Integer additionalDays = 0;
		//Loops throw the hashmap consisting of books issued by the student
		for ( Map.Entry<Book, Integer> loanDuration: books.entrySet() ) {
			
			additionalDays = loanDuration.getValue() - days ;
			if ( additionalDays < 0 ) {
				fines = fines + additionalDays ;
			}
		}
		return fines ;
	}
	
	/**
	 * Determine if the <code>Student</code> is a non returner.
	 * @param days- Number of days beyond which the student should have atleast one book to be non returner
	 * @return boolean - Indicate if the student is a non returner
	 * */
	public boolean isNonReturner ( int days ) {
		Integer additionalDays ;
		for ( Map.Entry<Book, Integer> loanDuration: books.entrySet() ) {
			additionalDays = loanDuration.getValue() - days ;
			if ( additionalDays < 0 ) {
				return true ;
			}
		}
		
		return false ;
	}
	
	/**
	 * Debug tool- set the duration of a <code>Book</code> issued by this student to the specified days
	 * @param book - the <code>Book</code> whose duration needs to be set
	 * @param days - Number of days to which duration must be set
	 * @return - Indicates the success or the failure of the operation
	 * */
	public boolean setDuration ( Book book, Integer days ) {
		
		// Loop through the HashMap consisting of the books reserved by this Student
		for ( Map.Entry<Book, Integer> bookList : books.entrySet() ) {
			// Compare the book name
			if ( bookList.getKey().getName().equalsIgnoreCase(book.getName()) ) {
				bookList.setValue( days ) ;			// Set the days
				return true ;
			}
		}
		
		return false;
	}
	
	// Helper method
	// Write the given message to the activity log of the student.
	private void writeLog ( String message ) {
		// Multiple access to the activity log of the same student must be synchronized as it is a shared resource
		synchronized ( logFile ) {
			try {
				logFile.write (message + System.lineSeparator()) ;
				logFile.flush();
			} catch ( IOException e ) {
				System.out.println ( e.getMessage() ) ;
			}
			
		}
	}
	
}
