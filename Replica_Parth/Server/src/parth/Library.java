package parth;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import util.NonReturnersParser;
import DRMSServices.LibraryInterfacePOA;
import DRMSServices.lateStudent;
import DRMSServices.nonReturners;
/**
 *@author Parth Patel
 * A <code>Library</code> represents the library of a University.
 * It consist of <code>Student</code> account database and the <code>Book</code> database.
 * The two users of the <code>Library</code> are <code>Student</code> and <code>Admin</code>
 * The library provides many facilities to the students
 * */
public class Library extends LibraryInterfacePOA implements Runnable{
	
	private String name ;
	private HashMap < String, ArrayList<Student> > accountDatabase = new HashMap < String, ArrayList<Student> >();
	private HashMap<Book,Book> bookDatabase = new HashMap <Book,Book>() ;
	private FileWriter logFile ;
	private FileWriter adminFile;
	private DatagramSocket udpServerSocket ;
	private boolean toShutDown = false ;
	private boolean toCreateBug	= false ;
	
	public String getName () {
		return name ;
	}
	
	public HashMap<String, ArrayList<Student>> getAccountDatabase () {
		return accountDatabase ;
	}
	
	public HashMap<Book,Book> getBookDatabase () {
		return bookDatabase ;
	}
	
	/**
	 * Constructor- constructs a new library
	 * @param newname - The name of the university to which the library belongs to
	 * @udpConnectionPort - The port number through which library will serve other libraries
	 * */
	public Library ( String newname, int udpConnectionPort  ){
		name = newname ;
		
		// Create 26 entries for the student username into the hashtable
		// Each entry represents a unique alphabet of the English language
		for ( char i = 'a'; i <= 'z' ; i++  ) {
			String key = Character.toString(i) ;
			accountDatabase.put( key, new ArrayList<Student>()) ;
		}
		
		// Message to be written to the activity log of the library
		String message = name + " server created at " + Calendar.getInstance().getTime() + "\n" ;
		
		try {
			// Create the activity log file of the library
			String fileName = name + "_log" + ".txt" ;
			logFile = new FileWriter ( fileName ) ;	
			logFile.write(message + System.lineSeparator());	// Write to the log file
			logFile.flush();
			
			// udpServerSocket is the socket on which the library listens to the requests coming from other libraries
			// to get non returners or to reserve an inter library book
			udpServerSocket = new DatagramSocket(udpConnectionPort) ;
			
			// create an activity log file for the admin of the library
			adminFile = new FileWriter( name + "_" + "admin_log.txt" ) ;
			
			// Create a folder to hold the activity log files of each student of the library
			File studentFiles = new File( name + "_Student_Files") ;
			studentFiles.deleteOnExit();
			
			studentFiles.mkdir();	// Create directory
			
			initializeStudents () ;
			initializeBooks () ;
			// Start UDP Server
			Thread UDPServer = new Thread ( this ) ;
			UDPServer.start();
		}catch ( IOException e ) {
			System.out.println ( e.getMessage() ) ;
		}
		// IOException also handles SocketException
	}
	
	/**
	 * Adds new <code>Book</code> to the library. This method is supposed to be available only through access to the server
	 * @param bookName - Name of the new <code>Book</code>
	 * @param authorName - Name of the author <code>Author</code>
	 * @param bookCopies - Number of copies of the <code>Book</code>
	 * @return - Indicate if the book was added to the library
	 * */

	public boolean addBook ( String bookName, String authorName, Integer bookCopies ) {
		// Create the book object
		Book newArrival = new Book ( bookName, authorName, bookCopies ) ;
		
		//check if it is already available in the library
		if ( bookDatabase.containsKey(newArrival)) {
			String message = name + " tried to add an already existing book named " + newArrival.getName() 
					+ " to the collection at " + Calendar.getInstance().getTime() ;
			writeLog ( message ) ;
			return false ;
		}
		else {
			
			bookDatabase.put(newArrival, newArrival) ;
			String message = bookCopies + " Copies of Book named " + newArrival.getName() + " added to collection at " 
					+ Calendar.getInstance().getTime() + " by " + name ;
			writeLog ( message ) ;	
			return true ;
		}
		
		
 	}
	
