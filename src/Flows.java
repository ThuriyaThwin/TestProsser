

import general.Definitions;
import general.Problem;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JFrame;

import prosser.FC_Cbj;
import jxl.*;
import jxl.write.*;
import jxl.write.Number;


/************************************************************************************

*************************************************************************************/

/**
 * This class contains the main running flows
 * make_samples() and run_tests() should be run.
 * make_sampes() � creates the problems in directory input
 * run_tests() � runs FC-CBJ and FC-CBJ-DAC and prints the results to files
 * run_queens() � runs an n queen problem (n is hard coded)
 */
public class Flows {

	private static String random_input_dir = "random_input";
	private static String out_dir = "output";
	private static String hard_input_dir = "hard_input";
	
	// p for DSA parallel executation propablility 
	private static double p_min = 0.1;
	private static double p_max = 0.91;
	private static double p_jump = 0.1;
	
	
   // p for problem type generation
   // "Play" with this to run with diffrent p1/p2 values
   	
   private static String input_dir_name = "input";
	
   private static double p2_min = 0.1;
   private static double p2_max = 0.91;
   private static double p2_jump = 0.05;
   
   private static double p1_min = 0.3;
   private static double p1_max = 0.7;
   private static double p1_jump = 0.4;
   
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		run_queens("DBAAgent", 5, 20000);
		run_queens("DSA_A_Agent", 4, 2000);
	    //run_gui_test("DSA_B_Agent", 10, 100, false);
		run_gui_test("DBAAgent", 5, 100, true);

		 //make_samples(60);
		 //run_tests(60, 500, 0.05);
		 
		// make_hard_samples(300);
		 //run_hard_tests(300, 1000);
	    
