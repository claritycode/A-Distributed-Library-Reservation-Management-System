package rm;

import idl.Library;
import idl.LibraryHelper;
import idl.ReplicaManager;
import idl.ReplicaManagerHelper;

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

public class ReplicaManagerStarter {
	
	public static final String CONFIG_PATH = "./resources/config.properties";
	public static final String ROOT_POA = "RootPOA";

	public static void main(String[] args) {
		try {
			printInstructions(args);

			if (args != null && args.length == 4) {
				String rmId = args[0];
				Map<String, String> properties = loadProperties();

				ORB orb = getORB(properties);
				POA rootpoa = getRootPOA(orb);

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

	private static Map<String, String> loadProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(CONFIG_PATH);
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

	/**
	 * Create and initialize the ORB
	 * @param properties
	 * @return
	 */
	private static ORB getORB(Map<String, String> properties) {
		String port = properties.get(PropertiesEnum.ORB_INITIAL_PORT.val());
		String host = properties.get(PropertiesEnum.ORB_INITIAL_HOST.val());
		String[] orbArgs = new String[] { OrbEnum.ORB_INITIAL_PORT_ARG.val(), port, OrbEnum.ORB_INITIAL_HOST_ARG.val(), host };
		return ORB.init(orbArgs, null);
	}
	
	/**
	 * get reference to rootpoa & activate the POAManager
	 * @param orb
	 * @return
	 * @throws UserException
	 */
	private static POA getRootPOA(final ORB orb) throws UserException {
		POA rootpoa = POAHelper.narrow(orb.resolve_initial_references(ROOT_POA));
		rootpoa.the_POAManager().activate();
		return rootpoa;
	}

	public static NamingContextExt getNamingContextExt(final ORB orb) throws InvalidName {
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

}
