
/**
 * @author Miriam k.
 * @author Elad l.
 */



package general;
/**
 * Val set class is a set that can contain numbers 
 * between 0 and n 
 * 
 * Limitations:
 *    When performing union or diff with IntStack bounds are not
 *    Check.
 * Created Feb 2009
 * @author Miriam Kreisler
 */

// Needed since we allow to add the contents 
// of stack to Set
import com.sosnoski.util.stack.IntStack;


public class ValSet {
	private boolean setArray[];  // true value means belong to set false not in set
	public int n; // size of set
	public int count; // the amount of elements in set (keep in order to avoid count each time
	
	/**
	 *  init an empty set of size n
	 * @param n
	 */
	public ValSet(int n) {
		setArray = new boolean[n];
		this.n = n;		
        clear();
	}
	
	/**
	 *  remove all values
	 */
	public void clear() {
		for (int i = 0; i < n ; i++)
		      setArray[i]=false;		
		count = 0;
	}
	
	/**
	 * fill the set (contains all values
	 */
	public void fill() {
		for (int i = 0; i < n ; i++)
		      setArray[i]=true;
		
		count = n;
	}
	
	
	/**
	 * copy another ValSet into this one
	 */
	public void copy(ValSet other) {
			
		for (int j=0; j < n; j++) {
			setArray[j] = other.setArray[j];
	    }
		
		n = other.n;
		count = other.count;
	}
	
	/**
	 *  does current set contain value?
	 * @param val
	 * @return setArray[val]
	 */
	public boolean isMember(int val) {
		return setArray[val];
	}
	
	/**
	 * are there any values in set?
	 * @return count == 0
	 */
	public boolean isEmpty() {
		return (count == 0);
	}
	
	/**
	 * remove a value from set
	 * @param val
	 */
	public void remove(int val) {
		if (setArray[val])
			count--;
		setArray[val] = false;
	}
	
	// add a value from set
	public void add(int val) {
		if (! setArray[val])
			count++;
		setArray[val] = true;
	}
	
	/**
	 * will add to current set all values from other
	 * count is incremented only if val is not in set
	 * @param other
	 */
	public void union(ValSet other) {
	
		if (other.n != n) {
		   System.out.println("can only combine ValSets of the same size");
		   System.exit(1);
		}
			
		for (int j=0; j < n; j++) {
		  if (other.isMember(j)) {
			if (! setArray[j])
				count++;
			setArray[j] = true;
		  }
		}
		
	}
	
	/**
	 * will add to current set all values that exist in IntStack
	 * count is incremented only if val is not in set
	 */
	
	public void union(IntStack list) {
		// TODO: bounds are not checked here list may contain values larger then n
		int size = list.size();
		for (int j=0; j < size; j++) {
			int k = list.peek(j);
			if (! setArray[k])
				count++;
			setArray[k] = true;
		}
	}
	
	/**
	 *  will remove from current set all values that exist in IntStack
	 *  count is decremented only if val was already in set
	 * @param list
	 */
	public void setDiff(IntStack list) {
		//TODO: bounds are not checked here list may contain values larger then n
		int size = list.size();

		for (int j = 0; j < size; j++) {
			int k = list.peek(j);
			if (setArray[k])
				count--;
			setArray[k] = false;
		}
	}
	
	/**
	 * will remove from current set all values that exist in the other ValSet
	 * count is decremented only if val was already in set
	 * @param other
	 */
	public void setDiff(ValSet other) {
		// TODO: bounds are not checked here list may contain values larger then n
		for (int j = 0; j < n; j++) {
			if (setArray[j] && other.setArray[j] )
				count--;
			setArray[j] = false;
		}
	}
	

	/**
	 *  return the maximum value of set
	 *  if the value doesn't exist in set then return -1
	 * @return max
	 */
    public int get_max() {
        int max = -1;  // when ther is not val return -1
        
        for(int i = n-1; i >= 0 ; i--)
        	if (setArray[i])
        		return i;
        
        return max;
   }     
    
    /**
     * return the amount of values in set
     * @return count
     */
    public int get_count() {
    	return count;
    }
	
}
