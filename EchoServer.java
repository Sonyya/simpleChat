// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import common.ChatIF;
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
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
	
	
	final private String key = "loginKey"; 
	
	final String  signal_de_deconnection = "S1GN4L_2_DECONNECTION";
	  /**
	   * The interface type variable.  It allows the implementation of 
	   * the display method in the client.
	   */
	  ChatIF serverUI; 
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   * @param serverUI The interface type variable.
   */
  public EchoServer(int port, ChatIF serverUI) throws IOException 
  {
    super(port);//Call the superclass constructor
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	serverUI.display("Message received: " + msg + " from " + client.getInfo(key));
	
	String msgString = String.valueOf(msg).trim();
	
	if(msgString.startsWith("#login"))
	{
	    int sep_index= msgString.indexOf(" ") ;		
	    String loginID = msgString.substring(sep_index+1).trim();
	    client.setInfo(key, loginID);
	    String annoucement = client.getInfo(key)+" has lOGGED ON.";
	    serverUI.display(annoucement);
	    this.sendToAllClients(annoucement);
	}
	else if(msgString.equals(signal_de_deconnection))
	{
		clientDisconnected(client);
	}
	else
	{
		this.sendToAllClients(client.getInfo(key)+ "> "+msg);
	}
	  
    
  }
  
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
	  serverUI.display
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
	  serverUI.display
      ("Server has stopped listening for connections.");
	  sendToAllClients("WARNING - The server has stopped listening for connections");
  }
  
  @Override
protected void clientConnected(ConnectionToClient client) {
	// TODO Auto-generated method stub
	super.clientConnected(client);
	 serverUI.display( "a NEW CLIENT is attempting to CONNECT to the server.");
	 
}


@Override
protected synchronized void clientDisconnected(ConnectionToClient client) {
	// TODO Auto-generated method stub
	super.clientDisconnected(client);
	String msg = client.getInfo(key)+" has DISCONNECTED";
	serverUI.display("Client "+msg);
	this.sendToAllClients(msg);
	/*try {
		client.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
}
  
  //Class methods ***************************************************

	/**
	 * This method handles all data coming from the UI            
	 *
	 * @param message The message from the UI.    
	 */
	public void handleMessageFromServerUI(String message)
	{
		if(message.startsWith("#"))
		{
			this.handleCommand(message);
		}
		else
		{
			 message= "SERVER MSG> "+message;
			 serverUI.display(message);
			 sendToAllClients(message);
		}
		 
	}
	
	 /**
	 * @param cmd la command qui indiquera l action a effectue
	 * Cette methode permet d executer les commandes entrees dans la partie client
	 */
	private void handleCommand(String cmd)
	  {
		cmd = cmd.trim();
		String sep = " ";
	    int sep_index= cmd.indexOf(sep) ;
	    String action = sep_index==-1?cmd:cmd.substring(0,sep_index);
		String arg= sep_index==-1?"":cmd.substring(sep_index+1).trim();
		
		/*serverUI.display("Action " + action);
		serverUI.display("Arg "+ arg);*/
		
		switch(action) {
		
		  case "#quit":
		    // code block
			 serverUI.display("The server will quit .....");
			 this.quit();
		    break;
		    
		  case "#stop":
		    // code block
			  
			  serverUI.display("the server will stop listening for connections ......");
			  this.stopListening();
			 
		    break;
		    
		  case "#close":
			    // code block
			  try {
				  serverUI.display("The server will close ...... ");
				  sendToAllClients("SERVER SHUTTING DOWN! DISCONNECTING!");
				this.close();
				serverUI.display("The server is now CLOSED");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				serverUI.display("ERROR DETECTED: " + '\n'+e.toString());
			}
				  
			  break;
			  
		  case "#setport":
			    // code block
			  if(!this.isListening())
			  {
				  int port = 0; 
				  try
				  {
					  port=Integer.parseInt(arg);
					  this.setPort(port);
					  serverUI.display("server port set to "+ arg);
				  }
				  catch(NumberFormatException e)
				    {
					  serverUI.display("ERROR DETECTED: " + '\n'+e.toString());
				    }
			  }
			  else
			  {
				  serverUI.display("ERROR DETECTED: the server is open");
			  }
				  
			    break;
			    
		  case "#start":
			    // code block
			try {
				this.listen();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				serverUI.display("ERROR DETECTED: " + '\n'+e.toString());
			}
			    break;
	
		  case "#getport":
			    // code block
			  	serverUI.display("current server port number: " + this.getPort());
			    break;
			    
		  default:
		    // code block
			  serverUI.display("ERROR: command doesnt exist."+'\n' + "Read the documentation"+'\n' + commandeDoc());
		}
		
		
	  }

		/**
		 * affiche la documentation des commandes disponibles
		 */
		private String commandeDoc()
		{
			return (
			        "****************************"+'\n'+
			        "LISTE COMMANDES DISPONIBLES"+'\n'+
			        "****************************"+'\n'+
			        "#quit:  Provoque l'arrêt normal du serveur." +'\n'+
			"#stop: Provoque le serveur de cesser d'écouter aux nouveaux clients."+'\n'+
			"#close <host>: Provoque le serveur de cesser non seulement d'écouter aux nouveaux clients, mais également de déconnecter tous les clients existants."+'\n'+
			"#setport <port>: Appelle la méthode setPort sur le serveur." +'\n'+
			"#start: Provoque le serveur de commencer à écouter pour des nouveaux clients. "+'\n'+
			"#getport: Affiche le numéro de port actuel.");
			
		}
	  
		/**
		   * This method terminates the server.
		   */
		  public void quit()
		  {
		    try
		    {
		      close();
		      serverUI.display("The server just QUIT");
		    }
		    catch(IOException e)
		    {
		    	
		    }
		    System.exit(0);
		  }


		@Override
		protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
			// TODO Auto-generated method stub
			super.clientException(client, exception);
			String msg = client.getInfo("loginID").toString() + " has disconnected";

			serverUI.display(msg);
		    this.sendToAllClients(msg);
	
		}

		  


}
//End of EchoServer class
