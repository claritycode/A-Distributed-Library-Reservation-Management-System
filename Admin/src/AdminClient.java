import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

import java.util.Properties ;

import org.omg.CosNaming.NameComponent;

import util.NonReturnersParser;
import DRMSServices.LibraryInterface;
import DRMSServices.LibraryInterfaceHelper;
import DRMSServices.nonReturners;

/**
 * @author Parth Patel
 * A non GUI client interface to use the library services
 * It takes admin input, gets the appropriate remote object of the University and performs the request
 * and delivers the response.
 * 
 * */
public class AdminClient {
	
	private ORB adminORB ;
	private NamingContext directory ;
	
	/**
	 * Constructor - Generates a new student client
	 * It takes the initial port number of NameService as an argument.
	 * */
	public AdminClient ( String[] args ) {
		try {
			Properties p = System.getProperties () ;
			adminORB = ORB.init( args, p ) ;
			
			try {
				org.omg.CORBA.Object obj = adminORB.resolve_initial_references( "NameService" ) ;
				directory = NamingContextHelper.narrow(obj) ;
			} catch ( org.omg.CORBA.ORBPackage.InvalidName e ) {
				System.out.println ( "Exception:  " + "Invalid service requested" ) ;
			}
		} catch ( Exception e ) {
			System.out.println ( "Exception " + e.getMessage() ) ;
		}
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
					System.out.println("1. Get Non Returners" );
					System.out.println("2. Set Duration" );
					System.out.println("3. Exit");
					int choice = Integer.parseInt(r.readLine()) ;
					if ( choice  == 1 ) {
						getNonReturners () ;
					} else if ( choice == 2 ) {
						setDuration() ;
					} else if ( choice == 3 ){
						break ;
					}else {
						System.out.println( "You have entered a wrong choice. Please try again" ) ;
						continue ;
					}
					System.out.println("Press any key to continue:...");
					r.readLine() ;
				} catch ( IOException e ) {
					System.out.println( e.getMessage() ) ;
					break ;
				} 
				catch ( NumberFormatException e ) {
					System.out.println( "You have entered a wrong choice. Please try again" ) ;
					continue ;
				}
			}
		}
	
	/**
	 * Provides a GUI interface to the Admin to obtain non returners
	 * */
	public void getNonReturners () {
		
		try {
			InputStreamReader in = new InputStreamReader ( System.in ) ;
			BufferedReader r = new BufferedReader ( in ) ;
			System.out.println("Enter the username: ");
			String username = r.readLine () ;
			System.out.println("Enter the pasword: ");
			String password = r.readLine () ;
			System.out.println("Enter the educational institute: ");
			String educationalInstitute = r.readLine () ;
			System.out.println("Enter the number of days: ");
			String days = r.readLine () ;
			
			LibraryInterface lb = getRemoteObject ( educationalInstitute ) ;
			
			if ( lb == null ) {
				System.out.println( "Sorry! the demanded service can't be provided at the moment" );
				System.out.println("Please try again later" );
			}
			try {
				nonReturners[] result ;

				result = lb.getNonReturners(username,password,educationalInstitute, Integer.parseInt(days));
				for ( nonReturners data : result ) {
					System.out.println ( NonReturnersParser.nonReturnersToString(data) ) ;
				}	
			} catch ( Exception e ) {
				System.out.println("Your opeartion failed due to an error at the library server");
				System.out.println ("Please try again") ;
			}
		} catch ( IOException e ) {
			System.out.println("Your operation failed" + " " 
		+ e.getMessage());
		}
	}
	
	/**
	 * Provides an interface to the debugging tool to set duration for the student
	 * */
	public void setDuration () {
		try {
			InputStreamReader in = new InputStreamReader ( System.in ) ;
			BufferedReader r = new BufferedReader ( in ) ;
//			System.out.println("Enter the username: ");
//			String username = r.readLine () ;
//			System.out.println("Enter the pasword: ");
//			String password = r.readLine () ;
			System.out.println("Enter the username of student: " );
			String studentUsername = r.readLine() ;
			System.out.println("Enter the name of the book: ");
			String bookName = r.readLine() ;
//			System.out.println("Enter the name of the author: ");
//			String authorName = r.readLine();
			System.out.println("Enter the educational institute: ");
			String educationalInstitute = r.readLine () ;
			System.out.println("Enter the number of days: ");
			String daysString = r.readLine () ;
			int days = Integer.parseInt(daysString);
			
			LibraryInterface lb = getRemoteObject ( educationalInstitute ) ;
			
			if ( lb == null ) {
				System.out.println( "Sorry! the demanded service can't be provided at the moment" );
				System.out.println("Please try again later" );
			}
			try {
				boolean result = false ;

				result = lb.setDuration ( studentUsername, bookName, days ) ;
				if ( result ) {
					System.out.println("Your operation was successful");
				}
				else {
					System.out.println("Your operation failed");
				}
			} catch ( Exception e ) {
				System.out.println("Your opeartion failed due to an error at the library server");
				System.out.println ("Please try again") ;
			}
		} catch ( IOException e ) {
			System.out.println("Your operation failed" + " " 
		+ e.getMessage());
		}	
	}
	
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
