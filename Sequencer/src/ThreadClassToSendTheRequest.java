import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class ThreadClassToSendTheRequest extends Thread
{
	int n;
	String s;
	ThreadClassToSendTheRequest(int n, String s)
	{
		this.n=n;
		this.s=s;
	}
	public void run()
	{
		try
		{
		DummyClass dc= new DummyClass(n,s);
		byte array[];
		InetAddress ip= InetAddress.getByName("localhost");
		System.out.println("Running the Main Class");
		ByteArrayOutputStream boas= new ByteArrayOutputStream();
		ObjectOutputStream oos= new ObjectOutputStream(boas);
		oos.writeObject(dc);
		array=boas.toByteArray();
		DatagramPacket packet= new DatagramPacket(array,array.length,ip,9988);
		DatagramSocket socket= new DatagramSocket();
		socket.send(packet);
		}
	
	
	catch(Exception e)
	{
		e.printStackTrace();
	}
	}

}
