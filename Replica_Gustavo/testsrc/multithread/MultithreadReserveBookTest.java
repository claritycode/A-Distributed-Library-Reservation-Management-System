package multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.UserException;

import ui.UserUI;
import DRMSServices.LibraryInterface;
import client.POALoader;
import client.StudentClient;
import entities.constants.PropertiesEnum;

public class MultithreadReserveBookTest {
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append(">>>>>>>>>>");
		sb.append("\nBefore starting this test, please start (or restart) 'server.StartServer' for libraries 'concordia', 'vanier', and 'webster'");
		sb.append("\nTest will have 3 students from concordia and 2 from vanier trying to reserve book1 and book2.");
		sb.append("\nThe initial availability in concordia is book1=1; book2=5");
		sb.append("\nThe initial availability in vanier is book1=5; book2=1");
		sb.append("\n\nExpected results:");
		sb.append("\nConcordia: only 1 student will get book1. All 3 will get book2. Final availability will be: book1=0; book2=2");
		sb.append("\nVanier: all 2 students will get book1. Only 1 student will get book2. Final availability will be: book1=2; book2=0");
		sb.append("\n<<<<<<<<<<");
		System.out.println(sb.toString());
		try {
			MultithreadReserveBookTest test = new MultithreadReserveBookTest();
			test.testMultipleReservations();
		} catch (UserException e) {
			e.printStackTrace();
		}
	}
	
	public void testMultipleReservations() throws UserException {
		List<Thread> threads = createHolderThreads("concordia", 3);
		threads.addAll(createHolderThreads("vanier", 2));
		
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
	
	protected LibraryInterface loadPoa(String institution) throws UserException {
		Map<String, String> properties = UserUI.loadProperties();
		String port = properties.get(PropertiesEnum.ORB_INITIAL_PORT.val());
		String host = properties.get(PropertiesEnum.ORB_INITIAL_HOST.val());
		
		return POALoader.load(port, host, institution);
	}
	
	class StudentHolder implements Runnable {
		final String username;
		final StudentClient client;
		
		StudentHolder(String username, String password, String institution, LibraryInterface poa) {
			this.username = username;
			this.client = new StudentClient(username, password, institution, poa);
		}

		@Override
		public void run() {
			String msg1 = client.reserveBook("book1","author1");
			System.out.println("\t" + username + "\treserving\tbook1,author1 with result ="+ msg1);
			String msg2 = client.reserveBook("book2","author2");
			System.out.println("\t" + username + "\treserving\tbook2,author2 with result ="+ msg2);
		}
	}
}
