
/**
 * @author Miriam k.
 * @author Elad l.
 */

package agents;

/**
 * DSA_D_Agent extends DSAAgent.
 * Implements the DSA with D selection function
 * 
 * @see DSAAgent
 * @see select_next_value
 *
 */

public class DSA_D_Agent extends DSAAgent{
	
	/**
	 * 
	 * @param id
	 * @param max_cycles
	 * @param p
	 * @param any_time
	 * @param d
	 * @param n
	 */
	public DSA_D_Agent(int id, int max_cycles, double p, boolean any_time, int d, int n) {
		super(id, max_cycles, p, any_time, d, n);
	}

	/**
	 * Selects next value with probability p 
	 * iff delta == 0 and current_conflicts_count is not 0. 
	 * if delta > 0, sets v as the next value.   
	 * @param delta
	 * @param v
	 * @param p
	 */
	void select_next_value(int delta, int v, double p){
		if (delta > 0){
			value = v;
		}	
		else if ((delta == 0) && (0 != current_conflicts_count)){
			change_value_with_prob(v,p);
		}		
	}
}
