import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Account {

	private String nickname;
	private BlockingQueue<Message> queue;
	private MessageLog<Message> log;
	
	public Account(String name) {
		nickname = name;
		queue = new LinkedBlockingQueue<Message>();
		log = new MessageLog<Message>();
	}
	
	public String getName() {
		return nickname;
	}
	
	public BlockingQueue<Message> getQueue() {
		return queue;
	}
	
	public void setQueue(boolean isLoggedIn) {
		if(isLoggedIn && queue == null) {
			queue = new LinkedBlockingQueue<Message>();
		}
		else if (!isLoggedIn && queue != null) {
			queue = null;
		}
	}
	
	public MessageLog<Message> getLog() {
		return log;
	}
	
}
