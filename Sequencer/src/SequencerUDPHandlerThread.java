import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class SequencerUDPHandlerThread extends Thread
{
	
	SequencerMain object;// the object of the Sequencer
	DatagramPacket request;// datagram packet to take the request from the Front End
	ByteArrayInputStream bais;// Byte Arry input Stream to be used to perform the Deserialization on the data  
	ObjectInputStream ois;// object input stream for creating the actual Deserialized object from the Byte arry Stream
	byte array[];	// the arry to be used to accept the data from the Front end
	InetAddress ip;		// IP ON WHICH THE REPLICAS ARE RUNNING 
	ByteArrayOutputStream baos;// Byte Array output stream  to serialize the objects 
	ObjectOutputStream oos;// Object output stream  to serialize the objects 
	public SequencerUDPHandlerThread (SequencerMain object,DatagramPacket request)
	{
		this.object=object;// initializing the instance variables 
		this.request=request;// intializing the datagram packet with the values of the costructor  
		
	}
	@Override
	public void run()
	{
		try
		{
			ip= InetAddress.getByName("localhost");
			baos= new ByteArrayOutputStream();
			oos= new ObjectOutputStream(baos);
			bais = new ByteArrayInputStream(request.getData());
			ois= new ObjectInputStream(bais);
			Object recievedObject=ois.readObject();
			
			 if(recievedObject instanceof  createAccountCall)
			{
				 //Code to perform the Deserialization on  the object  
					
					//input the fields such as the sequence number
					
					//serialize the object again
					
				ClientCall newObject=(createAccountCall) recievedObject;
				synchronized(object._sequenceNumber)
				{
				newObject.setSequenceNumber(++object._sequenceNumber);
				}
				oos.writeObject(newObject);
				array=baos.toByteArray();
				sendToReplica(array);
			}
			else if(recievedObject instanceof  getNonReturnersCall)
			{
				//Code to perform the Deserialization on  the object  
				
				//input the fields such as the sequence number
				
				//serialize the object again
				
				ClientCall newObject=(getNonReturnersCall) recievedObject;
				synchronized(object._sequenceNumber)
				{
				newObject.setSequenceNumber(++object._sequenceNumber);
				}
				oos.writeObject(newObject);
				array=baos.toByteArray();
				sendToReplica(array);
			}
			else if(recievedObject instanceof  reserveBookCall)
			{
				//Code to perform the Deserialization on  the object  
				
				//input the fields such as the sequence number
				
				//serialize the object again
				
				ClientCall newObject=(reserveBookCall) recievedObject;
				synchronized(object._sequenceNumber)
				{
				newObject.setSequenceNumber(++object._sequenceNumber);
				}
				oos.writeObject(newObject);
				array=baos.toByteArray();
				sendToReplica(array);
			}
			else if(recievedObject instanceof  setDurationCall)
			{
				//Code to perform the Deserialization on  the object  
				
				//input the fields such as the sequence number
				
				//serialize the object again
				
				ClientCall newObject=(setDurationCall) recievedObject;
				synchronized(object._sequenceNumber)
				{
				newObject.setSequenceNumber(++object._sequenceNumber);
				}
				oos.writeObject(newObject);
				array=baos.toByteArray();
				sendToReplica(array);
			}
			else if(recievedObject instanceof  ClientCall)
				{
				//Code to perform the Deserialization on  the object  
				
				//input the fields such as the sequence number
				
				//serialize the object again
				
				ClientCall newObject=(ClientCall) recievedObject;
				
				synchronized(object._sequenceNumber)
				{
				newObject.setSequenceNumber(++object._sequenceNumber);
				
				}
				oos.writeObject(newObject);
				array=baos.toByteArray();
				sendToReplica(array);
				}
			else if(recievedObject instanceof  ToSequencerPortNumber)
			{
				ToSequencerPortNumber newObject=(ToSequencerPortNumber) recievedObject;
				if(newObject.replicaModuleName.equalsIgnoreCase("harpreet"))
				{
					object.portNumberOfReplica1=newObject.portNumber;
				}
				if(newObject.replicaModuleName.equalsIgnoreCase("parth"))
				{
					object.portNumberOfReplica2=newObject.portNumber;
				}
				if(newObject.replicaModuleName.equalsIgnoreCase("gustavo"))
				{
					object.portNumberOfReplica3=newObject.portNumber;
				}
				
			}
			
			
			
			
			
			
	}	
			
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//synchronized(object._sequenceNumber)
		{
		//object._sequenceNumber++;
		  
		
		//Code to perform the Deserialization on  the object  
		
		//input the fields such as the sequence number
		
		//serialize the object again
		
		
		//DatagramPacket packetToReplica=new DatagramPacket(buf, length);
		}
		
	}
	public void sendToReplica(byte array[])
	{
		// multicasting the request to the replicas by creating the packets and forwarding the packets to the replica
		try
		{
		DatagramPacket packet1= new DatagramPacket(array,array.length,ip,object.portNumberOfReplica1);
		DatagramPacket packet2= new DatagramPacket(array,array.length,ip,object.portNumberOfReplica2);
		DatagramPacket packet3= new DatagramPacket(array,array.length,ip,object.portNumberOfReplica3);
		DatagramSocket socket= new DatagramSocket();
		socket.send(packet1);
		socket.send(packet2);
		socket.send(packet3);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
	


