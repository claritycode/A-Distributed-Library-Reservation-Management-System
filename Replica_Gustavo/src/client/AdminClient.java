package client;

import idl.Library;

import org.apache.log4j.Logger;

import entities.Administrator;

public class AdminClient extends Client<Administrator> {
	private final Logger LOGGER;

	public AdminClient(final String username, final String password, final String institution, final Library server) {
		super(username, password, institution, server);
		System.setProperty("obj.log","./admin_" + username + "_" + institution +".log");
		LOGGER = Logger.getLogger(AdminClient.class);
	}

	@Override
	public Administrator createUser(String username, String password, String institution) {
		return new Administrator(username, password, institution);
	}

	public String getNonRetuners(final int numDays) {
		String message = poa.getNonRetuners(user.getUsername(), user.getPassword(), user.getInstitution(), numDays);
		LOGGER.info(message);
		return message;
	}
	
	public String setDuration(String username, String bookName, int num_of_days) {
		String message =  poa.setDuration(username, bookName, num_of_days);
		LOGGER.info(message);
		return message;
	}

}
