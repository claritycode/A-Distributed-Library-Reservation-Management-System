package ui;

import idl.Library;

import org.omg.CORBA.UserException;

import client.StudentClient;
import entities.Student;

public class StudentUI extends UserUI<Student, StudentClient> {

	public static void main(String[] args) {
		StudentUI ui = new StudentUI();
		ui.init(4);
	}

	@Override
	public StudentClient createClient() throws UserException {
		String institution = getLibraryName();
		Library server = loadServer(institution);
		
		System.out.println("Please set client info to start:");
		String username = getValidString("username: ");
		String password = getValidString("password: ");
		
		return new StudentClient(username, password, institution, server);
	}
	
	@Override
	public void showMenu() {
		System.out.println("Please select an option (1-4)"
				+ "\n1. create account"
				+ "\n2. reserve a book"
				+ "\n3. reserve a book - inter libraries"
				+ "\n4. Exit");
	}
	
	@Override
	public void manageUserSelection(final StudentClient client, final int userChoice) {
		switch (userChoice) {
		case 1:
			createAccount(client);
			break;
		case 2:
			reserveBook(client);
			break;
		case 3:
			reserveInterLibrary(client);
			break;
		case 4:
			exit();
			break;
		default:
			System.out.println("Invalid Input, please try again.");
		}
	}
	
	private void createAccount(final StudentClient client) {
		String firstName = getValidString("firstName: ");
		String lastName = getValidString("lastName: ");
		String emailAddress = getValidString("emailAddress: ");
		String phoneNumber = getValidString("phoneNumber: ");
		
		String message = client.createAccount(firstName, lastName, emailAddress, phoneNumber);
		System.out.println(message);
		
		showMenu();
	}
	
	private void reserveBook(final StudentClient client) {
		String bookName = getValidString("bookName: ");
		String authorName = getValidString("authorName: ");
		
		String message = client.reserveBook(bookName, authorName);
		System.out.println(message);
		
		showMenu();
	}
	
	private void reserveInterLibrary(final StudentClient client) {
		String bookName = getValidString("bookName: ");
		String authorName = getValidString("authorName: ");
		
		String message = client.reserveInterLibrary(bookName, authorName);
		System.out.println(message);
		
		showMenu();
	}

}
