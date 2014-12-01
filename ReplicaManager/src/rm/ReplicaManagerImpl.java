package rm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;

import rm.constants.UdpEnum;
import rm.udp.RMUDPServer;
import DRMSServices.LibraryInterface;

public class ReplicaManagerImpl implements ReplicaManager {
	
	public static final String UDP_MSG_SPLIT = ":";

	private final String rmId;
	private final Map<String, LibraryInterface> libraries;
	private final ORB orb;
	private final POA rootpoa;
	private final Map<String, Integer> rmUDPPorts;
	private final RMUDPServer udpServer;
	private final HeartBeatDispatcher hearBeat;

	public ReplicaManagerImpl(final String rmId, final List<String> libraryNames, final Map<String, Integer> rmUDPPorts, final ORB orb, final POA rootpoa)
			throws UserException {
		this.rmId = rmId;
		this.orb = orb;
		this.rootpoa = rootpoa;
		this.libraries = new HashMap<String, LibraryInterface>();
		this.startLibraries(libraryNames);
		this.rmUDPPorts = rmUDPPorts;
		
		// create and start the udp server
		this.udpServer = startUdpServer();
		this.hearBeat = startHeartBeatDispatcher();
	}

	private void startLibraries(final List<String> libraryNames) throws UserException {
		for (String libraryName : libraryNames) {
			LibraryInterface library = ReplicaFactory.createLibrary(getLibraryCorbaName(libraryName), libraryName, rootpoa, orb);
			this.libraries.put(libraryName, library);
		}
	}
	
	private RMUDPServer startUdpServer() {
		RMUDPServer rmUdpServer = null;
		if (this.udpServer == null) {
			rmUdpServer = new RMUDPServer(rmUDPPorts.get(rmId), this);
			Thread t = new Thread(rmUdpServer);
			t.start();
		}
		return rmUdpServer;
	}
	
	private HeartBeatDispatcher startHeartBeatDispatcher() {
		HeartBeatDispatcher dispatcher = null;
		if (this.hearBeat == null) {
			dispatcher = new HeartBeatDispatcher(this, this.libraries.keySet());
			Thread t = new Thread(dispatcher);
			t.start();
		}
		return dispatcher;
	}
	
	private String getLibraryCorbaName(final String libraryName) {
		return rmId + "_" + libraryName;
	}

	@Override
	public void handleFailure(String educationalInstitution, String rmId) {
		// If replica manager is not this one, just ignore it. That just here to comply with requirements: "If any one of the
		// replicas produces incorrect result, the FE informs all the RMs about that replica."
		if (rmId != null && rmId.equals(this.rmId)) {
			replaceLibrary(educationalInstitution);
		}
	}

	private void replaceLibrary(final String educationalInstitution) {
		try {
			String libraryCorbaName = getLibraryCorbaName(educationalInstitution);
			LibraryInterface library = libraries.get(educationalInstitution);
			library.shutDown();
			ReplicaFactory.removeLibrary(libraryCorbaName, orb);
			ReplicaFactory.createLibrary(libraryCorbaName, educationalInstitution, rootpoa, orb);
		} catch (UserException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean processHeartBeat(String educationalInstitution) {
		boolean isUp = false;
		try {
			String libraryCorbaName = getLibraryCorbaName(educationalInstitution);
			isUp = ReplicaFactory.corbaObjectIsUp(libraryCorbaName, orb);
		} catch (UserException e) {
			e.printStackTrace();
		}
		return isUp;
	}

	@Override
	public boolean processCrashAgreement(String crashedReplicaName, String crashedRmId, String notifierRmId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String processUdpClientMsg(final String clientMsg) {
		String message = null;

		if (clientMsg != null && clientMsg.length() > 0) {
			String[] msgArray = clientMsg.trim().split(UDP_MSG_SPLIT);

			if (msgArray != null && msgArray.length > 2) {
				String methodName = msgArray[0];
				String rmId = msgArray[1];
				String[] params = Arrays.copyOfRange(msgArray, 2, msgArray.length);

				if (methodName.equals(UdpEnum.HEART_BEAT.name())) {
					message = Boolean.toString(processHeartBeat(params[0]));
				} //else if (methodName.equals(UdpEnum.GET_NON_RETURNERS.name())) {
					// FIXME - resolve method
				//}
			}
		}

		return message;
	}
	
	@Override
	public String getRmId() {
		return this.rmId;
	}
	
	@Override
	public Map<String, Integer> getRMUDPPorts() {
		return this.rmUDPPorts;
	}

}
