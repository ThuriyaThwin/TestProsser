package messages;

// this is the message that will be sent to neighbors that are not parent and not children 
// on the BFS Tree
public class MessageOKAnyTime extends MessageOK{

	public int id; // the id of sender
	public int current_value; // the current value of sender
	public boolean terminate;
	
	public MessageOKAnyTime(int id, int current_value, boolean terminate) {
		super(id, current_value);
		this.current_value = current_value;
		this.id = id;
		this.terminate = terminate;

	}

}
