
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.BlockingQueue;

// Gets messages from client and puts them in a queue, for another
// thread to forward to the appropriate client.

public class ServerReceiver extends Thread {
	private String myClientsName;
	private BufferedReader myClient;
	private ClientTable clientTable;
	private ServerSender companion;
	private boolean running;

  /**
   * Constructs a new server receiver.
   * @param n the name of the client with which this server is communicating
   * @param c the reader with which this receiver will read data
   * @param t the table of known clients and connections
   * @param s the corresponding sender for this receiver
   */
	public ServerReceiver(String n, BufferedReader c, ClientTable t, ServerSender s) {
		myClientsName = n;
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
			BlockingQueue<Message> clientsQueue = clientTable.getQueue(myClientsName);
			MessageLog<Message> clientsLog = clientTable.getMessageLog(myClientsName);
			
			//Send the latest message if it exists when the user logs in
			if (!clientsLog.isEmpty()) clientsQueue.offer(clientsLog.getCurrent());
			
			while (running) {
				String cmd = myClient.readLine();
				
				switch(cmd.toLowerCase()) {
				case Commands.LOGOUT:
					running = false;
					break;
					
				case Commands.SEND:
                    String recipient = myClient.readLine();
                    String text = myClient.readLine();

                    Message msg = new Message(myClientsName, text);
                    BlockingQueue<Message> recipientsQueue
                        = clientTable.getQueue(recipient);
                    MessageLog<Message> recipientsLog 
                    	= clientTable.getMessageLog(recipient);
                    if (recipientsQueue != null) {
                      recipientsQueue.offer(msg);
                      recipientsLog.add(msg);
                    } else
                      Report.error("Message for non-existent client "
                                   + recipient + ": " + text);
                    break;
				
                case Commands.PREV:
                	clientsLog.getPrevious();
                	break;
                	
                case Commands.NEXT:
                	clientsLog.getNext();
                	break;
                	
                case Commands.DELETE:
                    clientsLog.delete();
                    break;

                default:
                    Report.error("Received invalid command.");
                    break;
				
				}
				
			}
		} catch (IOException e) {
			Report.error("Something went wrong with the client " 
					+ myClientsName + " " + e.getMessage()); 
			// No point in trying to close sockets. Just give up.
			// We end this thread (we don't do System.exit(1)).
		}

		Report.behaviour("Server receiver ending");
		companion.interrupt();
		clientTable.remove(myClientsName);
	}
}

