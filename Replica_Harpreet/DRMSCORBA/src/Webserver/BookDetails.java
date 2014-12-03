package Webserver;



public class BookDetails 
{
	String m_bookName;			// name of the book 
	String m_authorName;	// name of the author of the book 
	int m_noOfCopies;			// number of copies that book has initially 
	
	public BookDetails(String _bookName,String _authorName,int _noOfCopies)
	{
		
		
		m_bookName=_bookName;
		m_authorName=_authorName;
		m_noOfCopies=_noOfCopies;
		
	}
	/*public BookDetails(String _bookName,String _authorName,int _copies)
	{
		synchronized(this)
		{
		m_bookName=_bookName;
		m_authorName=_authorName;
		m_noOfCopies=_copies;
		}
	}*/
}
