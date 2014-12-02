package Response;

import java.io.Serializable ;
public class BooleanResponse implements Serializable  {
	
	private String replicaName ;
	private boolean result ;
	
	public BooleanResponse ( String newReplicaName, boolean newResult ) {
		replicaName = newReplicaName ;
		result = newResult ;
	}
	
	/**
	 * @return the replicaName
	 */
	public String getReplicaName() {
		return replicaName;
	}
	
	/**
	 * @return the result
	 */
	public boolean getResult() {
		return result;
	}
}
