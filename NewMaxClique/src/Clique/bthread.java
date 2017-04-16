package Clique;

import java.io.*;
import java.util.*;
import java.text.*;




public class bthread implements Runnable  {

	static private int[][] graph; // the adjacency matrix
	static private node3[] graph3; // the adjacency matrix
	private int[][] old_graph; //when changing the graph around, can keep the old one to ensure that the returned set is indeed a clique
	static private int nodes; // total number of nodes
	private int BK_iteration_deep = 0; // iterations deep into BronKerbosch
	private long BK_calls = 0; // calls to BronKerbosch
	private int B_iteration_deep = -1; //iterations deep into Bochert
	public long B_calls = 0; // calls to Bochert
	public long B_calls_TOP = 0; // calls to Bochert
	private boolean verboseBK = false; // Verbosity of output... verbosity should be a word... it sounds cool
	private int[] node_edge_count; // number of edges each node has connected to it
	private int[] nodes_ordered_increasing; // array of nodes with decreasing edge count - first node has highest num edges
	private int[] index_ordered_nodes; //array of nodes, where int[0] represents the index of the first node into nodes_ordered_decreasing, and int[1] represents the index of the second, etc 
	boolean start_showing_crap = false;
	static private node3 empty_node;
	private boolean sort_smallest_first = true;
	static private boolean degressive_display = false;
	private long hotswap_trigger = -1;
	private boolean level_0_display = false;
	static private node3[] reach_back;
	private node3 find;
//	private boolean display_global = false;
	static private long[] reach_back_B_calls;
	static private long[] reach_back_B_calls_TOP;
	private int whoami;
	private node3[] previous_nodes;
	private int previously_known_max;
	private int who_ran_me = 100;
	private int pre_depth_memory;
	private Date dNow = new Date( );
	private SimpleDateFormat ft = new SimpleDateFormat ("HH:mm:ss");


	static boolean fing_semaphore = false;
	static int[] thread_count = new int[1];
	static int thread_pool = 1;
	static int max_thread_pool = 1;
	static int[] status;//0 means free, 1 means running
	static semaphore semasema = null;
	static semaphore semaMax = null;
	semaphore stillrunning = null;
	int display_level = 0;
	static int[] best_star = new int[1];
	static boolean priority_threading = false;

	static int[] mid_thread_count = new int[1];
	static int mid_thread_pool = 1;
	static int mid_max_thread_pool = 1;
	static int[] mid_status;//0 means free, 1 means running
	static semaphore mid_semasema = null;

	static node3[] mid_reach_back = null;
	private node3[] mid_previous_nodes;

	static private int lowest_backtrack = 0;

	static semaphore semasematext = null;
	static int min_new_bthread_size = 10;
	static int min_new_midthread_size = 10;
	static int total_calls_to_bthread = 0;
    long[] time_analysis = new long[10];
    static long[] reach_back_time_analysis;

    static int[] exit_loop;
    int middle_loop_run = 0;
    static int[] reach_back_middle_loop_run;
    int waittime = 10000;

	
/*	private int[][] graph; // the adjacency matrix
	private node3[] graph3; // the adjacency matrix
	private int[][] old_graph; //when changing the graph around, can keep the old one to ensure that the returned set is indeed a clique
	private int nodes; // total number of nodes
	private int BK_iteration_deep = 0; // iterations deep into BronKerbosch
	private long BK_calls = 0; // calls to BronKerbosch
	private int B_iteration_deep = -1; //iterations deep into Bochert
	public long B_calls = 0; // calls to Bochert
	public long B_calls_TOP = 0; // calls to Bochert
	private boolean verboseBK = false; // Verbosity of output... verbosity should be a word... it sounds cool
	private int[] node_edge_count; // number of edges each node has connected to it
	private int[] nodes_ordered_increasing; // array of nodes with decreasing edge count - first node has highest num edges
	private int[] index_ordered_nodes; //array of nodes, where int[0] represents the index of the first node into nodes_ordered_decreasing, and int[1] represents the index of the second, etc 
	boolean start_showing_crap = false;
	private node3 empty_node;
	private boolean sort_smallest_first = true;
	private boolean degressive_display = false;
	private long hotswap_trigger = -1;
	private boolean level_0_display = false;
	private node3[] reach_back;
	private node3 find;
	private boolean display_global = false;
	private long[] reach_back_B_calls;
	private long[] reach_back_B_calls_TOP;
	public int whoami;
	private node3[] previous_nodes;
	private int previously_known_max;
	private int who_ran_me = 100;
	private int pre_depth_memory;
	private Date dNow = new Date( );
	private SimpleDateFormat ft = new SimpleDateFormat ("HH:mm:ss");


	static boolean fing_semaphore = false;
	static int[] thread_count = new int[1];
	static int thread_pool = 1;
	static int max_thread_pool = 1;
	static int[] status;//0 means free, 1 means running
	static semaphore semasema = null;
	static semaphore semaMax = null;
	semaphore stillrunning = null;
	int display_level = 0;
	static int[] best_star = new int[1];
	static boolean priority_threading = false;

	static int[] mid_thread_count = new int[1];
	static int mid_thread_pool = 1;
	static int mid_max_thread_pool = 1;
	static int[] mid_status;//0 means free, 1 means running
	static semaphore mid_semasema = null;

	static node3[] mid_reach_back = null;
	private node3[] mid_previous_nodes;

	private int lowest_backtrack = 0;

	static semaphore semasematext = null;
	static int min_new_bthread_size = 10;
	static int min_new_midthread_size = 10;
	static int total_calls_to_bthread = 0;
    long[] time_analysis = new long[10];
    static long[] reach_back_time_analysis;

    static int[] exit_loop;
    int middle_loop_run = 0;
    int[] reach_back_middle_loop_run;
    int waittime = 0;	
  */  
	static int total_calls_to_midthread = 0;
	private node3 find_not_used;
	node3 ZTOP_nodes_to_consider; 
	node3 Znodes_to_consider; 
	node3 Zall_nodes_in_set_deleted_used; 
	boolean Zdisplay_internal; 
//	int Zdepth_charge; 
	int Zdeepness; 
	node3 Zchecked_set; 
	node3 Zall_nodes_in_set_whole; 
	node3 Zalpha3; 
	node3[] ZDCC; 
	node3 Zmemory_element; 
	node3 ZTOP_checked_set;	
	int Zcheck_set;
	int Zwhere_from;
	public boolean mid_entry = false;

	int I_declared_max_size = 0;
	boolean show_me_intermitent_maxes = false;
	

	bthread(){

	}


	bthread(semaphore semasematext1, node3[] reach_back1, int thread_pool1, node3[] graph31, int[][] graph1, int nodes1, long[] reach_back_B_calls1, node3 empty_node1, boolean degressive_display1, int[] status1, node3[] previous_nodes1, semaphore semasema1, semaphore semaMax1, boolean priority_threading1, int[] best_star1, int[] thread_count1, semaphore mid_semasema1, int[] mid_thread_count1, int mid_thread_pool1, int[] mid_status1, node3[] mid_reach_back1, int min_new_bthread_size1, int min_new_midthread_size1, int lowest_backtrack1, long[] reach_back_B_calls_TOP1, int max_thread_pool1, int mid_max_thread_pool1, long[] reach_back_time_analysis1, int[] exit_loop1,int[] reach_back_middle_loop_run1){
		// all of the meta data to run thread
		reach_back_middle_loop_run = reach_back_middle_loop_run1;
		reach_back_time_analysis = reach_back_time_analysis1;
		semasematext = semasematext1;
		reach_back = reach_back1;
		graph3 = graph31;
		nodes = nodes1;
		lowest_backtrack = lowest_backtrack1;
		graph = graph1;
		reach_back_B_calls = reach_back_B_calls1;
		reach_back_B_calls_TOP = reach_back_B_calls_TOP1;
		empty_node = empty_node1;
		degressive_display = degressive_display1;
		thread_pool = thread_pool1;
		max_thread_pool = max_thread_pool1; 
		status = status1;
		semasema = semasema1;
		semaMax = semaMax1;
		priority_threading = priority_threading1;
		best_star = best_star1;
		thread_count = thread_count1;
		mid_reach_back = mid_reach_back1;
		min_new_bthread_size = min_new_bthread_size1;
		min_new_midthread_size = min_new_midthread_size1;
		mid_thread_count = mid_thread_count1;
		mid_thread_pool = mid_thread_pool1;
		mid_max_thread_pool = mid_max_thread_pool1;
		mid_status = mid_status1;
		mid_semasema = mid_semasema1;
		exit_loop = exit_loop1;

	}
	
