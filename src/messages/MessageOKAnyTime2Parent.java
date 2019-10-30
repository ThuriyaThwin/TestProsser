package messages;

//This is the message the will be sent to BFS parent when Any Time is implemented
public class MessageOKAnyTime2Parent extends MessageOKAnyTime {
	public int cost_i;
	public int step_no;
	
	public MessageOKAnyTime2Parent (int id, int current_value, int cost_i, int step_no, boolean terminate) {		
		super(id, current_value, terminate); 
		this.cost_i = cost_i;
		this.step_no = step_no;
	}
}
