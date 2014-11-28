import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class MainClass 
{
	
	public static void main (String args[])
	{
		//DummyClass dc= new DummyClass(1,"hello");
		//byte array[];
		try
		{
			for(int i=0;i<10;i++)
			{
		ThreadClassToSendTheRequest threadClassObject=new ThreadClassToSendTheRequest(i,"hello"+i);
		threadClassObject.start();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
