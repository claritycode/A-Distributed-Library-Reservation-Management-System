package rm;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import rm.constants.UdpEnum;
import rm.udp.RMUDPClient;

public class HeartBeatDispatcher implements Runnable {
	
	public static int SLEEP_TIME = 5000;
	
	private final String rmId;
	private final Set<String> libraryNames;
	private final Map<String, Integer> rmUDPPorts;
	
	public HeartBeatDispatcher(final ReplicaManager rm, final Set<String> libraryNames) {
		this.rmId = rm.getRmId();
		this.libraryNames = libraryNames;
		this.rmUDPPorts = rm.getRMUDPPorts();
	}		

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(SLEEP_TIME);

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

	public void dispatchHeartBeats() {
	}
	
	private String dispatchHeartBeat(String libraryName, int port, String rmIdTarget) {
		String clientMsg = RMUDPClient.buildUdpMsg(rmId, UdpEnum.HEART_BEAT, libraryName);
		String heartBeat = RMUDPClient.sendUdpRequest("localhost", port, clientMsg);
		// FIXME - process heartBeat
		System.out.println("heartBeat for [" + libraryName + "] in [" + rmIdTarget + "]  = " + heartBeat);
		
		return heartBeat;
	}

}
