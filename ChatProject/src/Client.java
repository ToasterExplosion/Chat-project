import java.net.*;
import java.io.*;

public class Client implements Runnable{
	private Socket socket = null; //default is null so we don't need to make another constructor
	private Thread thread = null;
	private DataInputStream inStream = null; //where we put the messages
	private DataOutputStream outStream = null;
	private ClientThread clientThread = null;
	
	final String exitWord = ".exit";
	
	public Client(String serverName, int serverPort) {
		try { //set up the sockets and data streams so we can send the data
			socket = new Socket(serverName, serverPort);
			start();
		}
		catch(UnknownHostException uhe) {
			System.out.println("<<HOST UNAVAILABLE>>");
		}
		catch(IOException ioe) {
			//text
		}
		
		boolean exit = false;
		while(!exit) { //this checks if we want to quit
			try {
				String line = inStream.readUTF();//see what the message says
				outStream.writeUTF(line);
				outStream.flush();
				exit = line.equals(exitWord); //leave if we say the line
			}
			catch(IOException ioe) {
				System.out.println("<<MESSAGE COULD NOT BE SENT>>");
			}
		}
	}
	
	public void start() throws IOException{
		inStream = new DataInputStream(System.in);
		outStream = new DataOutputStream(socket.getOutputStream());
		if (thread == null) {
			clientThread = new ClientThread(this, socket);
	    	thread = new Thread(this);                   
	    	thread.start();
	    }
	}
	
	public void stop(){
		if(thread != null) {
			thread.stop();
			thread = null;
		}
		try {
			if (inStream != null) {
				inStream.close();
			}
			if (outStream != null) {
				outStream.close();
			}
			if (socket != null) {
				socket.close();
			}
		}
		catch(IOException ioe) {
			System.out.println("<<UNABLE TO CLOSE>>");
		}
		clientThread.close();
		clientThread.stop();
	}
	
	public void handle(String msg) throws IOException {
		if (msg.equals(exitWord)) {
			stop();
			System.out.println("<<YOU HAVE EXITED THE CHAT>>");
		}
		else {
			System.out.println("[YOU] " + msg);
		}
	}
	
	public void run() {
		while (thread != null) {
			try {
				outStream.writeUTF(inStream.readLine());
				outStream.flush();
			}
			catch(IOException ioe) {
				System.out.println("<<ERROR: COULD NOT SEND MESSAGE>>");
				stop();
			}
		}
	}
	
	public static void main(String args[]){//Presumably where the GUI goes
		Server server = null;	//idk what this is meant to do. If you have better setup for main method please add
	    if (args.length != 1)
	    	System.out.println("Ma'am this is a server port");
	    else
	    	server = new Server(Integer.parseInt(args[1]));
	}

}
