import java.io.* ;
import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import java.util.Properties ;

/**
 * @author Parth Patel
 * A non GUI client interface to use the library services
 * It takes Student input, gets the appropriate remote object of the University and performs the request
 * and delivers the response.
 * 
 * */
public class StudentClient implements Runnable {
	
	private ORB clientORB ;	
	private NamingContext directory ;
	
	/**
	 * Constructor - Generates a new student client
	 * It takes the initial port number of NameService as an argument.
	 * */
	public StudentClient ( String[] args ) {
		try {
			// Get system properties
			Properties p = System.getProperties () ;
			clientORB = ORB.init( args, p ) ;
			
			try {
				org.omg.CORBA.Object obj = clientORB.resolve_initial_references( "NameService" ) ;
				directory = NamingContextHelper.narrow(obj) ;
			} catch ( org.omg.CORBA.ORBPackage.InvalidName e ) {
				System.out.println ( "Exception:  " + "Invalid service requested" ) ;
			}
		} catch ( Exception e ) {
			System.out.println ( "Eception " + e.getMessage() ) ;
		}
	}
	
	public void run() {
		
			
		String[] institute = {"Concordia University", "McGill University", "Vanier College"} ;
		String[] book = { "CM", "MV", "CV", "name0" } ;
		int rand = (int) (Math.random() * 10) ;
		int randInstitute = (int)(Math.random() * 3) ;
		int randBook = (int)(Math.random() * 4 ) ;
		LibraryInterface lib = getRemoteObject ( institute[randInstitute] ) ;
		String firstName = "First" + rand ;
		String lastName = "last" + rand ;
		String email = "emailaddress" + rand ;
		String phoneNumber = "51451451" + rand;
		String username = "username" + rand ;
		String password = "password" + rand ;
		String educationalInstitute = institute[randInstitute];
		
		String result ;
		result = lib.createAccount(firstName, lastName, email, phoneNumber, username, password, educationalInstitute);

		String reserveBook ;
		reserveBook = lib.reserveBook(username, password, book[randBook], book[randBook]) ;
		
		randBook = (int) ( Math.random() * 4 ) ;
		String reserveInterBook ;
		reserveInterBook = lib.reserveInterLibrary(username, password, book[randBook], book[randBook]) ;			
	}
	
	/**
	 * Give student a Menu
	 * */
	public void getMenu () {
				
		while ( true ) {
			try {
				
				
				InputStreamReader in = new InputStreamReader ( System.in ) ;
				BufferedReader r = new BufferedReader ( in ) ;
				System.out.println( "Enter an operation" );
				System.out.println("1. Create Account" ); 
				System.out.println("2. Reserve Book");
				System.out.println("3. Reserve Inter Library") ;
				System.out.println("4. Exit") ;
				int choice = Integer.parseInt(r.readLine()) ;
				if ( choice  == 1 ) {
					createAccount () ;
				} else if ( choice == 2 ) {
					reserveBook () ;
				} else if ( choice == 3 ) {
					reserveInterLib () ;
				} else if ( choice == 4 ) {
					break ;
				} else {
					System.out.println( "You have entered a wrong choice. Please try again" ) ;
					continue ;
				}
				System.out.println("Press any key to continue:...");
				r.readLine() ;
			} catch ( IOException e ) {
				System.out.println( e.getMessage() ) ;
				break ;
			} catch ( NumberFormatException e ) {
				System.out.println( "You have entered a wrong choice. Please try again" ) ;
				continue ;
			} 
		}
	}

