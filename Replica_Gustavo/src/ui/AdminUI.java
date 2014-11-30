package ui;

import org.omg.CORBA.UserException;

import DRMSServices.LibraryInterface;
import client.AdminClient;
import entities.Administrator;

public class AdminUI extends UserUI<Administrator, AdminClient> {

	public static void main(String[] args) {
		AdminUI ui = new AdminUI();
		ui.init(3);
	}

	@Override
	public AdminClient createClient() throws UserException {
		String institution = getLibraryName();
		LibraryInterface poa = loadServer(institution);
		
		return new AdminClient("Admin", "Admin", institution, poa);
	}
	
	@Override
	public void showMenu() {
		System.out.println("Please select an option (1-3)" 
				+ "\n1. get non-returners"
				+ "\n2. set duration"
				+ "\n3. Exit");
	}
	
	@Override
	public void manageUserSelection(final AdminClient client, final int userChoice) {
		switch (userChoice) {
		case 1:
			getNonReturners(client);
			break;
		case 2:
			setDuration(client);
			break;
		case 3:
			exit();
			break;
		default:
			System.out.println("Invalid Input, please try again.");
		}
	}
	
	private void getNonReturners(final AdminClient client) {
		int numDays = getValidInt("numDays: ", -100, 100);
		
		String message = client.getNonRetuners(numDays);
		System.out.println(message);
		
		showMenu();
	}
	
	private void setDuration(final AdminClient client) {
		int numDays = getValidInt("numDays: ", -100, 100);
		String username = getValidString("username");
		String bookName = getValidString("book");
		
		String message = client.setDuration(username, bookName, numDays);
		System.out.println(message);
		
		showMenu();
	}

}
