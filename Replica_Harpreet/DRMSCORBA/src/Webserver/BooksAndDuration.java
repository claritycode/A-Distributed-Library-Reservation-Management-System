package Webserver;



public class BooksAndDuration 
{
	String g_bookName;			//the varilble to store the name of the book 
	int g_returnDate;			//the variable that tells about the return date of the book
	/*DESC:Function to initialize tha member variable of the class
	PARAM1:
		PARAM2:
			PARAM3:
				PARAM4:
					RETURN:
					*/
	public BooksAndDuration( String _bookName,int _returnDate)
	{
		synchronized(this)
		{
		g_bookName=_bookName;
		g_returnDate=_returnDate;
		}
		
		
	}

}
