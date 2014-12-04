package rm;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;

import server.LibraryPOAImpl;
import DRMSServices.LibraryInterface;
import DRMSServices.LibraryInterfaceHelper;

public class ReplicaFactory {

	public static void removeLibrary(final String libraryCorbaName, final ORB orb) throws UserException {
		NamingContextExt ncRef = ReplicaManagerStarter.getNamingContextExt(orb);
		NameComponent[] path = ncRef.to_name(libraryCorbaName);
		ncRef.unbind(path);
	}

	public static LibraryInterface createLibrary(final String libraryCorbaName, final String libraryName, final POA rootpoa, final ORB orb)
			throws UserException {
		System.out.println("createLibrary: binding library [" + libraryName + "] to corba with naming [" + libraryCorbaName + "]");
		
		// create servant and register it with the ORB
		LibraryPOAImpl impl = new LibraryPOAImpl(libraryName);
		// FIXME - use any of the 3 implementations
		// Gustavo - LibraryPOAImpl
		// Harpreet - Webserver.DRMSMcgill, DRMSServer, or DRMSSherbrooke (vanier)
		// Parth - Library

		// get object reference from the servant
		org.omg.CORBA.Object ref = rootpoa.servant_to_reference(impl);
		LibraryInterface lref = LibraryInterfaceHelper.narrow(ref);

		NamingContextExt ncRef = ReplicaManagerStarter.getNamingContextExt(orb);

		// bind the Object Reference in Naming
		NameComponent[] path = ncRef.to_name(libraryCorbaName);
		ncRef.rebind(path, lref);
		
		return lref;
	}
	
	public static boolean corbaObjectIsUp(final String libraryCorbaName, final ORB orb) throws UserException {
		boolean found = true;
		NamingContextExt ncRef = ReplicaManagerStarter.getNamingContextExt(orb);
		// resolve the Object Reference in Naming
		try {
			ncRef.resolve_str(libraryCorbaName);
		} catch (NotFound | InvalidName e) {
			// if it throws exception, the object was not found
			found = false;
		}
		return found;
	}
}
