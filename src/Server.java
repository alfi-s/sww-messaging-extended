
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

		// This table will be shared by the server threads:
		ClientTable clientTable = new ClientTable();

		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(Port.number);
		} catch (IOException e) {
			Report.errorAndGiveUp("Couldn't listen on port " + Port.number);
		}

		try {
			// We loop for ever, as servers usually do.
			while (true) {
				Socket socket = serverSocket.accept();
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream toClient = new PrintStream(socket.getOutputStream());

                ServerLoginChecker loginChecker = new ServerLoginChecker(fromClient, toClient, clientTable);
                loginChecker.start();
			}
		} catch (IOException e) {
			// Lazy approach:
			Report.error("IO error " + e.getMessage());
			// A more sophisticated approach could try to establish a new
			// connection. But this is beyond the scope of this simple exercise.
		}
	}
}
