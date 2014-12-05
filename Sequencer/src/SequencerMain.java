import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class SequencerMain extends Thread implements Serializable
{
	Integer _sequenceNumber=0;// the Sequence No. that the sequencer is maintaing to gurantee Total Order
	DatagramSocket _sequencerSocket=null;//Socket to accept the values fro the client i.e. the UDP packets fro the clients and the FrontENd
	SequencerMain object;
	DatagramPacket request;
	ByteArrayInputStream bais;
	ObjectInputStream ois;
	byte array[];
	InetAddress ip;
	ByteArrayOutputStream boas;
	ObjectOutputStream oos;
	
	int portNumberOfReplica1=5222;
	int portNumberOfReplica2=5223;
	int portNumberOfReplica3=5224;
	//SequencerUDPHandlerThread []ThreadArray;
	public void run()
	{
		int i=0;
		byte recieveRequestArray[]=new byte[1000];
		//ThreadArray= new SequencerUDPHandlerThread[10];
		System.out.println("Running the Sequencer main Class");
		try
		{
			_sequencerSocket = new DatagramSocket(9988);
		
		while(true)
		{
			
				
				System.out.println("In the while loop of the sequencer main class");
			
			DatagramPacket request= new DatagramPacket(recieveRequestArray,recieveRequestArray.length);//creating the packet and recieving the request from the Front End
			_sequencerSocket.receive(request);//Recieving the request
			System.out.println("The Request recieved ");
			SequencerUDPHandlerThread newThread= new SequencerUDPHandlerThread(this, request);//Creating tyhe Thread to Handle The Request
			newThread.start();	//starting the new Thread
			//ThreadArray[i]= new SequencerUDPHandlerThread(this,request);
			//Thread newThread= new Thread(ThreadArray[i]);
			//newThread.start();
			//ThreadArray[i].start();
			/*i++;
			System.out.println("The Request  forwarded to the Thread");*/
		}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	


	
	public static void main(String args[])
	{
		SequencerMain _startingObject= new SequencerMain();
		_startingObject.start();
		
	}

}
