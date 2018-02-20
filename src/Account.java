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
	
	public String getName() {
		return nickname;
	}
	
	public void addQueue(String id) {
		queues.put(id, new LinkedBlockingQueue<Message>());
	}
	
	public void removeQueue(String id) {
		queues.remove(id);
	}
	
	public BlockingQueue<Message> getQueue(String id) {
		return queues.get(id);
	}
	
	public Password getPassword() {
		return password;
	}
	
	/**
	 * Gets a list of all blocking queues, one for each instance of a login
	 * @return ArrayList of client blocking queues
	 */
	public ArrayList<BlockingQueue<Message>> getAllQueues() {
		ArrayList<BlockingQueue<Message>> listOfQueues= new ArrayList<>();
		for(String instanceID : queues.keySet()) {
			listOfQueues.add(queues.get(instanceID));
		}
		return listOfQueues;
	}
	
	public MessageLog<Message> getLog() {
		return log;
	}
	
}
