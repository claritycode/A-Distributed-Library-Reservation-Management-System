package rm;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import rm.constants.UdpEnum;
import rm.udp.RMUDPClient;

public class HeartBeatDispatcher implements Runnable {
	
	public static int SLEEP_TIME = 5000;
	
	private final ReplicaManager rm;
	private final String rmId;
	private final Set<String> libraryNames;
	private final Map<String, Integer> rmUDPPorts;
	
	public HeartBeatDispatcher(final ReplicaManager rm, final Set<String> libraryNames) {
		this.rm = rm;
		this.rmId = rm.getRmId();
		this.libraryNames = libraryNames;
		this.rmUDPPorts = rm.getRMUDPPorts();
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(SLEEP_TIME);

				// for each library in each RM that is not this.rm, send heart beat
				for (Entry<String, Integer> entry : rmUDPPorts.entrySet()) {
					if (!entry.getKey().equals(rmId)) {
						for (String libraryName : libraryNames) {
							dispatchHeartBeat(libraryName, entry.getValue(), entry.getKey());
						}
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Send heart beat to check if library in another replica manager is alive. The response will be handled by this.rm.
	 * @param libraryName
	 * @param port
	 * @param rmIdTarget
	 */
	public void dispatchHeartBeat(String libraryName, int port, String rmIdTarget) {
		String clientMsg = RMUDPClient.buildUdpMsg(rmId, UdpEnum.HEART_BEAT, libraryName);
		System.out.println(rmId + " sending [" + clientMsg + "] to [" + RMUDPClient.DEFAULT_HOST + ":" + port);
		String heartBeat = RMUDPClient.sendUdpRequest(RMUDPClient.DEFAULT_HOST, port, clientMsg);
		
		rm.handleHeartBeatResponse(libraryName, rmIdTarget, heartBeat);
	}

}
