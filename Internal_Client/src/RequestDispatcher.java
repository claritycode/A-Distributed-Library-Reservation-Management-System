import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import DRMSServices.LibraryInterface;
import DRMSServices.LibraryInterfaceHelper;
import DRMSServices.nonReturners;

import java.util.Properties ;

public class RequestDispatcher {

	private ORB orb ;
	private NamingContext directory ;
	private String replicaName ;
	
	public RequestDispatcher ( String[] args, String newReplicaName ) {
		replicaName = newReplicaName ;
		try {
			// Get System Properties
			Properties p = System.getProperties() ;
			// Initialize ORB
			orb = ORB.init( args, p ) ;
			
			try {
				// Get a Reference to Name Service
				// It will be used to get the CORBA Object
				org.omg.CORBA.Object obj = orb.resolve_initial_references( "NameService" ) ;
				directory = NamingContextHelper.narrow( obj ) ;
			} catch ( org.omg.CORBA.ORBPackage.InvalidName e ) {
				System.out.println ( "Exception: " + e.getMessage() ) ;
			}
		} catch ( Exception e ) {
			System.out.println ( "Exception: " + e.getMessage() ) ;
		}
	}
	
	
	public BooleanResponse dispatch ( createAccountCall call ) {
		
		// Get the remote object
		LibraryInterface library = getRemoteObject ( call.getEducationalInstitute() ) ;
		
		// Call the method
		boolean result = library.createAccount(call.getFirstName(), call.getLastName(), call.getEmail(), call.getPhoneNumber(),
				call.getUsername(), call.getPassword(), call.getEducationalInstitute() ) ;
		
		// Return the result as a BooleanResponse object along with the name of the replica whose library performed the request
		return new BooleanResponse ( replicaName, result ) ;
	}
	
	public BooleanResponse dispatch ( reserveBookCall call ) {
		
		// Get the remote object
		LibraryInterface library = getRemoteObject ( call.getEducationalInstitute() ) ;
		
		// Call the method
		boolean result = library.reserveBook(call.getUsername(), call.getPassword(), call.getBookName(), call.getAuthorName()) ;
		
		// Return the result as a BooleanResponse object along with the name of the replica whose library performed the request
		return new BooleanResponse ( replicaName, result ) ;
	}
	
	public BooleanResponse dispatch ( reserveInterLibraryCall call ) {
		
		// Get the remote object
		LibraryInterface library = getRemoteObject ( call.getEducationalInstitute() ) ;
		
		// Call the method
		boolean result = library.reserveInterLibrary(call.getUsername(), call.getPassword(), call.getBookName(), call.getAuthorName());
		
		// Return the result as a BooleanResponse object along with the name of the replica whose library performed the request
		return new BooleanResponse ( replicaName, result ) ;
	}
	
	public BooleanResponse dispatch ( setDurationCall call ) {
		
		// Get the remote object
		LibraryInterface library = getRemoteObject ( call.getEducationalInstitute() ) ;
		
		// Call the method
		boolean result = library.setDuration(call.getUsername(), call.getPassword(), call.getDays());
		
		// Return the result as a BooleanResponse object along with the name of the replica whose library performed the request
		return new BooleanResponse ( replicaName, result ) ;
	}
	
	public GetNonReturnersResponse dispatch ( getNonReturnersCall call ) {
		
		// Get the remote object
		LibraryInterface library = getRemoteObject ( call.getEducationalInstitute() ) ;
			
		// Call the method
		nonReturners[] result = library.getNonReturners(call.getUsername(), call.getPassword(), call.getEducationalInstitute(),
				call.getDays()) ;
				
		// Return the result as a GetNonReturnersResponse object along with the name of the replica whose library 
		// performed the request
		return new GetNonReturnersResponse ( replicaName, result ) ;
		
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
