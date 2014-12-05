import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import Call.ClientCall;
import Call.createAccountCall;
import Call.getNonReturnersCall;
import Call.reserveBookCall;
import Call.reserveInterLibraryCall;
import Call.setDurationCall;
import Call.Response.BooleanResponse;
import Call.Response.GetNonReturnersResponse;
import DRMSServices.LibraryInterfacePOA;
import DRMSServices.nonReturners;


/**
 * 
 * @author Parth Patel
 * A <code>FrontEnd</code> is the dummy CORBA Object corresponding to a <code>Library</code>. It is dummy because it does not serve the client
 * request. Instead it forwards it to the replicated system. The replicated system gives a response back to the <code>FrontEnd</code>
 * which then returns it to the client as a CORBA response. 
 * The major task of <code>FrontEnd</code> are:
 * 1. Ensure the correctness of the System.
 * 	-> It does so by calculating the majority of the results obtained from the replicated system. It assumes the majority result to be the 
 * 		correct result.
 * 2. To ensure ( Fault Tolerance or High Availability) as expected from the system
 * 	-> It does so by communicating with the Replica Manager and informing it of the software bug in a replica. The bug is detected during
 * 		step 1 (Calculating majority). It also ensures high availability by guaranteeing that it will obtain reply from at least 
 * 		one replica for every client request.
 * 3. Make the replicated system appear as a single system to the client.
 *
 */
public class FrontEnd extends LibraryInterfacePOA {
	
	/**
	 * replicaManagerDatabase - Consists the address of all the replica managers i the replicated system
	 */
	private static HashMap< String, InetSocketAddress > replicaManagerDatabase = new HashMap <String, InetSocketAddress> ();
	
	/**
	 * systemProperty - The property expected from the entire system. Valid values are "Fault Tolerance" and "High Availability"
	 * This value is supposed to be common among all the <code>FrontEnd</code> objects created by the system.
	 */
	private static String systemProperty = null ;
	
	/**
	 * sequencerAddress - The address of the sequencer to whom the <code>FrontEnd</code> will forward every client request.
	 */
	private InetSocketAddress sequencerAddress ;
	
	/**
	 * libraryName - The name of the library for which this <code>FrontEnd</code> is a dummy object
	 */
	private String libraryName ;
	
	
	
	/**
	 * @return the systemProperty
	 */
	public static String getSystemProperty() {
		return systemProperty;
	}

	/**
	 * @param systemProperty the systemProperty to set
	 */
	public static void setSystemProperty(String systemProperty) {
		FrontEnd.systemProperty = systemProperty;
	}

	/**
	 * Constructor
	 * @param newLibraryName - name of the library that this <code>FrontEnd</code> represents/ is a dummy object
	 * @param newSequencerAddress - The address of the <code>Sequencer</code>
	 * @param newNames
	 */
	public FrontEnd ( String newLibraryName, InetSocketAddress newSequencerAddress ) {
		libraryName = newLibraryName ;
		sequencerAddress = newSequencerAddress ;
	}
	
	/**
	 * Add the address of a new replica manager to the FrontEnd
	 * @param replicaManagerName - Name of the replica manager
	 * @param ipAddress - IP address of the replica manager
	 * @param portNo - Port number of the replica manager
	 */
	public static void addReplicaManager ( String replicaManagerName, InetAddress ipAddress, int portNo ) {
		replicaManagerDatabase.put( replicaManagerName, new InetSocketAddress( ipAddress, portNo )) ;
	}
	
	/**
	 * Get the name of the library for which this FrontEnd is a dummy object
	 * @return
	 */
	public String getName () {
		return libraryName ;
	}
	
	
	
	
	
