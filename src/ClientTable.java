
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientTable {
	
	private ConcurrentMap<String, Account> userAccounts = new ConcurrentHashMap<>();

	/**
	 * Registers an account to the client table
	 * @param nickname The name of the user
	 * @param password the encrypted password of the user
	 */
	public void add(String nickname, Password password) {
		userAccounts.put(nickname, new Account(nickname, password));
	}

	/**
	 * Gets the message log of a specified user.
	 * @param nickname The user to access.
	 * @return The MessageLog object stored in the user's account.
	 */
	public MessageLog<Message> getMessageLog(String nickname) {
		return userAccounts.get(nickname).getLog();
	}

	/**
	 * Gets the blocking queue of a specific login.
	 * @param nickname The name of the user to access. 
	 * @param id The instance ID of the user's login.
	 * @return The BlockingQueue of the specified login
	 */
	// Returns null if the nickname is not in the table:
	public BlockingQueue<Message> getQueue(String nickname, String id) {
		return userAccounts.get(nickname).getQueue(id);
	}

	/**
	 * Removes an entry from the userAccounts HashTable
	 * @param nickname The name of the user to remove.
	 */
	public void remove(String nickname) {
		userAccounts.remove(nickname);
	}
	
	/**
	 * Searches the table if it has a specified user.
	 * @param nickname The name of the user to search
	 * @return True if the user account is in the HashTable, false otherwise.
	 */
	public boolean has(String nickname) {
		return userAccounts.containsKey(nickname);
	}
	
	/**
	 * Adds a BlockingQueue to a user. Represents another login.
	 * @param nickname Name of the user to access.
	 * @param id The instance ID of the user's login.
	 */
	public void addQueue(String nickname, String id) {
		userAccounts.get(nickname).addQueue(id);
	}
	
	/**
	 * Removes a BlockingQueue from a user. Represents a logout.
	 * @param nickname The name of the user to access.
	 * @param id The instance ID of teh user's login.
	 */
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
		String pInput = pw.getSHA256(input);
		return pw.getHashedPassword().equals(pInput);
	}
	
	/**
	 * Creates a representation of what to save.
	 * @return A String which is what is stored when saved.
	 */
	public String saveTable() {
		StringBuilder table = new StringBuilder();
		
		for(String name : userAccounts.keySet()) {
			table.append(userAccounts.get(name).saveState());	
			table.append("\r\n");
		}
		
		return table.toString();
	}
	
	/**
	 * Sets the information in the client table according to a given data.
	 * @param data The data passed in.
	 */
	public void loadTable(String data) {
		BufferedReader dataReader = new BufferedReader(new StringReader(data));
		String accountLine;
		try {
			while((accountLine = dataReader.readLine()) != null) {
				if (accountLine.equals("\\BEGIN ACCOUNT\\")) {
					
					String name = dataReader.readLine();
					Password password = new Password (dataReader.readLine(), true);
					
					ArrayList<Message> logToMake = new ArrayList<>();
					String message;
					while(!(message = dataReader.readLine()).equals("\\END ACCOUNT\\")) {
						String[] messageComponents = message.split(" ", 3); //splits the string of the message
						String nameToMake = 
								messageComponents[1].substring(0, messageComponents[1].length() - 1); //gets the name
						String textToMake = 
								messageComponents[2]; //gets the message
						logToMake.add(new Message(nameToMake, textToMake));
					}
					add(name, password);
					getMessageLog(name).setList(logToMake);
				}
				
					
			}	
		}
		catch(IOException e) {
			Report.error("Error: problem reading data: " + e.getMessage());
		}
	}
}
