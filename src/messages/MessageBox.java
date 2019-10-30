package messages;
/**
 * A generic message box that allows passing objects between different threads
 */
import java.util.*;
public class MessageBox<M> {
	LinkedList<M> messages;
	
	public MessageBox() {
		messages = new LinkedList<M>();
	}
	
	public synchronized void send_message(M message) {
		messages.addFirst(message);
		notify();
	}
	
	public synchronized M read_message() {
		while(messages.isEmpty()) {
			try {
			   wait();
			}
			catch (InterruptedException e) {
				// shouldn't get here
				System.out.println("read_message interuupted");
				System.exit(1);
			}
		}
		
		M ret_val =	messages.removeLast();
		return ret_val;
	}
	
}