	bthread(boolean display1, int whoami1, semaphore stillrunning1, node3[] previous_nodes1, node3[] mid_previous_nodes1, int display_level1, int B_iteration_deep1, int pre_depth_memory1, node3 find1, int who_ran_me1, int previously_known_max1){
	//newer Bochert
		total_calls_to_bthread++;
		
		level_0_display = display1; 
		whoami = whoami1;
		stillrunning = stillrunning1;
		previous_nodes = previous_nodes1;
		mid_previous_nodes = mid_previous_nodes1;
		display_level = display_level1;
		B_iteration_deep = B_iteration_deep1;
		pre_depth_memory = pre_depth_memory1;

		
		find = find1;
		previously_known_max = previously_known_max1;
		who_ran_me = who_ran_me1;
		
		
	}
	
	
	bthread(boolean display1, int whoami1, semaphore stillrunning1, node3[] previous_nodes1, node3[] mid_previous_nodes1, int display_level1, int B_iteration_deep1, int pre_depth_memory1, node3 TOP_nodes_to_consider, node3 nodes_to_consider, node3 all_nodes_in_set_deleted_used, boolean display_internal, int depth_charge, int deepness, node3 checked_set, node3 all_nodes_in_set_whole, node3 alpha3, node3[] DCC, node3 memory_element, node3 TOP_checked_set, int check_set, int where_from1){ 			
	//mid section
	
		mid_entry = true;
		total_calls_to_midthread++;

		level_0_display = display1; 
		whoami = whoami1;
		stillrunning = stillrunning1;
		previous_nodes = previous_nodes1;
		mid_previous_nodes = mid_previous_nodes1;
		display_level = display_level1;
		B_iteration_deep = B_iteration_deep1;

		
		ZTOP_nodes_to_consider = TOP_nodes_to_consider;
		Znodes_to_consider = nodes_to_consider;
		Zall_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used;  
		Zdisplay_internal = display_internal ;
//		Zdepth_charge = depth_charge ;
		Zdeepness = deepness ;
		Zchecked_set = checked_set ; 
		Zall_nodes_in_set_whole = all_nodes_in_set_whole ; 
		Zalpha3 = alpha3 ;
		ZDCC = DCC ;
		Zmemory_element =  memory_element ;
		ZTOP_checked_set = TOP_checked_set ;
		Zcheck_set = check_set ;
		Zwhere_from = where_from1;
		
	}
	
	
	bthread(semaphore semasematext1, node3[] reach_back1, int thread_pool1, node3[] graph31, int[][] graph1, node3 find1, boolean display1, int nodes1, long[] reach_back_B_calls1, int display_level1, node3 empty_node1, boolean degressive_display1, int whoami1, int[] status1, node3[] previous_nodes1, int previously_known_max1, semaphore semasema1, semaphore stillrunning1, int B_iteration_deep1,int who_ran_me1, semaphore semaMax1, int pre_depth_memory1, boolean priority_threading1, int[] best_star1, int[] thread_count1, semaphore mid_semasema1, int[] mid_thread_count1, int mid_thread_pool1, int[] mid_status1, node3[] mid_reach_back1, 	node3[] mid_previous_nodes1, int min_new_bthread_size1, int min_new_midthread_size1, int lowest_backtrack1, long[] reach_back_B_calls_TOP1, int max_thread_pool1, int mid_max_thread_pool1, long[] reach_back_time_analysis1, int[] exit_loop1,int[] reach_back_middle_loop_run1, boolean show_me_intermitent_maxes1){

		total_calls_to_bthread++;

		show_me_intermitent_maxes = show_me_intermitent_maxes1;
		reach_back_middle_loop_run = reach_back_middle_loop_run1;
		reach_back_time_analysis = reach_back_time_analysis1;
		semasematext = semasematext1;
		reach_back = reach_back1;
		graph3 = graph31;
		find = find1;
		level_0_display = display1; 
		nodes = nodes1;
		lowest_backtrack = lowest_backtrack1;
		graph = graph1;
		reach_back_B_calls = reach_back_B_calls1;
		reach_back_B_calls_TOP = reach_back_B_calls_TOP1;
		if(display_level1>0) display_level = display_level1;
		empty_node = empty_node1;
		degressive_display = degressive_display1;
		whoami = whoami1;
		if(thread_pool1 > 0){ 
			thread_pool = thread_pool1;
			max_thread_pool = max_thread_pool1; 
		}
		if(status1 != null) status = status1;
		previous_nodes = previous_nodes1;
		previously_known_max = previously_known_max1;
		if(semasema1 != null) semasema = semasema1;
		stillrunning = stillrunning1;
		B_iteration_deep = B_iteration_deep1;
		who_ran_me = who_ran_me1;
		if(semaMax1 != null) semaMax = semaMax1;
		pre_depth_memory = pre_depth_memory1;
		priority_threading = priority_threading1;
		if(best_star1 != null)best_star = best_star1;
		if(thread_count1 != null) thread_count = thread_count1;
		if(mid_reach_back1 != null) mid_reach_back = mid_reach_back1;
		min_new_bthread_size = min_new_bthread_size1;
		min_new_midthread_size = min_new_midthread_size1;

		if(mid_semasema1 != null){
			mid_thread_count = mid_thread_count1;
			mid_thread_pool = mid_thread_pool1;
			mid_max_thread_pool = mid_max_thread_pool1;
			mid_status = mid_status1;
			mid_semasema = mid_semasema1;
		}
		mid_previous_nodes = mid_previous_nodes1;

		exit_loop = exit_loop1;
	}

	
//	task = new bthread(semasematext, reach_back, thread_pool, graph3, graph, DCC[0].copy_by_erasing(), display_next/*display_global*/, nodes, reach_back_B_calls ,  (display_next?display_level:-1)/*display_level*/, empty_node, degressive_display,temp,status,previous_nodes,((best_star[0]-depth_charge)==0?0:(best_star[0]-depth_charge)-1),semasema,stillrunning1,B_iteration_deep,(where_from<=1?1:3),semaMax,depth_charge+1,priority_threading,best_star, thread_count, mid_semasema, mid_thread_count, mid_thread_pool, mid_status,temp_nodes_to_consider.memory_previous, temp_nodes_to_consider, temp_all_nodes_in_set_deleted_used,display_next /*display_internal*/, depth_charge, deepness, temp_checked_set, temp_all_nodes_in_set_whole, temp_alpha3, DCC, temp_memory_element, temp_checked_set, check_set,mid_reach_back,mid_previous_nodes, where_from,min_new_bthread_size,min_new_midthread_size,lowest_backtrack,reach_back_B_calls_TOP,max_thread_pool,mid_max_thread_pool,reach_back_time_analysis,exit_loop,reach_back_middle_loop_run);
	bthread(semaphore semasematext1, node3[] reach_back1, int thread_pool1, node3[] graph31, int[][] graph1, node3 find1, boolean display1, int nodes1, long[] reach_back_B_calls1, int display_level1, node3 empty_node1, boolean degressive_display1, int whoami1, int[] status1, node3[] previous_nodes1, int previously_known_max1, semaphore semasema1, semaphore stillrunning1, int B_iteration_deep1,int who_ran_me1, semaphore semaMax1, int pre_depth_memory1, boolean priority_threading1, int[] best_star1, int[] thread_count1, semaphore mid_semasema1, int[] mid_thread_count1, int mid_thread_pool1, int[] mid_status1,node3 TOP_nodes_to_consider, node3 nodes_to_consider, node3 all_nodes_in_set_deleted_used, boolean display_internal, int deepness, node3 checked_set, node3 all_nodes_in_set_whole, node3 alpha3, node3[] DCC, node3 memory_element, node3 TOP_checked_set,	int check_set, node3[] mid_reach_back1,node3[] mid_previous_nodes1, int where_from1, int min_new_bthread_size1, int min_new_midthread_size1, int lowest_backtrack1, long[] reach_back_B_calls_TOP1,int max_thread_pool1, int mid_max_thread_pool1,long[] reach_back_time_analysis1,int[] exit_loop1,int[] reach_back_middle_loop_run1, boolean show_me_intermitent_maxes1){
//Midthread  
		mid_entry = true;
			total_calls_to_midthread++;
			
			semasematext = semasematext1;
			show_me_intermitent_maxes = show_me_intermitent_maxes1;

			reach_back_middle_loop_run = reach_back_middle_loop_run1;
			reach_back_time_analysis = reach_back_time_analysis1;
			reach_back = reach_back1;
			graph3 = graph31;
			find_not_used = find1;
			level_0_display = display1; 
			nodes = nodes1;
			lowest_backtrack = lowest_backtrack1; //this doesn't matter here, it only matters for calling bthread, this thread (mid thread) will never have a use for this because, in theory, it's already low enough level to call more threads
			graph = graph1;
			reach_back_B_calls = reach_back_B_calls1;
			reach_back_B_calls_TOP = reach_back_B_calls_TOP1;
			if(display_level1>0) display_level = display_level1;
			empty_node = empty_node1;
			degressive_display = degressive_display1;
			whoami = whoami1;
//			if(thread_pool1 > 0){ 
				thread_pool = thread_pool1;
				max_thread_pool = max_thread_pool1; 
//			}
			if(status1 != null) status = status1;
			previous_nodes = previous_nodes1;
			previously_known_max = previously_known_max1;
			if(semasema1 != null) semasema = semasema1;
			stillrunning = stillrunning1;
			B_iteration_deep = B_iteration_deep1;
			who_ran_me = who_ran_me1;
			if(semaMax1 != null) semaMax = semaMax1;
			pre_depth_memory = pre_depth_memory1;
			priority_threading = priority_threading1;
			if(best_star1 != null)best_star = best_star1;
			if(thread_count1 != null) thread_count = thread_count1;
			if(mid_reach_back1 != null) mid_reach_back = mid_reach_back1;
			mid_previous_nodes = mid_previous_nodes1;
			min_new_bthread_size = min_new_bthread_size1;
			min_new_midthread_size = min_new_midthread_size1;
			
//			if(mid_semasema1 != null){
				mid_thread_count = mid_thread_count1;
				mid_thread_pool = mid_thread_pool1;
				mid_max_thread_pool = mid_max_thread_pool1; 
				mid_status = mid_status1;
				mid_semasema = mid_semasema1;
//			}

			
			ZTOP_nodes_to_consider = TOP_nodes_to_consider;
			Znodes_to_consider = nodes_to_consider;
			Zall_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used;  
			Zdisplay_internal = display_internal ;
//			Zdepth_charge = depth_charge ;
			Zdeepness = deepness ;
			Zchecked_set = checked_set ; 
			Zall_nodes_in_set_whole = all_nodes_in_set_whole ; 
			Zalpha3 = alpha3 ;
			ZDCC = DCC ;
			Zmemory_element =  memory_element ;
			ZTOP_checked_set = TOP_checked_set ;
			Zcheck_set = check_set ;
			Zwhere_from = where_from1;
			exit_loop = exit_loop1;
			
		}


	@Override
	public void run() {

		//Thread.currentThread().setPriority(whoami);
		
		if(!mid_entry)
			reach_back[whoami].zero();
		else
			mid_reach_back[whoami].zero();



		if(level_0_display){
			try {semasematext.take();} catch(InterruptedException e) {} 
			this.insert_spaces_for_iteration("B");
			System.out.println("++++++++++display: "+level_0_display+" entering run... whoami: "+whoami+" thread_count: "+thread_count[0]+" priority: "+Thread.currentThread().getPriority()+" prev_nodes: "+(mid_entry?mid_previous_nodes[whoami].get_length():previous_nodes[whoami].get_length()));
			semasematext.release();
		}

		
		node3 result = null; 
		
		if(!mid_entry)
			result = Newer_Bochert(find,previously_known_max,nodes,level_0_display,who_ran_me,pre_depth_memory);
		else
			result = mid_section(ZTOP_nodes_to_consider, Znodes_to_consider, Zall_nodes_in_set_deleted_used, Zdisplay_internal, pre_depth_memory, Zdeepness, Zchecked_set, Zall_nodes_in_set_whole, Zalpha3, ZDCC, Zmemory_element, ZTOP_checked_set, Zcheck_set, Zwhere_from, System.currentTimeMillis(),new ArrayList<Thread>(), new ArrayList<semaphore>(), new ArrayList<Thread>(), new ArrayList<semaphore>(),mid_entry);


/*		if((I_declared_max_size != 0) ){
			if((result.get_length()+pre_depth_memory) == I_declared_max_size){
				this.insert_spaces_for_iteration("B");
				System.out.println("Returning with declared max of "+I_declared_max_size+" and depth charge of: "+pre_depth_memory+" best_star[0]: "+best_star[0]);
			}
			else{
				this.insert_spaces_for_iteration("B");
				System.out.println("FAIL! Returning with declared max of "+I_declared_max_size+" but actual max is: "+result.get_length()+" and depth charge of: "+pre_depth_memory+" best_star[0]: "+best_star[0]);
//				System.exit(0);
			}
		}
		*/
			

			
			try{semaMax.take();} catch(InterruptedException e){}

		if(!mid_entry){
			reach_back[whoami] = result;
			reach_back_B_calls[0] += B_calls;
			reach_back_B_calls_TOP[0] += B_calls_TOP;
			for(int i = 0; i<reach_back_time_analysis.length; i++)
				reach_back_time_analysis[i] = reach_back_time_analysis[i]+time_analysis[i];
			reach_back_middle_loop_run[0] = reach_back_middle_loop_run[0] + middle_loop_run;
		}
		else{
			mid_reach_back[whoami] = result;
			reach_back_B_calls[0] += B_calls;
			reach_back_B_calls_TOP[0] += B_calls_TOP;
			for(int i = 0; i<reach_back_time_analysis.length; i++)
				reach_back_time_analysis[i] = reach_back_time_analysis[i]+time_analysis[i];
			reach_back_middle_loop_run[0] = reach_back_middle_loop_run[0] + middle_loop_run;
		}

		semaMax.release();

		if(level_0_display)	{
			try {semasematext.take();} catch(InterruptedException e) {} 
			this.insert_spaces_for_iteration("B");
			System.out.println("----------exiting run... whoami: "+whoami+" thread_count: "+thread_count[0]+" prev_nodes: "+(mid_entry?mid_previous_nodes[whoami].get_length():previous_nodes[whoami].get_length()));
			semasematext.release();
		}


		stillrunning.release();

	}


	public int available_thread() throws InterruptedException{
		int new_thread = 0;

		semasema.take();

		if((thread_count[0]) < thread_pool){
			thread_count[0]++;

			for(int i = 0; i<thread_pool; i++){
				if(status[i] == 0){//it's free
					new_thread = i;
					status[i] = 1;
					i = thread_pool;
				}
			}

		}

		semasema.release();

		return new_thread;

	}


	public int available_mid_thread() throws InterruptedException{
		int new_thread = 0;

		mid_semasema.take();

		if((mid_thread_count[0]) < mid_thread_pool){
			mid_thread_count[0]++;

			for(int i = 0; i<mid_thread_pool; i++){
				if(mid_status[i] == 0){//it's free
					new_thread = i;
					mid_status[i] = 1;
					i = mid_thread_pool;
				}
			}

		}

		mid_semasema.release();

		return new_thread;

	}


	public void release_thread(int releasing) throws InterruptedException{

		semasema.take();

		status[releasing] = 0;
		thread_count[0]--;

		semasema.release();


	}

	public void release_mid_thread(int releasing) throws InterruptedException{

		mid_semasema.take();

		mid_status[releasing] = 0;
		mid_thread_count[0]--;

		mid_semasema.release();


	}


	public int update_max(int max_size, int depth_charge, node3 maxi) throws InterruptedException{

		
		semaMax.take();

		if(best_star[0] < max_size)
			best_star[0] = max_size;
		I_declared_max_size = max_size;
		
if(show_me_intermitent_maxes){
	try {semasematext.take();} catch(InterruptedException e) {} 
		this.insert_spaces_for_iteration("B");
		System.out.println("In Update Max - new global max: "+max_size+" name: "+Thread.currentThread().getName()+" id: "+Thread.currentThread().getId()+" depth_charge: "+depth_charge+" actual max size: "+maxi.get_length()+" actual max: "+(maxi.get_length()<300?maxi.print_list():"Over size 300, not bothering to print, too big"));
		semasematext.release();
}
		
		semaMax.release();

		return best_star[0];
	}


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









