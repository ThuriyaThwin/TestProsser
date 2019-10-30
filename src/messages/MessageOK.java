package messages;

// this is uses when anytime is not set
public class MessageOK {

	public int id; // the id of sender
	public int current_value; // the current value of sender
	
	public MessageOK(int id, int current_value) {
		this.current_value = current_value;
		this.id = id;

	}

}
