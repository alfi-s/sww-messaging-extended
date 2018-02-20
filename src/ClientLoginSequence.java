import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
/**
 * A Thread which handles registering and logging-in
 * @author alfis
 *
 */
public class ClientLoginSequence extends Thread {
	
	private BufferedReader fromServer;
	private PrintStream toServer;
	private BufferedReader userInput;
	private boolean running;
	
	public ClientLoginSequence(BufferedReader s, PrintStream c, BufferedReader u) {
		fromServer = s;
		toServer = c;
		userInput = u;
		running = true;
	}
	
	@Override
	public void run() {
		System.out.println("Welcome, please either login or register:");
		
		String username = null;
        boolean waitForCmd = true;
        
		try {
			mainloop: 
			while(running) {
                
				//User types in commands and the commands are sent to the server
                while(waitForCmd) {
                	System.out.print("Please enter a command: ");
                    String cmd = userInput.readLine();
                    
                	if(cmd.equalsIgnoreCase(Commands.REGISTER) || cmd.equalsIgnoreCase(Commands.LOGIN)) {
                        
                		System.out.print("Please enter your username: ");
                    	username = userInput.readLine();
                    	System.out.print("Please enter your password: ");
                    	String password = userInput.readLine();
                    	
                        toServer.println(cmd);
                        toServer.println(username);
                        toServer.println(password);
                        waitForCmd = false;
                    } 
                	
                    else if (cmd.equalsIgnoreCase(Commands.QUIT)) {
                    	toServer.println(Commands.QUIT);
                    	break mainloop;
                    }
                	
                    else {
                        Report.error("Invalid command. Please either register or login");
                    }
                }
                
                //Wait for the server to reply, then appropriately creates threads to begin the messaging system
                String reply = fromServer.readLine();
                System.out.println(reply);
                if (reply.equals(Commands.USER_NOT_FOUND) 
                		|| reply.equals(Commands.USER_ALREADY_EXISTS) 
                		|| reply.equals(Commands.INVALID_PASSWORD))
                	waitForCmd = true;
                
                if (reply.equals(Commands.CONNECTION_SUCCESS)) {
                    makeThreads(username);
                    waitForCmd = true;
                }
            }
        }
        catch(IOException e) {
            Report.errorAndGiveUp("I/O Exception occurred");
        }	
	}

	private void makeThreads(String username) {
		//Creates threads necessary for communication to the server and back
		ClientSender clientSender = new ClientSender(username, toServer, userInput);
		ClientReceiver clientReceiver = new ClientReceiver(fromServer);
		
		//starts the threads
		clientSender.start();
		clientReceiver.start();
		
		//waits for the threads to end.
		try {
			clientSender.join();
			Report.behaviour("ClientSender Thread ended");
			clientReceiver.join();
			Report.behaviour("ClientReceiver Thread ended");
		}
		catch(InterruptedException e) {
			Report.errorAndGiveUp("Fatal error: unexpected interruption occurred");
		}
	}
}
