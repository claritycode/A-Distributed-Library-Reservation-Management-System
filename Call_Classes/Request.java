import java.io.Serializable ;

/**
* @author Parth Patel
* A wrapper class for <code>ClientCall</code>. Helps in deserialization
*/
public class Request implements Serializable{

	private ClientCall call ;

	public Request ( ClientCall newCall ) {
		call = newCall ;
	}

	/**
	*@retrun the ClientCall object
	*/
	public ClientCall getCall () {
		return call ;
	}
}