	/**
	 * Calculate the majority result out of the obtained results
	 * @param response - The Response object representing the response from the actual library implementation
	 * @return - The boolean value that indicates the majority of the result
	 */
	private boolean getMajority ( ArrayList<BooleanResponse> response ) {
		// If all the concerned libraries have not responded then there is a process crash. Hence, the obtained results are assumed to be 
		// correct. Hence, we can pass the result without calculating the majority.
		System.out.println ( "Response size" + response.size() ) ;
		System.out.println ( "Responses are: " ) ;
		for ( BooleanResponse b : response ) {
			System.out.println ( "Replica: " + b.getReplicaName() ) ;
			System.out.println ( "Response: "+ b.getResult() ) ;
		}
		if ( response.size() != replicaManagerDatabase.size()  && response.size() != 0 ) {
			
			return response.get(0).getResult() ;
			
		} 
		
		boolean majorityResult = false ;		// The majority result caulcuated so far
								
		int counter  = 0 ;						// The number of times the majority result is ahead of other result
		
		// Otherwise loop through all the responses
		for ( BooleanResponse r: response ) {
			/*
			 * When the counter != 0 then the result in the majorityResult is indeed the majorityResult 
			 */
			if ( counter == 0 ) {
				majorityResult = r.getResult() ; 
				counter ++ ;
			}
			/*
			 * If next response is the same as the majority result calculated so far then just increment the counter
			 */
			else if ( majorityResult == r.getResult() ) {
				counter ++ ;
			} 
			/*
			 * If next response is different from the majority result calculated so far then just decrement the counter
			 */
			else {
				counter-- ;
			}
		}
		
		/*
		 * The counter will be equal to number of replicas only when all the replicas have given the same result
		 */
		if ( counter == replicaManagerDatabase.size() ) {
			return majorityResult ;
		}
		
		/*
		 * Otherwise, there is a software bug. Hence, loop through the result
		 */
		for ( BooleanResponse r: response ) {
			// If this result has a bug then inform the concerned replica manager
			if ( r.getResult() != majorityResult ) {
				notifySoftwareBug ( r.getReplicaName(), libraryName ) ;
				return majorityResult ;
			}
		}
		
		return false ;
	}
	
	/**
	 * Sends a UDP message to the replica manager about the software bug encountered
	 * @param replicaName - Name of the replica
	 * @param libraryName - Name of the library
	 */
	private void notifySoftwareBug ( String replicaName, String libraryName ) {
		
		DatagramSocket socket = null ;
		try {
			
			socket = new DatagramSocket () ;
			
			// The nofitication sended to the replica manager is in the following format
			// name of the replia:Name of the library
			String data = "FAILURE" + ":" + replicaName + ":" + libraryName ;
			byte[] sendBuffer = data.getBytes() ;
			DatagramPacket sendPacket = new DatagramPacket ( sendBuffer, sendBuffer.length, 
					replicaManagerDatabase.get(replicaName).getAddress(), replicaManagerDatabase.get(replicaName).getPort() ) ;
			socket.send(sendPacket);
		} catch ( SocketException e ) {
			System.out.println ( "Exception: " + e.getMessage() ) ;
		} catch ( IOException e ) {
			System.out.println ( "Exception: " + e.getMessage() ) ;
		} 
	}
	
	/**
	 * In the case where the system is expected to provide High Availability we assume that a software bug will not happen.
	 * Hence, the FrontEnd simply waits until it gets atleast one response from any library and then forwards it to the cliet
	 */
	private boolean receiveFirstReply ( DatagramSocket socket ) throws SocketException, IOException {
		BooleanResponse result ;
		
		byte[] receiveBuffer = new byte[512] ;
		DatagramPacket receivePacket = new DatagramPacket ( receiveBuffer, receiveBuffer.length ) ;
		// Receive response
		socket.receive(receivePacket);
		
		// Deserialize it
		ByteArrayInputStream bs = new ByteArrayInputStream ( receivePacket.getData() ) ;
		ObjectInputStream is = new ObjectInputStream ( bs ) ;
		try {
			result = ( BooleanResponse ) is.readObject() ;
			return result.getResult() ;			
		} catch ( ClassNotFoundException e ) {
			System.out.println ( e.getMessage() ) ;
			// TODO Handle Exception properly
			return false ;
		}

	}
	
	/**
	 * In the case where the system is expected to provide High Availability we assume that a software bug will not happen.
	 * Hence, the FrontEnd simply waits until it gets atleast one response from any library and then forwards it to the cliet
	 */
	private nonReturners[] receiveFirstGetNonReturnersReply ( DatagramSocket socket ) throws IOException  {
		GetNonReturnersResponse result ;
		byte[] receiveBuffer = new byte[1024] ;
		DatagramPacket receivePacket = new DatagramPacket ( receiveBuffer, receiveBuffer.length ) ;
		socket.receive(receivePacket);
		ByteArrayInputStream bs = new ByteArrayInputStream ( receivePacket.getData() ) ;
		ObjectInputStream is = new ObjectInputStream ( bs ) ;
		try {
			result = ( GetNonReturnersResponse ) is.readObject() ;
			return result.getResult() ;
		} catch ( ClassNotFoundException e ) {
			System.out.println ( e.getMessage() ) ;
			return null ;
		}
	}
	