	private void Bochert_neighbor(node3 result, int n, node3 array){

		result.use_me_and(graph3[n-1], array);

		return;

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



	private void check_if_threads_are_done(boolean stop, List<Thread> thread_ownership, List<semaphore> stillrunninglist, boolean display_internal, int depth_charge, node3 max_star, node3[] previous_nodes, node3[] reach_back, boolean midthread, node3 delete_this_nodes_to_consider, node3 delete_this_all_nodes_in_set_whole){



		Runnable task = null;
		Thread worker = null;
		Thread thread_index = null; 
		int temp;
		node3 Pointer_ONLY;
		boolean added_new_thread = false;


		for (int i = 0; i< thread_ownership.size(); i++) {

			thread_index = thread_ownership.get(i); 

			if(display_internal){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("In check_if_threads_are_done, looking at index: "+i+" which is thread: "+thread_index.getName()+" thread_ownership.length: "+thread_ownership.size()+" and it's alive? "+thread_index.isAlive()+" stop: "+stop+" midthread: "+midthread);
				semasematext.release();
			}

			if(stop && thread_index.isAlive()){
				
				try{stillrunninglist.get(i).take();} catch(InterruptedException e) {}
			}

			if(display_internal){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("after stop code, looking at index: "+i+" thread_ownership.length: "+thread_ownership.size()+" and it's alive? "+thread_index.isAlive()+" stop: "+stop);
				semasematext.release();
			}



			if(!thread_index.isAlive() || stop){//Hokay, this is kinda silly... if stop is active, then it's taken the semaphore, so the thread is done, okay? It's done you stupid java compiler... anyway, occasionally, the dumb thread releases the semaphore but Java doesn't finish killing the thread... so if stop is true, and it's here (so it has the semaphore lock) then it's done, read the results

				temp = Integer.parseInt(thread_index.getName());		

				thread_ownership.remove(thread_index);
				stillrunninglist.remove(stillrunninglist.get(i));
				i--;//because there is one less now


				Pointer_ONLY = reach_back[temp];

				if(display_internal){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.print("!!! "+thread_index.getName()+" is no longer alive, and it returned: "+Pointer_ONLY.print_list()+" with prev_nodes of: "+previous_nodes[temp].print_list());
					System.out.println(" Did it return a star? "+this.is_star(Pointer_ONLY.to_int(), true));
					semasematext.release();
				}

				//combine them, has to contain some dumb duplicates... wait, does it? This is an extra safe-guard against double counting doublicates
				Pointer_ONLY.use_me_or(Pointer_ONLY, previous_nodes[temp]);


				if((Pointer_ONLY.get_length()+depth_charge)>=(best_star[0])){//Poop! Don't make this mistake again, it needs to be >= not > because at nodes go back, it needs to be able to acknowledge that the previous set found a node of this size, and take that, and pass it on up
					if(display_internal){
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("Found new max in check if threads done. Pointer_ONLY: "+Pointer_ONLY.get_length()+" depth_charge: "+depth_charge+" best_star[0]: "+best_star[0]);
						this.insert_spaces_for_iteration("B");
						if(delete_this_nodes_to_consider == null)
							System.out.println("In check if threads are done, thread: "+thread_index.getName()+" id: "+thread_index.getId()+" midthread: "+midthread+" found new max star!! just_a_pointer: "+Pointer_ONLY.print_list()+" previous_nodes: "+previous_nodes[temp].print_list()+" previous max_star.md: "+(best_star[0]-depth_charge));
						else
							System.out.println("In check if threads are done, thread: "+thread_index.getName()+" id: "+thread_index.getId()+" delete_this_nodes_to_consider: "+delete_this_nodes_to_consider.print_list()+" delete_this_all_nodes_in_set_whole: "+delete_this_all_nodes_in_set_whole.print_list()+" midthread: "+midthread+" found new max star!! just_a_pointer: "+Pointer_ONLY.print_list()+" previous_nodes: "+previous_nodes[temp].print_list()+" previous max_star.md: "+(best_star[0]-depth_charge));
						semasematext.release();
					}

					
					max_star.copy_array(Pointer_ONLY);

					max_star.use_me_or(max_star, previous_nodes[temp]);

					//max_star.meta_data = max_star.get_length();
					try{update_max(max_star.get_length()+depth_charge,depth_charge,max_star);} catch(InterruptedException e){}

					if(!this.is_star(max_star.to_int(), true)){	try {semasematext.take();} catch(InterruptedException e) {}	System.out.println("B:"+whoami+" failing thread was mid? "+midthread+" thread: "+temp+" reachback: "+reach_back[temp].print_list()+" prev_nodes: "+previous_nodes[temp].print_list()+" not star anymore :(");reach_back[-1]=null;}
				}
				else{
				}

				if(!midthread){
					try{release_thread(temp);} catch(InterruptedException e){}
				}
				else{
					try{release_mid_thread(temp);} catch(InterruptedException e){}
				}


				//status[temp] = 0;
				//thread_count--;


			}
		}

		
	}


	private node3 Newer_Bochert(node3 all_nodes, int current_max_not_used, int sought_max, boolean show, int where_from, int depth_charge){

		
		long start = System.currentTimeMillis();
	
		B_iteration_deep++;
		B_calls++;

//		if(depth_charge)
		
		if(B_iteration_deep == 0)
			show = level_0_display;

		if(B_calls == hotswap_trigger){
			hotswap();
		}

		boolean display = (((where_from < 4)&&(level_0_display == true)&&(B_iteration_deep < (display_level+1)))?true:false);
		//boolean display = ((((where_from <= 4)||!degressive_display)&&(level_0_display == true)&&(B_iteration_deep < (display_level+1)))?true:false);

		
		if(display){
			try {semasematext.take();} catch(InterruptedException e) {} 
			this.insert_spaces_for_iteration("B");
			System.out.println("STARTING NEWER_BOCHERT all_nodes: "+all_nodes.print_list()+" where_from: "+where_from+" depth_charge: "+depth_charge+" Prev_nodes.len: "+(mid_entry?mid_previous_nodes[whoami].get_length():previous_nodes[whoami].get_length()));
			semasematext.release();
		}



		if((all_nodes.get_length() == 0)||(all_nodes.get_length() == 1)){

			if(display){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("returning because all_nodes is zero or length 1");
				semasematext.release();
			}
			B_iteration_deep--;
			return all_nodes;

		}

		node3 result = new node3(nodes);		

		if(sought_max <= 1){
			if(display){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("returning because sought_max <= 1");
				semasematext.release();
			}
			result.add(all_nodes.get_index(0));
			B_iteration_deep--;
			return result;
		}

		if(all_nodes.get_length()+depth_charge <= (best_star[0])){//if it's equal to, you'll only get the same as the current max
			//this.insert_spaces_for_iteration("B");
			//System.out.println("returning because all_nodes < current max, where from: "+where_from);
			if(display){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("Returning because all_nodes.gl ("+all_nodes.get_length()+") <= (best_star[0]("+best_star[0]+")-depth_charge("+depth_charge+"))");
				semasematext.release();
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
		//max_star.meta_data = (best_star[0]-depth_charge);
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
		int priority;


		Runnable task = null;
		Thread worker = null;
		Thread thread_index = null; 
		List<Thread> thread_ownership = new ArrayList<Thread>();
		List<semaphore> stillrunninglist = new ArrayList<semaphore>();
		List<Thread> mid_thread_ownership = new ArrayList<Thread>();
		List<semaphore> mid_stillrunninglist = new ArrayList<semaphore>();
		semaphore stillrunning1 = null;



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
			if((where_from <= 1)&&(all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
				//			if(display){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println(" NO  WHILE thread:"+whoami+" thread_count: "+thread_count[0]+" ,  B_calls: "+B_calls+" toptop (which is: "+toptop+") connected to all other nodes (which are: "+result.print_list()+"), calling Bochert("+result.print_list()+" ,cm: "+((best_star[0]-depth_charge)==0?0:(best_star[0]-depth_charge)-1)+" ,sm: "+(sought_max==0?0:sought_max-1)+" , abc: null; (ntc node was connected to all nodes)");
				semasematext.release();
			}


			boolean display_next = false;
			result = Newer_Bochert(result,((best_star[0]-depth_charge)==0?0:(best_star[0]-depth_charge)-1),(sought_max==0?0:sought_max-1),display_next/*display*/,(where_from<=1?1:4),depth_charge+1);
			if(display_next)
				try {Thread.sleep(waittime);} catch(InterruptedException e) {} 
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
				try {semasematext.take();} catch(InterruptedException e) {} 		
				this.insert_spaces_for_iteration("B");
				System.out.println(" -- DCC["+i+"].md: "+DCC[i].meta_data+" and is: "+DCC[i].print_list());
				semasematext.release();
			}
		}



		time_analysis[1] = time_analysis[1] + (System.currentTimeMillis() - start);
		start = System.currentTimeMillis();


		///////////////////////////////////////////////////////////////////////////////////////////////////
		//  TOP WHILE
		///////////////////////////////////////////////////////////////////////////////////////////////////



		if((where_from <= 1)&&(all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){

			try {semasematext.take();} catch(InterruptedException e) {} 			
			this.insert_spaces_for_iteration("B");
			if(display)
				System.out.println(" TOP WHILE thread:"+whoami+" thread_count: "+thread_count[0]+" mid_thread_count: "+mid_thread_count[0]+" time: "+disp_time_old()+" B_calls: "+B_calls+" starting on, check set is "+(1+0)+" out of "+(DCC.length)+" which is node "+DCC[0].meta_data+" with DCC of: "+DCC[0].print_list()+" with no comp_set but current max of: "+(best_star[0]));
			else
				System.out.println(" TOP WHILE thread:"+whoami+" thread_count: "+thread_count[0]+" mid_thread_count: "+mid_thread_count[0]+" time: "+disp_time_old()+" B_calls: "+B_calls+" starting on, check set is "+(1+0)+" out of "+(DCC.length)+" which is node "+DCC[0].meta_data+" with DCC size of: "+DCC[0].get_length()+" with no comp_set but current max of: "+(best_star[0]));
			semasematext.release();
		}

		if((best_star[0]-depth_charge) <= DCC[0].get_length()){//or equal because if DCC[0].meta_data + DCC[0].get_length() are the nodes, which means one more than DCC[0].get_length()


			temp = 0;
			//System.out.println("#0 outside lowest BT call");
			if(lowest_backtrack <= B_iteration_deep){
				try {
					if((DCC[0].get_length() >= min_new_bthread_size) && (thread_count[0] < thread_pool))
						temp = available_thread();
					else{
						temp = 0;
					}
					//System.out.println("#0 inside lowest BT call, temp: "+temp+" thread_count[0]: "+thread_count[0]+" thread_pool: "+thread_pool+" and min_new_thread_size: "+min_new_thread_size+" DCC[0].get_length(): "+DCC[0].get_length());
				} catch(InterruptedException e) {
				} 
			}

			//			if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
			boolean display_next = (display&&(DCC[0].meta_data == 0));
			if(display){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("temp = "+temp+" display_next: "+display_next);
				semasematext.release();
			}
			//			}

			if(temp == 0){//no new threads

				B_calls_TOP++;
				temp_element = Newer_Bochert(DCC[0].copy_by_erasing(), ((best_star[0]-depth_charge)==0?0:(best_star[0]-depth_charge)-1), nodes, display_next/*display*/, (where_from<=1?1:3), depth_charge+1);

				if((temp_element.get_length()+1)>=(best_star[0]-depth_charge)){
					max_star.copy_array(temp_element);
					max_star.add(toptop);

					if(display){
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("TOP While found new max star!! te.gl: "+temp_element.get_length()+" deepness: "+deepness+" max_star.md: "+(best_star[0]-depth_charge));
						semasematext.release();
					}
					
					if(!this.is_star(max_star.to_int(), true)){System.out.println("not star anymore :(");DCC[-1]=null;}
					try{update_max(max_star.get_length()+depth_charge,depth_charge,max_star);} catch(InterruptedException e){}
					//max_star.meta_data = max_star.get_length();


				}
				else{
				}


			}
			else{


				//				task = new bthread(reach_back, thread_pool, graph3, graph, memory_element.copy_by_erasing(), display, nodes, reach_back_B_calls, display_level, empty_node, degressive_display,temp,status,previous_nodes);
				stillrunning1 = new semaphore();
				try{stillrunning1.take();} catch(InterruptedException e){}

				task = new bthread(semasematext, reach_back, -1, graph3, graph, DCC[0].copy_by_erasing(), display_next /*display_global*/, nodes, reach_back_B_calls, (display_next?display_level:-1)/*-1*/, empty_node, degressive_display,temp,null,previous_nodes,((best_star[0]-depth_charge)==0?0:(best_star[0]-depth_charge)-1),semasema,stillrunning1,B_iteration_deep,(where_from<=1?1:3),null,depth_charge+1,priority_threading,null,null,null, mid_thread_count, mid_thread_pool, mid_status, mid_reach_back,mid_previous_nodes,min_new_bthread_size,min_new_midthread_size,lowest_backtrack, reach_back_B_calls_TOP,max_thread_pool,mid_max_thread_pool,reach_back_time_analysis,exit_loop,reach_back_middle_loop_run,show_me_intermitent_maxes);
				worker = new Thread(task);
				worker.setName(String.valueOf(temp));

				previous_nodes[temp].zero();
				previous_nodes[temp].add(toptop);

				if(display){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("!!!Make a new BTHREAD!!! Temp: "+temp+" Telling thread to find max clique in: "+memory_element.print_list()+" and prev_nodes is: "+previous_nodes[temp].print_list());
					semasematext.release();
				}

				if(priority_threading){
					priority = Thread.currentThread().getPriority();
					if(priority>5)
						worker.setPriority(priority-1);
					else
						worker.setPriority(5);
				}
				B_calls_TOP++;
				worker.start();


				thread_ownership.add(worker);
				stillrunninglist.add(stillrunning1);

				//delete, for testing purposes only asdf
				//check_if_threads_are_done(true, thread_ownership, stillrunninglist, display, depth_charge, max_star,mid_previous_nodes, mid_reach_back,true);

				
			}
			if(display_next)
				try {Thread.sleep(waittime);} catch(InterruptedException e) {} 

		}
		else{
			if(display){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("This was NOT run because ms.md >= DCC[0].gl");
				semasematext.release();
			}
			temp_element.zero();
		}


		if((where_from <= 1)&&(all_nodes.get_length() != 0)){
			if(lowest_backtrack > B_iteration_deep)
				lowest_backtrack = B_iteration_deep;
		}



		if(display){
			try {semasematext.take();} catch(InterruptedException e) {} 
			this.insert_spaces_for_iteration("B");
			System.out.println("about to enter main  while loop, ntc: "+nodes_to_consider.print_list()+" Tntc: "+TOP_nodes_to_consider.print_list());
			semasematext.release();
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////
		//  MAIN WHILE
		///////////////////////////////////////////////////////////////////////////////////////////////////
		while(check_set < (DCC.length)){	

			display = ((((where_from <= 4)||!degressive_display)&&(level_0_display == true)&&(B_iteration_deep < (display_level+1)))?true:false);
			//display_internal = true;



			Bochert_neighbor(temp_element, DCC[check_set].meta_data, original_all_nodes);//the nodes that have already been checked can be used to eliminate unneeded nodes... in fact... I can do this at every level...
			temp_element.use_me_and_not_first(DCC[check_set], temp_element);//only extras
			checked_set.copy_array(temp_element);//remember these deleted nodes...

			all_nodes_in_set_deleted_used = DCC[check_set].copy_by_erasing();//well... shoot... it's necessarily good to remove all the nodes yet... sigh...

			if(display){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("At the top, deciding all_nodes for node: "+DCC[check_set].meta_data+", extra already deleted nodes connected to it is: "+temp_element.print_list()+" and all_nodes before reduction was: "+DCC[check_set].print_list()+" and after reduction: "+all_nodes_in_set_deleted_used.print_list());
				semasematext.release();
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
			alpha3.meta_data = comp_set;


			//see if you can eliminate completely first
			run = true;
			alpha3.similar_differences(all_nodes_in_set_deleted_used, temp_element2, best_next_ntc);
			if(best_next_ntc.get_length() == 0){
				if(display){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("RUN == FALSE!!! Don't run this NODE!!! Because comp_set: "+comp_set+" which is node "+DCC[comp_set].meta_data+" connected to: "+alpha3.print_list()+" contains check_set: "+check_set+" which is node "+DCC[check_set].meta_data+" connected to: "+all_nodes_in_set_deleted_used.print_list());
					semasematext.release();
				}
				run = false;
			}

			if(run){			

				best_next_me.use_me_and_not_first(best_next_ntc, all_nodes_in_set_deleted_used);

				unranked_find_best_ntc_dcc(alpha3, all_nodes_in_set_deleted_used, unused_best_next_ntc, unused_best_next_me);
				if(unused_best_next_ntc.get_length() < best_next_ntc.get_length()){
					best_next_ntc.copy_array(unused_best_next_ntc);
					best_next_me.copy_array(unused_best_next_me);
				}

				if(display){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("after unranked find best, best_next_ntc was: "+best_next_ntc.print_list()+" and best_next_me: "+best_next_me.print_list());
					semasematext.release();
				}
			}
			else{
				best_next_ntc.zero();
				best_next_me.zero();
			}




			if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){

				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				if(display)
					System.out.println(" MAIN WHILE lowest_backtrack: "+lowest_backtrack+" thread:"+whoami+" thread_count: "+thread_count[0]+" mid_thread_count: "+mid_thread_count[0]+" time: "+disp_time_old()+" B_calls: "+B_calls+" threads: "+thread_count[0]+" starting on, check set is "+(1+check_set)+" out of "+(DCC.length)+" which is node "+DCC[check_set].meta_data+" with the comp_set "+comp_set+" which is node "+DCC[comp_set].meta_data+" and common nodes are: "+best_next_me.print_list()+" max_star: "+max_star.print_list()+" max_star.md: "+(best_star[0]-depth_charge));
				else
					System.out.println(" MAIN WHILE lowest_backtrack: "+lowest_backtrack+" thread:"+whoami+" thread_count: "+thread_count[0]+" mid_thread_count: "+mid_thread_count[0]+" time: "+disp_time_old()+" B_calls: "+B_calls+" threads: "+thread_count[0]+" starting on, check set is "+(1+check_set)+" out of "+(DCC.length)+" which is node "+DCC[check_set].meta_data+" with the comp_set "+comp_set+" which is node "+DCC[comp_set].meta_data+" max_star.md: "+(best_star[0]-depth_charge));
				semasematext.release();

				if(!run){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println(" so in this case run was actually false, which means it found a set that had a node that could contain all of check_set so go no further (and make ntc == 0) ");					
					semasematext.release();
				}
				//				else
				//					System.out.println(" MAIN WHILE time: "+this.disp_time()+" B_calls: "+B_calls+" starting on, check set is "+(1+check_set)+" out of "+(nodes_to_consider.get_length()+length_extra_alredy_been_checked+(already_been_checked.get_length() == 0?0:1))+" which is node "+alpha[check_set].meta_data+" with alpha length of: "+alpha[check_set].get_length()+" with the comp_set "+comp_set+" which is node "+alpha[comp_set].meta_data+" with alpha length of: "+alpha[comp_set].get_length()+" and common nodes length of: "+memory_element.get_length()+" max_star: "+max_star.print_list()+" max_star.md: "+max_star.meta_data);


				//if(degressive_display){
				//	display_level = B_iteration_deep; 
				//}

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
				alpha3.memory_next.meta_data = alpha3.meta_data;
				alpha3.memory_next.memory_previous = alpha3;
				alpha3 = alpha3.memory_next;
				

				if(display){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("reduced ntc is: "+nodes_to_consider.print_list()+" and reduced all nodes is: "+all_nodes_in_set_deleted_used.print_list());
					semasematext.release();
				}

				deepness++;


				if(display){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("Entering while loop with all_nodes: "+all_nodes_in_set_deleted_used.print_list()+" NTC: "+nodes_to_consider.print_list()+" mem_elm: "+memory_element.print_list()+" and first comp_set of: "+comp_set+" checked_set: "+checked_set.print_list());
					semasematext.release();
				}

				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//                     START SUPER WHILE
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



					deepness = 1;// should always be 1 going in from here to account for check_set
					Pointer_ONLY = mid_section(TOP_nodes_to_consider, nodes_to_consider, all_nodes_in_set_deleted_used, display, depth_charge, deepness, checked_set, all_nodes_in_set_whole, alpha3, DCC, memory_element, TOP_checked_set, check_set, where_from, System.currentTimeMillis(), mid_thread_ownership, mid_stillrunninglist, thread_ownership, stillrunninglist, false);


					if((Pointer_ONLY.get_length()+1)>(best_star[0]-depth_charge)){
						if(display){
							try {semasematext.take();} catch(InterruptedException e) {} 
							this.insert_spaces_for_iteration("B");
							System.out.println("After return from mid_section, found new max star!! just_a_pointer ("+Pointer_ONLY.get_length()+"): "+Pointer_ONLY.print_list()+" previous max_star.md: "+(best_star[0]-depth_charge));
							semasematext.release();
						}
						max_star.copy_array(Pointer_ONLY);
						//max_star.meta_data = max_star.get_length();
						try{update_max(max_star.get_length()+depth_charge,depth_charge,max_star);} catch(InterruptedException e){}

						if(!this.is_star(max_star.to_int(), true)){System.out.println("not star anymore :(");reach_back[-1]=null;}



				}

				if(display){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("GETTING BACK after running midsection");
					semasematext.release();
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

			//you can add in the previous nodes now, the ones you didn't need to check because they were already checked at the highest level, but this additional info can help isolate what needs to be checked
			Bochert_neighbor(DCC[check_set],DCC[check_set].meta_data,original_all_nodes);
			check_set++;

			start = System.currentTimeMillis();
			
//			try {semasematext.take();} catch(InterruptedException e) {} 
//			this.insert_spaces_for_iteration("B");
//			System.out.println("before bcheck_if_threads done FALSE with max_star: "+max_star.print_list());
//			semasematext.release();

			check_if_threads_are_done(false, thread_ownership, stillrunninglist, display, depth_charge, max_star,previous_nodes, reach_back,false,null,null);
			
//			try {semasematext.take();} catch(InterruptedException e) {} 
//			this.insert_spaces_for_iteration("B");
//			System.out.println("after bcheck_if_threads done FALSE with max_star: "+max_star.print_list());
//			semasematext.release();
			
			time_analysis[5] = time_analysis[5] + (System.currentTimeMillis() - start);


		}


		if(display){
			try {semasematext.take();} catch(InterruptedException e) {} 
			this.insert_spaces_for_iteration("B");
			System.out.println("Checking threads for finished threads");
			semasematext.release();
		}

		boolean add_temp_thread = false;
		if(false && (thread_pool < max_thread_pool) && thread_ownership.size() > 0){//add a temporary thread to the thread pool
			add_temp_thread = true;
		}

		if(add_temp_thread){
			try{
				semasema.take();
				thread_pool++;
				semasema.release();
			} catch(InterruptedException e) {}
		}

		start = System.currentTimeMillis();

//		try {semasematext.take();} catch(InterruptedException e) {} 
//		this.insert_spaces_for_iteration("B");
//		System.out.println("before bcheck_if_threads done TRUE with max_star: "+max_star.print_list());
//		semasematext.release();

		check_if_threads_are_done(true, thread_ownership, stillrunninglist, display, depth_charge, max_star,previous_nodes, reach_back,false,null,null);
		check_if_threads_are_done(true, mid_thread_ownership, mid_stillrunninglist, display, depth_charge, max_star,mid_previous_nodes, mid_reach_back,true,null,null);

//		try {semasematext.take();} catch(InterruptedException e) {} 
//		this.insert_spaces_for_iteration("B");
//		System.out.println("after bcheck_if_threads done TRUE with max_star: "+max_star.print_list());
//		semasematext.release();

		time_analysis[6] = time_analysis[6] + (System.currentTimeMillis() - start);

		if(add_temp_thread){
			try{
				semasema.take();
				thread_pool--;
				semasema.release();
			} catch(InterruptedException e) {}
		}


		if(display){
			try {semasematext.take();} catch(InterruptedException e) {} 
			this.insert_spaces_for_iteration("B");
			System.out.println("Returning: "+max_star.print_list());
			semasematext.release();
		}

		if(degressive_display && (display_level > B_iteration_deep)&&(where_from == 1)){
			display_level = B_iteration_deep-1; 
		}


		B_iteration_deep--;
		return max_star;

	}

	

	private node3 mid_section(node3 TOP_nodes_to_consider, node3 nodes_to_consider, node3 all_nodes_in_set_deleted_used, boolean display_internal, int depth_charge, int deepness, node3 checked_set, node3 all_nodes_in_set_whole, node3 alpha3, node3[] DCC, node3 memory_element, node3 TOP_checked_set,	int check_set, int where_from, long start, List<Thread> mid_thread_ownership, List<semaphore> mid_stillrunninglist, List<Thread> thread_ownership, List<semaphore> stillrunninglist, boolean local_mid_entry){

		
		if(display_internal){
			try {semasematext.take();} catch(InterruptedException e) {} 
			this.insert_spaces_for_iteration("B");
			System.out.println("STARTING new Mid_section ntc: "+nodes_to_consider.print_list()+" all_nodes_in_set_whole: "+all_nodes_in_set_whole.print_list()+" depth_charge: "+depth_charge+" deepness: "+deepness+" checked_set: "+checked_set.print_list()+" Tntc=ntc? "+(TOP_nodes_to_consider==nodes_to_consider)+" local_mid_entry: "+local_mid_entry);
			semasematext.release();
		}
				
		node3 max_star = new node3(nodes);

		node3 delete_this_all_nodes_in_set_whole= all_nodes_in_set_whole.copy_by_erasing();
		node3 delete_this_nodes_to_consider = nodes_to_consider.copy_by_erasing();
		boolean I_was_deleted = true;
		boolean run = true;
		boolean all_others_empty = true;
		int temp = 0;
		int priority = 0;
		node3 temp_element = new node3(nodes);
		node3 temp_element2 = new node3(nodes);
		node3 best_next_ntc = new node3(nodes);
		node3 best_next_me = new node3(nodes);
		node3 unused_best_next_ntc = new node3(nodes);
		node3 unused_best_next_me = new node3(nodes);
		node3 best_unique_alpha = new node3(nodes);
		node3 best_nodes_in_common = new node3(nodes);
		node3 unique_check = new node3(nodes);
		node3 Pointer_ONLY;
		node3 Pointer_ONLY2;
		node3 unique_alpha = new node3(nodes);
		node3 nodes_in_common = new node3(nodes);
		
		node3 nodes_at_this_level_run_by_other_threads = new node3(nodes);
		nodes_at_this_level_run_by_other_threads.memory_next = new node3(nodes);
		nodes_at_this_level_run_by_other_threads.memory_next.memory_previous = nodes_at_this_level_run_by_other_threads;
		nodes_at_this_level_run_by_other_threads = nodes_at_this_level_run_by_other_threads.memory_next;
		int blagger = 0;//delete this

//		List<Thread> mid_thread_ownership = new ArrayList<Thread>();
//		List<semaphore> mid_stillrunninglist = new ArrayList<semaphore>();

//		List<Thread> thread_ownership = new ArrayList<Thread>();
//		List<semaphore> stillrunninglist = new ArrayList<semaphore>();

		Thread thread_index;
		Runnable task;
		Thread worker; 
		semaphore stillrunning1; 


//		try {semasematext.take();} catch(InterruptedException e) {} 
//		this.insert_spaces_for_iteration("B");
//		System.out.println("Starting mid_section with ntc: "+nodes_to_consider.print_list()+" ntc.mp: "+nodes_to_consider.memory_previous.print_list());
//		semasematext.release();

		time_analysis[2] = time_analysis[2] + (System.currentTimeMillis() - start);

		while(TOP_nodes_to_consider != nodes_to_consider){

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
/*			Pointer_ONLY = nodes_to_consider;
			blagger = 0;
			while(Pointer_ONLY != TOP_nodes_to_consider){
				blagger++;
				Pointer_ONLY = Pointer_ONLY.memory_previous;
			}
			

		if((!local_mid_entry)&&((blagger) != (deepness))){
			try {semasematext.take();} catch(InterruptedException e) {} 

			this.insert_spaces_for_iteration("B");
			System.out.println("FAIL TOP MIDSECTION!!!!!"+(blagger+mid_previous_nodes[whoami].get_length())+" deepness: "+deepness+" depth_charge: "+depth_charge+" pre_depth_memory: "+pre_depth_memory+" B_iteration_deep: "+B_iteration_deep);

			this.insert_spaces_for_iteration("B");
			System.out.println("FAIL EVAL:"+" blagger: "+blagger+" deepness: "+deepness+" previous_nodes: "+mid_previous_nodes[whoami].get_length());

			semasematext.release();
			System.exit(0);
			}
		else if((local_mid_entry)&&((blagger+mid_previous_nodes[whoami].get_length()) != (deepness+1))){
			try {semasematext.take();} catch(InterruptedException e) {} 

			this.insert_spaces_for_iteration("B");
			System.out.println("FAIL TOP MIDSECTION!!!!!"+(blagger+mid_previous_nodes[whoami].get_length())+" deepness: "+deepness+" depth_charge: "+depth_charge+" pre_depth_memory: "+pre_depth_memory+" B_iteration_deep: "+B_iteration_deep);

			this.insert_spaces_for_iteration("B");
			System.out.println("FAIL EVAL:"+" blagger: "+blagger+" deepness: "+deepness+" previous_nodes: "+mid_previous_nodes[whoami].get_length());

			semasematext.release();
			System.exit(0);
			}
		else{
			if(display_internal){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("checked out okay... blagger: "+blagger+" deepness: "+deepness+" previous_nodes: "+mid_previous_nodes[whoami].get_length()+" depth_charge: "+depth_charge);									
				semasematext.release();
			}
			}
			*/
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


			I_was_deleted = true;
			run = true;
			while(I_was_deleted && (nodes_to_consider.get_length() > 0)){

				
				nodes_to_consider.meta_data = nodes_to_consider.pop_first(); //set up, this is the node we're looking at
				all_nodes_in_set_deleted_used.delete(nodes_to_consider.meta_data); //remove from the set of nodes that're to considered against

				
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				do{
				
				temp = 0;
				//System.out.println("#2 outside lowest BT call");
				if(nodes_to_consider.get_length() > 0){

				if(lowest_backtrack <= B_iteration_deep){

					try {
						//System.out.println(((nodes_to_consider.get_length() >= min_new_midthread_size)?"true":"false") + "ntc.gl: " + nodes_to_consider.get_length() + " mnms: "+min_new_midthread_size);
						if((nodes_to_consider.get_length() /*+ memory_element.get_length()*/ >= min_new_midthread_size)  && (mid_thread_count[0] < mid_thread_pool))									
							temp = available_mid_thread();
						//System.out.println("#2 inside lowest BT call, temp: "+temp+" thread_count[0]: "+thread_count[0]+" thread_pool: "+thread_pool+" and min_new_thread_size: "+min_new_thread_size+" memory_element: "+memory_element.get_length());

					} catch(InterruptedException e) {
					} 
				}		


				if(temp != 0){

					stillrunning1 = new semaphore();
					try{stillrunning1.take();} catch(InterruptedException e){}


					mid_previous_nodes[temp].zero();//set up all the previous nodes being sent to the new mid
					Pointer_ONLY = nodes_to_consider.memory_previous;
					while(Pointer_ONLY != TOP_nodes_to_consider){
						mid_previous_nodes[temp].add(Pointer_ONLY.meta_data);
						Pointer_ONLY = Pointer_ONLY.memory_previous;
					}
					if(!local_mid_entry)//don't use DCC unless you came from Newer_bochert
						mid_previous_nodes[temp].add(DCC[check_set].meta_data);
//					else{
//						mid_previous_nodes[temp].use_me_or(mid_previous_nodes[temp], mid_previous_nodes[whoami]);
//					}

					
					////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					node3 temp_all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.copy_by_erasing();
					temp_all_nodes_in_set_deleted_used.memory_previous = new node3(nodes);//this will effectively be the new TOP, and tell it when to stop //all_nodes_in_set_deleted_used.memory_previous.copy_by_erasing();
					temp_all_nodes_in_set_deleted_used.memory_previous.memory_next = temp_all_nodes_in_set_deleted_used;
					temp_all_nodes_in_set_deleted_used.delete(nodes_to_consider.meta_data);
					temp_all_nodes_in_set_deleted_used.delete(nodes_at_this_level_run_by_other_threads);

					node3 temp_all_nodes_in_set_whole = all_nodes_in_set_whole.copy_by_erasing();
					temp_all_nodes_in_set_whole.memory_previous = new node3(nodes);//this will effectively be the new TOP, and tell it when to stop //all_nodes_in_set_whole.memory_previous.copy_by_erasing();
					temp_all_nodes_in_set_whole.memory_previous.memory_next = temp_all_nodes_in_set_whole;

					int node_being_run = nodes_to_consider.pop_first();
					node3 temp_nodes_to_consider = new node3(nodes);//nodes_to_consider.copy_by_erasing();// only instead send it the next node to consider, not all the nodes
					temp_nodes_to_consider.add(node_being_run);
					temp_nodes_to_consider.memory_previous = new node3(nodes);//this will effectively be the new TOP, and tell it when to stop //nodes_to_consider.memory_previous.copy_by_erasing();
					temp_nodes_to_consider.memory_previous.memory_next = temp_nodes_to_consider;

					//nodes_to_consider.meta_data
					
					node3 temp_memory_element = memory_element.copy_by_erasing();
					temp_memory_element.memory_previous = new node3(nodes);//this will effectively be the new TOP, and tell it when to stop //memory_element.memory_previous.copy_by_erasing();
					temp_memory_element.memory_previous.memory_next = temp_memory_element;
					//this.Bochert_neighbor(temp_element2, node_being_run, nodes_to_consider); //find nodes connected to the node being run, so they aren't lost
					//temp_memory_element.use_me_or(temp_memory_element, temp_element2); // add them to the ME. They wouldn't normally be there, but we need to make sure they're not lost since we're only passing ntc of the node to consider

					node3 temp_alpha3 = alpha3.copy_by_erasing();
					temp_alpha3.memory_previous = new node3(nodes);//this will effectively be the new TOP, and tell it when to stop //alpha3.memory_previous.copy_by_erasing();
					temp_alpha3.memory_previous.memory_next = temp_alpha3;
					temp_alpha3.meta_data = alpha3.meta_data;
					Pointer_ONLY = alpha3;
					Pointer_ONLY2 = temp_alpha3;
					while(Pointer_ONLY.alpha_next != null){
						Pointer_ONLY2.alpha_next = Pointer_ONLY.alpha_next.copy_by_erasing();
						Pointer_ONLY2.alpha_next.meta_data = Pointer_ONLY.alpha_next.meta_data;
						Pointer_ONLY2.alpha_next.alpha_previous = Pointer_ONLY2;
						Pointer_ONLY = Pointer_ONLY.alpha_next;
						Pointer_ONLY2 = Pointer_ONLY2.alpha_next;
					}

					node3 temp_checked_set = checked_set.copy_by_erasing();
					temp_checked_set.memory_previous = checked_set.memory_previous.copy_by_erasing();
					temp_checked_set.memory_previous.memory_next = temp_checked_set;

					temp_checked_set.use_me_or(temp_checked_set, nodes_at_this_level_run_by_other_threads); //theoretically the previous nodes have been "run" in the future sense and should be reflected
					temp_checked_set.add(nodes_to_consider.meta_data); //add the node that is about to be run by the parent thread
					nodes_at_this_level_run_by_other_threads.add(node_being_run);//so this parent thread can remove this node after completing the current node that it's on
					temp_all_nodes_in_set_deleted_used.use_me_and_not_first(nodes_at_this_level_run_by_other_threads, temp_all_nodes_in_set_deleted_used);
					
					if(false&&(nodes_to_consider.meta_data == 34)&&(node_being_run == 35)){
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("Making mid thread from bthread. NtC.md: "+nodes_to_consider.meta_data+" ntc: "+nodes_to_consider.print_list()+" all_nodes_in_set_deleted_used: "+all_nodes_in_set_deleted_used.print_list()+" all_nodes_in_set_whole: "+all_nodes_in_set_whole.print_list()+" memory_element: "+memory_element.print_list()+" alpha3: "+alpha3.print_list());
						this.insert_spaces_for_iteration("B");
						System.out.println("Being sent to midthread. temp_ntc: "+temp_nodes_to_consider.print_list()+" temp_all_nodes_in_set_deleted_used: "+temp_all_nodes_in_set_deleted_used.print_list()+" temp_all_nodes_in_set_whole: "+temp_all_nodes_in_set_whole.print_list()+" temp_memory_element: "+temp_memory_element.print_list()+" temp_alpha3: "+temp_alpha3.print_list()+" mid_previous_nodes[temp]: "+mid_previous_nodes[temp].print_list());
						semasematext.release();
						pause();
					}

					
					////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*					int[] a = {1024};
					node3 search_nodes_to_consider = new node3(a,nodes);
					int[] b = {250,251,252,256,257,258,262,263,264,283,284,285,298,299,300,1017,1018,1025,1026,1027,1030,1031,1032,1034};
					node3 search_all_nodes_in_set_whole = new node3(b,nodes);

					if(temp_nodes_to_consider.set_equals(search_nodes_to_consider)){
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("FOUND THE CALLING THREAD!!!Make a new MIDTHREAD!!! Temp: "+temp+" node being run by thread: "+node_being_run+" temp_Memory_element: "+temp_memory_element.print_list()+" and ntc: "+temp_nodes_to_consider.print_list()+" and prev_nodes is: "+mid_previous_nodes[temp].print_list()+" checked_set: "+temp_checked_set.print_list());
						this.insert_spaces_for_iteration("B");
						System.out.println("CALLING THREAD: "+" delete_this_nodes_to_consider: "+delete_this_nodes_to_consider.print_list()+" delete_this_all_nodes_in_set_whole: "+delete_this_all_nodes_in_set_whole.print_list());
						semasematext.release();						
					}
*/					
					boolean display_next = (display_internal&&((nodes_to_consider.meta_data == 0)&&(node_being_run == 0)));
					task = new bthread(semasematext, reach_back, thread_pool, graph3, graph, DCC[0].copy_by_erasing(), display_next/*display_global*/, nodes, reach_back_B_calls ,  (display_next?display_level:-1)/*display_level*/, empty_node, degressive_display,temp,status,previous_nodes,((best_star[0]-depth_charge)==0?0:(best_star[0]-depth_charge)-1),semasema,stillrunning1,B_iteration_deep,(where_from<=1?1:3),semaMax,(depth_charge+deepness),priority_threading,best_star, thread_count, mid_semasema, mid_thread_count, mid_thread_pool, mid_status,temp_nodes_to_consider.memory_previous, temp_nodes_to_consider, temp_all_nodes_in_set_deleted_used,display_next /*display_internal*/, /*deepness*/0, temp_checked_set, temp_all_nodes_in_set_whole, temp_alpha3, DCC, temp_memory_element, temp_checked_set, check_set,mid_reach_back,mid_previous_nodes, where_from,min_new_bthread_size,min_new_midthread_size,lowest_backtrack,reach_back_B_calls_TOP,max_thread_pool,mid_max_thread_pool,reach_back_time_analysis,exit_loop,reach_back_middle_loop_run, show_me_intermitent_maxes);
					//task = new bthread(reach_back, -1, graph3, graph, memory_element.copy_by_erasing(), display, nodes, reach_back_B_calls, -1, empty_node, degressive_display,temp,null,previous_nodes,((best_star[0]-depth_charge)-deepness-1<=1?0:(best_star[0]-depth_charge)-deepness-1),semasema,stillrunning1,B_iteration_deep,3, null,depth_charge+1+deepness,priority_threading,null,null,null, mid_thread_count, mid_thread_pool, mid_status, mid_reach_back)	
					worker = new Thread(task);
					worker.setName(String.valueOf(temp));

					if(display_internal){
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("!!!Make a new MIDTHREAD!!! Temp: "+temp+" node being run by thread: "+node_being_run+" Display_next: "+display_next+" temp_Memory_element: "+temp_memory_element.print_list()+" and ntc: "+temp_nodes_to_consider.print_list()+" and prev_nodes is: "+mid_previous_nodes[temp].print_list()+" checked_set: "+temp_checked_set.print_list());
						semasematext.release();
					}

					if(priority_threading){
						priority = Thread.currentThread().getPriority();
						if(priority>5)
							worker.setPriority(priority-1);
						else
							worker.setPriority(5);
					}
					worker.start();

					mid_thread_ownership.add(worker);
					mid_stillrunninglist.add(stillrunning1);
					
					//go back now
					if(display_internal){
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("GOING BACK after skipping midsection and making a mid thread instead");
						semasematext.release();
					}

//					checked_set.memory_next = null;//I think this is unnecessary 
//					 if(nodes_to_consider.meta_data > 0){
//						 checked_set.add(nodes_to_consider.meta_data);
//					 }
//					 deepness--;

						if(display_next)
							try {Thread.sleep(waittime);} catch(InterruptedException e) {} 
				}
				}
				}while(temp != 0);
				
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				middle_loop_run++;
				
				start = System.currentTimeMillis();

				//nodes_to_consider.meta_data = nodes_to_consider.pop_first(); //set up, this is the node we're looking at
				//all_nodes_in_set_deleted_used.delete(nodes_to_consider.meta_data); //remove from the set of nodes that're to considered against

//				try {semasematext.take();} catch(InterruptedException e) {} 
//				this.insert_spaces_for_iteration("B");
//				System.out.println("Executing node: "+nodes_to_consider.meta_data+" ntc: "+nodes_to_consider.print_list()+" ntc.mp: "+nodes_to_consider.memory_previous.print_list());
//				semasematext.release();

				
				this.Bochert_neighbor(temp_element2, nodes_to_consider.meta_data, all_nodes_in_set_deleted_used); //find connected nodes
				//this.Bochert_neighbor(all_nodes_extra_extra, nodes_to_consider.meta_data, all_nodes_extra_extra);

				temp_element = reduction(temp_element2, empty_node, null); //remove obvious extraneous nodes
				
				if(display_internal){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("initial check of node: "+nodes_to_consider.meta_data+" deepness: "+deepness+" which is connected to: "+temp_element.print_list()+" but before reduction it was: "+temp_element2.print_list()+" which was pulled from all_nodes_in_set_deleted_used of: "+all_nodes_in_set_deleted_used.print_list()+" checked_set: "+checked_set.print_list());
					semasematext.release();
				}

				if(((best_star[0]-depth_charge)>(temp_element.get_length()+deepness))){ //is it even possible to produce a larger star?

					checked_set.add(nodes_to_consider.meta_data);
					if(nodes_at_this_level_run_by_other_threads.length != 0){
						nodes_to_consider.use_me_and_not_first(nodes_at_this_level_run_by_other_threads, nodes_to_consider);//take away the nodes that were looked at by other threads
						checked_set.use_me_or(checked_set, nodes_at_this_level_run_by_other_threads);//add them to the looked at
						nodes_at_this_level_run_by_other_threads.zero();//then zero them out
					}
					
					if(display_internal){
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("EARLY ELIMINATED OUT!!!! (that is, node: "+nodes_to_consider.meta_data+") aka, no longer can form bigger star because too few left to consider... ms.md: "+(best_star[0]-depth_charge)+" > deepness: "+deepness+" and all_nodes.gl: "+temp_element.get_length()+" which is: "+temp_element.print_list());
						semasematext.release();
					}

					if(((best_star[0]-depth_charge)>(all_nodes_in_set_deleted_used.get_length()+(deepness-1)))){//check to see if you're done yet, if there are fewer nodes left, so even if they were all interconnected, they still couldn't produce a bigger star
						//check no more

						if(display_internal){
							try {semasematext.take();} catch(InterruptedException e) {} 
							this.insert_spaces_for_iteration("B");
							System.out.println("EARLY ELIMINATED OUT the rest of the nodes too!!!! because ms.md: "+(best_star[0]-depth_charge)+" > deepness-1: "+(deepness-1)+" and all_nodes_in_set_deleted_used: "+all_nodes_in_set_deleted_used.get_length()+" which is: "+all_nodes_in_set_deleted_used.print_list());
							semasematext.release();
						}


						nodes_to_consider.zero();
					}

					time_analysis[7] = time_analysis[7] + (System.currentTimeMillis() - start);
					start = System.currentTimeMillis();

				}
				else{

					time_analysis[7] = time_analysis[7] + (System.currentTimeMillis() - start);
					start = System.currentTimeMillis();

					I_was_deleted = this.deletable(nodes_to_consider.meta_data, all_nodes_in_set_whole, empty_node, false, temp_element); //check to see if this one node can be optimized out

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

						if(display_internal){
							try {semasematext.take();} catch(InterruptedException e) {} 
							this.insert_spaces_for_iteration("B");
							System.out.println("alpha3["+0+"].md: "+alpha3.memory_previous.meta_data+" was: "+alpha3.memory_previous.print_list()+" but .md: "+alpha3.meta_data+" it's now: "+alpha3.print_list()+" deepness: "+deepness);
							semasematext.release();
						}


						if(all_others_empty && (alpha3.get_length() != 0)){
							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("all_others_empty is not false... so don't run it down yet said because "+alpha3.print_list()+" best next me is: "+best_next_me.print_list());
								semasematext.release();
							}
							all_others_empty = false;
						}

						time_analysis[8] = time_analysis[8] + (System.currentTimeMillis() - start);
						start = System.currentTimeMillis();


						alpha3.similar_differences(temp_element, best_unique_alpha, unique_check);
						best_nodes_in_common.use_me_and_not_first(best_unique_alpha, alpha3);


						Pointer_ONLY = alpha3.memory_previous;
						Pointer_ONLY2 = alpha3;

						int analysis_start = (local_mid_entry?-1:0);
						for(int i = analysis_start; i < deepness; i++){

							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("In for loop, at top of alpha expansion for loop, i is: "+i);
								semasematext.release();
							}

							Pointer_ONLY2.alpha_next = new node3(nodes);//DCC[i].copy_by_erasing();

							if(i+1 == deepness){// the new one...
								//Bochert_neighbor(temp_element2, nodes_to_consider.memory_previous.meta_data, checked_set);//which of the deleted nodes is connected...
								temp_element2.copy_array(all_nodes_in_set_whole);//all nodes in the set... includes deleted nodes?
								temp_element2.use_me_or(temp_element2, checked_set);//add in deleted nodes... in case there was a deleted node from a previous level, current level deleted nodes should still be contained in all_nodes_whole
								temp_element2.delete(nodes_to_consider.meta_data);//don't include the current node... duh...

								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println("Adding new one... using the options of checked nodes: "+checked_set.print_list()+" connected to (all possible nodes at this level): "+temp_element2.print_list()+" checked_set: "+checked_set.print_list());
									semasematext.release();
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

								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println("found better next_ntc unused_best_ntc: "+unused_best_next_ntc.print_list()+"best_next_ntc: "+best_next_ntc.print_list()+" unused_best_me: "+unused_best_next_me.print_list()+" best_me: "+best_next_me.print_list()+" checked_set: "+checked_set.print_list());
									semasematext.release();
								}


								best_next_ntc.copy_array(unused_best_next_ntc);
								best_next_me.copy_array(unused_best_next_me);
							}


							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("alpha3["+i+"].md: "+Pointer_ONLY.meta_data+" was: "+(i+1 == deepness?"NEW":Pointer_ONLY.print_list())+" but .md: "+Pointer_ONLY2.meta_data+" it's now: "+Pointer_ONLY2.print_list()+" deepness: "+deepness+" checked_set: "+checked_set.print_list());
								semasematext.release();
							}


							Pointer_ONLY2.similar_differences(temp_element, unique_alpha, unique_check);
							nodes_in_common.use_me_and_not_first(unique_alpha, Pointer_ONLY2);
							if(nodes_in_common.get_length() > best_nodes_in_common.get_length()){
								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println("found better next_ntc unused_best_ntc: "+unused_best_next_ntc.print_list()+"best_next_ntc: "+best_next_ntc.print_list()+" unused_best_me: "+unused_best_next_me.print_list()+" best_me: "+best_next_me.print_list()+" checked_set: "+checked_set.print_list());
									semasematext.release();
								}
								best_unique_alpha.copy_array(unique_alpha);
								best_nodes_in_common.copy_array(nodes_in_common);
							}
							else if(nodes_in_common.get_length() == best_nodes_in_common.get_length()){
								if(unique_alpha.get_length() > best_unique_alpha.get_length()){
									if(display_internal){
										try {semasematext.take();} catch(InterruptedException e) {} 
										this.insert_spaces_for_iteration("B");
										System.out.println("found better next_ntc unused_best_ntc: "+unused_best_next_ntc.print_list()+"best_next_ntc: "+best_next_ntc.print_list()+" unused_best_me: "+unused_best_next_me.print_list()+" best_me: "+best_next_me.print_list()+" checked_set: "+checked_set.print_list());
										semasematext.release();
									}
									best_unique_alpha.copy_array(unique_alpha);
									best_nodes_in_common.copy_array(nodes_in_common);
								}
							}


							if(all_others_empty && (Pointer_ONLY2.get_length() != 0)){
								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println("all_others_empty is not false... so don't run it down yet, said because "+alpha3.print_list()+" best next me is: "+best_next_me.print_list()+" checked_set: "+checked_set.print_list()+" checked_set: "+checked_set.print_list());
									semasematext.release();
								}
								all_others_empty = false;
							}

						}

						time_analysis[9] = time_analysis[9] + (System.currentTimeMillis() - start);
						start = System.currentTimeMillis();

						////////////////////////////////////////////////////////////////////////////////
						/*try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("SUPER WHILE "+exit_loop[0]+" initial check of node: "+nodes_to_consider.meta_data+" which is connected to: "+temp_element.print_list()+/*" but before reduction it was: "+temp_element2.print_list()+/" which was pulled from all_nodes_in_set_deleted_used of: "+all_nodes_in_set_deleted_used.print_list()+" Time: "+disp_time_old()+" B_calls: "+B_calls+" starting on, check set is "+(1+check_set)+" out of "+(DCC.length)+" which is node "+DCC[check_set].meta_data+" with the comp_set "+/*comp_set+/" which is node "+/*DCC[comp_set].meta_data+/" and common nodes are: "+best_next_me.print_list()+" max_star: "+max_star.print_list()+" max_star.md: "+max_star.meta_data);
						Pointer_ONLY2 = alpha3;
						this.insert_spaces_for_iteration("B");
						System.out.print("Printing alpha nexts that were just found: ");
						for(int i = 0; i < deepness; i++){
							System.out.print(" == "+Pointer_ONLY2.alpha_next.print_list());
						}
						System.out.println();
						exit_loop[0]--;
						if(exit_loop[0] <= 0)
							System.exit(0);
						semasematext.release();
						*/
						///////////////////////////////////////////////////////////////////////////////

						
						if(I_was_deleted){
							alpha3 = alpha3.memory_previous;
							alpha3.memory_next = null;

						}
						else{	
							//temp_element = reduction(temp_element, empty_node, null);//already been done

							temp_element2.use_me_or(best_unique_alpha, best_nodes_in_common);
							unranked_find_best_ntc_dcc(temp_element2, temp_element, unused_best_next_ntc, unused_best_next_me); 
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

					if(I_was_deleted){
						checked_set.add(nodes_to_consider.meta_data);
					
						if(nodes_at_this_level_run_by_other_threads.length != 0){
							nodes_to_consider.use_me_and_not_first(nodes_at_this_level_run_by_other_threads, nodes_to_consider);//take away the nodes that were looked at by other threads
							checked_set.use_me_or(checked_set, nodes_at_this_level_run_by_other_threads);//add them to the looked at
							nodes_at_this_level_run_by_other_threads.zero();//then zero them out
						}
					}
				}



				if(display_internal){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("I_was_deleted: "+I_was_deleted+" ntc.md: "+nodes_to_consider.meta_data+" ntc: "+nodes_to_consider.print_list()+" checked_set: "+checked_set.print_list());
					semasematext.release();
				}

				time_analysis[3] = time_analysis[3] + (System.currentTimeMillis() - start);

			}			

			if(!I_was_deleted){// || (nodes_to_consider.get_length()+lost_nodes.get_length()) > 0){

				start = System.currentTimeMillis();

				deepness++; //ntc.md, as of right now, is now part of the consideration, there is now one more node under the belt, hense the deepness++
	

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

					nodes_at_this_level_run_by_other_threads.memory_next = new node3(nodes);
					nodes_at_this_level_run_by_other_threads.memory_next.memory_previous = nodes_at_this_level_run_by_other_threads;
					nodes_at_this_level_run_by_other_threads = nodes_at_this_level_run_by_other_threads.memory_next;


					checked_set.memory_next = new node3(nodes);
					checked_set.memory_next.memory_previous = checked_set;
					checked_set = checked_set.memory_next;
					this.Bochert_neighbor(checked_set, nodes_to_consider.memory_previous.meta_data, checked_set.memory_previous);//move to the next set of deleted nodes the ones from the previous set connected to current node


					checked_set = checked_set.memory_previous;
					nodes_to_consider = nodes_to_consider.memory_previous;
					nodes_at_this_level_run_by_other_threads = nodes_at_this_level_run_by_other_threads.memory_previous;

				}

//				if(display_internal){
//					try {semasematext.take();} catch(InterruptedException e) {} 
//					this.insert_spaces_for_iteration("B");
//					System.out.println(" considering node: "+nodes_to_consider.meta_data+" deepness: "+deepness+" with nodes still to consider: "+nodes_to_consider.print_list()+" has memory_elment("+memory_element.get_length()+"): "+memory_element.print_list()+" and it's own ntc: "+(nodes_to_consider.memory_next != null?nodes_to_consider.memory_next.print_list():"NULL")+" all_nodes: "+all_nodes_in_set_deleted_used.print_list()+" checked_set: "+checked_set.print_list());
//					semasematext.release();
//				}

				if(display_internal){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					if(best_next_ntc.get_length() > 0)
						System.out.println("Analysis complete on "+nodes_to_consider.meta_data+" ntc: "+nodes_to_consider.memory_next.print_list()+" checked_set: "+checked_set.memory_next.print_list()+" checked_set.prev: "+checked_set.print_list()+" all_nodes_in_set_deleted_used: "+all_nodes_in_set_deleted_used.print_list()+" all_nodes_in_set_whole: "+all_nodes_in_set_whole.print_list()+" memory_element: "+memory_element.print_list()+" nodes_at_this_level_run_by_other_threads: "+nodes_at_this_level_run_by_other_threads.memory_next.print_list()+" previous nodes to consider (where this nodes was pulled from) is: "+nodes_to_consider.print_list());
					else
						System.out.println("Analysis complete on "+nodes_to_consider.meta_data+" ntc: "+"N/A"+" checked_set: "+"N/A"+" checked_set.prev: "+checked_set.print_list()+" all_nodes_in_set_deleted_used: "+all_nodes_in_set_deleted_used.print_list()+" all_nodes_in_set_whole: "+all_nodes_in_set_whole.print_list()+" memory_element: "+memory_element.print_list()+" nodes_at_this_level_run_by_other_threads: "+"N/A"+" previous nodes to consider (where this nodes was pulled from) is: "+nodes_to_consider.print_list());
					semasematext.release();
				}




				//should be checked already higher up
				if(((best_star[0]-depth_charge)>(all_nodes_in_set_deleted_used.get_length()+deepness))){
					//need not look further

					if(display_internal){
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("FAIL!!! THIS SHOULD NEVER RUN!!!! SHOULD'VE ELMINIATED EARLIER!!!! ELIMINATED OUT!!!! aka, no longer can form bigger star because too few left to consider... ms.md: "+(best_star[0]-depth_charge)+" > me.gl: "+memory_element.get_length()+" all_nodes: "+all_nodes_in_set_deleted_used.get_length()+" + deepness: "+deepness);
						semasematext.release();
					}

					checked_set.add(nodes_to_consider.meta_data);
					
					if(nodes_at_this_level_run_by_other_threads.length != 0){
						nodes_to_consider.use_me_and_not_first(nodes_at_this_level_run_by_other_threads, nodes_to_consider);//take away the nodes that were looked at by other threads
						checked_set.use_me_or(checked_set, nodes_at_this_level_run_by_other_threads);//add them to the looked at
						nodes_at_this_level_run_by_other_threads.zero();//then zero them out
					}
					
					memory_element = memory_element.memory_previous;								
					all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_previous;
					all_nodes_in_set_whole = all_nodes_in_set_whole.memory_previous;



					alpha3 = alpha3.memory_previous;
					deepness--;




				}
				else{


					if(display_internal){
						try {semasematext.take();} catch(InterruptedException e) {} 
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
						semasematext.release();
					}

					time_analysis[4] = time_analysis[4] + (System.currentTimeMillis() - start);
					start = System.currentTimeMillis();

					if(all_others_empty){

						check_if_threads_are_done(false, thread_ownership, stillrunninglist, display_internal, depth_charge, max_star,previous_nodes, reach_back,false,delete_this_nodes_to_consider,delete_this_all_nodes_in_set_whole);


						if(display_internal){
							try {semasematext.take();} catch(InterruptedException e) {} 
							this.insert_spaces_for_iteration("B");
							System.out.println(">> B_calls: "+B_calls+" run: "+run+" calling Bochert("+memory_element.print_list()+" ,cm: "+((best_star[0]-depth_charge)-deepness<1?0:(best_star[0]-depth_charge)-deepness-1)+"(aka: max_star is: "+max_star.print_list()+") ,sm: "+nodes+" , abc: "+temp_element.print_list()+"; ");
							semasematext.release();
						}

						if(((best_star[0]-depth_charge)-deepness-1<=0?0:(best_star[0]-depth_charge)-deepness-1) >= memory_element.get_length()){

							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("in run loop, in second number check, but run now false because ms.md ("+(best_star[0]-depth_charge)+") - deepness ("+deepness+" -1 >= me.gl"+memory_element.get_length()+" which is the same thing as all_nodes");
								semasematext.release();
							}

							Pointer_ONLY = empty_node;
						}
						else{
							temp = 0;
							//System.out.println("#1 outside lowest BT call");
							if(lowest_backtrack <= B_iteration_deep){

								try {
									if((memory_element.get_length() >= min_new_bthread_size) && (thread_count[0] < thread_pool))

										temp = available_thread();
									else
										temp = 0;

									//System.out.println("#1 inside lowest BT call, temp: "+temp+" thread_count[0]: "+thread_count[0]+" thread_pool: "+thread_pool+" and min_new_thread_size: "+min_new_thread_size+" memory_element: "+memory_element.get_length());

								} catch(InterruptedException e) {
								} 
							}		


							//if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("temp = "+temp);
								semasematext.release();
								//}
							}

							boolean display_next = (display_internal&&(nodes_to_consider.meta_data == 0));
							if(temp == 0){
							
								
								Pointer_ONLY = Newer_Bochert(memory_element, ((best_star[0]-depth_charge)-deepness-1<1?0:(best_star[0]-depth_charge)-deepness-1), nodes, display_next /*display_internal*/,3,depth_charge+deepness);									

								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println(">> returned with: "+Pointer_ONLY.print_list()+" FYI tho, just_a_pointer.get_length: "+Pointer_ONLY.get_length()+" deepness: "+deepness+" <?> max_star.md: "+(best_star[0]-depth_charge)+" and fyi, empty node: "+empty_node.print_list());
									semasematext.release();
								}

								if((Pointer_ONLY.get_length()+deepness+depth_charge)>(best_star[0])){
									
									try{update_max(Pointer_ONLY.get_length()+deepness+depth_charge,depth_charge,max_star);} catch(InterruptedException e){}

									if(display_internal){
										try {semasematext.take();} catch(InterruptedException e) {} 
										this.insert_spaces_for_iteration("B");
										System.out.println("found new max star!! te.gl: "+Pointer_ONLY.print_list()+" deepness: "+deepness+" depth_charge: "+depth_charge+" previous max_star.md: "+(best_star[0]-depth_charge));
										semasematext.release();
									}
									max_star.copy_array(Pointer_ONLY);
									Pointer_ONLY = nodes_to_consider;
									while(Pointer_ONLY != TOP_nodes_to_consider){
										if(display_internal){
											try {semasematext.take();} catch(InterruptedException e) {} 
											this.insert_spaces_for_iteration("B");
											System.out.println("Adding: "+Pointer_ONLY.meta_data);
											semasematext.release();
										}
										max_star.add(Pointer_ONLY.meta_data);
										Pointer_ONLY = Pointer_ONLY.memory_previous;
										if(!this.is_star(max_star.to_int(), true)){System.out.println("B_calls: "+B_calls+" not star anymore :(");DCC[-1]=null;}
									}
									if(!local_mid_entry){//don't use DCC unless you came from Newer_bochert
										max_star.add(DCC[check_set].meta_data);
									}
									else{
										max_star.use_me_or(max_star, mid_previous_nodes[whoami]);
									}

									if(display_internal){
										try {semasematext.take();} catch(InterruptedException e) {} 
										this.insert_spaces_for_iteration("B");
										System.out.println("In Midsection, new Max_star it's: "+max_star.print_list());
										semasematext.release();
									}
									if(!this.is_star(max_star.to_int(), true)){System.out.println("B_calls: "+B_calls+" not star anymore :(");DCC[-1]=null;}


								}


							}
							else{


								stillrunning1 = new semaphore();
								try{stillrunning1.take();} catch(InterruptedException e){}

								task = new bthread(semasematext, reach_back, -1, graph3, graph, memory_element.copy_by_erasing(), display_next /*display_global*/, nodes, reach_back_B_calls, (display_next?display_level:-1)/*-1*/, empty_node, degressive_display,temp,null,previous_nodes,((best_star[0]-depth_charge)-deepness-1<=1?0:(best_star[0]-depth_charge)-deepness-1),semasema,stillrunning1,B_iteration_deep,3, null,depth_charge+deepness,priority_threading,null,null,null, mid_thread_count, mid_thread_pool, mid_status, mid_reach_back,mid_previous_nodes,min_new_bthread_size,min_new_midthread_size,lowest_backtrack, reach_back_B_calls_TOP,max_thread_pool,mid_max_thread_pool,reach_back_time_analysis,exit_loop,reach_back_middle_loop_run,show_me_intermitent_maxes);	
								worker = new Thread(task);
								worker.setName(String.valueOf(temp));

								previous_nodes[temp].zero();
								Pointer_ONLY = nodes_to_consider;
								while(Pointer_ONLY != TOP_nodes_to_consider){
									previous_nodes[temp].add(Pointer_ONLY.meta_data);
									Pointer_ONLY = Pointer_ONLY.memory_previous;
								}
								if(!local_mid_entry){//don't use DCC unless you came from Newer_bochert
									previous_nodes[temp].add(DCC[check_set].meta_data);
								}
//								else{
//									previous_nodes[temp].use_me_or(previous_nodes[temp], mid_previous_nodes[whoami]);
//								}


								if(priority_threading){
									priority = Thread.currentThread().getPriority();
									if(priority>5)
										worker.setPriority(priority-1);
									else
										worker.setPriority(5);
								}
								worker.start();

								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println("Before adding new thread, thread ownership is now size: "+thread_ownership.size());
									semasematext.release();
								}

								
								thread_ownership.add(worker);
								stillrunninglist.add(stillrunning1);

								
								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println("!!!Make a new BTHREAD!!! Temp: "+temp+" Telling thread to find max clique in: "+memory_element.print_list()+" and prev_nodes is: "+previous_nodes[temp].print_list()+" after adding new thread, thread ownership is now size: "+thread_ownership.size());
									semasematext.release();
								}

								
								//delete, for testing purposes only asdf
								//check_if_threads_are_done(true, thread_ownership, stillrunninglist, display_internal, depth_charge, max_star,mid_previous_nodes, mid_reach_back,true);

							}

							//if(display_next)
							//	try {Thread.sleep(waittime);} catch(InterruptedException e) {} 
							
						}



