package DRMSIDL;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.omg.CORBA.ORB;


public class DRMSStudent extends Thread 
{
	DRMS _serverConcordia;// the Object of the Concordia Server
	DRMS _serverSherbrooke;// the Object Of the Sherbrooke Server
	DRMS _serverMcgill; // the Object of the Mcgill server
	//----------------------------------------costructor to initialize the objects of the server--------------------------------
	/*DESC:The Constructor of the DRMS Student Class Used for Initailizing the Orb
	 * And NArrowing the Java Reference from the Corba Object and initializing the Objects of the Severs with
	 *  The Narrowed JAva extension 
	 */
	public DRMSStudent() 
	{
		//super();
		try
		{
			String args[]=null;
			ORB orb=ORB.init(args,null);
			BufferedReader br = new BufferedReader(new FileReader("iorConcordia.txt"));
			String iorConcordia=br.readLine();
			br.close();
			org.omg.CORBA.Object objectConcordia=orb.string_to_object(iorConcordia);
			_serverConcordia=DRMSHelper.narrow(objectConcordia);
			
			br = new BufferedReader(new FileReader("iorSherbrooke.txt"));
			String iorSherbrooke=br.readLine();
			br.close();
			org.omg.CORBA.Object objectSherbrooke=orb.string_to_object(iorSherbrooke);
			_serverSherbrooke=DRMSHelper.narrow(objectSherbrooke);
			
			
			br = new BufferedReader(new FileReader("iorMcgill.txt"));
			String iorMcgill=br.readLine();
			br.close();
			org.omg.CORBA.Object objectMcgill=orb.string_to_object(iorMcgill);
			_serverMcgill=DRMSHelper.narrow(objectMcgill);
		/*_serverConcordia=(DRMSServerInterface)Naming.lookup("rmi://localhost:5000/concordia");
		_serverMcgill=(DRMSServerInterface)Naming.lookup("rmi://localhost:6000/mcgill");
		_serverSherbrooke=(DRMSServerInterface)Naming.lookup("rmi://localhost:7000/sherbrooke");*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	//int i=1;
	/*public DRMSStudent(String institute,int port)throws RemoteException
	{
		try
		{
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}*/
	//---------------------run function of the thread to create account of the students to the specific server-------------------------------------------------------------------
		public void run ()
		{
			try
			{
				//DRMSServerInterface Server=(DRMSServerInterface)Naming.lookup("rmi://localhost:5000/concordia");
				int random=(int)(Math.random()*20);
				/*_serverConcordia.createAccount("student"+random,"last"+random,"email@emil.com"+random,"username"+random,"username"+random,"concordia",5145414+random);
				_serverConcordia.reserveInterLibrary("username"+random,"username"+random, "book"+random, "author"+random);
				_serverMcgill.createAccount("student"+random,"last"+random,"email@emil.com"+random,"username"+random,"username"+random,"concordia",5145414+random);
				_serverMcgill.reserveInterLibrary("username"+random,"username"+random, "book"+random, "author"+random);
				_serverSherbrooke.createAccount("student"+random,"last"+random,"email@emil.com"+random,"username"+random,"username"+random,"concordia",5145414+random);
				_serverSherbrooke.reserveInterLibrary("username"+random,"username"+random, "book"+random, "author"+random);*/
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
			/*DESC: function to Show the Welcome Message to the student on The Welcome Screen*/
		
		public  void showMenuWelcome()
		{
		System.out.println("\n****Welcome to Distribute Reservation Management System****\n");
		System.out.println("Please select an option (1-4)");
		System.out.println("1. Sign Up");
		System.out.println("2. Login");
		System.out.println("3. Admin Login");
		System.out.println("4. Quit");
		}
		
		/*DESC: function to Show the Login Menu Message to the student on Startup */
		public  void showMenuLogin(String _username)
		{
		System.out.println("\n****Welcome "+_username+"****\n");
		System.out.println("Please select an option 1 for :");
		System.out.println("1. Reserver Book");
		
		}
		
		
		//---------------------------------------Function for creating the account----------------------
		/*DESC:Function To Create the Account for The Student
		 * The User Input Is taken and The If theStudent UserNAme is Unique Then The Account for that User Is Created 
		  
		*/
		public void studentCreateAccount()
		{
			
			try
		
			{
				String m_institute;	//m_institute to know the name of institute to which server wants to connect
				int m_portNo;		//m_portNo to know the Port No. of institute to which server wants to connect
				
			int _check=0;
			BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
			/*System.out.println("Please enter the name of the institute which You Want to Connect");
			m_institute=br.readLine();
			System.out.println("Please enter the Port Number of the institute which You Want to Connect");
			m_portNo=Integer.parseInt(br.readLine());*/
			
			
			String _first_name;
			String _last_name;
			String _email;
			String _username;
			String _password;
			int	_phone_no;
			String _institute;
			boolean _continue_flag=true;
			while(_continue_flag)
			 {
			//Reading the Student Credentials From the student From the Console These Include The User's first Name , Last Name, email Address, 
			//	Username ,Password The educational Institute to which It want to Make The account and the Phone Number
				
				
			System.out.println("please enter Your First Name:");
			_first_name=br.readLine();						//Reading the first Name for the User From The console
			System.out.println("please enter Your Last Name:");
			_last_name=br.readLine();						//Reading the Last Name for the User From The console
			System.out.println("please enter Your Email:");
			_email=br.readLine();							//Reading the Email for the User From The console
			System.out.println("please enter Your Username:");
			_username=br.readLine();						//Reading the UserName for the User From The console
			System.out.println("please enter Your Password:");
			_password=br.readLine();						//Reading the Password for the User From The console
			System.out.println("please enter Your Institute:");
			_institute=br.readLine();						//Reading the Institute Name for the User From The console
			System.out.println("please enter Your Phone Number:");
			_phone_no=(Integer.parseInt(br.readLine()));	//Reading the Phone Number for the User From The console
			
			if(_institute.equalsIgnoreCase("concordia"))
			{
				
				//DRMSServerInterface Server=(DRMSServerInterface)Naming.lookup("rmi://localhost:5000/concordia");
				_check=_serverConcordia.createAccount(_first_name,_last_name,_email,_username,_password,_institute,_phone_no);
				log(_username," Tried to create account at concordia");//Creating the Log That Account has been Created for the Concordia LIbrary Student
				
				
			}
			else if(_institute.equalsIgnoreCase("mcgill"))
			{
				_check=_serverMcgill.createAccount(_first_name,_last_name,_email,_username,_password,_institute,_phone_no);
				log(_username,"Tried to create account at Mcgill");//Creating the Log That Account has been Created for the Mcgill LIbrary Student
			}
				
			else if(_institute.equalsIgnoreCase("sherbrooke"))
			{
				_check=_serverSherbrooke.createAccount(_first_name,_last_name,_email,_username,_password,_institute,_phone_no);
				log(_username,"Tried to create account at sherbrooke");//Creating the Log That Account has been Created for the Sherbrooke LIbrary Student
			}
			switch(_check)
			 {
			case 1:
					System.out.println("username does not lie between 6 to 15 characters");//Display the Message That the UserName Does not lieBetween 6 to 15 characters
					log(_username,"username does not lie between 6 to 15 characters");
					break;
			case 2:
					System.out.println("password has less than  6 characters");//Display the Message That the password Does not have 6  characters
					log(_username,"password has less than  6 characters");
					break;
			case 3:
					System.out.println("username alrady exists");//Display the Message That the username alrady exists
					log(_username,"username alrady exists");
					break;
			case 4:
					System.out.println("Sign up Successful");//Display the Message That the UserName has Successfully Signed Up
					log(_username,"Sign up Successful");
					_continue_flag=false;
					break;
					
			default:
				    System.out.println("Error with the function Create Account at the Server Side");//Display the Message That the UserName Does not lieBetween 6 to 15 characters
				    break;
			  }
			 }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		//-------------------------------------------Function for Login of the student ----------------------------------
		
		
		/*DESC:Function for Student to Login  
		Check The Student Credentails weather all Are correct and then login the Student on those Basis*/
		public  void login ()
		{
			try
			{
			String _userName="",_password="",_institute="",_bookName,_authorName="";//variables for the Credentials of The student
			BufferedReader br= new BufferedReader(new InputStreamReader(System.in));// Buffered reader to take the Input from the Console From the User 
			boolean _continueFlag=false;											//The Flag is False in Intial and the it is made True				
			//DRMSServerInterface _serverConcordia=(DRMSServerInterface)Naming.lookup("rmi://localhost:5000/concordia");
			while((_continueFlag)==false)
			{
			System.out.println("please enter Your Username:");
			_userName=br.readLine();						// taking the Input from the user as its UserName
			System.out.println("please enter Your Password:");
			_password=br.readLine();						//The The Input from the User IT's Password
			System.out.println("please enter Your Institute:");
			_institute=br.readLine();						//The The Input from the User IT's Institute Name
			
			if(_institute.equalsIgnoreCase("concordia"))
			{
			_continueFlag=_serverConcordia.studentLogin(_userName,_password); // Invoking The Function on the Object of the Concordia Library server For Login
			log(_userName,"login Concordia");									//Writting in the log File for the Concordia Library server
			}
			else if(_institute.equalsIgnoreCase("mcgill"))
			{
			_continueFlag=_serverMcgill.studentLogin(_userName,_password);	// Invoking The Function on the Object of the Mcgill Library server For Login
			log(_userName,"login Mcgill");									//Writting in the log File for the Mcgill Library server
			}
			//System.out.println("Invalid Username/Password");
			else if(_institute.equalsIgnoreCase("sherbrooke"))		// Invoking The Function on the Object of the Sherbrooke Library server For Login
			{
				_continueFlag=_serverSherbrooke.studentLogin(_userName,_password);
				log(_userName,"login Sherbrooke");					//Writting in the log File for the Sherbrooke Library server
			}
					
			}
			boolean _flagForWhile=true;
			int choiceForWhile;
			while(_flagForWhile)
			{
			System.out.println("please enter 1 to reserve Book locally");
			System.out.println("please enter 2 to reserve Book Remotely");
			try
			{
				choiceForWhile=(Integer.parseInt(br.readLine()));				//Taking the input from the User Weather It wants to reserve the Book Remotely or Locally
				System.out.println("Please enter the details for reserving book");
				log(_userName,"Login Successful");						//Writting in the log File for the  Library server
				switch(choiceForWhile)
				{
				case 1:
						_continueFlag=false;
						while(_continueFlag==false)
						{
							
							System.out.println("PLease Enter the Name of the Book");
							log(_userName,"reserving book");	//Writting in the log File for the  Library server
							_bookName=br.readLine();
							if(_institute.equalsIgnoreCase("concordia"))
							_continueFlag=_serverConcordia.reserveBook(_userName,_bookName);//Calling the reserveBook function on the Concordia server Object
							else if(_institute.equalsIgnoreCase("mcgill"))
							_continueFlag=_serverMcgill.reserveBook(_userName,_bookName);	//Calling the reserveBook function on the Mcgill server Object
							else if(_institute.equalsIgnoreCase("sherbrooke"))
								_continueFlag=_serverSherbrooke.reserveBook(_userName,_bookName);//Calling the reserveBook function on the Sherbrooke server Object
							if(_continueFlag)
							{
								System.out.println("Book Has Been Reserved");
								log(_userName," book reserved");
								_flagForWhile=false;
							}
							else
							{
								System.out.println("The Specified Book Cannot be Located");
								log(_userName,"The Specified Book Cannot be Located");
							}
						}
						break;
						
				case 2: 
						_continueFlag=false;
						System.out.println("PLease Enter the Name of the Book");
						log(_userName,"reserving book Remotely");
						_bookName=br.readLine();
						System.out.println("please Enter The Author Name");
						_authorName=br.readLine();
						System.out.println(_institute);
						if(_institute.equalsIgnoreCase("concordia"))
						{
							System.out.println("Value of the flag before the call is made For Specified Book--------------"+_bookName+" "+_continueFlag);
							_continueFlag=_serverConcordia.reserveInterLibrary(_userName,_password,_bookName,_authorName);
							System.out.println("Remote Response For Specified Book--------------"+_continueFlag);
						}
						else if(_institute.equalsIgnoreCase("mcgill"))
						{
							System.out.println("Value of the flag before the call is made For Specified Book--------------"+_bookName+" "+_continueFlag);
							_continueFlag=_serverMcgill.reserveInterLibrary(_userName, _password, _bookName, _authorName);
							System.out.println("Remote Response For Specified Book--------------"+_continueFlag);
						}
						else if(_institute.equalsIgnoreCase("sherbrooke"))
						{
							System.out.println("Value of the flag before the call is made For Specified Book--------------"+_bookName+" "+_continueFlag);
							_continueFlag=_serverSherbrooke.reserveInterLibrary(_userName, _password, _bookName, _authorName);
							System.out.println("Remote Response For Specified Book--------------"+_continueFlag);
						}
						if(_continueFlag)
						{
						System.out.println("Book Has Been Reserved");
						log(_userName," book reserved");
						_flagForWhile=false;
						}
						else
						{
						System.out.println("The Specified Book Cannot be Reserved");
						log(_userName,"The Specified Book Cannot be Located");
						 
						}
						break;
					
				}
				 
			
			}
			catch(NumberFormatException e)
			{
				_flagForWhile=true;
				e.printStackTrace();
			}
			}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		//------------------------------------------------Function For Admin  to a------------------------------------
		/*public void reserveBook( String _institute )
		{
			boolean _continueFlag=false;
			String _bookName,_authorName;
			try
			{
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		
			while(_continueFlag==false)
			{
				System.out.println("Login Has Been Successful please enter 1 to Reserve Book and 2 to get Non returners list");
				int choice=Integer.parseInt(br.readLine());
				switch(choice)
				{
					case 1:
									System.out.println("enter the name of the book");
									_bookName=br.readLine();
									System.out.println("enter the Author name of the book");
									_authorName=br.readLine();
									if(_institute.equalsIgnoreCase("concordia"))
									{
										_serverConcordia.reserveBook(_bookName,_authorName);
									}
								else if(_institute.equalsIgnoreCase("mcgill"))
									{
										_serverMcgill.reserveBook(_bookName,_authorName);
									}
								else if(_institute.equalsIgnoreCase("sherbrooke"))
									{
										_serverSherbrooke.reserveBook(_bookName,_authorName);
									}
									_continueFlag=true;
									if(_continueFlag)
										{
										System.out.println("Book Has Been Reserved");
										_continueFlag=true;
										}
				}
			}
			
		  }
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}*/
//-----------------------------------------------------Main Class--------------------------------------------------------
		
		public static void main(String[] args)
		{
			DRMSStudent studentObject=new DRMSStudent();
			
			BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
			
			boolean first_while_flag=true;
			int welcome_choice=0;
			try
			{
				//System.setSecurityManager(new RMISecurityManager());
				for(int i=0;i<10;i++)
					(new DRMSStudent()).start();
				//DRMS_serverConcordia _serverConcordia=(DRMSServer)Naming.lookup("rmi://localhost:5000/test");
			
			while(first_while_flag)
				{
				studentObject.showMenuWelcome();
				Boolean valid = true;
			
				// Enforces a valid integer input.
				while(valid)
					{
					try
					{
					welcome_choice=(Integer.parseInt(br.readLine()));
					valid=false;
					}
					catch(Exception e)
					{
					System.out.println("Invalid Input, please enter an Integer");
					valid=true;
					System.out.println();
					}
		
					}
				switch(welcome_choice)
				{
				case 1:			System.out.println("Welcome To Sign Up Process");
								studentObject.studentCreateAccount();
								
									break;
				case 2:			studentObject.login();
								/*while(server.loginCheck())
									{
										afterLoginOptions();
									}
									
								first_while_flag=false;*/
								break;
				case 3:			studentObject.adminLogin();
								break;
								
				case 4:			first_while_flag=false;
								break;
				default:		
								System.out.println("Please Enter The correct Choice");
					
				}

				}
			 }
			
			 catch(Exception e)
			 {
				e.printStackTrace();
			 }
			
		}
		

//----------------------------------------------------Function for Admin Login and  Add book at the server and get non returners list------------------------------------------------------
public  void adminLogin()
 {
			try
				{
					String _userName="",_password,_institute="",_bookName,_authorName;
					BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
					boolean _continueFlag=false;
					while((_continueFlag)==false)
						{
							System.out.println("please enter Your Username:");
							_userName=br.readLine();
							System.out.println("please enter Your Password:");
							_password=br.readLine();
							System.out.println("please enter the name of the Institute:");
							_institute=br.readLine();	
							log(_userName,"admin Login");
							if(((_userName.equalsIgnoreCase("admin"))&&(_password.equalsIgnoreCase("admin")))&&((_institute.equalsIgnoreCase("concordia"))||(_institute.equalsIgnoreCase("mcgill"))||(_institute.equalsIgnoreCase("sherbrooke"))))
								{
									while(_continueFlag==false)
									{
										System.out.println("Login Has Been Successful please enter 1 to Add Book and 2 to get Non returners list and 3 to Set Duration");
										int choice=Integer.parseInt(br.readLine());
										switch(choice)
										{
											case 1:
															System.out.println("enter the name of the book");
															_bookName=br.readLine();
															System.out.println("enter the Author name of the book");
															_authorName=br.readLine();
															/*System.out.println("enter the Author name of the book");
															_authorName=br.readLine();*/
															if(_institute.equalsIgnoreCase("concordia"))
															{
															_continueFlag=_serverConcordia.addBook(_bookName,_authorName);
															if(_continueFlag)
															log(_userName,"add the book at concordia "+_bookName+" "+_authorName);
															}
															else if(_institute.equalsIgnoreCase("mcgill"))
															{
																log(_userName,"add the book at Mcgill "+_bookName+" "+_authorName);
																if(_continueFlag)
																_continueFlag=_serverMcgill.addBook(_bookName,_authorName);
															}
															else if(_institute.equalsIgnoreCase("sherbrooke"))
															{
																log(_userName,"add the book at sherbrooke"+_bookName+" "+_authorName);
																if(_continueFlag)
																_continueFlag=_serverSherbrooke.addBook(_bookName,_authorName);
															}
															if(_continueFlag)
																{
																//System.out.println(_continueFlag+"------");
																System.out.println("Book Has Been Reserved");
																log(_userName,"Book Has Been Reserved");
																}
															else
															{
																System.out.println("The Specified Book Cannot be Located");
																log(_userName,"The Specified Book Cannot be Located");
															}
																break;
											
											case 2:				System.out.println("Enter the Duration");
																boolean valid=true;
																while(valid)
																	{
																		try
																		{
																		int duration=Integer.parseInt(br.readLine());
																		getNonReturners(_institute,duration);
																		_continueFlag=true;
																		valid=false;
																		}
																		catch(Exception e)
																			{
																			System.out.println("Invalid Input, please enter an Integer");
																			valid=true;
																			System.out.println();
																			}
								
																	}
																break;
																
											case 3:				System.out.println("Enter the name of the student");
																String student=br.readLine();
																System.out.println("Enter the number of days");
																valid=true;
																while(valid)
																	{
																	try
																	{
																		int _numberOfDays=Integer.parseInt(br.readLine());
																		setDuration(student,_numberOfDays,_institute);
																		_continueFlag=true;
																		valid=false;
																	}
																	catch(Exception e)
																	{
																		System.out.println("Invalid Input, please enter an Integer");
																		valid=true;
																		System.out.println();
																	}
			
																	}
																break;
											
											default:			System.out.println("InValid Credential Entered");
						
										}
				
									}
									_continueFlag=true;
								}
							else
							{
								System.out.println("In valid Credentials :");
							}
						}
				}
	
						catch(Exception e)
						{
									e.printStackTrace();
						}
	
	}
//--------------------------------------function to get the list of non returners in the library---------------------------------------------
					public void getNonReturners(String institute,int duration)
					{
						String _nonReturnersDetails;
						if(institute.equalsIgnoreCase("concordia"))
						{
							try
							{
								System.out.println("request sent to the server");
								_nonReturnersDetails=_serverConcordia.getNonReturners(duration);
								log("admin",_nonReturnersDetails);
								System.out.println("Check the Log file for details");
							}
							catch (Exception e) 
							{
								e.printStackTrace();
							}
						}
						else if(institute.equalsIgnoreCase("mcgill"))
						{
							try
							{
								_nonReturnersDetails=_serverMcgill.getNonReturners(duration);
								log("admin",_nonReturnersDetails);
								System.out.println("Check the Log file for details");
							}
							catch (Exception e) 
							{
								e.printStackTrace();
							}
						}
						else if(institute.equalsIgnoreCase("sherbrooke"))
						{
							try
							{
								_nonReturnersDetails=_serverSherbrooke.getNonReturners(duration);
								System.out.println("Check the Log file for details");
								log("admin",_nonReturnersDetails);
							}
							catch (Exception e) 
							{
								e.printStackTrace();
							}
						}
		
					}
//--------------------------------------------function to set duration for the specific student ---------------------------------------
					public void setDuration(String student,int _mNoOfDays,String _institute )
					{
						try
						{
						boolean _continueFlag=false;
						if(_institute.equalsIgnoreCase("concordia"))
							_continueFlag=_serverConcordia.setDuration(student,_mNoOfDays);
							else if(_institute.equalsIgnoreCase("mcgill"))
							_continueFlag=_serverMcgill.setDuration(student,_mNoOfDays);
							else if(_institute.equalsIgnoreCase("sherbrooke"))
								_continueFlag=_serverSherbrooke.setDuration(student,_mNoOfDays);
							if(_continueFlag)
								{
								System.out.println(_continueFlag+"------");
								log("admin",student+"duration updated to"+_mNoOfDays);
								System.out.println("Student details Updated Successfully");
								}
							else
							{
								System.out.println("The Specified student cannot be located ");
								log("admin",student+"The Specified student cannot be located ");
							}
								
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					
					public void log(String student,String logString)
					{
						try
						{
						File file = new File(student+".txt");
						 
						// if file doesnt exists, then create it
						if (!file.exists()) {
							file.createNewFile();
						}

						FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(logString);
						bw.newLine();
						bw.close();


					} catch (IOException e) {
						e.printStackTrace();
					}
					}
}
