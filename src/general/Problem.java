
/**
 * @author Miriam k.
 * @author Elad l.
 */



package general;
/* hello world this is a test mark */
import java.io.*;
/**
 * This class represents the problem 
 * it contains the constraints table and other arrays with info needed by different heuristics 
 * Created Feb 2009
 * @author Miriam Kreisler
 */


import javax.swing.JOptionPane;
import com.sosnoski.util.stack.IntStack;

/**
 * Implements a CSP 
 * @author user
 *
 */
public class Problem implements Serializable {

	private int n;  // number of variable in problem
	private int d;  // size of domain 

	
	public int constraint_checks;  // number of constrain checks done
	                               // is incremented when check is called
	                               // when get_conflicts* functions are called
	                               // when data is setup for fast conflicts evaluation 
	private boolean[][] constraints[][]; // contains for each pair of variables 
	                                     // for each value in domain false of there is a constraint 
	                                     // true if there is not - in the case that the variables are not
	                                     // constrained at all constraints[i][j] is null
	
	private int conflict_count[]; // for each variable it counts the number of constraints the variable has 
	                              // with the other variables (this is filled upon request
	                              // only when setup_conflict_count is called - and this happens the first time
	                              // get_conflicts is called
	
	private int conflict_count_for_val[][];  // for each 2 variables count the nuber of conflicts between them
	
	static final long serialVersionUID = 42L;  // this is needed in order to save samples to disk

	
	/**
	 * create an instance of a problem with n variables and domain size d
	 * @param n
	 * @param d
	 * @param p1
	 * @param p2
	 */
    
	public Problem(int n, int d, double p1, double p2) {
    
       init(n,d);
    	
    	for (int v1 = 0; v1 < n; v1++) {
    		constraints[v1][v1] = null;
    		for (int v2 = v1 + 1; v2 < n; v2++) {
    			double rnd1 = Math.random();
    			if ((rnd1 < p1)) {
    				constraints[v1][v2] = new boolean[d][d];
    				constraints[v2][v1] = new boolean[d][d];
    				boolean conflict_created = false;
	    			for (int d1 = 0; d1 < d; d1++)
	    				for (int d2 = 0; d2 < d; d2++) {
	    					double rnd2 = Math.random();
	    					if ((rnd2 < p2)) {
	    						constraints[v1][v2][d1][d2] = false;
	    						constraints[v2][v1][d2][d1] = false;
	    						conflict_created = true;
	    					}
	    					else {
	    						constraints[v1][v2][d1][d2] = true;
	    						constraints[v2][v1][d2][d1] = true;
	    					}
	    				}
	    			// if there are no conflicts then remove the conflict table
	    			if (! conflict_created) {
	    				constraints[v1][v2] = null;
	    				constraints[v2][v1] = null;
	    			}
	    				
    			}
    			else {
    				constraints[v1][v2] = null;
    				constraints[v2][v1] = null;
    			}
    		}
    	}
    }
    
   /**
    * create an queens instance
    * @param n
    */
	public Problem(int n) {
    	
        init(n,n);
    	
    	for (int v1 = 0; v1 < n; v1++)
    		for (int v2 = 0; v2 < n; v2++) {
    				constraints[v1][v2] = new boolean[d][d];
	    			for (int d1 = 0; d1 < d; d1++)
	    				for (int d2 = 0; d2 < d; d2++) {
	    					if (d1== d2)
	    						constraints[v1][v2][d1][d2] = false;
	    					else if ((d1 - v1) == (d2 - v2))
	    						constraints[v1][v2][d1][d2] = false;
	    					else if ((d1 + v1) == (d2 + v2))
	    						constraints[v1][v2][d1][d2] = false;
	    					else
	    						constraints[v1][v2][d1][d2] = true;
	    				}
    		}
    	
    }
   
    /**
     * 
     * @return n
     */
	public int getN() {
    	return n;
    }
    
    /**
     * 
     * @return d
     */
	public int getD() {
    	return d;
    }
  
