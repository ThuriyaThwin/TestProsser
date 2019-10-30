package messages;
/**
 * improve message for DBA 
 */

public class MessageImprove {
	public int id; // the id of sender
	public int imporove_val; // the planned improvement 
	public int current_eval; // the conflict evaluation result
	public int termination_counter; // termination counter
	
	public MessageImprove(int id, int imporove_val, int current_eval, int termination_counter) {
		this.id = id;
		this.current_eval = current_eval;
		this.imporove_val = imporove_val;
		this.termination_counter = termination_counter;
	}

}
