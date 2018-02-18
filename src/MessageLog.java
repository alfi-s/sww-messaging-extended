import java.util.ArrayList;
import java.util.ListIterator;

public class MessageLog<E> {
	
	private ArrayList<E> list;
	private ListIterator<E> iterator;
	private E current;
	
	public MessageLog() {
		list = new ArrayList<E>();
		iterator = list.listIterator();
		current = null;
	}
	
	public void add(E e) {
		while(iterator.hasNext()) iterator.next();
		
		iterator.add(e);
		current = iterator.previous();
		iterator.next();
	}
	
	public E getCurrent() {
		return current;
	}
	
	public E getNext() {
		if (iterator.hasNext()) current = iterator.next();
		return getCurrent();
	}
	
	public E getPrevious() {
		if (iterator.hasPrevious()) current = iterator.previous();
		return getCurrent();
	}
	
	public void delete() {
		try {
			iterator.remove();
		}
		catch (IllegalStateException e) {
			Report.error("Error: cannot delete item from empty list.");
		}
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
}
