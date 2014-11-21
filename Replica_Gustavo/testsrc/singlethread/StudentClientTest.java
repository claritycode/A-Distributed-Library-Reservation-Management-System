package singlethread;

import idl.Library;

import java.util.Map;

import org.omg.CORBA.UserException;

import ui.UserUI;
import client.POALoader;
import client.StudentClient;
import entities.constants.PropertiesEnum;

public class StudentClientTest {
	
	private StudentClient loadClient() throws UserException {
		String institution = "concordia";
		Library poa = loadPoa(institution);
		return new StudentClient("test1", "password", institution, poa);
	}
	
	protected Library loadPoa(String institution) throws UserException {
		Map<String, String> properties = UserUI.loadProperties();
		String port = properties.get(PropertiesEnum.ORB_INITIAL_PORT.val());
		String host = properties.get(PropertiesEnum.ORB_INITIAL_HOST.val());
		
		return POALoader.load(port, host, institution);
	}
	
	private void assertMessage(String expected, String received, String method) {
		if (expected.equals(received)) {
			System.out.println(method + ": SUCCESS");
		} else {
			System.err.println(method + ": FAIL");
			System.out.println("\texpected: " + expected);
			System.out.println("\treceived: " + received);
		}
	}
	
	public void createUserSuccess(StudentClient sc) {
		String msg = sc.createAccount("test", "test", "test", "test");
		String expected = "Account created: Student [firstName=test, lastName=test, email=test, phone=test, username=test1, institution=concordia]";
		assertMessage(expected, msg, "createUserSuccess");
	}
	
	public void createUserDuplicate(StudentClient sc) {
		String msg = sc.createAccount("test", "test", "test", "test");
		String expected = "Unable to create account: username = [test1] already exists in this server.";
		assertMessage(expected, msg, "createUserDuplicate");
	}
	
	public void reserveBookSuccess(StudentClient sc) {
		String msg = sc.reserveBook("book1", "author1");
		String expected = "Book successfully reserved: Reservation [username=test1, book=Book [name=book1, author=author1, copies=0], library=concordia, duration=14]";
		assertMessage(expected, msg, "reserveBookSuccess");
	}
	
	public void reserveBookUnavailable(StudentClient sc) {
		String msg = sc.reserveBook("book1", "author1");
		String expected = "There are no copies of this book left: Book [name=book1, author=author1, copies=0]";
		assertMessage(expected, msg, "reserveBookUnavailable");
	}
	
	public void reserveInterLibrarySuccess(StudentClient sc) {
		String msg = sc.reserveInterLibrary("book5", "author5");
		String expected = "Book successfully reserved: Reservation [username=test1, book=Book [name=book5, author=author5, copies=0], library=webster, duration=14]";
		assertMessage(expected, msg, "reserveInterLibrarySuccess");
	}
	
	public void reserveInterLibraryFail(StudentClient sc) {
		String msg = sc.reserveInterLibrary("book5", "author5");
		String expected = "Unable to reserve inter-library";
		assertMessage(expected, msg, "reserveInterLibraryFail");
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(">>> Before starting this test, please start (or restart) 'server.StartServer' for libraries 'concordia' and 'webster' <<<");
		StudentClientTest test = new StudentClientTest();
		StudentClient sc = test.loadClient();
		test.createUserSuccess(sc);
		test.createUserDuplicate(sc);
		test.reserveBookSuccess(sc);
		test.reserveBookUnavailable(sc);
		test.reserveInterLibrarySuccess(sc);
		test.reserveInterLibraryFail(sc);
		System.out.println("Shut down library server when you are done.");
	}

}
