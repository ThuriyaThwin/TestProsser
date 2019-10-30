
/**
 * @author Miriam k.
 * @author Elad l.
 */


package agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import messages.MessageOK;
import messages.MessageOKAnyTime;
import messages.MessageOKAnyTime2Parent;
import messages.MessageOKAnyTime2Son;



/**
 * Class: AbstractAgent
 * This call implements methods that are common to all agent types
 * it handles any time required parameters
 * The decision whether or not the agent is an "anytime" agent is determined by constructor
 * @see DBAAgent
 * @see DSAAgent
 */

public abstract class AbstractAgent implements Runnable{
	public int id; // the id of current agent (public so solver can see it)
	protected int[][] weight_table[]; // how much does each conflict cost
	protected int agent_view[]; // agent view for neighbors
	                                              // tree map will enable going over only part of children
	protected int d;
	protected int n; // n is needed relay only for DBA termination counter
	public int value;   
	public int step_no;    // what cycle was reached?
	protected int max_cycles;    // after how many cycles to terminate if solution not found?
	public int messages_sent;  // how many messages did current agent send
	public int ncccs;          // What is the NCCCS of current agent
	protected double p; // the probability to change the current value is actualy used only by DSA
						// but we are setting it in all agents in order to enable common interface 
	protected int no_of_neighbors; // for fast looping over all neighbors
	
	// these are used in order to decide when  to stop
	protected int termination_counter; // when reaches n can stop
	protected boolean consistent; // no reason to change value
	AgentInfo neighbors[];
	protected int larger_neighbors_index; // this will point to index in agent_view where 
	                                  // after it all agents will have an id larger then currents agent
	HashMap<Integer,Integer> neighbor_id_map; // map id to index
	
	protected abstract void do_alg(int cycles);
	boolean completed=false; // will be set to false when not done
	
	// Variables for AnyTime implementation 
	protected boolean any_time=false;
	public final static int NULL = -1;
	private int bfs_parent_id = NULL;
	private HashSet<Integer> bfs_children;
	private int bfs_height;
	private int bfs_dist;
	private int best = NULL;
	public  int best_index = NULL;
	private int best_cost = Integer.MAX_VALUE; // this is used only by root

	private int cost_i[]; // this cost of steps history
	private int val_i[]; // the value history
	private int val_i_len;
	boolean terminate = false; // this os set to true for root if result was reached and 
	                           // for children if the got terminate message from parent
	int termination_count;

	/**
	 * 
	 * @param id
	 * @param max_cycles
	 * @param p
	 * @param any_time
	 * @param d
	 * @param n
	 */
	public AbstractAgent(int id, int max_cycles, double p, boolean any_time, int d, int n) {
		this.d = d;
		this.n = n;
		this.max_cycles = max_cycles;
		this.id = id;
		termination_counter = 0;
		consistent = false;
		this.any_time = any_time;
		this.p = p;

		//create map to idenitfy where in agent_view will agent's value be found
		neighbor_id_map = new HashMap<Integer, Integer>();
		
		value = (int) (Math.random() * d);
		step_no = -1;
		termination_count = Integer.MAX_VALUE;

	}
	/**
	 * 
	 * @param neighbors
	 * @param larger_neighbors_index
	 * @param weight_table
	 */
	public void init (AgentInfo neighbors[], int larger_neighbors_index, int[][] weight_table[] ){
		this.neighbors = neighbors;
		no_of_neighbors = neighbors.length;
		this.larger_neighbors_index = larger_neighbors_index;
		this.weight_table = weight_table;
		
		for (int i = 0; i < no_of_neighbors ; i++) {
		    neighbor_id_map.put(neighbors[i].id, i);        	
        }
		agent_view = new int[no_of_neighbors+1];
	}
	
	public int[] get_neighbors() {
		if (no_of_neighbors == 0)
			return new int[0];

		int[] result = new int[no_of_neighbors]; 
		
		for (int i = 0; i < no_of_neighbors; i++) {
			result[i] = neighbors[i].id;
		}	
		return result;
		
	}
	/**
	 * toString
	 * @return the string representation as html tag.
	 */
	public String toString()
	{
		String my_str;
		my_str = "<html><center>id:" + id + "<p>" + "val:" +  value;
		
		return my_str;
	}
	
