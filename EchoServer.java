// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.IOException;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer {

	//Class variables *************************************************
	/**
	 * The default port to listen on.
	 */
	final public static int DEFAULT_PORT = 5555;
  
	//Constructors ****************************************************
	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 */
	public EchoServer(int port) {
		super(port);	
	}
  
	//Instance methods ************************************************
	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg The message received from the client.
	 * @param client The connection from which the message originated.
	 */
  	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
  		
  		// Variable Declaration
  		String message = (String) msg;
  		
  		// Process: checking if message is a command
		if ((message).charAt(0) == '#') { //command
			
			// Process: checking length of command
			if (message.length() > 8) {
				
				// Process: determining if command involves port
				if (message.substring(0, 8).equals("#setport")) { //set port command
					
					// Process: checking for logged off client
					if (!isListening() && getNumberOfClients() == 0) { //closed
					
						setPort(Integer.parseInt(message.substring(9))); //setting port
						
					}
					else { //open server
						
						// Output
						System.out.println("ERROR - Server is open. Cannot change port.");
						
					}
					
				}
				else {
					
					// Output
					System.out.println("ERROR - Invalid command.");
					
				}
				
			}
			else {
			
				// Process: determining which command was entered
				switch(message) {
				
					case "#quit" :
						
						try {
							
							close(); //closing server & disconnecting all clients
							
						}
						catch (IOException ioe) {

							// Output
							System.out.println("Could not close server.");
							
						}
						
						break;
						
					case "#stop" :
						
						stopListening(); //stop listening
						
						// Output
						serverStopped(); //stopping new connections
						
						break;
						
					case "#close" :
						
						stopListening(); //stop listening
						
						// Output
						serverStopped(); //stopping new connections
						
						try {
							
							close(); //closing server & disconnecting all clients
							
						}
						catch (IOException ioe) {

							// Output
							System.out.println("Could not close server.");
							
						}
						
						break;
						
					case "#start" :
						
						if (isListening()) { //already listening
							
							// Output
							System.out.println("ERROR - Already listening for clients.");
							
						}
						else {
							
							try {
								
								listen(); //start listening
								
							}
							catch (IOException e) {

								// Output
								System.out.println("Could not listen for clients");
								
							}
							
						}
						
					case "#getport" :
						
						// Output
						System.out.println(getPort());
						
						break;
						
					default : //not an actual command
						
						// Output
						System.out.println("ERROR - Invalid command.");
						
				}
				
			}
			
		}
		else { //not command
			
			// Output
			System.out.println("Message received: " + msg + " from " + client);
	  		
			// Process: sending message to server
			this.sendToAllClients(msg);
			
		}
	
  	}
    
  	/**
  	 * This method overrides the one in the superclass.  Called
  	 * when the server starts listening for connections.
  	 */
  	protected void serverStarted() {
  		System.out.println("Server listening for connections on port " + getPort());	
  	}
  
  	/**
  	* This method overrides the one in the superclass.  Called
  	* when the server stops listening for connections.
  	*/
  	protected void serverStopped() {
  		System.out.println("Server has stopped listening for connections.");	
  	}
  
  	/**
  	 * Hook method called each time a client disconnects.
  	 * The default implementation does nothing. The method
  	 * may be overridden by subclasses but should remains synchronized.
  	 *
  	 * @param client the connection with the client.
  	 */
  	synchronized protected void clientDisconnected(ConnectionToClient client) {
	  
  		// Output
  		System.out.println("Client has disconnected :)");
  		
  	}

  	/**
  	 * Hook method called each time an exception is thrown in a
  	 * ConnectionToClient thread.
  	 * The method may be overridden by subclasses but should remains
  	 * synchronized.
  	 *
  	 * @param client the client that raised the exception.
  	 * @param Throwable the exception thrown.
  	 */
  	synchronized protected void clientException(ConnectionToClient client,
		  Throwable exception) {
  		
  		// Process: calling clientDisconnected()</code> method to print msg
  		// acknowledging disconnection
  		clientDisconnected(client);
  		
  	}

  	//Class methods ***************************************************
  	/**
  	 * This method is responsible for the creation of 
  	 * the server instance (there is no UI in this phase).
  	 *
  	 * @param args[0] The port number to listen on.  Defaults to 5555
  	 * if no argument is entered.
  	 */
  	public static void main(String[] args) {
  		int port = 0; //Port to listen on

  		try {
  			port = Integer.parseInt(args[0]); //Get port from command line
  		}
  		catch(Throwable t) {
  			port = DEFAULT_PORT; //Set port to 5555
  		}
	
  		EchoServer sv = new EchoServer(port);
    
  		try {
  			sv.listen(); //Start listening for connections
  		} 
  		catch (Exception ex) {
  			System.out.println("ERROR - Could not listen for clients!");
  		}
  	}
  	
}
//End of EchoServer class