	/**
	 * A <code>Library</code> maintains an activity log of all the activity happening through its server
	 * This method maintains and updates the activity log of the library.
	 * It write the string argument to the activity log file of the library.
	 * Concurrent access to the activity log file of the library can not be provided as it is a shared resource.
	 * @param message - Message to be written to the activity log of the library
	 * */
	public synchronized void writeLog ( String message ) {
		try {
			// write message along with operating systems native line separator character
			logFile.write( message + System.lineSeparator() ) ;
			logFile.flush();
		} catch ( IOException e ) {
			System.out.println( "Could not write the following string to log for " + name + " " + e.getMessage() ) ;
		}
	}
	
	@Override
	/**
	 * Creates a new <code>Student</code> account into the library.
	 * This is one of the main service provided by the library to the <code>Student</code>.
	 * It creates a new <code>Student</code> into the account database of the library.
	 * The Student can there after use other library facilities
	 * It checks the Student details for validity.
	 * @return boolean - A boolean value representing success or failure.
	 */
	public boolean createAccount(String firstName, String lastName,
			String email, String phoneNumber, String username,
			String password, String educationalInstitute) {

		// Extract the first alphabet of the student username
		// Note: Student username must begin with an alphabet because the details are stored alphabetically 
		// according to student's username
		String firstAlpha = username.substring(0, 1) ;
		if ( ! firstAlpha.matches("[a-zA-Z]") )  {
			String message = "Bad username used in createAccount at " + Calendar.getInstance().getTime() ;
			writeLog(message) ;
			return false ;
		}
		
		
		firstAlpha.toLowerCase() ;	// To avoid case sensitivity problem
		// get a reference to the list of students with username begining with firstAlpha
		ArrayList<Student> list = accountDatabase.get(firstAlpha) ;		
		
		// Create a new Student object.
		// It may through an exception indicating invalid data
		Student newStudent ;
		try {
			newStudent = new Student ( firstName, lastName, email, phoneNumber, username, password,
					educationalInstitute ) ;	
		}
		catch ( Exception e ) {
			String message = "An attempt create a user with invalid information was made at " 
					+ Calendar.getInstance().getTime() ;
			writeLog (message) ;
			return false ;
		}
		
		boolean userFound = false ;
		
		// Check if the username is already taken
		// Duplicate usernames are not allowed by the system
		// The list must be synchronized as it a shared resource across the library
		synchronized ( list ) {
			for ( Student existing : list ) {
				if ( existing.getUserName().equals (username) ) {
					userFound = true ;
					break ;
				}
			}		
			// If the username is not taken.
			// Add the student to the library's account database
			if ( !userFound ) {
				list.add(newStudent) ;	
			}	
		}
		
		// If the username is already taken inform the client about it.
		if ( userFound ) {
			String message = "An attempt to create an existing user was made at " + Calendar.getInstance().getTime() ;
			writeLog(message) ;
			return false ;
		}
		
		String message = "A new student with username " + newStudent.getUserName () + " was created at " 
				+ Calendar.getInstance().getTime();
		writeLog ( message ) ;
		return true ;
		
	}

