
/**
 * @author Miriam k.
 * @author Elad l.
 */


package agents;
import messages.*;

/**
 * 
 * holds the agent id and the in\out msg boxs 
 * @see MessageBox
 */
public class AgentInfo {
	public MessageBox<MessageOK> ok_message_box_in;
	public MessageBox<MessageOK> ok_message_box_out;
	public int id;
	
	/**
	 * 
	 * @param id
	 * @param ok_message_box_in
	 * @param ok_message_box_out
	 */
	public AgentInfo(int id, MessageBox<MessageOK> ok_message_box_in, MessageBox<MessageOK> ok_message_box_out) {
		this.id = id;
		this.ok_message_box_in = ok_message_box_in;
		this.ok_message_box_out = ok_message_box_out;
		
	}
}
