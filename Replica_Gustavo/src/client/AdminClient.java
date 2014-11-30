package client;

import org.apache.log4j.Logger;

import DRMSServices.LibraryInterface;
import DRMSServices.lateStudent;
import DRMSServices.nonReturners;
import entities.Administrator;

public class AdminClient extends Client<Administrator> {
	private final Logger LOGGER;

	public AdminClient(final String username, final String password, final String institution, final LibraryInterface server) {
		super(username, password, institution, server);
		System.setProperty("obj.log","./admin_" + username + "_" + institution +".log");
		LOGGER = Logger.getLogger(AdminClient.class);
	}

	@Override
	public Administrator createUser(String username, String password, String institution) {
		return new Administrator(username, password, institution);
	}

	public String getNonRetuners(final int numDays) {
		nonReturners[] response = poa.getNonReturners(user.getUsername(), user.getPassword(), user.getInstitution(), numDays);
		String message = "getNonRetuners[\n" + nonReturnersToMessage(response) + "]";
		LOGGER.info(message);
		return message;
	}
	
	public String setDuration(String username, String bookName, int num_of_days) {
		boolean response = poa.setDuration(username, bookName, num_of_days);
		String message = processResponse(response, "setDuration");
		LOGGER.info(message);
		return message;
	}
	
	private String nonReturnersToMessage(final nonReturners[] response) {
		String result = null;
		for (nonReturners nr : response) {
			result += nr.universityName;
			for (lateStudent ls : nr.studentList) {
				result += "\n" + ls.firstName + " " + ls.lastName + " " + ls.phoneNumber;
			}
			result += "\n......";
		}
		return result;
	}

}