	/**
	 * Provides an interface to create account to the client
	 * */
	public void createAccount () throws IOException {
		InputStreamReader in = new InputStreamReader ( System.in ) ;
		BufferedReader r = new BufferedReader ( in ) ;
		System.out.print("Enter the first name: ");
		String fName = r.readLine () ;
		System.out.print("Enter the last name: ");
		String lName = r.readLine () ;
		System.out.print("Enter the phone number: ");
		String phoneNumber = r.readLine () ;
		System.out.print("Enter the username: ");
		String userName = r.readLine () ;
		System.out.print("Enter the password: ");
		String password = r.readLine () ;
		System.out.print("Enter the email address: ");
		String email = r.readLine () ;
		System.out.print("Enter the educational institue: ");
		String educationalInstitute = r.readLine () ;
		
		LibraryInterface lb = getRemoteObject ( educationalInstitute ) ;
		
		if ( lb == null ) {
			System.out.println( "Sorry! the demanded service can't be provided at the moment" );
			System.out.println("Please try again later" );
		}
		
		try {
			String result ;
			result = lb.createAccount(fName, lName, email, phoneNumber, userName, password, educationalInstitute);
			System.out.println ( result ) ;
		} catch ( Exception e ) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * Provides an Interface to client to reserve Book
	 * */
	public void reserveBook () throws IOException {
		InputStreamReader in = new InputStreamReader ( System.in ) ;
		BufferedReader r = new BufferedReader ( in ) ;
		System.out.print("Enter the username: ");
		String userName = r.readLine () ;
		System.out.print("Enter the password: ");
		String password = r.readLine () ;
		System.out.print("Enter the educational institue: ");
		String educationalInstitute = r.readLine () ;
		System.out.print("Enter the book name: ");
		String bookName = r.readLine () ;
		System.out.print("Enter the author name: ");
		String authorName = r.readLine () ;
		
		LibraryInterface lb = getRemoteObject ( educationalInstitute ) ;
 
		
		if ( lb == null ) {
			System.out.println( "Sorry! the demanded service can't be provided at the moment" );
			System.out.println("Please try again later" );
		}
		
		try {
			String result ;
			result = lb.reserveBook(userName, password, bookName, authorName) ;
			System.out.println ( result ) ;
		} catch ( Exception e ) {
			System.out.println("Exception: " + e.getMessage() );
		}
	}
	
	/**
	 * Provides an interface to the client to reserve inter library
	 * */
	public void reserveInterLib () throws IOException {
		InputStreamReader in = new InputStreamReader ( System.in ) ;
		BufferedReader r = new BufferedReader ( in ) ;
		System.out.print("Enter the username: ");
		String userName = r.readLine () ;
		System.out.print("Enter the password: ");
		String password = r.readLine () ;
		System.out.print("Enter the educational institue: ");
		String educationalInstitute = r.readLine () ;
		System.out.print("Enter the book name: ");
		String bookName = r.readLine () ;
		System.out.print("Enter the author name: ");
		String authorName = r.readLine () ;
		
		LibraryInterface lb = getRemoteObject ( educationalInstitute ) ;
 
		
		if ( lb == null ) {
			System.out.println( "Sorry! the demanded service can't be provided at the moment" );
			System.out.println("Please try again later" );
		}
		
		try {
			String result ;
			result = lb.reserveInterLibrary(userName, password, bookName, authorName) ;
			System.out.println ( result ) ;
		} catch ( Exception e ) {
			System.out.println("Exception: " + e.getMessage() );
		}
	}
	
	
	/**
	 * Helper method - to get a remote object reference based on the String argument
	 * */
	public LibraryInterface getRemoteObject ( String educationalInstitute ) {
		
		NameComponent[] name = new NameComponent[1] ;
		name[0] = new NameComponent () ;
		name[0].id = educationalInstitute ;
		name[0].kind = "library" ;
		LibraryInterface lib = null ;

		try {
			org.omg.CORBA.Object obj = directory.resolve(name) ;
			lib = LibraryInterfaceHelper.narrow(obj) ;
		} catch ( org.omg.CosNaming.NamingContextPackage.NotFound e ) {
			System.out.println ( "The requested binding is not present in the directory" ) ;
		} catch ( org.omg.CosNaming.NamingContextPackage.CannotProceed e ) {
			System.out.println ( "Can't proced due to some implementation error: " + e.getMessage() ) ;
		} catch ( org.omg.CosNaming.NamingContextPackage.InvalidName e ) {
			System.out.println ( "You have entered an invalid library name" ) ;
		}
		
		return lib ;
	}
	
}
