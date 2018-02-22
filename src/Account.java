
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An Account object holds all the necessary information for a user
 * when the register for the messaging service.
 * @author alfis
 *
 */
public class Account {
	
	private String nickname;
	private ConcurrentMap<String, BlockingQueue<Message>> queues;
	private MessageLog<Message> log;
	private Password password;
	
	public Account(String name, Password password) {
		nickname = name;
		queues = new ConcurrentHashMap<String, BlockingQueue<Message>>();
		this.password = password;
		log = new MessageLog<Message>();
	}
	
	/**
	* Gets the name of the account holder.
	* @return A String representing the name of the account holder. 
	*/
	public String getName() {
		return nickname;
	}
	
	/**
	* Adds a BlockingQueue to the list of queues. Represents another login.
	* @param id The id of the login instance helping to identify the queue.
	*/
	public void addQueue(String id) {
		queues.put(id, new LinkedBlockingQueue<Message>());
	}
	
	/**
	* Removes a BlockingQueue from the list of queues. Represents a logout.
	*/
	public void removeQueue(String id) {
		queues.remove(id);
	}
	
	/**
	* Gets a specified BlockingQueue.
	* @param id The id of the login instance helping to identify the queue.
	* @return A BlockingQueue with the specified id.
	*/
	public BlockingQueue<Message> getQueue(String id) {
		return queues.get(id);
	}
	
	/**
	* Gets the password of the account holder.
	* @return The Password object belonging to the account holder.
	*/
	public Password getPassword() {
		return password;
	}
	
	/**
	 * Gets a list of all blocking queues, one for each instance of a login.
	 * @return ArrayList of client blocking queues.
	 */
	public ArrayList<BlockingQueue<Message>> getAllQueues() {
		ArrayList<BlockingQueue<Message>> listOfQueues= new ArrayList<>();
		for(String instanceID : queues.keySet()) {
			listOfQueues.add(queues.get(instanceID));
		}
		return listOfQueues;
	}
	
	/**
	* Gets the list of messages that the user has received.
	* @return The MessageLog object belonging to the user.
	*/
	public MessageLog<Message> getLog() {
		return log;
	}
	
	/**
	* Creates a string of all the information held by the account. Used for
	* saving the data to an account. See SOLUTION.md for format.
	* @return A String in a specified format holding account information.
	*/	
	public String saveState() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\\BEGIN ACCOUNT\\")
		  .append("\r\n")
		  .append(nickname)
		  .append("\r\n")
		  .append(password.getHashedPassword())
		  .append("\r\n");
		
		for (Message message : log.getList()) {
			sb.append(message);
			sb.append("\r\n");
		}
		
		sb.append("\\END ACCOUNT\\");
		return sb.toString();
	}
}
