
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class ServerReceiver extends Thread {
	private String myClientsName;
	private String instanceID;
	private BufferedReader myClient;
	private ClientTable clientTable;
	private ServerSender companion;
	private boolean running;
	private BlockingQueue<Message> clientsQueue;
	private MessageLog<Message> clientsLog;

	/**
	 * Constructs a new server receiver.
	 * 
	 * @param n the name of the client with which this server is communicating
	 * @param id the instance ID assigned when the thread was created
	 * @param c the reader with which this receiver will read data
	 * @param t the table of known clients and connections
	 * @param s the corresponding sender for this receiver
	 */
	public ServerReceiver(String n, String id, BufferedReader c, ClientTable t, ServerSender s) {
		myClientsName = n;
		instanceID = id;
		myClient = c;
		clientTable = t;
		companion = s;
		running = true;
		clientsQueue = clientTable.getQueue(myClientsName, instanceID);
		clientsLog = clientTable.getMessageLog(myClientsName);
	}

	/**
	 * Starts this server receiver.
	 */
	public void run() {
		try {

			// Send the latest message if it exists when the user logs in
			if (!clientsLog.isEmpty())
				clientsQueue.offer(clientsLog.getCurrent());

			//reads a command and calls the appropriate method.
			while (running) {
				String cmd = myClient.readLine();

				switch (cmd.toLowerCase()) {
					case Commands.LOGOUT : logoutClient(); break;
					
					case Commands.SEND   : updateBlockingQueue(); break;
					
	                case Commands.PREV   : sendPrevious(); break;
	                	
	                case Commands.NEXT   : sendNext(); break;
	                	
	                case Commands.DELETE : sendDelete(); break;
	
	                default				 : Report.error("Received invalid command."); break;
				}
			}
		} catch (IOException e) {
			Report.error("Something went wrong with the client " + myClientsName + " " + e.getMessage());
			// No point in trying to close sockets. Just give up.
		}

		Report.behaviour("Server receiver ending");
		// interrupts the companion to signal that the client has logged out
		companion.interrupt();
	}
	
	/**
	 * Delete a message from the client's log
	 */
	private void sendDelete() {
		try {
			clientsLog.delete();
		} catch (NullPointerException e) {
			Report.error(myClientsName + " tried to delete a non-existent message.");
		}
	}

	/**
	 * Gets the next message and passes it to the user
	 */
	private void sendNext() {
		try {
			clientsQueue.offer(clientsLog.getNext());
		} catch(NullPointerException e) {
			//Actually redundant, already handled in the MessageLog class. Included for safety.
			Report.error(myClientsName + " tried to access non-existent message (next).");
		}
	}

	/**
	 * Gets the previous message and passes it to the user
	 */
	private void sendPrevious() {
		try {
			clientsQueue.offer(clientsLog.getPrevious());
		} catch(NullPointerException e) {
			Report.error(myClientsName + " tried to access non-existent message (previous).");
		}
	}

	/**
	 * Removes the BlockingQueue of a client's login instance
	 */
	private void logoutClient() {
		clientTable.removeQueue(myClientsName, instanceID);
		running = false;
	}

	/**
	 * Updates the blocking queues and message logs with a sent message 
	 * @throws IOException
	 */
	private void updateBlockingQueue() throws IOException {
		// Reads the messages from the client
		String recipient = myClient.readLine();
		String text = myClient.readLine();

		Message msg = new Message(myClientsName, text);

		if (clientTable.has(recipient)) {

			// get all the blocking queues that are running for each place the client is
			// logged in
			ArrayList<BlockingQueue<Message>> recipientsQueues = clientTable.getAllQueues(recipient);
			MessageLog<Message> recipientsLog = clientTable.getMessageLog(recipient);

			if (recipientsQueues.isEmpty()) {
				recipientsLog.add(msg); //Recipient will still get message when they login later.
			} else {
				// offer the message to each instance of a client login
				for (BlockingQueue<Message> queue : recipientsQueues) {
					queue.offer(msg);
				}
				recipientsLog.add(msg);
			}
		} else
			clientsQueue.offer(new Message("Server", "(Error) recipient " + recipient + " not found"));
	}
}
