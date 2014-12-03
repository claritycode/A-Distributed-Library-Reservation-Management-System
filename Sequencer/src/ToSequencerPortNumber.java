

import java.io.Serializable;

public class ToSequencerPortNumber implements Serializable
{
	String replicaModuleName;
	int portNumber;
	public ToSequencerPortNumber(String replicaModuleName,int portNumber )
	{
		this.replicaModuleName=replicaModuleName;
		this.portNumber=portNumber;
	}
	public String getReplicaModuleName()
	{
		return replicaModuleName;
		
	}
	public int getPortNumber()
	{
		return portNumber;
	}

}
