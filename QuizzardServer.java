import java.net.*;
import java.io.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

public class QuizzardServer {
	

	public static Socket clientSocket;
	public static ServerSocket serverSocket;
	

	static String command;
	
	public static void main (String[] args) throws Throwable
	{
		
		System.out.println("connecting");
		serverSocket = new ServerSocket(Integer.parseInt(args[0]));

		while (true)
		{
			try{
				connect(args);
				//setUpStreams();
				//readCommand(args);
			}
			catch(IOException i)
			{
				i.printStackTrace();
			}
			finally
			{
				//in.close();
				//out.close();
				//clientSocket.close();
				//serverSocket.close();
			}
			
		}
	}
		
		
	public static void connect(String[] args) throws NumberFormatException, IOException
	{
		clientSocket = serverSocket.accept();
		System.out.println("Connected to " + clientSocket.getInetAddress());
		QuizzardServerThread serverThread = new QuizzardServerThread(clientSocket);
		Thread T = new Thread(serverThread);
		T.start();
	}
}
	

	
	


