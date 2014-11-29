package rm;

import idl.Library;
import idl.LibraryHelper;
import idl.ReplicaManager;
import idl.ReplicaManagerHelper;
import idl.ReplicaManagerPOA;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import rm.constants.OrbEnum;
import rm.constants.PropertiesEnum;
import server.LibraryPOAImpl;

public class ReplicaManagerPOAImpl extends ReplicaManagerPOA {

	private final String rmId;
	private final Map<String, Library> replicas;
	private final Map<String, String> properties;
	private final ORB orb;
	private final POA rootpoa;

	public ReplicaManagerPOAImpl(final String rmId, final Map<String, Library> replicas, final Map<String, String> properties, 
			final ORB orb, final POA rootpoa) {
		this.rmId = rmId;
		this.replicas = replicas;
		this.properties = properties;
		this.orb = orb;
		this.rootpoa = rootpoa;
	}

	@Override
	public void handleFailure(String educationalInstitution, String rmId) {
		System.out.println("step 4"); // TODO - remove sysout
		
		// If replica manager is not this one, just ignore it. That just here to comply with requirements: "If any one of the
		// replicas produces incorrect result, the FE informs all the RMs about that replica."
		if (rmId != null && rmId.equals(this.rmId)) {
			replaceReplica(educationalInstitution);
		}
	}

	private void replaceReplica(final String educationalInstitution) {
		try {
			// dummy
			this.properties.put(educationalInstitution + ".udp.port", "9999");
			
			
			removeReplica(educationalInstitution, orb);
			createReplica(educationalInstitution, properties, rootpoa, orb);
		} catch (UserException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendHeartBeats() {
		// FIXME - erase - add correct implementation instead
		System.out.println("step 2");
		for (Map.Entry<String, Library> entry : replicas.entrySet()) {
			System.out.println(entry.getKey() + " ==> " + entry.getValue().setDuration("w1", "BonesOfTheLost", 1));
		}
	}

	@Override
	public String processHeartBeat(String educationalInstitution) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getCrashAgreement(int crashedRMId, int notifierRMId) {
		// TODO Auto-generated method stub
		return false;
	}

	private static Map<String, String> loadProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream("./resources/config.properties");
			prop.load(input);

			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				properties.put(key, value);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return properties;
	}
	
	private static void removeReplica(final String replicaName, final ORB orb) throws UserException {
		NamingContextExt ncRef = getNamingContextExt(orb);
		NameComponent[] path = ncRef.to_name(replicaName);
		ncRef.unbind(path);
	}

	private static Library createReplica(final String replicaName, Map<String, String> properties, final POA rootpoa, final ORB orb)
			throws UserException {
		// create servant and register it with the ORB
		LibraryPOAImpl impl = new LibraryPOAImpl(replicaName, properties);

		// get object reference from the servant
		org.omg.CORBA.Object ref = rootpoa.servant_to_reference(impl);
		Library lref = LibraryHelper.narrow(ref);

		NamingContextExt ncRef = getNamingContextExt(orb);

		// bind the Object Reference in Naming
		NameComponent[] path = ncRef.to_name(replicaName);
		ncRef.rebind(path, lref);
		
		return lref;
	}

	private static void printInstructions(final String[] args) {
		System.out.println("Current args: " + Arrays.toString(args));

		System.out.println("Instructions:");
		System.out.println("1) from unix command line run: 'orbd -ORBInitialPort 1050&'");
		System.out.println("2) start all 3 replicas of this Replica Manager");
		System.out.println("3) run this class with the replica with 4 arguments: id as first argument and all libraries "
				+ "names as other 3 arguments. e.g.: rm1 concordia1 vanier2 webster1");
	}

	private static String[] buildOrgArgs(Map<String, String> properties) {
		String port = properties.get(PropertiesEnum.ORB_INITIAL_PORT.val());
		String host = properties.get(PropertiesEnum.ORB_INITIAL_HOST.val());
		return new String[] { OrbEnum.ORB_INITIAL_PORT_ARG.val(), port, OrbEnum.ORB_INITIAL_HOST_ARG.val(), host };
	}

	private static ORB getORB(Map<String, String> properties) {
		String port = properties.get(PropertiesEnum.ORB_INITIAL_PORT.val());
		String host = properties.get(PropertiesEnum.ORB_INITIAL_HOST.val());
		String[] orbArgs = new String[] { OrbEnum.ORB_INITIAL_PORT_ARG.val(), port, OrbEnum.ORB_INITIAL_HOST_ARG.val(), host };
		return ORB.init(orbArgs, null);
	}

	private static NamingContextExt getNamingContextExt(final ORB orb) throws InvalidName {
		// get the root naming context
		// NameService invokes the name service
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		// Use NamingContextExt which is part of the Interoperable Naming Service (INS) specification.
		return NamingContextExtHelper.narrow(objRef);
	}

	private static Map<String, Library> loadReplicas(final ORB orb, String... replicaNames) throws UserException {
		// FIXME - create replicas on RM start, instead of loading externally
		Map<String, Library> replicas = new HashMap<String, Library>();
		for (String name : replicaNames) {
			replicas.put(name, loadLibrary(orb, name));
		}
		return replicas;
	}

	private static Library loadLibrary(final ORB orb, final String institution) throws UserException {
		NamingContextExt ncRef = getNamingContextExt(orb);

		// resolve the Object Reference in Naming
		return LibraryHelper.narrow(ncRef.resolve_str(institution));
	}

	public static void main(String[] args) {
		try {
			printInstructions(args);

			if (args != null && args.length == 4) {
				String rmId = args[0];
				Map<String, String> properties = loadProperties();

				// create and initialize the ORB
				ORB orb = getORB(properties);

				// get reference to rootpoa & activate the POAManager
				POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
				rootpoa.the_POAManager().activate();

				final Map<String, Library> replicas = loadReplicas(orb, args[1], args[2], args[3]);

				// create servant and register it with the ORB
				final ReplicaManagerPOAImpl impl = new ReplicaManagerPOAImpl(rmId, replicas, properties, orb, rootpoa);

				// get object reference from the servant
				org.omg.CORBA.Object ref = rootpoa.servant_to_reference(impl);
				ReplicaManager rmRef = ReplicaManagerHelper.narrow(ref);

				NamingContextExt ncRef = getNamingContextExt(orb);

				// bind the Object Reference in Naming
				NameComponent[] path = ncRef.to_name(rmId);
				ncRef.rebind(path, rmRef);

				// FIXME - erase
				System.out.println("step 1");
				impl.sendHeartBeats();

				System.out.println("step 3");
				impl.handleFailure(args[1], rmId);
				// end of erase

				System.out.println("Replica Manager POA is ready: " + rmId);
				orb.run();
			} else {
				System.err.println("Please provide args as defined in the instructions.");
			}
		} catch (Exception e) {
			System.err.println("ERROR: " + e);
			System.out.println("\t>> If system gives you error of 'NamingContextPackage.NotFound', try deleting all 'orb.db' "
					+ "folders, and restarting the orbd'.");
			e.printStackTrace();
		} finally {
			System.out.println("Exiting Library Server ");
		}
	}

}
