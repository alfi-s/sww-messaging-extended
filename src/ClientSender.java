
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

// Repeatedly reads recipient's nickname and text from the user in two
// separate lines, sending them to the server (read by ServerReceiver
// thread).

public class ClientSender extends Thread {

	private String nickname;
	private BufferedReader user;
	private PrintStream server;
	private boolean running;

	ClientSender(String nickname, PrintStream server, BufferedReader user) {
		this.nickname = nickname;
		this.server = server;
		this.user = user;
		running = true;
	}

	@Override
	public void run() {

		try {
			// Then loop forever sending messages to recipients via the server:
			while (running) {
				String cmd = user.readLine();

				switch (cmd.toLowerCase()) {
				case Commands.LOGOUT:
					server.println(cmd);
					running = false;
					break;

				case Commands.SEND:
					String recipient = user.readLine();
					String text = user.readLine();
					server.println(cmd);
					server.println(recipient);
					server.println(text);
					break;

				case Commands.PREV:
				case Commands.NEXT:
				case Commands.DELETE:
					server.println(cmd);
					break;

				default:
					Report.error("Invalid command entered!");
					break;
				}
			}
		} catch (IOException e) {
			Report.errorAndGiveUp("Communication broke in ClientSender" + e.getMessage());
		}

		Report.behaviour("Client sender thread ending"); // Matches GGGGG in Client.java
	}
}
