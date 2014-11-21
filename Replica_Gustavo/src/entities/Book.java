package entities;

/**
 * Entity representing a book in the system.
 * The book structure should contain the following fields:
 * Name of the book
 * Author of the book
 * Number of copies available
 */
public class Book {
	/** The name of the book. */
	private final String name;
	/** The author of the book. */
	private final String author;
	/** The number of copies available. */
	private int copies;
	
	public Book(String name, String author) {
		super();
		this.name = name;
		this.author = author;
	}

	public Book(String name, String author, int copies) {
		super();
		this.name = name;
		this.author = author;
		this.copies = copies;
	}

	public int getCopies() {
		return copies;
	}

	public void setCopies(int copies) {
		this.copies = copies;
	}

	public String getName() {
		return name;
	}

	public String getAuthor() {
		return author;
	}

	@Override
	public String toString() {
		return "Book [name=" + name + ", author=" + author + ", copies=" + copies + "]";
	}

	/**
	 * Hash calculated over author and name.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Equals over author and name.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