	/**
	 * In the case where the system is expected to provide Fault Tolerance the FrontEnd waits for a reasonable time to obtain
	 * the response from all the replicas.
	 * @return - ArrayList of all the responses obtained until timeout
	 */
	private ArrayList<BooleanResponse> receiveReply ( DatagramSocket socket ) throws SocketException, IOException {
		ArrayList<BooleanResponse> result = new ArrayList<BooleanResponse> () ;
		
		// Set the time out. FrontEnd will wait for responses form libraries until this much time.
		socket.setSoTimeout(1000);
		try {
			while ( true ) {
				byte[] receiveBuffer = new byte[512] ;
				DatagramPacket receivePacket = new DatagramPacket ( receiveBuffer, receiveBuffer.length ) ;
				// Receive Response
				socket.receive(receivePacket);
				
				// Deserialize it
				ByteArrayInputStream bs = new ByteArrayInputStream ( receivePacket.getData() ) ;
				ObjectInputStream is = new ObjectInputStream ( bs ) ;
				try {
					BooleanResponse res = ( BooleanResponse ) is.readObject() ;
					result.add(res) ;		// add to results
				} catch ( ClassNotFoundException e ) {
					System.out.println ( e.getMessage() ) ;
				}
			}
			// This exception will happen when the time out will happen
		} catch (SocketTimeoutException e ) {
			return result ;		// Return the results on timeout
		}
		
	}
	
	/**
	 * Forward the client request to the sequencer
	 * @param call - A <code>ClientCall</code> object representing the client request
	 * @param socket - A datagramSocket of the FrontEnd
	 * @throws IOException
	 */
	private void sendRequest ( ClientCall call, DatagramSocket socket ) throws IOException {
		
		// Serialize the object
		ByteArrayOutputStream bs = new ByteArrayOutputStream () ;
		ObjectOutputStream os = new ObjectOutputStream ( bs ) ;
		os.writeObject( call );
		os.close() ;
		bs.close();
		
		
		// Send it to the sequencer
		byte[] sendBuffer = bs.toByteArray() ;
		DatagramPacket sendPacket = new DatagramPacket ( sendBuffer, sendBuffer.length, 
				sequencerAddress.getAddress(), sequencerAddress.getPort() ) ;
		socket.send(sendPacket);
		System.out.println ( "Sended: " + call.getClass().getSimpleName()) ;
	}
	
	/**
	 * In the case where the system is expected to provide Fault Tolerance the FrontEnd waits for a reasonable time to obtain
	 * the response from all the replicas.
	 * @return - ArrayList of all the responses obtained until timeout
	 */
	private ArrayList<GetNonReturnersResponse> receiveGetNonReturnersReply ( DatagramSocket socket ) throws IOException  {
		ArrayList<GetNonReturnersResponse> result = new ArrayList<GetNonReturnersResponse> () ;
		// Set the time out. FrontEnd will wait for responses form libraries until this much time.
		socket.setSoTimeout(1000);
		try {
			while ( true ) {
				// Receive response
				byte[] receiveBuffer = new byte[1024] ;
				DatagramPacket receivePacket = new DatagramPacket ( receiveBuffer, receiveBuffer.length ) ;
				socket.receive(receivePacket);
				
				// Deserialize it
				ByteArrayInputStream bs = new ByteArrayInputStream ( receivePacket.getData() ) ;
				ObjectInputStream is = new ObjectInputStream ( bs ) ;
				try {
					GetNonReturnersResponse res = ( GetNonReturnersResponse ) is.readObject() ;
					result.add(res) ;
					is.close() ;
					bs.close() ;
				} catch ( ClassNotFoundException e ) {
					System.out.println ( e.getMessage() ) ;
				}
			}
		}
		// This exception will happen when the time out will happen
		catch (SocketTimeoutException e ) {
			return result ;
		}
	}
	
