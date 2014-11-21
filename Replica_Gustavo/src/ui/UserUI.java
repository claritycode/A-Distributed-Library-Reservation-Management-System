package ui;

import idl.Library;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.omg.CORBA.UserException;

import client.Client;
import client.POALoader;
import entities.User;
import entities.constants.PropertiesEnum;

public abstract class UserUI<T extends User, K extends Client<T>> {
	protected final Scanner keyboard;
	private final Map<String, String> properties;

	public UserUI() {
		super();
		this.keyboard = new Scanner(System.in);
		this.properties = loadProperties();
	}
	
	public void init(int choices) {
		K client = null;
		
		try {
			client = createClient();
		} catch (Exception e) {
			e.printStackTrace();
			keyboard.close();
		}
		if (client != null) {
			System.out.println("Client is up.");
			showMenu();
			
			while (true) {
				int userChoice =  getValidInt("", 1, choices);
				manageUserSelection(client, userChoice);
			}
		} else {
			System.out.println("Unable to create client. Shutting down.");
		}
	}
	
	protected abstract K createClient() throws UserException;
	protected abstract void showMenu();
	protected abstract void manageUserSelection(final K client, final int userChoice);

	protected int getValidInt(final String message, final int min, final int max) {
		System.out.print(message);
		int userChoice = 0;
		boolean valid = false;
		while (!valid) {
			try {
				userChoice = keyboard.nextInt();
				if (userChoice >= min && userChoice <= max) {
					valid = true;
				}
			} catch (Exception e) {
				keyboard.nextLine();
			}
			if (!valid) {
				System.out.println("Invalid input. Please enter an integer between " + min + "-" + max);
			}
		}
		System.out.println(userChoice);
		return userChoice;
	}
	
	protected String getValidString(final String message) {
		System.out.print(message);
		String value = null;
		boolean valid = false;
		while (!valid) {
			value = keyboard.next();
			if(value != null && value.length() > 0) {
				valid = true;
			} else {
				System.out.println("Invalid input. Please enter a valid String: ");
			}
		}
		System.out.println(value);
		return value;
	}
	
	protected String getLibraryName() {
		String[] names = loadLibraryNames();
		String message = "Select a library:\n";
		for (int i = 0; i < names.length; i++) {
			message +=(i+1) + ") " + names[i] + "\n";
		}
		
		int serverIndex = getValidInt(message, 1, names.length);
		String libraryName = names[serverIndex-1].trim();
		System.out.println("You selected library [" + libraryName + "]");
		return libraryName;
	}
	
	protected Library loadServer(String institution) throws UserException {
		String port = properties.get(PropertiesEnum.ORB_INITIAL_PORT.val());
		String host = properties.get(PropertiesEnum.ORB_INITIAL_HOST.val());
		
		return POALoader.load(port, host, institution);
	}
	
	private String[] loadLibraryNames() {
		String libraries = this.properties.get(PropertiesEnum.LIBRARIES.val());
		if (libraries != null && libraries.length() > 0) {
			return libraries.split(",");
		} else {
			throw new IllegalArgumentException("Unable to load property 'libraries'. Please check your config.properties file.");
		}
	}

	protected void exit() {
		System.out.println("Turning off client!");
		keyboard.close();
		System.exit(0);
	}
	
	public static Map<String, String> loadProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
			input = new FileInputStream("./resources/config.properties");
			prop.load(input);
			
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				properties.put(key, value);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return properties;
	}
}
