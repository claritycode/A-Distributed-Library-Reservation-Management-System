package client;

import idl.Library;
import entities.User;

public abstract class Client<T extends User> {
	protected final T user;
	protected final Library poa;
	
	public Client(final String username, final String password, final String institution, final Library poa) {
		if (poa != null) {
			this.user = createUser(username, password, institution);
			this.poa = poa;
		} else {
			throw new IllegalArgumentException("server should be valid");
		}
	}
	
	public abstract T createUser(final String username, final String password, final String institution);
}
