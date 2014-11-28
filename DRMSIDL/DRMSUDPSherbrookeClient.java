package DRMSIDL;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class DRMSUDPSherbrookeClient
{
	DRMSSherbrooke server;
	String duration;
	DRMSUDPSherbrookeClient(DRMSSherbrooke server,String duration)
	{
		this.server=server;
		this.duration=duration;
		
	}
	public void run()
	{
		DatagramSocket socket=null;
		try
		{
	
	InetAddress ip=InetAddress.getByName("localhost");
	socket=new DatagramSocket(7000);
	String durationString=duration+" ";
	byte b[]=durationString.getBytes();
	int serverPort=5000;
	DatagramPacket request=new DatagramPacket(b,b.length,ip,serverPort);
	socket.send(request);
	byte buffer[]=new byte[5000];
	DatagramPacket reply=new DatagramPacket(buffer,buffer.length);
	socket.receive(reply);
	 server.getNonReturnersString.append(new String(reply.getData()));
	System.out.println("reply recieved"+new String(reply.getData()));
	//InetAddress ip=InetAddress.getByName("localhost");
	//socket=new DatagramSocket(5001);
	//String durationString=duration+" ";
	//byte b[]=durationString.getBytes();
	 serverPort=6001;
	 request=new DatagramPacket(b,b.length,ip,serverPort);
	socket.send(request);
	buffer=new byte[5000];
	reply=new DatagramPacket(buffer,buffer.length);
	socket.receive(reply);
	 server.getNonReturnersString.append(new String(reply.getData()));
	System.out.println("reply recieved"+new String(reply.getData()));
	//_detailsOfNonReturnersFinal.append(_detailsOfNonReturners);
	socket.close();
	

}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
}
}