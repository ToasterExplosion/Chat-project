import java.io.*;
import java.net.*;

public class Server implements Runnable{//Comments are for my own understanding
	
	private Thread thread;
	private ServerSocket serverSock;
	
	private int totalClients = 0;
	private ServerThread capacity[] = new ServerThread[10]; //This is how many clients we can have at once
	
	final String exitWord = ".exit"; //I have it as a variable in case I want to change it w/o hassle
	
	public Server(int port) {
		try { //needed because .accept() demands it
			serverSock = new ServerSocket(port);
			start();
		}
		
		catch(IOException ioe) {//try demands it, also general good practice
			System.out.println("<<ERROR: CANNOT SET UP HOST>>");
		}
	}
	public void stop() {
		if (thread != null) {
			thread.stop();
			thread = null;
		}
	}
	
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	private int getClientID(int ID) {
		for(int x = 0; x < totalClients; x++) {	//go thru list of clients to find one that matches the desired ID
			if(capacity[x].getID() == ID) {
				return x;
			}
		}
		return -1; //in case we have no clients
	}
	
	public synchronized void handle(int ID, String msg) { //the part that looks at the message
		if (msg.equals(exitWord)) {
			capacity[getClientID(ID)].sendMessage(exitWord);
			kick(ID);
		}
		else { //if it's not the exit condition just send to all
			for(int x = 0; x < totalClients; x++) {
				capacity[x].sendMessage("[" + ID + "] " + msg);
			}
		}
	}
	
	public synchronized void kick(int ID) {
		int pos = getClientID(ID);
	    if (pos >= 0){
	    	ServerThread end = capacity[pos];
	        System.out.println("Removing client thread " + ID + " at " + pos);
	        if (pos < totalClients - 1) {
	        	for (int x = pos + 1; x < totalClients; x++) {
	               capacity[x - 1] = capacity[x];
	        	}
	        }
	        totalClients--;
	        try {
	        	end.close();
	        }
	        catch(IOException ioe) {
	        	System.out.println("<<ERROR: COULD NOT KICK " + ID + ">>");
	        }
	    }
	}
	
	public void addClient(Socket socket) {
		if(totalClients < capacity.length) {
			capacity[totalClients] = new ServerThread(this, socket);
			try {
				capacity[totalClients].open();
				capacity[totalClients].start();
				totalClients++;
			}
			catch(IOException ioe) {
				System.out.println("<<ERROR: CANNOT OPEN CLIENT THREAD>>");
			}
		}
	}
	
	public void run() { //we get this from runnable
		
	}
	
	public static void main(String args[]){//I hate cargo-cult programming but I'm not above it either
		Client client = null;	//idk what this is meant to do. If you have better setup for main method please add
	    if (args.length != 2)
	    	System.out.println("Ma'am this is a Wendy's");
	    //from the looks of it this just means a server is already active???
	    else
	    	client = new Client(args[0], Integer.parseInt(args[1]));
	    
	}

	
}
