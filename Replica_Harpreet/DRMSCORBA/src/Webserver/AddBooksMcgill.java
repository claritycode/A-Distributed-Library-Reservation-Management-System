package Webserver;



public class AddBooksMcgill implements Runnable
{
DRMSMcgill server;		// making the Object of the Library server and Invoking the Function for Reservation ofBook on It
	
	public AddBooksMcgill(DRMSMcgill server)
	{
		this.server=server;// intailizing the Server Variable with the Value that is the Object passed by the Server while calling this function in threads
	}
	/*DESC:RUN FUNCTION TO RANDOMLY ADD BOOK TO THE SERVER
	
		
					*/
	@Override
	public void run() 
	{
		int i=(int)(Math.random()*10);
		server.addBook("book2","author2",4);
		
	}

}