						if(display_internal){
							try {semasematext.take();} catch(InterruptedException e) {} 
							this.insert_spaces_for_iteration("B");
							System.out.println("ran it, GOING BACK now");
							semasematext.release();
						}

						checked_set.add(nodes_to_consider.meta_data);
						
						if(nodes_at_this_level_run_by_other_threads.length != 0){
							nodes_to_consider.use_me_and_not_first(nodes_at_this_level_run_by_other_threads, nodes_to_consider);//take away the nodes that were looked at by other threads
							checked_set.use_me_or(checked_set, nodes_at_this_level_run_by_other_threads);//add them to the looked at
							nodes_at_this_level_run_by_other_threads.zero();//then zero them out
						}
						
						memory_element = memory_element.memory_previous;
						all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_previous;
						all_nodes_in_set_whole = all_nodes_in_set_whole.memory_previous;


						alpha3 = alpha3.memory_previous;

						deepness--;




					}
					else{
						//go deeper

						if(best_next_ntc.get_length() > 0){
							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("did not run it because run was not true at start, PRESSING ON because best_next_ntc: "+best_next_ntc.print_list());
								semasematext.release();
							}

							checked_set = checked_set.memory_next;
							nodes_to_consider = nodes_to_consider.memory_next;					
							nodes_at_this_level_run_by_other_threads = nodes_at_this_level_run_by_other_threads.memory_next;

//							try {semasematext.take();} catch(InterruptedException e) {} 
//							this.insert_spaces_for_iteration("B");
//							System.out.println("Going deeper, ntc: "+nodes_to_consider.print_list()+" ntc.mp: "+nodes_to_consider.memory_previous.print_list()+" ntc.mp.mp: "+nodes_to_consider.memory_previous.memory_previous.print_list());
//							semasematext.release();

							
//							deepness++;

						}
						else{
							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("did not run it because run was not true at start, GOING BACK because best_next_ntc: "+best_next_ntc.print_list());
								semasematext.release();
							}

							checked_set.add(nodes_to_consider.meta_data);
							
							if(nodes_at_this_level_run_by_other_threads.length != 0){
								nodes_to_consider.use_me_and_not_first(nodes_at_this_level_run_by_other_threads, nodes_to_consider);//take away the nodes that were looked at by other threads
								checked_set.use_me_or(checked_set, nodes_at_this_level_run_by_other_threads);//add them to the looked at
								nodes_at_this_level_run_by_other_threads.zero();//then zero them out
							}
							
							memory_element = memory_element.memory_previous;								
							all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_previous;
							all_nodes_in_set_whole = all_nodes_in_set_whole.memory_previous;

							alpha3 = alpha3.memory_previous;

							deepness--;
						}

					}
				}
			}
			else{
				if(display_internal){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("GOING BACK because nodes_to_consider is: "+nodes_to_consider.print_list()+" and I_was_deleted: "+I_was_deleted+" before going back deepness is: "+deepness);
					semasematext.release();
				}
				//go back to previous
				all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_previous;
				all_nodes_in_set_whole = all_nodes_in_set_whole.memory_previous;
				nodes_to_consider = nodes_to_consider.memory_previous;
				memory_element = memory_element.memory_previous;
				nodes_at_this_level_run_by_other_threads = nodes_at_this_level_run_by_other_threads.memory_previous;


				alpha3 = alpha3.memory_previous;


				checked_set = checked_set.memory_previous;
				checked_set.memory_next = null;
				
				if(nodes_at_this_level_run_by_other_threads.length != 0){
					nodes_to_consider.use_me_and_not_first(nodes_at_this_level_run_by_other_threads, nodes_to_consider);//take away the nodes that were looked at by other threads
					checked_set.use_me_or(checked_set, nodes_at_this_level_run_by_other_threads);//add them to the looked at
					nodes_at_this_level_run_by_other_threads.zero();//then zero them out
				}

				if(nodes_to_consider.meta_data > 0){
					checked_set.add(nodes_to_consider.meta_data);
					}
				
				deepness--;
			}
