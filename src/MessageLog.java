
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * A MessageLog stores a list of messages and provides ways to traverse
 * between messages
 * @author alfis
 *
 * @param <E> the type of the message
 */
public class MessageLog<E> {

	private ArrayList<E> list;
	private transient ListIterator<E> iterator;
	private E current;
	
	public MessageLog() {
		list = new ArrayList<E>();
		iterator = list.listIterator();
		current = null;
	}
	
	/**
	 * Gets the list of messages
	 * @return ArrayList containing all the messages
	 */
	public ArrayList<E> getList() {
		return list;
	}
	
	/**
	 * Sets the list
	 * @param l The ArrayList to be set
	 */
	public void setList(ArrayList<E> l) {
		list = l;
		iterator = l.listIterator();
		while(iterator.hasNext()) current = iterator.next();
	}
	
	/**
	 * Adds an element to the end of the list and makes that the current message
	 * @param e the message
	 */
	public void add(E e) {
		while(iterator.hasNext()) iterator.next();
		
		iterator.add(e);
		current = iterator.previous();
		iterator.next();
	}
	
	/**
	 * Gets the current message
	 * @return Message object of the current message
	 * @throws NullPointerException if the current message doesn't exist yet 
	 */
	public E getCurrent() throws NullPointerException{
		return current;
	}
	
	/**
	 * Gets the next message by moving the iterator to the next
	 * element, if possible, and sets that as the current message
	 * @return the next message
	 */
	public E getNext() throws NullPointerException{
		E temp = null;
		if (iterator.hasNext()) {
			do {
				temp = iterator.next();
			} while(temp == current && iterator.hasNext());
			current = temp;
		}
		return getCurrent();
	}
	

	/**
	 * Gets the next message by moving the iterator to the previous
	 * element, if possible, and sets that as the current message
	 * @return the previous message
	 */
	public E getPrevious() throws NullPointerException{
		E temp = null;
		if (iterator.hasPrevious()) {
			do {
				temp = iterator.previous();
			} while(temp == current && iterator.hasPrevious());
			current = temp;
		}
		return getCurrent();
	}
	
	/**
	 * Uses the iterator to delete the current message
	 */
	public void delete() throws NullPointerException{
		try {
			iterator.remove();
			if(iterator.hasNext()) current = iterator.next();
			else if(iterator.hasPrevious()) current = iterator.previous();
			else current = null;
		}
		catch (IllegalStateException e) {
			Report.error("Error: cannot delete item from empty list.");
		}
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
}
