package DRMSIDL;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ServerThread extends Thread 
{
	DatagramSocket socket=null;
	private int duration;
	DatagramPacket request;
	Map<Character,ArrayList<StudentCredentials>> hash;
	String nonreturners;
	static int portNumber=2;
	String _instituteName;
	public ServerThread(int duration ,DatagramPacket request,Map<Character,ArrayList<StudentCredentials>> hash,String _instituteName ,int port)
	{
	
		try {
			socket= new DatagramSocket(port);
			portNumber++;
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		this.hash=hash;
		this.duration=duration;
		this.request=request;
		this.hash=hash;
		this._instituteName=_instituteName;
		
	}
	public ServerThread(int duration ,DatagramPacket request,Map<Character,ArrayList<StudentCredentials>> hash,String _instituteName )
	{
	
		try {
			socket= new DatagramSocket();
			portNumber++;
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		this.hash=hash;
		this.duration=duration;
		this.request=request;
		this.hash=hash;
		this._instituteName=_instituteName;
		
	}
	public void run()
	{
		String reply;
		//DRMSServer ServerObject= new Server();
		//ServerObject.getNonReturners();
		
		


			    byte[] nonreturenrsByte = new byte[1000];
			    try {
			        // Get client request
			       // DatagramPacket request = new DatagramPacket(nonreturenrs,nonreturenrs.length);
			        //socket.receive(request);

			        // Retrieve definition from dictionary file
			       /* if(word != null) 
			            definition = getDefinition(new String(word), dictionaryFile);
*/
			        // Put reply into packet, send packet to client
			    	nonreturners=getNonReturnersLocal(duration,_instituteName);
			    	nonreturenrsByte=nonreturners.getBytes();
			        DatagramPacket Reply = new DatagramPacket(nonreturenrsByte, nonreturenrsByte.length, request.getAddress(), request.getPort());
			        socket.send(Reply);
			        socket.disconnect();
			        socket.close();
			        System.out.println(nonreturners);

			    }
			    catch (Exception e) 
			    {
			        System.out.println("Error: " + e.getMessage());
			    }

			    System.out.println(Thread.currentThread().getName() + " just run.");
			    //DictServer.decNumThreads();
	}
	public String getNonReturnersLocal(int duration, String institute)
	{
		//printStudentMap();
		StringBuilder _detailsOfNonReturners=new StringBuilder();
		_detailsOfNonReturners.append(institute+":  ");
		for(Iterator it=hash.entrySet().iterator();it.hasNext();)
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
	

