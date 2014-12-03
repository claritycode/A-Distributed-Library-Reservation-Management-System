package Webserver;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.swing.text.Style;

@WebService
@SOAPBinding(style=javax.jws.soap.SOAPBinding.Style.RPC)
public interface DRMSServerInterface
{
	@WebMethod
	public boolean createAccount (String firstName, String lastName, String email, String phoneNumber, String username, String password, String educationalInstitute ) ;
	@WebMethod
	public boolean reserveBook ( String username, String password, String bookName, String authorName ) ;
	@WebMethod
	public boolean reserveInterLibrary(String _username,String _password, String _bookName,String _authorName);
	@WebMethod
	public boolean setDuration(String student,String _bookName, int _noOfDays);
	@WebMethod
	public boolean addBook(String _bookName,String _authorName, int _noOfCopies);
	@WebMethod
	public String getNonReturners(int duration);
	
	@WebMethod
	public boolean studentLogin(String _username,String _password);
}
