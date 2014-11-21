package client;

import idl.Library;
import idl.LibraryHelper;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import entities.constants.OrbEnum;

public class POALoader {
	public static Library load(final String orbPort, final String orbHost, final String institution) throws UserException {
		// create and initialize the ORB
		String[] args = new String[] { OrbEnum.ORB_INITIAL_PORT_ARG.val(), orbPort, OrbEnum.ORB_INITIAL_HOST_ARG.val(), orbHost};
		ORB orb = ORB.init(args, null);
		
		// get the root naming context
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		// Use NamingContextExt instead of NamingContext. This is
		// part of the Interoperable naming Service.
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		
		// resolve the Object Reference in Naming
		return LibraryHelper.narrow(ncRef.resolve_str(institution));
	}
}
