package Clique;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class midthread implements Runnable  {

	private int[][] graph; // the adjacency matrix
	private node3[] graph3; // the adjacency matrix
	private int[][] old_graph; //when changing the graph around, can keep the old one to ensure that the returned set is indeed a clique
	private int nodes; // total number of nodes
	private int BK_iteration_deep = 0; // iterations deep into BronKerbosch
	private long BK_calls = 0; // calls to BronKerbosch
	private int B_iteration_deep = -1; //iterations deep into Bochert
	private long B_calls = 0; // calls to Bochert
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
	private boolean display = false;
	private long[] reach_back_B_calls;
	private int whoami;
	private node3[] previous_nodes;
	private int previously_known_max;
	private int who_ran_me = 100;
	private int pre_depth_memory;

	static boolean fing_semaphore = false;
	static int[] thread_count = new int[1];
	static int thread_pool = 1;
	static int[] status;//0 means free, 1 means running
	semaphore semasema = null;
	static semaphore semaMax = null;
	semaphore stillrunning = null;
	static int display_level = 0;
	static int[] best_star = new int[1];
	static boolean priority_threading = false;
	
	node3 ZTOP_nodes_to_consider; 
	node3 Znodes_to_consider; 
	node3 Zall_nodes_in_set_deleted_used; 
	boolean Zdisplay_internal; 
	int Zdepth_charge; 
	int Zdeepness; 
	node3 Zchecked_set; 
	node3 Zall_nodes_in_set_whole; 
	node3 Zalpha3; 
	node3[] ZDCC; 
	node3 Zmemory_element; 
	node3 ZTOP_checked_set;	
	List<Thread> Zthread_ownership; 
	List<semaphore> Zstillrunninglist; 
	int Zcheck_set;
	int Zwhere_from;
	
	static int[] mid_thread_count = new int[1];
	static int mid_thread_pool = 1;
	static int[] mid_status;//0 means free, 1 means running
	static semaphore mid_semasema = null;
	
	static node3[] mid_reach_back = null;
	static node3[] mid_previous_nodes = null;
	
	semaphore semasematext = null;
	static int min_new_thread_size = 10;
	
	midthread(semaphore semasematext1, node3[] reach_back1, int thread_pool1, node3[] graph31, int[][] graph1, node3 find1, boolean display1, int nodes1, long[] reach_back_B_calls1, int display_level1, node3 empty_node1, boolean degressive_display1, int whoami1, int[] status1, node3[] previous_nodes1, int previously_known_max1, semaphore semasema1, semaphore stillrunning1, int B_iteration_deep1,int who_ran_me1, semaphore semaMax1, int pre_depth_memory1, boolean priority_threading1, int[] best_star1, int[] thread_count1, semaphore mid_semasema1, int[] mid_thread_count1, int mid_thread_pool1, int[] mid_status1,node3 TOP_nodes_to_consider, node3 nodes_to_consider, node3 all_nodes_in_set_deleted_used, boolean display_internal, int depth_charge, int deepness, node3 checked_set, node3 all_nodes_in_set_whole, node3 alpha3, node3[] DCC, node3 memory_element, node3 TOP_checked_set,	List<Thread> thread_ownership, List<semaphore> stillrunninglist, int check_set, node3[] mid_reach_back1,node3[] mid_previous_nodes1, int where_from1, int min_new_thread_size1){
//	(node3[] reach_back1, int thread_pool1, node3[] graph31, int[][] graph1, node3 find1, boolean display1, int nodes1, long[] reach_back_B_calls1, int display_level1, node3 empty_node1, boolean degressive_display1, int whoami1, int[] status1, node3[] previous_nodes1, int previously_known_max1, semaphore semasema1, semaphore stillrunning1, int B_iteration_deep1,int who_ran_me1, semaphore semaMax1, int pre_depth_memory1, boolean priority_threading1, int[] best_star1, 
		
		semasematext = semasematext1;
			
		reach_back = reach_back1;
		graph3 = graph31;
		find = find1;
		display = display1;
		level_0_display = display1; 
		nodes = nodes1;
		graph = graph1;
		reach_back_B_calls = reach_back_B_calls1;
		if(display_level1>0) display_level = display_level1;
		empty_node = empty_node1;
		degressive_display = degressive_display1;
		whoami = whoami1;
		if(thread_pool1 > 0) thread_pool = thread_pool1;
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
		min_new_thread_size = min_new_thread_size1;
		
		if(mid_semasema1 != null){
			mid_thread_count = mid_thread_count1;
			mid_thread_pool = mid_thread_pool1;
			mid_status = mid_status1;
			mid_semasema = mid_semasema1;
		}

		
		ZTOP_nodes_to_consider = TOP_nodes_to_consider;
		Znodes_to_consider = nodes_to_consider;
		Zall_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used;  
		Zdisplay_internal = display_internal ;
		Zdepth_charge = depth_charge ;
		Zdeepness = deepness ;
		Zchecked_set = checked_set ; 
		Zall_nodes_in_set_whole = all_nodes_in_set_whole ; 
		Zalpha3 = alpha3 ;
		ZDCC = DCC ;
		Zmemory_element =  memory_element ;
		ZTOP_checked_set = TOP_checked_set ;
		Zthread_ownership = thread_ownership ;
		Zstillrunninglist = stillrunninglist ;
		Zcheck_set = check_set ;
		Zwhere_from = where_from1;
	}

	
	@Override
	public void run() {

		//Thread.currentThread().setPriority(whoami);
		
		mid_reach_back[whoami].zero();
		
		try {semasematext.take();} catch(InterruptedException e) {} 
		if(display)		System.out.println("M++++++++++display: "+display+" engtering run... whoami: "+whoami+" thread_count[0]: "+thread_count[0]+" priority: "+Thread.currentThread().getPriority()+" sending memory_element of: "+Zmemory_element.print_list()+" and sending ntc of: "+Znodes_to_consider.print_list()+" prev_nodes: "+mid_previous_nodes[whoami].print_list());
		semasematext.release();

//		node3 result = Newer_Bochert(find,previously_known_max,nodes,display,who_ran_me,pre_depth_memory);
		node3 result = mid_section(ZTOP_nodes_to_consider, Znodes_to_consider, Zall_nodes_in_set_deleted_used, Zdisplay_internal, Zdepth_charge, Zdeepness, Zchecked_set, Zall_nodes_in_set_whole, Zalpha3, ZDCC, Zmemory_element, ZTOP_checked_set, Zcheck_set, Zwhere_from, true);
//		node3 mid_section(node3 TOP_nodes_to_consider, node3 nodes_to_consider, node3 all_nodes_in_set_deleted_used, boolean display_internal, int depth_charge, int deepness, node3 checked_set, node3 all_nodes_in_set_whole, node3 alpha3, node3[] DCC, node3 memory_element, node3 TOP_checked_set,	List<Thread> thread_ownership, List<semaphore> stillrunninglist, int check_set, int where_from){


		try{semaMax.take();} catch(InterruptedException e){}
		
		mid_reach_back[whoami] = result;
		reach_back_B_calls[0] += B_calls;
		
		semaMax.release();

		stillrunning.release();

		try {semasematext.take();} catch(InterruptedException e) {} 
		if(display)		System.out.println("M----------exiting run... whoami: "+whoami+" thread_count: "+thread_count[0]+" returnning: "+result.print_list()+" prev_nodes: "+mid_previous_nodes[whoami].print_list());
		semasematext.release();

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

		public int update_max(int new_max) throws InterruptedException{
			
		semaMax.take();

		if(best_star[0] < new_max)
			best_star[0] = new_max;
			
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

	

	private void check_if_threads_are_done(boolean stop, List<Thread> thread_ownership, List<semaphore> stillrunninglist, boolean display_internal, int depth_charge, node3 max_star, node3[] previous_nodes, node3[] reach_back, boolean midthread){



		Runnable task = null;
		Thread worker = null;
		Thread thread_index = null; 
		int temp;
		node3 Pointer_ONLY;


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

				//combine them, has to contain some dumb duplicates, this is an extra safe-guard against double counting doublicates
				Pointer_ONLY.use_me_or(Pointer_ONLY, previous_nodes[temp]);


				if((Pointer_ONLY.get_length()+1)>(best_star[0]-depth_charge)){
					if(display_internal){
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("found new max star!! just_a_pointer: "+Pointer_ONLY.print_list()+" previous_nodes: "+previous_nodes[temp].print_list()+" previous max_star.md: "+(best_star[0]-depth_charge));
					semasematext.release();
						}
					max_star.copy_array(Pointer_ONLY);

					max_star.use_me_or(max_star, previous_nodes[temp]);

					//max_star.meta_data = max_star.get_length();
					try{update_max(max_star.get_length()+depth_charge);} catch(InterruptedException e){}

					//if(!this.is_star(max_star.to_int(), true)){System.out.println("not star anymore :(");reach_back[-1]=null;}
					if(!this.is_star(max_star.to_int(), true)){try {semasematext.take();} catch(InterruptedException e) {}System.out.println("M:"+whoami+" failing thread was mid? "+midthread+" thread: "+temp+" reachback: "+reach_back[temp].print_list()+" prev_nodes: "+previous_nodes[temp].print_list()+" not star anymore :(");reach_back[-1]=null;}

					if(display_internal){
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("updated max");
					semasematext.release();
						}

				}
				else{
					if(display_internal){
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("was found that this WAS NOT new max star!! just_a_pointer("+Pointer_ONLY.get_length()+"): "+Pointer_ONLY.print_list()+" previous_nodes (should be combined): "+previous_nodes[temp].print_list()+" previous max_star length: "+(best_star[0]-depth_charge));
					semasematext.release();
						}
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

		B_iteration_deep++;
		B_calls++;

		if(B_iteration_deep == 0)
			show = level_0_display;

		if(B_calls == hotswap_trigger){
			hotswap();
		}


		boolean display_internal = ((((where_from <= 4)||!degressive_display)&&(level_0_display == true)&&(B_iteration_deep < (display_level+1)))?true:false);
		//		display_internal = true;




		if((all_nodes.get_length() == 0)||(all_nodes.get_length() == 1)){

			if(display_internal){
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
			if(display_internal){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("returning because sought_max <= 1");
				semasematext.release();
			}
			result.add(all_nodes.get_index(0));
			B_iteration_deep--;
			return result;
		}

		if(all_nodes.get_length() <= (best_star[0]-depth_charge)){//if it's equal to, you'll only get the same as the current max
			//this.insert_spaces_for_iteration("B");
			//System.out.println("returning because all_nodes < current max, where from: "+where_from);
			if(display_internal){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("Returning because all_nodes.gl <= (best_star[0]-depth_charge)");
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
		semaphore stillrunning1 = null;



		node3[] DCC = new node3[0];
		node3 alpha3 = new node3(nodes);

		//node3 comp_nodes = new node3(nodes);



		this.Bochert_neighbor(result, toptop, all_nodes);


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



			result = Newer_Bochert(result,((best_star[0]-depth_charge)==0?0:(best_star[0]-depth_charge)-1),(sought_max==0?0:sought_max-1),display_internal,(where_from<=1?1:4),depth_charge+1);
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



		if(display_internal){
			for(int i = 0; i<DCC.length; i++){
				try {semasematext.take();} catch(InterruptedException e) {} 		
				this.insert_spaces_for_iteration("B");
				System.out.println(" -- DCC["+i+"].md: "+DCC[i].meta_data+" and is: "+DCC[i].print_list());
			semasematext.release();
			}
		}




		///////////////////////////////////////////////////////////////////////////////////////////////////
		//  TOP WHILE
		///////////////////////////////////////////////////////////////////////////////////////////////////
		if((where_from <= 1)&&(all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
			try {semasematext.take();} catch(InterruptedException e) {} 			
			this.insert_spaces_for_iteration("B");
			if(display_internal)
				System.out.println(" TOP WHILE thread:"+whoami+" thread_count: "+thread_count[0]+" time: "+this.disp_time()+" B_calls: "+B_calls+" starting on, check set is "+(1+0)+" out of "+(DCC.length)+" which is node "+DCC[0].meta_data+" with DCC of: "+DCC[0].print_list()+" with no comp_set but current max of: "+(best_star[0]));
			else
				System.out.println(" TOP WHILE thread:"+whoami+" thread_count: "+thread_count[0]+" time: "+this.disp_time()+" B_calls: "+B_calls+" starting on, check set is "+(1+0)+" out of "+(DCC.length)+" which is node "+DCC[0].meta_data+" with DCC size of: "+DCC[0].get_length()+" with no comp_set but current max of: "+(best_star[0]));
		semasematext.release();
		}




		try {
			if(DCC[0].get_length()>=min_new_thread_size)
				temp = available_thread();
			else
				temp = 0;
		} catch(InterruptedException e) {
		} 
		//			if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
		if(display_internal){
			try {semasematext.take();} catch(InterruptedException e) {} 
			this.insert_spaces_for_iteration("B");
			System.out.println("temp = "+temp);
					semasematext.release();
}
		//			}

		if(temp == 0){//no new threads

			temp_element = Newer_Bochert(DCC[0].copy_by_erasing(), ((best_star[0]-depth_charge)==0?0:(best_star[0]-depth_charge)-1), nodes, display_internal, (where_from<=1?1:3), depth_charge+1);

			if((temp_element.get_length()+1)>=(best_star[0]-depth_charge)){
				if(display_internal){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("found new max star!! te.gl: "+temp_element.get_length()+" deepness: "+deepness+" max_star.md: "+(best_star[0]-depth_charge));
				semasematext.release();
					}
				max_star.copy_array(temp_element);
				max_star.add(toptop);

				if(!this.is_star(max_star.to_int(), true)){System.out.println("not star anymore :(");DCC[-1]=null;}
				try{update_max(max_star.get_length()+depth_charge);} catch(InterruptedException e){}
				//max_star.meta_data = max_star.get_length();
			}
			else{
			}


		}
		else{


			//				task = new bthread(reach_back, thread_pool, graph3, graph, memory_element.copy_by_erasing(), display, nodes, reach_back_B_calls, display_level, empty_node, degressive_display,temp,status,previous_nodes);
			stillrunning1 = new semaphore();
			try{stillrunning1.take();} catch(InterruptedException e){}

			task = new bthread(semasematext, reach_back, -1, graph3, graph, DCC[0].copy_by_erasing(), display, nodes, reach_back_B_calls, -1, empty_node, degressive_display,temp,null,previous_nodes,((best_star[0]-depth_charge)==0?0:(best_star[0]-depth_charge)-1),semasema,stillrunning1,B_iteration_deep,(where_from<=1?1:3),null,depth_charge+1,priority_threading,null,null,null, mid_thread_count, mid_thread_pool, mid_status, mid_reach_back,mid_previous_nodes,min_new_thread_size);
			worker = new Thread(task);
			worker.setName(String.valueOf(temp));

			previous_nodes[temp].zero();
			previous_nodes[temp].add(toptop);

			if(display_internal){
				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				System.out.println("!!!Make a new THREAD!!! Telling thread to find max clique in: "+memory_element.print_list()+" and prev_nodes is: "+previous_nodes[temp].print_list());
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


			thread_ownership.add(worker);
			stillrunninglist.add(stillrunning1);


		}


		if(display_internal){
			try {semasematext.take();} catch(InterruptedException e) {} 
			this.insert_spaces_for_iteration("B");
			System.out.println("about to enter main  while loop, ntc: "+nodes_to_consider.print_list()+" Tntc: "+TOP_nodes_to_consider.print_list());
		semasematext.release();
			}

		///////////////////////////////////////////////////////////////////////////////////////////////////
		//  MAIN WHILE
		///////////////////////////////////////////////////////////////////////////////////////////////////
		while(check_set < (DCC.length)){	

			display_internal = ((((where_from <= 4)||!degressive_display)&&(level_0_display == true)&&(B_iteration_deep < (display_level+1)))?true:false);
			//display_internal = true;



			Bochert_neighbor(temp_element, DCC[check_set].meta_data, original_all_nodes);//the nodes that have already been checked can be used to eliminate unneeded nodes... in fact... I can do this at every level...
			temp_element.use_me_and_not_first(DCC[check_set], temp_element);//only extras
			checked_set.copy_array(temp_element);//remember these deleted nodes...

			all_nodes_in_set_deleted_used = DCC[check_set].copy_by_erasing();//well... shoot... it's necessarily good to remove all the nodes yet... sigh...

			if(display_internal){
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


			//see if you can eliminate completely first
			run = true;
			alpha3.similar_differences(all_nodes_in_set_deleted_used, temp_element2, best_next_ntc);
			if(best_next_ntc.get_length() == 0){
				if(display_internal){
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

				if(display_internal){
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




			if((where_from <= 1)&&(all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){

				try {semasematext.take();} catch(InterruptedException e) {} 
				this.insert_spaces_for_iteration("B");
				if(display_internal)
					System.out.println(" MAIN WHILE thread:"+whoami+" thread_count: "+thread_count[0]+" time: "+this.disp_time()+" B_calls: "+B_calls+" threads: "+thread_count[0]+" starting on, check set is "+(1+check_set)+" out of "+(DCC.length)+" which is node "+DCC[check_set].meta_data+" with the comp_set "+comp_set+" which is node "+DCC[comp_set].meta_data+" and common nodes are: "+best_next_me.print_list()+" max_star: "+max_star.print_list()+" max_star.md: "+(best_star[0]));
				else
					System.out.println(" MAIN WHILE thread:"+whoami+" thread_count: "+thread_count[0]+" time: "+this.disp_time()+" B_calls: "+B_calls+" threads: "+thread_count[0]+" starting on, check set is "+(1+check_set)+" out of "+(DCC.length)+" which is node "+DCC[check_set].meta_data+" with the comp_set "+comp_set+" which is node "+DCC[comp_set].meta_data+" max_star.md: "+(best_star[0]));
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
				alpha3.memory_next.memory_previous = alpha3;
				alpha3 = alpha3.memory_next;

				if(display_internal){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("reduced ntc is: "+nodes_to_consider.print_list()+" and reduced all nodes is: "+all_nodes_in_set_deleted_used.print_list());
				semasematext.release();
					}

				deepness++;


				if(display_internal){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("Entering while loop with all_nodes: "+all_nodes_in_set_deleted_used.print_list()+" NTC: "+nodes_to_consider.print_list()+" mem_elm: "+memory_element.print_list()+" and first comp_set of: "+comp_set);
				semasematext.release();
					}

				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//                     START SUPER WHILE
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




				if(true){
					Pointer_ONLY = mid_section(TOP_nodes_to_consider, nodes_to_consider, all_nodes_in_set_deleted_used, display_internal, depth_charge, deepness, checked_set, all_nodes_in_set_whole, alpha3, DCC, memory_element, TOP_checked_set, check_set, where_from, false);


					if((Pointer_ONLY.get_length()+1)>(best_star[0]-depth_charge)){
						if(display_internal){
							try {semasematext.take();} catch(InterruptedException e) {} 
							this.insert_spaces_for_iteration("B");
							System.out.println("found new max star!! just_a_pointer ("+Pointer_ONLY.get_length()+"): "+Pointer_ONLY.print_list()+" previous max_star.md: "+(best_star[0]-depth_charge));
						semasematext.release();
							}
						max_star.copy_array(Pointer_ONLY);
						//max_star.meta_data = max_star.get_length();
						try{update_max(max_star.get_length()+depth_charge);} catch(InterruptedException e){}

						if(!this.is_star(max_star.to_int(), true)){System.out.println("not star anymore :(");reach_back[-1]=null;}
					}



				}
				else{
					stillrunning1 = new semaphore();
					try{stillrunning1.take();} catch(InterruptedException e){}


					//task = new bthread(reach_back1, num_threads, graph3, graph, send,                    display, nodes, reach_back_B_calls1, display_level, empty_node, degressive_display,0,status,   previous_nodes,0,                                                               semasema,stillrunning,-1               ,0                  ,semaMax,0,             priority_threading,new int[1]);
					task = new midthread(semasematext, reach_back, thread_pool, graph3, graph, DCC[0].copy_by_erasing(), display, nodes, reach_back_B_calls , display_level, empty_node, degressive_display,temp,status,previous_nodes,((best_star[0]-depth_charge)==0?0:(best_star[0]-depth_charge)-1),semasema,stillrunning1,B_iteration_deep,(where_from<=1?1:3),semaMax,depth_charge+1,priority_threading,best_star, thread_count, mid_semasema, mid_thread_count, mid_thread_pool, mid_status,TOP_nodes_to_consider, nodes_to_consider, all_nodes_in_set_deleted_used, display_internal, depth_charge, deepness, checked_set, all_nodes_in_set_whole, alpha3, DCC, memory_element, TOP_checked_set, thread_ownership, stillrunninglist, check_set,mid_reach_back,mid_previous_nodes, where_from,min_new_thread_size);
					worker = new Thread(task);
					worker.setName(String.valueOf("midthread unnamed"));

					//					previous_nodes[temp].zero();
					//					previous_nodes[temp].add(toptop);

					//					if(display_internal){
					//						this.insert_spaces_for_iteration("B");
					//						System.out.println("!!!Make a new THREAD!!! Telling thread to find max clique in: "+memory_element.print_list()+" and prev_nodes is: "+previous_nodes[temp].print_list());
					//					}

					//					if(priority_threading){
					//					priority = Thread.currentThread().getPriority();
					//					if(priority>5)
					//						worker.setPriority(priority-1);
					//					else
					//						worker.setPriority(5);
					//					}
					worker.start();

					try {
						stillrunning1.take();
					} catch(InterruptedException e) {
					} 


				}


				if(display_internal){
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


			check_if_threads_are_done(false, thread_ownership, stillrunninglist, display_internal, depth_charge, max_star,previous_nodes, reach_back,false);


		}


		if(display_internal){
			try {semasematext.take();} catch(InterruptedException e) {} 
			this.insert_spaces_for_iteration("B");
			System.out.println("Checking threads for finished threads");
		semasematext.release();
			}

		check_if_threads_are_done(true, thread_ownership, stillrunninglist, display_internal, depth_charge, max_star,previous_nodes, reach_back,false);

		if(display_internal){
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


	private node3 mid_section(node3 TOP_nodes_to_consider, node3 nodes_to_consider, node3 all_nodes_in_set_deleted_used, boolean display_internal, int depth_charge, int deepness, node3 checked_set, node3 all_nodes_in_set_whole, node3 alpha3, node3[] DCC, node3 memory_element, node3 TOP_checked_set,	int check_set, int where_from, boolean mid_entry){


		node3 max_star = new node3(nodes);


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

		List<Thread> mid_thread_ownership = new ArrayList<Thread>();
		List<semaphore> mid_stillrunninglist = new ArrayList<semaphore>();

		List<Thread> thread_ownership = new ArrayList<Thread>();
		List<semaphore> stillrunninglist = new ArrayList<semaphore>();


		Thread thread_index;
		Runnable task;
		Thread worker; 
		semaphore stillrunning1; 





		while(TOP_nodes_to_consider != nodes_to_consider){



			I_was_deleted = true;
			run = true;
			while(I_was_deleted && (nodes_to_consider.get_length() > 0)){

				nodes_to_consider.meta_data = nodes_to_consider.pop_first();
				all_nodes_in_set_deleted_used.delete(nodes_to_consider.meta_data);

				this.Bochert_neighbor(temp_element2, nodes_to_consider.meta_data, all_nodes_in_set_deleted_used);
				//this.Bochert_neighbor(all_nodes_extra_extra, nodes_to_consider.meta_data, all_nodes_extra_extra);

				temp_element = reduction(temp_element2, empty_node, null);

				if(display_internal){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("initial check of node: "+nodes_to_consider.meta_data+" which is connected to: "+temp_element.print_list()+" but before reduction it was: "+temp_element2.print_list()+" which was pulled from all_nodes_in_set_deleted_used of: "+all_nodes_in_set_deleted_used.print_list()+"");
				semasematext.release();
					}

				if(((best_star[0]-depth_charge)>(temp_element.get_length()+deepness))){

					checked_set.add(nodes_to_consider.meta_data);
					if(display_internal){
						try {semasematext.take();} catch(InterruptedException e) {} 
						this.insert_spaces_for_iteration("B");
						System.out.println("EARLY ELIMINATED OUT!!!! (that is, node: "+nodes_to_consider.meta_data+") aka, no longer can form bigger star because too few left to consider... ms.md: "+(best_star[0]-depth_charge)+" > deepness: "+deepness+" and all_nodes.gl: "+temp_element.get_length()+" which is: "+temp_element.print_list());
					semasematext.release();
						}

					if(((best_star[0]-depth_charge)>(all_nodes_in_set_deleted_used.get_length()+(deepness-1)))){
						//check no more

						if(display_internal){
							try {semasematext.take();} catch(InterruptedException e) {} 
							this.insert_spaces_for_iteration("B");
							System.out.println("EARLY ELIMINATED OUT the rest of the nodes too!!!! because ms.md: "+(best_star[0]-depth_charge)+" > deepness-1: "+(deepness-1)+" and all_nodes_in_set_deleted_used: "+all_nodes_in_set_deleted_used.get_length()+" which is: "+all_nodes_in_set_deleted_used.print_list());
						semasematext.release();
							}


						nodes_to_consider.zero();								
					}

				}
				else{

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

						if(display_internal){
							try {semasematext.take();} catch(InterruptedException e) {} 
							this.insert_spaces_for_iteration("B");
							System.out.println("alpha3["+0+"] was: "+alpha3.memory_previous.print_list()+" but it's now: "+alpha3.print_list()+" deepness: "+deepness);
						semasematext.release();
							}


						if(all_others_empty && (alpha3.get_length() != 0)){
							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("all_others_empty is not false... so don't run it down yet");
							semasematext.release();
								}
							all_others_empty = false;
						}



						alpha3.similar_differences(temp_element, best_unique_alpha, unique_check);
						best_nodes_in_common.use_me_and_not_first(best_unique_alpha, alpha3);


						Pointer_ONLY = alpha3.memory_previous;
						Pointer_ONLY2 = alpha3;

						for(int i = 0; i < deepness; i++){

							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("In for loop, at top of random for loop, i is: "+i);
							semasematext.release();
								}

							Pointer_ONLY2.alpha_next = new node3(nodes);//DCC[i].copy_by_erasing();

							if(i+1 == deepness){// the new one...
								//Bochert_neighbor(temp_element2, nodes_to_consider.memory_previous.meta_data, checked_set);//which of the deleted nodes is connected...
								temp_element2.copy_array(all_nodes_in_set_whole);//all nodes in the set... includes deleted nodes?
								temp_element2.use_me_or(temp_element2, checked_set);//add in deleted nodes... incase there was a deleted node from a previous level, current level deleted nodes should still be contained in all_nodes_whole
								temp_element2.delete(nodes_to_consider.meta_data);//don't include the current node... duh...

								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println("Adding new one... using the options of deleted nodes: "+checked_set.print_list()+" connected to: "+temp_element2.print_list());
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
								best_next_ntc.copy_array(unused_best_next_ntc);
								best_next_me.copy_array(unused_best_next_me);
							}


							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("alpha3["+i+"] was: "+(i+1 == deepness?"NEW":Pointer_ONLY.print_list())+" but it's now: "+Pointer_ONLY2.print_list()+" deepness: "+deepness);
							semasematext.release();
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
								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println("all_others_empty is not false... so don't run it down yet");
								semasematext.release();
									}
								all_others_empty = false;
							}

						}

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

					if(I_was_deleted)
						checked_set.add(nodes_to_consider.meta_data);
				}



				if(display_internal){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("I_was_deleted: "+I_was_deleted);
				semasematext.release();
					}


			}

			if(!I_was_deleted){// || (nodes_to_consider.get_length()+lost_nodes.get_length()) > 0){




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

				if(display_internal){
					try {semasematext.take();} catch(InterruptedException e) {} 
					this.insert_spaces_for_iteration("B");
					System.out.println("deepness: "+deepness+" considering node "+nodes_to_consider.meta_data+" with nodes still to consider: "+nodes_to_consider.print_list()+" has memory_elment("+memory_element.get_length()+"): "+memory_element.print_list()+" and it's own ntc: "+(nodes_to_consider.memory_next != null?nodes_to_consider.memory_next.print_list():"NULL")+" all_nodes: "+all_nodes_in_set_deleted_used.print_list()+" checked_set: "+checked_set.print_list());
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
					memory_element = memory_element.memory_previous;								
					all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_previous;
					all_nodes_in_set_whole = all_nodes_in_set_whole.memory_previous;



					alpha3 = alpha3.memory_previous;




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


					if(all_others_empty){

			
						check_if_threads_are_done(false, thread_ownership, stillrunninglist, display_internal, depth_charge, max_star,previous_nodes, reach_back,false);


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
							try {
								if(memory_element.get_length() >= min_new_thread_size)
									temp = available_thread();
								else
									temp = 0;
							} catch(InterruptedException e) {
							} 

							//if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("temp = "+temp);
								semasematext.release();
								//}
							}

							if(temp == 0){

								Pointer_ONLY = Newer_Bochert(memory_element, ((best_star[0]-depth_charge)-deepness-1<1?0:(best_star[0]-depth_charge)-deepness-1), nodes, display_internal,3,depth_charge+1+deepness);									

								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println(">> returned with: "+Pointer_ONLY.print_list()+" FYI tho, just_a_pointer.get_length: "+Pointer_ONLY.get_length()+" deepness: "+deepness+" <?> max_star.md: "+(best_star[0]-depth_charge)+" and fyi, empty node: "+empty_node.print_list());
								semasematext.release();
									}

							if((Pointer_ONLY.get_length()+deepness+1)>=(best_star[0]-depth_charge)){
									
									if(display_internal){
										try {semasematext.take();} catch(InterruptedException e) {} 
										this.insert_spaces_for_iteration("B");
										System.out.println("found new max star!! te.gl: "+Pointer_ONLY.print_list()+" deepness: "+deepness+" previous max_star.md: "+(best_star[0]-depth_charge));
									semasematext.release();
										}

									max_star.copy_array(Pointer_ONLY);
/*									Pointer_ONLY = nodes_to_consider;
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
									if(!mid_entry)//don't use DCC unless you came from Newer_bochert
										max_star.add(DCC[check_set].meta_data);
*/
									Pointer_ONLY = nodes_to_consider;
									while(Pointer_ONLY != TOP_nodes_to_consider){
										max_star.add(Pointer_ONLY.meta_data);
										Pointer_ONLY = Pointer_ONLY.memory_previous;
									}
									if(!mid_entry){//don't use DCC unless you came from Newer_bochert
										max_star.add(DCC[check_set].meta_data);
									}
									else{
										max_star.use_me_or(max_star, mid_previous_nodes[whoami]);
									}

									
									
									
									//max_star.meta_data = max_star.get_length();
									try{update_max(max_star.get_length()+depth_charge);} catch(InterruptedException e){}
									
									if(display_internal&&!mid_entry){
										try {semasematext.take();} catch(InterruptedException e) {} 
										this.insert_spaces_for_iteration("B");
										System.out.println("just added: "+DCC[check_set].meta_data+" so max_star is now: "+max_star.print_list());
									semasematext.release();
										}
									if(!this.is_star(max_star.to_int(), true)){System.out.println("B_calls: "+B_calls+" not star anymore :(");DCC[-1]=null;}


								}


							}
							else{


								stillrunning1 = new semaphore();
								try{stillrunning1.take();} catch(InterruptedException e){}

								task = new bthread(semasematext, reach_back, -1, graph3, graph, memory_element.copy_by_erasing(), display, nodes, reach_back_B_calls, -1, empty_node, degressive_display,temp,null,previous_nodes,((best_star[0]-depth_charge)-deepness-1<=1?0:(best_star[0]-depth_charge)-deepness-1),semasema,stillrunning1,B_iteration_deep,3, null,depth_charge+1+deepness,priority_threading,null,null,null, mid_thread_count, mid_thread_pool, mid_status, mid_reach_back,mid_previous_nodes,min_new_thread_size);	
								worker = new Thread(task);
								worker.setName(String.valueOf(temp));

								previous_nodes[temp].zero();
								Pointer_ONLY = nodes_to_consider;
								while(Pointer_ONLY != TOP_nodes_to_consider){
									previous_nodes[temp].add(Pointer_ONLY.meta_data);
									Pointer_ONLY = Pointer_ONLY.memory_previous;
								}
								if(!mid_entry){//don't use DCC unless you came from Newer_bochert
									previous_nodes[temp].add(DCC[check_set].meta_data);
								}
								else{
									previous_nodes[temp].use_me_or(previous_nodes[temp], mid_previous_nodes[whoami]);
								}

								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println("!!!Make a new B THREAD!!! Telling thread to find max clique in: "+memory_element.print_list()+" and prev_nodes is: "+previous_nodes[temp].print_list());
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


								thread_ownership.add(worker);
								stillrunninglist.add(stillrunning1);
							}

						}



						if(display_internal){
							try {semasematext.take();} catch(InterruptedException e) {} 
							this.insert_spaces_for_iteration("B");
							System.out.println("ran it, GOING BACK now");
						semasematext.release();
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
							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("did not run it because run was not true at start, PRESSING ON because best_next_ntc: "+best_next_ntc.print_list());
							semasematext.release();
								}

							checked_set = checked_set.memory_next;
							nodes_to_consider = nodes_to_consider.memory_next;

							deepness++;


							try {
								if(nodes_to_consider.get_length()+memory_element.get_length() >= min_new_thread_size)
									temp = available_mid_thread();
								else
									temp = 0;
							} catch(InterruptedException e) {
							} 

							if(temp != 0){

								stillrunning1 = new semaphore();
								try{stillrunning1.take();} catch(InterruptedException e){}


								mid_previous_nodes[temp].zero();
								Pointer_ONLY = nodes_to_consider.memory_previous;
								while(Pointer_ONLY != TOP_nodes_to_consider){
									mid_previous_nodes[temp].add(Pointer_ONLY.meta_data);
									Pointer_ONLY = Pointer_ONLY.memory_previous;
								}
								if(!mid_entry)//don't use DCC unless you came from Newer_bochert
									mid_previous_nodes[temp].add(DCC[check_set].meta_data);
								else{
									mid_previous_nodes[temp].use_me_or(mid_previous_nodes[temp], mid_previous_nodes[whoami]);
								}


								////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
								all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_previous;
								node3 temp_all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.copy_by_erasing();
								temp_all_nodes_in_set_deleted_used.zero();//actually, I don't think I want to save the previous values...
								temp_all_nodes_in_set_deleted_used.memory_next = all_nodes_in_set_deleted_used.memory_next;
								all_nodes_in_set_deleted_used.memory_next = null;//I don't want any backward traffic
								temp_all_nodes_in_set_deleted_used.memory_next.memory_previous = temp_all_nodes_in_set_deleted_used;
								temp_all_nodes_in_set_deleted_used = temp_all_nodes_in_set_deleted_used.memory_next;

								all_nodes_in_set_whole = all_nodes_in_set_whole.memory_previous;
								node3 temp_all_nodes_in_set_whole = all_nodes_in_set_whole.memory_previous.copy_by_erasing();
								temp_all_nodes_in_set_whole.zero();//actually, I don't think I want to save the previous values...
								temp_all_nodes_in_set_whole.memory_next = all_nodes_in_set_whole.memory_next;
								all_nodes_in_set_whole.memory_next = null;//I don't want any backward traffic
								temp_all_nodes_in_set_whole.memory_next.memory_previous = temp_all_nodes_in_set_whole;
								temp_all_nodes_in_set_whole = temp_all_nodes_in_set_whole.memory_next;

								nodes_to_consider = nodes_to_consider.memory_previous;
								node3 temp_nodes_to_consider = nodes_to_consider.copy_by_erasing();
								temp_nodes_to_consider.zero();//actually, I don't think I want to save the previous values...
								temp_nodes_to_consider.memory_next = nodes_to_consider.memory_next;
								nodes_to_consider.memory_next = null;//I don't want any backward traffic
								temp_nodes_to_consider.memory_next.memory_previous = temp_nodes_to_consider;
								temp_nodes_to_consider = temp_nodes_to_consider.memory_next;

								memory_element = memory_element.memory_previous;
								node3 temp_memory_element = memory_element.copy_by_erasing();
								temp_memory_element.zero();//actually, I don't think I want to save the previous values...
								temp_memory_element.memory_next = memory_element.memory_next;
								memory_element.memory_next = null;//I don't want any backward traffic
								temp_memory_element.memory_next.memory_previous = temp_memory_element;
								temp_memory_element = temp_memory_element.memory_next;

								alpha3 = alpha3.memory_previous;
								node3 temp_alpha3 = alpha3.copy_by_erasing();
								temp_alpha3.zero();//actually, I don't think I want to save the previous values...
								temp_alpha3.memory_next = alpha3.memory_next;
								alpha3.memory_next = null;//I don't want any backward traffic
								temp_alpha3.memory_next.memory_previous = temp_alpha3;
								temp_alpha3 = temp_alpha3.memory_next;

								checked_set = checked_set.memory_previous;
								node3 temp_checked_set = checked_set.copy_by_erasing();
								temp_checked_set.zero();//actually, I don't think I want to save the previous values...
								temp_checked_set.memory_next = checked_set.memory_next;
								checked_set.memory_next = null;//I don't want any backward traffic
								temp_checked_set.memory_next.memory_previous = temp_checked_set;
								temp_checked_set = temp_checked_set.memory_next;
								////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

								task = new midthread(semasematext, reach_back, thread_pool, graph3, graph, DCC[0].copy_by_erasing(), display, nodes, reach_back_B_calls , display_level, empty_node, degressive_display,temp,status,previous_nodes,((best_star[0]-depth_charge)==0?0:(best_star[0]-depth_charge)-1),semasema,stillrunning1,B_iteration_deep,(where_from<=1?1:3),semaMax,depth_charge+1,priority_threading,best_star, thread_count, mid_semasema, mid_thread_count, mid_thread_pool, mid_status,/*TOP_nodes_to_consider*/temp_nodes_to_consider.memory_previous, temp_nodes_to_consider, temp_all_nodes_in_set_deleted_used, display_internal, depth_charge, deepness, temp_checked_set, temp_all_nodes_in_set_whole, temp_alpha3, DCC, temp_memory_element, temp_checked_set.memory_previous, thread_ownership, stillrunninglist, check_set,mid_reach_back,mid_previous_nodes, where_from,min_new_thread_size);
								//task = new bthread(reach_back, -1, graph3, graph, memory_element.copy_by_erasing(), display, nodes, reach_back_B_calls, -1, empty_node, degressive_display,temp,null,previous_nodes,((best_star[0]-depth_charge)-deepness-1<=1?0:(best_star[0]-depth_charge)-deepness-1),semasema,stillrunning1,B_iteration_deep,3, null,depth_charge+1+deepness,priority_threading,null,null,null, mid_thread_count, mid_thread_pool, mid_status, mid_reach_back)	
								worker = new Thread(task);
								worker.setName(String.valueOf(temp));

								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println("!!!Make a new THREAD!!! Telling thread to find max clique in: "+temp_memory_element.print_list()+" and ntc: "+temp_nodes_to_consider.print_list()+" and prev_nodes is: "+mid_previous_nodes[temp].print_list());
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

								//						try{stillrunning1.take();} catch(InterruptedException e){}
								//						stillrunning1.release();



								mid_thread_ownership.add(worker);
								mid_stillrunninglist.add(stillrunning1);


								//go back now
								if(display_internal){
									try {semasematext.take();} catch(InterruptedException e) {} 
									this.insert_spaces_for_iteration("B");
									System.out.println("GOING BACK after running midsection");
								semasematext.release();
									}
								//go back to previous//moved earlier
								/*						all_nodes_in_set_deleted_used = all_nodes_in_set_deleted_used.memory_previous;
						all_nodes_in_set_whole = all_nodes_in_set_whole.memory_previous;
						nodes_to_consider = nodes_to_consider.memory_previous;
						memory_element = memory_element.memory_previous;

						alpha3 = alpha3.memory_previous;


						checked_set = checked_set.memory_previous;
								 */						checked_set.memory_next = null;
								 if(nodes_to_consider.meta_data > 0){
									 checked_set.add(nodes_to_consider.meta_data);
								 }
								 deepness--;

							}




						}
						else{
							if(display_internal){
								try {semasematext.take();} catch(InterruptedException e) {} 
								this.insert_spaces_for_iteration("B");
								System.out.println("did not run it because run was not true at start, GOING BACK because best_next_ntc: "+best_next_ntc.print_list());
							semasematext.release();
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

				alpha3 = alpha3.memory_previous;


				checked_set = checked_set.memory_previous;
				checked_set.memory_next = null;
				if(nodes_to_consider.meta_data > 0){
					checked_set.add(nodes_to_consider.meta_data);
				}
				deepness--;
			}

			check_if_threads_are_done(false, mid_thread_ownership, mid_stillrunninglist, display_internal, depth_charge, max_star,mid_previous_nodes, mid_reach_back,true);


		}	

		check_if_threads_are_done(true, mid_thread_ownership, mid_stillrunninglist, display_internal, depth_charge, max_star,mid_previous_nodes, mid_reach_back,true);

		if(mid_entry){//finish threads
			check_if_threads_are_done(true, thread_ownership, stillrunninglist, display_internal, depth_charge, max_star,previous_nodes, reach_back,false);
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
			if(display)System.out.println("!!!!!comp_all_nodes: "+comp_all_nodes.print_list());
			if(display)System.out.println("!!!!!connected to: "+cycle[i]+" is temp element: "+temp_element.print_list()+" graph[55][57]"+graph[55][57]);

			temp_element.similar_differences(all_nodes_deleting, temp_unique, all_nodes_unique);
			common.use_me_and_not_first(all_nodes_unique, all_nodes_deleting);

			if(display)System.out.println("comparing node: "+cycle[i]+" connected to: "+temp_element.print_list()+" with unique: "+temp_unique.print_list());
			if(display)System.out.println("check_set connected to: "+all_nodes_deleting.print_list()+" with unique: "+all_nodes_unique.print_list());
			if(display)System.out.println("common nodes: "+common.print_list());



			//			all_nodes_unique = reduction(all_nodes_deleting, null, all_nodes_unique);
			//			if(display)System.out.println("all nodes unique reduced is: "+all_nodes_unique.print_list());

			if(common.get_length() != 0){
			
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


		return contains_check_set;
	}


	private void insert_spaces_for_iteration(String mode){
		if (mode == "B"){
			System.out.print("M:"+whoami+mid_previous_nodes[whoami].print_list());

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


	public String disp_time(){

		long t = System.currentTimeMillis();

		t=t/1000;
		long s = t%60;
		t=t/60;
		long m = t%60;
		t=t/60;
		long h = t%60+7;


		return h+":"+m+":"+s;
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