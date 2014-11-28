import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;


public class SequencerUDPHandlerThread extends Thread
{
	SequencerMain object;
	DatagramPacket request;
	ByteArrayInputStream bais;
	ObjectInputStream ois;
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
			bais = new ByteArrayInputStream(request.getData());
			ois= new ObjectInputStream(bais);
			Object recievedObject=ois.readObject();
			if(recievedObject instanceof  DummyClass)
					{
			System.out.println(ois.getClass() +"The class sent over the socket is");
					}
			DummyClass object=(DummyClass)recievedObject;
			System.out.println(object.a+"---------------"+object.s+"    "+this.getName());
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
	

}
