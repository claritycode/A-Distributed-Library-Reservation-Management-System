package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import DRMSServices.LibraryInterface;
import DRMSServices.LibraryInterfaceHelper;
import entities.constants.OrbEnum;
import entities.constants.PropertiesEnum;

public class StartServer {

	public static void main(String[] args) {
		try {
			System.out.println("Current args: " + Arrays.toString(args));
			
			System.out.println("Instructions:");
			System.out.println("1) from unix command line run: 'orbd -ORBInitialPort 1050&'");
			System.out.println("2) run this class with library name as argument. e.g.: concordia");
			
			if (args != null && args.length > 0) {
				String libraryName = args[0];
				Map<String, String> properties = loadProperties();
			
				// create and initialize the ORB
				ORB orb = ORB.init(buildOrgArgs(properties), null);
				
				// get reference to rootpoa & activate the POAManager
				POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
				rootpoa.the_POAManager().activate();
				
				// create servant and register it with the ORB
				LibraryPOAImpl impl = new LibraryPOAImpl(libraryName);
				
				// get object reference from the servant
				org.omg.CORBA.Object ref = rootpoa.servant_to_reference(impl);
				LibraryInterface lref = LibraryInterfaceHelper.narrow(ref);
				
				// get the root naming context
				// NameService invokes the name service
				org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
				// Use NamingContextExt which is part of the Interoperable Naming Service (INS) specification.
				NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
				
				// bind the Object Reference in Naming
				NameComponent[] path = ncRef.to_name(libraryName);
				ncRef.rebind(path, lref);
				
				System.out.println("Library POA is ready");
				orb.run();
			} else {
				System.err.println("Please provide args as defined in the instructions.");
			}
		} catch (Exception e) {
			System.err.println("ERROR: " + e);
			System.out.println("If system gives you error of 'NamingContextPackage.NotFound', try deleting all 'orb.db' folders, and restarting the orbd'.");
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
	
	private static String[] buildOrgArgs(Map<String, String> properties) {
		String port = properties.get(PropertiesEnum.ORB_INITIAL_PORT.val());
		String host = properties.get(PropertiesEnum.ORB_INITIAL_HOST.val());
		return new String[] {OrbEnum.ORB_INITIAL_PORT_ARG.val(), port, OrbEnum.ORB_INITIAL_HOST_ARG.val(), host};
	}

}
