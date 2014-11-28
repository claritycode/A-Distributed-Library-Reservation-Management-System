package DRMSIDL;


public class ReserveInterLibraryThread implements Runnable
{
	String _bookName, _authorName;
	ReserveInterLibraryThread(String _bookName, String _authorName)
	{
		this._bookName=_bookName;
		this._authorName=_authorName;
	}
	public void run()
	{
		
	}

}