	@Override
	/**
	 * Reserves the requested <code>Book</code> for the <code>Student</code>.
	 * This is one of the primary service provided by the library
	 * @param username - username of the <code>Student</code>
	 * @param password - password of the <code>Student</code>
	 * @param bookName - NAme of the book
	 * @param authorName - Name of the author
	 * @return boolean - A boolean value representing success or failure
	 * */
	public boolean reserveBook(String username, String password,
			String bookName, String authorName) {

		// Helper method to check if a registered student has requested this service
		Student currentStudent = authorizeStudent ( username, password ) ;
//		 If the login details are invalid write appropriate message ot the library activity log and 
//		 Inform the client of invalid login credentials
		if ( currentStudent == null ) {
			String message = "A user with invalid login credentials tried to register a book named" + bookName 
					+  " at " + Calendar.getInstance().getTime();
			writeLog(message) ;
			return false ;	
		}
		
		// Create a new Book object. This object represents just a single copy of the Book
		Book demandedBook = new Book ( bookName, authorName ) ;
		
		// Check if the library has the book in its database
		if ( bookDatabase.containsKey(demandedBook)) {
			// If the book is available then order the book
			if ( bookDatabase.get(demandedBook).order() ) {
				// If the book is successfully ordered then put the book into the student's issued booklist
				// As a resource ( issued books list) of the student is to be modified, synchronization is necessary
				synchronized ( currentStudent ) {
					currentStudent.reserve (demandedBook) ;
				}
				// Write appropriate  message to activity log
				String message = bookName + " was reserved by " + currentStudent.getUserName()
						+ " at " + Calendar.getInstance().getTime() + " (Copies Left: " + bookDatabase.get(demandedBook).getCopies() + ")";
				writeLog(message) ;
				return true ;
			}
			// An available book will not be up for order if the number of copies is zero
			// Hence, inform the client about the unavailability of the book
			else {
				String message = "Could not reserve book titled " + bookName + 
						" for " + username + " as the book is unavailable at " + Calendar.getInstance().getTime() ;
				writeLog ( message ) ;
				return false ;
			}
			
		} // If the book is not available inform the client about it 
		else {
			String message = username + " demanded an unavailable book titled "
					+ bookName + " at " + Calendar.getInstance().getTime() ;
			writeLog ( message ) ;
			return false ;
		}
	}
	
	/**
	 * Reserves a <code> Book </code> for the <code>Student</code>.
	 * This is a very helpful and flexible service provided by the library to the students
	 * If a <code>Book</code> is unavailable with the library then it consults other libraries on
	 * their UDP Port to request them to reserve the book on its behalf.
	 * @param username - username of the <code>Student</code>
	 * @param password - password of the <code>Student</code>
	 * @param bookName - NAme of the book
	 * @param authorName - Name of the author
	 * @return boolean - A boolean value representing success or failure
	 * */
	public boolean reserveInterLibrary ( String username, String password, String bookName, String authorName ) {

		// Helper method to check if a registered student has requested this service

		Student currentStudent = authorizeStudent ( username, password ) ;
		
		 //If the login details are invalid write appropriate message ot the library activity log and 
		//Inform the client of invalid login credentials
		if ( currentStudent == null ) {
			String message = "A user with invalid login credentials tried to register a book named" + bookName 
					+  " at " + Calendar.getInstance().getTime();
			writeLog(message) ;
			return false ;	
		}
		
		// Create a new Book object. This object represents just a single copy of the Book
		Book demandedBook = new Book ( bookName, authorName ) ;
		
		// Check if the library has the book in its database
		if ( bookDatabase.containsKey(demandedBook)) {
			// If the book is available then order the book
			if ( bookDatabase.get(demandedBook).order() ) {
				// If the book is successfully ordered then put the book into the student's issued booklist
				// As a resource ( issued books list) of the student is to be modified, synchronization is necessary
				synchronized ( currentStudent ) {
					currentStudent.reserve (demandedBook) ;
				}
				
				// Write appropriate  message to activity log
				String message = bookName + " was reserved by " + currentStudent.getUserName()
						+ " at " + Calendar.getInstance().getTime() + " (Copies Left: " + bookDatabase.get(demandedBook).getCopies() + ")";
				writeLog(message) ;
				return true ;
			}
		}
		
		// If the book is not available with the server then it is time to contact other servers
		// The server will send UDP message to other servers consisting of bookname and author name.
		// If a server has the book they will order it from there library on the behalf of the requesting library
		// The server sends the message on UDP port of the other servers
		// A server gets this UDP ports from a user define Registry service that maintains the UDP Port of all servers
		// Helper method - It gives address (UDP port number) of other libraries as an object of LibraryAddress
		
		ArrayList<LibraryAddress> others = getOtherLibraries () ; ;
		
		
		boolean result ;
		
		// The operation of book order on the peer server and issuing the book to the student must
		// be atomic
		// Hence, Synchronization on the Student is necessary to prevent any other behaviour 
		// between this two operations
		synchronized ( currentStudent ) {
			// The book needs to be registered only to one of the peer libraries
			// Hence, book registration is requested iteratively
			// Loop through the other library addresses
			for ( LibraryAddress lib : others ) {
				// Helper method that performs sending and receiving of UDP messages
				result = interReserve ( lib.getPortNo(), bookName, authorName ) ;
				// If the book was ordered by lib then add it to the students issued book list 
				if ( result ) {
					currentStudent.reserve(demandedBook);
					String message = bookName + " was reserved by " + currentStudent.getUserName()
							+ " from " + lib.getName() + " library at " + Calendar.getInstance().getTime() ;
					writeLog(message) ;
					return true ;
				}
			}
		}
		
		// If none of the library have the demanded book then display appropriate message to the student and update log
		String message = " Username " + currentStudent.getUserName() + " tried to registered a non existing book " 
				+ "using inter library service at " + Calendar.getInstance().getTime() ; 
		writeLog(message) ;
		return false ;
	}

