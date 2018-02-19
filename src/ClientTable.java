
// Each nickname has a different incoming-message queue.

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
	public BlockingQueue<Message> getQueue(String nickname) {
		return userAccounts.get(nickname).getQueue();
	}
	
	public void setQueue(String nickname, boolean isLoggedIn) {
		userAccounts.get(nickname).setQueue(isLoggedIn);
	}

	// Removes from table:
	public void remove(String nickname) {
		userAccounts.remove(nickname);
	}
	
	// Searches the table:
	public boolean has(String nickname) {
		return userAccounts.containsKey(nickname);
	}
}