    /**
     * Read a text file in the format of examples that were gived (was used for debugging)
     * @param n
     * @param d
     * @param fileName
     */
    public Problem(String fileName) {
    	ObjectInputStream input = null;
    	
    	
    	// open the file
        try {
        	  input = new ObjectInputStream(
              new FileInputStream( fileName ) );
        	  
        	  Problem problem = (Problem) input.readObject();
        	  this.n = problem.n;
        	  this.d = problem.d;
        	  this.constraints = problem.constraints;
        }
        
        
        // process exceptions from writeign and closing file 
        catch( IOException ioException ) {
           JOptionPane.showMessageDialog( null, 
              "Error reading file " +  fileName + "\n" + ioException.getMessage(),  "",
              JOptionPane.ERROR_MESSAGE );
           System.exit( 1 );
        }   

        catch (ClassNotFoundException cnf) {
		    // should put in status panel
		    System.err.println("Class Problem not found: " + cnf.getMessage());
		    System.exit( 1 );
		}
    	
    }
    
    /**
     * this is common to all the constructors 
     * @param n
     * @param d
     */
	private void init(int n, int d) {
	  	this.n = n;
    	this.d = d;
    	
    	constraint_checks = 0;
    	constraints = new boolean[n][n][][];
	}

	
	/**
	 * read the lines from a file in the format that was given in examples
	 * @param v1
	 * @param v2
	 * @param line
	 */
    private void setConstraintsFromLine(int v1, int v2, String line) {
		 int colon_location = line.indexOf(':');
		 String val1_str = line.substring(colon_location-1,colon_location);
		 int val1 = Integer.parseInt(val1_str);
		 int end_val_2 = line.indexOf(']');
		 String val2_srings = line.substring(colon_location+2, end_val_2);
		 String vals_array[] = val2_srings.split("\\s*,\\s*");
		 for (int i = 0 ; i < vals_array.length; i++) {
			 int val2 = Integer.parseInt(vals_array[i]);
			 if (constraints[v1][v2] == null) {
				 constraints[v1][v2] = new boolean[d][d];
				 constraints[v2][v1] = new boolean[d][d];
				 for (int q=0; q < d; q++) {
					 for (int r=0; r < d; r++) {
						 constraints[v1][v2][q][r]=true;
						 constraints[v2][v1][q][r]=true;
					 }
				 }
			 }
			 constraints[v1][v2][val1][val2]= false;
			 constraints[v2][v1][val2][val1]= false;
		 }
 	
    }
    
    /**
     *  load from a file
     * @param n
     * @param d
     * @param fileName
     */
    public Problem(int n, int d,String fileName) {
    	BufferedReader input = null;
    	
      init(n,d);
	  
	  for (int v1 = 0; v1 < n; v1++)
  		for (int v2 = 0; v2 < n; v2++) {
  		   constraints[v1][v2] = null;			
  		}
	
    	
    	// open file
        try {
        	  input = new BufferedReader(
              new FileReader( fileName ) );
        	  int v1=0, v2=0;
        	  String line;
        	  
        	  while ((line = input.readLine()) != null)
        	  {
	              if (line.contains("---")) {
		        	  String v1_str = line.substring(0, 1);
		        	  v1 = Integer.parseInt(v1_str);
		        	  
		        	  String v2_str = line.substring(4, 5);
		        	  v2 = Integer.parseInt(v2_str);
	              }      
	              else if (line.contains(":")) {
		        	setConstraintsFromLine(v1, v2, line);
		           }

        	  }
        }
        
        
        // process exceptions from writeign and closing file 
        catch( IOException ioException ) {
           JOptionPane.showMessageDialog( null, 
              "Error reading file " +  fileName + "\n" + ioException.getMessage(),  "",
              JOptionPane.ERROR_MESSAGE );
           System.exit( 1 );
        }   

    	
    }
    
    /**
     * This is the function that does the actual constraint checks
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return true if there is no constraint between var1=val1 and val2=var2
     *         false if these two are not permitted together 
     */
    public boolean check(int var1, int val1, int var2, int val2) {
    	constraint_checks++;
    	//System.out.println (var1 +  ", "+  val1 + ", " + var2 +  ", " + val2); 
    	if (constraints[var1][var2] == null)
    		return true;
    	
    	return constraints[var1][var2][val1][val2];
    }
    
