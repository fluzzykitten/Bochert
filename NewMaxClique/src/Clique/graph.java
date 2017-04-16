package Clique;

import java.io.*;
import java.util.Scanner;

//import Clique2.graph;

//import OldClique.node;


public class graph {

	private int[][] graph; // the adjacency matrix
	private node3[] graph3; // the adjacency matrix
	private int[][] old_graph; //when changing the graph around, can keep the old one to ensure that the returned set is indeed a clique
	private int nodes; // total number of nodes
	private int BK_iteration_deep = 0; // iterations deep into BronKerbosch
	private long BK_calls = 0; // calls to BronKerbosch
	private int B_iteration_deep = -1; //iterations deep into Bochert
	private long B_calls = 0; // calls to Bochert
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
	private int display_level = 0;
	private node3 empty_node;
	private int previous_depth = 0;
	private int count_down = 20;
	private gpu graphics_card;
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
	private long hotswap_trigger = -1;
	private boolean level_0_display = false;

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

	private void populate_results_array(node3 n){

		for(int i = 0; i<n.get_length(); i++){

			Bochert_neighbor(results_array[i], n.get_index(i), empty_node, n);

		}

		results_array_length = n.get_length();


	}

	private boolean deleteme(node3 deleteme, node3 compare, node3 deleted){

		node3 result = deleteme.copy_by_erasing();

		//		node3 new_deleteme = new node3(nodes);
		//		node3 new_compare = new node3(nodes);
		//		new_deleteme.copy_array(deleteme);
		//		new_compare.copy_array(deleteme);

		result.delete(compare);
		result.delete(deleted);

		if(result.get_length() == 0)
			return true;

		return false;
	}




	public boolean ox_redux(node3 memory_elementA, node2 relic_node2){

		boolean found_deletions = false;
		deleted_nodes.zero();//.set_length(0);

		populate_results_array(memory_elementA);
		results_array_length=memory_elementA.get_length();
		memory_elementA.to_old_node2(relic_node2);

		for(int i = 0; i<relic_node2.get_length(); i++){
			for(int j = 0; j<relic_node2.get_length(); j++){
				if((i!=j)&&(!deleted_nodes.find(relic_node2.get_full_array()[j]))){
					if(deleteme(results_array[i],results_array[j],deleted_nodes)){
						deleted_nodes.add(relic_node2.get_full_array()[i]);
						found_deletions = true;
						j = relic_node2.get_length();
					}
				}


			}

		}
		if(found_deletions){
			memory_elementA.delete(deleted_nodes);
		}


		return found_deletions;
	}	


	public boolean ox_redux_new(node3 memory_elementA, node3 puttyA, node3 puttyB, node2 relic_node2){

		boolean found_deletions = false;
		deleted_nodes.zero();//.set_length(0);

		populate_results_array(memory_elementA);
		results_array_length=memory_elementA.get_length();

		memory_elementA.to_old_node2(relic_node2);


		for(int i = 0; i<relic_node2.get_length(); i++){
			for(int j = i; j<relic_node2.get_length(); j++){

				results_array[i].similar_differences(results_array[j], puttyA, puttyB, deleted_nodes);

				if(puttyB.get_length() == 0){
					deleted_nodes.add(relic_node2.get_full_array()[j]);
					found_deletions = true;					
				}
				else if(puttyA.get_length() == 0){
					deleted_nodes.add(relic_node2.get_full_array()[i]);
					found_deletions = true;
					j = relic_node2.get_length();
				}



			}

		}
		if(found_deletions){
			memory_elementA.delete(deleted_nodes);
		}


		return found_deletions;
	}	
















	private void Bochert_neighbor(node3 result, int n, node3 array, node3 node_array){

		result.use_me_or(array, node_array);
		result.use_me_and(graph3[n-1], result);



		return;

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
				//				System.out.println("node: "+(i+1)+" connected to: "+graph3[i].print_literal());//.print_list());
			}

			level_0_display = display;
			//this.disp_graph();
			//			pause();

			node3 send = reduction(new node3(this.all_neighbors(-1),nodes),null,null);
			

