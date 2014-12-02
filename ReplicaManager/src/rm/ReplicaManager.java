package rm;

import java.util.Map;

public interface ReplicaManager {
	
	String getRmId();
	Map<String, Integer> getRMUDPPorts();

	/**
	 * Process failure notification from the Front End. If the same replica produces incorrect results for three 
	 * consecutive client requests, then the replica is replaced.
	 * 
	 * @param replicaName
	 * @param rmId
	 */
	void handleFailure(String replicaName, String rmId);
	
	/**
	 * Process heartbeat from other ReplicaManager to check if a specific replica is alive.
	 * 
	 * @param replicaName
	 * @return true if replica is alive
	 */
	boolean processHeartBeat(String replicaName);
	
	/**
	 * Handle response from Heart Beat to another Replica Manager.
	 * 
	 * @param libraryName
	 * @param rmIdTarget
	 * @param response
	 */
	void handleHeartBeatResponse(String libraryName, String rmIdTarget, final String response);
	
	/**
	 * Process crash agreement request issued by another ReplicaManager.
	 * 
	 * @param crashedReplicaName
	 * @param crashedRmId
	 * @param notifierRmId
	 * @return true if this ReplicaManager agrees the replica has crashed.
	 */
	boolean processCrashAgreement(String crashedReplicaName, String crashedRmId, String notifierRmId);
	
	/**
	 * Process an incoming udp message from another rm.
	 * @param clientMsg
	 * @return
	 */
	String processUdpClientMsg(final String clientMsg);
}
