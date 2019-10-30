
/**
 * @author Miriam k.
 * @author Elad l.
 */



/**
 * This class is used in AnyTime implementation for BFS root and other parent nodes in order to propagate
 * to their BFS children the index of step with best value
 */
public class MessageBestIndex {
	public int best_step_index; // the id of sender
	
	/**
	 * Sets the best_step_index
	 * @param best_step_index
	 */
	public MessageBestIndex(int best_step_index) {
		this.best_step_index = best_step_index;
	}

}