//			try {semasematext.take();} catch(InterruptedException e) {} 
//			this.insert_spaces_for_iteration("B");
//			System.out.println("before check_if_threads done FALSE with max_star: "+max_star.print_list());
//			semasematext.release();

			check_if_threads_are_done(false, mid_thread_ownership, mid_stillrunninglist, display_internal, depth_charge, max_star,mid_previous_nodes, mid_reach_back,true,delete_this_nodes_to_consider,delete_this_all_nodes_in_set_whole);

//			try {semasematext.take();} catch(InterruptedException e) {} 
//			this.insert_spaces_for_iteration("B");
//			System.out.println("after check_if_threads done FALSE with max_star: "+max_star.print_list());
//			semasematext.release();

		}	

//		try {semasematext.take();} catch(InterruptedException e) {} 
//		this.insert_spaces_for_iteration("B");
//		System.out.println("before check_if_threads done TRUE with max_star: "+max_star.print_list());
//		semasematext.release();

//		check_if_threads_are_done(true, mid_thread_ownership, mid_stillrunninglist, display_internal, depth_charge, max_star,mid_previous_nodes, mid_reach_back,true);
		check_if_threads_are_done(false, mid_thread_ownership, mid_stillrunninglist, display_internal, depth_charge, max_star,mid_previous_nodes, mid_reach_back,true,delete_this_nodes_to_consider,delete_this_all_nodes_in_set_whole);