			if(num_threads <= 0){
				return merge_sort(unreorganize_nodes(Newer_Bochert(send,0,nodes,display,empty_node,0)), all_neighbors(-1));
			}
			else{
				node3[] reach_back1 = new node3[num_threads];
				node3[] previous_nodes = new node3[num_threads];

				for(int i = 0; i<num_threads; i++){
					reach_back1[i] = new node3(nodes);
					previous_nodes[i] = new node3(nodes);
				}

				node3 find1 = new node3(this.all_neighbors(-1),nodes);
				long[] reach_back_B_calls1 = new long[1];
				int[] status = new int[num_threads];
				status[0] = 1;
				semaphore semasema = new semaphore();
				semaphore stillrunning = new semaphore();

				try{stillrunning.take();} catch(InterruptedException e){}

				//			bthread(node3[] reach_back1, int thread_pool1, node3[] graph31, int[][] graph1, node3 find1, boolean display1, int nodes1, long[] reach_back_B_calls1, int display_level1, node3 empty_node1, boolean degressive_display1, int whoami1, int[] status1, node3[] previous_nodes1, int thread_count1){
				Runnable task = new bthread(reach_back1, num_threads, graph3, graph, find1, display, nodes, reach_back_B_calls1, display_level, empty_node, degressive_display,0,status,previous_nodes,0,semasema,stillrunning);	
				Thread worker = new Thread(task);
				worker.setName("TOP_Thread");
				worker.start();

				//			while (worker.isAlive()) {
				try {
					stillrunning.take();
				} catch(InterruptedException e) {
				} 
				//			}

				B_calls = reach_back_B_calls1[0];
				return merge_sort(unreorganize_nodes(reach_back1[0]), all_neighbors(-1));
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


	private node3 Newer_Bochert(node3 all_nodes, int current_max, int sought_max, boolean show, node3 already_been_checked, int where_from){

		B_iteration_deep++;
		B_calls++;

		if(B_iteration_deep == 0)
			show = level_0_display;

		if(B_calls == hotswap_trigger){
			hotswap();
		}




		//all_nodes = reduction(all_nodes, null, null);//this is now in pre_bochert

		boolean display = (((where_from < 4)&&(level_0_display == true)&&(B_iteration_deep < (display_level+1)))?true:false);



		//		if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
		//			this.insert_spaces_for_iteration("B");
		//			System.out.println(">> B_calls: "+B_calls+" calling Bochert("+all_nodes.print_list()+" ,cm: "+current_max+" ,sm: "+sought_max+" , abc: "+(already_been_checked==null?"null":already_been_checked.print_list())+" );");
		//		}

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
		node3 deleted_all_nodes = new node3(nodes);
		node3 all_nodes_extra_extra = new node3(nodes);
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
		node3 memory_unique = new node3(nodes); 
		node3 element_unique = new node3(nodes);
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


		node3[] DCC = new node3[0];
		node3 alpha3 = new node3(nodes);

		int length_extra_alredy_been_checked = 0;

		//node3 comp_nodes = new node3(nodes);


		if(already_been_checked.get_length() > 0){



			already_been_checked.similar_differences(all_nodes, temp_element, memory_element);

			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("already been checked"+already_been_checked.print_list()+" all_nodes: "+all_nodes.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println("extra already been checked"+temp_element.print_list()+" extra all_nodes: "+memory_element.print_list());
			}


			if(memory_element.get_length() == 0){
				if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println("returning because mem elm has no distinct nodes");
				}
				//should never hit here tho...
				B_iteration_deep--;
				return new node3(nodes);
			}

			if(temp_element.get_length() == 0){
				already_been_checked.zero();
			}
			else{

				if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
					//			if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println("starting again, checked unique were: "+temp_element.print_list()+" and all_nodes unique were: "+memory_element.print_list());

					node3 delete1 = new node3(nodes);
					node3 delete2 = new node3(nodes);
					node3 delete1u = new node3(nodes);
					node3 delete2u = new node3(nodes);

					if(memory_element.get_length() > 1){
						Bochert_neighbor(delete1,memory_element.get_index(0),all_nodes);
						Bochert_neighbor(delete2,memory_element.get_index(1),all_nodes);

						delete1.similar_differences(delete2, delete1u, delete2u);

						this.insert_spaces_for_iteration("B");
						System.out.println("extra all node: "+memory_element.get_index(0)+" had extra nodes: "+delete1u.print_list()+" while extra all node: "+memory_element.get_index(1)+" had extra nodes: "+delete2u.print_list());
					}
				}


				TOP_nodes_to_consider.copy_array(memory_element);//.use_me_and_not_first(TOP_dont_consider_connected, all_nodes);
				nodes_to_consider = TOP_nodes_to_consider;//new node3(nodes);
				toptop = TOP_nodes_to_consider.pop_first();// get rid of toptop
				this.Bochert_neighbor(TOP_dont_consider_connected, toptop, all_nodes);


				temp_element2.copy_array(already_been_checked);
				temp = temp_element2.pop_first();
				this.Bochert_neighbor(temp_element,temp,temp_element2);
				temp_element.use_me_and_not_first(temp_element, temp_element2);
				temp_element.add(temp);//nodes to consider


				DCC = new node3[temp_element.get_length()+TOP_nodes_to_consider.get_length()+1];
				//alpha3 = new node3[temp_element.get_length()+TOP_nodes_to_consider.get_length()+1];
				for(int i = 0; i< DCC.length; i++){			
					DCC[i] = new node3(nodes);
					//alpha3[i] = new node3(nodes);
				}

				length_extra_alredy_been_checked = temp_element.get_length();
				for(int i = 0; i< (temp_element.get_length()); i++){			
					DCC[i].meta_data = temp_element.get_index(i);
					Bochert_neighbor(DCC[i], DCC[i].meta_data, temp_element2); 
					//temp_element2.delete(DCC[i].meta_data);<-no need to delete it, in fact, deleting it makes it less applicable
					DCC[i].meta_data = -1*DCC[i].meta_data;
					//alpha3[i].copy_array(DCC[i]);
					//alpha3[i].meta_data = DCC[i].meta_data;
				}


				DCC[length_extra_alredy_been_checked] = TOP_dont_consider_connected.copy_by_erasing();
				DCC[length_extra_alredy_been_checked].meta_data = toptop;
				all_nodes.delete(toptop);
				for(int i = 1; i< (TOP_nodes_to_consider.get_length()+1); i++){			
					DCC[i+length_extra_alredy_been_checked].meta_data = TOP_nodes_to_consider.get_index(i-1);
					Bochert_neighbor(DCC[i+length_extra_alredy_been_checked], DCC[i+length_extra_alredy_been_checked].meta_data, all_nodes); 
					all_nodes.delete(DCC[i+length_extra_alredy_been_checked].meta_data);
					//alpha3[i+length_extra_alredy_been_checked].copy_array(DCC[i+length_extra_alredy_been_checked]);
					//alpha3[i+length_extra_alredy_been_checked].meta_data = DCC[i+length_extra_alredy_been_checked].meta_data;
				}




				check_set = length_extra_alredy_been_checked;

				if(display){
					for(int a = 0; a < DCC.length; a++){
						this.insert_spaces_for_iteration("B");
						System.out.println("DCC["+a+"].md: "+DCC[a].meta_data);
					}
					this.insert_spaces_for_iteration("B");
					System.out.println();
					if(B_iteration_deep == 9){


					}
				}

			}
		}
		if(already_been_checked.get_length() == 0){		

			this.Bochert_neighbor(result, toptop, all_nodes);


			//it's connected to all the nodes
			if((result.get_length()+1)==all_nodes.get_length()){
				if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
					//			if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println(" NO  WHILE,  B_calls: "+B_calls+" toptop (which is: "+toptop+") connected to all other nodes (which are: "+result.print_list()+"), calling Bochert("+result.print_list()+" ,cm: "+(current_max==0?0:current_max-1)+" ,sm: "+(sought_max==0?0:sought_max-1)+" , abc: null; (ntc node was connected to all nodes)");
				}



				result = Newer_Bochert(result,(current_max==0?0:current_max-1),(sought_max==0?0:sought_max-1),display, empty_node,1);
				result.add(toptop);
				B_iteration_deep--;
				return result;
			}


			TOP_dont_consider_connected.copy_array(result);
			//all_nodes_in_set_deleted_used = TOP_dont_consider_connected;//new node3(nodes);
			TOP_nodes_to_consider.use_me_and_not_first(TOP_dont_consider_connected, all_nodes);
			TOP_nodes_to_consider.pop_first();// get rid of toptop
			nodes_to_consider = TOP_nodes_to_consider;//new node3(nodes);

			//alpha3 = new node3[TOP_nodes_to_consider.get_length()+1];
			//for(int i = 0; i< alpha3.length; i++){			
				alpha3 = new node3(nodes);
			//}

