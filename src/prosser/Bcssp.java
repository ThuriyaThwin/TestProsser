package prosser;
import general.Definitions;
import general.Problem;

import java.io.PrintStream;
import com.sosnoski.util.stack.IntStack;

//import com.sosnoski.util.stack.IntStack;

/*****************************************************************************************
 * 
 * this file is a generalized solver it contains the method bcssp and the variables 
 * that all solvers should contain. In addition it contains definition of abstract 
 * methods label/unlabel that are implemented by explicit classes. 
 * It also contains some general help methods for printing solution and checking it 
 *
 *****************************************************************************************/
public abstract class Bcssp {

	protected boolean consistant; // consistant variable from algorithem 
	protected Problem problem;    // the problem to solve
	protected int v[];            // the assigment/solution vector
	protected int n;  // these can be taken from probelem but put here to simplify code
	protected int d;
	public int assignments;  // how many assignments were made?

	// should be implemeted by the Algorithem classes
	public abstract int label(int i);
	public abstract int unlabel(int i);
	
	// Init variables commong to all implementations 
	public Bcssp(Problem problem) {
		this.problem = problem;
		n = problem.getN();
		d = problem.getD();
		assignments = 0;
	}
	
	// run the algorithem main loop
	public Definitions.StatOptions bcssp() {
		Definitions.StatOptions status = Definitions.StatOptions.UNKNOWN;
		consistant = true;
		int i = 0;
		
		
		while(status == Definitions.StatOptions.UNKNOWN) {
			if (consistant) 
				i = label(i);
			else
				i = unlabel(i);
			
			if (i >= problem.getN()) 
				status  = Definitions.StatOptions.SOLUTION;
			else if (i == -1) 
				status = Definitions.StatOptions.IMPOSSIBLE;
		}
	
		/*  This is the printing of result as suggested by assignment it is
		 *  commented out since I didn't find any use to it.
		 
		if (status == StatOptions.SOLUTION)
			printV();
		else 
			System.out.print("No Solution !!! ");
		
		System.out.println(",CCs=" + problem.constraint_checks + ",Assigments=" + assignments);
		*/
		
		return status;
	}
	

	// print the results to a PrintStream (you can use System.out
	public void printV(PrintStream output) {
		output.print("Assignment=");
		for (int i = 0; i < problem.getN(); i++)
			output.print("<" + i + "," + v[i] + ">,");
	}
	
	// check that the solutions are the same as in anothr Bcssp
	// used to make sure that FC-CBJ-DAC and FC-CBJ return the same
	// results (was also used with CBT
	public boolean CompareResults(Bcssp other) {
		for (int i = 0; i < problem.getN(); i++) {
		    if (v[i] != other.v[i]) 
		    	return false;
		}
		
		return true;
	}
	
	// check that the results that are currently in v satisfy the constraints in problem
	public boolean check_results() {
	
		for (int i = 0; i < problem.getN(); i++) {
			for (int j = i+1; j < problem.getN(); j++) {
				if (! problem.check(i, v[i], j, v[j]))
		    	   return false;
			}
		}
		
		return true;
	}
	
	// that returns the max element of an IntStack 
	//(since IntStack didn’t contain such a method and.
	// due the naature of problem we know that only 0 and 
	// positive number are used with this function so we can return -1 if it was empty
	
	public static int max_list(IntStack list) {
		int result = -1;
	    for (int l = 0; l < list.size(); l++){
			int k = list.peek(l);
			if (k > result)
				result = k;
		}
		
	    return result;
	}
	
}