	@Override
	/**
	 * Calculates the Non returner Students.
	 * A non returner is a student who has not submitted the issued book within an appropriate time
	 * This service is available only to the administrator of the library
	 * Non returners from all the libraries are displayed.
	 * The non returners of the peers are obtained by using UDP messages.
	 * @param username - username of the admin
	 * @param password - password of the admin
	 * @param educationalInstitute - University of the admin
	 * @param days - the number of days beyond which an issued book is considered for fine
	 * @return nonReturners[] - representing all the non returners from all the libraries that are part of this DRMS
	 * */
	public nonReturners[] getNonReturners(String username, String password,
			String educationalInstitute, int days) {
		// Check login credentials of admin
		if ( !username.equals("admin") || !password.equals("admin")) {
			String message = "A wrong username or password was given to retrieve non returners at "
					+ Calendar.getInstance().getTime() ;
			writeLog(message) ;
			return null ;
		}
		
		// ArrayList to hold the result
		ArrayList<lateStudent> result = new ArrayList<lateStudent>() ;
		
		// Loop through the student account database
		
		for ( Map.Entry<String, ArrayList<Student>> studentList : accountDatabase.entrySet() ) {
			for ( Student student : studentList.getValue() ) {
				synchronized (student) {
					// Check if the student is a non returner
					if ( student.isNonReturner(days)) {
						// Put an entry into array for the non returner
						result.add( new lateStudent ( student.getFirstName(), student.getLastName(), student.getPhoneNumber())) ;
					}
				}
			}
		}
		
		// Convert the ArrayList into an array
		// It is necessary as the nonReturner class has an array of lateStudent
		// Note: This has to be done because IDL does not have a mapping for Java ArrayList.
		lateStudent[] studentList = new lateStudent[result.size()] ;
		studentList = result.toArray(studentList) ;

		// Get addresses of other libraries
		ArrayList<LibraryAddress> others = getOtherLibraries() ; ;
		

		// Final Result
		nonReturners[] resultArray = new nonReturners[others.size() + 1] ;
		resultArray[0] = new nonReturners( name, studentList ) ;		// Add the nonReturners for this library
		
		// Concurrently send request to other libraries to obtain their non returners
		LibraryUDPClient[] requester = new LibraryUDPClient[others.size()] ;
		Thread requests[] = new Thread[requester.length] ;
		for ( int i = 0; i != others.size(); i++ ) {
			requester[i] = new LibraryUDPClient ( others.get(i).getPortNo(), days ) ;
			requests[i] = new Thread ( requester[i]) ;
			requests[i].start() ;
		}

		// Wait for the request made to all the libraries to be completed
		for ( int i = 0 ; i < requests.length ; i++ ) {
			try {
				requests[i].join();
			} catch ( InterruptedException e ) {
				continue ;
			}
		}		
		
		// Add the results obtained from all the peer libraries
		for ( int i = 0; i < requester.length ; i++ ) {
			resultArray[i+1] = requester[i].getResult() ;
		}
		
		
	
			try {
				for ( nonReturners message : resultArray ) {
					// Write the results to the admin file
					// Use a static method from NonReturnersParser to help parse the state of a nonReturners object
					adminFile.write( NonReturnersParser.nonReturnersToString(message));
					adminFile.flush();	
				}	
			} catch ( IOException e ) {
				System.out.println ( "The following exception happened while writing to the admin log for "
						+ educationalInstitute + " "+ e.getMessage() ) ;
			}
		
		// Write Library log
		String message = "A successful call to get the non returners of all the libraries was made at " 
				+ Calendar.getInstance().getTime() ;
		writeLog(message) ;

		// Convert the arraylist into a String array to be sended to the client
		return resultArray;
	}

