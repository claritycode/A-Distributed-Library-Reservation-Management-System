import java.net.* ;
import java.io.* ;
import DRMSServices.LibraryInterfacePOA;
import DRMSServices.nonReturners;
import java.util.ArrayList ;
import Call.* ;
import Response.* ;

public class FrontEnd extends LibraryInterfacePOA {
	private static String systemProperty = null ;
	private InetSocketAddress sequencerAddress ;
	private ArrayList<String> replicaNames ;
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

	public FrontEnd ( String newLibraryName, InetSocketAddress newAddress, ArrayList<String> newNames ) {
		libraryName = newLibraryName ;
		sequencerAddress = newAddress ;
		replicaNames = newNames ;
	}
	
	public String getName () {
		return libraryName ;
	}
	@Override
	public boolean createAccount(String firstName, String lastName, String email, String phoneNumber, 
			String username, String password, String educationalInstitute) {
		
		DatagramSocket socket = null ;
		try {
			socket = new DatagramSocket() ;
			
			createAccountCall call = new createAccountCall ( socket.getInetAddress(), socket.getPort(), 
				firstName, lastName, email, phoneNumber,
				username, password, educationalInstitute ) ;
		
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
	
	private boolean getMajority ( ArrayList<BooleanResponse> response ) {
		
		if ( response.size() != replicaNames.size() ) {
			return response.get(0).getResult() ;
		}
		
		boolean majorityResult = false ;
		int counter  = 0 ;
		for ( BooleanResponse r: response ) {
			
			if ( counter == 0 ) {
				majorityResult = r.getResult() ; 
				counter ++ ;
			} else if ( majorityResult == r.getResult() ) {
				counter ++ ;
			} else {
				counter-- ;
			}
		}
		
		if ( counter == replicaNames.size() ) {
			return majorityResult ;
		}
		
		for ( BooleanResponse r: response ) {
			if ( r.getResult() != majorityResult ) {
				// TODO ReplicaManager faultyReplica = getRemoteObject ( r.getReplicaName() )
				return majorityResult ;
			}
			
		
		}
		
		return false ;
	}
	
	private boolean receiveFirstReply ( DatagramSocket socket ) throws SocketException, IOException {
		BooleanResponse result ;
		byte[] receiveBuffer = new byte[512] ;
		DatagramPacket receivePacket = new DatagramPacket ( receiveBuffer, receiveBuffer.length ) ;
		socket.receive(receivePacket);
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
	
	
	private ArrayList<BooleanResponse> receiveReply ( DatagramSocket socket ) throws SocketException, IOException {
		ArrayList<BooleanResponse> result = new ArrayList<BooleanResponse> () ;
		socket.setSoTimeout(30);
		byte[] receiveBuffer = new byte[512] ;
		DatagramPacket receivePacket = new DatagramPacket ( receiveBuffer, receiveBuffer.length ) ;
		try {
			while ( true ) {
				socket.receive(receivePacket);
				ByteArrayInputStream bs = new ByteArrayInputStream ( receivePacket.getData() ) ;
				ObjectInputStream is = new ObjectInputStream ( bs ) ;
				try {
					BooleanResponse res = ( BooleanResponse ) is.readObject() ;
					result.add(res) ;
				} catch ( ClassNotFoundException e ) {
					System.out.println ( e.getMessage() ) ;
				}
			}
		} catch (SocketTimeoutException e ) {
			return result ;
		}
		
	}
	
	
	private void sendRequest ( ClientCall call, DatagramSocket socket ) throws IOException {
		
		ByteArrayOutputStream bs = new ByteArrayOutputStream () ;
		ObjectOutputStream os = new ObjectOutputStream ( bs ) ;
		os.writeObject( call );
		
		byte[] sendBuffer = bs.toByteArray() ;
		DatagramPacket sendPacket = new DatagramPacket ( sendBuffer, sendBuffer.length, 
				sequencerAddress.getAddress(), sequencerAddress.getPort() ) ;
		socket.send(sendPacket);
	}
	
	private ArrayList<GetNonReturnersResponse> receiveGetNonReturnersReply ( DatagramSocket socket ) throws IOException  {
		ArrayList<GetNonReturnersResponse> result = new ArrayList<GetNonReturnersResponse> () ;
		socket.setSoTimeout(30);
		byte[] receiveBuffer = new byte[1024] ;
		DatagramPacket receivePacket = new DatagramPacket ( receiveBuffer, receiveBuffer.length ) ;
		try {
			while ( true ) {
				socket.receive(receivePacket);
				ByteArrayInputStream bs = new ByteArrayInputStream ( receivePacket.getData() ) ;
				ObjectInputStream is = new ObjectInputStream ( bs ) ;
				try {
					GetNonReturnersResponse res = ( GetNonReturnersResponse ) is.readObject() ;
					result.add(res) ;
				} catch ( ClassNotFoundException e ) {
					System.out.println ( e.getMessage() ) ;
				}
			}
		} catch (SocketTimeoutException e ) {
			return result ;
		}
	}
	
	private boolean compareNonReturners ( nonReturners[] majorityResult, nonReturners[] otherResult ) {
		
		for ( nonReturners m: majorityResult ) {
			boolean universityFound = false ;
			for ( nonReturners o: otherResult ) {
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
	
	private DRMSServices.nonReturners[] getNonReturnersMajority ( ArrayList<GetNonReturnersResponse> response ) {
		if ( response.size() != replicaNames.size() ) {
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
		
		if ( counter == replicaNames.size() ) {
			return majorityResult ;
		}
		
		for ( GetNonReturnersResponse r: response ) {
			if ( compareNonReturners ( majorityResult, r.getResult() ) ) {
				// TODO ReplicaManager faultyReplica = getRemoteObject ( r.getReplicaName() )
				break ;
			}
		}
		return majorityResult ;
	}
	
	@Override
	public nonReturners[] getNonReturners(String username, String password, String educationalInstitute, int days) {
		DatagramSocket socket = null ;
		try {
			socket = new DatagramSocket() ;
			
			getNonReturnersCall call = new getNonReturnersCall ( socket.getInetAddress(), socket.getPort(), 
				username, password, libraryName, days ) ;
			
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
	public boolean reserveBook(String username, String password, String bookName, String authorName) {
		DatagramSocket socket = null ;
		try {
			socket = new DatagramSocket() ;
			
			reserveBookCall call = new reserveBookCall ( socket.getInetAddress(), socket.getPort(), 
				username, password, bookName, authorName, libraryName ) ;
			
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
	public boolean reserveInterLibrary(String username, String password, String bookName, String authorName) {
		DatagramSocket socket = null ;
		try {
			socket = new DatagramSocket() ;
			
			reserveInterLibraryCall call = new reserveInterLibraryCall ( socket.getInetAddress(), socket.getPort(), 
				username, password, bookName, authorName, libraryName ) ;
			
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
	public boolean setDuration( String studentUsername, String bookName, int days) {
		DatagramSocket socket = null ;
		try {
			socket = new DatagramSocket() ;
			
			setDurationCall call = new setDurationCall ( socket.getInetAddress(), socket.getPort(), 
				"admin", "admin", studentUsername, bookName, null, days, libraryName ) ;
			
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