			//find DCCs
			DCC = new node3[TOP_nodes_to_consider.get_length()+1];
			DCC[0] = TOP_dont_consider_connected.copy_by_erasing();
			DCC[0].meta_data = toptop;
			//alpha3.copy_array(DCC[0]);
			//alpha3.meta_data = DCC[0].meta_data;
			all_nodes.delete(toptop);
			for(int i = 1; i< DCC.length; i++){			
				DCC[i] = new node3(nodes);
				DCC[i].meta_data = TOP_nodes_to_consider.get_index(i-1);
				Bochert_neighbor(DCC[i], DCC[i].meta_data, all_nodes); 
				//alpha3[i].copy_array(DCC[i]);
				//alpha3[i].meta_data = DCC[i].meta_data;
				all_nodes.delete(DCC[i].meta_data);
			}
		}


		if(display){
			for(int i = 0; i<DCC.length; i++){
				this.insert_spaces_for_iteration("B");
				System.out.println(" -- DCC["+i+"].md: "+DCC[i].meta_data+" and is: "+DCC[i].print_list());
			}
		}



		//		if(false){
		if(already_been_checked.get_length() == 0){

			if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
				//			if(display){
				this.insert_spaces_for_iteration("B");
				if(display)
					System.out.println(" TOP WHILE time: "+this.disp_time()+" B_calls: "+B_calls+" starting on, check set is "+(1+0)+" out of "+(DCC.length)+" which is node "+DCC[0].meta_data+" with DCC of: "+DCC[0].print_list()+" with no comp_set but current max of: "+max_star.meta_data);
				else
					System.out.println(" TOP WHILE time: "+this.disp_time()+" B_calls: "+B_calls+" starting on, check set is "+(1+0)+" out of "+(DCC.length)+" which is node "+DCC[0].meta_data+" with DCC size of: "+DCC[0].get_length()+" with no comp_set but current max of: "+max_star.meta_data);
			}


			if(max_star.meta_data <= DCC[0].get_length()){//or equal because if DCC[0].meta_data + DCC[0].get_length() are the nodes, which means one more than DCC[0].get_length()
				temp_element = Newer_Bochert(DCC[0].copy_by_erasing(), (max_star.meta_data==0?0:max_star.meta_data-1), nodes, display, empty_node,2);
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
			}
			else{
			}
		}


		if(display){
			this.insert_spaces_for_iteration("B");
			System.out.println("about to enter main  while loop, ntc: "+nodes_to_consider.print_list()+" Tntc: "+TOP_nodes_to_consider.print_list()+" length_extra_nodes_already_been_checked: "+length_extra_alredy_been_checked);
		}

		if(display){
			this.insert_spaces_for_iteration("B");
			System.out.println("ntc.gl: "+nodes_to_consider.get_length()+" length extra already been checked: "+length_extra_alredy_been_checked+" abc.gl: "+already_been_checked.get_length());
			this.insert_spaces_for_iteration("B");
			System.out.println(" DCC.gl: "+DCC.length);
		}


		while(check_set < (DCC.length)){	

			display = (((where_from < 4)&&(level_0_display == true)&&(B_iteration_deep < (display_level+1)))?true:false);
			
			
			Bochert_neighbor(all_nodes_extra_extra, DCC[check_set].meta_data, original_all_nodes);//the nodes that have already been checked can be used to eliminate unneeded nodes... in fact... I can do this at every level...
			all_nodes_extra_extra.use_me_and_not_first(DCC[check_set], all_nodes_extra_extra);//only extras
			all_nodes_in_set_deleted_used = reduction(DCC[check_set], all_nodes_extra_extra, null);//I will leave this reduction because it incorporates the completed nodes
			checked_set.copy_array(all_nodes_extra_extra);//remember these deleted nodes...

			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("At the top, deciding all_nodes for node: "+DCC[check_set].meta_data+", extra already deleted nodes connected to it is: "+all_nodes_extra_extra.print_list()+" and all_nodes before reduction was: "+DCC[check_set].print_list()+" and after reduction: "+all_nodes_in_set_deleted_used.print_list());
			}
			



			temp = 0;
			for(int i = 0; i< check_set; i++){

					memory_element.use_me_and_not_first(all_nodes_in_set_deleted_used, DCC[i]);

					if((i<check_set)&&(temp < (DCC[i].get_length()-memory_element.get_length()))){
						temp = (DCC[i].get_length()-memory_element.get_length());
						comp_set = i;
					}


			}
//			all_nodes_in_set_deleted_used.copy_array(DCC[check_set]);
			alpha3.copy_array(DCC[comp_set]);


			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Find Memory Element
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



			

//see if you can eliminate completely first
			all_others_empty = true;
			run = true;
//			for(int i = 0; i < check_set; i++){
				
				temp_element2.use_me_and_not_first(alpha3, all_nodes_in_set_deleted_used);//they're both the same depth, check that all_nodes isn't already contained in alpha3

				if(temp_element2.get_length() == 0){
					if(display){
						this.insert_spaces_for_iteration("B");
						System.out.println("RUN == FALSE!!! Don't run this NODE!!! Because comp_set: "+comp_set+" which is node "+DCC[comp_set].meta_data+" connected to: "+alpha3.print_list()+" contains check_set: "+check_set+" which is node "+DCC[check_set].meta_data+" connected to: "+all_nodes_in_set_deleted_used.print_list());
					}
					run = false;
//					i = check_set;
//				}
	
			}
			
			if(run){			
				unranked_find_best_ntc_dcc(alpha3,check_set, all_nodes_in_set_deleted_used, best_next_ntc, best_next_me);
				if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println("after unranked find best, best_next_ntc was: "+best_next_ntc.print_list()+" and best_next_me: "+best_next_me.print_list());
				}
				if(best_next_ntc.get_length() == 0){
					
					
					//shouldn't be run now that there is just one set
					ranked_find_best_ntc_dcc(alpha3,check_set, all_nodes_in_set_deleted_used, best_next_ntc, best_next_me);
//					if(display){
//						this.insert_spaces_for_iteration("B");
//						System.out.println("after ranked find best, best_next_ntc was: "+best_next_ntc.print_list()+" and best_next_me: "+best_next_me.print_list());
//					}
				}
			}
			else{
				best_next_ntc.zero();
				best_next_me.zero();
			}

			
			
			
			if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
				
				this.insert_spaces_for_iteration("B");
				System.out.println(" MAIN WHILE time: "+this.disp_time()+" B_calls: "+B_calls+" starting on, check set is "+(1+check_set)+" out of "+(DCC.length)+" which is node "+DCC[check_set].meta_data+" with the comp_set "+comp_set+" which is node "+DCC[comp_set].meta_data+" and common nodes are: "+best_next_me.print_list()+" max_star: "+max_star.print_list()+" max_star.md: "+max_star.meta_data);
				
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
				all_nodes_in_set_whole.copy_array(all_nodes_in_set_deleted_used);

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
					

				//wait until now to reduce them
