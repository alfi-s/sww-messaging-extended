
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientTable {
	
	/**
	 * A collection of all registered accounts
	 */
	private ConcurrentMap<String, Account> userAccounts = new ConcurrentHashMap<>();

	/**
	 * Registers an account to the client table
	 * @param nickname the name of the user
	 * @param password the encrypted password of the user
	 */
	public void add(String nickname, Password password) {
		userAccounts.put(nickname, new Account(nickname, password));
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
	
	/**
	 * Gets all the queues such that messages are sent everywhere the user is logged-in
	 * @param nickname name of the user
	 * @return an ArrayList of all the user's blocking queues, one for each login
	 */
	public ArrayList<BlockingQueue<Message>> getAllQueues(String nickname) {
		return userAccounts.get(nickname).getAllQueues();
	}
	
	/**
	 * Tests if a given string matches a certain user's password
	 * @param nickname the name of the user
	 * @param input the password that was passed in
	 * @return true if the password is correct, false otherwise
	 */
	public boolean testPassword(String nickname, String input) {
		Password pw = userAccounts.get(nickname).getPassword();
		String pInput = pw.getSHA256(input, pw.getSalt());
		return pw.getHashedPassword().equals(pInput);
	}
}