//		try {semasematext.take();} catch(InterruptedException e) {} 
//		this.insert_spaces_for_iteration("B");
//		System.out.println("after check_if_threads done TRUE with max_star: "+max_star.print_list());
//		semasematext.release();

		//		if(display_internal){
		//			try {semasematext.take();} catch(InterruptedException e) {} 
		//			this.insert_spaces_for_iteration("B");
		//			System.out.println("GOING BACK because nodes_to_consider is: "+nodes_to_consider.print_list()+" and I_was_deleted: "+I_was_deleted+" before going back deepness is: "+deepness);
		//			semasematext.release();
		//		}

		
//		try {semasematext.take();} catch(InterruptedException e) {} 
//		this.insert_spaces_for_iteration("B");
//		System.out.println("Ending mid_section with max_star: "+max_star.print_list());
//		semasematext.release();

		if(local_mid_entry){//finish threads
			check_if_threads_are_done(true, thread_ownership, stillrunninglist, display_internal, depth_charge, max_star,previous_nodes, reach_back,false,delete_this_nodes_to_consider,delete_this_all_nodes_in_set_whole);
			check_if_threads_are_done(true, mid_thread_ownership, mid_stillrunninglist, display_internal, depth_charge, max_star,mid_previous_nodes, mid_reach_back,true,delete_this_nodes_to_consider,delete_this_all_nodes_in_set_whole);
		}


		return max_star;
	}



	private boolean unranked_find_best_ntc_dcc(node3 alpha3, node3 check_all_nodes, node3 best_ntc, node3 best_dcc){

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
			try {semasematext.take();} catch(InterruptedException e) {} 
			this.insert_spaces_for_iteration("B");
			System.out.println("In get_next_comp_all_nodes, comp_all_nodes: "+comp_all_nodes.print_list()+" all_nodes_deleting: "+all_nodes_deleting.print_list());
			semasematext.release();
		}

		if(comp_all_nodes.get_length() == 0){
			if(display){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("returning because comp_all_nodes is zero length");
				semasematext.release();
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
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("returning because comp_all_nodes has no nodes that are not already in all_nodes_deleting");
				semasematext.release();
			}
			next.zero();
			contains_check_set = false;
			return contains_check_set;
		}
		if(temp_extra.get_length() == comp_all_nodes.get_length()){
			if(display){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("returning because comp_all_nodes has no nodes in common with all_nodes_deleting");
				semasematext.release();
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
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("!!!!!comp_all_nodes: "+comp_all_nodes.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println("!!!!!connected to: "+cycle[i]+" is temp element: "+temp_element.print_list()+" graph[55][57]"+graph[55][57]);
				semasematext.release();
			}


			temp_element.similar_differences(all_nodes_deleting, temp_unique, all_nodes_unique);
			common.use_me_and_not_first(all_nodes_unique, all_nodes_deleting);

			if(display){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("comparing node: "+cycle[i]+" connected to: "+temp_element.print_list()+" with unique: "+temp_unique.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println("check_set connected to: "+all_nodes_deleting.print_list()+" with unique: "+all_nodes_unique.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println("common nodes: "+common.print_list());
				semasematext.release();
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
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("min_unique is g.t. all_nodes_unique");
						semasematext.release();
					}
					minimizing_unique_all_nodes = all_nodes_unique.get_length();
					extra = temp_unique.get_length();
					node = cycle[i];				
				}
				else if(minimizing_unique_all_nodes == all_nodes_unique.get_length()){ 
					if(temp_unique.get_length() > extra){//guarnteed better
						if(display){
							try {semasematext.take();} catch(InterruptedException e) {} 
							this.insert_spaces_for_iteration("B");
							System.out.println("min_unique is equal to all_nodes_unique, but temp_unique is greater than extra");
							semasematext.release();
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
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("none of the next nodes contain any common nodes");
				semasematext.release();
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
			try {semasematext.take();} catch(InterruptedException e) {} 
			this.insert_spaces_for_iteration("B");
			System.out.println("returning, node "+node+" won"+" and temp_extra: "+temp_extra.print_list());
			semasematext.release();
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
			System.out.print(disp_time_old()+" "+(mid_entry?"M:":"B:")+whoami);

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

	public void disp_graph(){
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

}
