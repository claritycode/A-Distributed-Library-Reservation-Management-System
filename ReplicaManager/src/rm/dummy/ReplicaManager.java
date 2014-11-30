package rm.dummy;

public interface ReplicaManager {

	/**
	 * Process failure notification from the Front End. If the same replica produces incorrect results for three 
	 * consecutive client requests, then the replica is replaced.
	 * 
	 * @param replicaName
	 * @param rmId
	 */
	void handleFailure(String replicaName, String rmId);
	
	/**
	 * Launch heartbeats to other replica managers to check if their replicas are alive.
	 */
	void launchHeartBeats();
	
	/**
	 * Process heartbeat from other ReplicaManager to check if a specific replica is alive.
	 * 
	 * @param replicaName
	 * @return true if replica is alive
	 */
	boolean processHeartBeat(String replicaName);
	
	/**
	 * Process crash agreement request issued by another ReplicaManager.
	 * 
	 * @param crashedReplicaName
	 * @param crashedRmId
	 * @param notifierRmId
	 * @return true if this ReplicaManager agrees the replica has crashed.
	 */
	boolean processCrashAgreement(String crashedReplicaName, String crashedRmId, String notifierRmId);
}