		//make_random_samples(1000);
		//run_random_tests(1500, 500);
		
	}
	

    /**
     * a driver for this demo
     * @param AgentAlgorith
     * @param queens_count
     * @param cycle_count
     */
    public static void run_gui_test(String AgentAlgorith, int queens_count, int cycle_count, boolean any_time) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		Problem problem = new Problem(queens_count);
        //Problem problem = new Problem(10, 10, 0.1,0.5);
        //problem.save2File("problem_save.prb");
        //Problem problem = new Problem("problem_save.prb");
		AgentSolver solver = new AgentSolver(problem, AgentAlgorith, cycle_count, 0.1, any_time);
		
        f.setLayout(new GridLayout(1,2));
        f.getContentPane().add(solver.get_panel());
        if (solver.use_any_time)
            f.getContentPane().add(solver.get_bfs_panel());
        f.pack();
        f.setVisible(true);
        
		solver.solve();
		
        f.setLayout(new GridLayout(1,2));
        f.getContentPane().add(solver.get_panel());
        if (solver.use_any_time)
            f.getContentPane().add(solver.get_bfs_panel());
        f.pack();
        f.setVisible(true);
        
    	System.out.println("ncccs = " + solver.ncccs);
        if (solver.check_results()) {
        	System.out.println("result ok");
        }
        else {
        	System.out.println("number of conflicts is : " + solver.count_conflicts());
        }
	    solver.printV(System.out);

    }


	/**
	 * makes no_of_random_samples rundom samples.
	 * Input from -  random_input_dir
	 * 
	 * @param no_of_random_samples
	 */
    public static void make_random_samples(int no_of_random_samples) {

			
		File input_dir = new File(random_input_dir);
		if ((! input_dir.isDirectory()) && (! input_dir.mkdir())) {
			System.out.println("Error createing dir " + random_input_dir);
			System.exit(1);
		}
		
		for (int i = 0; i < no_of_random_samples; i++) {
			String fileName = random_input_dir + "/case." + i;
			double p1 = Math.random();
			double p2 = Math.random();
			System.out.println("creating " + fileName);
			Problem problem = new Problem(15, 10, p1,p2);
			problem.save2File(fileName);
		}
	}


	

	/**
	 * used for printing results in both algorithms
	 */
    private static String measure_names[] = {
			"conflicts_at_end",
			"failures",
			"any_time_index",
			"total_messages", 
			"max_messages",
			"ncccs", 
			"steps"
		};
	
	/**
	 * Runs rundom test with from the differents algs
	 * @param no_of_random_samples
	 * @param cycle_count
	 * 
	 * @see DSA_A_Agent
	 * @see DSA_B_Agent
	 * @see DSA_C_Agent
	 * @see DSA_D_Agent
	 * @see DSA_E_Agent
	 * @see DBAAgent
	 */
    public static void run_random_tests(int no_of_random_samples,  int cycle_count) {
		String agent_class_names[] = {
			"DSA_A_Agent", "DSA_B_Agent", "DSA_C_Agent", "DSA_D_Agent", 
			"DSA_E_Agent", "DBAAgent"  	    
		};
		
		int num_of_p = 1 + (int) Math.ceil((p_max-p_min)/p_jump);
		int num_of_alg = 2*agent_class_names.length;
	    int conflicts_at_end[][] = new int[num_of_alg][num_of_p]; // need to avrage at end
	    int failures[][] = new int[num_of_alg][num_of_p]; // when was there a solution but it was not found
	    int any_time_index[][] =   new int[num_of_alg][num_of_p]; 
	    int total_messages[][] = new int[num_of_alg][num_of_p]; 
	    int max_messages[][] = new int[num_of_alg][num_of_p]; 
	    int ncccs[][] = new int[num_of_alg][num_of_p]; 
	    int steps[][] = new int[num_of_alg][num_of_p]; 
	    
	    int solvable = 0;

	    // problem is defined out of loop in order to enable 
	    // getting n and d from it at the end when writing the report
	    Problem problem = null;

		for (int i = 0; i < no_of_random_samples; i++) {
			String inputFileName = random_input_dir + "/case." + i;
			// read problem
			problem = new Problem(inputFileName);
				
			// run FC_Cbj;
			System.out.println("Running FC_Cbj for case case" + i);
			FC_Cbj solver_FC_Cbj = new FC_Cbj(problem);
				
			Definitions.StatOptions fc_cbj_status = solver_FC_Cbj.bcssp();
				
			// verify solution
			if (fc_cbj_status == Definitions.StatOptions.SOLUTION && ! solver_FC_Cbj.check_results()) {
					System.out.println("Bug !!! FC-CBJ result is wrong");
					System.exit(1);
			}
			
			if (fc_cbj_status == Definitions.StatOptions.SOLUTION)
				solvable++;
				
				

			int p_index = 0;
			for (double p = p_min; p <= p_max; p+= p_jump, p_index++) {
				for (int alg_no = 0; alg_no < num_of_alg ; alg_no++) {
						String alg_name = agent_class_names[alg_no%agent_class_names.length];
						boolean any_time = false;
						if (alg_no >= agent_class_names.length)
							any_time = true;
						
						System.out.println("Running " + alg_name + " for case #" + i + " p is " + p + " any time is " + any_time);
						AgentSolver solver = new AgentSolver(problem, alg_name, cycle_count, p, any_time);
						solver.solve();	
						
						int conflicts = solver.count_conflicts();
					    conflicts_at_end[alg_no][p_index] += conflicts;
					    if ((fc_cbj_status == Definitions.StatOptions.SOLUTION) &&  (conflicts != 0))
					    		failures[alg_no][p_index]++;
					    any_time_index[alg_no][p_index] += solver.any_time_max_index;
					    total_messages[alg_no][p_index] += solver.messages_sent; 
					    max_messages[alg_no][p_index] += solver.max_messages_sent; 
					    ncccs[alg_no][p_index] += solver.ncccs;
					    steps[alg_no][p_index] += solver.steps;
				} // end of alg loop
			} // end of p loop	
		} // end of i loop (go to next sample)
	
		/********************
         * print reports
         ********************/ 		
		
		
		int [][] measure_arrays[] = {
				conflicts_at_end,
				failures,
				any_time_index,
				total_messages,
				max_messages,
				ncccs,
				steps
		};
		

		
		// make output dir
		File output_dir = new File(out_dir);
		if ((! output_dir.isDirectory()) && (! output_dir.mkdir())) {
			System.out.println("Error createing dir " + output_dir);
			System.exit(1);
		}
		
		String reportFileName = out_dir + "/random_report" +  "_N#" + problem.getN() + "_D#" + problem.getD() + ".xls";

		WritableCellFormat cf = new WritableCellFormat(new NumberFormat("#,##0.00"));
		
		try{
			// create the xls file
			WritableWorkbook workbook = Workbook.createWorkbook(new File(reportFileName));
			WritableSheet sheet;

			
			int sheet_no=0;
			
			
			for (int m=0; m < measure_arrays.length; m++) {
				sheet = workbook.createSheet(measure_names[m], sheet_no++);
				Label label =  new Label(0, 0, "p");
				sheet.addCell(label);
				
				for (int alg_no = 0; alg_no < num_of_alg ; alg_no++) {
					String alg_name = agent_class_names[alg_no%agent_class_names.length];
					if (alg_no >= agent_class_names.length) {
						alg_name = alg_name + "_any_time";
					}
					
				    label = new Label(alg_no+1, 0, alg_name);
					sheet.addCell(label);
				}
			
			    int p_index = 0;
				for (double p = p_min; p <= p_max; p+= p_jump, p_index++)  {
					Number number = new Number(0,p_index+1,p);
					sheet.addCell(number);
				
					for (int alg_no = 0; alg_no < num_of_alg ; alg_no++) {
						String alg_name = agent_class_names[alg_no%agent_class_names.length];
						if (alg_no >= agent_class_names.length) {
							alg_name = alg_name + "_any_time";
						}
				
						if (measure_names[m].equals("failures")) {
							number = new Number(alg_no+1, p_index+1, failures[alg_no][p_index]);
						}
						else {
							number = new Number(alg_no+1, p_index+1, (double) measure_arrays[m][alg_no][p_index]/(double) no_of_random_samples);
							number.setCellFormat(cf);
						}
						sheet.addCell(number);
					}
					
					if (measure_names[m].equals("failures")) {
						number = new Number(num_of_alg+1, p_index+1, solvable);
						sheet.addCell(number);

					}
				}
			}	
				
			workbook.write();
			workbook.close();
		}
		catch (Exception e) {
			System.out.println("problem with file excel api" + reportFileName );
			e.printStackTrace();
			System.exit(1);
		}

}

	
    /**
     * creates the problems in directory input
     * make sure directory input exists before running
     * @param samples_count
     */
    public static void make_samples(int samples_count) {
			
		File input_dir = new File(input_dir_name);
		if ((! input_dir.isDirectory()) && (! input_dir.mkdir())) {
			System.out.println("Error createing dir " + input_dir);
			System.exit(1);
		}
		
		for (double p1 = p1_min; p1 <= p1_max; p1+= p1_jump) {
			for (double p2 = p2_min; p2 <= p2_max; p2+= p2_jump) {
				for (int i = 0; i < samples_count; i++) {
					String fileName = input_dir_name + "/case.p1=" + p1 + "_p2=" + p2 + "_i=" + i;
					System.out.println("creating " + fileName);
					Problem problem = new Problem(15, 10, p1,p2);
					problem.save2File(fileName);
				}
			}
		}
		
	}
	
    /**
     * 
     * @param samples_count
     * @param cycle_count
     * @param p
     */
    public static void run_tests(int samples_count, int cycle_count, double p) {

		int p1_index = 0;
		int p2_index = 0;
		int num_of_p2 = 1 + (int) Math.ceil((p2_max-p2_min)/p2_jump);
		int num_of_p1 = 1 + (int) Math.ceil((p1_max-p1_min)/p1_jump);

		String agent_class_names[] = {
				"DSA_A_Agent", "DSA_B_Agent", "DSA_C_Agent", "DSA_D_Agent", 
				"DSA_E_Agent", "DBAAgent"  	    
			};
			
		
		
		int num_of_alg = agent_class_names.length;
		int conflicts_at_end[][][] = new int[num_of_alg][num_of_p1][num_of_p2]; // need to avrage at end
		int failures[][][] = new int[num_of_alg][num_of_p1][num_of_p2]; // when was there a solution but it was not found
		int any_time_index[][][] =   new int[num_of_alg][num_of_p1][num_of_p2]; 
		int total_messages[][][] = new int[num_of_alg][num_of_p1][num_of_p2]; 
		int max_messages[][][] = new int[num_of_alg][num_of_p1][num_of_p2]; 
		int ncccs[][][] = new int[num_of_alg][num_of_p1][num_of_p2]; 
		int steps[][][] = new int[num_of_alg][num_of_p1][num_of_p2];
		int solvable[][] = new int [num_of_p1][num_of_p2]; 
		    
		int i;
		Problem problem = null;
		for (i = 0; i < samples_count; i++) {
			p1_index = 0;
			for (double p1 = p1_min; p1 <= p1_max; p1+= p1_jump, p1_index++) {
				p2_index=0;
				for (double p2 = p2_min; p2 <= p2_max; p2+= p2_jump, p2_index++) {
					String inputFileName = input_dir_name + "/case.p1=" + p1 + "_p2=" + p2 + "_i=" + i;
		
					// read problem
					problem = new Problem(inputFileName);
						
					// run FC_Cbj;
					System.out.println("Running FC_Cbj for case case" + i);
					FC_Cbj solver_FC_Cbj = new FC_Cbj(problem);
						
					Definitions.StatOptions fc_cbj_status = solver_FC_Cbj.bcssp();
						
					// verify solution
					if (fc_cbj_status == Definitions.StatOptions.SOLUTION && ! solver_FC_Cbj.check_results()) {
							System.out.println("Bug !!! FC-CBJ result is wrong");
							System.exit(1);
					}
					
					if (fc_cbj_status == Definitions.StatOptions.SOLUTION)
						solvable[p1_index][p2_index]++;
						
				
					for (int alg_no = 0; alg_no < num_of_alg ; alg_no++) {
						String alg_name = agent_class_names[alg_no];		
						
						System.out.println("Running " + alg_name + " for case #" + i + " p1 is " + p1 + " p2 is " + p2);
						AgentSolver solver = new AgentSolver(problem, alg_name, cycle_count, p, true);
						solver.solve();	
						
						int conflicts = solver.count_conflicts();
					    conflicts_at_end[alg_no][p1_index][p2_index] += conflicts;
					    if ((fc_cbj_status == Definitions.StatOptions.SOLUTION) &&  (conflicts != 0))
					    		failures[alg_no][p1_index][p2_index]++;
					    any_time_index[alg_no][p1_index][p2_index] += solver.any_time_max_index;
					    total_messages[alg_no][p1_index][p2_index] += solver.messages_sent; 
					    max_messages[alg_no][p1_index][p2_index] += solver.max_messages_sent; 
					    ncccs[alg_no][p1_index][p2_index] += solver.ncccs;
					    steps[alg_no][p1_index][p2_index] += solver.steps;						
					} // end of alg loop
				} // end of p2 loop	
			} // end of p1 loop
		} // end of i loop (go to next sample)
	
		/********************
         * print reports
         ********************/ 	
		

		WritableCellFormat cf = new WritableCellFormat(new NumberFormat("#,##0.00"));

		p1_index = 0;
		p2_index = 0;
		for (double p1 = p1_min; p1 <= p1_max; p1+= p1_jump, p1_index++)  {
			
			int [][][] measure_arrays[] = {
					conflicts_at_end,
					failures,
					any_time_index,
					total_messages,
					max_messages,
					ncccs,
					steps
			};
			
			WritableSheet sheet;
			
			// make output dir
			File output_dir = new File(out_dir);
			if ((! output_dir.isDirectory()) && (! output_dir.mkdir())) {
				System.out.println("Error createing dir " + output_dir);
				System.exit(1);
			}
			
			String reportFileName = out_dir + "/report" + "_p1="+ p1 +"_dsa_p="+ p + "_N#" + problem.getN() + "_D#" + problem.getD() + ".xls";

			try{
				// create the xls file
				WritableWorkbook workbook = Workbook.createWorkbook(new File(reportFileName));
				
				int sheet_no=0;
				
				for (int m=0; m < measure_arrays.length; m++) {
					sheet = workbook.createSheet(measure_names[m], sheet_no++);
					Label label =  new Label(0, 0, "p");
					sheet.addCell(label);
					
					for (int alg_no = 0; alg_no < num_of_alg ; alg_no++) {
						String alg_name = agent_class_names[alg_no%agent_class_names.length];
						if (alg_no >= agent_class_names.length) {
							alg_name = alg_name + "_any_time";
						}
						
					    label = new Label(alg_no+1, 0, alg_name);
						sheet.addCell(label);
					}
				
				    p2_index = 0;
					for (double p2 = p2_min; p2 <= p2_max; p2+= p2_jump, p2_index++)  {
						Number number = new Number(0,p2_index+1,p2);
						sheet.addCell(number);
					
						for (int alg_no = 0; alg_no < num_of_alg ; alg_no++) {
					
							if (measure_names[m].equals("failures")) {
								number = new Number(alg_no+1, p2_index+1, failures[alg_no][p1_index][p2_index]);
							}
							else {
								number = new Number(alg_no+1, p2_index+1, (double) measure_arrays[m][alg_no][p1_index][p2_index]/(double)  samples_count);
								number.setCellFormat(cf);
							}
							sheet.addCell(number);
						}
						
						if (measure_names[m].equals("failures")) {
							number = new Number(num_of_alg+1, p2_index+1, solvable[p1_index][p2_index]);
							sheet.addCell(number);
						}
					}
				}	
					
				workbook.write();
				workbook.close();
			}
			catch (Exception e) {
				System.out.println("problem with file excel api" + reportFileName );
				e.printStackTrace();
				//System.exit(1);
			}
		
	    }
			
	}
	
