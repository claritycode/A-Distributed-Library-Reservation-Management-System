import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class SequencerUDPHandlerThread extends Thread
{
	
	SequencerMain object;
	DatagramPacket request;
	ByteArrayInputStream bais;
	ObjectInputStream ois;
	byte array[];
	InetAddress ip;
	ByteArrayOutputStream baos;
	ObjectOutputStream oos;
	public SequencerUDPHandlerThread (SequencerMain object,DatagramPacket request)
	{
		this.object=object;
		this.request=request;
		
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
			if(recievedObject instanceof  ClientCall)
			{
			ClientCall newObject=(ClientCall) recievedObject;
			
			synchronized(object._sequenceNumber)
			{
			newObject.setSequenceNumber(++object._sequenceNumber);
			
			}
			oos.writeObject(newObject);
			array=baos.toByteArray();
			sendToReplica(array);
			}
			else if(recievedObject instanceof  createAccountCall)
			{
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
				ClientCall newObject=(setDurationCall) recievedObject;
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
	


