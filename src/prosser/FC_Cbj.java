package prosser;

import java.util.*;

import general.Problem;
import general.ValSet;

import com.sosnoski.util.stack.IntStack; 

public class FC_Cbj extends Bcssp {

	private ValSet current_domain[];
	private Stack<IntStack>[] reductions;
	private IntStack[] future_fc;
	private IntStack[] past_fc;
	ValSet conf_set[];
	
	public FC_Cbj(Problem problem) {
		super(problem);

		v = new int [n];
		current_domain = new ValSet[n];
		reductions = new Stack[n];	
		future_fc = new IntStack [n];
		past_fc = new IntStack[n];
		conf_set = new ValSet[n];
		
		for (int i = 0; i < n ; i++) {
			reductions[i] = new Stack();
			future_fc[i] = new IntStack();
			past_fc[i] = new IntStack();	
			conf_set[i] = new ValSet(n);
			current_domain[i] = new ValSet(d);
			current_domain[i].fill();
		}
		

	}
	
	private boolean check_foward(int i, int j) {
		//System.out.println("check_foward " + i + "," + j);
		IntStack reduction = new IntStack();
		int d_index = 0;
		
		
		// for v[j] <-- each element of current_domain[j]
		while(d_index < d) {
			if (! current_domain[j].isMember(d_index)) {
				d_index++;
				continue;
			}
			
			v[j] = d_index;
			//assignments++;
			
			if (! problem.check(i, v[i], j, v[j]))
				reduction.push(new Integer(v[j]));
			
			d_index++;
		}
		
		if (! reduction.isEmpty()) {
			
			// current_domain[j] <-- set_diffrence(current_domain[j], reduction)
			current_domain[j].setDiff(reduction);
			reductions[j].push(reduction);
			future_fc[i].push(j);
			past_fc[j].push(i);
		}
	
	    
		
	  //return(current_domain[j] != nil)
		return (! current_domain[j].isEmpty());         
	}
	
	
	private void undo_reductions(int i) {
		IntStack reduction;
		

		while (! future_fc[i].isEmpty()) {
			int j = future_fc[i].pop();
			reduction = reductions[j].pop();
			
			// current_domain[j] = union(current_domain[j], reduction)
			current_domain[j].union(reduction);
			past_fc[j].pop();
		}
	}
	
	private void update_current_domain(int i) {
		IntStack reduction;
		
		
		current_domain[i].fill();
		
		// foreach element of  reductions[i]
		Iterator<IntStack> iter1 = reductions[i].iterator();
		while (iter1.hasNext()) {
			reduction = iter1.next();
			
			// current_domain[i] = set_diffrence(current_domain[i], reduction)
			current_domain[i].setDiff(reduction);
		}
		
	}
	
	@Override
	public int label(int i) {
		//System.out.println("label " + i);
		consistant = false;

		int d_index = 0;
		while((d_index < d) && (! consistant)) {
			if (! current_domain[i].isMember(d_index)) {
				d_index++;
				continue;
			}
				
			v[i] = d_index;
			assignments++;
			consistant = true; 
			
			int j;		
			for (j = i+1 ; (j < n) &&  consistant ; j++) {
				consistant = check_foward(i,j); 
			}
			
			if (!consistant) {
				current_domain[i].remove(v[i]);
 				undo_reductions(i);
				conf_set[i].union(past_fc[j-1]);
				conf_set[i].remove(i);
			}
			
			d_index++;
		}
			
		if (consistant)
			return (i+1);
		else
			return (i);
	}

	@Override
	public int unlabel(int i) {
        int h;
        int max_conf_set = conf_set[i].get_max();
        int max_past_fc = max_list(past_fc[i]);
        
		h = Math.max(max_conf_set, max_past_fc);
		if (h == -1) {
			return h;
		}
		
		// conf_set[h] <-- union(conf_set[h], conf_set[i] 
		conf_set[h].union(conf_set[i]);
		
		// conf_set[h] = union(conf_set[h], past_fc[i]
		conf_set[h].union(past_fc[i]);

		
		// remove h
		conf_set[h].remove(h);
		

		for (int j = i; j > h; j--) {
            // conf_set[j] = {}
			conf_set[j].clear();
			undo_reductions(j);          
            update_current_domain(j);
		 }
		
		 undo_reductions(h);
	     //System.out.println("h : " + h + " v[h]: " + v[h]);
			
		current_domain[h].remove(v[h]);
			
		
		consistant = (! current_domain[h].isEmpty());

		
		return h;
	}
	
}
