
/**
 * @author Miriam k.
 * @author Elad l.
 */


package agents;

/**
 * DSA_C_Agent extends DSAAgent.
 * Implements the DSA with C selection function
 * 
 * @see DSAAgent
 * @see select_next_value
 *
 */
public class DSA_C_Agent extends DSAAgent{
	
	/**
	 * 
	 * @param id
	 * @param max_cycles
	 * @param p
	 * @param any_time
	 * @param d
	 * @param n
	 */
	public DSA_C_Agent(int id, int max_cycles, double p, boolean any_time, int d, int n) {
		super(id, max_cycles, p, any_time, d, n);
	}

	/**
	 * Selects next value with probability p iff delta >= 0 
	 * @param delta
	 * @param v
	 * @param p
	 */
	void select_next_value(int delta, int v, double p){
		if (delta >= 0)
			change_value_with_prob(v,p);	
			
	}
}
