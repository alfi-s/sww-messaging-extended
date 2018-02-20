import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;

public class ServerLoginChecker extends Thread {
	
	private BufferedReader fromClient;
	private PrintStream toClient;
	private ClientTable clientTable;
	private boolean running;
	
	public ServerLoginChecker(BufferedReader c, PrintStream s, ClientTable t) {
		fromClient = c;
		toClient = s;
		clientTable = t;
		running = true;
	}
	
	@Override
	public void run() {
		try {
			while (running) {
				String cmd = fromClient.readLine();
				
				switch(cmd) {
				
				case Commands.REGISTER: registerClient(); break;
				
				case Commands.LOGIN: loginClient(); break;
				
				case Commands.QUIT:
					Report.behaviour("LoginChecker Closing");
					running = false;
					break;
				}
			}
		}
		catch(IOException e) {
			Report.error("I/O exception has occurred");
		}
	}

	private void loginClient() throws IOException {
		//creates an ID to identify the current login
		String instanceID = UUID.randomUUID().toString();
		String loginName = fromClient.readLine();
		String password = fromClient.readLine();
		
		if (clientTable.has(loginName) && clientTable.testPassword(loginName, password)) {
			Report.behaviour(loginName + " has connected");
			toClient.println(Commands.CONNECTION_SUCCESS);
			clientTable.addQueue(loginName, instanceID);
			makeThreads(loginName, instanceID);
		} 
		else if (!clientTable.testPassword(loginName, password)) toClient.println(Commands.INVALID_PASSWORD);
		else toClient.println(Commands.USER_NOT_FOUND);
	}

	private void registerClient() throws IOException {
		//creates an ID to identify the current login
		String instanceID = UUID.randomUUID().toString();
		String registerName = fromClient.readLine();
		String passwordInput = fromClient.readLine();
		
		if (!clientTable.has(registerName)) {
			Report.behaviour(registerName + "has registered and connected");
			clientTable.add(registerName, new Password(passwordInput));
			clientTable.addQueue(registerName, instanceID);
			toClient.println(Commands.CONNECTION_SUCCESS);
			makeThreads(registerName, instanceID);
		} else toClient.println(Commands.USER_ALREADY_EXISTS);
	}

	private void makeThreads(String registerName, String instanceID) {
		
		ServerSender serverSender = new ServerSender(clientTable.getQueue(registerName, instanceID), toClient);
		ServerReceiver serverReceiver = new ServerReceiver(registerName, instanceID, fromClient, clientTable, serverSender);
		
		serverSender.start();
		serverReceiver.start();
		
		try {
			serverSender.join();
			serverReceiver.join();
		}
		catch(InterruptedException e) {
			Report.errorAndGiveUp("Fatal error: unexpected interruption has occurred");
		}
	}
}