	@Override
	/**
	 * A debugging tool - It changes the duration of an issued book
	 * It is used to test the validity of other functions like getNonReturners
	 * It is available only to admin
	 * @param username - username of admin
	 * @param password - password of admin
	 * @param studentUsername - username of the student whose data is to be manipulated
	 * @param bookName - The name of a book issued by this student
	 * @param authorNAme - The name of the author of the book
	 * @return boolean - Indicate success or failure of the operation
	 * */
	public boolean setDuration( String studentUsername, String bookName, int days ) {
	
				
		// Search for the student
		String firstAlpha = studentUsername.substring(0, 1) ;
		if ( ! firstAlpha.matches("[a-zA-Z]"))  {
			String message = "Bad username used in the setDuration debug tool at " 
					+ Calendar.getInstance().getTime();
			writeLog(message) ;
			return false ;
		}
		
		firstAlpha.toLowerCase() ;
		ArrayList<Student> list = accountDatabase.get(firstAlpha) ;
		
		if ( list != null ) {
			for ( Student existing : list ) {
				if ( existing.getUserName().equals (studentUsername) ) {
					// Set the duration for the student
					synchronized ( existing ) {
						Book b = new Book ( bookName, null ) ;
						return existing.setDuration ( b, days ) ;
					}
				}
			}	
		}		
		return false ;
	}
	
	// A helper method
	// Authenticate the login credentails of the student
	private Student authorizeStudent ( String username, String password ) {
		
		// Extract the first alphabet of the student username
		// Note: Student username must begin with an alphabet because the details are stored alphabetically 
		// according to student's username
		String firstAlpha = username.substring(0, 1) ;
		if ( ! firstAlpha.matches("[a-zA-Z]"))  {
			return null ;
		}
		
		
		firstAlpha.toLowerCase() ;		// To avoid case sensitivity issues
		ArrayList<Student> list = accountDatabase.get(firstAlpha) ;
				
		// Loop through the database
		for ( Student existing : list ) {
			// if the student is found then return the reference to the student
			if ( existing.getUserName().equals (username) ) {
				
				if ( existing.getPassword (). equals (password)) {
					
					return existing ;
				} else {
					// Otherwise return null
					return null ;
				}
			}
		}
		
		return null ;
	}
	
	// Helper method
	// Sends UDP messages to peer servers and receives replies from them
	// The UDP messages consists of data needed to reserve a book on the behalf of this library
	private boolean interReserve ( Integer portNo, String bookName, String authorName ) {
		
		DatagramSocket sender = null ; // Socket
		try {
			sender = new DatagramSocket () ;
			
			// The UDP message is formatted as follows
			// Operation Code (0 or 1) ;; (delimiter) bookName ;; authorName
			String messageString = "0" + ";;" + bookName + ";;" + authorName ;
			byte[] message = messageString.getBytes() ;		// Convert to bytes
			InetAddress destAddress = InetAddress.getLocalHost() ; // Localhost address to execute on same machine
			DatagramPacket pack = new DatagramPacket ( message, message.length, destAddress, portNo ) ;
			sender.send(pack) ;	// Send the packet
			
			// The reply from the peer servers will be either 1 ( success ) or 0 ( failure )
			byte[] receivedMessage = new byte[512] ;
			DatagramPacket recvPack = new DatagramPacket ( receivedMessage, receivedMessage.length ) ;
			sender.receive(recvPack);
			if ( new String( recvPack.getData(), 0, recvPack.getLength() ).equals("1")) {
				return true ;
			}
			
		} // Handle appropriate exception
		catch ( SocketException e ) {
			String message = "A socket exception prevented from sending inter library "
					+ "request at " + Calendar.getInstance().getTime()  ;
			writeLog ( message ) ;
			return false ;
		} catch ( UnknownHostException e ) {
			String message = "An Unknown host exception prevented from sending inter library "
					+ "request at " + Calendar.getInstance().getTime()  ;
			writeLog ( message ) ;
			return false ;
			
		}catch ( IOException e ) {
			String message = "An IO exception prevented from sending inter library "
					+ "request at " + Calendar.getInstance().getTime()  ;
			writeLog ( message ) ;
			return false ;
			
		}
		finally {
			if ( sender != null ) {
				sender.close() ;	// Avoid resource leakage
			}
		}
		
		return false ;
	}
	
