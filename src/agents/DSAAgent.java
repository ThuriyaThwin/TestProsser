
/**
 * @author Miriam k.
 * @author Elad l.
 */


package agents;

import java.util.Random;

/**
 * 
 * DSAAgent extends AbstractAgent
 * Implements the DSAAgent.
 * 
 * @see AbstractAgent
 *
 */
abstract public class DSAAgent extends AbstractAgent {
	int current_conflicts_count;
	int delta;

	boolean is_improve = false;
	Random rand_generator;
	
	/**
	 * 
	 * @param id
	 * @param max_cycles
	 * @param p
	 * @param any_time
	 * @param d
	 * @param n
	 */
	public DSAAgent(int id, int max_cycles, double p,  boolean any_time, int d, int n) {
		super(id, max_cycles, p, any_time, d, n);
		rand_generator = new Random();
		value = rand_generator.nextInt(d); 
		current_conflicts_count = evalueate(value); 
		delta = current_conflicts_count; 
	}
	
	/**
	 * The main alg part
	 * @param cycles
	 * 
	 */
	public void do_alg(int cycles) {
		while (! completed) {
			send_ok(); 
			wait_ok(); 
			if (step_no == cycles)
				completed=true;

	   }
	}
			
	
	/**
	 * 
	 * @return a new value (the lowest delta value) a new value (the lowest delta value)
	 * if there is one. otherwise the current value.
	 * also updates the delta.
	 */
	protected int get_lowest_delta_value(){
		/*
		 * returns  
		 * */
		
		is_improve = false;
		int best_value = -1;
		int after_read_conflicts_count;
		int new_conflicts_count = Integer.MAX_VALUE;
		
		for (int val = 0 ; val < d ; val++) {
			if (val == value)
				continue;
			after_read_conflicts_count = evalueate(val);
			if (after_read_conflicts_count < new_conflicts_count) {
				is_improve = true;	
				delta = current_conflicts_count - after_read_conflicts_count;
				new_conflicts_count = after_read_conflicts_count;
				best_value = val;	
			}
		}
		
		return best_value;
	}
	
	
	/**
	 * change the current value with probability of p.
	 * @param v
	 * @param p
	 */
	protected void change_value_with_prob(int v,double p){
		int i = rand_generator.nextInt(100);
		
		if (i <= (p * 100)){
			/* need to change the value*/
			value = v;
		}
	}
	
	/**
	 * abstract select_next_value.
	 * Implemened by each of the DBAs
	 * @param delta
	 * @param v
	 * @param p
	 * 
	 * @see DSA_A_Agent
	 * @see DSA_B_Agent
	 * @see DSA_C_Agent
	 * @see DSA_D_Agent
	 * @see DSA_E_Agent
	 */
	abstract void select_next_value(int delta, int v, double p); 
	
	/**
	 * waits of the neighbors ok mgs, and select the next value.
	 */
	protected void wait_ok() {
		read_neighbors_ok();
		int v = get_lowest_delta_value();
		select_next_value(delta, v, p);
		current_conflicts_count = evalueate(value);
	}
	
}
