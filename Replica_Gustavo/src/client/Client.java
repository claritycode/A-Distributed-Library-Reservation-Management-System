package client;

import DRMSServices.LibraryInterface;
import entities.User;

public abstract class Client<T extends User> {
	protected final T user;
	protected final LibraryInterface poa;
	
	public Client(final String username, final String password, final String institution, final LibraryInterface poa) {
		if (poa != null) {
			this.user = createUser(username, password, institution);
			this.poa = poa;
		} else {
			throw new IllegalArgumentException("server should be valid");
		}
	}
	
	public abstract T createUser(final String username, final String password, final String institution);
	
	public String processResponse(final boolean response, final String call) {
		String result = null;
		if (response) {
			result = "SUCCESS: " + call;
		} else {
			result = "FAIL: " + call;
		}
		return result;
	}
}