	// Each library has a thread dedicated to receiving requests from peer libraries.
	// The request arrives at udpServerSocket
	// This thread is created immediately after creating the library
	// The thread represents a concurrent server
	// For every request arriving at udpServerSocket the thread forks a new LibraryUDPServer object and executes 
	// its thread
	// However, initially this thread registers its udp port number to the udp registry.
	// This helps other servers in finding this server
	public void run () {
		
		// Register port number with Registry
		// Message format : operation code ( 0 or 10 ;; (delimiter) ;; name of library
		try {
						
			byte[] sendBuf = ("0" + ";;" + name).getBytes() ;
			DatagramPacket sendPack = new DatagramPacket ( sendBuf, sendBuf.length, 
						InetAddress.getLocalHost(), 8000 ) ;
			udpServerSocket.send(sendPack);
		} catch ( IOException e ) {
			System.out.println ( "An exception: " + e.getMessage() ) ;
		}
		
		
		// Stop listening for requests only when told to shutdown
		// Infinite loop listening mode
		while ( ! toShutDown ) {
			try {
				byte[] buffer = new byte[512] ;
				DatagramPacket initial = new DatagramPacket(buffer, buffer.length);
				udpServerSocket.receive(initial);	// Wait for the packet
				
				// Fork a new thread for each received packet 
				// Note that we pass a reference to the library to LibraryUDPServer object in order to allow it
				// to work with library data in fulfilling the requests
						
				LibraryUDPServer lib = new LibraryUDPServer ( this, initial ) ;
				Thread request = new Thread ( lib ) ;
				request.start () ;
				
			} catch ( IOException e ) {
				System.out.println(e.getMessage());
			}
			
		}
	}
	
