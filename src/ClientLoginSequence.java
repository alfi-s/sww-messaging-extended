import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

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
		System.out.println("Welcome, please either login or register: \n");
		
		String username = null;
        boolean waitForCmd = true;
        
		try {
            while(running) {
                
                while(waitForCmd) {
                    String cmd = userInput.readLine();
                	if(cmd.equalsIgnoreCase(Commands.REGISTER) || cmd.equalsIgnoreCase(Commands.LOGIN)) {
                        System.out.print("Please enter your username: ");
                    	username = userInput.readLine();
                        toServer.println(cmd);
                        toServer.println(username);
                        waitForCmd = false;
                    } 
                    else if (cmd.equalsIgnoreCase(Commands.QUIT)) {
                    	toServer.println(Commands.QUIT);
                    	running = false;
                    	waitForCmd = false;
                    }
                    else {
                        Report.error("Invalid command. Please either register or login");
                    }
                }
                
                String reply = fromServer.readLine();
                System.out.println(reply);
                if (reply.equals(Commands.USER_NOT_FOUND) || reply.equals(Commands.USER_ALREADY_EXISTS))
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
		ClientSender clientSender = new ClientSender(username, toServer, userInput);
		ClientReceiver clientReceiver = new ClientReceiver(fromServer);
		
		clientSender.start();
		clientReceiver.start();
		
		try {
			clientSender.join();
			clientReceiver.join();
		}
		catch(InterruptedException e) {
			Report.errorAndGiveUp("Fatal error: unexpected interruption occurred");
		}
	}
}
