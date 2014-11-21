package entities;

import java.util.Hashtable;

public class Student extends User {

	private String firstName;
	private String lastName;
	/** Email Address */
	private String email;
	/** Phone Number */
	private String phone;
	/** Reserved books and duration to return the books */
	private final Hashtable<String, Reservation> reservations; 
	// /** Fines accumulated */
	// not used anywhere
	// private List<BigDecimal> fines;
	
	public Student(String username, String password, String institution) {
		super(username, password, institution);
		this.reservations = new Hashtable<String, Reservation>();
	}
	
	public Student(String username, String password, String institution, String firstName, String lastName, String email,
			String phone) {
		super(username, password, institution);
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.reservations = new Hashtable<String, Reservation>();
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Hashtable<String, Reservation> getReservations() {
		return reservations;
	}

	@Override
	public String toString() {
		return "Student [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", phone=" + phone
				+ ", username=" + getUsername() + ", institution=" + getInstitution() + "]";
	}
}
