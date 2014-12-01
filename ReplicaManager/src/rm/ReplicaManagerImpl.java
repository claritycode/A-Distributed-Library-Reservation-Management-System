package rm;

import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;

import DRMSServices.LibraryInterface;

public class ReplicaManagerImpl implements ReplicaManager {

	private final String rmId;
	private final Map<String, LibraryInterface> replicas;
	private final Map<String, String> properties;
	private final ORB orb;
	private final POA rootpoa;

	public ReplicaManagerImpl(final String rmId, final Map<String, LibraryInterface> replicas, final Map<String, String> properties, 
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

	public void launchHeartBeats() {
		// FIXME - erase - add correct implementation instead
		System.out.println("step 2");
		for (Map.Entry<String, LibraryInterface> entry : replicas.entrySet()) {
			System.out.println(entry.getKey() + " ==> " + entry.getValue().setDuration("w1", "BonesOfTheLost", 1));
		}
	}

	@Override
	public boolean processHeartBeat(String educationalInstitution) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean processCrashAgreement(String crashedReplicaName, String crashedRmId, String notifierRmId) {
		// TODO Auto-generated method stub
		return false;
	}

}