	/**
	 * get_id
	 * @return id
	 */
	public int get_id() {
		return id;
	}
	
	/**
	 * sets the bfs parent
	 * @param parent_id
	 */
	public void set_bfs_parent(int parent_id) {
		bfs_parent_id = parent_id;
	}
	
	/**
	 * adds bfs child
	 * @param child_id
	 */
	public void add_bfs_child(int child_id) {
         if (bfs_children == null)
        	 bfs_children = new HashSet<Integer>();
         
         bfs_children.add(child_id);
	}
	
	/**
	 * sets bfs params
	 * @param dist
	 * @param height
	 */
	public void set_bfs_params (int dist, int height) {
		bfs_height = height;
		bfs_dist = dist;

        cost_i = new int[bfs_height];
        
        // need to start with maximal values in order to make sure that there 
        // are no false results
        for (int i = 0 ; i < bfs_height; i++) {
        	cost_i[i]= Integer.MAX_VALUE;
        }
        
        val_i_len = height + 2*dist;
        val_i = new int[val_i_len];
        
        for (int i = 0 ; i < val_i_len; i++) {
        	val_i[i]= NULL;
        }
        
        bfs_children = new HashSet<Integer>();
	}
	
	/**
	 * send OK msg (one of MessageOKAnyTime, MessageOK) to the agent neighbors.
	 * (uses any_time_send_ok()).
	 * @see MessageOK
	 * @see any_time_send_ok
	 */
	protected void send_ok() {			
		step_no++;
		
		if (any_time) {
			any_time_send_ok();
		}
		else {
			MessageOK message = new MessageOK(id, value);
			
			for (int i = 0 ; i < no_of_neighbors; i++) {
			    neighbors[i].ok_message_box_out.send_message(message);
			    messages_sent++;
			}
		}
	}
	
	/**
	 * sends the MessageOKAnyTime to the agent neighbors.
	 */
	public void any_time_send_ok() {
		
		MessageOKAnyTime message = new MessageOKAnyTime(id, value, terminate);
		int cost = Integer.MAX_VALUE;
		
		int i = step_no - bfs_height;
		
		if (i >= 0) {
			cost = cost_i[i%bfs_height];
		}

		MessageOKAnyTime2Parent parent_message = new MessageOKAnyTime2Parent (id, value, cost, i,  terminate);
		MessageOKAnyTime2Son child_message = new MessageOKAnyTime2Son(id, value, best_index, terminate);
		
		for (int k = 0 ; k < no_of_neighbors; k++) {
		    int neighbor_id = neighbors[k].id;

		    if (bfs_children.contains(neighbor_id)) {
		    	neighbors[k].ok_message_box_out.send_message(child_message);
		    }
		    else if (bfs_parent_id == neighbor_id) {
		    	neighbors[k].ok_message_box_out.send_message(parent_message);
		    }
		    else {
		    	neighbors[k].ok_message_box_out.send_message(message);
		    }
		    messages_sent++;

		}
		
		
	}
	/**
	 * waits of all neighbors ok reply
	 */
	protected void read_neighbors_ok(){

		if (any_time) {
			any_time_read_neighbors_ok();
		}
		else {
			for(int neighbor_index = 0; neighbor_index < no_of_neighbors; neighbor_index++) {
				MessageOK message = neighbors[neighbor_index].ok_message_box_in.read_message();	
				agent_view[neighbor_index] = message.current_value;
			}
		}
	}
	
