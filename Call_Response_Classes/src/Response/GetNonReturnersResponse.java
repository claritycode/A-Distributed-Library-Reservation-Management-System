package Response;

import java.io.Serializable ;
public class GetNonReturnersResponse implements Serializable {

		private String replicaName ;
		private DRMSServices.nonReturners[] result ;
		
		public GetNonReturnersResponse ( String newReplicaName, DRMSServices.nonReturners[] newResult ) {
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
		public DRMSServices.nonReturners[] getResult() {
			return result;
		}
		
}
