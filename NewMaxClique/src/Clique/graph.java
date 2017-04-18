package Clique;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import MaxClique.*;

public class graph {

	private int[][] graph; // the adjacency matrix
	private node3[] graph3; // the adjacency matrix
	private int[][] old_graph; //when changing the graph around, can keep the old one to ensure that the returned set is indeed a clique
	private String graphs_directory = "..\\graph_binaries\\";
	private String include_graphs = "";
	private String exclude_graphs = "\"MANN_a45.clq\",\"MANN_a81.clq\",\"keller5.clq\",\"keller6.clq\""; 
	private int nodes; // total number of nodes
	private int BK_iteration_deep = 0; // iterations deep into BronKerbosch
	private long BK_calls = 0; // calls to BronKerbosch
	private int B_iteration_deep = -1; //iterations deep into Bochert
	private long B_calls = 0; // calls to Bochert
	private long B_calls_TOP = 0; // TOP calls to Bochert
	private long B_calls_background = 0; // calls to Bochert
	private boolean verboseBK = false; // Verbosity of output... verbosity should be a word... it sounds cool
	private int edges = 0; // total edges in graph
	private int initial_connected = 1; // used when functions are looking creating the initial matrix, for finding max independant set instead of max clique, this value is inverted
	private int initial_not_connected = 0; // used when functions are looking creating the initial matrix, for finding max independant set instead of max clique, this value is inverted
	private int internal_connected = 1; // used when functions are looking at internal connected ness
	private int internal_not_connected = 0; // used when functions are looking at internal connected ness
	private int[] node_edge_count; // number of edges each node has connected to it
	private int[] nodes_ordered_increasing; // array of nodes with decreasing edge count - first node has highest num edges
	private int[] index_ordered_nodes; //array of nodes, where int[0] represents the index of the first node into nodes_ordered_decreasing, and int[1] represents the index of the second, etc 
	boolean start_showing_crap = false;
	private int display_level = -1;
	private node3 empty_node;
	private int previous_depth = 0;
	private int count_down = 20;

	//private gpu graphics_card;
	private node3[] results_array;
	private int results_array_length;
	private node3 deleted_nodes;
	private long before = 0;
	private long after = 0;
	private long[] timings = new long[10];
	private String[] timings2 = new String[10];
	private boolean sort_smallest_first = true;
	private boolean sort = true;
	private boolean degressive_display = false;
	private int num_threads = 1;
	private int mid_num_threads = 1;
	private long hotswap_trigger = -1;
	private boolean level_0_display = false;
	private boolean priority_threading = false;
	private int min_new_bthread_size = 10;
	private int min_new_midthread_size = 10;
	private int lowest_backtrack = 0; //by setting to "nodes", threads will do a depth first search, this helps in tracking progress, but it's not as efficient
	private Date dNow = new Date( );
	private SimpleDateFormat ft = new SimpleDateFormat ("HH:mm:ss");
	semaphore semasema = new semaphore("semasema");
	semaphore mid_semasema = new semaphore("mid_semasema");
	semaphore semaMax = new semaphore("semaMax");
	semaphore semasematext = new semaphore("semasematext");
	int max_thread_scaling_factor = 3; //used to decide how much larger the pool of threads can grow, keeping the max number of active threads the same
	ThreadMXBean thMxB = ManagementFactory.getThreadMXBean();
	long[] time_analysis = new long[10];
	int exit_loop = 15000;
	int middle_loop_run = 0;
	private boolean disp_found_max = true;
	private int size_announced_max = 0;

	Random generator = new Random();
	int randseed = -1;

	String IV = "AAAAAAAAAAAAAAAA";
	byte[] plaintext = new byte[32];
	byte[] encryptionKey = new byte[16];
	int current_byte = 8;
	byte[] cipher = new byte[32];





	private void hotswap(){

		//		Scanner scanner;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String name = null;
		try {


			System.out.println();
			System.out.print("trigger("+hotswap_trigger+"): ");
			name = br.readLine();
			if(name.isEmpty()){
				System.out.println("empty line");
			}
			else{
				hotswap_trigger = Integer.parseInt(name);
				System.out.println("trigger("+hotswap_trigger+"): ");
			}

			System.out.print("display_level("+display_level+"): ");
			name = br.readLine();
			if(name.isEmpty()){

			}
			else{
				display_level = Integer.parseInt(name);
				System.out.println("display_level("+display_level+"): ");
			}

			System.out.print("degressive_display("+degressive_display+"): ");
			name = br.readLine();
			name.trim();
			if(name.isEmpty()){

			}
			else if(name.startsWith("true")){
				degressive_display = true;
				System.out.println("degressive_display("+degressive_display+"): ");
			}
			else{
				degressive_display = false;
				System.out.println("degressive_display("+degressive_display+"): ");
			}

			System.out.print("level_0_display ("+level_0_display +"): ");
			name = br.readLine();
			name.trim();
			if(name.isEmpty()){
				System.out.println("was empty");
			}
			else if(name.startsWith("true")){
				level_0_display  = true;
				System.out.println("level_0_display ("+level_0_display +"): ");
			}
			else{
				level_0_display  = false;
				System.out.println("level_0_display ("+level_0_display +"): ");
			}

		} catch (IOException e) {
			//		         System.out.println("Error!");
			//		         System.exit(1);
		}

		/*/		       System.out.print("Enter your name and press Enter: ");
//		       BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		       String name = null;
		       try {
		    	   System.out.print("degressive_display("+degressive_display+"): ");
//		    	   degressive_display = (boolean)(int)br.read .readLine();
		    	   System.out.print("degressive_display("+degressive_display+"): ");

		    	degressive_display = true;
				num_threads = 0;

		         name = br.readLine();
		       } catch (IOException e) {
//		         System.out.println("Error!");
//		         System.exit(1);
		       }
//		       System.out.println("Your name is " + name);
		 */	
	}


	private void pause() 
	{
		System.out.println("Press the anykey to continue");
		try
		{
			System.in.read();
		}
		catch(IOException exe)
		{
		}
	}




	/*	private boolean is_there_another(final node2 check_set, final node2 nodes_to_consider, final node2 dont_consider_connected, node2 result, boolean display){

		//		Bochert_neighbor(result,node,nodes_a, nodes_b, internal_connected);

		if(check_set.get_length() == 0)
			return false;

		//		node2 check_set = memory_element;
		if(display)
			System.out.println("about to start and look at "+check_set.get_last()+" which is index "+check_set.get_length()+" out of: "+result.get_length());

		Bochert_neighbor(result,check_set.get_last(),nodes_to_consider, dont_consider_connected, internal_connected);

		if(display)
			System.out.println("looking at "+check_set.get_last()+" which is index "+check_set.get_length()+" out of: "+result.get_length());


		if(result.get_length() <= 1)
			return false;

		//		
		for(int i = check_set.get_length()-2; i>=0; i--){
			Bochert_neighbor(result,check_set.get_full_array()[i],empty_node, result, internal_connected);

			if(display)
				System.out.println("looking at "+check_set.get_full_array()[i]+" which is index "+i+" out of: "+result.get_length());

			if(result.get_length() <= 1){
				return false;
			}

		}

		return true;
	}
	 */









	private void sort_nodes(){

		nodes_ordered_increasing = merge_sort(this.all_neighbors(-1),node_edge_count);

		index_ordered_nodes = new int[nodes];

		for(int i = 0; i<nodes_ordered_increasing.length; i++){
			//			System.out.println(i+" node: "+nodes_ordered_increasing[i]+" has "+node_edge_count[nodes_ordered_increasing[i]-1]+" edges");
			index_ordered_nodes[nodes_ordered_increasing[i]-1] = i;			
		}

		//		pause();

	}

	private void reorganize_nodes(){
		//function reorganizes the graph[][] to make the node with the fewest edges node 1 and the node with the most nodes the last node

		int[][] newgraph = new int[nodes][nodes];

		for(int i = 0; i<nodes; i++)
			for(int j = 0; j<nodes; j++){
				newgraph[i][j] = graph[nodes_ordered_increasing[i]-1][nodes_ordered_increasing[j]-1];								
			}				

		old_graph = graph;
		graph = newgraph;

	}

	private int[] unreorganize_nodes(node3 result){
		//find out what the real nodes are

		//		System.out.println("result was: "+result.print_list());

		int[] real_result = new int[result.get_length()];
		int[] fake_result = result.to_int();

		for(int i = 0; i<result.get_length(); i++)
			real_result[i] = nodes_ordered_increasing[fake_result[i]-1];

		graph = old_graph;

		return real_result;
	}

	private int[] merge_sort(int[] list, int[] weight)
	{
		if(weight == null){
			weight = this.all_neighbors(-1);			
		}

		if (list.length <= 1)
			return list;
		int[] left, right, result;

		int middle = list.length / 2;
		left = new int[middle];
		right = new int[list.length - middle];
		System.arraycopy(list, 0, left, 0, middle);
		System.arraycopy(list, middle, right, 0, list.length - middle);

		left = merge_sort(left, weight);
		right = merge_sort(right, weight);
		result = merge(left, right, weight);

		return result;



	}



	private int[] merge(int[] left,int [] right, int[] weight)
	{

		int lefti = 0, righti = 0, resulti = 0;
		int leftl = left.length, rightl = right.length;
		int resultl = leftl + rightl;
		int[] result = new int[resultl];


		while ((lefti < leftl) && (righti < rightl)){
			if (sort_smallest_first?(weight[left[lefti]-1] <= weight[right[righti]-1]):(weight[left[lefti]-1] >= weight[right[righti]-1])){
				result[resulti] = left[lefti];
				resulti++;
				lefti++;
			}
			else{
				result[resulti] = right[righti];
				resulti++;
				righti++;        }
		}

		while (lefti < leftl){		
			result[resulti] = left[lefti];
			resulti++;
			lefti++;
		}
		while (righti < rightl){		
			result[resulti] = right[righti];
			resulti++;
			righti++;
		}

		return result;
	}

	private void Bochert_neighbor(node3 result, int n, node3 array){

		result.use_me_and(graph3[n-1], array);


		return;

	}


	public int[] pre_Newer_Bochert(boolean display){

		/*		graph3 = new node3[nodes];
		for(int i = 0; i<nodes; i++){
			graph3[i] = new node3(graph[i],nodes,true);
		}
		empty_node = new node3(nodes);

		if(1==1)
		return Newer_Bochert(new node3(this.all_neighbors(-1),nodes),0,nodes,display).to_int();
		 */


		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		if(sort){
			sort_nodes();
			reorganize_nodes();
		}
		else{
			nodes_ordered_increasing = new int[nodes];
			for(int i = 0; i<nodes_ordered_increasing.length; i++){
				nodes_ordered_increasing[i]= i+1;
			}

		}

		sort_smallest_first = true;
		empty_node = new node3(nodes);
		int[] newgraph = new int[nodes*nodes];

		for (int i = 0; i< nodes; i++)
			for (int j = 0; j<nodes; j++)
				newgraph[i*nodes+j] = graph[i][j];

		//graphics_card = new gpu(newgraph, nodes);

		results_array = new node3[nodes];
		for(int i = 0; i<nodes; i++){
			results_array[i] = new node3(nodes);
		}

		results_array_length = 0;
		deleted_nodes = new node3(nodes);

		graph3 = new node3[nodes];
		for(int i = 0; i<nodes; i++){
			graph3[i] = new node3(graph[i],nodes,true);
			//							System.out.println("node: "+(i+1)+" connected to: "+graph3[i].print_literal());//.print_list());
		}

		level_0_display = display;

		node3 send = reduction(new node3(this.all_neighbors(-1),nodes),null,null);

		if(num_threads <= 0){
			return merge_sort(unreorganize_nodes(Newer_Bochert(send,0,nodes,display,0)), all_neighbors(-1));
			//return Newer_Bochert(send,0,nodes,display,0).to_int();
		}
		else{
			node3[] reach_back1 = new node3[max_thread_scaling_factor*num_threads];
			node3[] previous_nodes = new node3[max_thread_scaling_factor*num_threads];

			for(int i = 0; i<max_thread_scaling_factor*num_threads; i++){
				reach_back1[i] = new node3(nodes);
				previous_nodes[i] = new node3(nodes);
			}

			//node3 find1 = new node3(this.all_neighbors(-1),nodes);
			long[] reach_back_B_calls1 = new long[1];
			long[] reach_back_B_calls_TOP1 = new long[1];
			int[] status = new int[max_thread_scaling_factor*num_threads];
			status[0] = 1;
			semaphore stillrunning = new semaphore();


			int[] thread_count = new int[1];
			thread_count[0] = 1;
			int[] exit_loop1 = new int[1];
			exit_loop1[0] = exit_loop;

			int[] mid_status = new int[max_thread_scaling_factor*mid_num_threads]; 
			int[] mid_thread_count = new int[1];
			mid_thread_count[0] = 1;			

			node3[] mid_reach_back = new node3[max_thread_scaling_factor*mid_num_threads];
			node3[] mid_previous_nodes = new node3[max_thread_scaling_factor*mid_num_threads];
			for(int i = 0; i<max_thread_scaling_factor*mid_num_threads; i++){
				mid_reach_back[i] = new node3(nodes);
				mid_previous_nodes[i] = new node3(nodes);
			}

			int[] reach_back_middle_loop_run = new int[1];
			reach_back_middle_loop_run[0] = 0;


			try{stillrunning.take();} catch(InterruptedException e){}


			//			bthread(node3[] reach_back1, int thread_pool1, node3[] graph31, int[][] graph1, node3 find1, boolean display1, int nodes1, long[] reach_back_B_calls1, int display_level1, node3 empty_node1, boolean degressive_display1, int whoami1, int[] status1, node3[] previous_nodes1, int thread_count1){
			Runnable task = new bthread(semasematext, reach_back1, num_threads, graph3, graph, send, display, nodes, reach_back_B_calls1, display_level, empty_node, degressive_display,0,status,previous_nodes,0,semasema,stillrunning,-1,0,semaMax,0,priority_threading,new int[1], thread_count, mid_semasema, mid_thread_count, mid_num_threads, mid_status, mid_reach_back,mid_previous_nodes,min_new_bthread_size,min_new_midthread_size,lowest_backtrack,reach_back_B_calls_TOP1,max_thread_scaling_factor*num_threads,max_thread_scaling_factor*mid_num_threads,time_analysis,exit_loop1,reach_back_middle_loop_run,disp_found_max);	


			//bthread(semaphore semasematext1, node3[] reach_back1, int thread_pool1, node3[] graph31, int[][] graph1, node3 find1, boolean display1, int nodes1, long[] reach_back_B_calls1, int display_level1, node3 empty_node1, boolean degressive_display1, int whoami1, int[] status1, node3[] previous_nodes1, int previously_known_max1, semaphore semasema1, semaphore stillrunning1, int B_iteration_deep1,int who_ran_me1, semaphore semaMax1, int pre_depth_memory1, boolean priority_threading1, int[] best_star1, int[] thread_count1, semaphore mid_semasema1, int[] mid_thread_count1, int mid_thread_pool1, int[] mid_status1, node3[] mid_reach_back1, 	node3[] mid_previous_nodes1, int min_new_bthread_size1, int min_new_midthread_size1, int lowest_backtrack1, long[] reach_back_B_calls_TOP1, int max_thread_pool1, int mid_max_thread_pool1, long[] reach_back_time_analysis1, int[] exit_loop1,int[] reach_back_middle_loop_run1){
			//bthread(semaphore semasematext1, node3[] reach_back1, int thread_pool1, node3[] graph31, int[][] graph1, int nodes1, long[] reach_back_B_calls1, int display_level1, node3 empty_node1, boolean degressive_display1, int whoami1, int[] status1, node3[] previous_nodes1, semaphore semasema1, semaphore stillrunning1, int B_iteration_deep1, semaphore semaMax1, boolean priority_threading1, int[] best_star1, int[] thread_count1, semaphore mid_semasema1, int[] mid_thread_count1, int mid_thread_pool1, int[] mid_status1, node3[] mid_reach_back1, node3[] mid_previous_nodes1, int min_new_bthread_size1, int min_new_midthread_size1, int lowest_backtrack1, long[] reach_back_B_calls_TOP1, int max_thread_pool1, int mid_max_thread_pool1, long[] reach_back_time_analysis1, int[] exit_loop1,int[] reach_back_middle_loop_run1){


			Thread worker = new Thread(task);
			worker.setName("TOP_Thread");
			if(priority_threading){
				worker.setPriority(9);
			}
			worker.start();

			//			while (worker.isAlive()) {
			try {
				stillrunning.take();
			} catch(InterruptedException e) {
			} 
			//			}

			B_calls = reach_back_B_calls1[0];
			B_calls_TOP = reach_back_B_calls_TOP1[0];
			middle_loop_run = reach_back_middle_loop_run[0];

			return merge_sort(unreorganize_nodes(reach_back1[0]), all_neighbors(-1));
			//return(reach_back1[0].to_int());
		}

		/*		else{
			System.out.println("non sorting algorithm isn't still being maintained");
			System.out.println("non sorting algorithm isn't still being maintained");
			System.out.println("non sorting algorithm isn't still being maintained");

			empty_node = new node3(nodes);

			int[] newgraph = new int[nodes*nodes];

			for (int i = 0; i< nodes; i++)
				for (int j = 0; j<nodes; j++)
					newgraph[i*nodes+j] = graph[i][j];

			results_array = new node3[nodes];
			for(int i = 0; i<nodes; i++){
				results_array[i] = new node3(nodes);
			}

			results_array_length = 0;
			deleted_nodes = new node3(nodes);

			graph3 = new node3[nodes];
			for(int i = 0; i<nodes; i++){
				graph3[i] = new node3(graph[i],nodes,true);
			}

			//this.disp_graph();
			//		this.New_Bochert(nodesA, current_max, current_max_starA, nodesB, current_max_starB, display)
			return Newer_Bochert(new node3(this.all_neighbors(-1),nodes),0,nodes,display,empty_node,0).to_int();
		}
		 */
	}


