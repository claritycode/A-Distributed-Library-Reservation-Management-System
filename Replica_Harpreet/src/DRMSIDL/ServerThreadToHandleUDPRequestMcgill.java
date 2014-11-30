package DRMSIDL;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;


public class ServerThreadToHandleUDPRequestMcgill extends Thread
{
	DRMSMcgill server;		// The object of the  Server is made so as to invoke the UDP Functions on it 
	
	DatagramPacket request;	//The Request packet that is used to accept the connection request from the Different servers
	DatagramPacket replyUDP;// the Reply packet that is used to send the Reply message to the specific Server
	String nonreturners;	//The String Non returner to put the Details of the NonReutrners
	DatagramSocket socket = null;// Datagram Socket Used to Send The Reply messages
	String reserveInterLibraryString[];// the String that is used to get the rquest for the Inter library Reservation of books And also to get the Acknowledgement
	
	/*DESC:Constructor f the class used to intialize the different values
	PARAM1:the object of the DRMSSEVER
	PARAM2:The datagram Packet used for handling  the Request Message 
	*/
	public ServerThreadToHandleUDPRequestMcgill(DRMSMcgill server, DatagramPacket request)
	{
		this.server=server;
		this.request=request;
	}
	
	//desc: The run function that handles theUDP requests/ Acknowledgements that have been made to the server by this thread
	
	public void run()
	{
		try
		{
		socket=new DatagramSocket();	//Socket to Send the Reply to the Servers
		String reply;					//String in which the Reply is sent to the Servers
		reserveInterLibraryString =(new String(request.getData()).split(" "));// parsing the the String that is recieved through the UDP Request so as to know weather the it si request or Acknowledgement
		//DRMSServer ServerObject= new Server();
		//ServerObject.getNonReturners();
		
		 if(new String(request.getData()).contains(" "))
		    {
			 	Boolean _wasBookReserved=false;	// to tell weather the book was reserved im the server or not
			 	reserveInterLibraryString =(new String(request.getData()).split(" "));
			 	if(reserveInterLibraryString[0].equals("reserve"))
			 	{
		    	_wasBookReserved=server.reserveBookInterLibrary(reserveInterLibraryString[1],reserveInterLibraryString[2],reserveInterLibraryString[3]);
		    	System.out.println(_wasBookReserved);
		    	String logString=new String(reserveInterLibraryString[1]+"  Trying to reserve the book  "+reserveInterLibraryString[2]+"   "+reserveInterLibraryString[3]+"-----  "+_wasBookReserved.toString()+" "+Calendar.getInstance().getTimeInMillis() );
		    	server.log(logString);
		    	byte _wasBookReservedReply[]=new byte[5000];
		    	_wasBookReservedReply=(Boolean.toString(_wasBookReserved)).getBytes();
		    	replyUDP=new DatagramPacket(_wasBookReservedReply, _wasBookReservedReply.length,request.getAddress(),request.getPort());
		    	socket.send(replyUDP);
		    	server.log("The Reply has been sent back to the server");
			 	}
		 
			 	else if(reserveInterLibraryString[0].equals("acknowledgement"))
			 	{
			 		server.log("the Acknowledge ment recieved "+reserveInterLibraryString[2].trim());
			 		if(reserveInterLibraryString[2].trim().equals("true"))
			 		{
			 			server.log("The acknowledge ment has been recieved ");
			 		}
			 		else if(reserveInterLibraryString[2].trim().equals("false"))
			 		{
			 			server.increamentTheBookCount(reserveInterLibraryString[1].trim());
			 			server.log("The acknowledge ment has been recieved and changes has been reveted to the book  "+reserveInterLibraryString[1]);
			 		
			 		}
			 	}
			 	else 
			 	{
			 		System.out.println("There is something wrong");
			 	}
		    }
		 
		    else
		    {
		    int _noOfDaysInt=Integer.parseInt(new String(request.getData()).trim());
		    String _instituteName="Mcgill";
		    
					

			    byte[] nonreturenrsByte = new byte[1000];
			    
			        // Get client request
			       // DatagramPacket request = new DatagramPacket(nonreturenrs,nonreturenrs.length);
			        //socket.receive(request);

			        // Retrieve definition from dictionary file
			       /* if(word != null) 
			            definition = getDefinition(new String(word), dictionaryFile);
*/
			        // Put reply into packet, send packet to client
			    	nonreturners=getNonReturnersLocal(_noOfDaysInt,_instituteName);
			    	nonreturenrsByte=nonreturners.getBytes();
			        DatagramPacket Reply = new DatagramPacket(nonreturenrsByte, nonreturenrsByte.length, request.getAddress(), request.getPort());
			        socket.send(Reply);
			        socket.disconnect();
			        socket.close();
			        System.out.println(nonreturners);

			    
			    

			    System.out.println(Thread.currentThread().getName() + " just run");
		    }
			    //DictServer.decNumThreads();
		}
		catch (Exception e) 
	    {
	        System.out.println("Error: " + e.getMessage());
	    }
	}
	
	/*DESC:the Function is used to get the List of NonReturners in the Current Library server and return list as sting 
			so that Server can returne this String to other servers through UDP message
		PARAM1:the duration of for which the non returners is asked
		PARAM2:name of the institute for which the data isasked
		RETUEN:the String containing the list ofnon returners*/
	public String getNonReturnersLocal(int duration, String institute)
	{
		//printStudentMap();
		StringBuilder _detailsOfNonReturners=new StringBuilder();
		_detailsOfNonReturners.append(institute+":  ");
		for(Iterator it=server.hash.entrySet().iterator();it.hasNext();)
		{
			Map.Entry entry=(Map.Entry)it.next();
			Character ch=(Character)entry.getKey();
			System.out.println("key is "+ch+"     ");
			ArrayList<StudentCredentials> al=(ArrayList<StudentCredentials>) entry.getValue();
			for(StudentCredentials s:al)
			{
				ArrayList<BooksAndDuration> _toCheckTheDateOfReturn=s.books_duration;
				for(BooksAndDuration _bookAndDate:_toCheckTheDateOfReturn)
				{
					if(_bookAndDate.g_returnDate<=duration)
					{
						System.out.println(s.first_name+" "+s.last_name+" "+s.phone_no);
						_detailsOfNonReturners.append(s.first_name+"  ");
						_detailsOfNonReturners.append(s.last_name+"  ");
						_detailsOfNonReturners.append((s.phone_no)+"");
						_detailsOfNonReturners.append("----");
						break;
					}
				}
			}
			
		}
		System.out.println(_detailsOfNonReturners);
		return _detailsOfNonReturners.toString();
	 }
	
}
	