	/**
	 * compares the two results of the getNonReturners method available to the admin for equality
	 * @param majorityResult - The first result
	 * @param otherResult - the second result
	 * @return - boolean value indicating if they are equal
	 */
	private boolean compareNonReturners ( nonReturners[] lhs, nonReturners[] rhs ) {
		
		for ( nonReturners m: lhs ) {
			boolean universityFound = false ;
			for ( nonReturners o: rhs ) {
				if ( m.universityName.equalsIgnoreCase(o.universityName)) {
					universityFound = true ;
					if ( m.studentList.length != o.studentList.length ) {
						return false ;
					}
					for ( int i = 0; i != m.studentList.length; i++ ) {
						if ( !(m.studentList[i].firstName.equalsIgnoreCase(o.studentList[i].firstName)) ||
								!(m.studentList[i].lastName.equalsIgnoreCase(o.studentList[i].lastName))  ||
								!(m.studentList[i].phoneNumber.equalsIgnoreCase(o.studentList[i].phoneNumber)) ) {
							return false ;
						}
					}
				}
			}
		
			if ( !universityFound ) {
				return false ;
			}
		}
		
		return true ;
	}
	
	/**
	 * Calculates the majority result ( correct result) for the getNonReturners method available to admin
	 * @param response - The list of all the results obtained from the replicas
	 * @return - The majority result
	 */
	private DRMSServices.nonReturners[] getNonReturnersMajority ( ArrayList<GetNonReturnersResponse> response ) {
		
		// If all the concerned libraries have not responded then there is a process crash. Hence, the obtained results are assumed to be 
		// correct. Hence, we can pass the result without calculating the majority.
		if ( response.size() != replicaManagerDatabase.size() && response.size() != 0) {
			return response.get(0).getResult() ;
		}
		
		nonReturners[] majorityResult = null ;
		int counter  = 0 ;
		for ( GetNonReturnersResponse r: response ) {
			
			if ( counter == 0 ) {
				majorityResult = r.getResult() ; 
				counter ++ ;
			} else if ( compareNonReturners ( majorityResult, r.getResult() ) ) {
				counter ++ ;
			} else {
				counter-- ;
			}
		}
		
		if ( counter == replicaManagerDatabase.size() ) {
			return majorityResult ;
		}
		
		for ( GetNonReturnersResponse r: response ) {
			if ( ! compareNonReturners ( majorityResult, r.getResult() ) ) {
				notifySoftwareBug ( r.getReplicaName(), libraryName ) ;
				break ;
			}
		}
		return majorityResult ;
	}
	
	@Override
	/**
	 * Dummy Implementation for the client request to create a new account
	 */
	public boolean createAccount(String firstName, String lastName, String email, String phoneNumber, 
			String username, String password, String educationalInstitute) {
		
		// Create a new Socket
		DatagramSocket socket = null ;
		try {
			socket = new DatagramSocket() ;

			// Create a call object
			createAccountCall call = new createAccountCall ( socket.getLocalAddress(), socket.getLocalPort(),firstName, lastName, email, phoneNumber,
				username, password, educationalInstitute ) ;
			System.out.println ( socket.getLocalPort())  ;
			// Send the call object in serialized form to the sequencer
			sendRequest ( call, socket ) ;			
			
			// If High Availability is required than only wait for the first reply
			if ( FrontEnd.getSystemProperty().equals("High Availability") ) {
				return receiveFirstReply ( socket ) ;
			}
			
			// Else obtain all the replies
			ArrayList<BooleanResponse> response  = receiveReply ( socket ) ;
			// Calculate majority and inform replica manager if there is a software bug
			boolean result = getMajority ( response ) ;
			return result ;
			
		} catch ( SocketException e ) {
			System.out.println ( e.getMessage () ) ;
			return false ;
		} catch ( IOException e ) {
			System.out.println( e.getMessage () ); 
			return false ;
		} 
		
	}
	