	// Helper method
	// Obtains Library Addresses of peer servers from Registry
	// Sends UDP messages and receives UDP messages from registry
	private ArrayList<LibraryAddress> getOtherLibraries ()  {
		
		DatagramSocket sock = null ;
		ArrayList<LibraryAddress> result = null ;
		try {	
			sock = new DatagramSocket () ;
			// send request to obtain library Addresses
			byte[] sendBuf = ("1" + ";;" + name).getBytes() ;
			DatagramPacket sendPack = new DatagramPacket ( sendBuf, sendBuf.length, 
						InetAddress.getLocalHost(), 8000 ) ;
			sock.send(sendPack);
			
			// Initially the server sends size packet indicating the number of peer servers.
			// Then the server sends size number of packets one for each peer.
			byte[] size = new byte[512] ;
			DatagramPacket sizePacket = new DatagramPacket(size, size.length) ;
			sock.receive(sizePacket);
			String dataSizeString = new String(sizePacket.getData(),0,sizePacket.getLength());
			int dataSize = Integer.parseInt(dataSizeString);
			result = new ArrayList<LibraryAddress>(dataSize) ;
			
			// loop until all the packets arrive
			while ( dataSize != 0 ) {
				byte[] reply = new byte[512] ;
				DatagramPacket data = new DatagramPacket ( reply, reply.length ) ;
				sock.receive(data);
				
				// PAckets are obtained as serialized objects of type LibraryAddress
				// LIbraryAddress is a type known to server and registry
				ByteArrayInputStream bs = new ByteArrayInputStream (data.getData()) ;
				ObjectInputStream os = new ObjectInputStream (bs) ;
				result.add ( (LibraryAddress) os.readObject() ) ; // deserialize the object
						
				--dataSize ;
			}	
		} catch ( SocketException e ) {
			System.out.println ( "An exception: " + e.getMessage() ) ;
		} catch ( IOException e ) {
			System.out.println ( "An exception: " + e.getMessage() ) ;
		} catch ( ClassNotFoundException e ) {
			System.out.println ( "An Exception: " + e.getMessage() ) ;
		}finally {
			if ( sock != null ) {
				sock.close();
			}
		}
		
		return result ;
	}
	
	
	private void initializeStudents () {
	
		FileReader file = null ;
		try {
			file = new FileReader ( "../../IdlFiles/resources/students" + "_" + name + ".csv" ) ;			
		} catch ( FileNotFoundException e ) {
			System.out.println ( "Could Not find the file to load initial students" ) ;
			System.out.println ( e.getMessage () ) ;
			return ;
		}
		BufferedReader br = new BufferedReader ( file ) ;
		String line = null ;
		try {
			while ( ( line = br.readLine() ) != null ) {
				String[] data = line.split(",") ;
				if ( data.length != 7 ) {
					System.out.println ( "The file for initialzing students is invalid" ) ;
					if ( file != null ) {
						try {
							br.close () ;
							file.close () ;
						} catch ( IOException e ) {
							System.out.println ( "An Io Exception happened while trying to close the stream during initializing students" ) ;
							System.out.println ( e.getMessage () ) ;
							return ;
						}
					}
					return ;
				}
				createAccount ( data[0], data[1], data[2], data[3], data[4], data[5], data[6] ) ;
			}
		} catch ( IOException e ) {
			System.out.println ( "IO Exception happened while initalizing some students" ) ;
			System.out.println ( e.getMessage () ) ;
			return ;
		} finally {
			if ( file != null ) {
				try {
					br.close () ;
					file.close () ;
				} catch ( IOException e ) {
					System.out.println ( "An Io Exception happened while trying to close the stream during initializing students" ) ;
					System.out.println ( e.getMessage () ) ;
					return ;
				}
			}
		}
	}
	

	private void initializeBooks () {
		FileReader file = null ;
		try {
			file = new FileReader ( "../../IdlFiles/resources/books" + "_" + name + ".csv") ;			
		} catch ( FileNotFoundException e ) {
			System.out.println ( "Could Not find teh file to load initial books" ) ;
			System.out.println ( e.getMessage () ) ;
			return ;
		}
		BufferedReader br = new BufferedReader ( file ) ;
		String line = null ;
		try {
			while ( ( line = br.readLine() ) != null ) {
				String[] data = line.split(",") ;
				if ( data.length != 3 ) {
					System.out.println ( "The file for initialzing books is invalid" ) ;
					if ( file != null ) {
						try {
							br.close () ;
							file.close () ;
						} catch ( IOException e ) {
							System.out.println ( "An Io Exception happened while trying to close the stream during initializing books" ) ;
							System.out.println ( e.getMessage () ) ;
							return ;
						}
					}
					return ;
				}
				addBook( data[0], data[1], Integer.parseInt(data[2]) ) ;
			}
		} catch ( IOException e ) {
			System.out.println ( "IO Exception happened while initalizing some books" ) ;
			System.out.println ( e.getMessage () ) ;
			return ;
		} finally {
			if ( file != null ) {
				try {
					br.close () ;
					file.close () ;
				} catch ( IOException e ) {
					System.out.println ( "An Io Exception happened while trying to close the stream during initializing books" ) ;
					System.out.println ( e.getMessage () ) ;
					return ;
				}
			}
		}
	}
	
	@Override
	public void shutDown() {
		toShutDown = true ;
		if ( udpServerSocket != null ) {
			udpServerSocket.close();			
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setByzantineFlag(boolean byzantineFlag) {
		toCreateBug = true ;
		// TODO Auto-generated method stub
		
	}

}
