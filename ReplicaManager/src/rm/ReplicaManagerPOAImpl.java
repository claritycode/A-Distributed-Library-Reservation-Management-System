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
			// FIXME - add correct port - need to create one every time
			this.properties.put(educationalInstitution + ".udp.port", "9999");
			
			ReplicaFactory.removeReplica(educationalInstitution, orb);
			ReplicaFactory.createReplica(educationalInstitution, properties, rootpoa, orb);
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

}
