
/**
 * @author Miriam k.
 * @author Elad l.
 */


package agents;


/**
 * DSA_B_Agent extends DSAAgent.
 * Implements the DSA with B selection function
 * 
 * @see DSAAgent
 * @see select_next_value
 *
 */
public class DSA_B_Agent extends DSAAgent{
	
	/**
	 * 
	 * @param id
	 * @param max_cycles
	 * @param p
	 * @param any_time
	 * @param d
	 * @param n
	 */
	public DSA_B_Agent(int id, int max_cycles, double p, boolean any_time, int d, int n) {
		super(id, max_cycles, p, any_time, d, n);
	}

	/**
	 * Selects next value with probability p iff delta > 0
	 * or if current_conflicts_count is not 0. 
	 * @param delta
	 * @param v
	 * @param p
	 */
	void select_next_value(int delta, int v, double p){
		if (delta > 0){
			change_value_with_prob(v,p);
		}	
		else if ((delta== 0) && (0 != current_conflicts_count)){
			/* there is at least one conflict */
			change_value_with_prob(v,p);
		}		
	}
}
