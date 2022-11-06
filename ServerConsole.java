import java.io.IOException;
import java.util.*;
import common.*;

public class ServerConsole implements ChatIF {
	
	// Variable Declaration------------------------------------------------------------
	/**
	 * the default port to listen on
	 */
	static final int DEFAULT_PORT = 5555;
	
	/**
	 * instance of EchoServer that created this console
	 */
	EchoServer server;
	
	/**
	 * Scanner to read user input from the console	
	 */
	Scanner fromConsole;
	
	// Constructor Method--------------------------------------------------------------
	/**
	 * this constructor method
	 * 
	 * @param port
	 * 	the port on which the server will listen
	 */
	public ServerConsole(int port) {
		
		// Initialization
		fromConsole = new Scanner(System.in);
		
		try {
			
			server = new EchoServer(port, this);

		}
		catch (IOException ioe) { //error-handling

			// Output
			System.out.println("ERROR - Can't set up connection! Terminating server.");

			System.exit(1); //closing server
			
		}
		
		// Process: listening for connections
		try {

			server.listen(); //start listening
			
		}
		catch (Exception e) { //error-handling
			
			// Output
			System.out.println("ERROR - Could not listen for clients!");

		}
		
	}

	// Instance Methods----------------------------------------------------------------
	/**
	 * this method waits for user input from the console
	 * it then sends the input to the server's message handler
	 */
	public void accept() {
		
		// Process: reading from console
		try {
			
			// Variable Declaration
			String message;
			
			// Process: continously reading from console
			while (true) {
				
				// Initialization
				message = fromConsole.nextLine();
				
				// Process: telling server to handle the msg
				server.handleMessageFromEndUser(message);
				
			}
			
		}
		catch (Exception e) { //error-handling
			
			// Output
			System.out.println("Unexpected error while reading from console!");
			
		}
		
	}

	@Override
	/**
	 * this method overrides the superclass one and displays objects onto the UI
	 * 
	 * @param message
	 * 	the String to be displayed
	 */
	public void display(String message) {
		/* i) Anything typed on the server’s console by an end-user of the server
		 * should be echoed to the server’s console and to all the clients.
		 * 
		 * ii) Any message originating from the end-user of the server
		 * should be prefixed by the string "SERVER MSG>". */
		
		// Output
		System.out.println("SERVER MSG> " + message);
		
	}
	
	public static void main(String[] args) {

		// Variable Declaration
		int port; //port to listen on

		// Process: retrieving port number from console input
		try {

			// Initialization
			port = Integer.parseInt(args[0]);
			
		}
		catch(Throwable t) { //error-handling
			
			// Initialization: setting to default port 5555
			port = DEFAULT_PORT;
			
		}

		// Variable Declaration
		ServerConsole chat = new ServerConsole(port);
		
		// User Input: waiting to read from console
		chat.accept();
		
	}

}