	private node3 reduction(node3 check_nodes_orig, node3 not_included_extra_nodes, node3 just_try_to_delete_me_MEMORY_NOT_PRESERVED){
		//try to delete as many check_nodes as possible by looking at other check_nodes and not_included_extra_nodes

		//System.out.println("check_nodes started as: "+check_nodes_orig.print_list());

		node3 check_nodes = check_nodes_orig.copy_by_erasing();
		node2 set;
		boolean limited_scope = false;
		if(just_try_to_delete_me_MEMORY_NOT_PRESERVED != null && just_try_to_delete_me_MEMORY_NOT_PRESERVED.get_length() != 0){
			set = just_try_to_delete_me_MEMORY_NOT_PRESERVED.to_new_node2();
			limited_scope = true;
		}
		else{
			set = check_nodes.to_new_node2();			
		}

		node3 optional_set_of_nodes_connected_to_n = new node3(nodes);

		node3 all_nodes = check_nodes.copy_by_erasing();

		if(not_included_extra_nodes != null)
			all_nodes.use_me_or(all_nodes, not_included_extra_nodes);

		int deleted = -1;
		boolean multi_node = false;

		for(int i = 0; i<set.get_length(); i++){
			if((deleted != -1)&&(i>=deleted)){
				check_nodes.delete(set.get_full_array()[deleted]);
				if(limited_scope)just_try_to_delete_me_MEMORY_NOT_PRESERVED.delete(set.get_full_array()[deleted]);
				set.delete(set.get_full_array()[deleted]);				
				deleted=-1;
				multi_node = false;
				i--;//in case this was the last node, reset to re-evalute the number of nodes left to check (aka, stop if zero)
			}
			else{
				if(deleted == -1){
					Bochert_neighbor(optional_set_of_nodes_connected_to_n,set.get_full_array()[i],check_nodes_orig);
					if(deletable(set.get_full_array()[i], all_nodes,null,false, optional_set_of_nodes_connected_to_n)){
						//						System.out.println("check_nodes was: "+check_nodes_orig.print_list());
						//						System.out.println(set.get_full_array()[i]+" a was tagged for deletion as it was connected to: "+optional_set_of_nodes_connected_to_n.print_list());
						deleted = i;
						i = -1;
					}
				}
				else{
					if(multi_node){
						Bochert_neighbor(optional_set_of_nodes_connected_to_n,set.get_full_array()[i],check_nodes_orig);
						if(deletable(set.get_full_array()[i], all_nodes,null,false, optional_set_of_nodes_connected_to_n)){
							//							System.out.println(set.get_full_array()[i]+" b was tagged for deletion as it was connected to: "+optional_set_of_nodes_connected_to_n.print_list());
							check_nodes.delete(set.get_full_array()[i]);
							if(limited_scope)just_try_to_delete_me_MEMORY_NOT_PRESERVED.delete(set.get_full_array()[deleted]);
							set.delete(set.get_full_array()[i]);
							deleted--;
							i = -1;					
						}
					}
					else if(graph[i][deleted] == 1){//not connected so worth considering again
						Bochert_neighbor(optional_set_of_nodes_connected_to_n,set.get_full_array()[i],check_nodes_orig);
						if(deletable(set.get_full_array()[i], all_nodes,null,false, optional_set_of_nodes_connected_to_n)){
							//							System.out.println(set.get_full_array()[i]+" c was tagged for deletion as it was connected to: "+optional_set_of_nodes_connected_to_n.print_list());
							check_nodes.delete(set.get_full_array()[i]);
							if(limited_scope)just_try_to_delete_me_MEMORY_NOT_PRESERVED.delete(set.get_full_array()[deleted]);
							set.delete(set.get_full_array()[i]);
							multi_node = true;
							deleted--;
							i = -1;					
						}
					}
					else{
					}
				}

			}
		}

		if(limited_scope){
			return just_try_to_delete_me_MEMORY_NOT_PRESERVED;
		}
		else{
			return check_nodes;
		}
	}

	private boolean deletable(int n, node3 all_nodes, node3 lost_nodes,boolean save, node3 optional_set_of_nodes_connected_to_n){

		node3 connected = new node3(nodes);
		node3 test = new node3(nodes);

		if(optional_set_of_nodes_connected_to_n == null || optional_set_of_nodes_connected_to_n.get_length()==0)
			connected.use_me_and(graph3[n-1], all_nodes);
		else
			connected.copy_array(optional_set_of_nodes_connected_to_n);

		test.use_me_and_not_first(connected, all_nodes);//no need to check the nodes that it's connected to, they can't be connected to the same because they cannot be connected to themself		
		test.delete(n);//just in case, current implementation doesn't need this tho, later ones might
		int[] int_nodes = test.to_int();

		for(int i = 0; i<int_nodes.length; i++){
			test.use_me_and(graph3[int_nodes[i]-1], connected);
			//System.out.println("====== connected: "+connected.print_list()+" test: "+)
			if(test.get_length() == connected.get_length()){
				//System.out.println("node: "+n+" connected to: "+connected.print_list()+" and node: "+int_nodes[i]+" is connected to: "+test.print_list());
				all_nodes.delete(n);
				if(save){
					all_nodes.side = (char)int_nodes[i];
				}
				return true;
			}
		}
		if(lost_nodes != null){
			int_nodes = lost_nodes.to_int();

			for(int i = 0; i<int_nodes.length; i++){
				test.use_me_and(graph3[int_nodes[i]-1], connected);
				//System.out.println("====== connected: "+connected.print_list()+" test: "+)
				if(test.get_length() == connected.get_length()){
					all_nodes.delete(n);
					if(save){
						all_nodes.side = (char)int_nodes[i];
					}
					return true;
				}
			}			
		}


		return false;

	}


	private node3 Newer_Bochert(node3 all_nodes, int current_max, int sought_max, boolean show, int where_from){

		long start = System.currentTimeMillis();

		B_iteration_deep++;
		B_calls++;

		if(B_iteration_deep == 0)
			show = level_0_display;

		if(B_calls == hotswap_trigger){
			hotswap();
		}


		boolean display = (((where_from < 4)&&(level_0_display == true)&&(B_iteration_deep < (display_level+1)))?true:false);

		if((all_nodes.get_length() == 0)||(all_nodes.get_length() == 1)){
			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("returning because all_nodes is zero or length 1");
			}
			B_iteration_deep--;
			return all_nodes;
		}

		node3 result = new node3(nodes);		

