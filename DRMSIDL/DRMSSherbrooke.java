package DRMSIDL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class DRMSSherbrooke extends DRMSPOA implements Runnable
{
	Map<Character,ArrayList<StudentCredentials>> hash=new HashMap<>();	//Hash MAp to STORE THE Details of the Student at Library Servers
	Map<Character,ArrayList<BookDetails>> book=new HashMap<>();			//Hash MAp to STORE THE Details of the Books Present at Library Servers
	ArrayList <StudentCredentials> al=new ArrayList<>();				// List for fetching up the student credentials from the student hash map
	DatagramSocket _serverSocket=null;									//  Server Socket to Accept the Connection from the Different servers
	StringBuilder getNonReturnersString=new StringBuilder();			// String Builder so as to get the details of nonreturners from the different servers
	public DRMSSherbrooke()
	{
		for(Character i='a';i<='z';i++)
		{
			hash.put(i,new ArrayList<StudentCredentials>());// initialize the has map of the student with the key that contains the first alphabet of thier names
			book.put(i,new ArrayList<BookDetails>());// initialize the hash map of the book with the key that contains the first alphabet of thier names
		}
		for(int i=0;i<9;i++)
		{
			String number=Integer.toString(i);
			Character index=number.charAt(0);
			book.put(index,new ArrayList<BookDetails>());// initialize the hash map of the book with the key that contains the first alphabet of thier names
			
		}
	}
   //----------------------------------------------------- Function Export Server------------------------------------------------	
	/*public void exportserver1()throws Exception
	{
		Remote objectSherbrooke=UnicastRemoteObject.exportObject(this,7000);
		Registry r2=LocateRegistry.createRegistry(7000);
		r2.bind("sherbrooke", objectSherbrooke);
	}
	*///--------------------------------------------------ThreadFuntion Run---------------------------------------------------
	public void run()
	{
		try
		{
			try
			{
			_serverSocket=new DatagramSocket(7001);			// initializing the Datagram Socket for Specific Port Number of the Server
			System.out.println("server started");
			int i=0;
			
			while(true)				// while loop So That Server is continuosly Listening at its Server Port for the connection request from other Servers
			{
				i++;
				byte _noOfDays[]= new byte[100];
				 DatagramPacket request = new DatagramPacket(_noOfDays, _noOfDays.length);
			    _serverSocket.receive(request);
			    ServerThreadToHandleUDPRequestSherbrooke server = new ServerThreadToHandleUDPRequestSherbrooke(this,request);// A thread is initiated to handle every Request
			    server.start();
			}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//-----------------------------------------------Main function---------------------------------------------------------------
	public static void main(String args[])
	{
		try
		{
			DRMSSherbrooke sherbrookeServer=new DRMSSherbrooke();//Creating the object of the Server So as to initate the server and create threads on that server
			Thread sherbrookeServerThread= new Thread(sherbrookeServer);//Creating new Thread so that the server keep on listening at the Port for new UDP Request
			sherbrookeServerThread.start();
			for(int i=0;i<1;i++)	//Multiple Threads Running and Trying to Add Books At the same Time to the Server To check concurrency
			{
			AddBooksSherbrooke bookcreator=new AddBooksSherbrooke(sherbrookeServer);// Multiple times the Thread are trying to create the Book in the Server
			Thread bookCreatorThread=new Thread(bookcreator);//this is done to check the concurrency of the Shared Data
			bookCreatorThread.start();			//Starting the thread
			}
			ORB orbSherbrooke =ORB.init(args, null);				//Orb is intialized 
			POA rootPOA= POAHelper.narrow(orbSherbrooke.resolve_initial_references("RootPOA"));// To get the JAVAReference from the CORBA Reference
			
			byte [] id=rootPOA.activate_object(sherbrookeServer);
			org.omg.CORBA.Object ref=rootPOA.id_to_reference(id);
			String ior=orbSherbrooke.object_to_string(ref);
			System.out.println(ior);
			PrintWriter file= new PrintWriter("iorSherbrooke.txt");
			file.println(ior);
			file.close();
			rootPOA.the_POAManager().activate();
			orbSherbrooke.run();
			
			//sherbrookeServer.exportserver1();
			System.out.println("The Server is now Running");
			//(new DRMSSherbrooke()).start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//----------------------------------------------------------------------Create Account Function for Student--------------------------------------
	
	
	/*DESC:   Function to create the Account of the Student and the Parametrs Are User to enter 
	The Credentails Of the Student while Creating the Account
	
	*PARAM1: Fiirst Name of the User
	*PARAM2:Laset Name of the Student
	*PARAM3:Email Address of the user
	*PARAM4:Username 
	*PARAM5:password required for Sign up and Login
	*PARAM6:name of the institute to which student want to make the account
	*PARAM7: The Phone number of the student
	*/

	@Override
	public int createAccount(String _first_name, String _last_name, String _email,
			String _username, String _password, String _institute, int _phone_no) 
	{
System.out.println("in the create account");
		
		Character key=' ';
		boolean successful=false;
		try
		{
			if(!(_username.length()<6)&&(_username.length()>15))
			{
				return 1;
			}
			else if(!(_password.length()>=6))
			{
				return 2;
			}
		key = new Character(_username.charAt(0)); 
		//if(hash.containsKey(key)==true)
		//{
			
		int flag=test(hash,key,_username);
		switch(flag)
		{
		case 1:			{
						/*System.out.println("returned 1");
						//ArrayList<StudentCredentials> al_new=new ArrayList<StudentCredentials>();
						hash.put(key, new ArrayList<StudentCredentials>());
						StudentCredentials student=new StudentCredentials(_first_name, _last_name,_email,_username,_password,_institute,_phone_no);
						hash.get(key).add(student);
						java.util.Collections.sort(hash.get(key),new StudentArrayListSorter());
						log(" create account "+_username+" "+_institute+" "+_phone_no);
						successful=true;
						break;
						*/
						StudentCredentials newstudent=new StudentCredentials(_first_name, _last_name,_email,_username,_password,_institute,_phone_no);
						synchronized(hash.get(key))
						{
							hash.get(key).add(newstudent);
							java.util.Collections.sort(hash.get(key),new StudentArrayListSorter());
							log("Account credated For :"+_username+" "+_institute+" "+_phone_no);
								/*for(StudentCredentials s:_to_add_new)
	`							{
								System.out.print(s.username+"    ");
								}
								_to_add_new.add(new StudentCredentials(username,password));
								hash.put(key,_to_add_new);*/
								successful=true;
								break;
						}
						}
						
		case 2:			//ArrayList<StudentCredentials> _to_add_new=hash.get(key);
						StudentCredentials newstudent=new StudentCredentials(_first_name, _last_name,_email,_username,_password,_institute,_phone_no);
						synchronized(hash.get(key))
						{
						hash.get(key).add(newstudent);
						java.util.Collections.sort(hash.get(key),new StudentArrayListSorter());
						log("Account credated For :"+_username+" "+_institute+" "+_phone_no);
						/*for(StudentCredentials s:_to_add_new)
				`		{
							System.out.print(s.username+"    ");
						}
						_to_add_new.add(new StudentCredentials(username,password));
						hash.put(key,_to_add_new);*/
						successful=true;
						break;
						}
						
		case 3:			System.out.println("the username you entered is alreday exists");
						log("create account"+_username+" "+_institute+" "+_phone_no+"username already exits");
						successful=false;
						break;
						
						
		default: 		System.out.println("returned value is not correct");
						break;
						
						
		}

		/*}
		else
		{
			al.add(new StudentCredentials(_first_name, _last_name,_email,_username,_password,_institute,_phone_no));
			hash.put(key,al);
		}
		*/	
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			
			
		
				
		
		/*
		System.out.println(hash);
		System.out.println("key"+hash.get)*/
		if(successful)
		{
			return 4;
		}
		else
			return 3;
		
		
	}
	//-----------------------------------------------Function to test the details of the hash map----------------------------------------
	
	
	/*DESC: FUnction to Test Weather there is a username or the key for the username already existing in the Hash Map
	PARM1:Hash Map containing the details of the student
	PARAM2:Character key that is the key of the hash map
	PARAM3:username 
	Return: The Integer Value on the basis of the criteria
		*/	
	public  int test(Map<Character,ArrayList<StudentCredentials>> hash1,Character key1,String username)
	{
		if(hash1.get(key1)!=null)
		{
		ArrayList<StudentCredentials> al2=(ArrayList)hash1.get(key1);
		for(StudentCredentials user:al2)
		{
			if(username.equals(user.username))
				return 3;
		}
			return 2;
		}
	
			return 1;
		
	}
	
	//------------------------------------------------function for Checking the details for student login-------------------------
	
	
	
	/*DESC:Student Login Function for checking weather the Student Login is Successful or not on the basis of parameters value 
	PARM1: username of the student
	PARAM2:password of the student account
	RETURN:True or False on the basis of the weather the login was successful or not
	
		*/
	

	@Override
	public boolean studentLogin(String _username, String _password) 
	{
		printStudentMap();
		Character key=_username.charAt(0);
		if(hash.containsKey(key))
		{
		ArrayList<StudentCredentials> al2=(ArrayList)hash.get(key);
		for(StudentCredentials user:al2)
		{
			if((_username.equals(user.username))&&(_password.equals(user.password)))
			{
				log("studentlogin  "+_username+" "+_password);
				return true;
			}
				
		}
			return false;
		}
	
			return false;
		
	}
	//----------------------------------------------------Function for Reserving book in library by Student------------------------------
	
	
	/*DESC:Function used to Resrve Book in the Library by the Specific Student 
	PARM1:username of the student who want to reserve the book
	PARAM2:Name of the Book which student want to reserve
	ReturnType:boolean Describe Weather operation was Successful or not
		*/
	@Override
	public boolean reserveBook(String _userName, String _bookName)
	{
		Character _keyStudent=_userName.charAt(0);
		Character _keyBook=_bookName.charAt(0);
		boolean _flagBookExists=false;
		if(book.containsKey(_keyBook))
		{
		ArrayList<BookDetails>_bookExists=book.get(_keyBook);
		for(BookDetails _bookReference:_bookExists)
		{
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$"+_bookReference.m_bookName);
			if(_bookReference.m_bookName.equals(_bookName))
			{				
				if(_bookReference.m_noOfCopies>0)
				{
				_flagBookExists=true;
				_bookReference.m_noOfCopies-=1;
				log("reserve Book "+_userName+" "+_bookName);
				break;
				}
			}
			
		}
		if(_flagBookExists)
		{
			ArrayList<StudentCredentials> al2=(ArrayList)hash.get(_keyStudent);
			for(StudentCredentials user:al2)
			{
				System.out.println("----------------------------------------------------------"+user.username);
				if((_userName.equals(user.username)))
				{
					user.books_duration.add(new BooksAndDuration(_bookName,14));
					bookDetailsPrint();
					return true;
				}
			}
		}
		else
		{ 
			log("reserve Book "+_userName+" "+_bookName+" book not found");
			return false;
		}
		}
		
		
		return false;
		
	}
	//-----------------------------------------------------Function to Add Book By the Admnistrator to the Library-----------------------
	
	
	/*DESC:Function that allow the Admin to Add Book to the Library
	PARM1:name of the book
	PARAM2:the name of the author
	RETURN:Returns the value on the basis weather the operation was successful or not
	
		*/
	@Override
	public boolean addBook(String _bookName, String _authorName) 
	{
		
		
			Character key=_bookName.charAt(0);
		
		
						if(book.containsKey(key))
						{
							//ArrayList<StudentCredentials> _to_add_new=hash.get(key);
							BookDetails _newBook= new BookDetails(_bookName,_authorName);
							synchronized(book.get(key))
							{
								for(Iterator it=book.entrySet().iterator();it.hasNext();)
								{
									Map.Entry entry=(Map.Entry)it.next();
									Character ch=key;
									//System.out.print("key is"+ch+"        ");
									ArrayList<BookDetails> al1=(ArrayList<BookDetails>) entry.getValue();
									for(BookDetails s:al1)
									{
										if(s.m_bookName.equals((_bookName)))
										return false;
										
									}
									//System.out.println();
								}
							book.get(key).add(_newBook);
							log("add book by admin"+_bookName+" "+_authorName);
						/*for(StudentCredentials s:_to_add_new)
						{
							System.out.print(s.username+"    ");
						}
						_to_add_new.add(new StudentCredentials(username,password));
						hash.put(key,_to_add_new);*/
						//successful=true;
							return true;
							}
						}
						else
						{
							
							//ArrayList<StudentCredentials> al_new=new ArrayList<StudentCredentials>();
							BookDetails _newBook=(new BookDetails(_bookName,_authorName));
							book.put(key, new ArrayList<BookDetails>());
							book.get(key).add(_newBook);
							log("add book by admin"+_bookName+" "+_authorName);
							//successful=true;
							return true;
						}
		}

						
		//--------------------------------------------Block to print the Value of the Book Hash Map----------------------------------------------
		

		/*for(Iterator it=book.entrySet().iterator();it.hasNext();)
		{
			Map.Entry entry=(Map.Entry)it.next();
			Character ch=(Character) entry.getKey();
			System.out.print("key is"+ch+"        ");
			ArrayList<BookDetails> al1=(ArrayList<BookDetails>) entry.getValue();
			for(BookDetails s:al1)
			{
				System.out.print(s.m_bookName+"------------");
				
			}
			System.out.println();
		}
		
		System.out.println(book);
		
		
		
		System.out.println(hash);
		System.out.println("key"+hash.get)
		 }
		 */
		
		
	/*DESC:Function to print the Details of the book on the Console
		*/	

	 public void bookDetailsPrint()
	 {
		for(Iterator it=book.entrySet().iterator();it.hasNext();)
		{
			Map.Entry entry=(Map.Entry)it.next();
			Character ch=(Character) entry.getKey();
			System.out.print("key is"+ch+"        ");
			ArrayList<BookDetails> al1=(ArrayList<BookDetails>) entry.getValue();
			for(BookDetails s:al1)
			{
				System.out.print(s.m_bookName+"------------"+s.m_noOfCopies);
				
			}
			System.out.println();
		}
	 }
	 /*DESC:Function to print the details of the Student Hash Map on the Console
			*/

		
	public void printStudentMap()
	{
		for(Iterator it=hash.entrySet().iterator();it.hasNext();)
		{
			Map.Entry entry=(Map.Entry)it.next();
			Character ch=(Character) entry.getKey();
			System.out.print("key is"+ch+"        ");
			ArrayList<StudentCredentials> al1=(ArrayList<StudentCredentials>) entry.getValue();
			for(StudentCredentials s:al1)
			{
				System.out.print(s.username+"------------");
				ArrayList<BooksAndDuration> _toCheckTheDateOfReturn=s.books_duration;
				for(BooksAndDuration _bookAndDate:_toCheckTheDateOfReturn)
				{
					System.out.println(_bookAndDate.g_bookName);
					System.out.println(_bookAndDate.g_returnDate);
				}
			}
			System.out.println();
				
		}
			
		
		
		System.out.println(hash);

	}

	//--------------------------------------------------------------Get Non returners Server Connection Function ------------------------------------------------------------------
	/*DESC:Function to get the list of Nonreturners
	PARAM1:The Duration in integer
	Return: Return the String containing the names of all the Non-Returners 
*/
	@Override
	public String getNonReturners(int duration)
	{
		String _detailsOfNonReturners=getNonReturnersLocal(duration);
		log(_detailsOfNonReturners);
		StringBuilder _detailsOfNonReturnersFinal=new StringBuilder(_detailsOfNonReturners);
		DatagramSocket socket=null;
		String finalDeatils="";
		String durationString=duration+"";
		try
		{
			DRMSUDPSherbrookeClient udpClient=new DRMSUDPSherbrookeClient(this, durationString);
			Thread udpClientThread=new Thread();
			log(getNonReturnersString.toString());
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return(getNonReturnersString.toString());
	}
	/*DESC:LocalFunction that is used by the Get Non Returners to get the Non Returners in the Local server
	PARAM1:duration describing the integer
	Return: the String containing the information about the Non Returners in Local Library server
*/

	public String getNonReturnersLocal(int duration)
	{
		printStudentMap();
		StringBuilder _detailsOfNonReturners=new StringBuilder();
		_detailsOfNonReturners.append("sherbrooke:  ");
		for(Iterator it=hash.entrySet().iterator();it.hasNext();)
		{
			Map.Entry entry=(Map.Entry)it.next();
			Character ch=(Character)entry.getKey();
			System.out.println("key is "+ch+"     ");
			ArrayList<StudentCredentials> al=(ArrayList<StudentCredentials>) entry.getValue();
			for(StudentCredentials s:al)
			{
				synchronized(s)
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
			
		}
		System.out.println(_detailsOfNonReturners);
		return _detailsOfNonReturners.toString();
	 }
	
	//--------------------------------------------Function to create the log file----------------------------
	/*DESC:Function to write the Log in the text file
	  
*/
	public void log(String logString)
	{
		try
		{
		File file = new File("SherbrookeLog.txt");
		 
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(logString+" at "+Calendar.getInstance().getTime());
		bw.newLine();
		bw.close();

		

	} catch (IOException e) {
		e.printStackTrace();
	}
	}

	

	/*DESC:Function to Set Duration For Specific Student
	PARAM1:Student Id of the Student
	PARAM2:The No. of Days in Integer that set the duration for specific student to mentioned value
	Return: Return True or False on the basis weather the operation was successful or not 
*/
	@Override
	public boolean setDuration(String _student, int _noOfDays) 
	{
		
			boolean _studentHasReserverBook=false;
			for(Iterator it=hash.entrySet().iterator();it.hasNext();)
			{
				Map.Entry entry=(Map.Entry)it.next();
				Character ch=(Character)entry.getKey();
				System.out.println("key is "+ch+"     ");
				ArrayList<StudentCredentials> al=(ArrayList<StudentCredentials>) entry.getValue();
				for(StudentCredentials _studentObject:al)
				{
					synchronized(_studentObject)
					{
					if(_studentObject.username.equals(_student))
					 {
						ArrayList<BooksAndDuration> _toSetDuration=_studentObject.books_duration;
						for(BooksAndDuration _bookAndDate:_toSetDuration)
						{
							if(_bookAndDate.g_bookName!=null)
							{
								_bookAndDate.g_returnDate=_noOfDays;
								_studentHasReserverBook=true;
								log("set Duration by admin "+_student+" "+_noOfDays);
							}
						}
					 }
					}
				}
				
			}
			printStudentMap();
			return _studentHasReserverBook;
		}
	//------------------------------------------------------Reserve Inter Library Function------------------------------------------

	/*DESC:Function to ReservetheBook for Specific Student in the Remote Library 
	PARAM1:username of the student who want to reserve the book
	PARAM2:Password of the account of the student 
	PARAM3:Name of the Book which the Student Want to reserve
	RETURN:True Or False on the Basis of weather Operation was Successful or Not
	*/

	@Override
	public boolean reserveInterLibrary(String _username, String _password, String _bookname, String _authorname)
	{
		Boolean _loginCredentialCheck;
		//_loginCredentialCheck=studentLogin(_username,_password);
		DatagramSocket socket=null;
		InetAddress ip;
		String _bookString="reserve "+_username+" "+_bookname+" "+_authorname;
		DatagramPacket request=null;
		DatagramPacket reply=null;
		byte b[]=_bookString.getBytes();
		boolean atomicity;
				//if(_loginCredentialCheck)
				//{
					if(reserveBook(_username,_bookname))
					{
						return true;
					}
					else 
					{
						
						try
						{
							
							ip=InetAddress.getByName("localhost");
							socket=new DatagramSocket();
							atomicity=false;
							String acknowledgement;
							int serverPort=5000;
							request=new DatagramPacket(b,b.length,ip,serverPort);
							socket.send(request);
							byte buffer[]=new byte[5000];
							reply=new DatagramPacket(buffer,buffer.length);
							socket.receive(reply);
							System.out.println("The Reply Recieved from the conocrdia"+new String(reply.getData()).trim()+"**");
							if((new String(reply.getData()).trim()).equals("true"))
							{
								
									atomicity=reservedBookInRemoteLibrary(_username,_bookname);
									acknowledgement=new String("acknowledgement "+_bookname+" "+Boolean.toString(atomicity));
									b=acknowledgement.getBytes();
									request=new DatagramPacket(b,b.length,ip,serverPort);
									socket.send(request);
									if(atomicity==true)
									{
										return true;
									}
									else
									{
										return false;
									}
							}
							else
							{
								socket=new DatagramSocket();
								atomicity=false;
								serverPort=6001;
								request=new DatagramPacket(b,b.length,ip,serverPort);
								socket.send(request);
								reply=new DatagramPacket(buffer,buffer.length);
								socket.receive(reply);
								System.out.println("The Reply Recieved from the Mcgill"+new String(reply.getData()).trim()+"**");
								if((new String(reply.getData()).trim()).equals("truee"))
								{
										System.out.println("The Reply Recieved  true from  the McGill");
										atomicity=reservedBookInRemoteLibrary(_username,_bookname);
										acknowledgement=new String("acknowledgement "+_bookname+" "+Boolean.toString(atomicity));
										b=acknowledgement.getBytes();
										request=new DatagramPacket(b,b.length,ip,serverPort);
										socket.send(request);
										if(atomicity==true)
										{
											return true;
										}
										else
										{
											return false;
										}
								}
							
							
						   }
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				//}
				 
					return false;
			
			
		
		
	}
	
	/*DESC:Function used to Add Or Reserve The Particular Book to THe Specific Student orUser
	PARAM1:username for which the book has to be reserved
	PARAM2:bookName the Name of the book that is to be Reserved
	RETURN:Trueor False on the Basis the Weather The Book wsa adddedto the sStudent or Not 
	*/
	public boolean reservedBookInRemoteLibrary(String _username,String _bookname)
	{
		Character _keyStudent=_username.charAt(0);
		ArrayList<StudentCredentials> _student=(ArrayList)hash.get(_keyStudent);
		synchronized(hash.get(_keyStudent))
		{
		for(StudentCredentials user:_student)
		{
			System.out.println("----------------------------------------------------------"+user.username);
			if(_username.equals("test"))
			{
				return false;
			}
			if((_username.equals(user.username)))
			{
				user.books_duration.add(new BooksAndDuration(_bookname,14));
				bookDetailsPrint();
				return true;
			}
		}
		return false;
	}
	}


	//----------------------------------------------------Reserve Book Inter Library local -----------------------------------
	
	/*DESC:Describe Weather the Book Can be Reserved to the Particular Student or Nor in The Local LiBrary Server
	PARAM1:The NAme of the Student To Which the Book Is to Be reserved
	PARAM2:the Name of the Book
	PARAM3:The NAme of the author
	RETURN:Return the value True or False on The Basis Weather The Operation was Successful or not
	*/
	public boolean reserveBookInterLibrary(String _userName, String _bookName,String _authorName) 
	{
		//Character _keyStudent=_userName.charAt(0);
		Character _keyBook=_bookName.charAt(0);
		boolean _flagBookExists=false;
		log(_userName+"&&&&&&&&&&&&&&&& Trying to Reserve the Book "+_bookName+" Remotely");
		synchronized (book.get(_keyBook))
		{
		ArrayList<BookDetails>_bookExists=book.get(_keyBook);
		for(BookDetails _bookReference:_bookExists)
		{
			System.out.println("***********************"+_bookReference.m_bookName);
			
			if(_bookReference.m_bookName.equals(_bookName))
			{		
				System.out.println("In The If block");
				if(_bookReference.m_noOfCopies>0)
				{
					
				log("reserve Book "+_userName+" "+_bookName);
				_flagBookExists=true;
				_bookReference.m_noOfCopies-=1;
				return true;
				
				}
			}
			
		}
		}
		return false;


	}
	//------------------------------Function to increase the book count when acknowledgement is false---------------
	/*DESC:The Function To Increament the Count of The Book if there occurs some Error While adding the Book to the specific student 
	PARAM1:The Name of the Book of Wich the Count is to bes increased
*/	public void increamentTheBookCount(String bookName)
	{
		Character _keyBook=bookName.charAt(0);
		
		ArrayList<BookDetails>_bookExists=book.get(_keyBook);
		synchronized (book.get(_keyBook))
		{
		
		for(BookDetails _bookReference:_bookExists)
		{
			System.out.println("***********************"+_bookReference.m_bookName);
			
			if(_bookReference.m_bookName.equals(bookName))
			{				
				_bookReference.m_noOfCopies+=1;
				System.out.println("The Book Count Has been Increased "+bookName);
				log("The Book Count Has been Increased "+bookName);
				
			}
		}
			
		}
		bookDetailsPrint();
		
		
	}

}
