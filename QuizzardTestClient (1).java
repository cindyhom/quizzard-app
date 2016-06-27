import java.net.*;
import java.io.*;


public class QuizzardTestClient {

	static Socket clientSocket;
	static int portNumber;
	private static ObjectOutputStream out;
	private  static ObjectInputStream in;
	private static String line = "";
	public static String outputfile = "";
	
	
	public static void main (String args[]) throws IOException, ClassNotFoundException
	{
		portNumber = Integer.parseInt(args[0]);
		connect();
		System.out.println("Exited Connect");
		setUpStreams();
		System.out.println("Exited Set Up Streams");
		messageToServer(args[1]);
		recieveMessage();
		clientSocket.close();
	}
	
	public static void connect() throws IOException
	{
		System.out.println("In connect()");
		clientSocket = new Socket("54.174.83.66", portNumber);
		System.out.println("Bottom of connect()");
	}
	
	public static void setUpStreams() throws IOException
	{	
		System.out.println("In setUpStreams");
		in = new ObjectInputStream(clientSocket.getInputStream());
		System.out.println(in.getClass());
		out = new ObjectOutputStream(clientSocket.getOutputStream());
		out.flush();
		System.out.println("Bottom of setUpStreams");
	}
	public static void messageToServer(String message) throws IOException
	{
		out.writeObject(message);
	}
	
	public static void recieveMessage() throws ClassNotFoundException, IOException
	{
		boolean check = true;
		while (check)
		{

			line = (String) in.readObject();
			
			
			if (line.equals("endchar"))
			{
				check = false;
			}
			else 
				outputfile = outputfile + "\n" + line;

		}

		System.out.println(outputfile);
	}
	

}