	/**
	 * waits of all neighbors MessageOKAnyTime reply
	 * @see MessageOKAnyTime
	 */
	public void any_time_read_neighbors_ok() {
			
		int i = step_no - bfs_height + 1;
		
		for(int neighbor_index = 0; neighbor_index < no_of_neighbors; neighbor_index++) {
			MessageOKAnyTime message = (MessageOKAnyTime) neighbors[neighbor_index].ok_message_box_in.read_message();
			
			if (terminate)  // after reaching terminate we don't need the info in messages any more 
				continue;
			
			if (message.terminate == true) {
				terminate = true;
				termination_count = this.bfs_height;
			}
			
			agent_view[neighbor_index] = message.current_value;

			
			if (message.id == bfs_parent_id) {
				MessageOKAnyTime2Son parent_message = (MessageOKAnyTime2Son) message;
				
				if (parent_message.best_index != best_index) {
					best_index = parent_message.best_index ;
					best = val_i[best_index%val_i_len];
				}
			}
			else if (bfs_children.contains(message.id)) {
				MessageOKAnyTime2Parent child_message = (MessageOKAnyTime2Parent) message;
				if (child_message.step_no >= 0)
				    cost_i[child_message.step_no%bfs_height] += child_message.cost_i;
			}
		}
  
		
		val_i[step_no%val_i_len] = value;
	    cost_i[step_no%bfs_height] = evalueate(value);

		if (i >= 0) {
			//root
			if ((bfs_parent_id == NULL) && (cost_i[i%bfs_height] < best_cost)) {
				best_cost = cost_i[i%bfs_height];
				best = val_i[i%val_i_len];
				best_index = i;
				if (best_cost == 0) {
					terminate = true;
					termination_count = this.bfs_height;
				}
			}
		
		}
		
		//TODO
		// This is the place where automatic termination can be set on and off
		// it is a good Idea to move it to be part of the class
		if (terminate)
			if (termination_count-- == 0) {
				completed = true;
			}
		
	}
	/**
	 * evalueate
	 * @param current_val
	 * @return
	 */
	protected int evalueate(int current_val) {
		int eval = 0;
		for (int i=0; i < no_of_neighbors; i++) {
			ncccs++;
			if (weight_table[i][current_val][agent_view[i]] > 0)
			eval += 1;
		}
		
		return eval;
	}
	
	/**
	 * run function - runs the do alg of the derived classes
	 */
	public void run() {
		// if there are no conflicts then the first value can be selected and nothing needs to be checked
		if (no_of_neighbors == 0) {
			value = 0;	
		}
		else {
	        do_alg(max_cycles + bfs_dist + bfs_height);
	        if (any_time && (termination_count != 0)) {
	           post_alg_steps();
	           value = best;
	        }   
        }
		
	}
	/**
	 * post_alg_steps
	 */
	public void post_alg_steps() {
        for (int k = 0; k < (bfs_dist + bfs_height) ; k++) {
        	step_no++;
            // send message to children
	        MessageOKAnyTime2Son child_message = new MessageOKAnyTime2Son(id, value, best_index, true);
			
	        Iterator<Integer> iter = bfs_children.iterator();
	        while (iter.hasNext()) {
	        	int child_id = iter.next().intValue();
	        	int child_index = neighbor_id_map.get(child_id);
	        	neighbors[child_index].ok_message_box_out.send_message(child_message);
	        	messages_sent++;
	        }

        	// read parent message
        	if (bfs_parent_id != NULL) {
        		int parent_index = neighbor_id_map.get(bfs_parent_id);
	        	MessageOK message = neighbors[parent_index].ok_message_box_in.read_message();
	        	if (message.id != bfs_parent_id) {
					System.out.println("Bug !!! got a non parent message at post_steps");
					System.out.println("message id is " + message.id + " id is" + id);
					continue;
	        	}
	        	
	        	MessageOKAnyTime2Son parent_message = (MessageOKAnyTime2Son) message;			
				if (parent_message.best_index != best_index) {
					best_index = parent_message.best_index ;
					best = val_i[best_index%val_i_len];
				}
        	}        	   
			

        }
	}
		
	/**
	 * return true iff the other object is the same to "this".
	 * @param other
	 * @return
	 */
	public boolean equals(Object other) {
		AbstractAgent otherVar = (AbstractAgent)  other;
	      
	      return (otherVar.id == this.id);
	}
	
	// TODO remove this is for debug only
	public void print_agent_view() {
		String str = "agent view of id " + id +":";
		for(int i = 0; i < no_of_neighbors; i++) {
			int neighbor_id = neighbors[i].id;
			str += "  id=" + neighbor_id+ " val=" + agent_view[i]; 
		}
		System.out.println(str);
		
	}
}
