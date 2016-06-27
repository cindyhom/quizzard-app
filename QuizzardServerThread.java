import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class QuizzardServerThread implements Runnable{
	static Socket mySocket;
	static String command;
	public static ObjectOutputStream out;
	public static ObjectInputStream in; 
	
	QuizzardServerThread(Socket socket)
	{
		mySocket = socket;
	}
	
	public void run()
	{
		try {
			setUpStreams();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			readCommand();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setUpStreams() throws IOException
	{
		out = new ObjectOutputStream(mySocket.getOutputStream()); 
		out.flush();
		in = new ObjectInputStream(mySocket.getInputStream());
	}
	
	public static void readCommand() throws Throwable
	{
		String inputCommand;
		if ((inputCommand = (String) in.readObject()) != null)
		{
			command = inputCommand;
			System.out.println("Command recieved: " + command);
		}
		
		String[] mapReduceArguments = new String[3];

		if (command.startsWith("t"))
		{
			System.out.println("running tagcounter");
			mapReduceArguments[0] = "tagcounter";
			mapReduceArguments[1] = "QuizApp/mary.txt";
			Driver myDriver = new Driver();
			myDriver.run(mapReduceArguments);
			sendFile();
		}
		
		else if (command.startsWith("s"))
		{
			mapReduceArguments[0] = "quizsearcher";
			mapReduceArguments[1] = "QuizApp/mary.txt";
			mapReduceArguments[2] = command.substring(1);
			Driver myDriver = new Driver();
			myDriver.run(mapReduceArguments);
			sendFile();
		}
		else if (command.equalsIgnoreCase("close"))
		{
			mapReduceArguments[0] = "close";
			Driver myDriver = new Driver();
			myDriver.run(mapReduceArguments);		
			in.close();
			out.close();
			mySocket.close();
		}
		else if (command.startsWith("create:"))
		{
			System.out.println("IN CREATE");
			CharSequence quizToAdd = command.substring(7);
			System.out.println(quizToAdd);
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", "hdfs://localhost:9000");
			FileSystem fs = FileSystem.get(conf);
			Path path = new Path("QuizApp/mary.txt");
			FSDataOutputStream fsOutput = fs.append(path);
			System.out.println(fsOutput);
			PrintWriter pw = new PrintWriter(fsOutput);
			pw.append("\n" + quizToAdd);
			out.writeObject("Added " + quizToAdd + " to the file \n");
            out.writeObject("endchar");
			pw.close();
			fs.close();
		}

	}
	public static void sendFile(){
        try{
        	System.out.println("In sendFile()");

            Path path = new Path("quizoutput/part-r-00000");
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://localhost:9000");
            FileSystem fs = FileSystem.get(conf);
        	System.out.println("TEST PATH : " + fs.exists(path));
            System.out.println("READFILE OUTPUT: " + path);
            BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(path)));
            String line;
            line=br.readLine();
            while (line != null){
                    out.writeObject(line);
                    System.out.println(line);
                    line=br.readLine();
            	}
            out.writeObject("endchar");
        }catch(Exception e){
        		e.printStackTrace(System.out);
        }
	}
}
