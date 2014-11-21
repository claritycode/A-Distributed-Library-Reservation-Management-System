package multithread;

import idl.Library;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.UserException;

import ui.UserUI;
import client.POALoader;
import client.StudentClient;
import entities.constants.PropertiesEnum;

public class MultithreadReserveInterLibraryTest {
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append(">>>>>>>>>>");
		sb.append("\nBefore starting this test, please start (or restart) 'server.StartServer' for libraries 'concordia', 'vanier', and 'webster'");
		sb.append("\nTest will have 3 students from concordia trying to reserve book1, book5 and book 7.");
		sb.append("\nThe initial availability in concordia is book1=1; book5=inexistent; book7=inexistent");
		sb.append("\nThe initial availability in vanier is book1=5; book5=3; book7=inexistent");
		sb.append("\nThe initial availability in webster is book1=inexistent; book5=1; book7=2");
		sb.append("\n\nExpected results:");
		sb.append("\nBook1) One student will reserve book1 in concordia. Other 2 will reserve in vanier.");
		sb.append("\nBook5) No student will reserve in concordia. All 3 will be able to reserve either in vanier or webster.");
		sb.append("\nBook5) No student will reserve in concordia or vanier. Only 2 of 3 will be able to reserve in webster.");
		sb.append("\nFinal availability will be:");
		sb.append("\nThe final availability in concordia is book1=0; book5=inexistent; book7=inexistent");
		sb.append("\nThe final availability in vanier is book1=3; book5=(1 or 0); book7=inexistent");
		sb.append("\nThe final availability in webster is book1=inexistent; book5=(1 or 0); book7=0");
		sb.append("\nfyi: The final availability of book5 for vanier and webster is unknow, as it depends on where each student "
				+ "reserved. But the sum of both availability should be 1 (either 1 in vanier and 0 in webster or 0 in vanier "
				+ "and 0 in webster).");
		sb.append("\n<<<<<<<<<<");
		System.out.println(sb.toString());
		try {
			MultithreadReserveInterLibraryTest test = new MultithreadReserveInterLibraryTest();
			test.testMultipleReservations();
		} catch (UserException e) {
			e.printStackTrace();
		}
	}
	
	public void testMultipleReservations() throws UserException {
		List<Thread> threads = createHolderThreads("concordia", 3);
		
		for (Thread t : threads) {
			t.start();
		}
	}
	
	private List<Thread> createHolderThreads(final String institution, final int quantity) throws UserException {
		List<Thread> threads = new ArrayList<Thread>();
		String first = institution.substring(0, 1);
		for (int i = 0; i < quantity; i++) {
			StudentHolder sh = new StudentHolder(first + i, "pw", institution, loadPoa(institution));
			threads.add(new Thread(sh));
		}
		return threads;
	}
	
	protected Library loadPoa(String institution) throws UserException {
		Map<String, String> properties = UserUI.loadProperties();
		String port = properties.get(PropertiesEnum.ORB_INITIAL_PORT.val());
		String host = properties.get(PropertiesEnum.ORB_INITIAL_HOST.val());
		
		return POALoader.load(port, host, institution);
	}
	
	class StudentHolder implements Runnable {
		final String username;
		final StudentClient client;
		
		StudentHolder(String username, String password, String institution, Library poa) {
			this.username = username;
			this.client = new StudentClient(username, password, institution, poa);
		}

		@Override
		public void run() {
			String msg1 = client.reserveInterLibrary("book1","author1");
			System.out.println("\t" + username + "\treserving\tbook1,author1 with result ="+ msg1);
			String msg2 = client.reserveInterLibrary("book5","author5");
			System.out.println("\t" + username + "\treserving\tbook5,author5 with result ="+ msg2);
			String msg3 = client.reserveInterLibrary("book7","author7");
			System.out.println("\t" + username + "\treserving\tbook7,author7 with result ="+ msg3);
		}
	}
}
