package entities;

public class Reservation {

	public final static int DEFAULT_PERIOD = 14;

	/** user that holds reservation */
	private final String username;
	/** reserved book */
	private final Book book;
	/** the name of the library that owns the book. */
	private final String library;
	/** duration to return in days */
	private int duration;
	

	/**
	 * Creates a reservation for a user with the desired book with the default period of duration do return.
	 * 
	 * @param username
	 * @param book
	 */
	public Reservation(final String username, final Book book, final String library) {
		super();
		this.username = username;
		this.book = book;
		this.library = library;
		this.duration = DEFAULT_PERIOD;
	}

	public String getUsername() {
		return username;
	}

	public Book getBook() {
		return book;
	}

	public int getDuration() {
		return duration;
	}
	
	public String getLibrary() {
		return library;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "Reservation [username=" + username + ", book=" + book + ", library=" + library + ", duration=" + duration + "]";
	}

	/**
	 * hash calculated over book and username.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((book == null) ? 0 : book.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((library == null) ? 0 : library.hashCode());
		return result;
	}

	/**
	 * Equals over book, username and library.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reservation other = (Reservation) obj;
		if (book == null) {
			if (other.book != null)
				return false;
		} else if (!book.equals(other.book))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (library == null) {
			if (other.library != null)
				return false;
		} else if (!library.equals(other.library))
			return false;
		return true;
	}

}
