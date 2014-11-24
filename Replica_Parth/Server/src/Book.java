/**
 * @author Parth Patel
 * A <code>Book</code> represents a book in the library. Every book has a name, author 
 * and a certain number of copies maintained in the library. A <code>Book</code> is uniquely
 * identified by its name and the author's name.
 * */
public class Book {

	private final String name;
	private final String author ;
	private Integer copies ;
	
	/**
	 * Constructs a new Book.
	 * @param newName - Name of the book
	 * @param newAuthor - Name of the author
	 * @param newCopies - NUmber of copies of the book available in the library
	 * */
	public Book ( String newName, String newAuthor, int newCopies ) {
		name = newName ;
		author = newAuthor ;
		copies = newCopies ;
	}
	
	/**
	 * Constructs a new <code>Book</code>. 
	 * Number of copies of the <code>Book</code> available in the library is set to 1.
	 * @param newName - Name of the book
	 * @param newAuthor - Name of the author
	 * */
	public Book ( String newName, String newAuthor ) {
		name = newName ;
		author = newAuthor ;
		copies = 1 ;
	}
	
	public String getName () {
		return name ;
	}
	
	public String getAuthor () {
		return author ;
	}
	
	public int getCopies () {
		return copies ;
	}
	
	/**
	 * Reserves one copy of the <code>Book</code>. 
	 * This method internally decrements the number of copies of the book available in the library.
	 * @return boolean - Indicates if the operation was successful.
	 * */
	public boolean order () {
		/*Maintain data integrity while allowing concurrent access to the same Book object
		 by synchronizing shared data i.e copies */
		synchronized ( copies ) {
			if ( copies > 0 ) {
				--copies ;
				return true ;
			} else {
				return false ;
			}
		}
			

	}

	/**
	 * Generate hashcode for the <code>Book</code>.
	 * Book name and author name is used to generate hashcode.
	 * @return int - the generated hashcode
	 * */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Compares the <code>Book</code> with another <code>Book</code>
	 * @param obj - Another <code>Book</code>
	 * @return boolean - Indicates if two <code>Book</code> are equal.
	 * */
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
