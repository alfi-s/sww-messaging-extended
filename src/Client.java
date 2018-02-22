import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

class Client {

	public static void main(String[] args) {

		// Check correct usage:
		if (args.length != 1) {
			Report.errorAndGiveUp("Usage: java Client server-hostname");
		}

		// Initialize information:
		String hostname = args[0];

		// Open sockets:
		PrintStream toServer = null;
		BufferedReader fromServer = null;
		BufferedReader userIn = null;
		Socket server = null;

			//try to initialize the sockets
		try {
			server = new Socket(hostname, Port.number);
			toServer = new PrintStream(server.getOutputStream());
			fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
			userIn = new BufferedReader(new InputStreamReader(System.in));
		} catch (UnknownHostException e) {
			Report.errorAndGiveUp("Unknown host: " + hostname);
		} catch (IOException e) {
			Report.errorAndGiveUp("The server doesn't seem to be running " + e.getMessage());
		}

		// Create login managing sequence
		ClientLoginSequence loginSequence = new ClientLoginSequence(fromServer, toServer, userIn);
		loginSequence.start();

		// Wait for it to end and close sockets/streams.
		try {
			loginSequence.join();
			Report.behaviour("LoginSequence ending");
			toServer.close(); 
			fromServer.close(); 
			server.close(); 
		} catch (IOException e) {
			Report.errorAndGiveUp("Something wrong " + e.getMessage());
		} catch (InterruptedException e) {
			Report.errorAndGiveUp("Unexpected interruption " + e.getMessage());
		}
		Report.behaviour("Goodbye.");
	}
}