		if(sought_max <= 1){
			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("returning because sought_max <= 1");
			}
			result.add(all_nodes.get_index(0));
			B_iteration_deep--;
			return result;
		}

		if(all_nodes.get_length() <= current_max){//if it's equal to, you'll only get the same as the current max
			//this.insert_spaces_for_iteration("B");
			//System.out.println("returning because all_nodes < current max, where from: "+where_from);
			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("Returning because all_nodes.gl <= current_max");
			}
			B_iteration_deep--;
			return new node3(nodes);
		}

		node3 original_all_nodes = all_nodes.copy_by_erasing();
		int toptop = all_nodes.get_index(0);
		node3 TOP_dont_consider_connected = new node3(nodes);// = result.copy_by_erasing();
		node3 all_nodes_in_set_deleted_used = new node3(nodes); //TOP_dont_consider_connected;
		node3 all_nodes_in_set_whole = new node3(nodes);
		node3 TOP_checked_set = new node3(nodes);
		node3 checked_set = TOP_checked_set;
		node3 TOP_nodes_to_consider = new node3(nodes);
		node3 memory_element = new node3(nodes);
		node3 temp_element = new node3(nodes);
		node3 max_star = new node3(nodes);
		max_star.meta_data = current_max;
		node3 nodes_to_consider = TOP_nodes_to_consider;//new node3(nodes);
		node3 temp_element2 = new node3(nodes);
		node3 Pointer_ONLY;
		node3 Pointer_ONLY2;
		int temp = 0;
		int check_set = 1;
		int comp_set = 0;
		int deepness = 0;
		boolean I_was_deleted = false;
		boolean run = true;
		boolean all_others_empty = true;
		node3 best_next_ntc = new node3(nodes);
		node3 best_next_me = new node3(nodes);
		node3 unused_best_next_ntc = new node3(nodes);
		node3 unused_best_next_me = new node3(nodes);
		node3 unique_alpha = new node3(nodes);
		node3 unique_check = new node3(nodes);
		node3 nodes_in_common = new node3(nodes);
		node3 best_unique_alpha = new node3(nodes);
		node3 best_nodes_in_common = new node3(nodes);



		node3[] DCC = new node3[0];
		node3 alpha3 = new node3(nodes);

		//node3 comp_nodes = new node3(nodes);



		this.Bochert_neighbor(result, toptop, all_nodes);		

		time_analysis[0] = time_analysis[0] + (System.currentTimeMillis() - start);
		start = System.currentTimeMillis();

		///////////////////////////////////////////////////////////////////////////////////////////////////
		//  NO WHILE
		///////////////////////////////////////////////////////////////////////////////////////////////////
		//it's connected to all the nodes
		if((result.get_length()+1)==all_nodes.get_length()){
			if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
				//			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println(" NO  WHILE,  B_calls: "+B_calls+" toptop (which is: "+toptop+") connected to all other nodes (which are: "+result.print_list()+"), calling Bochert("+result.print_list()+" ,cm: "+(current_max==0?0:current_max-1)+" ,sm: "+(sought_max==0?0:sought_max-1)+" , abc: null; (ntc node was connected to all nodes)");
			}



			result = Newer_Bochert(result,(current_max==0?0:current_max-1),(sought_max==0?0:sought_max-1),display,1);
			result.add(toptop);
			B_iteration_deep--;
			return result;
		}




		///////////////////////////////////////////////////////////////////////////////////////////////////
		//  SET UP
		///////////////////////////////////////////////////////////////////////////////////////////////////
		TOP_dont_consider_connected.copy_array(result);
		TOP_nodes_to_consider.use_me_and_not_first(TOP_dont_consider_connected, all_nodes);
		TOP_nodes_to_consider.pop_first();// get rid of toptop
		nodes_to_consider = TOP_nodes_to_consider;//new node3(nodes);


		//find DCCs
		DCC = new node3[TOP_nodes_to_consider.get_length()+1];
		DCC[0] = TOP_dont_consider_connected.copy_by_erasing();
		DCC[0].meta_data = toptop;
		all_nodes.delete(toptop);
		for(int i = 1; i< DCC.length; i++){			
			DCC[i] = new node3(nodes);
			DCC[i].meta_data = TOP_nodes_to_consider.get_index(i-1);
			Bochert_neighbor(DCC[i], DCC[i].meta_data, all_nodes); 
			all_nodes.delete(DCC[i].meta_data);
		}



		if(display){
			for(int i = 0; i<DCC.length; i++){
				this.insert_spaces_for_iteration("B");
				System.out.println(" -- DCC["+i+"].md: "+DCC[i].meta_data+" and is: "+DCC[i].print_list());
			}
		}


		time_analysis[1] = time_analysis[1] + (System.currentTimeMillis() - start);
		start = System.currentTimeMillis();


		///////////////////////////////////////////////////////////////////////////////////////////////////
		//  TOP WHILE
		///////////////////////////////////////////////////////////////////////////////////////////////////
		if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
			this.insert_spaces_for_iteration("B");
			if(display)
				System.out.println(" TOP WHILE time: "+disp_time_old()+" B_calls: "+B_calls+" starting on, check set is "+(1+0)+" out of "+(DCC.length)+" which is node "+DCC[0].meta_data+" with DCC of: "+DCC[0].print_list()+" with no comp_set but current max of: "+max_star.meta_data);
			else
				System.out.println(" TOP WHILE time: "+disp_time_old()+" B_calls: "+B_calls+" starting on, check set is "+(1+0)+" out of "+(DCC.length)+" which is node "+DCC[0].meta_data+" with DCC size of: "+DCC[0].get_length()+" with no comp_set but current max of: "+max_star.meta_data);
		}


		if(max_star.meta_data <= DCC[0].get_length()){//or equal because if DCC[0].meta_data + DCC[0].get_length() are the nodes, which means one more than DCC[0].get_length()
			B_calls_TOP++;
			temp_element = Newer_Bochert(DCC[0].copy_by_erasing(), (max_star.meta_data==0?0:max_star.meta_data-1), nodes, display, 2);
		}
		else{
			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("This was NOT run because ms.md >= DCC[0].gl");
			}
			temp_element.zero();
		}


		if((temp_element.get_length())>=max_star.meta_data){
			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("found new max star!! te.gl: "+temp_element.get_length()+" deepness: "+deepness+" max_star.md: "+max_star.meta_data);
			}
			max_star.copy_array(temp_element);
			max_star.add(toptop);
			if(!this.is_star(max_star.to_int(), true)){System.out.println("B_calls: "+B_calls+" not star anymore :(");DCC[-1]=null;}
			max_star.meta_data = max_star.get_length();

			if(size_announced_max < (B_iteration_deep + max_star.get_length()) && disp_found_max){
				System.out.println("** Found a new max clique of size: "+(B_iteration_deep + max_star.get_length()));
				size_announced_max = (B_iteration_deep + max_star.get_length());
			}

		}
		else{
		}



		if(display){
			this.insert_spaces_for_iteration("B");
			System.out.println("about to enter main  while loop, ntc: "+nodes_to_consider.print_list()+" Tntc: "+TOP_nodes_to_consider.print_list());
		}



		///////////////////////////////////////////////////////////////////////////////////////////////////
		//  MAIN WHILE
		///////////////////////////////////////////////////////////////////////////////////////////////////
		while(check_set < (DCC.length)){	

			start = System.currentTimeMillis();

			display = (((where_from < 4)&&(level_0_display == true)&&(B_iteration_deep < (display_level+1)))?true:false);


			Bochert_neighbor(temp_element, DCC[check_set].meta_data, original_all_nodes);//the nodes that have already been checked can be used to eliminate unneeded nodes... in fact... I can do this at every level...
			temp_element.use_me_and_not_first(DCC[check_set], temp_element);//only extras
			checked_set.copy_array(temp_element);//remember these deleted nodes...

			all_nodes_in_set_deleted_used = DCC[check_set].copy_by_erasing();//well... shoot... it's necessarily good to remove all the nodes yet... sigh...

			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("At the top, deciding all_nodes for node: "+DCC[check_set].meta_data+", extra already deleted nodes connected to it is: "+temp_element.print_list()+" and all_nodes before reduction was: "+DCC[check_set].print_list()+" and after reduction: "+all_nodes_in_set_deleted_used.print_list());
			}




			temp = 0;
			for(int i = 0; i< check_set; i++){

				memory_element.use_me_and_not_first(all_nodes_in_set_deleted_used, DCC[i]);

				if((temp < (DCC[i].get_length()-memory_element.get_length()))){
					temp = (DCC[i].get_length()-memory_element.get_length());
					comp_set = i;
				}


			}
			alpha3.copy_array(DCC[comp_set]);


			//see if you can eliminate completely first
			run = true;
			alpha3.similar_differences(all_nodes_in_set_deleted_used, temp_element2, best_next_ntc);
			if(best_next_ntc.get_length() == 0){
				if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println("RUN == FALSE!!! Don't run this NODE!!! Because comp_set: "+comp_set+" which is node "+DCC[comp_set].meta_data+" connected to: "+alpha3.print_list()+" contains check_set: "+check_set+" which is node "+DCC[check_set].meta_data+" connected to: "+all_nodes_in_set_deleted_used.print_list());
				}
				run = false;
			}

			if(run){			

				best_next_me.use_me_and_not_first(best_next_ntc, all_nodes_in_set_deleted_used);

				unranked_find_best_ntc_dcc(alpha3,check_set, all_nodes_in_set_deleted_used, unused_best_next_ntc, unused_best_next_me);
				if(unused_best_next_ntc.get_length() < best_next_ntc.get_length()){
					best_next_ntc.copy_array(unused_best_next_ntc);
					best_next_me.copy_array(unused_best_next_me);
				}

				if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println("after unranked find best, best_next_ntc was: "+best_next_ntc.print_list()+" and best_next_me: "+best_next_me.print_list());
				}
			}
			else{
				best_next_ntc.zero();
				best_next_me.zero();
			}




			if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){

				this.insert_spaces_for_iteration("B");
				if(display)
					System.out.println(" MAIN WHILE time: "+disp_time_old()+" B_calls: "+B_calls+" starting on, check set is "+(1+check_set)+" out of "+(DCC.length)+" which is node "+DCC[check_set].meta_data+" with the comp_set "+comp_set+" which is node "+DCC[comp_set].meta_data+" and common nodes are: "+best_next_me.print_list()+" max_star: "+max_star.print_list()+" max_star.md: "+max_star.meta_data);
				else
					System.out.println(" MAIN WHILE time: "+disp_time_old()+" B_calls: "+B_calls+" starting on, check set is "+(1+check_set)+" out of "+(DCC.length)+" which is node "+DCC[check_set].meta_data+" with the comp_set "+comp_set+" which is node "+DCC[comp_set].meta_data+" max_star.md: "+max_star.meta_data);

				if(!run){
					this.insert_spaces_for_iteration("B");
					System.out.println(" so in this case run was actually false, which means it found a set that had a node that could contain all of check_set so go no further (and make ntc == 0) ");					
				}
				//				else
				//					System.out.println(" MAIN WHILE time: "+this.disp_time()+" B_calls: "+B_calls+" starting on, check set is "+(1+check_set)+" out of "+(nodes_to_consider.get_length()+length_extra_alredy_been_checked+(already_been_checked.get_length() == 0?0:1))+" which is node "+alpha[check_set].meta_data+" with alpha length of: "+alpha[check_set].get_length()+" with the comp_set "+comp_set+" which is node "+alpha[comp_set].meta_data+" with alpha length of: "+alpha[comp_set].get_length()+" and common nodes length of: "+memory_element.get_length()+" max_star: "+max_star.print_list()+" max_star.md: "+max_star.meta_data);


				if(degressive_display){
					display_level = B_iteration_deep; 
				}

			}



			if(best_next_ntc.get_length() > 0){



				all_nodes_in_set_deleted_used.memory_next = new node3(nodes);
				all_nodes_in_set_deleted_used.memory_next.memory_previous = all_nodes_in_set_deleted_used; 
				all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_next;
				all_nodes_in_set_deleted_used.use_me_or(best_next_ntc, best_next_me);//copy_array(temp_element);
				//don't need to reduce against extra_extra, it's already been done at this level


				all_nodes_in_set_whole.memory_next = new node3(nodes);
				all_nodes_in_set_whole.memory_next.memory_previous = all_nodes_in_set_whole; 
				all_nodes_in_set_whole = all_nodes_in_set_whole.memory_next;
				//all_nodes_in_set_whole.copy_array(all_nodes_in_set_deleted_used);
				Bochert_neighbor(all_nodes_in_set_whole, DCC[check_set].meta_data, original_all_nodes);//the nodes that have already been checked can be used to eliminate unneeded nodes... in fact... I can do this at every level...

				checked_set.memory_next = checked_set.copy_by_erasing();
				checked_set.memory_next.memory_previous = checked_set; 
				checked_set = checked_set.memory_next;

				nodes_to_consider.memory_next = new node3(nodes);
				nodes_to_consider.memory_next.memory_previous = nodes_to_consider; 
				nodes_to_consider = nodes_to_consider.memory_next;
				nodes_to_consider.copy_array(best_next_ntc);


				memory_element.memory_next = new node3(nodes);
				memory_element.memory_next.memory_previous = memory_element; 
				memory_element = memory_element.memory_next;
				memory_element.copy_array(best_next_me);


				alpha3.memory_next = alpha3.copy_by_erasing(); //new node3(nodes);//DCC[i].copy_by_erasing();
				alpha3.memory_next.memory_previous = alpha3;
				alpha3 = alpha3.memory_next;

				if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println("reduced ntc is: "+nodes_to_consider.print_list()+" and reduced all nodes is: "+all_nodes_in_set_deleted_used.print_list());
				}

				deepness++;


				if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println("Entering while loop with all_nodes: "+all_nodes_in_set_deleted_used.print_list()+" NTC: "+nodes_to_consider.print_list()+" mem_elm: "+memory_element.print_list()+" and first comp_set of: "+comp_set);
				}

				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//                     START SUPER WHILE
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

				time_analysis[2] = time_analysis[2] + (System.currentTimeMillis() - start);

				while(TOP_nodes_to_consider != nodes_to_consider){

					I_was_deleted = true;
					run = true;
					while(I_was_deleted && (nodes_to_consider.get_length() > 0)){

						middle_loop_run++;

						start = System.currentTimeMillis();


						nodes_to_consider.meta_data = nodes_to_consider.pop_first();
						all_nodes_in_set_deleted_used.delete(nodes_to_consider.meta_data);

						this.Bochert_neighbor(temp_element2, nodes_to_consider.meta_data, all_nodes_in_set_deleted_used);
						//this.Bochert_neighbor(all_nodes_extra_extra, nodes_to_consider.meta_data, all_nodes_extra_extra);

						temp_element = reduction(temp_element2, empty_node, null);

						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println("initial check of node: "+nodes_to_consider.meta_data+" which is connected to: "+temp_element.print_list()+/*" but before reduction it was: "+temp_element2.print_list()+*/" which was pulled from all_nodes_in_set_deleted_used of: "+all_nodes_in_set_deleted_used.print_list()+"");
						}

						if((max_star.meta_data>(temp_element.get_length()+deepness))){

							checked_set.add(nodes_to_consider.meta_data);
							if(display){
								this.insert_spaces_for_iteration("B");
								System.out.println("EARLY ELIMINATED OUT!!!! (that is, node: "+nodes_to_consider.meta_data+") aka, no longer can form bigger star because too few left to consider... ms.md: "+max_star.meta_data+" > deepness: "+deepness+" and all_nodes.gl: "+temp_element.get_length()+" which is: "+temp_element.print_list());
							}

							if((max_star.meta_data>(all_nodes_in_set_deleted_used.get_length()+(deepness-1)))){
								//check no more

								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println("EARLY ELIMINATED OUT the rest of the nodes too!!!! because ms.md: "+max_star.meta_data+" > deepness-1: "+(deepness-1)+" and all_nodes_in_set_deleted_used: "+all_nodes_in_set_deleted_used.get_length()+" which is: "+all_nodes_in_set_deleted_used.print_list());
								}


								nodes_to_consider.zero();								
							}

							time_analysis[7] = time_analysis[7] + (System.currentTimeMillis() - start);
							start = System.currentTimeMillis();

						}
						else{

							time_analysis[7] = time_analysis[7] + (System.currentTimeMillis() - start);
							start = System.currentTimeMillis();

							I_was_deleted = this.deletable(nodes_to_consider.meta_data, all_nodes_in_set_whole, empty_node, false, temp_element);

							if(!I_was_deleted){

								all_others_empty = true;
								alpha3.memory_next = new node3(nodes);//DCC[i].copy_by_erasing();
								if(this.get_next_comp_all_nodes(alpha3.memory_next, alpha3, temp_element)){
									I_was_deleted = true;
								}
								alpha3.memory_next.memory_previous = alpha3;
								alpha3 = alpha3.memory_next;
								best_next_ntc.use_me_and_not_first(alpha3, temp_element);
								best_next_me.use_me_and_not_first(best_next_ntc, temp_element);

								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println("alpha3["+0+"] was: "+alpha3.memory_previous.print_list()+" but it's now: "+alpha3.print_list()+" deepness: "+deepness);
								}


								if(all_others_empty && (alpha3.get_length() != 0)){
									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("all_others_empty is not false... so don't run it down yet");
									}
									all_others_empty = false;
								}


								time_analysis[8] = time_analysis[8] + (System.currentTimeMillis() - start);
								start = System.currentTimeMillis();


								alpha3.similar_differences(temp_element, best_unique_alpha, unique_check);
								best_nodes_in_common.use_me_and_not_first(best_unique_alpha, alpha3);


								Pointer_ONLY = alpha3.memory_previous;
								Pointer_ONLY2 = alpha3;

								for(int i = 0; i < deepness; i++){

									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("In for loop, at top of random for loop, i is: "+i);
									}

									Pointer_ONLY2.alpha_next = new node3(nodes);//DCC[i].copy_by_erasing();

									if(i+1 == deepness){// the new one...
										//Bochert_neighbor(temp_element2, nodes_to_consider.memory_previous.meta_data, checked_set);//which of the deleted nodes is connected...
										temp_element2.copy_array(all_nodes_in_set_whole);//all nodes in the set... includes deleted nodes?
										temp_element2.use_me_or(temp_element2, checked_set);//add in deleted nodes... incase there was a deleted node from a previous level, current level deleted nodes should still be contained in all_nodes_whole
										temp_element2.delete(nodes_to_consider.meta_data);//don't include the current node... duh...

										if(display){
											this.insert_spaces_for_iteration("B");
											System.out.println("Adding new one... using the options of deleted nodes: "+checked_set.print_list()+" connected to: "+temp_element2.print_list());
										}

										if(this.get_next_comp_all_nodes_use_deleted(Pointer_ONLY2.alpha_next, temp_element2, temp_element,checked_set)){
											I_was_deleted = true;
										}



									}
									else{

										if(this.get_next_comp_all_nodes(Pointer_ONLY2.alpha_next, Pointer_ONLY.alpha_next, temp_element)){
											I_was_deleted = true;
										}
										Pointer_ONLY = Pointer_ONLY.alpha_next;

									}


									Pointer_ONLY2.alpha_next.alpha_previous = Pointer_ONLY2;
									Pointer_ONLY2 = Pointer_ONLY2.alpha_next;


									unused_best_next_ntc.use_me_and_not_first(Pointer_ONLY2, temp_element);
									unused_best_next_me.use_me_and_not_first(unused_best_next_ntc, temp_element);
									if(unused_best_next_ntc.get_length() < best_next_ntc.get_length()){
										best_next_ntc.copy_array(unused_best_next_ntc);
										best_next_me.copy_array(unused_best_next_me);
									}


									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("alpha3["+i+"] was: "+(i+1 == deepness?"NEW":Pointer_ONLY.print_list())+" but it's now: "+Pointer_ONLY2.print_list()+" deepness: "+deepness);
									}


									Pointer_ONLY2.similar_differences(temp_element, unique_alpha, unique_check);
									nodes_in_common.use_me_and_not_first(unique_alpha, Pointer_ONLY2);
									if(nodes_in_common.get_length() > best_nodes_in_common.get_length()){
										best_unique_alpha.copy_array(unique_alpha);
										best_nodes_in_common.copy_array(nodes_in_common);
									}
									else if(nodes_in_common.get_length() == best_nodes_in_common.get_length()){
										if(unique_alpha.get_length() > best_unique_alpha.get_length()){
											best_unique_alpha.copy_array(unique_alpha);
											best_nodes_in_common.copy_array(nodes_in_common);
										}
									}


									if(all_others_empty && (Pointer_ONLY2.get_length() != 0)){
										if(display){
											this.insert_spaces_for_iteration("B");
											System.out.println("all_others_empty is not false... so don't run it down yet");
										}
										all_others_empty = false;
									}

								}

								time_analysis[9] = time_analysis[9] + (System.currentTimeMillis() - start);
								start = System.currentTimeMillis();


								////////////////////////////////////////////////////////////////////////////////
								/*this.insert_spaces_for_iteration("B");
								System.out.println(" SUPER WHILE "+exit_loop+" initial check of node: "+nodes_to_consider.meta_data+" which is connected to: "+temp_element.print_list()+/*" but before reduction it was: "+temp_element2.print_list()+/" which was pulled from all_nodes_in_set_deleted_used of: "+all_nodes_in_set_deleted_used.print_list()+" Time: "+disp_time_old()+" B_calls: "+B_calls+" starting on, check set is "+(1+check_set)+" out of "+(DCC.length)+" which is node "+DCC[check_set].meta_data+" with the comp_set "+comp_set+" which is node "+DCC[comp_set].meta_data+" and common nodes are: "+best_next_me.print_list()+" max_star: "+max_star.print_list()+" max_star.md: "+max_star.meta_data);
								Pointer_ONLY2 = alpha3;
								System.out.print("Printing alpha nexts that were just found: ");
								for(int i = 0; i < deepness; i++){
									System.out.print(" == "+Pointer_ONLY2.alpha_next.print_list());
								}
								System.out.println();
								exit_loop--;
								if(exit_loop <= 0)
									System.exit(0);
								 */
								///////////////////////////////////////////////////////////////////////////////



								if(I_was_deleted){
									alpha3 = alpha3.memory_previous;
									alpha3.memory_next = null;

								}
								else{	
									//temp_element = reduction(temp_element, empty_node, null);//already been done

									temp_element2.use_me_or(best_unique_alpha, best_nodes_in_common);
									unranked_find_best_ntc_dcc(temp_element2,check_set, temp_element, unused_best_next_ntc, unused_best_next_me);
									if(unused_best_next_ntc.get_length() < best_next_ntc.get_length()){
										best_next_ntc.copy_array(unused_best_next_ntc);
										best_next_me.copy_array(unused_best_next_me);
									}

									//if(best_next_ntc.get_length() == 0){
									//	ranked_find_best_ntc_dcc(alpha3,check_set, temp_element, best_next_ntc, best_next_me);//this is to just use temp_element before it gets corrupted
									//}

									//temp_element.use_me_or(best_next_ntc, best_next_me);

									//wait until now to reduce them
									//best_next_ntc = reduction(temp_element, null, best_next_ntc);
									//best_next_me = reduction(best_next_me, best_next_ntc);
								}
							}

							if(I_was_deleted)
								checked_set.add(nodes_to_consider.meta_data);
						}



						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println("I_was_deleted: "+I_was_deleted+" ntc.md: "+nodes_to_consider.meta_data+" ntc: "+nodes_to_consider.print_list());
						}

						time_analysis[3] = time_analysis[3] + (System.currentTimeMillis() - start);

					}

					if(!I_was_deleted){// || (nodes_to_consider.get_length()+lost_nodes.get_length()) > 0){

						start = System.currentTimeMillis();



						all_nodes_in_set_deleted_used.memory_next = new node3(nodes);
						all_nodes_in_set_deleted_used.memory_next.memory_previous = all_nodes_in_set_deleted_used; 
						all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_next;
						all_nodes_in_set_deleted_used.use_me_or(best_next_me, best_next_ntc);


						all_nodes_in_set_whole.memory_next = new node3(nodes);
						all_nodes_in_set_whole.memory_next.memory_previous = all_nodes_in_set_whole; 
						all_nodes_in_set_whole = all_nodes_in_set_whole.memory_next;
						all_nodes_in_set_whole.copy_array(all_nodes_in_set_deleted_used);


						memory_element.memory_next = new node3(nodes);
						memory_element.memory_next.memory_previous = memory_element; 
						memory_element = memory_element.memory_next;
						memory_element.copy_array(best_next_me);



						if(all_others_empty){
							memory_element.use_me_or(memory_element, best_next_ntc);
							best_next_ntc.zero();
						}
						else if(best_next_ntc.get_length() > 0){

							nodes_to_consider.memory_next = new node3(nodes);
							nodes_to_consider.memory_next.memory_previous = nodes_to_consider; 
							nodes_to_consider = nodes_to_consider.memory_next;
							nodes_to_consider.copy_array(best_next_ntc);


							checked_set.memory_next = new node3(nodes);
							checked_set.memory_next.memory_previous = checked_set;
							checked_set = checked_set.memory_next;
							this.Bochert_neighbor(checked_set, nodes_to_consider.memory_previous.meta_data, checked_set.memory_previous);//move to the next set of deleted nodes the ones from the previous set connected to current node


							checked_set = checked_set.memory_previous;
							nodes_to_consider = nodes_to_consider.memory_previous;

						}

						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println("deepness: "+deepness+" considering node "+nodes_to_consider.meta_data+" with nodes still to consider: "+nodes_to_consider.print_list()+" has memory_elment("+memory_element.get_length()+"): "+memory_element.print_list()+" and it's own ntc: "+(nodes_to_consider.memory_next != null?nodes_to_consider.memory_next.print_list():"NULL")+" all_nodes: "+all_nodes_in_set_deleted_used.print_list()+" checked_set: "+checked_set.print_list());
						}




						//should be checked already higher up
						if((max_star.meta_data>(all_nodes_in_set_deleted_used.get_length()+deepness))){
							//need not look further

							if(display){
								this.insert_spaces_for_iteration("B");
								System.out.println("FAIL!!! THIS SHOULD NEVER RUN!!!! SHOULD'VE ELMINIATED EARLIER!!!! ELIMINATED OUT!!!! aka, no longer can form bigger star because too few left to consider... ms.md: "+max_star.meta_data+" > me.gl: "+memory_element.get_length()+" all_nodes: "+all_nodes_in_set_deleted_used.get_length()+" + deepness: "+deepness);
							}

							checked_set.add(nodes_to_consider.meta_data);
							memory_element = memory_element.memory_previous;								
							all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_previous;
							all_nodes_in_set_whole = all_nodes_in_set_whole.memory_previous;



							alpha3 = alpha3.memory_previous;




						}
						else{


							if(display){
								this.insert_spaces_for_iteration("B");
								System.out.println("all_others_empty before loop is: "+all_others_empty+(all_others_empty?" so all alphas were all zero length going into this":" so there is at least one alpha3 that still has nodes"));//"comp_all_nodes.md: "+comp_nodes.meta_data+" list: "+comp_nodes.print_list()+" mem_elm: "+memory_element.print_list()+" mem_elm is in can: "+(temp_element2.get_length() == memory_element.get_length()?"true":"false")+" comp_all_nodes.mem_prev: "+comp_nodes.memory_previous.print_list()+" all_nodes_in_set: "+all_nodes_in_set_deleted_used.print_list()+" lost_nodes.md: "+lost_nodes.meta_data+" checked_set: "+checked_set.print_list());

								this.insert_spaces_for_iteration("B");
								node3 blap = TOP_checked_set;
								System.out.print("starting at TOP_checked_set, it's: ");
								while(blap != null){
									if(blap == checked_set)
										System.out.print("[checked set]");

									System.out.print(blap.print_list()+" NEXT ");
									blap = blap.memory_next;
								}
								System.out.println(" then null");
							}

							time_analysis[4] = time_analysis[4] + (System.currentTimeMillis() - start);
							start = System.currentTimeMillis();


							if(all_others_empty){

								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println(">> B_calls: "+B_calls+" run: "+run+" calling Bochert("+memory_element.print_list()+" ,cm: "+(max_star.meta_data-deepness<1?0:max_star.meta_data-deepness-1)+"(aka: max_star is: "+max_star.print_list()+") ,sm: "+nodes+" , abc: "+temp_element.print_list()+"; ");
								}

								if((max_star.meta_data-deepness-1<=0?0:max_star.meta_data-deepness-1) >= memory_element.get_length()){

									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("in run loop, in second number check, but run now false because ms.md ("+max_star.meta_data+") - deepness ("+deepness+" -1 >= me.gl"+memory_element.get_length()+" which is the same thing as all_nodes");
									}

									Pointer_ONLY = empty_node;
								}
								else
									Pointer_ONLY = Newer_Bochert(memory_element/*will corrupt this pointer*/, (max_star.meta_data-deepness-1<1?0:max_star.meta_data-deepness-1), nodes, display,3);

								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println(">> returned with: "+Pointer_ONLY.print_list()+" FYI tho, just_a_pointer.get_length: "+Pointer_ONLY.get_length()+" deepness: "+deepness+" <?> max_star.md: "+max_star.meta_data+" and fyi, empty node: "+empty_node.print_list());
								}

								if((Pointer_ONLY.get_length()+deepness)>=max_star.meta_data){
									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("found new max star!! te.gl: "+Pointer_ONLY.print_list()+" deepness: "+deepness+" previous max_star.md: "+max_star.meta_data);
									}

									if(size_announced_max < (B_iteration_deep + max_star.get_length()) && disp_found_max){
										System.out.println("** Found a new max clique of size: "+(B_iteration_deep + max_star.get_length()));
										size_announced_max = (B_iteration_deep + max_star.get_length());
									}

									max_star.copy_array(Pointer_ONLY);
									Pointer_ONLY = nodes_to_consider;
									while(Pointer_ONLY != TOP_nodes_to_consider){
										if(display){
											this.insert_spaces_for_iteration("B");
											System.out.println("Adding: "+Pointer_ONLY.meta_data);
										}
										max_star.add(Pointer_ONLY.meta_data);
										Pointer_ONLY = Pointer_ONLY.memory_previous;
										if(!this.is_star(max_star.to_int(), true)){System.out.println("B_calls: "+B_calls+" not star anymore :(");DCC[-1]=null;}
									}
									max_star.add(DCC[check_set].meta_data);
									max_star.meta_data = max_star.get_length();
									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("just added: "+DCC[check_set].meta_data+" so max_star is now: "+max_star.print_list());
									}
									if(!this.is_star(max_star.to_int(), true)){System.out.println("B_calls: "+B_calls+" not star anymore :(");DCC[-1]=null;}


								}
								else{
								}

								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println("ran it, GOING BACK now");
								}

								checked_set.add(nodes_to_consider.meta_data);
								memory_element = memory_element.memory_previous;
								all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_previous;
								all_nodes_in_set_whole = all_nodes_in_set_whole.memory_previous;


								alpha3 = alpha3.memory_previous;




							}
							else{
								//go deeper

								if(best_next_ntc.get_length() > 0){
									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("did not run it because run was not true at start, PRESSING ON because best_next_ntc: "+best_next_ntc.print_list());
									}

									checked_set = checked_set.memory_next;
									nodes_to_consider = nodes_to_consider.memory_next;

									deepness++;
								}
								else{
									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("did not run it because run was not true at start, GOING BACK because best_next_ntc: "+best_next_ntc.print_list());
									}

									checked_set.add(nodes_to_consider.meta_data);
									memory_element = memory_element.memory_previous;								
									all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_previous;
									all_nodes_in_set_whole = all_nodes_in_set_whole.memory_previous;

									alpha3 = alpha3.memory_previous;


								}

							}
						}
					}
					else{
						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println("GOING BACK because nodes_to_consider is: "+nodes_to_consider.print_list()+" and I_was_deleted: "+I_was_deleted);
						}
						//go back to previous
						all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_previous;
						all_nodes_in_set_whole = all_nodes_in_set_whole.memory_previous;
						nodes_to_consider = nodes_to_consider.memory_previous;
						memory_element = memory_element.memory_previous;

						alpha3 = alpha3.memory_previous;


						checked_set = checked_set.memory_previous;
						checked_set.memory_next = null;
						if(nodes_to_consider.meta_data > 0){
							checked_set.add(nodes_to_consider.meta_data);
						}
						deepness--;
					}
				}


			}

			//you can add in the previous nodes now, the ones you didn't need to check because they were already checked at the highest level, but this additional info can help isolate what needs to be checked
			Bochert_neighbor(DCC[check_set],DCC[check_set].meta_data,original_all_nodes);
			check_set++;
		}



		if(display){
			this.insert_spaces_for_iteration("B");
			System.out.println("Returning: "+max_star.print_list());
		}

		B_iteration_deep--;
		return max_star;

	}


	private boolean does_alpha_already_exist(node3 alpha3, node3 set){

		if(set.length == 0)
			return true;

		node3 temp = alpha3;
		while((temp!=null)&&(temp != set)){
			if(temp.set_equals(set))
				return true;
			temp= temp.alpha_next;
		}
		return false;

	}


	private boolean unranked_find_best_ntc_dcc(node3 alpha3,int check_set, node3 check_all_nodes, node3 best_ntc, node3 best_dcc){

		if(check_all_nodes.length == 0){
			best_ntc.zero();
			best_dcc.zero();
			return false;
		}

		node3 temp = new node3(nodes);
		int[] cycle;
		int most_ntc = -1;
		int most_dcc = -1;
		int bestest_node = -1;
		node3 dcc = new node3(nodes);
		node3 nodes_not_alpha_connected = new node3(nodes); 
		nodes_not_alpha_connected.invert();//now all nodes

		//for(int i = 0; i<check_set; i++){
		nodes_not_alpha_connected.use_me_and_not_first(alpha3, nodes_not_alpha_connected); //all nodes not connected to alpha
		//}
		nodes_not_alpha_connected.use_me_and(nodes_not_alpha_connected, check_all_nodes); //all nodes that aren't connected to alpha but are connected to check_all_nodes

		//cycle = check_all_nodes.to_int();
		cycle = nodes_not_alpha_connected.to_int();


		for(int i = 0; i<cycle.length; i++){
			Bochert_neighbor(dcc, cycle[i], check_all_nodes);//dcc
			temp.use_me_and_not_first(dcc, nodes_not_alpha_connected);//ntc contained in nodes_not_alpha

			if(temp.get_length() > most_ntc){
				most_ntc = temp.get_length();
				most_dcc = dcc.get_length();
				bestest_node = cycle[i];
			}
			if(temp.get_length() == most_ntc){
				if(dcc.get_length() > most_dcc){
					most_ntc = temp.get_length();
					most_dcc = dcc.get_length();
					bestest_node = cycle[i];
				}
			}
		}

		if(most_ntc <= 0){//this means that there are no uncommon nodes
			//System.out.println("alpha3: "+alpha3.print_list());
			//System.out.println("check_all_nodes: "+check_all_nodes.print_list());
			best_dcc.zero();
			best_ntc.zero();
			return false;
		}

		Bochert_neighbor(best_dcc, bestest_node, check_all_nodes);
		best_ntc.use_me_and_not_first(best_dcc, check_all_nodes);
		best_ntc.add(bestest_node);

		return true;

	}


	private boolean get_next_comp_all_nodes(node3 next, node3 comp_all_nodes, node3 all_nodes_deleting){

		boolean display = false;//(B_calls >= 17)?true:false;
		boolean contains_check_set = false;


		if(display){						
			this.insert_spaces_for_iteration("B");
			System.out.println("In get_next_comp_all_nodes, comp_all_nodes: "+comp_all_nodes.print_list()+" all_nodes_deleting: "+all_nodes_deleting.print_list());
		}

		if(comp_all_nodes.get_length() == 0){
			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("returning because comp_all_nodes is zero length");
			}
			next.zero();
			return contains_check_set;
		}

		node3 temp_element = new node3(nodes);
		node3 temp_extra = new node3(nodes);
		node3 temp_unique = new node3(nodes);
		node3 all_nodes_unique = new node3(nodes);
		node3 common = new node3(nodes);

		temp_extra.use_me_and_not_first(all_nodes_deleting, comp_all_nodes);

		if(temp_extra.get_length() == 0){
			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("returning because comp_all_nodes has no nodes that are not already in all_nodes_deleting");
			}
			next.zero();
			contains_check_set = false;
			return contains_check_set;
		}
		if(temp_extra.get_length() == comp_all_nodes.get_length()){
			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("returning because comp_all_nodes has no nodes in common with all_nodes_deleting");
			}
			next.zero();
			contains_check_set = false;
			return contains_check_set;
		}


		//go_no_further = false;
		int[] cycle = temp_extra.to_int();
		int extra = -1;
		int node = -1;
		int minimizing_unique_all_nodes = nodes;


		for(int i = 0; i < cycle.length; i++){

			Bochert_neighbor(temp_element, cycle[i], comp_all_nodes);
			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("!!!!!comp_all_nodes: "+comp_all_nodes.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println("!!!!!connected to: "+cycle[i]+" is temp element: "+temp_element.print_list()+" graph[55][57]"+graph[55][57]);
			}


			temp_element.similar_differences(all_nodes_deleting, temp_unique, all_nodes_unique);
			common.use_me_and_not_first(all_nodes_unique, all_nodes_deleting);

			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("comparing node: "+cycle[i]+" connected to: "+temp_element.print_list()+" with unique: "+temp_unique.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println("check_set connected to: "+all_nodes_deleting.print_list()+" with unique: "+all_nodes_unique.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println("common nodes: "+common.print_list());
			}


			if(common.get_length() != 0){
				//			all_nodes_unique = reduction(all_nodes_deleting, null, all_nodes_unique);
				//			if(display)System.out.println("all nodes unique reduced is: "+all_nodes_unique.print_list());
				if(all_nodes_unique.get_length() == 0){
					contains_check_set = true;
					//				System.out.println("this is deletable, no need to run");

					next.copy_array(temp_element);
					next.meta_data = cycle[i];

					return contains_check_set;
				}


				//			temp_unique = reduction(temp_element, null, temp_unique);
				//			if(display)System.out.println("comp_nodes unique reduced is: "+temp_unique.print_list());


				if(minimizing_unique_all_nodes > all_nodes_unique.get_length()){
					if(display){
						this.insert_spaces_for_iteration("B");
						System.out.println("min_unique is g.t. all_nodes_unique");
					}
					minimizing_unique_all_nodes = all_nodes_unique.get_length();
					extra = temp_unique.get_length();
					node = cycle[i];				
				}
				else if(minimizing_unique_all_nodes == all_nodes_unique.get_length()){ 
					if(temp_unique.get_length() > extra){//guarnteed better
						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println("min_unique is equal to all_nodes_unique, but temp_unique is greater than extra");
						}
						minimizing_unique_all_nodes = all_nodes_unique.get_length();
						extra = temp_unique.get_length();
						node = cycle[i];
					}
				}
			}
		}


		if(node == -1){
			//no common nodes found
			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("none of the next nodes contain any common nodes");
			}
			next.zero();
			contains_check_set = false;
			return contains_check_set;
		}

		Bochert_neighbor(temp_extra, node, comp_all_nodes);
		temp_extra.meta_data = node;

		next.copy_array(temp_extra);
		next.meta_data = node;

		/*		System.out.println("node chosen for next alpha3 was: "+node+" contains_check_set: "+contains_check_set);
		System.out.println("comp_all_nodes: "+all_nodes_deleting.print_list()+" which was reduced to: "+best_ntc.print_list());
		node3 delete_this = reduction(all_nodes_deleting, null, null);
		System.out.println(" it could've been reduced to: "+delete_this.print_list());
		System.out.println(" alpha3 was: "+comp_all_nodes.print_list()+" is now: "+next.print_list());
		 */


		if(display){
			this.insert_spaces_for_iteration("B");
			System.out.println("returning, node "+node+" won"+" and temp_extra: "+temp_extra.print_list());
		}


		return contains_check_set;
	}


	private boolean get_next_comp_all_nodes_use_deleted(node3 next, node3 comp_all_nodes, node3 all_nodes_deleting,node3 checked_set){

		boolean display = false;//(B_calls >= 17)?true:false;
		boolean contains_check_set = false;


		if(display)System.out.println("In get_next_comp_all_nodes, comp_all_nodes: "+comp_all_nodes.print_list()+" all_nodes_deleting: "+all_nodes_deleting.print_list());

		if(comp_all_nodes.get_length() == 0){
			if(display)System.out.println("returning because comp_all_nodes is zero length");
			next.zero();
			return contains_check_set;
		}

		node3 temp_element = new node3(nodes);
		node3 temp_extra = new node3(nodes);
		node3 temp_unique = new node3(nodes);
		node3 all_nodes_unique = new node3(nodes);
		node3 common = new node3(nodes);

		//temp_extra.use_me_and_not_first(all_nodes_deleting, comp_all_nodes);
		temp_extra.copy_array(checked_set);//look into deleted nodes

		if(temp_extra.get_length() == 0){
			if(display)System.out.println("returning because comp_all_nodes has no nodes that are not already in all_nodes_deleting");
			next.zero();
			contains_check_set = false;
			return contains_check_set;
		}

		//go_no_further = false;
		int[] cycle = temp_extra.to_int();
		int extra = -1;
		int node = -1;
		int minimizing_unique_all_nodes = nodes;


		for(int i = 0; i < cycle.length; i++){

			Bochert_neighbor(temp_element, cycle[i], comp_all_nodes);
			if(display)System.out.println("!!!!!comp_all_nodes: "+comp_all_nodes.print_list());
			if(display)System.out.println("!!!!!connected to: "+cycle[i]+" is temp element: "+temp_element.print_list()+" graph[55][57]"+graph[55][57]);

			temp_element.similar_differences(all_nodes_deleting, temp_unique, all_nodes_unique);
			common.use_me_and_not_first(all_nodes_unique, all_nodes_deleting);

			if(display)System.out.println("comparing node: "+cycle[i]+" connected to: "+temp_element.print_list()+" with unique: "+temp_unique.print_list());
			if(display)System.out.println("check_set connected to: "+all_nodes_deleting.print_list()+" with unique: "+all_nodes_unique.print_list());
			if(display)System.out.println("common nodes: "+common.print_list());



			//			all_nodes_unique = reduction(all_nodes_deleting, null, all_nodes_unique);
			//			if(display)System.out.println("all nodes unique reduced is: "+all_nodes_unique.print_list());
			if(all_nodes_unique.get_length() == 0){
				contains_check_set = true;
				//				System.out.println("this is deletable, no need to run");

				next.copy_array(temp_element);
				next.meta_data = cycle[i];

				return contains_check_set;
			}


			//			temp_unique = reduction(temp_element, null, temp_unique);
			//			if(display)System.out.println("comp_nodes unique reduced is: "+temp_unique.print_list());


			if(minimizing_unique_all_nodes > all_nodes_unique.get_length()){
				if(display)System.out.println("min_unique is g.t. all_nodes_unique");
				minimizing_unique_all_nodes = all_nodes_unique.get_length();
				extra = temp_unique.get_length();
				node = cycle[i];				
			}
			else if(minimizing_unique_all_nodes == all_nodes_unique.get_length()){ 
				if(temp_unique.get_length() > extra){//guarnteed better
					if(display)System.out.println("min_unique is equal to all_nodes_unique, but temp_unique is greater than extra");
					minimizing_unique_all_nodes = all_nodes_unique.get_length();
					extra = temp_unique.get_length();
					node = cycle[i];
				}
			}

		}

		Bochert_neighbor(temp_extra, node, comp_all_nodes);
		temp_extra.meta_data = node;

		next.copy_array(temp_extra);
		next.meta_data = node;


		return contains_check_set;
	}




	private void insert_spaces_for_iteration(String mode){
		if (mode == "B"){
			System.out.print(disp_time_old()+" ");

			for (int i = 0; i<=B_iteration_deep; i++)
				System.out.print(".");
			System.out.print(B_iteration_deep+" ");
		}
		else if (mode == "BK"){
			for (int i = 0; i<=BK_iteration_deep; i++)
				System.out.print(" ");
			System.out.print(BK_iteration_deep+" ");
		}
	}		




	public int[] BronKerbosch (int[] R, int[] P, int[] X){

		//		if((BK_calls %100000 == 0))
		//			System.out.println("BK is on call number: "+BK_calls);


		if (verboseBK) {
			for (int i = 0; i<=BK_iteration_deep; i++)
				System.out.print(" ");
			System.out.println(BK_iteration_deep+" Entering BK with R="+array2string(R)+" and P="+array2string(P)+" and X="+array2string(X));

			if (BK_iteration_deep == 1){
				System.out.println("Current Node: "+array2string(R)+" current Iteration Count:"+BK_calls);
			}
			if (BK_iteration_deep == 2){
				System.out.println("Current Node (iteration 2 deep): "+array2string(R)+" current Iteration Count:"+BK_calls);
			}
		}

		//		long start;		

		BK_iteration_deep++;
		BK_calls++;

		if (((P == null) || (P.length ==0)) && ((X == null) || (X.length ==0))){
			BK_iteration_deep--;
			return R;
		}
		else if ((P == null) || P.length ==0){
			BK_iteration_deep--;
			return null;
		}

		int[] current_node = new int[1];
		int[] pivot_node = new int[1];
		int[] Pprime = new int[P.length];
		int[] Pnew, Rnew, Xnew;
		int[] max = null, temp_max = null;
		System.arraycopy(P, 0, Pprime, 0, P.length);


		System.arraycopy(P, 0, pivot_node, 0, 1); //define pivot
		for (int i = 0; i < P.length; i++){ //start on first node that isn't pivot

			//			start = System.currentTimeMillis();

			if (!neighbor(pivot_node[0], P[i])){

				System.arraycopy(P, i, current_node, 0, 1); //define current node

				Pprime = remove_node(Pprime,P[i]);
				Rnew = union(R,current_node);
				Pnew = intersection(Pprime,all_neighbors(current_node[0]));
				Xnew = intersection(X,all_neighbors(current_node[0]));

				//System.out.println("Calling BK on R="+array2string(Rnew)+" and P="+array2string(Pnew)+" and X="+array2string(Xnew));
				temp_max = BronKerbosch(Rnew, Pnew, Xnew);
				if ((max == null) || ((temp_max != null) &&(temp_max.length > max.length))){
					max = temp_max;
				}

				X = union(X, current_node);

			}


			//			System.out.println(System.currentTimeMillis()-start);
			//			this.pause();

		}

		BK_iteration_deep--;

		return max;
	}


	private int[] remove_node(int[] a, int b){
		int[] new_a = new int[a.length-1];
		int index = 0;

		while ((index < a.length) && (a[index] != b))
			index++;

		if ((index < a.length) && (a[index] == b)){ 			
			System.arraycopy(a, 0, new_a, 0, index);
			System.arraycopy(a, index+1, new_a, index, a.length-1-index);
			return new_a;
		}
		else		
			return a;
	}

	private boolean neighbor(int a, int b){
		if (graph[a-1][b-1] == 1)
			return true;
		else
			return false;	
	}

	private int[] all_neighbors(int a){
		int[] temp_ans = new int[nodes];
		int ans_index = 0;

		if (a < 0){
			for (int i = 0; i < nodes; i++){
				temp_ans[ans_index] = i+1;
				ans_index++;
			}			
		}
		else		
			for (int i = 0; i < nodes; i++){
				if (graph[a-1][i] == 1){
					temp_ans[ans_index] = i+1;
					ans_index++;
				}

			}	
		if (ans_index != 0){
			int[] ans = new int[ans_index];
			System.arraycopy(temp_ans, 0, ans, 0, ans_index);
			return ans;
		}
		else
			return null;
	}

	private int[] intersection(int[] a, int[] b){
		if ((a == null) ||  (b == null))
			return null;

		int alen = a.length, blen = b.length;

		int[] c = new int[alen];
		int ia = 0, ib = 0, ic = 0;

		while ((ia < alen) && (ib < blen)){
			if (a[ia] == b[ib]){
				c[ic] = a[ia];
				ia++;
				ib++;
				ic++;
			}
			else if (a[ia] < b[ib]){ 
				ia++;
			}
			else if (b[ib] < a[ia]){ 
				ib++;
			}
		}

		int[] d = new int[ic];

		System.arraycopy(c, 0, d, 0, d.length);

		return d;
	}



	private int[] union(int[] a, int[] b){
		if (a == null)
			return b;
		if (b == null)
			return a;

		int alen = a.length, blen = b.length;



		int[] c = new int[alen + blen];
		int ia = 0, ib = 0, ic = 0;

		while ((ia < alen) || (ib < blen)){
			if ((ia < alen) && (ib < blen)){
				if (a[ia] == b[ib]){
					c[ic] = a[ia];
					ia++;
					ib++;
					ic++;
				}
				else if (a[ia] < b[ib]){ 
					c[ic] = a[ia];
					ia++;
					ic++;
				}
				else if (b[ib] < a[ia]){ 
					c[ic] = b[ib];
					ib++;
					ic++;
				}}
			else if ((ia < alen) && !(ib < blen)){
				c[ic] = a[ia];
				ia++;
				ic++;
			}
			else if ((ib < blen) && !(ia < alen)){
				c[ic] = b[ib];
				ib++;
				ic++;
			}
		}


		int[] d = new int[ic];

		System.arraycopy(c, 0, d, 0, d.length);

		return d;
	}


	public String disp_time_old(){

		/*		long t = System.currentTimeMillis();

		t=t/1000;
		long s = t%60;
		t=t/60;
		long m = t%60;
		t=t/60;
		long h = t%60+7;


		return h+":"+m+":"+s;
		 */

		Date dNow = new Date( );
		SimpleDateFormat ft = new SimpleDateFormat ("MM-dd HH:mm:ss");
		return ft.format(dNow);

	}

	public graph() {
	}

	public graph(int n, int ms) {
		if (ms > n){
			System.out.println("!!! max clique is larger than number of nodes???? That's crazyness...");
			return;
		}
		if (n != (n/ms)*ms){
			System.out.println("!!! Please input n s.t. n|ms (that is to say that the size of the max clique evenly divides the number of nodes)");
			return;
		}

		graph = new int[n][n];
		edges = 0;
		nodes = n;		
		node_edge_count = new int[n];


		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++){
				if (i == j)
					graph[i][j] = 0;
				else if (((j-i)/ms)*ms == (j-i))
					graph[i][j] = initial_not_connected;
				else {
					graph[i][j] = initial_connected;
					edges++;
					node_edge_count[i]++;
				}

			}


		old_graph = graph;
	}

	public graph(String file_name, int display_level,String graphs_directory){

		try {

			/*			System.out.println("Hewwo");
			File dir = new File("./src/Clique");
			if (dir.isDirectory()){
				String[] dircont = dir.list();
				for (int i = 0; i < dircont.length; i++){
					System.out.println(dircont[i]);
				}
			}
			else
				System.out.println("Nope, it's not a directory");
			 */					

			//			File myFile = new File("src\\Clique\\graph_binaries\\"+file_name);
			File myFile = new File(graphs_directory+file_name);
			FileReader fileReader = new FileReader(myFile);

			BufferedReader reader = new BufferedReader(fileReader);

			String line;

			int count = 0, total_nodes, node1, node2;


			while (((line = reader.readLine()) != null) && (line.charAt(0) != 'p')){
				if (display_level >= -1)
					System.out.println(line);
			}

			if (display_level >= -1)
				System.out.println(line);
			line = line.substring(7);

			while ((line.length() > count) && (line.charAt(0) == ' '))
				line = line.substring(1);



			while ((line.length() > count) && (Character.isDigit(line.charAt(count))))
				count++;


			line = line.substring(0, count);
			total_nodes = Integer.parseInt(line);

			graph = new int[total_nodes][total_nodes];	
			nodes = total_nodes;
			edges = 0;
			node_edge_count = new int[nodes];

			if (initial_not_connected != 0)
				for (int i = 0; i < total_nodes; i++)
					for (int j = 0; j<total_nodes ; j++){
						if (i == j)
							graph[i][j] = 0;
						else
							graph[i][j] = initial_not_connected;
					}



			while ((line = reader.readLine()) != null){

				line = line.substring(2);

				count = 0;
				while (Character.isDigit(line.charAt(count)))
					count++;

				node1 = Integer.parseInt(line.substring(0, count));

				line = line.substring(count+1);
				count = 0;

				while ((count < line.length()) && (Character.isDigit(line.charAt(count))))
					count++;

				node2 = Integer.parseInt(line.substring(0, count));

				if (node1 != node2){
					graph[node1-1][node2-1] = initial_connected;
					graph[node2-1][node1-1] = initial_connected;
					node_edge_count[node1-1]++;
					node_edge_count[node2-1]++;
					edges ++;
					edges ++;
				}

			}


			reader.close();

		} catch(Exception ex) {
			ex.printStackTrace();
		} 

		empty_node = new node3(nodes);
		old_graph = graph;
	}


	public graph(int[][] g){
		graph = g;
		nodes = graph.length;
		node_edge_count = new int[nodes];
		edges = 0;
		int sum = 0;
		for (int i = 0; i< nodes; i++){
			sum = 0;
			for (int j=0; j<nodes; j++){
				if (i == j){
					graph[i][j] = 0;
				}
				if (graph[i][j] == 1){
					sum++;
					edges++;
				}
			}
			node_edge_count[i] = sum;
		}

		old_graph = graph;

		graph3 = new node3[nodes];
		for(int i = 0; i<nodes; i++){
			graph3[i] = new node3(graph[i],nodes,true);
			//							System.out.println("node: "+(i+1)+" connected to: "+graph3[i].print_literal());//.print_list());
		}


	}

	public void invert_graph(){

		for(int i = 0; i<graph.length; i++)
			for(int j = 0; j<graph.length; j++){
				if(i == j)
					graph[i][j] = 0;
				else if(graph[i][j] == 1)
					graph[i][j] = 0;
				else 
					graph[i][j] = 1;
			}

		graph3 = this.make_graph3(graph);
	}

	public void disp_graph(int[][] graph){
		for (int i = 0; i < graph.length; i++){
			for (int j = 0; j < graph[i].length; j++){
				System.out.print(graph[i][j]);
				System.out.print(' ');
			}
			System.out.println("");
		}
	}

	public String array2string(int[] array){
		String out = "";

		if (array != null)
			for(int i = 0; i < array.length; i++)
				out = out + array[i] + " ";


		return out;
	}

	/*public String barray2string(int[] array){
		String out = "";

		if (array != null)
			for(int i = 0; i < array.length; i++)
				out = out + (array[i]-1) + " ";


		return out;
	}*/

	public boolean is_star(int[] nodes, boolean clique){


		if((nodes == null)||(nodes.length <= 1))
			return true;

		int comparitor;

		if (clique)
			comparitor = 0;
		else
			comparitor = 1;


		for (int i = 0; i < nodes.length ; i++){
			for (int j = 0; j<nodes.length ; j++){
				if (i != j && graph[nodes[i]-1][nodes[j]-1] == comparitor){
					System.out.println(nodes[i]+" "+nodes[j]+" failed");
					return false;	
				}

			}
		}

		return true;
	}



	public int[] find_P(){
		int[] P = new int[nodes];

		for (int i = 0; i<nodes ; i++)
			P[i] = i+1;

		return P;
	}


	public int[] test(){

		int[] temp = new int[100];


		return temp;
	}

	public String[] create_list_of_graphs(String directory, String include, String exclude){

		try {

			File dir = new File(directory);
			if (dir.isDirectory()){

				Collection<String> dir_listing = new ArrayList<String>( Arrays.asList(dir.list()));
				String[] include_array = include.split("\\s*,\\s*");
				String[] exclude_array = exclude.split("\\s*,\\s*");
				for (int i = 0; i<include_array.length; i++)
					if(include_array[i].indexOf("\"") != -1)include_array[i] = include_array[i].substring(1, include_array[i].length()-1);
				for (int i = 0; i<exclude_array.length; i++)
					if(exclude_array[i].indexOf("\"") != -1)exclude_array[i] = exclude_array[i].substring(1, exclude_array[i].length()-1);
				Collection<String> include_items = new ArrayList<String>( Arrays.asList(include_array));
				Collection<String> exclude_items = new ArrayList<String>( Arrays.asList(exclude_array));

				System.out.println("got here, include: "+include_items.toString());

				if (include.length() != 0){
					dir_listing.retainAll( include_items );
				}
				if (exclude.length() != 0){
					dir_listing.removeAll(exclude_items);
				}

				return (String[]) dir_listing.toArray(new String[dir_listing.size()]);

			}
			else{
				System.out.println("Not a directory: " + directory);
				return new String[0];
			}

		} catch(Exception ex) {
			ex.printStackTrace();
		} 

		return new String[0];
	}


	//random integer in range [a,b] inclusive
	public int randInt(int a,int b){

		return (int)(nextDouble()*(b-a+.99999)+a);

		//((int)((random()/((double)RAND_MAX+1)*((b)-(a)+1)+(a))))
	}
	//randomly return either 1 (with probability 1/d) or -1 (with probability 1-1/d)
	public int randSign(int d){
		//(random()>RAND_MAX/(d) ? -1 : 1)
		return (nextDouble() > (1.0/(double)d) ? -1 : 1);
	}

	public int[][] createLeemon3SAT(int numVar, int numClauses, boolean force3satTrue){

		int sat[][] = new int[3][numClauses];
		int numVert = 3*numClauses;
		int perm[] = new int[numVert]; 

		for (int j=0; j<numClauses; j++) { //generate a random, hard 3SAT problem
			for (int i=0; i<3; i++)
				sat[i][j] = randSign(force3satTrue ? 4 : 2) * randInt(1,numVar);
			if (force3satTrue)
				sat[0][j] = randInt(1,numVar);
		}//for harder 3SAT: permute each clause, negate solution vars with prob 0.5

		for (int i=0; i<numVert; i++) perm[i]=i; //make perm[] a random permutation
		for (int i=0; i<numVert; i++) {
			int r = randInt(i,numVert-1);
			int t = perm[r];
			perm[r] = perm[i];
			perm[i] = t; }


		int[][] graph = new int[numVert][numVert];

		for (int i=0; i<numVert; i++) //convert 3SAT problem to max clique problem
			for (int j=0; j<numVert; j++)
				if (i/3 != j/3 && sat[i%3][i/3] != -sat[j%3][j/3])
					graph[perm[i]][perm[j]] = 1;
				else
					graph[perm[i]][perm[j]] = 0;

		/*		
		try {

			File file = new File("..\\graph_binaries\\AESLeemon3SATGen-g"+graphnum+"-v"+numVar+"-"+(force3satTrue?"forced":"unforced")+".clq");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("c graph generated with Leemon random 3SAT generator \nc used variables: "+numVar+" clauses: "+numClauses+" and has a graph number of: "+graphnum+" and has nodes: "+(short)(4.27*numVar)+"\n");
			for(int i = 0; i<numClauses; i++){
				bw.write("c "+sat[0][i]+" "+sat[1][i]+" "+sat[2][i]+"\n");
			}
			bw.write("p edge "+(3*numClauses)+" 0\n");

			for(int i = 0; i<numClauses*3; i++){
				for(int j = 0; j<i; j++){
					if(graph[i][j] == 1){
						bw.write("e "+(i+1)+" "+(j+1)+"\n");
					}
				}
			}

			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		 */

		return graph;


	}

	public void start_random_with_seed(int s){
		generator = new Random(s);
		randseed = s;
	}

	public void init_AES_rand(int G, int N)
	{	
		//	System.out.println("poopoo - G:"+G+" N:"+N);
		plaintext = new byte[32];
		encryptionKey = new byte[16];

		for (byte i=0; i<8; i++) {
			encryptionKey[8+i] = (byte)(G>>>(i*8));//255;
			encryptionKey[i] = (byte)(N>>>(i*8));//255;
		}

		//    for (byte i=0; i<16; i++) {
		//    	encryptionKey[i] = (byte)i;//255;
		//    }


		//    System.out.println(encryptionKey[0]+" "+encryptionKey[1]+" "+encryptionKey[2]+" "+encryptionKey[3]+" "+encryptionKey[4]+" "+encryptionKey[5]+" "+encryptionKey[6]+" "+encryptionKey[7]+" "+encryptionKey[8]+" "+encryptionKey[9]+" "+encryptionKey[10]+" "+encryptionKey[11]+" "+encryptionKey[12]+" "+encryptionKey[13]+" "+encryptionKey[14]+" "+encryptionKey[15]);    
		//    System.exit(0);

		//    for (int i=0; i<plaintext.length; i++)
		//    	plaintext[i] = (byte)i;
	}


	public void tickplaintext(){

		for(int i = 0; i<8; i++){
			plaintext[i] = (byte)(plaintext[i] + 1);
			if(plaintext[i] != 0)
				break;
		}

	}

	public double nextDouble(){

		//		if(true)
		//			return generator.nextDouble();

		if(current_byte == 8){

			try{
				cipher = encrypt(plaintext, encryptionKey);

			} catch (Exception e) {
				e.printStackTrace();
			} 

			//		System.out.print("cipher: ");
			//		for(int i = 15; i>=0; i--)
			//			System.out.print(Integer.toHexString(cipher[i])+" ");
			//		System.out.println();

			current_byte = 0;
			tickplaintext();
		}

		//		System.out.println(cipher[current_byte*4+0]+" "+((long)cipher[current_byte*4+1])+" "+((long)cipher[current_byte*4+2])+" "+(((long)cipher[current_byte*4+3])));

		long javadumb1 = (long)(cipher[current_byte*4+0]&0x0ff);
		long javadumb2 = (long)(cipher[current_byte*4+1]&0x0ff);
		long javadumb3 = (long)(cipher[current_byte*4+2]&0x0ff);
		long javadumb4 = (long)(cipher[current_byte*4+3]&0x0ff);

		long num = javadumb1+(javadumb2<<8)+(javadumb3<<16)+(javadumb4<<24);
		//long num = ((long)cipher[current_byte*4+0])+((long)cipher[current_byte*4+1]<<8)+((long)cipher[current_byte*4+2]<<16)+(((long)cipher[current_byte*4+3])<<24);
		//		num = num>>>1;//because all longs in java are signed, and I don't want negatives
		//		System.out.println("num sum: "+Integer.toHexString((int)num)+" = "+num);
		long dividend = (long)(0xfffffff);
		dividend = (dividend<<4)+0xf;
		//		dividend = dividend>>>1;
		//		System.out.println("dividing by: "+dividend);

		current_byte++;

		return (double)num/(double)dividend;
	}

	public int[][] create3SAT(int variables, int clauses){


		int c = clauses;    //number of clauses
		int v = variables; //number of variables

		if(c > (((v*2)*((v-1)*2)*((v-2)*2))/6)){
			System.out.println("you've requested too many clauses ("+clauses+") for the number of variables ("+variables+"), returning "+(((v*2)*((v-1)*2)*((v-2)*2))/6)+" unique clauses instead");
			return create3SAT(v,(((v*2)*((v-1)*2)*((v-2)*2))/6));
		}


		int[][] sat = new int[c][3];        //a single 3SAT problem
		//		int m[3*c][3*c];      //a single max clique problem
		//Random generator = new Random(0);//seeded to 0 because Leemon said it'd be a good idea for testing consistentcy

		double r;
		int var1; 
		int var2;
		int var3;
		int[] var = new int[3];
		boolean found_match;

		for(int row = 0; row<c; row++){

			r = generator.nextDouble();
			var1 = (int)(r*(double)v*2.0)-v;
			var1 = (var1>=0?var1+1:var1);
			do{
				r = generator.nextDouble();
				var2 = (int)(r*(double)v*2.0)-v;
				var2 = (var2>=0?var2+1:var2);
			}while(Math.abs(var1) == Math.abs(var2));
			do{
				r = generator.nextDouble();
				var3 = (int)(r*(double)v*2.0)-v;
				var3 = (var3>=0?var3+1:var3);
			}while(((Math.abs(var1)) == (Math.abs(var3)))||((Math.abs(var2)) == (Math.abs(var3))));

			if(Math.abs(var1) < Math.abs(var2)){
				if(Math.abs(var1) < Math.abs(var3)){
					var[0] = var1;
					if(Math.abs(var2) < Math.abs(var3)){
						var[1] = var2;
						var[2] = var3;
					}
					else{
						var[1] = var3;
						var[2] = var2;	
					}
				}
				else{//3<1<2
					var[0] = var3;
					var[1] = var1;	
					var[2] = var2;	
				}
			}
			else if(Math.abs(var1) < Math.abs(var3)){//2<1<3
				var[0] = var2;
				var[1] = var1;	
				var[2] = var3;				
			}
			else{//2<1 3<1
				var[2] = var1;
				if(Math.abs(var2) < Math.abs(var3)){//2<3<1
					var[0] = var2;
					var[1] = var3;					
				}
				else{//3<2<1
					var[0] = var3;
					var[1] = var2;					
				}
			}


			found_match = false;
			for(int i = 0; i<row; i++){
				if((sat[i][0] == var[0])&&(sat[i][1] == var[1])&&(sat[i][2] == var[2])){
					//System.out.println("found that number: "+i+" is the same, it's: "+sat[i][0]+" "+sat[i][1]+" "+sat[i][2]);
					found_match = true;
					break;
				}
			}


			sat[row][0] = var[0];
			sat[row][1] = var[1];
			sat[row][2] = var[2];

			//System.out.println("just added number: "+row+" it's: "+sat[row][0]+" "+sat[row][1]+" "+sat[row][2]);

			if(found_match){
				row--;//repeat this one
			}


		}


		int[][] graph = new int[3*c][3*c];

		for(int i = 0; i<c; i++){
			for(int j = 0; j<i; j++){
				for(int k = 0; k<3; k++){
					for(int l = 0; l<3; l++){
						if(sat[i][k] != -1*sat[j][l]){
							graph[3*i+k][3*j+l] = 1;
							graph[3*j+l][3*i+k] = 1;
						}
					}
				}
			}

		}



		try {

			File file = new File("..\\graph_binaries\\Random3SATGen-"+v+/*"-"+c+*/".clq");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("c graph generated with random 3SAT generator \nc used variables: "+v+" clauses: "+c+"\n");
			for(int i = 0; i<c; i++){
				bw.write("c "+sat[i][0]+" "+sat[i][1]+" "+sat[i][2]+"\n");
			}
			bw.write("p edge "+(3*c)+" 0\n");

			for(int i = 0; i<c*3; i++){
				for(int j = 0; j<i; j++){
					if(graph[i][j] == 1){
						bw.write("e "+(i+1)+" "+(j+1)+"\n");
					}
				}
			}

			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return graph;
	}



	public static byte[] encrypt(byte[] plainText, byte[] encryptionKey) throws Exception {
		Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
		SecretKeySpec k = new SecretKeySpec(encryptionKey, "AES");
		c.init(Cipher.ENCRYPT_MODE, k);
		byte[] encryptedData = c.doFinal(plainText);
		return encryptedData;
	}

	public static byte[] decrypt(byte[] cipherText, byte[] encryptionKey) throws Exception{
		Cipher d = Cipher.getInstance("AES/ECB/NoPadding");
		SecretKeySpec k = new SecretKeySpec(encryptionKey, "AES");
		d.init(Cipher.DECRYPT_MODE, k);
		byte[] data = d.doFinal(cipherText);
		return data;
	}

	private int get_node_with_max_degree(node3 graphmain){

		int big_node = -1;
		int big_size = -1;
		int[] graphnodes = graphmain.to_int();
		node3 test = new node3(nodes);

		for(int i = 0; i<graphnodes.length; i++){
			this.Bochert_neighbor(test, graphnodes[i], graphmain);
			if(test.length > big_size){
				big_size = test.length;
				big_node = graphnodes[i];
			}
		}

		return big_node;
	}

	private void connected_compent(node3 graphmain/*preserved*/, node3 sub1, node3 sub2){

		int[] considerations; 
		sub1.zero();
		sub2.copy_array(graphmain);

		boolean added_one = true;
		sub1.add(sub2.pop_first());
		int[] sub1_int;

		while(added_one){

			sub2.delete(sub1);
			sub1_int = sub1.to_int();
			considerations = sub2.to_int();
			added_one = false;

			for(int i = 0; i<considerations.length; i++){
				for(int j = 0; j<sub1_int.length; j++){
					if(graph[sub1_int[j]-1][considerations[i]-1]==1){
						sub1.add(considerations[i]);
						added_one = true;
						break;
					}
				}

			}
		}

	}

	public int[] domination(node3 graphmain){

		int[] checkset = graphmain.to_int();
		node3 node1 = new node3(nodes);
		node3 node2 = new node3(nodes);
		node3 node1_unique = new node3(nodes);
		node3 node2_unique = new node3(nodes);
		int[] return_val = new int[2];//rv[0]=dominated, rv[1]=dominator



		for(int i = 0; i<checkset.length; i++)
			for(int j = i+1; j< checkset.length; j++)
				if(graph[checkset[i]-1][checkset[j]-1] == 1){
					//						  System.out.println("checking node1: "+(checkset[i])+" against node2: "+(checkset[j]));
					this.Bochert_neighbor(node1, checkset[i], graphmain);
					this.Bochert_neighbor(node2, checkset[j], graphmain);

					node1.similar_differences(node2, node1_unique, node2_unique);
					node1_unique.delete(checkset[j]);
					node2_unique.delete(checkset[i]);
					//						  System.out.println("found node1_u: "+node1_unique.print_list()+" node2_u: "+node2_unique.print_list());
					if(node1_unique.length == 0){
						//							  return checkset[j];
						return_val[0] = checkset[j];
						return_val[1] = checkset[i];
						return return_val;
					}
					else if(node2_unique.length == 0){
						//							  return checkset[i];
						return_val[0] = checkset[i];
						return_val[1] = checkset[j];
						return return_val;
					}
				}			  

		return null;
	}

	private boolean contains_anti_triangle(node3 graphmain){

		//			  System.out.println("checking for ats, graphmain: "+graphmain.print_list());

		int[] checkset = graphmain.to_int();
		node3 node1 = new node3(nodes);
		node3 node2 = new node3(nodes);
		node3 node1_unique = new node3(nodes);
		node3 node2_unique = new node3(nodes);
		node3 node1_nc = new node3(nodes);
		node3 node2_nc = new node3(nodes);


		for(int i = 0; i<checkset.length; i++)
			for(int j = i+1; j<checkset.length; j++)
				if(graph[checkset[i]-1][checkset[j]-1] == 0){
					this.Bochert_neighbor(node1, checkset[i], graphmain);
					this.Bochert_neighbor(node2, checkset[j], graphmain);
					//						  System.out.println("checking node1: "+(checkset[i])+" contains: "+node1.print_list()+" against node2: "+(checkset[j])+" contains: "+node2.print_list());

					node1_nc.use_me_and_not_first(node1, graphmain);
					node1_nc.delete(checkset[i]);
					node2_nc.use_me_and_not_first(node2, graphmain);
					node2_nc.delete(checkset[j]);
					//						  System.out.println("found node1_nc: "+node1_nc.print_list()+" node2_nc: "+node2_nc.print_list());

					node1_unique.use_me_and(node1_nc, node2_nc);

					if(node1_unique.length != 0)
						return true;
				}			  


		return false;
	}

	public int[][] copy_graph(){

		int[][] new_graph = new int[nodes][nodes];

		for(int i = 0; i<new_graph.length; i++){
			new_graph[i] = graph[i].clone();
		}				  

		return new_graph;
	}
	public node3[] make_graph3(int[][] usegraph){
		node3[] newgraph3 = new node3[nodes];
		for(int i = 0; i<nodes; i++){
			newgraph3[i] = new node3(usegraph[i],nodes,true);
			//							System.out.println("node: "+(i+1)+" connected to: "+graph3[i].print_literal());//.print_list());
		}
		return newgraph3;
	}

	public int[][] fold(node3 graphmain, int[] v, int[] double_node_list, int[] hidden_node_list, int[] index){

		int[] checkset = graphmain.to_int();
		node3 connected = new node3(nodes);
		int[] connected_list;
		int[][] new_graph;

		//			  int[] double_node_list;
		//			  int[] hidden_node_list;
		//int index = 0;
		node3 combined = new node3(nodes);
		int[] combined_list;
		index[0] = 0;

		boolean second_connection = false;
		boolean need_second_inversion = false;


		for(int i = 0; i<checkset.length; i++){
			this.Bochert_neighbor(connected, checkset[i], graphmain);
			if(connected.length == 1){ //special case
				v[0] = checkset[i];
				graphmain.delete(checkset[i]);
				graphmain.delete(connected);
				return new int[0][0];
			}
			if(connected.length <= 4)
				if(!contains_anti_triangle(connected)){
					//						  System.out.println("found that node: "+checkset[i]+" is foldable and is connected to: "+connected.print_list());

					v[0] = checkset[i];
					connected_list = connected.to_int();
					new_graph = this.copy_graph();
					//double_node_list = new int[connected_list.length];
					//hidden_node_list = new int[connected_list.length];
					if((connected.length == 4)&&((graph[connected_list[0]-1][connected_list[2]-1] == 0)&&(graph[connected_list[0]-1][connected_list[3]-1] == 0)&&(graph[connected_list[1]-1][connected_list[2]-1] == 0)&&(graph[connected_list[1]-1][connected_list[3]-1] == 0))){//special case, can't do normal naming method
						need_second_inversion = true;
					}
					//combine nodes, save them as j-node, change new graph accordingly
					for(int j = 0; j<connected_list.length; j++){
						second_connection = false;
						for(int k = j+1; k<connected_list.length; k++){
							if(graph[connected_list[j]-1][connected_list[k]-1] == 0){

								if(need_second_inversion){//inverted
									//									  System.out.println("nsi, inverted");
									double_node_list[index[0]] = connected_list[k];
									hidden_node_list[index[0]] = connected_list[j];
								}
								else if(((double_node_list[0] == connected_list[j])||(double_node_list[1] == connected_list[j])||(double_node_list[2] == connected_list[j]))){//inverted
									//									  System.out.println("nsi, inverted");
									double_node_list[index[0]] = connected_list[k];
									hidden_node_list[index[0]] = connected_list[j];										  
								}
								else if(!second_connection){//normal
									//									  System.out.println("!sc, normal");
									double_node_list[index[0]] = connected_list[j];
									hidden_node_list[index[0]] = connected_list[k];
								}
								else{//inverted
									//									  System.out.println("nsi, inverted");
									double_node_list[index[0]] = connected_list[k];
									hidden_node_list[index[0]] = connected_list[j];
								}

								//								  System.out.println("combining node: "+double_node_list[index[0]]+" with now hidden node: "+hidden_node_list[index[0]]);

								//update new graph
								combined.use_me_or(graph3[connected_list[j]-1], graph3[connected_list[k]-1]);
								combined_list = combined.to_int();
								for(int l = 0; l<combined_list.length; l++){
									if(connected_list[j] != combined_list[l]){
										//System.out.println("connecting node: "+connected_list[j]+" to node: "+combined_list[l]);
										new_graph[double_node_list[index[0]]-1][combined_list[l]-1] = 1;
										new_graph[combined_list[l]-1][double_node_list[index[0]]-1] = 1;
									}
								}

								//								  System.out.println("resulting connections are: "+combined.print_list());

								index[0]++;

								if(need_second_inversion)
									need_second_inversion = false;
								else
									second_connection = true;
							}
						}
					}

					//						  System.out.println("double_node_list: "+this.array2string(double_node_list));

					//add one edge betwen each pair of new nodes
					for(int j = 0; j<index[0]; j++)
						for(int k = j+1; k<index[0]; k++){
							//							  System.out.println("connected nodes "+double_node_list[j]+" and "+double_node_list[k]);
							new_graph[double_node_list[j]-1][double_node_list[k]-1] = 1;
							new_graph[double_node_list[k]-1][double_node_list[j]-1] = 1;
						}
					//test
					for(int ii = 0; ii<nodes; ii++)
						if(new_graph[ii][ii] == 1){
							System.out.println("double_node_list: "+this.array2string(double_node_list));
							System.out.println("2it has a node connected to itself: "+ii);
							System.exit(0);
						}

					//delete N[v], or at least all but the combined nodes
					for(int j = 0; j<index[0]; j++){
						connected.delete(double_node_list[j]);
					}
					graphmain.delete(connected);//delete non reused nodes
					graphmain.delete(checkset[i]);//delete node v

					for(int ii = 0; ii<nodes; ii++)
						if(new_graph[ii][ii] == 1){
							System.out.println("it has a node connected to itself: "+ii);
							System.exit(0);
						}


					return new_graph;
				}


		}

		return null;
	}

	public node3 mirrors(node3 graphmain, int v){

		node3 not_connected_list = new node3(nodes);
		node3 connected = new node3(nodes);
		this.Bochert_neighbor(connected, v, graphmain);
		not_connected_list.use_me_and_not_first(connected,graphmain);
		not_connected_list.delete(v);

		int[] checkset = not_connected_list.to_int();
		node3 connected_unique = new node3(nodes);
		node3 sub = new node3(nodes);
		node3 sub_unique = new node3(nodes);

		node3 mirror_list = new node3(nodes);

		for(int i = 0; i < checkset.length; i++){
			this.Bochert_neighbor(sub, checkset[i], graphmain);
			connected.similar_differences(sub, connected_unique, sub_unique);
			if(this.is_star(connected_unique.to_int(), true)){
				mirror_list.add(checkset[i]);
			}
		}

		return mirror_list;
	}

	public node3 mis(node3 checkset, int depth, boolean display){
		if(display)	System.out.println(depth+": starting mis, checkset: "+checkset.print_list());
		boolean next_display = false;


		node3 sub1 = new node3(nodes);
		node3 sub2 = new node3(nodes);
		int v;
		int[] vint;


		if(checkset.length <= 1)
			return checkset.copy_by_erasing();

		//connected components 
		connected_compent(checkset, sub1, sub2);
		if(sub2.length != 0){
			if(display)			  System.out.println(depth+": splitting for connected component, component 1: "+sub1.print_list()+" component 2: "+sub2.print_list());
			sub1 = mis(sub1, depth+1,next_display);
			sub2 = mis(sub2, depth+1,next_display);
			sub1.use_me_or(sub1, sub2);
			return sub1;
		}

		//dominance
		vint = this.domination(checkset);
		if(vint != null){
			//				  if((vint[0] == -300)||((display)&&((vint[0]==1)||(vint[0]==23)||(vint[0]==6)||(vint[0]==31))))
			//					  next_display = true;
			checkset.delete(vint[0]);
			if(display)			 System.out.println(depth+" found that node: "+vint[0]+" is dominated by node: "+vint[1]);
			return mis(checkset, depth,next_display);
		}				  

		//foldable
		vint = new int[1];
		int[] double_node_list = new int[nodes];
		int[] hidden_node_list = new int[nodes];
		int[] index = new int[1];
		int[][] new_graph = this.fold(checkset, vint, double_node_list, hidden_node_list, index);
		if(new_graph != null){
			//				  if((vint[0] == 76)||(vint[0] == 33)||(vint[0] == 59)||(vint[0] == 54)){
			//					  next_display = true;
			//				  }
			if(display)			 System.out.println(depth+": folding node: "+vint[0]+" checking set: "+checkset.print_list());
			if(new_graph.length == 0){//special case of one node
				node3 returned_set = mis(checkset, depth+1,next_display);
				returned_set.add(vint[0]);	
				return returned_set;
			}
			else{
				int[][] saved_old_graph = graph;
				graph = new_graph;
				graph3 = this.make_graph3(graph);

				node3 returned_set = mis(checkset, depth+1,next_display);

				for(int i = 0; i<index[0]+1; i++){//will only have one node, or the vint[0], never can it have two in the same max ind set because they're connected
					if(i == index[0]){//didn't find any double nodes in the ind set
						returned_set.add(vint[0]);
					}
					else if(returned_set.find(double_node_list[i])){
						returned_set.add(hidden_node_list[i]);
						break;
					}//add the nodes back in that were taken if it's double node was chosen
				}

				graph = saved_old_graph;
				graph3 = make_graph3(graph);
				if(display) System.out.println(depth+": after folding, returning with: "+returned_set.print_list());
				return returned_set;
			}
		}


		//find node of max degree
		v = this.get_node_with_max_degree(checkset);

		//find mirrors
		node3 mirrors = mirrors(checkset,v);

		//			  if(display &&((v == 77)||(v==60)||(v==65)||(v==61)||(v==62)||(v==69)||(v==47)||(v==2)||(v==17)||(v==72)||(v==34)||(v==10)||(v==29)||(v==12)||(v==25)||(v==58)||(v==14)||(v==15)||(v==51)||(v==27)||(v==74)))
		//				  next_display = true;

		sub1.use_me_and_not_first(mirrors, checkset);
		sub1.delete(v);
		if(display)			  System.out.println(depth+": G-v-M, v: "+v+" mirrors: "+mirrors.print_list()+" searching: "+sub1.print_list());
		sub1 = mis(sub1,depth,next_display);
		//			  System.out.println(depth+": no optimzing, G-v-M is: "+sub1.print_list());

		next_display = false;

		//			  if(display &&(false))
		//				  next_display = true;

		this.Bochert_neighbor(sub2, v, checkset);
		sub2.use_me_and_not_first(sub2, checkset);
		sub2.delete(v);
		if(display)			  			  System.out.println(depth+": 1+G-N[v], searching: "+sub2.print_list());
		sub2 = mis(sub2, depth+1,next_display);
		sub2.add(v);

		if(sub1.length > sub2.length)
			return sub1;
		else
			return sub2;
	}

	public static void main(String args[]) throws Exception
	{


		/*System.out.println("shoot1");
	    try {

		      System.out.println("==Java==");
		      System.out.println("plain:   " + plaintext);

		      byte[] cipher = encrypt(plaintext, encryptionKey);

		      System.out.print("cipher:  ");
		      for (int i=0; i<cipher.length; i++)
		        System.out.print(new Integer(cipher[i])+" ");
		      System.out.println("");

		      String decrypted = decrypt(cipher, encryptionKey);

		      System.out.println("decrypt: " + decrypted);

		    } catch (Exception e) {
		      e.printStackTrace();
		    } 
		 */




		graph g = new graph();
		long start;
		int [] temp = new int[0];
		long elapsedTimeMillis;
		int[] r = null, x = null;
		int[] p = g.find_P();




		Options options = new Options();
		options.addOption("h","help", false, "this menu");
		options.addOption("d","directory", true, "directory for graph binaries, default is \"../graph_binaries\"");		
		options.addOption("g","graphs", true, ".clq graphs to run, default is all graphs in directory of graph binaries. Can be comma delimiated list, eg \"brock200_1.clq,brock200_2.clq,brock200_3.clq,brock200_4.clq\"");
		options.addOption("e","exclude", true, "graphs to exclude. Default is \"MANN_a45.clq,MANN_a81.clq,keller5.clq,keller6.clq\"");
		options.addOption("m","max",true,"display incrimental max as it's found, default is true");
		options.addOption("t","threads",true,"threads to allow, default is 24. Use 0 to disable multithreding");
		options.addOption("v","verbosity",true,"set verbosity, -2 minimal display, -1 display graph meta no algorithm out, 0 graph meta and algorithm display, 1 >= increasing algorithm verbosity level, default -1");
		options.addOption("o","other",true,"use other algorithm to find max Clique. Options include:\n"
				+ "     BK (BronKerbosch with pivot)\n"
				+ "     MC (Tomiata's MC)\n"
				+"      MC0 (Tomiata's MC)\n"
				+"      MCQ1 (Tomiata's MCQ style 1)\n"
				+"      MCQ2 (Tomiata's MCQ style 2)\n"
				+"      MCQ3 (Tomiata's MCQ style 3)\n"
				+"      MCSa1 (Tomiata's MCSa style 1)\n"
				+"      MCSa2 (Tomiata's MCSa style 2)\n"
				+"      MCSa3 (Tomiata's MCSa style 3)\n"
				+"      MCSb1 (Tomiata's MCSb style 1)\n"
				+"      MCSb2 (Tomiata's MCSb style 2)\n"
				+"      MCSb3 (Tomiata's MCSb style 3)\n"
				+"      BBMC1 (San Segundo's BBMC style 1)\n"
				+"      BBMC2 (San Segundo's BBMC style 1)\n"
				+"      BBMC3 (San Segundo's BBMC style 1)\n");


		String include_graphs;
		String graphs_directory;
		int num_threads;
		int mid_num_threads;
		boolean disp_found_max;		
		int display_level; 
		String exclude_graphs;
		String other_algorithm;


		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse( options, args);
		} catch (ParseException e){
			System.out.println(e.getMessage());
			formatter.printHelp("bochert [options] \n"
					+ "A multi-threaded, infinitely scalable solution to find an exact max clique from a graph.clq (nP problem-set solution)\n\n", options);
		}

		if(cmd.hasOption("h")){
			formatter.printHelp("bochert [options] \n"
					+ "A multi-threaded, infinitely scalable solution to find an exact max clique from a graph.clq (nP problem-set solution)\n\n", options);
			System.exit(0);
		}

		if(cmd.hasOption("d")){
			graphs_directory = cmd.getOptionValue("d");
			if (graphs_directory.charAt(graphs_directory.length()-1) != '\\')
				graphs_directory = graphs_directory + "\\";
			System.out.println("directory set to: " + graphs_directory);
		}
		else{
			graphs_directory = "..\\graph_binaries\\";
		}

		if(cmd.hasOption("g")){
			include_graphs = cmd.getOptionValue("g");
			System.out.println("graphs included set to: " + include_graphs);
		}
		else{
			include_graphs = "";
		}

		if(cmd.hasOption("e")){
			exclude_graphs = cmd.getOptionValue("e");
			System.out.println("graphs excluded set to: " + exclude_graphs);
		}
		else{
			exclude_graphs = "\"MANN_a45.clq\",\"MANN_a81.clq\",\"keller5.clq\",\"keller6.clq\"";
		}

		if(cmd.hasOption("m")){
			disp_found_max = Boolean.parseBoolean(cmd.getOptionValue("m"));
			System.out.println("display incrimental max set to: " + disp_found_max);
		}
		else{
			disp_found_max = true;
		}

		if(cmd.hasOption("t")){
			num_threads = Integer.parseInt(cmd.getOptionValue("t"));
			mid_num_threads = num_threads / 2;
			num_threads = num_threads - mid_num_threads;
			System.out.println("total allowed threads set to: " + (num_threads + mid_num_threads));
		}
		else{
			num_threads = 12;
			mid_num_threads = 12;
		}

		if(cmd.hasOption("v")){
			display_level = Integer.parseInt(cmd.getOptionValue("v"));
			System.out.println("Set verbosity to: " + (display_level));
			if (display_level > -1)
				System.out.println("debugging has been removed from this compile of code, please grab the previous compilation on github");
			if ((display_level < -1) && (disp_found_max))
				System.out.println("verbosity set to "+display_level+" but displaying incrimental maxes. To prevent this, run again with option \"-m false\"");
		}
		else{
			display_level = -1; 
		}

		if(cmd.hasOption("o")){
			other_algorithm = cmd.getOptionValue("o");
			System.out.println("Other algorithm Set to: " + (other_algorithm));
		}
		else{
			other_algorithm = ""; 
		}



		String s[] = g.create_list_of_graphs(graphs_directory, include_graphs, exclude_graphs);

		System.out.println("***********************************************************************************************************");
		System.out.println("Running on graphs: ");
		for(int i = 0; i < s.length; i++)
			System.out.print(s[i]+" ");
		System.out.println();


		for(int i = 0; i<s.length; i++){//i<s.length; i++){
			if (display_level >= -1){
				System.out.println("***********************************************************************************************************");
				System.out.println("graph#"+i+" "+s[i]);
			}


			if (other_algorithm.equalsIgnoreCase("BK")){

				System.out.println("Running BronKerbosch on graph ("+(i+1)+"/"+(s.length)+"): "+s[i]+" Number of nodes: "+g.nodes);
				start = System.currentTimeMillis();

				g = new graph(s[i],display_level,graphs_directory);

				r = null; 
				x = null;
				p = g.find_P();

				g.BK_calls = 0;
				temp = g.BronKerbosch(r, p, x);
				elapsedTimeMillis = System.currentTimeMillis()-start;

				System.out.println("BronKerbosch took: "+elapsedTimeMillis+" milliseconds and found max clique of size: "+temp.length+" and nodes:");
				System.out.println(g.array2string(temp));
			}
			else if (other_algorithm != ""){

				MaxClique other_clique = new MaxClique();

				System.out.println("Running "+other_algorithm+" on graph ("+(i+1)+"/"+(s.length)+"): "+s[i]+" Number of nodes: "+g.nodes);

				other_clique.readDIMACS(graphs_directory + s[i]);
				MC mc = null;

				if (other_algorithm.equalsIgnoreCase("MC"))         mc = new MC(other_clique.n,other_clique.A,other_clique.degree);
				else if (other_algorithm.equalsIgnoreCase("MC0"))   mc = new MC0(other_clique.n,other_clique.A,other_clique.degree);
				else if (other_algorithm.equalsIgnoreCase("MCQ1"))  mc = new MCQ(other_clique.n,other_clique.A,other_clique.degree,1);
				else if (other_algorithm.equalsIgnoreCase("MCQ2"))  mc = new MCQ(other_clique.n,other_clique.A,other_clique.degree,2);
				else if (other_algorithm.equalsIgnoreCase("MCQ3"))  mc = new MCQ(other_clique.n,other_clique.A,other_clique.degree,3);
				else if (other_algorithm.equalsIgnoreCase("MCSa1")) mc = new MCSa(other_clique.n,other_clique.A,other_clique.degree,1);
				else if (other_algorithm.equalsIgnoreCase("MCSa2")) mc = new MCSa(other_clique.n,other_clique.A,other_clique.degree,2);
				else if (other_algorithm.equalsIgnoreCase("MCSa3")) mc = new MCSa(other_clique.n,other_clique.A,other_clique.degree,3);
				else if (other_algorithm.equalsIgnoreCase("MCSb1")) mc = new MCSb(other_clique.n,other_clique.A,other_clique.degree,1);
				else if (other_algorithm.equalsIgnoreCase("MCSb2")) mc = new MCSb(other_clique.n,other_clique.A,other_clique.degree,2);
				else if (other_algorithm.equalsIgnoreCase("MCSb3")) mc = new MCSb(other_clique.n,other_clique.A,other_clique.degree,3);
				else if (other_algorithm.equalsIgnoreCase("BBMC1")) mc = new BBMC(other_clique.n,other_clique.A,other_clique.degree,1);
				else if (other_algorithm.equalsIgnoreCase("BBMC2")) mc = new BBMC(other_clique.n,other_clique.A,other_clique.degree,2);
				else if (other_algorithm.equalsIgnoreCase("BBMC3")) mc = new BBMC(other_clique.n,other_clique.A,other_clique.degree,3);
				else{
					System.out.println("Unknown algorithm used: "+other_algorithm);
					System.exit(0);
				}

				System.gc();
				//if (args.length > 2) mc.timeLimit = 1000 * (long)Integer.parseInt(args[2]);
				elapsedTimeMillis = System.currentTimeMillis();
				mc.search();
				elapsedTimeMillis = System.currentTimeMillis() - elapsedTimeMillis;

				System.out.println(other_algorithm + " took: " + elapsedTimeMillis + " milliseconds and found max clique of size: "+mc.maxSize+" and nodes:");
				System.out.println(mc.Arraylist_solution.toString());

			}    



			else {


				g = new graph(s[i],display_level,graphs_directory);

				g.timings[0] = 0;
				g.timings[1] = 0;
				g.timings[2] = 0;

				g.include_graphs = include_graphs;
				g.graphs_directory = graphs_directory;
				g.exclude_graphs = exclude_graphs;

				g.level_0_display = true;
				g.display_level = display_level;
				g.start_showing_crap = false;
				g.sort_smallest_first = false;
				g.sort = true;
				g.degressive_display = false;
				g.priority_threading = false;
				g.disp_found_max = disp_found_max;
				g.num_threads = num_threads; // set to zero to disable multithreading
				g.mid_num_threads = mid_num_threads;
				g.min_new_bthread_size = 10;//(int)(g.nodes*0.5); //number of nodes needed to make it worth calling a new thread
				g.min_new_midthread_size = 10;//(int)(g.nodes*0.04); //number of nodes needed to make it worth calling a new thread, should look only at ntc, not memory element because ntc dictates more closely how many times the loop will be run
				g.hotswap_trigger = -17;//115//116//1986;//12063; //-34530;//35105
				g.lowest_backtrack = 0;//set to nodes if you want to enable the depth first option, not as efficient but easier to track progress

				g.B_calls = 0;
				g.B_calls_background = 0;

				System.out.println();
				System.out.println();
				System.out.println("Running Bochert on graph ("+(i+1)+"/"+(s.length)+"): "+s[i]+" Number of nodes: "+g.nodes);

				start = System.currentTimeMillis();

				temp = g.pre_Newer_Bochert(false);

				elapsedTimeMillis = System.currentTimeMillis()-start;

				if (display_level < -1){
					System.out.println();
					System.out.println("Bochert took: "+elapsedTimeMillis+" milliseconds and found max clique of size: "+temp.length+" and nodes:");
					System.out.println(g.array2string(temp));
				}
				else {
					System.out.println();
					System.out.println("max clique from Bochert is: ");
					System.out.println(g.array2string(temp));
					System.out.println("total calls to Bochert: "+g.B_calls);
					System.out.println("background calls to Bochert were: "+g.B_calls_background);
					System.out.println("background to foreground were: "+((double)g.B_calls_background)/((double)g.B_calls));

					System.out.println("__ it took:"+elapsedTimeMillis+" milliseconds");
					System.out.println("is clique?: "+g.is_star(temp, true)+" and length is: "+temp.length);
				}

				if (!g.is_star(temp, true)){
					System.out.println("is clique?: "+g.is_star(temp, true)+" and length is: "+temp.length);
					System.out.print("POOP! Something went wrong, returned a not clique");
				}

				System.out.println();

			}




			/*		s[0] = "brock200_1.clq";
		s[1] = "brock200_2.clq";
		s[2] = "brock200_3.clq";
		s[3] = "brock200_4.clq";
		s[4] = "c-fat200-1.clq";
		s[5] = "c-fat200-2.clq";
		s[6] = "c-fat200-5.clq";
		s[7] = "c-fat500-1.clq";
		s[8] = "c-fat500-10.clq";
		s[9] = "c-fat500-2.clq";
		s[10] = "c-fat500-5.clq";
		s[11] = "hamming6-2.clq";
		s[12] = "hamming6-4.clq";
		s[13] = "hamming8-4.clq";
		s[14] = "johnson16-2-4.clq";
		s[15] = "johnson8-2-4.clq";
		s[16] = "johnson8-4-4.clq";
		s[17] = "keller4.clq";
		s[18] = "keller5.clq";
		s[19] = "keller6.clq";
		s[20] = "p_hat300-1.clq";
		s[21] = "p_hat300-2.clq";
		s[22] = "p_hat300-3.clq";
		s[23] = "p_hat500-1.clq";
		s[24] = "p_hat500-2.clq";
		s[25] = "p_hat700-1.clq";
		s[26] = "p_hat1000-1.clq";
		s[27] = "p_hat1500-1.clq";
		s[28] = "san400_0.5_1.clq";
		s[29] = "sanr200_0.7.clq";
		s[30] = "sanr400_0.5.clq";
		s[31] = "MANN_a27.clq";
		s[32] = "MANN_a45.clq";
		s[33] = "MANN_a81.clq";
		s[34] = "MANN_a9.clq";
		if (((i != 32) && (i != 33) && (i != 18)) && (i != 19)){
			 */




			/////////////////////////////////////////////
			// Create random graphs with Leemon's 3SAT generator
			/////////////////////////////////////////////
			if (false){	
				System.out.println("Bochert forced seed 0");
				System.out.println("nv:c:nodes:clique:runs:ms:tf");

				int v=51; //arbitrarily pick large number of variables for 3sat

				g.init_AES_rand((short)1,(short)(4.27*v));
				System.out.print(v+":"+(int)(4.27*v)+":"+(int)(4.27*v*3)+":");
				g.start_random_with_seed(0);

				g = new graph(g.createLeemon3SAT(v,(int)(4.27*v),true));

				g.display_level = -1;

				g.timings[0] = 0;
				g.timings[1] = 0;
				g.timings[2] = 0;

				g.start_showing_crap = false;
				g.sort_smallest_first = false;
				g.sort = true;
				g.degressive_display = false;
				g.priority_threading = false;
				g.disp_found_max = true;
				g.num_threads = 0; // set to zero to disable multithreading
				g.mid_num_threads = 8;
				g.min_new_bthread_size = 0;//(int)(g.nodes*0.5); //number of nodes needed to make it worth calling a new thread
				g.min_new_midthread_size = 0;//(int)(g.nodes*0.04); //number of nodes needed to make it worth calling a new thread, should look only at ntc, not memory element because ntc dictates more closely how many times the loop will be run
				g.hotswap_trigger = -17;//115//116//1986;//12063; //-34530;//35105
				g.lowest_backtrack = 0;//set to nodes if you want to enable the depth first option, not as efficient but easier to track progress

				start = System.currentTimeMillis();
				g.B_calls = 0;
				g.B_calls_background = 0;
				//				temp = g.pre_New_Bochert(false,g.nodes);

				r = null; 
				x = null;
				p = g.find_P();


				temp = g.pre_Newer_Bochert(false);
				//temp = g.BronKerbosch(r, p, x);	
				//g.invert_graph();
				//temp = g.mis(new node3(g.all_neighbors(-1),g.nodes), 0, false).to_int();


				elapsedTimeMillis = System.currentTimeMillis()-start;
				System.out.println(temp.length+":"+g.middle_loop_run+":"+elapsedTimeMillis+":"+g.is_star(temp, false));

				//				System.out.println();
				//				System.out.println("max clique from un-optimized Bochert is: ");
				//				System.out.println(g.array2string(temp));
				//				System.out.println("total calls to Bochert: "+g.B_calls+" and of those, total calls to TOP while were: "+g.B_calls_TOP);


				//temp = g.pre_Newer_Bochert(false);
				//								g.invert_graph();
				//								temp = g.mis(new node3(g.all_neighbors(-1),g.nodes), 0, false).to_int();
				//System.out.println("\nis star?: "+g.is_star(temp, false)+" and length is: "+temp.length+"\n set: "+g.array2string(temp)+"\n");

				//				bthread emptybthread = new bthread();
				//				System.out.println("calls to bthread: "+emptybthread.total_calls_to_bthread+" and interior Bochert calls were: "+emptybthread.B_calls);

				//				System.out.println("calls to midthread: "+emptybthread.total_calls_to_midthread+" and interior Bochert calls were: now indistinguishable");

				//				System.out.println("__ it took:"+elapsedTimeMillis+" miliseconds");
				//				System.out.println("Time analysis");// 0: "+g.time_analysis[0]+" 1: "+g.time_analysis[1]+" 2: "+g.time_analysis[2]+" 3: "+g.time_analysis[3]+" 4: "+g.time_analysis[4]+" 5: "+g.time_analysis[5]+" 6: "+g.time_analysis[6]);
				//				for(int a = 0; a<g.time_analysis.length; a++){
				//					System.out.print(" "+a+": "+g.time_analysis[a]);
				//				}
				//				System.out.println();

				//				System.out.println("is star?: "+g.is_star(temp, true)+" and length is: "+temp.length);

				//				System.out.println("waiting time for semasema: "+g.semasema.total_time_waiting+" mid_semasema: "+g.mid_semasema.total_time_waiting+" semaMax: "+g.semaMax.total_time_waiting+" semasematext: "+g.semasematext.total_time_waiting);
				//				System.out.println("middle loop run: "+g.middle_loop_run);



				//				System.out.println();





			}
		}
	}

}
