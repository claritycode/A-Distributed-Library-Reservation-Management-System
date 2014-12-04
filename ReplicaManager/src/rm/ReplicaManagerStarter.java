package rm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import rm.constants.OrbEnum;
import rm.constants.PropertiesEnum;

public class ReplicaManagerStarter {

	public static final String CONFIG_PATH = "./resources/rmconfig.properties";
	public static final String ROOT_POA = "RootPOA";

	public static void main(String[] args) {
		try {
			printInstructions(args);

			if (args != null && args.length == 4) {
				String rmId = args[0];
				Map<String, String> properties = loadProperties();

				ORB orb = getORB(properties);
				POA rootpoa = getRootPOA(orb);

				final List<String> libraryNames = Arrays.asList(new String[] { args[1], args[2], args[3] });
				final Map<String, Integer> rmUDPPorts = getRMUDPPorts(properties);

				final ReplicaManagerImpl impl = new ReplicaManagerImpl(rmId, libraryNames, rmUDPPorts, orb, rootpoa);

				// FIXME - remove test class (TempClass)
				Thread t = new Thread(new ReplicaManagerStarter().new TempClass(impl));
				t.start();

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

	// FIXME - erase TempClass
	class TempClass implements Runnable {
		ReplicaManagerImpl impl;

		TempClass(ReplicaManagerImpl impl) {
			this.impl = impl;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
				System.out.println("handleFailure 1");
				impl.handleFailure("concordia", this.impl.getRmId());
				Thread.sleep(100);
				System.out.println("handleFailure 2");
				impl.handleFailure("concordia", this.impl.getRmId());
				Thread.sleep(100);
				System.out.println("handleFailure 3");
				impl.handleFailure("concordia", this.impl.getRmId());
				Thread.sleep(100);
				System.out.println("handleFailure 4");
				impl.handleFailure("concordia", this.impl.getRmId());
				Thread.sleep(100);
				System.out.println("handleFailure 5");
				impl.handleFailure("concordia", this.impl.getRmId());
				Thread.sleep(100);
				System.out.println("handleFailure 6");
				impl.handleFailure("concordia", this.impl.getRmId());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

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

	/**
	 * Create and initialize the ORB
	 * 
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
	 * 
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

	private static Map<String, Integer> getRMUDPPorts(Map<String, String> properties) {
		final Map<String, Integer> rmUDPPorts = new HashMap<String, Integer>();
		String[] rmIds = properties.get(PropertiesEnum.RM_IDS.val()).split(",");
		String[] rmUdpPorts = properties.get(PropertiesEnum.RM_UDP_PORTS.val()).split(",");
		if (rmIds.length == rmUdpPorts.length) {
			for (int i = 0; i < rmIds.length; i++) {
				rmUDPPorts.put(rmIds[i], Integer.valueOf(rmUdpPorts[i]));
			}
		} else {
			throw new IllegalArgumentException("Size of '" + PropertiesEnum.RM_IDS.val() + "' and '"
					+ PropertiesEnum.RM_UDP_PORTS.val() + "' properties does not match");
		}
		return rmUDPPorts;
	}

}
