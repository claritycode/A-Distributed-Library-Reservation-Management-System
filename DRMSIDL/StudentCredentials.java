package DRMSIDL;


import java.io.Serializable;
import java.util.ArrayList;


public class StudentCredentials implements Comparable<StudentCredentials>  
{
	public String first_name;
	public String last_name;
	public String email;
	public String username;
	public String password;
	long phone_no;
	String institute;
	int fine;
	ArrayList<BooksAndDuration> books_duration;
	
	
	/*DESC:THE constructor that initalize the values of the Student Credentials 
	PRAM1:First Name of the Student
	PARAM2:Last name of the student
	PARAM3:Email name of the student
	PARAM4:Username of the student
		PARAM5:Paasword of the student
		PARAM6:Institute name of the student*/
	
	
	StudentCredentials(String _first_name,String _last_name,String _email,String _username,String _password,String _institute,long _phone_no)	//String _name_of_book but not required as Reserve book list will be separate
	{
		
		books_duration = new ArrayList<BooksAndDuration>();
		first_name=_first_name;
		last_name=_last_name;
		email=_email;
		username=_username;
		password=_password;
		institute=_institute;
		phone_no=_phone_no;
		//books_duration.add(new BooksAndDuration(_name_of_book));
		
		
	}
	@Override
	public int compareTo(StudentCredentials arg0) 
	{
		return username.compareTo(arg0.username);
	}
	 
	
}