	@Override
	/**
	 * Dummy Implementation for the client request to get non returners
	 */
	public nonReturners[] getNonReturners(String username, String password, String educationalInstitute, int days) {
		DatagramSocket socket = null ;
		try {
			socket = new DatagramSocket() ;
			
			getNonReturnersCall call = new getNonReturnersCall ( socket.getLocalAddress(), socket.getLocalPort(), username, password, libraryName, days ) ;
			
			sendRequest ( call, socket ) ;			
		
			if ( FrontEnd.getSystemProperty().equals("High Availability") ) {
				return receiveFirstGetNonReturnersReply ( socket ) ;
			}
			
			ArrayList<GetNonReturnersResponse> response  = receiveGetNonReturnersReply ( socket ) ;
			nonReturners[] result = getNonReturnersMajority ( response ) ;
			return result ;
			
		} catch ( SocketException e ) {
			System.out.println ( e.getMessage () ) ;
			return null ;
		} catch ( IOException e ) {
			System.out.println( e.getMessage () ); 
			return null ;
		} finally {
			if ( socket != null ) {
				socket.close() ;
			}
		}
	}

	@Override
	/**
	 * Dummy Implementation for the client request to reserve book
	 */
	public boolean reserveBook(String username, String password, String bookName, String authorName) {
		DatagramSocket socket = null ;
		try {
			socket = new DatagramSocket() ;
			
			reserveBookCall call = new reserveBookCall ( socket.getLocalAddress(), socket.getLocalPort(), username, password, bookName, authorName, libraryName ) ;
			
			sendRequest ( call, socket ) ;			
		
			if ( FrontEnd.getSystemProperty().equals("High Availability") ) {
				return receiveFirstReply ( socket ) ;
			}
			
			ArrayList<BooleanResponse> response  = receiveReply ( socket ) ;
			boolean result = getMajority ( response ) ;
			return result ;
			
		} catch ( SocketException e ) {
			System.out.println ( e.getMessage () ) ;
			return false ;
		} catch ( IOException e ) {
			System.out.println( e.getMessage () ); 
			return false ;
		} finally {
			if ( socket != null ) {
				socket.close() ;
			}
		}
	}

	@Override
	/**
	 * Dummy Implementation for the client request to reserve book using inter library service
	 */
	public boolean reserveInterLibrary(String username, String password, String bookName, String authorName) {
		DatagramSocket socket = null ;
		try {
			socket = new DatagramSocket() ;
			
			reserveInterLibraryCall call = new reserveInterLibraryCall ( socket.getLocalAddress(), socket.getLocalPort(), username, password, bookName, authorName, libraryName ) ;
			
			sendRequest ( call, socket ) ;			
		
			if ( FrontEnd.getSystemProperty().equals("High Availability") ) {
				return receiveFirstReply ( socket ) ;
			}
			
			ArrayList<BooleanResponse> response  = receiveReply ( socket ) ;
			boolean result = getMajority ( response ) ;
			return result ;
			
		} catch ( SocketException e ) {
			System.out.println ( e.getMessage () ) ;
			return false ;
		} catch ( IOException e ) {
			System.out.println( e.getMessage () ); 
			return false ;
		} finally {
			if ( socket != null ) {
				socket.close() ;
			}
		}
	}

	@Override
	/**
	 * Dummy Implementation for the client request to set duration of the loan
	 */
	public boolean setDuration( String studentUsername, String bookName, int days) {
		DatagramSocket socket = null ;
		try {
			socket = new DatagramSocket() ;
			
			setDurationCall call = new setDurationCall ( socket.getLocalAddress(), socket.getLocalPort(),"admin", "admin", studentUsername, bookName, null, days, libraryName ) ;
			
			sendRequest ( call, socket ) ;			
		
			if ( FrontEnd.getSystemProperty().equals("High Availability") ) {
				return receiveFirstReply ( socket ) ;
			}
			
			ArrayList<BooleanResponse> response  = receiveReply ( socket ) ;
			boolean result = getMajority ( response ) ;
			return result ;
			
		} catch ( SocketException e ) {
			System.out.println ( e.getMessage () ) ;
			return false ;
		} catch ( IOException e ) {
			System.out.println( e.getMessage () ); 
			return false ;
		} finally {
			if ( socket != null ) {
				socket.close() ;
			}
		}	
	}

	@Override
	public void shutDown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setByzantineFlag(boolean byzantineFlag) {
		// TODO Auto-generated method stub
		
	}

	
	
}