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
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  /**
   * le  Login ID du client
   */
  String loginID;
  
  final String  signal_de_deconnection = "S1GN4L_2_DECONNECTION";
  
  //final private String key = "loginKey"; 
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    try {
    	openConnection();
        sendToServer("#login "+loginID);
    }
    catch(IOException exception)
    {
    	clientUI.display("Cannot open connection.  Awaiting command.");
    }
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	  //gestion des commandes
	if(message.startsWith("#"))
	{
		
		this.handleCommand(message);
		
	}
	else
	{
		try
	    {
	      sendToServer(message);
	    }
	    catch(IOException e)
	    {
	      clientUI.display
	        ("Could not send message to server.  Terminating client.");
	      quit();
	    }
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
	
	/*clientUI.display("Action " + action);
	clientUI.display("Arg "+ arg);*/
	
	switch(action) {
	  case "#quit":
	    // code block
		  clientUI.display(this.loginID + ", you are quitting  ......");
		  if(this.isConnected())
		  {
			  try {
				sendToServer(signal_de_deconnection);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			  quit();
		  }
		  else
		  {
			  clientUI.display(this.loginID +", you just quit.");
			  System.exit(0);
		  }
		 

		 
	    break;
	    
	  case "#logoff":
	    // code block
		  try {
			  sendToServer(signal_de_deconnection);
			  clientUI.display(this.loginID +", you are logging off ....");
			  this.closeConnection();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			clientUI.display("ERROR DETECTED: " + '\n'+e.toString());
		}
	    break;
	    
	  case "#sethost":
		    // code block
			  if(!this.isConnected())
			  {
				  this.setHost(arg);
				  clientUI.display("host set to "+ arg);
			  }
			  else
			  {
				  clientUI.display("ERROR DETECTED: client already connected");
			  }
		  	
		    break;
		    
	  case "#setport":
		    // code block
			  
			  if(!this.isConnected())
			  {
				  int port = 0; 
				  try
				  {
					  port=Integer.parseInt(arg);
					  this.setPort(port);
					  clientUI.display("port set to "+ arg);
				  }
				  catch(NumberFormatException e)
				    {
					  clientUI.display("ERROR DETECTED: " + '\n'+e.toString());
				    }
			  }
			  else
			  {
				  clientUI.display("ERROR DETECTED: this client is already connected with a port number");
			  }
		    break;
		    
	  case "#login":
		    // code block
			  if(this.isConnected())
			  {
				  clientUI.display("ERROR DETECTED: Client already connected. Disconnect if you want to login");
			  }
			  else
			  {
				 
				  try {
					 //clientUI.notify();
					 this.loginID = arg;
					this.openConnection();
					
					sendToServer("#login " + arg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					clientUI.display("ERROR DETECTED: " + '\n'+e.toString());
				}
			  }
		    break;
	  case "#gethost":
		    // code block
		  		clientUI.display("current host: " + this.getHost());
		    break;
	  case "#getport":
		    // code block
		  	clientUI.display("current port: " + this.getPort());
		    break;
	  default:
	    // code block
		  clientUI.display("ERROR: command doesnt exist."+'\n' + "Read the documentation"+'\n' + commandeDoc());
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
		        "#quit:  Provoque l'arrêt normal du client." +'\n'+
		"#logoff: Provoque la déconnexion du client du serveur, mais pas la fermeture."+'\n'+
		"#sethost <host>: Appelle la méthode setHost dans le client."+'\n'+
		"#setport <port>: Appelle la méthode setPort dans le client." +'\n'+
		"#login: Cause le client à se connecter au serveur."+'\n'+
		"#gethost: Affiche le nom d'hôte actuel." +'\n'+
		"#getport: Affiche le numéro de port actuel.");

	
	}
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
    	
      this.closeConnection();
      clientUI.display(this.loginID +", you just quit.");
    }
    catch(IOException e) {}
    System.exit(0);
    
  }


@Override
protected void connectionClosed() {
	// TODO Auto-generated method stub
	super.connectionClosed();
	clientUI.display("Connexion has been Closed");
	//donner la possibilite de se reconnecter
	/*try {
		clientUI.
		clientUI.display("test la reconnection voir");
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		clientUI.display("UNE ERREUR S EST PRODUITE: " + '\n'+e.toString());
	}*/
}


@Override
protected void connectionException(Exception exception) {
	// TODO Auto-generated method stub
	clientUI.display("Abnormal termination of connection." +'\n' +"Server Stopped.");
	//System.exit(0); //terminate the runtime
}


@Override
protected void connectionEstablished() {
	// TODO Auto-generated method stub
	super.connectionEstablished();
	clientUI.display("Hi! "+ this.loginID + ",  you are logged on.");
}



  
  
}
//End of ChatClient class
