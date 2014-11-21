package client;

import org.apache.log4j.Logger;

import idl.Library;
import entities.Student;

public class StudentClient extends Client<Student> {
	private final Logger LOGGER;

	public StudentClient(final String username, final String password, final String institution, final Library poa) {
		super(username, password, institution, poa);
		System.setProperty("obj.log","./student_" + username + "_" + institution +".log");
		LOGGER = Logger.getLogger(StudentClient.class);
	}

	@Override
	public Student createUser(String username, String password, String institution) {
		return new Student(username, password, institution);
	}

	public String createAccount(final String firstName, final String lastName, final String emailAddress, final String phoneNumber) {
		String message = poa.createAccount(firstName, lastName, emailAddress, phoneNumber, user.getUsername(), user.getPassword(),
				user.getInstitution());
		LOGGER.info(message);
		return message;
	}

	public String reserveBook(String bookName, String authorName) {
		String message = poa.reserveBook(user.getUsername(), user.getPassword(), bookName, authorName);
		LOGGER.info(message);
		return message;
	}
	
	public String reserveInterLibrary(String bookName, String authorName) {
		String message = poa.reserveInterLibrary(user.getUsername(), user.getPassword(), bookName, authorName);
		LOGGER.info(message);
		return message;
	}
}
