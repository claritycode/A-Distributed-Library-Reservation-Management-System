
import java.io.Serializable ;

/**
 * 
 * @author Parth Patel
 * A <code>LateStudent</code> represents the necessary details to contact a <code>Student</code> who has due books. 
 *
 */
public class LateStudent implements Serializable {
	 private String firstName ;
	 private String lastName ;
	 private String phoneNumber  ;
	
	public LateStudent ( String f, String l, String ph ) {
		firstName = f ;
		lastName = l ;
		phoneNumber = ph ;
	}
	
	public boolean equals ( LateStudent rhs ) {
		if ( firstName.equals( rhs.getFirstName()) &&
				lastName.equals( rhs.getLastName() ) && 
				phoneNumber.equals( rhs.getPhoneNumber()) ) {
			return true ;
		}
		else {
			return false ;
		}
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
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
}
