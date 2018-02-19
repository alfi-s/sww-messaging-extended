
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientTable {

	private ConcurrentMap<String, Account> userAccounts = new ConcurrentHashMap<>();

	public void add(String nickname) {
		userAccounts.put(nickname, new Account(nickname));
	}
	
	public MessageLog<Message> getMessageLog(String nickname) {
		return userAccounts.get(nickname).getLog();
	}

	// Returns null if the nickname is not in the table:
	public BlockingQueue<Message> getQueue(String nickname, String id) {
		return userAccounts.get(nickname).getQueue(id);
	}

	// Removes from table:
	public void remove(String nickname) {
		userAccounts.remove(nickname);
	}
	
	// Searches the table:
	public boolean has(String nickname) {
		return userAccounts.containsKey(nickname);
	}
	
	
	public void addQueue(String nickname, String id) {
		userAccounts.get(nickname).addQueue(id);
	}
	
	public void removeQueue(String nickname, String id) {
		userAccounts.get(nickname).removeQueue(id);
	}
	
	public ArrayList<BlockingQueue<Message>> getAllQueues(String nickname) {
		return userAccounts.get(nickname).getAllQueues();
	}

}
