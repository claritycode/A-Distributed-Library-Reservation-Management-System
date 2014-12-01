package rm;

import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.PortableServer.POA;

import server.LibraryPOAImpl;
import DRMSServices.LibraryInterface;
import DRMSServices.LibraryInterfaceHelper;

public class ReplicaFactory {

	public static void removeReplica(final String replicaName, final ORB orb) throws UserException {
		NamingContextExt ncRef = ReplicaManagerStarter.getNamingContextExt(orb);
		NameComponent[] path = ncRef.to_name(replicaName);
		ncRef.unbind(path);
	}

	public static LibraryInterface createReplica(final String replicaName, Map<String, String> properties, final POA rootpoa, final ORB orb)
			throws UserException {
		// create servant and register it with the ORB
		LibraryPOAImpl impl = new LibraryPOAImpl(replicaName, properties);

		// get object reference from the servant
		org.omg.CORBA.Object ref = rootpoa.servant_to_reference(impl);
		LibraryInterface lref = LibraryInterfaceHelper.narrow(ref);

		NamingContextExt ncRef = ReplicaManagerStarter.getNamingContextExt(orb);

		// bind the Object Reference in Naming
		NameComponent[] path = ncRef.to_name(replicaName);
		ncRef.rebind(path, lref);
		
		return lref;
	}
}