    /**
     * do the two variables have conflicts among them?
     * @param var1
     * @param var2
     * @return
     */
    public boolean has_conflict(int var1, int var2) {
    	if (constraints[var1][var2] == null)
    		return false;
    	
    	return true;
    }
    
    
    /**
     * This functions saves the current object to a file
     * it can then be read from the file by the constructor that
     * accepts file names
     * @param fileName
     */
    public void save2File(String fileName) {
    	ObjectOutputStream output = null;
    	
    	
    	// open file
        try {
        	  output = new ObjectOutputStream(
              new FileOutputStream( fileName ) );
        }

        // process exceptions from opening file
        catch ( IOException ioException ) {
           JOptionPane.showMessageDialog(null, "Error Opening File",fileName, JOptionPane.ERROR_MESSAGE );
           System.exit(1);
        } 
     
        
        // write data and  close file 
        try {
           output.writeObject(this);
           output.close();

        }

        // process exceptions from writeign and closing file 
        catch( IOException ioException ) {
           JOptionPane.showMessageDialog( null, 
              "Error writing to file" + fileName,  "",
              JOptionPane.ERROR_MESSAGE );
           System.exit( 1 );
        }  
        
    	return;
    }
    
    
    /**
     * prints out the constrains (in a format similar to the examples we got)
     * @param output
     */
    public void printProblem(PrintStream output) {
    	output.println("n is: " + n);
    	output.println("d is: " + d);
    	
       	for (int v1 = 0; v1 < n; v1++)
    		for (int v2 = v1+1; v2 < n; v2++) {
    			if (constraints[v1][v2] != null) {
    				output.println(v1 + "---" + v2 + ":");
	    			for (int d1 = 0; d1 < d; d1++) {
	    				IntStack stack = new IntStack();
	    				for (int d2 = 0; d2 < d; d2++) {
	    					if (! constraints[v1][v2][d1][d2])
	    						stack.push(d2);
	    				}
	    			    if (! stack.isEmpty()) {
	    			    	output.print("          " + d1 + ":[");
	    			    	output.print(stack.peek(0));
	    			    	for (int i = 1; i < stack.size(); i++) {
	    			    		output.print("," + stack.peek(i));
	    			    	}
	    			    	output.println("]");
	    			    		
	    			    }
	    			}
    			}
    		}
    	
    }
    
    
    /**
     * creates an array where for each variable the number of conflicts is held 
     * each access to constraints[v1][v2]... is counted as a constraint check
     */
    public void setup_conflict_count() {
    	conflict_count = new int[n];
    	  	
    	for (int v1 = 0; v1 < n; v1++)
    		for (int v2 = 0; v2 < n; v2++) {
    			// need to count constraint checks since this is what we are doing here
    			constraint_checks++;
    			if (constraints[v1][v2] == null) 
    				continue;
	    			for (int d1 = 0; d1 < d; d1++)
	    				for (int d2 = 0; d2 < d; d2++) {
	    					if (! constraints[v1][v2][d1][d2]) {
	    						conflict_count[v1]++;
	    						conflict_count[v2]++;
	    					}
	    				}
    		}
    	
    }
    
    /**
     * @param i
     * @return the number of conflicts variable i has
     */
    public int get_conflicts(int i) {
       if (conflict_count == null) 
    	   setup_conflict_count();
   
       constraint_checks++;
        return  conflict_count[i];
    	
    }
    
    /**
     * creates an array counting the number of conflicts that one variable has for each
     * value in the domain
     */
    public void setup_conflict_count_for_val() {
    	conflict_count_for_val = new int[n][d];
    	
    	  	
    	for (int v1 = 0; v1 < n; v1++)
    		for (int v2 = 0; v2 < n; v2++) {
    			// need to count constraint checks since this is what we are doing here
    			constraint_checks++;
    			if (constraints[v1][v2] == null) 
    				continue;
	    			for (int d1 = 0; d1 < d; d1++)
	    				for (int d2 = 0; d2 < d; d2++) {
	    					if (! constraints[v1][v2][d1][d2]) {
	    						conflict_count_for_val[v1][d1]++;
	    						conflict_count_for_val[v2][d2]++;
	    					}
	    				}
    		}
    	
    }
    
    /**
     * @param i 
     * @param v 
     * @return  the number of conflicts variable i has for value v
     */
    public int get_conflicts_for_val(int i, int v) {
        if (conflict_count_for_val == null) 
     	   setup_conflict_count_for_val();
        
        constraint_checks++;
         return  conflict_count_for_val[i][v];
     	
     }

}
 // comment