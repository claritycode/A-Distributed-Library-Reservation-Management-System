package entities;

public abstract class User {

	/** Unique username (minimum 6 characters and max 15 characters) */
	private final String username;
	/** Password of at least 6 characters */
	private String password;
	/** Educational Institution */
	private final String institution;

	/**
	 * User constructor. All arguments should be not empty.
	 * 
	 * @param username	Unique username. Can be set only once.
	 * @param password	User password.
	 * @param institution	Educational institution linked to the user. Can be set only once.
	 * @throws IllegalArgumentException If any of the arguments is not valid.
	 */
	public User(final String username, final String password, final String institution) {
		if (username == null || username.length() == 0 || password == null || password.length() == 0 || institution == null
				|| institution.length() == 0) {
			throw new IllegalArgumentException("One of the parameters was null or empty: username = " + username
					+ "\tpassword = " + password + "\tinstitution = " + institution);
		}
		this.username = username;
		this.password = password;
		this.institution = institution;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInstitution() {
		return institution;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", institution=" + institution + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((institution == null) ? 0 : institution.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (institution == null) {
			if (other.institution != null)
				return false;
		} else if (!institution.equals(other.institution))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
