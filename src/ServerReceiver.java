
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

  /**
   * Constructs a new server receiver.
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
	}

	/**
	 * Starts this server receiver.
	 */
	public void run() {
		try {
			BlockingQueue<Message> clientsQueue = clientTable.getQueue(myClientsName, instanceID);
			MessageLog<Message> clientsLog = clientTable.getMessageLog(myClientsName);
			
			//Send the latest message if it exists when the user logs in
			if (!clientsLog.isEmpty()) clientsQueue.offer(clientsLog.getCurrent());
			
			while (running) {
				String cmd = myClient.readLine();
				
				switch(cmd.toLowerCase()) {
				case Commands.LOGOUT : logoutClient(); break;
					
				case Commands.SEND   : updateBlockingQueue(); break;
				
                case Commands.PREV   : clientsQueue.offer(clientsLog.getPrevious()); break;
                	
                case Commands.NEXT   : clientsQueue.offer(clientsLog.getNext()); break;
                	
                case Commands.DELETE : clientsLog.delete(); break;

                default				 : Report.error("Received invalid command."); break;
				
				}
				
			}
		} 
		catch (IOException e) {
			Report.error("Something went wrong with the client " 
					+ myClientsName + " " + e.getMessage()); 
			// No point in trying to close sockets. Just give up.
		}

		Report.behaviour("Server receiver ending");
		//interrupts the companion to signal that the client has logged out
		companion.interrupt();
	}

	private void logoutClient() {
		clientTable.removeQueue(myClientsName, instanceID);
		running = false;
	}

	private void updateBlockingQueue() throws IOException {
		//Reads the messages from the client
		String recipient = myClient.readLine();
		String text = myClient.readLine();

		Message msg = new Message(myClientsName, text);

		// get all the blocking queues that are running for each place the client is logged in
		ArrayList<BlockingQueue<Message>> recipientsQueues
		    = clientTable.getAllQueues(recipient);
		MessageLog<Message> recipientsLog 
			= clientTable.getMessageLog(recipient);
		
		if (clientTable.has(recipient)) {
			//offer the message to each instance of a client login
			for(BlockingQueue<Message> queue : recipientsQueues) {
				queue.offer(msg);
			}
			recipientsLog.add(msg);
		} 
		else if (recipientsQueues.isEmpty()) {
			recipientsLog.add(msg);
		}
		else Report.error("Message for non-existent client " + recipient + ": " + text);
	}
}

