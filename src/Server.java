
// Usage:
//        java Server
//
// There is no provision for ending the server gracefully.  It will
// end if (and only if) something exceptional happens.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	/**
	 * Start the server listening for connections.
	 */
	public static void main(String[] args) {

		// Create a FileKeeper object to manage saving ClientTable data.
		FileKeeper keeper = new FileKeeper();
		
		// This table will be shared by the server threads:
		ClientTable clientTable = keeper.readData("table.ser");
		
		// This shutdown hook will save the client table data when the server exits
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				keeper.writeData(clientTable);
				Report.behaviour("Server is shutting down");
			}
		});

		// creates a server socket
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(Port.number);
		} catch (IOException e) {
			Report.errorAndGiveUp("Couldn't listen on port " + Port.number);
		}

		Report.behaviour("Now waiting for connections...");

		try {
			// We loop forever, as servers usually do.
			while (true) {
				Socket socket = serverSocket.accept();
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream toClient = new PrintStream(socket.getOutputStream());

				ServerLoginChecker loginChecker = new ServerLoginChecker(fromClient, toClient, clientTable);
				loginChecker.start();
			}
		} catch (IOException e) {
			Report.error("IO error " + e.getMessage());
		}
	}
}
