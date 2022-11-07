// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
	
	//Instance variables **********************************************
	/**
	 * The interface type variable.  It allows the implementation of 
	 * the display method in the client.
	 */
	ChatIF clientUI; 
	
	/**
	 * the login ID of the client
	 */
	String loginID;

	//Constructors ****************************************************
	/**
	 * Constructs an instance of the chat client.
	 * 
	 * @param host The server to connect to.
	 * @param port The port number to connect on.
	 * @param clientUI The interface type variable.
	 */	  
	public ChatClient(String loginID, String host, int port, ChatIF clientUI) throws IOException {
		
		super(host, port); //Call the superclass constructor
		
		// Initialization
	    this.clientUI = clientUI;
	    
	    // Process: checking for null user ID
	    if (loginID == null) {
	    	
	    	throw new IOException("no login id provided"); //throwing exception
	    	
	    }
	    else {
	    	
	    	// Initialization
	    	this.loginID = loginID;
	    	
	    }
	    
	    openConnection(); //opening connection to server
	    
	    // Process: sending login message to server
	    sendToServer("#login " + loginID);
	    
	}

	//Instance methods ************************************************
	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {
		clientUI.display(msg.toString());	
	}

	/**
	 * This method handles all data coming from the UI            
	 *
	 * @param message The message from the UI.    
	 */
	public void handleMessageFromClientUI(String message) {
		
		// Process: checking if message is a command
		if (message.charAt(0) == '#') { //command
			
			// Process: checking length of command
			if (message.length() > 8) {
				
				// Process: determining if command involves host/port
				if (message.substring(0, 8).equals("#sethost")) { //set host command
					
					// Process: checking for logged off client
					if (!isConnected()) { //not yet connected
					
						setHost(message.substring(9)); //setting host
						
						// Output
						clientUI.display("Host set to: " + getHost());
						
					}
					else { //already connected
						
						// Output
						clientUI.display("ERROR - Client already logged in. Cannot change host name.");
						
					}
					
				}
				else if (message.substring(0, 8).equals("#setport")) { //set port command
					
					// Process: checking for logged off client
					if (!isConnected()) { //not yet connected
					
						setPort(Integer.parseInt(message.substring(9))); //setting port
						
						// Output
						clientUI.display("Port set to: " + getPort());
						
					}
					else { //already connected
						
						// Output
						clientUI.display("ERROR - Client already logged in. Cannot change port.");
						
					}
					
				}
				else if (message.substring(0, 6).equals("#login")) { //login command
					
					tryLogin(message); //try to login
					
				}
				else { //invalid command
					
					// Output
					clientUI.display("ERROR - Invalid command.");
					
				}
				
			}
			else if (message.length() > 6) { //could be login command
				
				// Process: checking for login command
				if (message.substring(0, 6).equals("#login")) { //command
					
					tryLogin(message); //checking login command conditions
					
				}
				else { //other command
					
					checkSwitchCase(message);
					
				}
				
			}
			else { //other command
				
				checkSwitchCase(message);
				
			}
			
		}
		else { //not command
			
			// Process: sending message to server
			try {
				
				sendToServer(message); //sending msg to server
				
			}
			catch(IOException e) {
				
				// Output
				clientUI.display("Could not send message to server.  Terminating client.");
				
				quit(); //terminating client connection
				
			}
			
		}
	}
	
	/**
	 * this helper method searches through the cases of possible commands
	 * @param message
	 * 	the input from the user
	 */
	private void checkSwitchCase(String message) {
		
		// Process: determining which command was entered
		switch(message) {
		
			case "#quit" :
				
				quit(); //terminating client connection
				
				break;
				
			case "#logoff" :
				
				try {
					
					closeConnection(); //closing connection
				
				}
				catch (IOException e) {}
				
				break;
				
			case "#gethost" :
				
				// Output
				clientUI.display(getHost());
				
				break;
				
			case "#getport" :
				
				// Output
				clientUI.display(String.valueOf(getPort()));
				
				break;
				
			default : //not an actual command
				
				// Output
				clientUI.display("ERROR - Invalid command.");
				
		}
		
	}
  
	/**
	 * this helper method attempts to log the user back in when the command is detected
	 * @param message
	 * 	the message inputted by the users
	 */
	private void tryLogin(String message) {
		
		if (!isConnected()) { //not yet connected
			
			try {
				
				openConnection(); //opening connection
				
				// Initialization
				this.loginID = message.substring(7);
				
				// Process: sending login message to server
			    sendToServer("#login " + loginID);
				
			}
			catch (IOException e) {
				
				// Output
				clientUI.display("ERROR - Cannot connect to server.");
				
			}
			
		}
		else { //already logged in
			
			// Output
			clientUI.display("ERROR - Already logged in.");
			
			// Process: sending message to server
			try {
				
				sendToServer(message); //sending msg to server
				
			}
			catch(IOException e) {
				
				// Output
				clientUI.display("Could not send message to server.  Terminating client.");
				
				quit(); //terminating client connection
				
			}
			
		}
		
	}
	
	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			closeConnection();
		}
		catch(IOException e) {}
		System.exit(0);
	}
	
	/**
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
	public void connectionClosed() {
		
		// Output
		clientUI.display("Client connection has been closed.");
		
	}
	
	/**
	 * Hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 * 	the exception raised.
	 */
	public void connectionException(Exception exception) {
		
		// Output
		clientUI.display("WARNING - The server has stopped listening for connections");
		clientUI.display("SERVER SHUTTING DOWN! DISCONNECTING!");
		
		try {
			closeConnection();
		}
		catch(IOException e) {}
		
		clientUI.display("Abnormal termination of connection.");
		
	}
	
}
//End of ChatClient class