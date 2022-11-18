
import java.io.IOException;
import java.util.Scanner;
import common.ChatIF;

public class ServerConsole implements ChatIF{

	//Class variables *************************************************
	  
	  /**
	   * The default port to listen on.
	   */
	  final public static int DEFAULT_PORT = 5555;
	  
	//Instance variables **********************************************
	  
	  /**
	   * The instance of the client that created this ConsoleChat.
	   */
	  EchoServer server;

	private Scanner fromConsole;
	  
	  
	//Constructors ****************************************************

	  /**
	   * Constructs an instance of the ClientConsole UI.
	   *
	   * @param port The port to connect on.
	   */
	  public ServerConsole(int port) 
	  {
	    try 
	    {
	    	server= new EchoServer(port, this);
	      
	      
	    } 
	    catch(IOException exception) 
	    {
	      System.out.println("Error: Can't setup connection!"
	                + " Terminating client.");
	      System.exit(1);
	    }
	    
	    // Create scanner object to read from console
	    //fromConsole = new Scanner(System.in); 
	  }
	
	//Instance methods ************************************************
	  
	  /**
	   * This method waits for input from the console.  Once it is 
	   * received, it sends it to the client's message handler.
	   */
	  public void accept() 
	  {
	    try
	    {

	      String message;

	      while (true) 
	      {
	    	fromConsole = new Scanner(System.in);
	        message = fromConsole.nextLine();
	        server.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }
	  

	@Override
	public void display(String message) {
		// TODO Auto-generated method stub
		System.out.println(message);
		
	}
	
	
	
	/**
	   * This method is responsible for the creation of 
	   * the server instance (there is no UI in this phase).
	   *
	   * @param args[0] The port number to listen on.  Defaults to 5555 
	   *          if no argument is entered.
	   */
	  public static void main(String[] args) 
	  {
	    int port = 0; //Port to listen on

	    try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(Throwable t)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
		
	    ServerConsole sv = new ServerConsole(port);
	   
	    try 
	    {
	      sv.server.listen(); //Start listening for connections
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println("ERROR - Could not listen for clients!");
	    }
	    sv.accept();
	  }
	  
	
	  
}
