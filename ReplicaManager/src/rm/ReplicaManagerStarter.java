package rm;

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
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import rm.constants.OrbEnum;
import rm.constants.PropertiesEnum;

public class ReplicaManagerStarter {

	public static final String CONFIG_PATH = "./resources/rmconfig.properties";
	public static final String ROOT_POA = "RootPOA";
	public static final String ERROR_NONE = "none";
	public static final String ERROR_BYZANTINE = "byzantine";
	public static final String ERROR_CRASH = "crash";

	public static void main(String[] args) {
		try {
			printInstructions(args);

			if (args != null && args.length == 2) {
				String rmId = args[0];
				String errorType = args[1];

				if ((errorType.equals(ERROR_NONE)) || (errorType.equals(ERROR_BYZANTINE)) || (errorType.equals(ERROR_CRASH))) {
					Map<String, String> properties = loadProperties();

					ORB orb = getORB(properties);
					POA rootpoa = getRootPOA(orb);

					final String[] libraryNames = properties.get("libraries").split(",");
					final String[] libraryPorts = properties.get(rmId + PropertiesEnum.RM_ANY_UDP_PORT.val()).split(",");
					final Map<String, Integer> rmUDPPorts = getRMUDPPorts(properties);

					final ReplicaManagerImpl impl = new ReplicaManagerImpl(rmId, libraryNames, libraryPorts, rmUDPPorts, orb,
							rootpoa);
					if (errorType.equals(ERROR_BYZANTINE)) {
						impl.setByzantineError(libraryNames[0]);
					} else if (errorType.equals(ERROR_CRASH)) {
						Thread t = new Thread(new ReplicaManagerStarter().new SimulateCrash(impl, libraryNames[0]));
						t.start();
					}
					orb.run();
				} else {
					throw new IllegalArgumentException("Error type should be one of (" + ERROR_NONE + "|" + ERROR_BYZANTINE + "|"
							+ ERROR_CRASH + ")");
				}
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

	class SimulateCrash implements Runnable {
		final ReplicaManagerImpl impl;
		final String libraryName;

		SimulateCrash(final ReplicaManagerImpl impl, final String libraryName) {
			this.impl = impl;
			this.libraryName = libraryName;
		}

		@Override
		public void run() {
			try {
				System.out.println("Simulate Crash: library [" + libraryName + "] will be killed in 10 seconds.");
				Thread.sleep(10000);
				System.out.println("\n\n!!!!!!!!!!\n!!! killing [" + libraryName + "] !!!\n!!!!!!!!!!\n\n");
				impl.killLibrary(libraryName);
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
		System.out.println("2) run this class with the replica manager id and error type  (" + ERROR_NONE + "|" + ERROR_BYZANTINE
				+ "|" + ERROR_CRASH + ") as " + "arguments. e.g.: rm1 none");
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