/***
 * 
 * @param no_of_samples
 */
public static void make_hard_samples(int no_of_samples) {

		
	File input_dir = new File(hard_input_dir);
	if ((! input_dir.isDirectory()) && (! input_dir.mkdir())) {
		System.out.println("Error createing dir " + hard_input_dir);
		System.exit(1);
	}
	
	for (int i = 0; i < no_of_samples; i++) {
		String fileName = hard_input_dir + "/case." + i;
		System.out.println("creating " + fileName);
		// use a predicted phase transition point to find hard problems
		Problem problem = new Problem(15, 10, 0.7,0.375);
		problem.save2File(fileName);
	}
}



/**
 * 
 * @param no_of_samples
 * @param cycle_count
 * 
 * @see DSA_A_Agent
 * @see DSA_B_Agent
 * @see DSA_C_Agent
 * @see DSA_D_Agent
 * @see DSA_E_Agent
 * @see DBAAgent
 */
public static void run_hard_tests(int no_of_samples,  int cycle_count) {
	String agent_class_names[] = {
		"DSA_A_Agent", "DSA_B_Agent", "DSA_C_Agent", "DSA_D_Agent", 
		"DSA_E_Agent", "DBAAgent"  	    
	};
	
	int num_of_p = 1 + (int) Math.ceil((p_max-p_min)/p_jump);
	int num_of_alg = 2*agent_class_names.length;
    int conflicts_at_end[][] = new int[num_of_alg][num_of_p]; // need to avrage at end
    int failures[][] = new int[num_of_alg][num_of_p]; // when was there a solution but it was not found
    int any_time_index[][] =   new int[num_of_alg][num_of_p]; 
    int total_messages[][] = new int[num_of_alg][num_of_p]; 
    int max_messages[][] = new int[num_of_alg][num_of_p]; 
    int ncccs[][] = new int[num_of_alg][num_of_p]; 
    int steps[][] = new int[num_of_alg][num_of_p]; 
    
    int solvable = 0;

    // problem is defined out of loop in order to enable 
    // getting n and d from it at the end when writing the report
    Problem problem = null;

	for (int i = 0; i < no_of_samples; i++) {
		String inputFileName = hard_input_dir + "/case." + i;
		// read problem
		problem = new Problem(inputFileName);
			
		// run FC_Cbj;
		System.out.println("Running FC_Cbj for case case" + i);
		FC_Cbj solver_FC_Cbj = new FC_Cbj(problem);
			
		Definitions.StatOptions fc_cbj_status = solver_FC_Cbj.bcssp();
			
		// verify solution
		if (fc_cbj_status == Definitions.StatOptions.SOLUTION && ! solver_FC_Cbj.check_results()) {
				System.out.println("Bug !!! FC-CBJ result is wrong");
				System.exit(1);
		}
		
		if (fc_cbj_status == Definitions.StatOptions.SOLUTION)
			solvable++;
			
			

		int p_index = 0;
		for (double p = p_min; p <= p_max; p+= p_jump, p_index++) {
			for (int alg_no = 0; alg_no < num_of_alg ; alg_no++) {
					String alg_name = agent_class_names[alg_no%agent_class_names.length];
					boolean any_time = false;
					if (alg_no >= agent_class_names.length)
						any_time = true;
					
					System.out.println("Running " + alg_name + " for case #" + i + " p is " + p + " any time is " + any_time);
					AgentSolver solver = new AgentSolver(problem, alg_name, cycle_count, p, any_time);
					solver.solve();	
					
					int conflicts = solver.count_conflicts();
				    conflicts_at_end[alg_no][p_index] += conflicts;
				    if ((fc_cbj_status == Definitions.StatOptions.SOLUTION) &&  (conflicts != 0))
				    		failures[alg_no][p_index]++;
				    any_time_index[alg_no][p_index] += solver.any_time_max_index;
				    total_messages[alg_no][p_index] += solver.messages_sent; 
				    max_messages[alg_no][p_index] += solver.max_messages_sent; 
				    ncccs[alg_no][p_index] += solver.ncccs;
				    steps[alg_no][p_index] += solver.steps;
			} // end of alg loop
		} // end of p loop	
	} // end of i loop (go to next sample)

	/********************
     * print reports
     ********************/ 		
	
	
	int [][] measure_arrays[] = {
			conflicts_at_end,
			failures,
			any_time_index,
			total_messages,
			max_messages,
			ncccs,
			steps
	};
	

	
	// make output dir
	File output_dir = new File(out_dir);
	if ((! output_dir.isDirectory()) && (! output_dir.mkdir())) {
		System.out.println("Error createing dir " + output_dir);
		System.exit(1);
	}
	
	String reportFileName = out_dir + "/hard_report" +  "_N#" + problem.getN() + "_D#" + problem.getD() + ".xls";

	WritableCellFormat cf = new WritableCellFormat(new NumberFormat("#,##0.00"));
	
	try{
		// create the xls file
		WritableWorkbook workbook = Workbook.createWorkbook(new File(reportFileName));
		WritableSheet sheet;

		
		int sheet_no=0;
		
		
		for (int m=0; m < measure_arrays.length; m++) {
			sheet = workbook.createSheet(measure_names[m], sheet_no++);
			Label label =  new Label(0, 0, "p");
			sheet.addCell(label);
			
			for (int alg_no = 0; alg_no < num_of_alg ; alg_no++) {
				String alg_name = agent_class_names[alg_no%agent_class_names.length];
				if (alg_no >= agent_class_names.length) {
					alg_name = alg_name + "_any_time";
				}
				
			    label = new Label(alg_no+1, 0, alg_name);
				sheet.addCell(label);
			}
		
		    int p_index = 0;
			for (double p = p_min; p <= p_max; p+= p_jump, p_index++)  {
				Number number = new Number(0,p_index+1,p);
				sheet.addCell(number);
			
				for (int alg_no = 0; alg_no < num_of_alg ; alg_no++) {
					String alg_name = agent_class_names[alg_no%agent_class_names.length];
					if (alg_no >= agent_class_names.length) {
						alg_name = alg_name + "_any_time";
					}
			
					if (measure_names[m].equals("failures")) {
						number = new Number(alg_no+1, p_index+1, failures[alg_no][p_index]);
					}
					else {
						number = new Number(alg_no+1, p_index+1, (double) measure_arrays[m][alg_no][p_index]/(double) no_of_samples);
						number.setCellFormat(cf);
					}
					sheet.addCell(number);
				}
				
				if (measure_names[m].equals("failures")) {
					number = new Number(num_of_alg+1, p_index+1, solvable);
					sheet.addCell(number);

				}
			}
		}	
			
		workbook.write();
		workbook.close();
	}
	catch (Exception e) {
		System.out.println("problem with file excel api" + reportFileName );
		e.printStackTrace();
		//System.exit(1);
	}

}
	public static void run_queens(String AgentAlgorith,int queens_count, int cycle_count) {
		//Problem problem = new Problem(15,10, 0.5, 0.5);
		// problem.save2File("input/problem.data1");
		
		//Problem problem = new Problem("input/problem.data1");
		long start = System.currentTimeMillis();

		Problem problem = new Problem(queens_count);
		AgentSolver solver = new AgentSolver(problem, AgentAlgorith, cycle_count, 0.2, true);
		solver.solve();
		
	    solver.printV(System.out);

	    
		long end = System.currentTimeMillis();
		System.out.println("Execution time was "+(end-start)+" ms.");

		//problem.printProblem(System.out);
	}
	
}
