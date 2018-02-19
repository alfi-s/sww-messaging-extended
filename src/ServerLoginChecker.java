import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

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
		String loginName = fromClient.readLine();
		if (clientTable.has(loginName)) {
			Report.behaviour(loginName + " has connected");
			toClient.println(Commands.CONNECTION_SUCCESS);
			clientTable.setQueue(loginName, true);
			makeThreads(loginName);
		} else toClient.println(Commands.USER_NOT_FOUND);
	}

	private void registerClient() throws IOException {
		String registerName = fromClient.readLine();
		if (!clientTable.has(registerName)) {
			clientTable.add(registerName);
			toClient.println(Commands.CONNECTION_SUCCESS);
			makeThreads(registerName);
		} else toClient.println(Commands.USER_ALREADY_EXISTS);
	}

	private void makeThreads(String registerName) {
		ServerSender serverSender = new ServerSender(clientTable.getQueue(registerName), toClient);
		ServerReceiver serverReceiver = new ServerReceiver(registerName, fromClient, clientTable, serverSender);
		
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
