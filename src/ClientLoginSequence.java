import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public class ClientLoginSequence extends Thread {
	
	private BufferedReader fromServer;
	private PrintStream toServer;
	private BufferedReader userInput;
	
	public ClientLoginSequence(BufferedReader s, PrintStream c, BufferedReader u) {
		fromServer = s;
		toServer = c;
		userInput = u;
	}
	
	@Override
	public void run() {
		System.out.println("Welcome, please either login or register: \n");
		
		String username = null;
		
		try {
            while(true) {
            	
                String cmd = userInput.readLine();
                
                if(cmd.equals(Commands.REGISTER) || cmd.equals(Commands.LOGIN)) {
                    System.out.print("Please enter your username: ");
                	username = userInput.readLine();
                    toServer.println(cmd);
                    toServer.println(username);
                } 
                else if (cmd.equals(Commands.QUIT)) {
                	toServer.println(Commands.QUIT);
                	break;
                }
                else {
                    Report.error("Invalid command. Please either register or login");
                }
                
                String reply = fromServer.readLine();
                System.out.println(reply);
                
                if (reply.equals(Commands.CONNECTION_SUCCESS)) {
                    makeThreads(username);
                }
            }
        }
        catch(IOException e) {
            Report.errorAndGiveUp("I/O Exception occurred");
        }	
	}

	private void makeThreads(String username) {
		ClientSender clientSender = new ClientSender(username, toServer);
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