/*				temp_element.use_me_or(best_next_me, best_next_ntc);//new all_nodes
					
				best_next_ntc = reduction(best_next_ntc, best_next_me);
				best_next_me = reduction(best_next_me, best_next_ntc);
				nodes_to_consider.copy_array(best_next_ntc);
				memory_element.copy_array(best_next_me);
				
				
				all_nodes_in_set_deleted_used.copy_array(temp_element);
				all_nodes_in_set_whole.copy_array(all_nodes_in_set_deleted_used);
				*/
					
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

				
				while(TOP_nodes_to_consider != nodes_to_consider){


					I_was_deleted = true;
					run = true;
					while(I_was_deleted && (nodes_to_consider.get_length() > 0)){

						nodes_to_consider.meta_data = nodes_to_consider.pop_first();
						all_nodes_in_set_deleted_used.delete(nodes_to_consider.meta_data);

						this.Bochert_neighbor(temp_element, nodes_to_consider.meta_data, all_nodes_in_set_deleted_used);
						//this.Bochert_neighbor(all_nodes_extra_extra, nodes_to_consider.meta_data, all_nodes_extra_extra);


						temp_element = reduction(temp_element, empty_node, null);

						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println("initial check of node: "+nodes_to_consider.meta_data+" which is connected to: "+temp_element.print_list());
						}
						
						if((max_star.meta_data>(temp_element.get_length()+deepness))){
							
							
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

						}
						else{
							
							I_was_deleted = this.deletable(nodes_to_consider.meta_data, all_nodes_in_set_whole, empty_node, false, temp_element);
							
//							if(!I_was_deleted){
//								I_was_deleted = this.deletable(nodes_to_consider.meta_data, alpha3, empty_node, false, temp_element);								
//							}
//								
							
							if(!I_was_deleted){
//							I_was_deleted = false;
							/*
							temp =  increment_alpha3_and_find_best_ntc_dcc(alpha3,check_set, temp_element, best_next_ntc, best_next_me);
								//return 0, I_was_deleted = true, run = true 
								//return 1, I_was_deleted = true, run = false
								//return 2, I_was_deleted = false, run = true
								//return 3, I_was_deleted = false, run = false
							if(temp == 0){
								I_was_deleted = true;
								run = true;
							}
							else if(temp == 1){
								I_was_deleted = true;
								run = false;
							}
							else if(temp == 2){
								I_was_deleted = false;
								run = true;
							}
							else{
								I_was_deleted = false;
								run = false;
							}
*/						


															
							
							all_others_empty = true;
								alpha3.memory_next = new node3(nodes);//DCC[i].copy_by_erasing();
								if(this.get_next_comp_all_nodes(alpha3.memory_next, alpha3, unused_best_next_me, unused_best_next_ntc, temp_element)){
									I_was_deleted = true;
								}
								alpha3.memory_next.memory_previous = alpha3;
								alpha3 = alpha3.memory_next;
								
								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println("alpha3["+0+"] was: "+alpha3.memory_previous.print_list()+" but it's now: "+alpha3.print_list());
								}

								
								if(all_others_empty && (alpha3.get_length() != 0)){
									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("all_others_empty is not false... so don't run it down yet");
									}
									all_others_empty = false;
								}
							

							if(I_was_deleted){
									alpha3 = alpha3.memory_previous;
										
							}
							else{	
								//temp_element = reduction(temp_element, empty_node, null);//already been done
								
							unranked_find_best_ntc_dcc(alpha3,check_set, temp_element, best_next_ntc, best_next_me);
							if(best_next_ntc.get_length() == 0){
								ranked_find_best_ntc_dcc(alpha3,check_set, temp_element, best_next_ntc, best_next_me);//this is to just use temp_element before it gets corrupted
							}

							//temp_element.use_me_or(best_next_ntc, best_next_me);
							
							//wait until now to reduce them
							//best_next_ntc = reduction(temp_element, null, best_next_ntc);
							//best_next_me = reduction(best_next_me, best_next_ntc);
							}
							}

						}

						

						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println("I_was_deleted: "+I_was_deleted);
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

							
							if(all_others_empty){

								run = scour_checked_set(deepness, TOP_checked_set, DCC[check_set], TOP_nodes_to_consider, memory_element, all_nodes_in_set_deleted_used, all_nodes_in_set_whole, alpha3, temp_element, check_set);
								//temp_element.zero();
								
								
								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println(">> B_calls: "+B_calls+" run: "+run+" calling Bochert("+memory_element.print_list()+" ,cm: "+(max_star.meta_data-deepness<1?0:max_star.meta_data-deepness-1)+"(aka: max_star is: "+max_star.print_list()+") ,sm: "+nodes+" , abc: "+temp_element.print_list()+"; ");
								}

								if(run && (max_star.meta_data-deepness-1<=0?0:max_star.meta_data-deepness-1) >= memory_element.get_length()){
									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("in run loop, in second number check, but run now false because ms.md ("+max_star.meta_data+") - deepness ("+deepness+" -1 >= me.gl"+memory_element.get_length()+" which is the same thing as all_nodes");
									}

									run=false;
								}


								if(run)
									Pointer_ONLY = Newer_Bochert(memory_element/*will corrupt this pointer*/, (max_star.meta_data-deepness-1<1?0:max_star.meta_data-deepness-1), nodes, display, temp_element,3);
								else
									Pointer_ONLY = empty_node;

								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println(">> returned with: "+Pointer_ONLY.print_list()+" FYI tho, just_a_pointer.get_length: "+Pointer_ONLY.get_length()+" deepness: "+deepness+" <?> max_star.md: "+max_star.meta_data+" and fyi, empty node: "+empty_node.print_list());
								}


								if((Pointer_ONLY.get_length()+deepness)>=max_star.meta_data){
									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("found new max star!! te.gl: "+Pointer_ONLY.print_list()+" deepness: "+deepness+" previous max_star.md: "+max_star.meta_data);
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



	private boolean scour_checked_set(int deepness, node3 TOP_checked_set, node3 DCC_check_set, node3 TOP_nodes_to_consider, node3 memory_element, node3 all_nodes_in_set_deleted_used, node3 all_nodes_in_set_whole, node3 comp_nodes,node3 returning_already_been_checked_set, int check_set){
		//this is an exhaustive search, so this function is probably exponential. It will neeed to be changed before final implimentation to use either a greedy algoritm, or call Bochert back to try and find if the extra nodes that were removed from DCC can form a star "deepness" big
		//System.out.println("starting Scour_checked_set");

		node3 pointer_top_checked_set = TOP_checked_set.memory_next; //deepness is 1
		int pointer_top_checked_set_depth = 1;
		node3 checkcheck = pointer_top_checked_set.copy_by_erasing();
		int checkcheck_depth = 1;
		checkcheck.meta_data = 0;
		returning_already_been_checked_set.zero();

		node3 already = DCC_check_set.copy_by_erasing(); 

		node3 already_unique = new node3(nodes);
		node3 nodes_to_consider_pointer = TOP_nodes_to_consider.memory_next;
		node3 memory_unique = new node3(nodes);
		node3 sillyputty = new node3(nodes);

		boolean run = true;
		int temp = nodes;
		int temp_extra = -1;

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//    check first all_nodes_in_set_deleted_used which is all the nodes that the current node is connected to and will be used in the next iteration
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		/*		int[] disposable = all_nodes_in_set_deleted_used.to_int();//nodes that ntc.md is connected to
		if(run){
			for(int s = 0; s<disposable.length; s++){
				this.Bochert_neighbor(sillyputty, disposable[s], all_nodes_in_set_whole.memory_previous);//look at previous...

				memory_element.similar_differences(sillyputty, memory_unique, already_unique);
				memory_unique = reduction(memory_unique, memory_element);

				if(memory_unique.get_length() == 0){
					run = false;
					s = disposable.length;
				}
				else if(memory_unique.get_length() == (1)){
					//special case - so you know that this node (disposable[s]) is connected to this node (ntc.md), so if the node that this is missing (the +1) is (worst case) part of the biggest clique, and it's the only biggest clique, now you'll return a clique of size max-1, but you take into account that this same size will be found by looking at another node that is also connected to this node (ntc.md), finding thus the same size start (Ms-1)+1, again, the +1 refers to disposable[s] because you know that it will be part of the star
					run = false;
					s = disposable.length;
				}
				else if(memory_unique.get_length() < temp){
					//sillyputty.meta_data = disposable[s];
					returning_already_been_checked_set.copy_array(sillyputty);
					temp = memory_unique.get_length();
					temp_extra = sillyputty.get_length();

				}
				else if(memory_unique.get_length() == temp){
					if(already_unique.get_length() > temp_extra){
						//sillyputty.meta_data = disposable[s];
						returning_already_been_checked_set.copy_array(sillyputty);
						temp = memory_unique.get_length();
						temp_extra = sillyputty.get_length();
					}
				}
			}
		}



		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//    check comp_all_nodes for a good comparison
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		//now look at comp_all_nodes and see if it's better than the other options

		if(true && run){

			for(int i = 0; i<check_set; i++){

				memory_element.similar_differences(comp_nodes[i], memory_unique, already_unique);
				memory_unique = reduction(memory_unique, memory_element);

				if(memory_unique.get_length() == 0){
					run = false;
					i = check_set;
				}
				else if(memory_unique.get_length() < temp){
					returning_already_been_checked_set.copy_array(comp_nodes[i]);
					//sillyputty.meta_data = -1;
					temp = memory_unique.get_length();
					temp_extra = already_unique.get_length();
				}
				else if(memory_unique.get_length() == temp){
					if(already_unique.get_length() > temp_extra){
						//sillyputty.meta_data = -1;
						returning_already_been_checked_set.copy_array(comp_nodes[i]);
						temp = memory_unique.get_length();
						temp_extra = already_unique.get_length();
					}
				}
				//								}
			}
		}

		 */


		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//    check all previously deleted nodes for comparison
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		pointer_top_checked_set_depth = 1;
		while (true && run && pointer_top_checked_set_depth <= deepness){

			//			System.out.println("blap_depth: "+blap_depth+" deepness: "+deepness);

			checkcheck = pointer_top_checked_set.copy_by_erasing();
			checkcheck_depth = pointer_top_checked_set_depth;
			checkcheck.meta_data = 0;

			pointer_top_checked_set_depth++;
			pointer_top_checked_set = pointer_top_checked_set.memory_next;

			//			System.out.println("checkcheck is: "+checkcheck.print_list());


			while((checkcheck.meta_data < checkcheck.get_length())){

				Bochert_neighbor(sillyputty, checkcheck.get_index(checkcheck.meta_data), already);
				//				System.out.println("considering with node: "+checkcheck.get_index(checkcheck.meta_data)+" of nodes: "+already.print_list()+" it is connected to: "+sillyputty.print_list());

				for(int i = 0; i< (deepness - checkcheck_depth); i++){
					get_next_comp_all_nodes(sillyputty, sillyputty,new node3(nodes), new node3(nodes),all_nodes_in_set_deleted_used);
					//					System.out.println("going once deeper, now it's connected to: "+sillyputty.print_list());
				}

				memory_element.similar_differences(sillyputty, memory_unique, already_unique);

				//				System.out.println("memory_unique: "+memory_unique.print_list()+" already_uniq: "+already_unique.print_list());


				//checkcheck_depth == deepness, used greedy algorithm to get here		
				//if(checkcheck_depth == deepness){//obviously must be true, a relic from previous version
				//					System.out.println("correct depth now");
				if(memory_unique.get_length() == 0){
					//						System.out.println("memory_unique.get_length() == 0");
					run = false;
					//checkcheck.meta_data = checkcheck.get_length();
					pointer_top_checked_set_depth = deepness+1;
					return run;
				}
				else if(memory_unique.get_length() < temp){
					//						System.out.println("memory_unique.get_length() < temp");
					returning_already_been_checked_set.copy_array(sillyputty);
					temp = memory_unique.get_length();
					temp_extra = already_unique.get_length();
				}
				else if(memory_unique.get_length() == temp){
					//						System.out.println("memory_unique.get_length() == temp");
					if(already_unique.get_length() > temp_extra){
						//							System.out.println("already_unique.get_length() > temp_extra");
						returning_already_been_checked_set.copy_array(sillyputty);
						temp = memory_unique.get_length();
						temp_extra = already_unique.get_length();
					}
				}											
				//}
				checkcheck.meta_data++;
				//					System.out.println("checkcheck.md is now: "+checkcheck.meta_data);
			}

			Bochert_neighbor(already,nodes_to_consider_pointer.meta_data, already); 
			nodes_to_consider_pointer = nodes_to_consider_pointer.memory_next;

		}

		
		//		System.out.println("done with run: "+run);
		return run;
	}

	
	private int increment_alpha3_and_find_best_ntc_dcc(node3[] alpha3,int check_set, node3 check_all_nodes, node3 best_ntc, node3 best_dcc){
		//return 0, I_was_deleted = true, run = true 
		//return 1, I_was_deleted = true, run = false
		//return 2, I_was_deleted = false, run = true
		//return 3, I_was_deleted = false, run = false

		boolean I_was_deleted = false;
		boolean run = true;

		int comp_set = -1;
		node3 best_next_ntc = new node3(nodes);
		best_next_ntc.invert();
		node3 best_next_me = new node3(nodes);
		best_next_me.invert();
		node3 memory_element = new node3(nodes);
		node3 memory_unique = new node3(nodes);
		node3 temp_element = check_all_nodes.copy_by_erasing();

		for(int i = 0; i < check_set/*alpha3.length*/; i++){
			memory_element.zero();
			memory_unique.zero();

			memory_unique.use_me_and_not_first(alpha3[i], temp_element);

			if(memory_unique.get_length() == 0){
				alpha3[i].memory_next = new node3(nodes);
				alpha3[i].memory_next.memory_previous = alpha3[i];
				alpha3[i] = alpha3[i].memory_next;

				if(temp_element.get_length() == alpha3[i].get_length()){//alpha3 has no unique nodes and they're the same length - egro, they're the same
					I_was_deleted = true;
					run = false;
				}

			}
			else{
				memory_unique.zero();//go ahead and reset this...

				if(!I_was_deleted){
					alpha3[i].memory_next = new node3(nodes);
					if( get_next_comp_all_nodes(alpha3[i].memory_next, alpha3[i],memory_element, memory_unique,temp_element))
						I_was_deleted = true;
				}
				else
					alpha3[i].memory_next = new node3(nodes);
				alpha3[i].memory_next.memory_previous = alpha3[i];
				alpha3[i] = alpha3[i].memory_next;

				//if all of the alphas are now zero length, then none of the bests were resolved and therefore this set should be run
				if(run && (alpha3[i].get_length()!=0)){
					run = false;
				}


				if((!I_was_deleted)&&(alpha3[i].get_length() != 0)){//if alpha3 is zero, then no real "best"s were actually anything
					if(memory_unique.get_length() < best_next_ntc.get_length()){
						comp_set = i;
						best_next_ntc.copy_array(memory_unique);
						best_next_me.copy_array(memory_element);
					}
					else if(memory_unique.get_length() == best_next_ntc.get_length()){
						if(memory_element.get_length() < best_next_me.get_length()){
							comp_set = i;
							best_next_ntc.copy_array(memory_unique);
							best_next_me.copy_array(memory_element);										
						}
					}
				}
			}
		}

		if(I_was_deleted){
			for(int i = 0; i < check_set/*alpha3.length*/; i++){
				alpha3[i] = alpha3[i].memory_previous;
			}	


		}

		
		best_ntc.copy_array(best_next_ntc);
		best_dcc.copy_array(best_next_me);
		
		

		
if(I_was_deleted){
	if(run){
		return 0;
	}
	else{
		return 1;
	}
}
else
	if(run){
		return 2;
	}
	else{
		return 3;
	}
			
		//return 0, I_was_deleted = true, run = true 
		//return 1, I_was_deleted = true, run = false
		//return 2, I_was_deleted = false, run = true
		//return 3, I_was_deleted = false, run = false

	
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
		int[] inner_cycle;
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

	
	private boolean ranked_find_best_ntc_dcc(node3 alpha3,int check_set, node3 check_all_nodes, node3 best_ntc, node3 best_dcc){

		if(check_all_nodes.length == 0){
			best_ntc.zero();
			best_dcc.zero();
			return false;
		}
		
		int[] connected_totals = new int[nodes];
		System.out.println("this is a relic function, now alpha3 has only one, not multiple");
		System.out.println(connected_totals[-1]);
		node3 temp = new node3(nodes);
		int[] cycle;
		int sum = 0;
		int best_sum = -1;
		int node_with_best_sum = -1;
		int[] inner_cycle;
		node3 dcc = new node3(nodes);
		
		for(int i = 0; i<check_set; i++){
			cycle = alpha3.to_int();
			
			for(int j = 0; j<cycle.length; j++){
				connected_totals[cycle[j]-1]++;
			}
		}
		
		
		cycle = check_all_nodes.to_int();
		for(int i = 0; i<cycle.length; i++){
			Bochert_neighbor(dcc, cycle[i], check_all_nodes);//dcc
			temp.use_me_and_not_first(dcc, check_all_nodes);//ntc
			
			inner_cycle = temp.to_int();

			sum = 0;			
			for(int j = 0; j<inner_cycle.length; j++){//cycle through all not connected nodes
				sum += (check_set - connected_totals[inner_cycle[j]-1]);//how many - how many have it connected = how many don't have it connected
			}
			sum += dcc.get_length() *(check_set>>>1); // connected nodes times half of the number of nodes, so each connected nodes gets half points

			if(sum > best_sum){
				best_sum = sum;
				node_with_best_sum = cycle[i];
			}
		}

		
			Bochert_neighbor(best_dcc, node_with_best_sum, check_all_nodes);
			best_ntc.use_me_and_not_first(best_dcc, check_all_nodes);
			best_ntc.add(node_with_best_sum);
		
		
		return false;
	}
	
	

	private boolean get_next_comp_all_nodes(node3 next, node3 comp_all_nodes, node3 best_me, node3 best_ntc, node3 all_nodes_deleting){

		boolean display = false;//(B_calls >= 17)?true:false;
		boolean contains_check_set = false;


		if(display)System.out.println("In get_next_comp_all_nodes, comp_all_nodes: "+comp_all_nodes.print_list()+" all_nodes_deleting: "+all_nodes_deleting.print_list());

		if(comp_all_nodes.get_length() == 0){
			if(display)System.out.println("returning because comp_all_nodes is zero length");
			next.zero();
			best_me.zero();
			best_ntc.zero();
			return contains_check_set;
		}

		node3 temp_element = new node3(nodes);
		node3 temp_extra = new node3(nodes);
		node3 temp_unique = new node3(nodes);
		node3 all_nodes_unique = new node3(nodes);
		node3 common = new node3(nodes);

		temp_extra.use_me_and_not_first(all_nodes_deleting, comp_all_nodes);

		if(temp_extra.get_length() == 0){
			if(display)System.out.println("returning because comp_all_nodes has no nodes that are not already in all_nodes_deleting");
			next.zero();
			best_me.zero();
			best_ntc.zero();
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
				best_ntc.copy_array(all_nodes_unique);
				best_me.copy_array(common);
			}
			else if(minimizing_unique_all_nodes == all_nodes_unique.get_length()){ 
				if(temp_unique.get_length() > extra){//guarnteed better
					if(display)System.out.println("min_unique is equal to all_nodes_unique, but temp_unique is greater than extra");
					minimizing_unique_all_nodes = all_nodes_unique.get_length();
					extra = temp_unique.get_length();
					node = cycle[i];
					best_ntc.copy_array(all_nodes_unique);
					best_me.copy_array(common);
				}
			}

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
		
		
		if(display)System.out.println("returning, node "+node+" won"+" and best_ntc: "+best_ntc.print_list()+" best_me: "+best_me.print_list()+" and temp_extra: "+temp_extra.print_list());
		
		
		return contains_check_set;
	}






	private void insert_spaces_for_iteration(String mode){
		if (mode == "B"){
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

		if((BK_calls %100000 == 0))
			System.out.println("BK is on call number: "+BK_calls);


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

	public graph(String file_name){

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
			File myFile = new File("..\\graph_binaries\\"+file_name);
			FileReader fileReader = new FileReader(myFile);

			BufferedReader reader = new BufferedReader(fileReader);

			String line;

			int count = 0, total_nodes, node1, node2;


			while (((line = reader.readLine()) != null) && (line.charAt(0) != 'p')){
				System.out.println(line);
			}

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
				if (graph[i][j] == 1){
					sum++;
					edges++;
				}
			}
			node_edge_count[i] = sum;
		}

		old_graph = graph;


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


	public static void main(String args[]) throws Exception
	{



		//int[] isstar = {2,4,6,8,10,11,13,14,19,30,32,36,39,40,45,48,49,54,57,59,63,66,69,70,75,77,81,83,87,90,93,96,98,101,103,106,110,113,115,118,121,124,128,130,133,138,141,144,147,150,153,156,159,162,165,168,171,174,177,180,183,186,189,192,195,198,201,204,207,210,213,216,219,222,225,228,231,234,237,240,243,245,249,252,254,257,261,264,267,270,272,276,279,282,285,288,291,294,297,300,303,306,309,312,315,316,321,324,325,329,333,336,339,342,343,348,351,352,357,360,363,366,369,372,375,378};
		//int[] isstar = {26,47,54,69,104,119,120,134,144,148,157,182};
		//int[] isstar = {2,4,6,8,10,11,13,14,19,30,32,36,39,40,45,48,49,54,57,59,63,66,69,70,75,77,81,83,87,90,93,96,98,101,103,106,110,113,115,118,121,124,128,130,133,138,141,144,147,150,153,156,159,162,165,168,171,174,177,180,183,186,189,192,195,198,201,204,207,210,213,216,219,222,225,228,231,234,237,240,243,245,249,252,254,257,261,264,267,270,272,276,279,282,285,288,291,294,297,300,303,306,309,312,315,316,321,324,325,329,333,336,339,342,343,348,351,352,357,360,363,366,369,372,375,378};


		System.out.println("Hello?");

		int[][] testie={{0,1,0,0,0,0,1,1,0,0,1},
				{1,0,1,0,0,0,1,1,0,0,0},
				{0,1,0,1,1,1,0,1,0,0,0},
				{0,0,1,0,1,1,0,0,0,0,0},
				{0,0,1,1,0,1,0,1,1,0,0},
				{0,0,1,1,1,0,0,0,1,0,0},
				{1,1,0,0,0,0,0,1,1,1,1},
				{1,1,1,0,1,0,1,0,1,1,1},
				{0,0,0,0,1,1,1,1,0,1,1},
				{0,0,0,0,0,0,1,1,1,0,1},
				{1,0,0,0,0,0,1,1,1,1,0}};
		//			graph g = new graph(testie);

		int[][] testie1={	
				{0,1,1,1,1,1,1},
				{1,0,1,1,1,1,1},
				{1,1,0,1,1,1,1},
				{1,1,1,0,1,1,1},
				{1,1,1,1,0,1,1},
				{1,1,1,1,1,0,1},
				{1,1,1,1,1,1,0}};

		int[][] testie2={	{0,0,1,1,1,1,0},
				{0,0,1,1,1,1,1},
				{1,1,0,1,1,1,1},
				{1,1,1,0,1,1,1},
				{1,1,1,1,0,1,1},
				{1,1,1,1,1,0,1},
				{0,1,1,1,1,1,0}};

		int[][] testie3={
				{0,1,1,1,1,1,1,1,1,0},
				{1,0,1,1,1,1,1,1,0,1},
				{1,1,0,1,1,1,1,0,1,1},
				{1,1,1,0,1,1,0,1,1,1},
				{1,1,1,1,0,0,1,1,1,1},
				{1,1,1,1,0,0,1,1,1,1},
				{1,1,1,0,1,1,0,1,1,1},
				{1,1,0,1,1,1,1,0,1,1},
				{1,0,1,1,1,1,1,1,0,1},
				{0,1,1,1,1,1,1,1,1,0}};

		int[][] testie4={
				{1,0,1,0,1,0,1,1,1},
				{0,1,0,1,0,1,1,1,1},
				{1,0,1,0,1,0,1,1,1},
				{0,1,0,1,0,1,1,1,1},
				{1,0,1,0,1,0,1,1,1},
				{0,1,0,1,0,1,1,1,1},
				{1,1,1,1,1,1,0,1,1},
				{1,1,1,1,1,1,1,0,1},
				{1,1,1,1,1,1,1,1,0}};


		int[][] testie5={
				//1,2,3,4,5,6,7,8,9,0,1,2,3,4
				{0,0,0,0,0,0,1,1,0,1,1,1,1,1},//1
				{0,0,1,0,0,1,1,1,1,0,0,0,1,1},//2
				{0,1,0,1,0,1,1,1,0,0,0,0,0,0},//3
				{0,0,1,0,0,0,0,0,0,0,0,0,0,0},//4
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0},//5
				{0,1,1,0,0,0,1,1,1,0,0,0,0,0},//6
				{1,1,1,0,0,1,0,1,1,1,1,1,0,0},//7
				{1,1,1,0,0,1,1,0,1,1,1,1,0,0},//8
				{0,1,0,0,0,1,1,1,0,0,0,0,0,0},//9
				{1,0,0,0,0,0,1,1,0,0,0,0,0,0},//0
				{1,0,0,0,0,0,1,1,0,0,0,0,0,0},//1
				{1,0,0,0,0,0,1,1,0,0,0,0,0,0},//2
				{1,1,0,0,0,0,0,0,0,0,0,0,0,1}, //3
				{1,1,0,0,0,0,0,0,0,0,0,0,1,0} //4
		};
		int[][] testie5b={
				//1,2,3,4,5,6,7,8,9,0,1,2,3,4
				{0,0,0,0,0,0,1,1,0,1,1,1,1,1},//1
				{0,0,1,0,0,1,1,1,1,0,0,0,1,1},//2
				{0,1,0,1,0,1,0,1,0,0,0,0,0,0},//3
				{0,0,1,0,0,0,0,0,0,0,0,0,0,0},//4
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0},//5
				{0,1,1,0,0,0,1,1,0,0,0,0,0,0},//6
				{1,1,0,0,0,1,0,1,0,1,1,1,0,0},//7
				{1,1,1,0,0,1,1,0,0,1,1,1,0,0},//8
				{0,1,0,0,0,0,0,0,0,0,0,0,0,0},//9
				{1,0,0,0,0,0,1,1,0,0,0,0,0,0},//0
				{1,0,0,0,0,0,1,1,0,0,0,0,0,0},//1
				{1,0,0,0,0,0,1,1,0,0,0,0,0,0},//2
				{1,1,0,0,0,0,0,0,0,0,0,0,0,1}, //3
				{1,1,0,0,0,0,0,0,0,0,0,0,1,0} //4
		};

		int[][] testie6={
				//				{1,2,3,4,5,6,7},
				{0,1,0,0,1,0,1},
				{1,0,1,1,1,0,1},
				{0,1,0,1,1,1,1},
				{0,1,1,0,0,0,1},
				{1,1,1,0,0,1,1},
				{0,0,1,0,1,0,1},
				{1,1,1,1,1,1,0}};

		int[][] testie7={
				//1,2,3,4,5,6,7,8,9,0,1,2,3,4
				{0,1,1,0,0,0,0},//1
				{1,0,1,0,0,0,0},//2
				{1,1,0,1,0,0,0},//3
				{0,0,1,0,1,0,0},//4
				{0,0,0,1,0,1,0},//5
				{0,0,0,0,1,0,1},//6
				{0,0,0,0,0,1,0},//7
		};

		int[][] testie8={
				{0,0,0,0,0,0,1,1,1,1},
				{0,0,0,1,0,0,1,1,1,1},
				{0,0,0,1,1,1,1,1,1,1},
				{0,1,1,0,0,0,1,1,1,1},
				{0,0,1,0,0,1,1,1,1,1},
				{0,0,1,0,1,0,1,1,1,1},
				{1,1,1,1,1,1,0,1,1,1},
				{1,1,1,1,1,1,1,0,1,1},
				{1,1,1,1,1,1,1,1,0,1},
				{1,1,1,1,1,1,1,1,1,0}
		};

		graph g = new graph(testie8);

		/*node3 TOP_checked_set = new node3(10);
		TOP_checked_set.memory_next = new node3(10);//deepness 1
		TOP_checked_set.memory_next.add(1);//added to deepness 1
		TOP_checked_set.memory_next.add(2);//added to deepness 1
		TOP_checked_set.memory_next.memory_next = new node3(10);//deepness 2
		TOP_checked_set.memory_next.memory_next.add(1);//added to deepness 1
		TOP_checked_set.memory_next.memory_next.memory_next = new node3(10);//deepness 3

		node3 DCC_check_set = new node3(10);
		DCC_check_set.add(1); DCC_check_set.add(2); DCC_check_set.add(3); DCC_check_set.add(4); DCC_check_set.add(5); 
		DCC_check_set.add(6); DCC_check_set.add(7); DCC_check_set.add(8); DCC_check_set.add(9); DCC_check_set.add(10); 

		node3 TOP_nodes_to_consider = new node3(10);
		TOP_nodes_to_consider.memory_next = new node3(10);//deepness 1
		TOP_nodes_to_consider.memory_next.meta_data = 1;
		TOP_nodes_to_consider.memory_next.memory_next = new node3(10);//deepness 2
		TOP_nodes_to_consider.memory_next.memory_next.meta_data = 7;
		TOP_nodes_to_consider.memory_next.memory_next.memory_next = new node3(10);//deepness 3
		TOP_nodes_to_consider.memory_next.memory_next.memory_next.meta_data = 8;

		node3 memory_element = new node3(10);
		memory_element.add(3);
		memory_element.add(4);
		memory_element.add(5);
		memory_element.add(6);

		g.graph3 = new node3[g.nodes];
		for(int i = 0; i<g.nodes; i++){
			g.graph3[i] = new node3(g.graph[i],g.nodes,true);
			//				System.out.println("node: "+(i+1)+" connected to: "+graph3[i].print_literal());//.print_list());
		}


		g.scour_checked_set(3, TOP_checked_set, DCC_check_set, TOP_nodes_to_consider, memory_element);
		 */

		long start;
		int [] temp;
		long elapsedTimeMillis;

		/*		g.graph3 = new node3[g.nodes];
		for(int i = 0; i<g.nodes; i++){
			g.graph3[i] = new node3(g.graph[i],g.nodes,true);
		}


		node3 all_nodes = new node3(g.all_neighbors(-1),g.nodes);

		System.out.println(">> all_nodes was: "+all_nodes.print_list());
		g.reduction(all_nodes);
		System.out.println(">> all_nodes is now: "+all_nodes.print_list());
		g.pause();
		 */

		String s[] = new String[35];

		s[0] = "brock200_1.clq";
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

		for(int i = 1; i<s.length; i++){//i<s.length; i++){
			if (((i == -1) || (i != 18)) && (i == 31) && (i != 19)){// && (i != 18) && (i != 19)){
				System.out.println("***********************************************************************************************************");
				System.out.println("graph#"+i+" "+s[i]);
				g = new graph(s[i]);
				//g = new graph(testie8);

				if(i>=0)
					g.display_level = 0;
				else if (i == 31)
					g.display_level = 4;
				else if (i == 32)
					g.display_level = 0;
				else if (i == 34)
					g.display_level = 2;
				else if ((i > 31))
					g.display_level = 4;
				else 
					g.display_level = 6;


				g.timings[0] = 0;
				g.timings[1] = 0;
				g.timings[2] = 0;


				System.out.println("Number of nodes: "+g.nodes);


				System.out.println();
				System.out.println();
				System.out.println("AND NOW THE NEWER VERSION");
				g.start_showing_crap = false;
				g.sort_smallest_first = false;
				g.sort = true;
				g.degressive_display = false;
				g.num_threads = 0;
				g.hotswap_trigger = -17;//115//116//1986;//12063; //-34530;//35105


				start = System.currentTimeMillis();
				g.B_calls = 0;
				g.B_calls_background = 0;
				//				temp = g.pre_New_Bochert(false,g.nodes);
				temp = g.pre_Newer_Bochert(false);

				elapsedTimeMillis = System.currentTimeMillis()-start;

				System.out.println();
				System.out.println("max clique from un-optimized Bochert is: ");
				System.out.println(g.array2string(temp));
				System.out.println("total calls to Bochert: "+g.B_calls);
				System.out.println("background calls to Bochert were: "+g.B_calls_background);
				System.out.println("background to foreground were: "+((double)g.B_calls_background)/((double)g.B_calls));

				System.out.println("__ it took:"+elapsedTimeMillis+" miliseconds");

				System.out.println("is star?: "+g.is_star(temp, true)+" and length is: "+temp.length);

				System.out.println();


				for(int ii=0; ii<3; ii++){
					System.out.println("timing "+ii+": "+(g.timings[ii]/1e6)+" for: "+g.timings2[ii]);
				}

				
/*			System.out.println();
				System.out.println("pooface");
				int[] pooface = {1,65,132,171};
				node3 pooface2 = new node3(pooface,g.nodes);
				int[] peeface = {1,65,132,171};
				node3 peeface2 = new node3(peeface,g.nodes);

				pooface2 = peeface2;
				
				if(pooface2==peeface2){
					System.out.println("equal");
				}
				else{
					System.out.println("not equal");
				}

				/*				node2 ntemp = new node2(temp);
				for(int a = 1; a<=g.nodes; a++){
					ntemp.add(a);
					if(g.is_star(ntemp.get_array_min_size(), true)){
						System.out.println(a+" was a succex");
					}
					ntemp.delete(a);
				}
				 */			}
		}




	}

}
