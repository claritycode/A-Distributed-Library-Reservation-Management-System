package rm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;

import rm.constants.UdpEnum;
import rm.udp.RMUDPClient;
import rm.udp.RMUDPServer;
import DRMSServices.LibraryInterface;

public class ReplicaManagerImpl implements ReplicaManager {

	public static final String UDP_MSG_SPLIT = ":";

	private final String rmId;
	private final Map<String, LibraryInterface> libraries;
	private final ORB orb;
	private final POA rootpoa;
	private final Map<String, Integer> rmUDPPorts;
	private final HashSet<String> crashedNodes;

	public ReplicaManagerImpl(final String rmId, final List<String> libraryNames, final Map<String, Integer> rmUDPPorts,
			final ORB orb, final POA rootpoa) throws UserException {
		this.rmId = rmId;
		this.orb = orb;
		this.rootpoa = rootpoa;
		this.libraries = new HashMap<String, LibraryInterface>();
		this.startLibraries(libraryNames);
		this.rmUDPPorts = rmUDPPorts;
		this.crashedNodes = new HashSet<String>();

		// create and start the udp server thread and heart beat thread
		startUdpServer();
		startHeartBeatDispatcher();
	}

	private void startLibraries(final List<String> libraryNames) throws UserException {
		for (String libraryName : libraryNames) {
			LibraryInterface library = ReplicaFactory.createLibrary(getLibraryCorbaName(libraryName), libraryName, rootpoa, orb);
			this.libraries.put(libraryName, library);
		}
	}

	private void startUdpServer() {
		RMUDPServer rmUdpServer = new RMUDPServer(rmUDPPorts.get(rmId), this);
		Thread t = new Thread(rmUdpServer);
		t.start();
	}

	private void startHeartBeatDispatcher() {
		HeartBeatDispatcher dispatcher = new HeartBeatDispatcher(this, this.libraries.keySet());
		Thread t = new Thread(dispatcher);
		t.start();
	}

	private String getLibraryCorbaName(final String libraryName) {
		return getLibraryCorbaName(rmId, libraryName);
	}

	private String getLibraryCorbaName(final String rmId, final String libraryName) {
		return rmId + "_" + libraryName;
	}

	@Override
	public void handleFailure(String educationalInstitution, String rmId) {
		// If replica manager is not this one, just ignore it. That just here to comply with requirements: "If any one of the
		// replicas produces incorrect result, the FE informs all the RMs about that replica."
		// FIXME - add counter to replace only after 3rd error
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
	public void handleHeartBeatResponse(final String libraryName, final String rmIdTarget, final String response) {
		System.out.println("heartBeat for [" + libraryName + "] in [" + rmIdTarget + "] = " + response);
		boolean isAlive = Boolean.getBoolean(response);
		
		final String key = getLibraryCorbaName(rmIdTarget, libraryName);
		if (isAlive) {
			// if node is alive, remove from crashed nodes map
			if (crashedNodes.contains(key)) {
				crashedNodes.remove(key);
			}
		} else {
			// if it just found a node is dead, put it in crashed nodes map and notify other RMs
			if (!crashedNodes.contains(key)) {
				crashedNodes.add(key);
				// notify others
				dispatchCrashAgreement(libraryName, rmIdTarget);
			}
		}
	}
	
	private void dispatchCrashAgreement(final String libraryName, final String rmIdTarget) {
		boolean agreement = false;
		final String host = RMUDPClient.DEFAULT_HOST;
		for (Entry<String, Integer> entry : rmUDPPorts.entrySet()) {
			// just get agreement with 3rd RM
			if ((!entry.getKey().equals(rmId)) && (!entry.getKey().equals(rmIdTarget))) {
				String clientMsg = RMUDPClient.buildUdpMsg(rmId, UdpEnum.CRASH_AGREEMENT, libraryName, rmIdTarget);
				int serverPort = entry.getValue();
				System.out.println(rmId + " sending [" + clientMsg + "] to [" + host + ":" + serverPort);
				agreement = Boolean.getBoolean(RMUDPClient.sendUdpRequest(host, serverPort, clientMsg));
			}
		}
		// if RMs agree, notify rmIdTarget and clean crashedNodes
		if (agreement) {
			String clientMsg = RMUDPClient.buildUdpMsg(rmId, UdpEnum.REMOVE_CRASHED, libraryName);
			int serverPort = rmUDPPorts.get(rmIdTarget);
			System.out.println(rmId + " sending [" + clientMsg + "] to [" + host + ":" + serverPort);
			RMUDPClient.sendUdpRequest(host, serverPort, clientMsg);
			String key = getLibraryCorbaName(rmIdTarget, libraryName);
			crashedNodes.remove(key);
		}
	}

	@Override
	public boolean processCrashAgreement(String crashedLibraryName, String crashedRmId, String notifierRmId) {
		boolean agreed = false;
		String key = getLibraryCorbaName(crashedRmId, crashedLibraryName);
		if (crashedNodes.contains(key)) {
			agreed = true;
		}
		return agreed;
	}

	@Override
	public String processUdpClientMsg(final String clientMsg) {
		String message = null;

		if (clientMsg != null && clientMsg.length() > 0) {
			String[] msgArray = clientMsg.trim().split(UDP_MSG_SPLIT);

			if (msgArray != null && msgArray.length > 2) {
				String methodName = msgArray[0];
				String originalRmId = msgArray[1];
				String[] params = Arrays.copyOfRange(msgArray, 2, msgArray.length);
				if (methodName.equals(UdpEnum.FAILURE.name())) {
					// call from Front End to handle failure doesn't send back any message
					handleFailure(params[0], originalRmId);
				} else if (methodName.equals(UdpEnum.HEART_BEAT.name())) {
					message = Boolean.toString(processHeartBeat(params[0]));
				} else if (methodName.equals(UdpEnum.CRASH_AGREEMENT.name())) {
					message = Boolean.toString(processCrashAgreement(params[0], params[1], originalRmId));
				} else if (methodName.equals(UdpEnum.REMOVE_CRASHED.name())) {
					// call to remove crashed after agreement doesn't send back any message
					replaceLibrary(params[0]);
				}
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
