
// Each nickname has a different incoming-message queue.

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientTable {

	private ConcurrentMap<String, BlockingQueue<Message>> queueTable = new ConcurrentHashMap<>();
	private ConcurrentMap<String, MessageLog<Message>> log = new ConcurrentHashMap<>();

	public void add(String nickname) {
		queueTable.put(nickname, new LinkedBlockingQueue<Message>());
		log.put(nickname, new MessageLog<Message>());
	}
	
	public MessageLog<Message> getMessageLog(String nickname) {
		return log.get(nickname);
	}

	// Returns null if the nickname is not in the table:
	public BlockingQueue<Message> getQueue(String nickname) {
		return queueTable.get(nickname);
	}

	// Removes from table:
	public void remove(String nickname) {
		queueTable.remove(nickname);
		log.remove(nickname);
	}
	
	// Searches the table:
	public boolean has(String nickname) {
		return queueTable.containsKey(nickname) && log.containsKey(nickname);
	}
}
