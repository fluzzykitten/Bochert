package Clique;

import java.io.*;

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
	private boolean trigger = false;
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


	private boolean already_checked_with_out_memory(node2 already_checked, node2 array, node2 putty){

		for(int i = 0; i < already_checked.get_length(); i++){
			if (this.additional_independant_check(already_checked.get_full_array()[i], array, putty)){
				return true;
			}

		}

		return false;
	}


	private boolean additional_independant_check(int checked_node, node2 array, node2 putty){
		//true == ind set
		//false == not ind set
		//assuming they can't be null



		//		int[] difference = new int[array.get_length()];
		putty.set_length(0);
		//		int index_difference = 0;

		for(int j = 0; j<array.get_length(); j++){

			if(graph[checked_node-1][array.get_full_array()[j]-1] != 1){

				for(int i = 0; i<putty.get_length(); i++){
					if(graph[putty.get_full_array()[i]-1][array.get_full_array()[j]-1] == internal_connected){
						return false;// not a star
					}
				}

				putty.add_to_end(array.get_full_array()[j]);
				//				index_difference++;

			}

		}


		return true;
	}

	private int check_extra_nodes(node3 base, node3 next, boolean display, int deep){
		//return 1 - delete base
		//return 2 - do nothing
		//return 3 - delete next


		//		putty.set_length(0);

		node3 base_uniq = new node3(nodes);
		node3 next_uniq = new node3(nodes);

		base.similar_differences(next, base_uniq, next_uniq);

		//		System.out.println(" turns out that base has the extra nodes: "+base_uniq.print_list()+" and next has the extra nodes: "+next_uniq.print_list());

		int index_base = 0;
		int index_next = 0;
		int recursion = 0;

		node2 result = new node2(nodes);

		node2 replacement = new node2(nodes);

		boolean delete_next = true;
		boolean delete_base = true;

		//		int numnum = 0;

		node3 base_temp = new node3(nodes);
		node3 next_temp = new node3(nodes);

		node3 base_temp_uniq = new node3(nodes);
		node3 next_temp_uniq = new node3(nodes);

		if(next_uniq.get_length()==0){
			delete_base = false;
		}
		else if(base_uniq.get_length() == 0){
			delete_next = false;
		}

		if(display){
			this.insert_spaces_for_iteration("B");
			System.out.println(deep+"in check_extra_nodes");
			System.out.println(deep+"base uniq("+base_uniq.get_length()+"): "+base_uniq.print_list()+"next uniq("+next_uniq.get_length()+"): "+next_uniq.print_list());
		}		

		//	while (numnum != next_uniq.get_length()){
		for(int n = 0; n < next_uniq.get_length(); n++){


			this.Bochert_neighbor(next_temp, next_uniq.get_index(n), empty_node, next);

			for(int i = 0; i<base_uniq.get_length(); i++){

				this.Bochert_neighbor(base_temp, base_uniq.get_index(i), empty_node, base);

				base_temp.similar_differences(next_temp, base_temp_uniq, next_temp_uniq);

				if(next_temp_uniq.get_length() == 0){
					next.delete(next_uniq.get_index(n));
					next_uniq.delete(next_uniq.get_index(n));
					n--;
					i=base_uniq.get_length();
				}
				else if(base_temp_uniq.get_length() == 0){
					base.delete(base_uniq.get_index(i));
					base_uniq.delete(base_uniq.get_index(i));
					i--;
					//					i=base_uniq.get_length();
				}
				else if((base_temp_uniq.get_length() == 1)&&(next_temp_uniq.get_length() == 1)){

					recursion = check_extra_nodes(base_temp, next_temp, display,deep+1);

					if(recursion == 3){
						next.delete(next_uniq.get_index(n));
						next_uniq.delete(next_uniq.get_index(n));
						n--;
						i=base_uniq.get_length();
					}
					else if(recursion == 1){
						base.delete(base_uniq.get_index(i));
						base_uniq.delete(base_uniq.get_index(i));
						i--;
						//					i=base_uniq.get_length();
					}

				}
				//				else if(i==(base_uniq.get_length()-1)){//tricky tricky, so if it was on the last one and the other ifs didn't match, it'll hit this, but iff they didn't fall to either of the previous conditions
				//					 delete_next = false;					 
				//				 }


			}

		}

		if(next_uniq.get_length() == 0){
			delete_next = true;
		}
		else
			delete_next = false;


		if(base_uniq.get_length() == 0){
			delete_base = true;
		}
		else
			delete_base = false;


		if(delete_next){
			if(display && B_iteration_deep <= 30){
				this.insert_spaces_for_iteration("B");
				System.out.println(">base_uniq("+base_uniq.get_length()+"): "+base_uniq.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println(">next_uniq("+next_uniq.get_length()+"): "+next_uniq.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println(">Returning: "+3);

			}
			return 3;
		}
		if(delete_base){
			if(display && B_iteration_deep <= 30){
				this.insert_spaces_for_iteration("B");
				System.out.println(">base_uniq("+base_uniq.get_length()+"): "+base_uniq.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println(">next_uniq("+next_uniq.get_length()+"): "+next_uniq.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println(">Returning: "+1);

			}
			return 1;
		}
		if(display && B_iteration_deep <= 30){
			this.insert_spaces_for_iteration("B");
			System.out.println(">base_uniq("+base_uniq.get_length()+"): "+base_uniq.print_list());
			this.insert_spaces_for_iteration("B");
			System.out.println(">next_uniq("+next_uniq.get_length()+"): "+next_uniq.print_list());
			this.insert_spaces_for_iteration("B");
			System.out.println(">Returning: "+2);

		}

		return 2;



		/*		for(int i = 0; i < next_uniq.get_length(); i++){

			Bochert_neighbor(result,next_uniq.get_full_array()[i],empty_node, next, internal_connected);

			if(this.is_there_another(result, base, empty_node, replacement,false)){

			}
			else{
				return false;
			}

		}
return true;
		 */			

	}

	private int[] translate(int[] pre){

		int[] post = new int[pre.length];

		for(int i = 0; i<pre.length; i++){
			post[i] = index_ordered_nodes[pre[i]-1]+1;
		}

		//		System.out.println(array2string(post));
		System.out.println(is_star(post,true));

		return post;

	}

	public int[] pre_New_Bochert(boolean display, int stop){


		int[] all_nodes = all_neighbors(-1);


		//		return New_Bochert(new node2(all_nodes), 0, new node2(),false).get_array_min_size();

		sort_nodes();
		reorganize_nodes();

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
		}

		//		this.New_Bochert(nodesA, current_max, current_max_starA, nodesB, current_max_starB, display)
		return merge_sort(unreorganize_nodes(New_Bochert(new node3(all_nodes, nodes), 0, new node3(nodes),new node3(nodes), new node3(nodes),display,stop)), all_neighbors(-1));


	}

	private int[] translate_real_to_sorted(int a[]){
		int[] b = new int[a.length];

		for(int i = 0; i<a.length; i++){
			b[i] = nodes_ordered_increasing[a[i]-1];
		}

		return b;
	}



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


	private boolean deleteme(node2 deleteme, node2 compare, node2 deleted){

		int index_deleteme = 0;
		int index_compare = 0;
		int index_deleted = 0;

		//look to see if all nodes in deleteme are in deleted
		while((index_deleteme < deleteme.get_length())&&(index_compare < compare.get_length())){
			while((index_deleted < deleted.get_length())&&(deleted.get_full_array()[index_deleted] < deleteme.get_full_array()[index_deleteme])){
				index_deleted++;
			}

			if(deleteme.get_full_array()[index_deleteme] < compare.get_full_array()[index_compare]){
				if((index_deleted < deleted.get_length())&&(deleted.get_full_array()[index_deleted] == deleteme.get_full_array()[index_deleteme])){
					index_deleted++;
					index_deleteme++;
				}
				else{
					return false;
				}
			}
			else if(compare.get_full_array()[index_compare] < deleteme.get_full_array()[index_deleteme]){
				index_compare++;
			}
			else{
				index_compare++;
				index_deleteme++;
			}
		}

		if(index_compare == compare.get_length()){

			while(index_deleteme < deleteme.get_length()){

				while((index_deleted < deleted.get_length())&&(deleted.get_full_array()[index_deleted] < deleteme.get_full_array()[index_deleteme])){
					index_deleted++;
				}

				if((index_deleted < deleted.get_length())&&(deleted.get_full_array()[index_deleted] == deleteme.get_full_array()[index_deleteme])){
					index_deleted++;
					index_deleteme++;
				}
				else{
					return false;
				}

			}

			/*while((index_deleteme < deleteme.get_length())&&((index_deleted < deleted.get_length())&&(deleted.get_full_array()[index_deleted] == deleteme.get_full_array()[index_deleteme]))){
				index_deleted++;
				index_deleteme++;
			}*/
		}

		if(index_deleteme == deleteme.get_length())
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

	private void add_to_memory_end(node3 TOP, node3 element){

		if(TOP.memory_previous == null){
			TOP.memory_next = element;
			TOP.memory_previous = element;
			element.memory_next = element;
			element.memory_previous = element;
		}
		else{
			element.memory_next = TOP.memory_next;
			element.memory_previous = TOP.memory_previous;
			TOP.memory_previous.memory_next = element;
			TOP.memory_next.memory_previous = element;
			TOP.memory_previous = element;
		}

	}

	private void add_to_memory_end(node2 TOP, node2 element){

		if(TOP.memory_previous == null){
			TOP.memory_next = element;
			TOP.memory_previous = element;
			element.memory_next = element;
			element.memory_previous = element;
		}
		else{
			element.memory_next = TOP.memory_next;
			element.memory_previous = TOP.memory_previous;
			TOP.memory_previous.memory_next = element;
			TOP.memory_next.memory_previous = element;
			TOP.memory_previous = element;
		}

	}


	private void find_remnant(node2 TOP_remnant, node3 TOP_remnant_connected, node3 nodes_stationary, node3 nodes_transitory, int current_node){

		node3 stationary_uniq = new node3(nodes);
		node3 transitory_uniq = new node3(nodes);

		node2 new_remnant = new node2(nodes); 
		new_remnant.add_to_end(current_node);

		node3 new_remnant_connected = new node3(nodes);


		nodes_stationary.similar_differences(nodes_transitory, stationary_uniq, transitory_uniq);




	}

	private void reduce_consideration2(node3 nodes_to_considerA, node3 dont_consider_connectedA, node3 temp_elementA, node3 memory_elementA, node3 run_meA, node2 relic_node2){

		node2 TOP_remnant = new node2();
		node3 TOP_remnant_connected = new node3();

		run_meA.add(nodes_to_considerA.pop_first());
		int current_node = 0;

		while(nodes_to_considerA.get_length() > 0){//first one already pulled off

			current_node = nodes_to_considerA.pop_first();
			this.Bochert_neighbor(memory_elementA, current_node, nodes_to_considerA, dont_consider_connectedA);
			find_remnant(TOP_remnant, TOP_remnant_connected, dont_consider_connectedA, memory_elementA, current_node);

		}





	}




	private void reduce_consideration(node3 nodes_to_considerA, node3 dont_consider_connectedA, node3 temp_elementA, node3 memory_elementA, node3 run_meA, node2 relic_node2){

		int current_nodeA = 0;
		boolean another;
		int numnum;
		int result;
		int temp_numnum;



		while (nodes_to_considerA.get_length() > 0){

			current_nodeA = nodes_to_considerA.pop_first();

			Bochert_neighbor(memory_elementA, current_nodeA, dont_consider_connectedA, nodes_to_considerA);

			//System.out.println("outer loop looking at cnA: "+current_nodeA+" and ntcA.l: "+nodes_to_considerA.get_length()+" and meA: "+memory_elementA.get_length());


			another = false;

			numnum = 0;
			//numnum = nodes_to_consider.get_length();
			while(numnum < nodes_to_considerA.get_length()){
				//for(int i = 0; i<nodes_to_consider.get_length(); i++){
				temp_numnum = nodes_to_considerA.get_index(numnum);

				Bochert_neighbor(temp_elementA, temp_numnum , dont_consider_connectedA, nodes_to_considerA);							



				//another = is_there_another(memory_element, nodes_to_consider, dont_consider_connected, temp_element_P, false);
				result = check_extra_nodes(memory_elementA, temp_elementA,false,0);

				//System.out.println("inner loop looking at numnum: "+numnum+" and tnn: "+temp_numnum+" and teA("+temp_elementA.get_length()+"): "+temp_elementA.print_list()+" result: "+result+" ntcA("+nodes_to_considerA.get_length()+"): "+nodes_to_considerA.print_list()+" dccA("+dont_consider_connectedA.get_length()+"): "+dont_consider_connectedA.print_list());


				if(result == 3){
					if(graph[current_nodeA-1][temp_numnum-1] == 0){
						nodes_to_considerA.delete(temp_numnum);
					}
					else{
						dont_consider_connectedA.add(temp_numnum);
						nodes_to_considerA.delete(temp_numnum);
					}
				}
				else if (result == 1){


					current_nodeA = temp_numnum;
					nodes_to_considerA.delete(temp_numnum);
					numnum = 0;

					Bochert_neighbor(memory_elementA, current_nodeA, dont_consider_connectedA, nodes_to_considerA);
					//					dont_consider_connectedA.copy_array(memory_elementA);

					memory_elementA.invert();
					temp_elementA.use_me_or(dont_consider_connectedA, nodes_to_considerA);
					temp_elementA.use_me_and(temp_elementA, memory_elementA);
					memory_elementA.invert();

					//System.out.println("meA("+memory_elementA.get_length()+"): "+memory_elementA.print_list()+" teA("+temp_elementA.get_length()+"): "+temp_elementA.print_list()+" ntcA("+nodes_to_considerA.get_length()+"): "+nodes_to_considerA.print_list()+" dcc("+dont_consider_connectedA.get_length()+"): "+nodes_to_considerA.print_list());

					//					temp_elementA.add(current_nodeA);


					//					Bochert_neighbor(memory_elementA, current_nodeA, dont_consider_connectedA, nodes_to_considerA);
					//					Bochert_neighbor(temp_elementA, current_nodeA, dont_consider_connectedA, nodes_to_considerA, internal_not_connected);
					if(nodes_to_considerA.get_length() > temp_elementA.get_length()){

						dont_consider_connectedA.copy_array(memory_elementA);
						nodes_to_considerA.copy_array(temp_elementA);

						for(int k = 0; k<run_meA.get_length(); k++){
							if(graph[run_meA.get_index(k)-1][current_nodeA-1]==0){
								nodes_to_considerA.add(run_meA.get_index(k));
							}
							else{
								dont_consider_connectedA.add(run_meA.get_index(k));
							}

						}
						run_meA.zero();


					}


					//another = true;
					//break;
				}
				else{
					//							System.out.println("nope, node "+nodes_to_consider.get_full_array()[numnum]+" has to stay in ntc");
					numnum++;
				}

			}


			run_meA.add(current_nodeA);

		}
	}


	private void cross_check_considerations(node3 run_meA, node3 dont_consider_connectedA, node3 memory_elementA, node3 run_meB, node3 dont_consider_connectedB, node3 memory_elementB){

		int current_nodeA = 0;
		int current_nodeB = 0;
		int result;

		while (current_nodeA < run_meA.get_length()){

			current_nodeB = 0;
			Bochert_neighbor(memory_elementA, run_meA.get_index(current_nodeA), dont_consider_connectedA, run_meA);

			while(current_nodeB < run_meB.get_length()){

				Bochert_neighbor(memory_elementB, run_meB.get_index(current_nodeB), dont_consider_connectedB, run_meB);

				result = check_extra_nodes(memory_elementA, memory_elementB,false,0);

				if(result == 3){//delete next
					run_meB.delete(run_meB.get_index(current_nodeB));
				}
				else if (result == 1){
					run_meA.delete(run_meA.get_index(current_nodeA));
					current_nodeB = run_meB.get_length();
					current_nodeA--;
				}
				else{
					current_nodeB++;
				}

			}

			current_nodeA++;

		}
	}


	private boolean find_next_set_to_examine(node3 nodes_to_considerA, node3 nodes_to_considerB, node2 current_nodeA, node3 memory_elementA, node3 temp_connected_starA, node3 temp_connected_nodesA, node3 dont_consider_connectedA, node3 dont_consider_connectedB, node3 current_max_starA, node3 current_max_starB, node3 head_max_star, boolean first, node2 relic_node2, node3 puttyA, node3 puttyB){

		/*		this.insert_spaces_for_iteration("B");
		if(first){
			System.out.print(" top first ");
		}else{
			System.out.print(" top second ");
		}
		System.out.println("ntcA: "+nodes_to_considerA.print_list()+" ntcB: "+nodes_to_considerB.print_list()+" cn: "+current_nodeA.get_last()+" me: "+memory_elementA.print_list()+" tcn: "+temp_connected_nodesA.print_list()+" tcs: "+temp_connected_starA.print_list()+" dccA: "+dont_consider_connectedA.print_list()+" dccB: "+dont_consider_connectedB.print_list());
		 */		
		if(((nodes_to_considerA == null) || (nodes_to_considerA.get_length() == 0))&&((nodes_to_considerB == null) || (nodes_to_considerB.get_length() == 0))){
			//should be dead code, just so code would correctly match inverse that is in the next chunk

			current_nodeA.add_to_end(-1);
			memory_elementA.zero();//.set_length(0);
			temp_connected_nodesA.zero();//.set_length(0);
			temp_connected_starA.zero();//.set_length(0);

			return false;

		}
		else{
			if((nodes_to_considerA != null) && (nodes_to_considerA.get_length() > 0)){

				current_nodeA.add_to_end(nodes_to_considerA.pop_first());
				//				nodes_to_considerA.delete(current_nodeA.get_last());

				Bochert_neighbor(memory_elementA, current_nodeA.get_last(), dont_consider_connectedA, nodes_to_considerA);

				if(first)
					memory_elementA.meta_data = 1;
				else
					memory_elementA.meta_data = 2;

				memory_elementA.side = 'A';

			}
			//else if((nodes_to_considerB != null) && (nodes_to_considerB.get_length() > 0)){
			else{

				current_nodeA.add_to_end(nodes_to_considerB.pop_first());
				//			nodes_to_considerB.delete(current_nodeA.get_last());

				Bochert_neighbor(memory_elementA, current_nodeA.get_last(), dont_consider_connectedB, nodes_to_considerB);
				if(first)
					memory_elementA.meta_data = 1;
				else
					memory_elementA.meta_data = 2;

				memory_elementA.side = 'B';

			}


			/*			this.insert_spaces_for_iteration("B");
			if(first){
				System.out.print(" midA first ");
			}else{
				System.out.print(" midA second ");
			}
			System.out.println("ntcA: "+nodes_to_considerA.print_list()+" ntcB: "+nodes_to_considerB.print_list()+" cn: "+current_nodeA.get_last()+" me: "+memory_elementA.print_list()+" tcn: "+temp_connected_nodesA.print_list()+" tcs: "+temp_connected_starA.print_list()+" dccA: "+dont_consider_connectedA.print_list()+" dccB: "+dont_consider_connectedB.print_list());
			 */

			ox_redux(memory_elementA,relic_node2);
			//			ox_redux_new(memory_elementA,puttyA,puttyB,relic_node2);

			/*			this.insert_spaces_for_iteration("B");
			if(first){
				System.out.print(" midB first ");
			}else{
				System.out.print(" midB second ");
			}
			System.out.println("ntcA: "+nodes_to_considerA.print_list()+" ntcB: "+nodes_to_considerB.print_list()+" cn: "+current_nodeA.get_last()+" me: "+memory_elementA.print_list()+" tcn: "+temp_connected_nodesA.print_list()+" tcs: "+temp_connected_starA.print_list()+" dccA: "+dont_consider_connectedA.print_list()+" dccB: "+dont_consider_connectedB.print_list());
			 */


			if(memory_elementA.get_length() < head_max_star.meta_data){

				memory_elementA.zero();//.set_length(0);
				temp_connected_nodesA.zero();//.set_length(0);
				temp_connected_starA.zero();//.set_length(0);		

				return false;

			}
			else{


				temp_connected_nodesA.copy_array(memory_elementA);
				if(first)
					nodes_to_considerA.side = memory_elementA.side;
				else
					nodes_to_considerB.side = memory_elementA.side;
				//temp_connected_nodesA = memory_elementA.copy_by_erasing();


				if(memory_elementA.side == head_max_star.side){
					Bochert_neighbor(temp_connected_starA, current_nodeA.get_last(), empty_node, head_max_star.memory_previous);
				}
				if(memory_elementA.side == 'A'){
					Bochert_neighbor(temp_connected_starA, current_nodeA.get_last(), empty_node, current_max_starA);
				}
				else{
					Bochert_neighbor(temp_connected_starA, current_nodeA.get_last(), empty_node, current_max_starB);
				}


				temp_connected_nodesA.delete(temp_connected_starA);

			}

		}

		/*		this.insert_spaces_for_iteration("B");
		if(first){
			System.out.print(" bot first ");
		}else{
			System.out.print(" bot second ");
		}
		System.out.println("ntcA: "+nodes_to_considerA.print_list()+" ntcB: "+nodes_to_considerB.print_list()+" cn: "+current_nodeA.get_last()+" me: "+memory_elementA.print_list()+" tcn: "+temp_connected_nodesA.print_list()+" tcs: "+temp_connected_starA.print_list()+" dccA: "+dont_consider_connectedA.print_list()+" dccB: "+dont_consider_connectedB.print_list());
		 */

		return true;
	}

	private node3 New_Bochert(node3 nodesA, int current_max, node3 current_max_starA_ext, node3 nodesB, node3 current_max_starB_ext, boolean display, int stop){

		//not original Bochert


		if ((nodesA.get_length() == 0)&&(nodesB.get_length() == 0))
			return (current_max_starA_ext.get_length()>=current_max_starB_ext.get_length()?current_max_starA_ext:current_max_starB_ext);


		node3 head_max_star = new node3(nodes);
		/*		if ((current_max_starA_ext.get_length() == 0)&&(current_max_starB_ext.get_length() == 0)){
			head_max_star = new node3();
		}
		else{
			if(current_max_starA_ext.get_length()>=current_max_starB_ext.get_length()){
				head_max_star = current_max_starA_ext;
				head_max_star.side = 'A';
			}
			else{
				head_max_star = current_max_starB_ext;
				head_max_star.side = 'B';
			}

		}*/
		head_max_star.meta_data = current_max;

		node3 TOP_nodes_to_considerA = new node3();
		node3 TOP_nodes_to_considerB = new node3();
		node3 TOP_dont_consider_connectedA = new node3();
		node3 TOP_dont_consider_connectedB = new node3();
		//		node2 TOP_puttyA = new node2();
		//		node2 TOP_puttyB = new node2();
		//node2 TOP_temp_max = new node2();
		node3 TOP_head_max_star = new node3(nodes);
		node2 current_nodeA = new node2(nodes);
		node2 current_nodeB = new node2(nodes);
		node3 TOP_current_max_starA = new node3();
		node3 TOP_current_max_starB = new node3();

		TOP_current_max_starA.memory_next = current_max_starA_ext;
		TOP_current_max_starB.memory_next = current_max_starB_ext;

		node3 current_max_starA = TOP_current_max_starA.memory_next;
		node3 current_max_starB = TOP_current_max_starB.memory_next;



		node3 temp_max = new node3();

		node2 relic_node2 = new node2(nodes);
		//int temp_current_max = current_max;
		//		int current_nodeA;
		//		int current_nodeB;
		//		int node_that_found_max_star = -1;
		int meta_data = 0;


		B_calls++;
		B_iteration_deep++;


		node3 nodes_to_considerA = nodesA;
		node3 nodes_to_considerB = nodesB;


		node3 memory_elementA;
		node3 memory_elementB;
		node3 temp_elementA;
		node3 temp_elementB;
		node3 puttyA;
		node3 puttyB;
		node3 temp_connected_nodesA = new node3(); //will be passed down the chain and thus might be corrupted
		node3 temp_connected_nodesB = new node3(); //will be passed down the chain and thus might be corrupted
		node3 temp_connected_starA;//= new node2(nodesA.get_length()+current_max_starA.get_length()); //will be passed down the chain but won't be corrupted
		node3 temp_connected_starB;//= new node2(nodesA.get_length()+current_max_starA.get_length()); //will be passed down the chain but won't be corrupted


		memory_elementA = new node3(nodes); //will not be passed and will not be corrupted
		memory_elementB = new node3(nodes); //will not be passed and will not be corrupted
		temp_elementA = new node3(nodes); //will be protected
		temp_elementB = new node3(nodes); //will be protected
		puttyA = new node3(nodes);
		puttyB = new node3(nodes);
		temp_connected_starA= new node3(nodes); //will be passed down the chain but won't be corrupted
		temp_connected_starB= new node3(nodes); //will be passed down the chain but won't be corrupted

		node3 dont_consider_connectedA = current_max_starA.copy_by_erasing();
		node3 dont_consider_connectedB = current_max_starB.copy_by_erasing();

		int current = 0;

		//		int[] super_star = {108,20,73,18,93,178,90,94,142,135,186,87,81,150,92,39,68,102,85,136,134};
		//		int[] super_star = {1,2,4,5,10,11,15,18,24,30,33,36,39,42,45,48,51,54,57,60,63,66,67,71,74,78,80,84,87,90,93,96,97,102,104,108,111,114,117,120,123,126,129,132,135,138,140,144,147,150,153,156,159,162,165,168,171,174,177,180,183,185,189,191,195,198,201,204,205,210,212,216,219,222,225,228,231,234,237,240,243,246,249,252,255,258,261,264,267,270,273,276,279,282,285,288,291,292,297,298,303,306,309,312,314,318,321,324,327,330,333,336,339,342,345,348,351,354,357,360,363,366,367,372,375,376};
		//		int[] super_star = {378,30,32,34,37,40,43,46,49,52,55,58,61,65,68,70,74,77,79,82,85,88,93,94,97,100,103,106,109,112,115,118,121,124,127,130,133,136,140,142,145,148,151,154,157,160,163,166,169,172,176,178,181,184,187,190,193,196,199,202,205,208,211,214,217,220,224,226,230,232,237,238,241,244,247,250,253,256,261,262,267,268,272,274,277,280,283,286,289,292,295,298,301,304,307,310,313,316,319,322,325,328,331,334,337,340,343,346,349,352,355,358,363,364,368,370,373,4,10,13,17,18,23,24,26,27};
		//		int[] super_star = {7,17,21,28,42,46,55,86,97,117,138,143,149,154,162,167,171,176,179,187,199};
		//		int[] super_star = {187,176,185,196,194,192,184,180,172,171,165,162,7,17,21,28,42,46,55,86,97,117,138,143,149,154,167,179,199};
		//		int[] super_star = {4,26,32,48,58,62,79,83,103,108,116,122,131,138,144,145,176,179,185,190};
		//		56 85 191
		//		int[] super_star = {200,199,173,190,183,175,178,133,168,157,137,148,136,93,47,66,76,91,186,195};
		//int[] super_star = {1,2,4,5,34,214,286,10,11,16,100,115,121,22,118,   37,40,45,47,55,60,67,74,78,80,84,92,95,106,110,114,135,136,140,145,148,152,156,163,167,172,177,181,184,189,191,199,203,210,212,219,225,230,233,237,240,245,249,255,261,266,269,272,275,352,357,362,365,378};

		boolean disp = false;
		boolean next_set1 = true;
		boolean next_set2 = true;

		before = System.nanoTime();

		if(nodes_to_considerA.get_length() > 0){

			current = nodes_to_considerA.pop_first();

			Bochert_neighbor(memory_elementA, current, dont_consider_connectedA, nodes_to_considerA);
			dont_consider_connectedA.copy_array(memory_elementA);

			memory_elementA.invert();
			temp_elementA.use_me_or(dont_consider_connectedA, nodes_to_considerA);
			temp_elementA.use_me_and(temp_elementA, memory_elementA);
			temp_elementA.add(current);

			nodes_to_considerA.copy_array(temp_elementA);
		}
		else{

		}

		if(nodes_to_considerB.get_length() > 0){
			current = nodes_to_considerB.pop_first();

			Bochert_neighbor(memory_elementB, current, dont_consider_connectedB, nodes_to_considerB);
			dont_consider_connectedB.copy_array(memory_elementB);

			memory_elementB.invert();
			temp_elementB.use_me_or(dont_consider_connectedB, nodes_to_considerB);
			temp_elementB.use_me_and(temp_elementB, memory_elementB);
			temp_elementB.add(current);

			nodes_to_considerB.copy_array(temp_elementB);

		}
		else{

		}

		after = System.nanoTime();
		timings[0] = timings[0] + (after-before);
		timings2[0] = "finding initial ntcA/ntcB and dccA/dccB";


		node3 run_meA = new node3(nodes);
		node3 run_meB = new node3(nodes);



		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


		before = System.nanoTime();

		reduce_consideration(nodes_to_considerA, dont_consider_connectedA, temp_elementA, memory_elementA, run_meA, relic_node2);

		reduce_consideration(nodes_to_considerB, dont_consider_connectedB, temp_elementB, memory_elementB, run_meB, relic_node2);

		cross_check_considerations(run_meA, dont_consider_connectedA, memory_elementA, run_meB, dont_consider_connectedB, memory_elementB);


		nodes_to_considerA.copy_array(run_meA);
		nodes_to_considerB.copy_array(run_meB);
		run_meA.zero();//.set_length(0);
		run_meB.zero();//.set_length(0);

		after = System.nanoTime();
		timings[1] = timings[1] + (after-before);
		timings2[1] = "running reduce_consideration and cross check";


		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		TOP_nodes_to_considerA.set_memory_next(nodes_to_considerA);
		TOP_nodes_to_considerB.set_memory_next(nodes_to_considerB);
		TOP_dont_consider_connectedA.set_memory_next(dont_consider_connectedA);
		TOP_dont_consider_connectedB.set_memory_next(dont_consider_connectedB);
		//TOP_temp_max.set_memory_next(temp_max);
		TOP_head_max_star.set_memory_next(head_max_star);
		head_max_star.memory_previous = TOP_head_max_star;

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	



		//		while((nodes_to_considerA.get_length() != 0)||(nodes_to_considerB.get_length() != 0)){
		//		while((!((TOP_nodes_to_considerA.get_memory_next().get_length() == 0)&&(TOP_nodes_to_considerB.get_memory_next().get_length() == 0))&&(TOP_head_max_star != head_max_star))){//((current_nodeA.get_length() == 0)&&(current_nodeB.get_length() == 0)))){
		while(TOP_head_max_star != head_max_star){

			/*			System.out.println("ntcA("+nodes_to_considerA.get_length()+"): "+nodes_to_considerA.print_list());
			System.out.println("dccA("+dont_consider_connectedA.get_length()+"): "+dont_consider_connectedA.print_list());
			System.out.println("ntcB("+nodes_to_considerB.get_length()+"): "+nodes_to_considerB.print_list());
			System.out.println("dccB("+dont_consider_connectedB.get_length()+"): "+dont_consider_connectedB.print_list());
			System.out.println("run_meA("+run_meA.get_length()+"): "+run_meA.print_list()+" run_meB("+run_meB.get_length()+"): "+run_meB.print_list());
//			pause();
			 */


			if(current_nodeA.get_length() > B_iteration_deep){
				this.insert_spaces_for_iteration("B");
				System.out.println("It has exceeded expectation: "+current_nodeA.get_length());
				//pause();
			}


			if (((nodes_to_considerA.get_length()+dont_consider_connectedA.get_length()) <= current_max)){
				nodes_to_considerA.zero();//.set_length(0);
			}

			if (((nodes_to_considerB.get_length()+dont_consider_connectedB.get_length()) <= current_max)){
				nodes_to_considerB.zero();//.set_length(0);
			}

			//			if ((nodes_to_considerA.get_length() == 0)&&(nodes_to_considerB.get_length() == 0)){

			//				B_iteration_deep--;
			//				return head_max_star;

			//			}


			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


			if((nodes_to_considerA.get_length() != 0)||(nodes_to_considerB.get_length() != 0)){	

				before = System.nanoTime();


				next_set1 = find_next_set_to_examine(nodes_to_considerA, nodes_to_considerB, current_nodeA, memory_elementA, temp_connected_starA, temp_connected_nodesA, dont_consider_connectedA, dont_consider_connectedB, current_max_starA, current_max_starB, head_max_star, true, relic_node2, puttyA, puttyB);

				next_set2 = find_next_set_to_examine(nodes_to_considerA, nodes_to_considerB, current_nodeB, memory_elementB, temp_connected_starB, temp_connected_nodesB, dont_consider_connectedA, dont_consider_connectedB, current_max_starA, current_max_starB, head_max_star, false, relic_node2, puttyA, puttyB);

				after = System.nanoTime();
				timings[2] = timings[2] + (after-before);
				timings2[2] = "find_next_set_to_examine";

				if((!next_set1)&&(!next_set2)){

					current_nodeA.delete_last();
					current_nodeB.delete_last();
					//	this.insert_spaces_for_iteration("B");
					//	System.out.println("deleted due to unnecessary creation");
				}
				//				if (!((memory_elementA.get_length() == 0) && (memory_elementB.get_length() == 0) && head_max_star.meta_data == 0))
				else if(((head_max_star.meta_data != 0)&&((head_max_star.meta_data+B_iteration_deep) >= stop))||
						((head_max_star.meta_data == 0)&&((head_max_star.meta_data+B_iteration_deep+1) >= stop))){
					//					pause();
					//					temp_connected_nodesA.length = 0;
					//					temp_connected_nodesB.length = 0;
					memory_elementA.length = 0;
					memory_elementB.length = 0;

					temp_connected_nodesA.zero();//.set_length(0);
					temp_connected_starA.zero();//.set_length(0);
					temp_connected_nodesB.zero();//.set_length(0);
					temp_connected_starB.zero();//.set_length(0);

					nodes_to_considerA.length = 0;
					nodes_to_considerB.length = 0;

					if (!((memory_elementA.get_length() == 0) && (memory_elementB.get_length() == 0) && head_max_star.meta_data == 0)){
						//						System.out.println(" deleting");
						current_nodeA.delete_last();
						current_nodeB.delete_last();

					}


				}

			}



			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			//			if((B_iteration_deep != 0)&&(current_nodeB.get_full_array()[B_iteration_deep-1] == 500))
			//				display = true;
			//			else
			//				display = false;

			if((display)||(B_iteration_deep <= display_level)){
				System.out.print(">>");
				this.insert_spaces_for_iteration("B");

				if((!next_set1)&&(!next_set2))
					System.out.println(" cnA and cnB were deleted B_calls: "+B_calls+" meA: "+memory_elementA.get_length()+" tcnA: "+temp_connected_nodesA.get_length()+" tcsA: "+temp_connected_starA.get_length()+" ntcA: "+nodes_to_considerA.get_length()+" dcA: "+dont_consider_connectedA.get_length()+" tcsA: "+temp_connected_starA.get_length()+" tcnA: "+temp_connected_nodesA.get_length()+" meB: "+memory_elementB.get_length()+" tcnB: "+temp_connected_nodesB.get_length()+" tcsB: "+temp_connected_starB.get_length()+" ntcB: "+nodes_to_considerB.get_length()+" dcB: "+dont_consider_connectedB.get_length()+" tcsB: "+temp_connected_starB.get_length()+" tcnB: "+temp_connected_nodesB.get_length()+" hms.gl: "+head_max_star.get_length()+" hms.md: "+head_max_star.meta_data);
				else
					System.out.println(" cnA: "+current_nodeA.get_last()+" cnB: "+current_nodeB.get_last()+" B_calls: "+B_calls+" meA: "+memory_elementA.get_length()+" tcnA: "+temp_connected_nodesA.get_length()+" tcsA: "+temp_connected_starA.get_length()+" ntcA: "+nodes_to_considerA.get_length()+" dcA: "+dont_consider_connectedA.get_length()+" tcsA: "+temp_connected_starA.get_length()+" tcnA: "+temp_connected_nodesA.get_length()+" meB: "+memory_elementB.get_length()+" tcnB: "+temp_connected_nodesB.get_length()+" tcsB: "+temp_connected_starB.get_length()+" ntcB: "+nodes_to_considerB.get_length()+" dcB: "+dont_consider_connectedB.get_length()+" tcsB: "+temp_connected_starB.get_length()+" tcnB: "+temp_connected_nodesB.get_length()+" hms.gl: "+head_max_star.get_length()+" hms.md: "+head_max_star.meta_data);
				//				System.out.println("adding to hms, cnA: "+current_nodeA.print_list());
			}
			//			display = false;

			//			if(B_calls > 20)
			//				pause();


			/////////////////////////////////////////////////////////////
			//      pass it down the chain?
			////////////////////////////////////////////////////////////


			if ((memory_elementA.get_length() == 0) && (memory_elementB.get_length() == 0) && head_max_star.meta_data == 0){// && node_that_found_max_star == -1){
				System.out.println("meA.l and meB.l == 0");

				head_max_star.add(current_nodeA.get_last());
				head_max_star.meta_data = 1;
				head_max_star.side = memory_elementA.side;

				current_nodeA.delete_last();
				current_nodeB.delete_last();
				//				this.insert_spaces_for_iteration("B");
				//				System.out.println("singularity deleted");
			}			
			else{


				puttyA.zero();//.set_length(0);
				puttyB.zero();//.set_length(0);
				//				puttyA.side = memory_elementA.side;
				//				puttyB.side = memory_elementB.side;
				temp_max.zero();//.set_length(0);


				if ((temp_connected_nodesA.get_length()) == 1) {

					Bochert_neighbor(puttyA,temp_connected_nodesA.get_index(0), empty_node,temp_connected_starA);

					puttyA.add(temp_connected_nodesA.get_index(0));
					puttyA.meta_data = memory_elementA.meta_data;
					puttyA.side = memory_elementA.side;

					temp_connected_nodesA.zero();//.set_length(0);
					temp_connected_starA.zero();//.set_length(0);
					memory_elementA.zero();//.set_length(0);
				}


				if ((temp_connected_nodesB.get_length()) == 1) {

					Bochert_neighbor(puttyB,temp_connected_nodesB.get_index(0), empty_node,temp_connected_starB);

					puttyB.add(temp_connected_nodesB.get_index(0));
					puttyB.meta_data = memory_elementB.meta_data;
					puttyB.side = memory_elementB.side;

					temp_connected_nodesB.zero();//.set_length(0);
					temp_connected_starB.zero();//.set_length(0);
					memory_elementB.zero();//.set_length(0);
				}


				if(puttyA.get_length() > temp_max.get_length()){
					temp_max.copy_array(puttyA);
					temp_max.meta_data = puttyA.meta_data;
					temp_max.side = puttyA.side;
				}


				if(puttyB.get_length() > temp_max.get_length()){
					temp_max.copy_array(puttyB);
					temp_max.meta_data = puttyB.meta_data;
					temp_max.side = puttyB.side;
				}

				if (((head_max_star.meta_data != 0) && (temp_max.get_length() >= head_max_star.meta_data))||(temp_max.get_length() > head_max_star.meta_data)){

					head_max_star.copy_array(temp_max);												

					/*					if(temp_max.meta_data == 0){

						if(temp_max.side == 'A'){
							head_max_star.add(current_nodeA.get_last());
							head_max_star.side = puttyA.side;
						}
						else{
							head_max_star.add(current_nodeB.get_last());						
							head_max_star.side = puttyB.side;
						}
					}
					 */					if(temp_max.meta_data == 1){
						 head_max_star.add(current_nodeA.get_last());
						 head_max_star.side = temp_max.side;
					 }
					 else if (temp_max.meta_data == 2){
						 head_max_star.add(current_nodeB.get_last());
						 head_max_star.side = temp_max.side;
					 }
					 else{
						 System.out.println("temp_max doesn't have an acceptable meta data: "+temp_max.meta_data);
						 pause();
					 }

					 //					head_max_star.meta_data = 0;

					 head_max_star.meta_data = head_max_star.get_length();

					 if ((memory_elementA.get_length() == 0)&&(memory_elementB.get_length() == 0)){
						 current_nodeA.delete_last();
						 current_nodeB.delete_last();
						 //						this.insert_spaces_for_iteration("B");
						 //						System.out.println("temp_max deleted");
					 }


				}

			}

			/////////////////////////////////////////////////////////////
			//      Set up the next iteration one deeper
			/////////////////////////////////////////////////////////////

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



			if (!((memory_elementA.get_length() == 0)&&(memory_elementB.get_length() == 0))){


				//				head_max_star.next = new node2();
				head_max_star.memory_next = new node3(nodes);

				/*					if ((current_max_starA.get_length() == 0)&&(current_max_starB.get_length() == 0)){
						head_max_star.memory_next = new node3(nodes);
					}
					else{
						if(current_max_starA.get_length()>=current_max_starB.get_length()){
							head_max_star.memory_next = current_max_starA;
							head_max_star.memory_next.side = 'A';
						}
						else{
							head_max_star.memory_next = current_max_starB;
							head_max_star.memory_next.side = 'B';
						}

					}
				 */
				head_max_star.memory_next.meta_data = (head_max_star.meta_data>0?head_max_star.meta_data-1:0);


				current_max_starA.memory_next = temp_connected_starA.copy_by_erasing();
				current_max_starB.memory_next = temp_connected_starB.copy_by_erasing();


				//temp_max.memory_next = new node2();
				//int meta_data = 0;


				B_calls++;
				B_iteration_deep++;


				nodes_to_considerA.memory_next = temp_connected_nodesA.copy_by_erasing();
				nodes_to_considerB.memory_next = temp_connected_nodesB.copy_by_erasing();



				dont_consider_connectedA.memory_next = temp_connected_starA.copy_by_erasing();
				dont_consider_connectedB.memory_next = temp_connected_starB.copy_by_erasing();

				before = System.nanoTime();

				if(nodes_to_considerA.memory_next.get_length() > 0){

					current = nodes_to_considerA.memory_next.pop_first();

					Bochert_neighbor(memory_elementA, current, dont_consider_connectedA.memory_next, nodes_to_considerA.memory_next);
					dont_consider_connectedA.memory_next.copy_array(memory_elementA);

					memory_elementA.invert();
					temp_elementA.use_me_or(dont_consider_connectedA.memory_next, nodes_to_considerA.memory_next);
					temp_elementA.use_me_and(temp_elementA, memory_elementA);
					temp_elementA.add(current);


					//		if(nodes_to_consider.get_length() > temp_element_P.get_length()){
					nodes_to_considerA.memory_next.copy_array(temp_elementA);
				}
				else{

				}

				if(nodes_to_considerB.memory_next.get_length() > 0){
					current = nodes_to_considerB.memory_next.pop_first();

					Bochert_neighbor(memory_elementB, current, dont_consider_connectedB.memory_next, nodes_to_considerB.memory_next);
					dont_consider_connectedB.memory_next.copy_array(memory_elementB);

					memory_elementB.invert();
					temp_elementB.use_me_or(dont_consider_connectedB.memory_next, nodes_to_considerB.memory_next);
					temp_elementB.use_me_and(temp_elementB, memory_elementB);
					temp_elementB.add(current);

					nodes_to_considerB.memory_next.copy_array(temp_elementB);

				}
				else{

				}

				after = System.nanoTime();
				timings[0] = timings[0] + (after-before);

				before = System.nanoTime();

				reduce_consideration(nodes_to_considerA.memory_next, dont_consider_connectedA.memory_next, temp_elementA, memory_elementA, run_meA, relic_node2);

				reduce_consideration(nodes_to_considerB.memory_next, dont_consider_connectedB.memory_next, temp_elementB, memory_elementB, run_meB, relic_node2);

				cross_check_considerations(run_meA, dont_consider_connectedA.memory_next, memory_elementA, run_meB, dont_consider_connectedB.memory_next, memory_elementB);

				after = System.nanoTime();
				timings[1] = timings[1] + (after-before);


				nodes_to_considerA.memory_next.copy_array(run_meA);
				nodes_to_considerB.memory_next.copy_array(run_meB);
				run_meA.zero();//.set_length(0);
				run_meB.zero();//.set_length(0);			


				nodes_to_considerA.memory_next.memory_previous = nodes_to_considerA;
				nodes_to_considerB.memory_next.memory_previous = nodes_to_considerB;
				dont_consider_connectedA.memory_next.memory_previous = dont_consider_connectedA; 
				dont_consider_connectedB.memory_next.memory_previous = dont_consider_connectedB; 
				//temp_max.memory_next.memory_previous = temp_max;
				head_max_star.memory_next.memory_previous = head_max_star;
				current_max_starA.memory_next.memory_previous = current_max_starA;
				current_max_starB.memory_next.memory_previous = current_max_starB;


				nodes_to_considerA = nodes_to_considerA.memory_next;
				nodes_to_considerB = nodes_to_considerB.memory_next;
				dont_consider_connectedA = dont_consider_connectedA.memory_next;
				dont_consider_connectedB = dont_consider_connectedB.memory_next;
				//temp_max = temp_max.memory_next;
				head_max_star = head_max_star.memory_next;
				current_max_starA = current_max_starA.memory_next;
				current_max_starB = current_max_starB.memory_next;


			}
			else if (((nodes_to_considerA.get_length() == 0)&&(nodes_to_considerB.get_length() == 0))){


				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

				/////////////////////////////////////////////////////////////
				//      if no passing down chain is needed, check if time to reduce (both were zero)
				////////////////////////////////////////////////////////////
				//***********************************************************//
				//           check for max clique
				//***********************************************************//



				nodes_to_considerA = nodes_to_considerA.memory_previous;
				nodes_to_considerB = nodes_to_considerB.memory_previous;
				dont_consider_connectedA = dont_consider_connectedA.memory_previous;
				dont_consider_connectedB = dont_consider_connectedB.memory_previous;
				//temp_max = temp_max.memory_previous;
				head_max_star = head_max_star.memory_previous;
				head_max_star.meta_data = (head_max_star.get_length()>head_max_star.meta_data?head_max_star.get_length():head_max_star.meta_data);
				B_iteration_deep--;
				current_nodeA.delete_last();
				current_nodeB.delete_last();
				current_max_starA = current_max_starA.memory_previous;
				current_max_starB = current_max_starB.memory_previous;
				//					this.insert_spaces_for_iteration("B");
				//					System.out.println("regular deleted");



				if((head_max_star.memory_next.get_length() +1) > (head_max_star.meta_data)){

					head_max_star.copy_array(head_max_star.memory_next);												

					//					System.out.println("adding to hms, cnA: "+current_nodeA.print_list());


					//						if(head_max_star.meta_data == 0){

					if(TOP_head_max_star != head_max_star){
						//						System.out.println("Thms != hms");
						if(head_max_star.memory_next.side == 'A'){
							//							System.out.println("hms.mn.s == A");
							//								head_max_star.add(current_nodeA.get_last());
							head_max_star.add(current_nodeA.get_full_array()[current_nodeA.get_length()]);
							head_max_star.side = nodes_to_considerA.side;
						}
						else if(head_max_star.memory_next.side == 'B'){
							//							System.out.println("hms.mn.s == B");
							//								head_max_star.add(current_nodeB.get_last());						
							head_max_star.add(current_nodeB.get_full_array()[current_nodeB.get_length()]);						
							head_max_star.side = nodes_to_considerB.side;
						}
						else{
							System.out.println("head_max_star.mem_next.side is not an acceptable value: "+head_max_star.memory_next.side);
							pause();
						}
					}


					head_max_star.meta_data = head_max_star.get_length();


					if(head_max_star.find(-1)){
						this.insert_spaces_for_iteration("B");
						System.out.println("shoot, found a -1 in head_max_star :( B_calls: "+B_calls+" hms: "+head_max_star.print_list());
						pause();
					}

					if(!is_star(head_max_star.to_int(),true)){
						this.insert_spaces_for_iteration("B");
						System.out.println("FAIL, head_max_star isn't star at all :( B_calls: "+B_calls+" length: "+head_max_star.get_length()+" and it's: "+head_max_star.print_list());
						pause();
					}

				}





			}





		}


		//		System.out.println("head_max_star inside: "+head_max_star.print_list());

		B_iteration_deep--;

		return head_max_star;

	}



	/*	private int[] Bochert_neighbor(int n, int[] nodes, int connection){

		if (nodes == null || nodes.length == 0 || nodes[0] == -1)
			return new int[0];

		int [] temp = new int[nodes.length];
		int size = 0;

		for (int i = 0; i<nodes.length; i++){

			if (((n-1) != (nodes[i]-1)) && (graph[n-1][nodes[i]-1] == connection)){
				temp[size] = nodes[i];
				size++;
			}
		}

		int[] result = new int[size];
		System.arraycopy(temp, 0, result, 0, size);
		return result;

	}
	 */

	private boolean run_this_star(node2 connections, int current_node, int[] pre_star){

		int[] pre_star2 = {1,2,4,5,10,11,15,18,24,30,33,34,39,42,45,47,49,52,57,60,61,66,67,70,74,78,80,84,85,88,92,95,97,100,103,106,110,114,115,118,121,124,127,130,135,138,140,142,146,150,152,156,157,160,164,167,169,173,177,178,183,185,189,191,193,196,200,203,205,210,212,214,219,220,225,226,230,233,237,240,241,245,249,250,255,256,261,262,266,269,272,275,277,280,283,286,289,292,295,298,301,304,307,310,313,316,319,322,325,328,331,334,337,340,343,346,349,354,357,358,362,365,367,370,373,378};

		int[] super_star = new int[pre_star2.length];


		for(int i = 0; i<super_star.length; i++){
			super_star[i] = index_ordered_nodes[pre_star2[i]-1]+1;			
		}

		super_star = merge_sort(super_star,all_neighbors(-1));
		System.out.println("is star? "+this.is_star(super_star,true)+" and length is: "+super_star.length);
		System.out.println("star is: "+array2string(super_star));

		pause();		


		//	int[] super_star = {7,17,21,28,42,46,55,86,97,117,138,143,149,154,162,167,171,176,179,187,199};
		node2 next = new node2(super_star);

		node2 base_uniq = new node2(nodes);
		node2 next_uniq = new node2(nodes);

		int first_num = -1;
		for(int i = 0; i<super_star.length; i++){
			if(current_node == super_star[i]){
				first_num = i;
				break;
			}
		}
		if(first_num < 0)
			return false;


		connections.similar_differences(next, base_uniq, next_uniq);

		//		System.out.println("similarities: "+(next.get_length()-next_uniq.get_length()));

		if((next.get_length()-next_uniq.get_length())+B_iteration_deep+1 == super_star.length){
			//			System.out.println("returning true");
			return true;
		}
		else{
			//			System.out.println((next.get_length()-next_uniq.get_length())+B_iteration_deep+1);
			//			System.out.println(super_star.length);
			return false;
		}


		/*int index_connections=0;

		for(int i = first_num+1; i<super_star.length; i++){
			while((index_connections < connections.get_length())&&(connections.get_full_array()[index_connections] < super_star[first_num])){
				index_connections++;
			}
			if(index_connections >= connections.get_length()){
				System.out.println("connnections were: "+index_connections);
				return false;
			}
			if(connections.get_full_array()[index_connections]>super_star[i]){
				System.out.println("connnections were: "+index_connections);
				return false;
			}


		}


		System.out.println("current_node: "+nodes_ordered_increasing[current_node-1]+" first was: "+first_num);
		return true;
		 */		


	}


	private void Bochert_neighbor_old(node2 result, int n, node2 array, node2 node_array, int connection){


		int length_array = array.get_length();
		int length_node_array = node_array.get_length();
		result.set_length(0);

		if (((array == null)||(length_array == 0)) && ((node_array == null)||(length_node_array == 0))){			
			return;
		}

		int nodes_index = 0;
		int nodes2_index = 0;

		while((nodes_index < length_array)||(nodes2_index < length_node_array)){

			if((nodes_index < length_array)&&((nodes2_index >= length_node_array)||(array.get_full_array()[nodes_index] < node_array.get_full_array()[nodes2_index]))){
				if (((n-1) != (array.get_full_array()[nodes_index ]-1)) && (graph[n-1][array.get_full_array()[nodes_index ]-1] == connection)){
					result.add_to_end(array.get_full_array()[nodes_index]);
				}
				nodes_index++;
			}
			else if((nodes2_index < length_node_array)&&((nodes_index >= length_array)||(array.get_full_array()[nodes_index] > node_array.get_full_array()[nodes2_index]))){

				if (((n-1) != (node_array.get_full_array()[nodes2_index]-1)) && (graph[n-1][node_array.get_full_array()[nodes2_index]-1] == connection)){
					result.add_to_end(node_array.get_full_array()[nodes2_index]);
				}
				nodes2_index++;
			}

			else if((nodes2_index < length_node_array)&&((nodes_index >= length_array)||(array.get_full_array()[nodes_index] == node_array.get_full_array()[nodes2_index]))){
				if (((n-1) != (node_array.get_full_array()[nodes2_index]-1)) && (graph[n-1][node_array.get_full_array()[nodes2_index]-1] == connection)){
					result.add_to_end(node_array.get_full_array()[nodes2_index]);
				}
				nodes2_index++;
				nodes_index++;
			}

		}

		return;

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
				//	System.out.println("node: "+(i+1)+" connected to: "+graph3[i].print_list());
			}

			//this.disp_graph();
			//		this.New_Bochert(nodesA, current_max, current_max_starA, nodesB, current_max_starB, display)
			return merge_sort(unreorganize_nodes(Newer_Bochert(new node3(this.all_neighbors(-1),nodes),0,nodes,display,null)), all_neighbors(-1));
		}
		else{

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
			return Newer_Bochert(new node3(this.all_neighbors(-1),nodes),0,nodes,display,null).to_int();
		}
	}


	private node3 reduction(node3 check_nodes, node3 not_included_extra_nodes){


		node2 set = check_nodes.to_new_node2();

		node3 all_nodes = check_nodes.copy_by_erasing();

		if(not_included_extra_nodes != null)
			all_nodes.use_me_or(all_nodes, not_included_extra_nodes);

		int deleted = -1;
		boolean multi_node = false;

		for(int i = 0; i<set.get_length(); i++){
			if((deleted != -1)&&(i>=deleted)){
				check_nodes.delete(set.get_full_array()[deleted]);
				set.delete(set.get_full_array()[deleted]);
				deleted=-1;
				multi_node = false;
				i--;//in case this was the last node, reset to re-evalute the number of nodes left to check (aka, stop if zero)
			}
			else{
				if(deleted == -1){
					if(deletable(set.get_full_array()[i], all_nodes)){
						deleted = i;
						i = -1;
					}
				}
				else{
					if(multi_node){
						if(deletable(set.get_full_array()[i], all_nodes)){
							check_nodes.delete(set.get_full_array()[i]);
							set.delete(set.get_full_array()[i]);
							deleted--;
							i = -1;					
						}
					}
					else if(graph[i][deleted] == 1){//not connected so worth considering again
						if(deletable(set.get_full_array()[i], all_nodes)){
							check_nodes.delete(set.get_full_array()[i]);
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


		return check_nodes;
	}

	private boolean deletable(int n, node3 all_nodes){

		node3 connected = new node3(nodes);
		node3 test = new node3(nodes);

		connected.use_me_and(graph3[n-1], all_nodes);

		test.use_me_and_not_first(connected, all_nodes);//no need to check the nodes that it's connected to, they can't be connected to the same because they cannot be connected to themself		
		test.delete(n);//just in case, current implementation doesn't need this tho
		int[] int_nodes = test.to_int();

		for(int i = 0; i<int_nodes.length; i++){
				test.use_me_and(graph3[int_nodes[i]-1], connected);
				//System.out.println("====== connected: "+connected.print_list()+" test: "+)
				if(test.get_length() == connected.get_length()){
					all_nodes.delete(n);
					return true;
				}
		}


		return false;

	}


	private int[] newest_Bochert(node3 all_nodes){

		System.out.println("Calling newest Bochert, looking at nodes: "+all_nodes.print_list());

		graph3 = new node3[nodes];
		for(int i = 0; i<nodes; i++){
			graph3[i] = new node3(graph[i],nodes,true);
			//	System.out.println("node: "+(i+1)+" connected to: "+graph3[i].print_list());
		}

		node2 inventory = all_nodes.to_new_node2();
		node3 running_set = new node3(nodes);
		node3 memory_element = new node3(nodes);
		node3 forming_set = new node3(nodes);

		for(int i = (all_nodes.get_length()-1); i>=0; i--){
			System.out.println("looking for sets of size: "+i);
			running_set.copy_array(all_nodes);

			while(forming_set.get_length() != running_set.get_length()){
				System.out.println("at top of while, forming set is length: "+forming_set.get_length()+" and running set is length: "+running_set.get_length());
				for(int j = 0; j < inventory.get_length(); j++){
					Bochert_neighbor(memory_element, inventory.get_full_array()[j], running_set);
					if(memory_element.get_length() >= i){
						System.out.println("adding node: "+inventory.get_full_array()[j]+" becuse it's length is: "+memory_element.get_length());
						forming_set.add(inventory.get_full_array()[j]);
					}
				}
				System.out.println("at mid of while, forming set is length: "+forming_set.get_length()+" and running set is length: "+running_set.get_length());

				if(forming_set.get_length() < (i+1)){
					running_set.zero();
					forming_set.zero();
				}
				else if(forming_set.get_length() != running_set.get_length()){
					running_set.copy_array(forming_set);
					forming_set.zero();
				}
				else{
					//done
					i = -1;
				}
				System.out.println("at bot of while, forming set is length: "+forming_set.get_length()+" and running set is length: "+running_set.get_length());


			}


		}

		forming_set.copy_array(running_set);
		int temp = 0;
		while(forming_set.get_length() != 0){
			temp = forming_set.pop_first();
			Bochert_neighbor(forming_set,temp,forming_set);
			memory_element.add(temp);
		}

		return memory_element.to_int();

	}


	private node3 Newer_Bochert(node3 all_nodes, int current_max, int sought_max, boolean show, node3 already_been_checked){

		B_iteration_deep++;
		B_calls++;

		//		if((all_nodes != null)&&(already_been_checked != null)){
		//			this.insert_spaces_for_iteration("B");
		//			System.out.println("already been checked"+already_been_checked.print_list()+" all_nodes: "+all_nodes.print_list());
		//		}

		all_nodes = reduction(all_nodes, null);
		//		if((all_nodes != null)&&(already_been_checked != null)){
		//			this.insert_spaces_for_iteration("B");
		//			System.out.println("after reduction already been checked"+already_been_checked.print_list()+" all_nodes: "+all_nodes.print_list());
		//		}

		boolean display = (((show == true)&&(B_iteration_deep < (display_level+1)))?true:false);

		//		if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
		//			this.insert_spaces_for_iteration("B");
		//			System.out.println(">> B_calls: "+B_calls+" calling Bochert("+all_nodes.print_list()+" ,cm: "+current_max+" ,sm: "+sought_max+" , abc: "+(already_been_checked==null?"null":already_been_checked.print_list())+" );");
		//		}

		if((all_nodes.get_length() == 0)||(all_nodes.get_length() == 1)){
			B_iteration_deep--;
			return all_nodes;
		}

		node3 result = new node3(nodes);		

		if(sought_max <= 1){
			result.add(all_nodes.get_index(0));
			B_iteration_deep--;
			return result;
		}

		if(all_nodes.get_length() < current_max){
			B_iteration_deep--;
			return new node3(nodes);
		}

		int toptop = all_nodes.get_index(0);
		node3 TOP_dont_consider_connected = new node3(nodes);// = result.copy_by_erasing();
		node3 all_nodes_in_set = TOP_dont_consider_connected;
		node3 TOP_nodes_to_consider = new node3(nodes);
		node3 TOP_comp_set_solution = new node3(nodes);
		node3 TOP_lost_nodes = new node3(nodes);
		node3 lost_nodes = TOP_lost_nodes;
		node2 alpha_index;
		node3 comp_set_solution = TOP_comp_set_solution;
		node3 memory_element = new node3(nodes);
		node3 temp_element = new node3(nodes);
		node3 max_star = new node3(nodes);
		max_star.meta_data = current_max;
		node3 nodes_to_consider = TOP_nodes_to_consider;//new node3(nodes);
		node3 current_alpha = new node3(nodes);
		node3 just_a_pointer = null;
		int temp = 0;
		int check_set = 1;
		int comp_set = 0;
		int deepness = 0;

		node2[] alpha = new node2[0];
		node3[] DCC = new node3[0];
		int length_extra_alredy_been_checked = 1;


		if(already_been_checked != null){



			already_been_checked.similar_differences(all_nodes, temp_element, memory_element);

			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("already been checked"+already_been_checked.print_list()+" all_nodes: "+all_nodes.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println("extra already been checked"+temp_element.print_list()+" extra all_nodes: "+memory_element.print_list());
			}


			if(memory_element.get_length() == 0){
				//should never hit here tho...
				B_iteration_deep--;
				return empty_node;
			}

			if(temp_element.get_length() == 0){
				already_been_checked = null;
			}
			else{
				alpha = new node2[temp_element.get_length()+memory_element.get_length()];
				for(int i = 0; i< alpha.length; i++){			
					alpha[i] = new node2(all_nodes.length);
				}

				length_extra_alredy_been_checked = temp_element.get_length();
				DCC = new node3[temp_element.get_length()+memory_element.get_length()];
				for(int i = 0; i<temp_element.get_length(); i++){
					DCC[i] = new node3(nodes);
					DCC[i].meta_data = temp_element.get_index(i); 
					Bochert_neighbor(DCC[i], DCC[i].meta_data, already_been_checked); 
					already_been_checked.delete(alpha[i].meta_data);		
					alpha[i].meta_data = -1*alpha[i].meta_data;
					DCC[i].meta_data = -1*DCC[i].meta_data;
					//to differentiate from not checked yet sets
				}
				for(int i = 0; i<memory_element.get_length(); i++){
					DCC[i+temp_element.get_length()] = new node3(nodes);
					DCC[i+temp_element.get_length()].meta_data = memory_element.get_index(i); //need to be checked still
					Bochert_neighbor(DCC[i+temp_element.get_length()], DCC[i+temp_element.get_length()].meta_data, all_nodes); 
					alpha[i+temp_element.get_length()].meta_data = DCC[i+temp_element.get_length()].meta_data;
					all_nodes.delete(alpha[i+temp_element.get_length()].meta_data);		
				}


				TOP_nodes_to_consider = memory_element.copy_by_erasing();// .use_me_and_not_first(TOP_dont_consider_connected, all_nodes);
				//TOP_nodes_to_consider.pop_first();// get rid of toptop
				//TOP_dont_consider_connected = new node3(nodes);
				//dont_consider_connected = TOP_dont_consider_connected;//new node3(nodes);
				nodes_to_consider = TOP_nodes_to_consider;//new node3(nodes);

				check_set = length_extra_alredy_been_checked;

			}
		}
		if(already_been_checked == null){		

			this.Bochert_neighbor(result, toptop, all_nodes);


			//it's connected to all the nodes
			if((result.get_length()+1)==all_nodes.get_length()){
				//				if(display){
				//					this.insert_spaces_for_iteration("B");
				//					System.out.println(">> B_calls: "+B_calls+" calling Bochert("+result.print_list()+" ,cm: "+(current_max==0?0:current_max-1)+" ,sm: "+(sought_max==0?0:sought_max-1)+" , abc: null; (ntc node was connected to all nodes)");
				//				}
				if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
					//			if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println(" NO  WHILE,  B_calls: "+B_calls+" toptop connected to all other nodes, calling Bochert("+result.print_list()+" ,cm: "+(current_max==0?0:current_max-1)+" ,sm: "+(sought_max==0?0:sought_max-1)+" , abc: null; (ntc node was connected to all nodes)");
				}



				result = Newer_Bochert(result,(current_max==0?0:current_max-1),(sought_max==0?0:sought_max-1),display, null);
				result.add(toptop);
				B_iteration_deep--;
				return result;
			}


			TOP_dont_consider_connected.copy_array(result);
			all_nodes_in_set = TOP_dont_consider_connected;//new node3(nodes);
			TOP_nodes_to_consider.use_me_and_not_first(TOP_dont_consider_connected, all_nodes);
			TOP_nodes_to_consider.pop_first();// get rid of toptop
			nodes_to_consider = TOP_nodes_to_consider;//new node3(nodes);

			alpha = new node2[TOP_nodes_to_consider.get_length()+1];
			for(int i = 0; i< alpha.length; i++){			
				alpha[i] = new node2(all_nodes.length);
			}

			//find DCCs
			DCC = new node3[TOP_nodes_to_consider.get_length()+1];
			DCC[0] = TOP_dont_consider_connected.copy_by_erasing();
			DCC[0].meta_data = toptop;
			alpha[0].meta_data = toptop;
			all_nodes.delete(toptop);
			for(int i = 1; i< DCC.length; i++){			
				DCC[i] = new node3(nodes);
				DCC[i].meta_data = TOP_nodes_to_consider.get_index(i-1);
				Bochert_neighbor(DCC[i], DCC[i].meta_data, all_nodes); 
				alpha[i].meta_data = DCC[i].meta_data;
				all_nodes.delete(alpha[i].meta_data);
			}
		}


		if(display){
			for(int i = 0; i<DCC.length; i++){
				this.insert_spaces_for_iteration("B");
				System.out.println(" -- DCC["+i+"].md: "+DCC[i].meta_data+" and is: "+DCC[i].print_list());
			}
		}



		//		if(false){
		if(already_been_checked == null){

			if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
				//			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println(" TOP  WHILE  B_calls: "+B_calls+" starting on, check set is "+(1+0)+" out of "+(nodes_to_consider.get_length()+length_extra_alredy_been_checked)+" which is node "+DCC[0].meta_data+" with DCC of: "+DCC[0].print_list()+" with no comp_set but current max of: "+(max_star.meta_data==0?0:max_star.meta_data-1));
			}


			temp_element = Newer_Bochert(DCC[0].copy_by_erasing(), (max_star.meta_data==0?0:max_star.meta_data-1), nodes, display, null);

			if((temp_element.get_length())>=max_star.meta_data){
				if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println("found new max star!! te.gl: "+temp_element.get_length()+" deepness: "+deepness+" max_star.md: "+max_star.meta_data);
				}
				max_star.copy_array(temp_element);
				max_star.add(toptop);
				if(!this.is_star(max_star.to_int(), true)){System.out.println("not star anymore :(");alpha[-1]=null;}
				max_star.meta_data = max_star.get_length();
			}
			else{
			}
		}




		while(check_set < (nodes_to_consider.get_length()+length_extra_alredy_been_checked)){	

			//				if(B_iteration_deep == 0){
			//					System.out.println("reseting ms.md to 0");
			//					max_star.meta_data = 0;
			//				}

			temp = 0;
			//set alphas
			for(int i = 0; i< alpha.length; i++){

				if(i != check_set){
					memory_element.use_me_and_not_first(DCC[check_set], DCC[i]);
					//memory_element.delete(DCC[check_set].meta_data); //is this true that it shouldn't be included? it can't hurt to remove it, but it might add efficiency if it was kept?
					memory_element.to_old_node2(alpha[i]);

					if((i<check_set)&&(temp < (DCC[i].get_length()-memory_element.get_length()))){
						//					if((temp < (DCC[i].get_length()-memory_element.get_length()))){
						temp = (DCC[i].get_length()-memory_element.get_length());
						comp_set = i;
					}
				}
				else
					alpha[i].set_length(0);


			}


			//set alpha[check_set]
			memory_element.use_me_and_not_first(DCC[comp_set], DCC[check_set]);
			//memory_element.delete((DCC[comp_set].meta_data<0?-1*DCC[comp_set].meta_data:DCC[comp_set].meta_data)); //must be removed
			memory_element.to_old_node2(alpha[check_set]);
			current_alpha.copy_array(memory_element);

			if(display){
				System.out.println();
				for(int i = 0; i<DCC.length; i++){
					this.insert_spaces_for_iteration("B");
					System.out.println(" -- alpha["+i+"].md: "+alpha[i].meta_data+" and is: "+alpha[i].print_list());
				}
			}





			//find DCC12
			memory_element.use_me_and(DCC[comp_set], DCC[check_set]);
			//memory_element.copy_array(DCC[comp_set]);
			//Bochert_neighbor(memory_element, DCC[check_set].meta_data, memory_element);

			if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
				//			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println(" MAIN WHILE  B_calls: "+B_calls+" starting on, check set is "+(1+check_set)+" out of "+(nodes_to_consider.get_length()+length_extra_alredy_been_checked)+" which is node "+alpha[check_set].meta_data+" with alpha of: "+alpha[check_set].print_list()+" with the comp_set "+comp_set+" which is node "+alpha[comp_set].meta_data+" with alpha of: "+alpha[comp_set].print_list()+" and common nodes are: "+memory_element.print_list()+" max_star: "+max_star.print_list()+" max_star.md: "+max_star.meta_data);

				/*				if(B_calls >= 83317809){
					display_level = 100;
					show = true;
					display = true;
				}
				if(B_calls >= 83318101){
					pause();
				}
				 */			
			}


			//			current_alpha = reduction(current_alpha, DCC[check_set]/*memory_element*/);
			current_alpha = reduction(current_alpha, memory_element);

			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("reduced current alpha is: "+current_alpha.print_list());
			}


			if(current_alpha.get_length() != 0){

				all_nodes_in_set.memory_next = new node3(nodes);
				all_nodes_in_set.memory_next.memory_previous = all_nodes_in_set; 
				all_nodes_in_set = all_nodes_in_set.memory_next;
				all_nodes_in_set.copy_array(current_alpha);
				//Bochert_neighbor(all_nodes_in_set,alpha[check_set].get_full_array()[0],current_alpha);
				//all_nodes_in_set.copy_array(reduction(all_nodes_in_set, memory_element)); //already reduced




				nodes_to_consider.memory_next = new node3(nodes);
				nodes_to_consider.memory_next.memory_previous = nodes_to_consider; 
				nodes_to_consider = nodes_to_consider.memory_next;
				//nodes_to_consider.copy_array(current_alpha);
				temp = all_nodes_in_set.pop_first();
				Bochert_neighbor(temp_element,temp,all_nodes_in_set);
				nodes_to_consider.use_me_and_not_first(temp_element, all_nodes_in_set);
				nodes_to_consider.add(temp);

				memory_element.memory_next = new node3(nodes);
				memory_element.memory_next.memory_previous = memory_element; 
				memory_element = memory_element.memory_next;
				memory_element.copy_array(memory_element.memory_previous);

				//comp_set_solution.copy_array(new node3(alpha[comp_set], nodes));
				comp_set_solution.memory_next = new node3(nodes);
				comp_set_solution.memory_next.memory_previous = comp_set_solution;
				comp_set_solution = comp_set_solution.memory_next;

				alpha[comp_set].memory_next = new node2(nodes);
				alpha[comp_set].memory_next.memory_previous = alpha[comp_set];
				alpha_index = alpha[comp_set].memory_next;
				alpha_index.copy_array(alpha_index.memory_previous);

				lost_nodes.memory_next = new node3(nodes);
				lost_nodes.memory_next.memory_previous = lost_nodes;
				lost_nodes = lost_nodes.memory_next;
				Bochert_neighbor(temp_element, temp, memory_element);
				lost_nodes.use_me_and_not_first(temp_element, memory_element);



				deepness++;

				//if(B_calls > 100)
				//pause();



				while(TOP_nodes_to_consider != nodes_to_consider){

					if((nodes_to_consider.get_length()+lost_nodes.get_length()) > 0){
						do{
							if(nodes_to_consider.get_length() == 0){
								nodes_to_consider.meta_data = lost_nodes.pop_first();
								//all_nodes_in_set.delete(nodes_to_consider.meta_data);
							}
							else{
								nodes_to_consider.meta_data = nodes_to_consider.pop_first();
								all_nodes_in_set.delete(nodes_to_consider.meta_data);
							}
							temp_element.use_me_or(memory_element, nodes_to_consider);
						}while((deletable(nodes_to_consider.meta_data,temp_element))&&((nodes_to_consider.get_length()+lost_nodes.get_length()) > 0));
					}
				if((nodes_to_consider.get_length()+lost_nodes.get_length()) > 0){

					memory_element.memory_next = new node3(nodes);
					memory_element.memory_next.memory_previous = memory_element; 
					memory_element = memory_element.memory_next;

					alpha_index.memory_next = new node2(nodes);
					alpha_index.memory_next.memory_previous = alpha_index;
					alpha_index = alpha_index.memory_next;
					alpha_index.copy_array(alpha_index.memory_previous);

					Bochert_neighbor(memory_element, nodes_to_consider.meta_data , memory_element.memory_previous);
					temp_element.use_me_and_not_first(memory_element, memory_element.memory_previous);
					if(temp_element.get_length() > 0){//nodes were removed
						alpha_index.add(temp_element.to_int());
					}

					Bochert_neighbor(temp_element,nodes_to_consider.meta_data,all_nodes_in_set);
					//						temp_element = reduction(temp_element, DCC[check_set]);//memory_element);
					temp_element = reduction(temp_element, memory_element);

					all_nodes_in_set.memory_next = new node3(nodes);
					all_nodes_in_set.memory_next.memory_previous = all_nodes_in_set; 
					all_nodes_in_set = all_nodes_in_set.memory_next;
					all_nodes_in_set.copy_array(temp_element);



					if(display){
						this.insert_spaces_for_iteration("B");
						System.out.println("deepness: "+deepness+" considering node "+nodes_to_consider.meta_data+" common nodes of ("+memory_element.get_length()+"): "+memory_element.print_list()+" and ntc: "+nodes_to_consider.print_list()+" and alpha_index: "+alpha_index.print_list());
					}


					//						if(show == true){
					//							System.out.println("ms.md: "+max_star.meta_data+" me.gl: "+memory_element.get_length()+" all_nodes"+all_nodes_in_set.get_length()+deepness+1))

					//						}
					//						if(display&&(max_star.meta_data>(memory_element.get_length()+all_nodes_in_set.get_length()+deepness+1))&&(run_it_down(alpha_index, memory_element, deepness, check_set, display, TOP_nodes_to_consider, comp_set,comp_set_solution))){
					//							System.out.println("ms.md: "+max_star.meta_data+" > me.gl: "+memory_element.get_length()+" all_nodes: "+all_nodes_in_set.get_length()+" + deepness: "+deepness+" + 1");
					//						}

					if((max_star.meta_data>(memory_element.get_length()+all_nodes_in_set.get_length()+deepness))){
						//need not look further

						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println("ELIMIATED OUT!!!!  ms.md: "+max_star.meta_data+" > me.gl: "+memory_element.get_length()+" all_nodes: "+all_nodes_in_set.get_length()+" + deepness: "+deepness+" + 1");
						}

						memory_element = memory_element.memory_previous;								
						alpha_index = alpha_index.memory_previous;
						all_nodes_in_set = all_nodes_in_set.memory_previous;



					}
					else{
						if((all_nodes_in_set.get_length() == 0)&&((deepness > comp_set_solution.get_length())/*&&run_it_down(DCC[check_set].to_new_node2(), memory_element, deepness, check_set, display, TOP_nodes_to_consider, check_set,new node3())*/&&run_it_down(/*alpha_index*/DCC[comp_set].to_new_node2(), memory_element, deepness, check_set, display, TOP_nodes_to_consider, comp_set,comp_set_solution))){

							//								just_a_pointer = ideal_comp(alpha_index, comp_set_solution, memory_element, deepness);

							//add back in unconsidered nodes, aka DCC2/1
							Bochert_neighbor(temp_element, nodes_to_consider.meta_data, all_nodes_in_set);
							memory_element.use_me_or(memory_element, temp_element);

							if(display){
								this.insert_spaces_for_iteration("B");
								System.out.println(">> B_calls: "+B_calls+" calling Bochert("+memory_element.print_list()+" ,cm: "+(max_star.meta_data-deepness<=0?0:max_star.meta_data-deepness)+" ,sm: "+nodes+" , abc: "+(just_a_pointer == null?"null":just_a_pointer.print_list())+"; ");
							}

							//								just_a_pointer = Newer_Bochert(memory_element, (max_star.meta_data-deepness-1<=0?0:max_star.meta_data-deepness-1), nodes, display, just_a_pointer);
							just_a_pointer = Newer_Bochert(memory_element, (max_star.meta_data-deepness-1<=0?0:max_star.meta_data-deepness-1), nodes, display, null);//just_a_pointer);


							if((just_a_pointer.get_length()+deepness)>=max_star.meta_data){
								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println("found new max star!! te.gl: "+just_a_pointer.print_list()+" deepness: "+deepness+" previous max_star.md: "+max_star.meta_data);
								}
								max_star.copy_array(just_a_pointer);
								just_a_pointer = nodes_to_consider;
								while(just_a_pointer != TOP_nodes_to_consider){
									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("Adding: "+just_a_pointer.meta_data);
									}
									max_star.add(just_a_pointer.meta_data);
									just_a_pointer = just_a_pointer.memory_previous;
								}
								max_star.add(alpha[check_set].meta_data);
								max_star.meta_data = max_star.get_length();
								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println("just added: "+alpha[check_set].meta_data+" so max_star is now: "+max_star.print_list());
								}
								if(!this.is_star(max_star.to_int(), true)){System.out.println("not star anymore :(");alpha[-1]=null;}
							}
							else{
							}
							memory_element = memory_element.memory_previous;
							alpha_index = alpha_index.memory_previous;
							all_nodes_in_set = all_nodes_in_set.memory_previous;



						}
						else{
							//go deeper


							if(all_nodes_in_set.get_length() > 0){


								nodes_to_consider.memory_next = new node3(nodes);
								nodes_to_consider.memory_next.memory_previous = nodes_to_consider; 
								nodes_to_consider = nodes_to_consider.memory_next;
								temp = all_nodes_in_set.pop_first();
								Bochert_neighbor(temp_element,temp,all_nodes_in_set);
								nodes_to_consider.use_me_and_not_first(temp_element, all_nodes_in_set);
								nodes_to_consider.add(temp);


								comp_set_solution.memory_next = comp_set_solution.copy_by_erasing();//new node3(nodes);
								comp_set_solution.memory_next.memory_previous = comp_set_solution;
								comp_set_solution = comp_set_solution.memory_next;


								//nodes_to_consider.copy_array(temp_element);
								//Bochert_neighbor(nodes_to_consider,nodes_to_consider.memory_previous.meta_data,nodes_to_consider.memory_previous);
								//nodes_to_consider.use_me_and_not_first(dont_consider_connected, dont_consider_connected.memory_previous);
								//nodes_to_consider.add(dont_consider_connected.memory_previous.get_index(0));

								lost_nodes.memory_next = new node3(nodes);
								lost_nodes.memory_next.memory_previous = lost_nodes;
								lost_nodes = lost_nodes.memory_next;
								Bochert_neighbor(temp_element, temp, memory_element);
								lost_nodes.use_me_and_not_first(temp_element, memory_element);





								deepness++;
							}
							else{
								memory_element = memory_element.memory_previous;								
								alpha_index = alpha_index.memory_previous;
								all_nodes_in_set = all_nodes_in_set.memory_previous;
							}

						}
					}
				}
				else{
					//go back to previous
					all_nodes_in_set = all_nodes_in_set.memory_previous;
					nodes_to_consider = nodes_to_consider.memory_previous;
					memory_element = memory_element.memory_previous;
					comp_set_solution = comp_set_solution.memory_previous;
					alpha_index = alpha_index.memory_previous;
					lost_nodes = lost_nodes.memory_previous;
					deepness--;
				}
			}

		}

		check_set++;
	}



	if(display){
		this.insert_spaces_for_iteration("B");
		System.out.println("Returning: "+max_star.print_list());
	}

	B_iteration_deep--;
	return max_star;

}



private void same_depth_reduce_options(node2 alpha_comp_set, node3 common, node3 alpha_check_set, node3 comp_set_solution){

	node3 look_in_set = new node3(nodes);
	node3 memory_element = new node3(alpha_comp_set, nodes);


	node2 ntc = comp_set_solution.to_new_node2();
	for(int i = 0; i<ntc.get_length(); i++){
		Bochert_neighbor(memory_element, ntc.get_full_array()[i], memory_element);			
	}

	memory_element.to_old_node2(ntc);
	for(int j = 0; j<ntc.get_length(); j++){
		Bochert_neighbor(memory_element, ntc.get_full_array()[j], common);
		if(memory_element.get_length() == common.get_length()){
			look_in_set.add(ntc.get_full_array()[j]);
		}
	}

	alpha_check_set.to_old_node2(ntc);	


	for(int i = 0; i<alpha_check_set.get_length(); i++){
		if(ntc.memory_previous == null){

		}

	}



}



private boolean run_it_down(node2 alpha, node3 common, int deepness, int check_set, boolean display, node3 TOP_nodes_to_consider, int comp_set,node3 comp_set_solution){

	node3 look_in_set = new node3(nodes);
	node3 memory_element = new node3(nodes);
	node3 index_Tntc;


	look_in_set = new node3(nodes);

	for(int j = 0; j<alpha.get_length(); j++){
		Bochert_neighbor(memory_element, alpha.get_full_array()[j], common);
		if(memory_element.get_length() == common.get_length()){
			look_in_set.add(alpha.get_full_array()[j]);
		}

	}


	if(comp_set < check_set){
		if(display){
			this.insert_spaces_for_iteration("B");
			System.out.print("== B(alpha"+comp_set+"("+alpha.meta_data+")"+"&&DCC="+look_in_set.print_list()+"):");
		}
		memory_element = Newer_Bochert(look_in_set, 0/*deepness-1*/, nodes/*deepness*/,false, null);
		comp_set_solution.copy_array(memory_element);
		if(deepness <= memory_element.get_length()){
			if(display)	System.out.println("true");
			return false;
		}
		if(display)System.out.println("false");
	}
	else if(comp_set == check_set){
		if(display){
			this.insert_spaces_for_iteration("B");
			System.out.print("== B(alpha"+comp_set+"("+alpha.meta_data+")"+"&&DCC="+look_in_set.print_list()+"):");
		}
		memory_element = Newer_Bochert(look_in_set,0/* deepness-1*/, nodes/*deepness+1*/,false, null);
		comp_set_solution.copy_array(memory_element);
		if((deepness+1) <= memory_element.get_length()){
			if(display)System.out.println("true");
			return false;
		}
		if(deepness == memory_element.get_length()){
			index_Tntc = TOP_nodes_to_consider.memory_next;

			while((index_Tntc != null)&&(memory_element.find(index_Tntc.meta_data))){
				index_Tntc = index_Tntc.memory_next;
			}
			if(index_Tntc != null){//ended because an element wasn't found, not because Tntc==null
				if(display)System.out.println("true");
				return false;					
			}

		}
		if(display)System.out.println("false");
	}
	else{//shouldn't ever trigger now that it has comp_set has to be l.t. check_set
		if(display){
			this.insert_spaces_for_iteration("B");
			System.out.print("== B(alpha"+comp_set+"("+alpha.meta_data+")"+"&&DCC="+look_in_set.print_list()+"):");
		}
		memory_element = Newer_Bochert(look_in_set, 0/*deepness*/, nodes/*deepness+1*/, false, null);
		comp_set_solution.copy_array(memory_element);
		if((deepness+1) <= memory_element.get_length()){
			if(display)System.out.println("true");
			return false;
		}
		if(display)System.out.println("false");
	}





	return true;
}


private node3 ideal_comp(node2 alpha_index, node3 found_set, node3 memory_element, int deepness){

	//System.out.println("alpha_index: "+alpha_index.print_list()+" prev_set: "+prev_set.print_list()+" mem_elm: "+memory_element.print_list());

	if(alpha_index.get_length() == 0)
		return null;

	node3 alpha = new node3(alpha_index, nodes);
	node2 prev_set2 = found_set.to_new_node2();//= new node2(nodes);
	//		prev_set.to_old_node2(prev_set2);


	for(int i = 0; i<prev_set2.get_length(); i++){
		Bochert_neighbor(alpha, prev_set2.get_full_array()[i], alpha);
	}


	if((deepness-1)==found_set.get_length()){
		prev_set2 = alpha.to_new_node2();

		if(prev_set2.get_length() == 0)
			return null;


		int current_max = -1;
		int cor_node = 0;
		node3 play_sand = new node3(nodes);
		for(int i = 0; i<prev_set2.get_length(); i++){
			Bochert_neighbor(play_sand, prev_set2.get_full_array()[i], memory_element);
			if(play_sand.get_length() > current_max){
				cor_node = prev_set2.get_full_array()[i];
				current_max = play_sand.get_length();
			}
		}


		Bochert_neighbor(alpha, cor_node, alpha);
		Bochert_neighbor(play_sand, cor_node, memory_element);
		alpha.use_me_or(alpha, play_sand);
	}
	else{
		if(1==1)
			return null;//dont even bother... just return null because the result isn't worth knowing

		//combine
		alpha.use_me_or(alpha, memory_element);
		node3 temp_element = alpha.copy_by_erasing();

		int dif = deepness-found_set.get_length();

		temp_element = this.Newer_Bochert(temp_element, 0, dif, false, null);

		if(temp_element.get_length() < dif){
			return null;
		}
		else{

			temp_element.to_old_node2(prev_set2);

			for(int i = 0; i<prev_set2.get_length(); i++){
				Bochert_neighbor(alpha, prev_set2.get_full_array()[i], alpha);
			}


		}

	}
	return alpha;
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


	System.out.println("Hello?2");

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


	graph g = new graph(testie7);
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
		if ((i != 18) && (i != 19)){// && (i != 18) && (i != 19)){
			System.out.println("***********************************************************************************************************");
			System.out.println(i+" "+s[i]);
			g = new graph(s[i]);
			//g = new graph(testie5);

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
				g.display_level = 0;



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

			start = System.currentTimeMillis();
			g.B_calls = 0;
			//				temp = g.pre_New_Bochert(false,g.nodes);
			temp = g.pre_Newer_Bochert(false);

			elapsedTimeMillis = System.currentTimeMillis()-start;

			System.out.println();
			System.out.println("max clique from un-optimized Bochert is: ");
			System.out.println(g.array2string(temp));
			System.out.println("total calls to Bochert: "+g.B_calls);
			System.out.println("__ it took:"+elapsedTimeMillis+" miliseconds");

			System.out.println("is star?: "+g.is_star(temp, true)+" and length is: "+temp.length);

			System.out.println();


			for(int ii=0; ii<3; ii++){
				System.out.println("timing "+ii+": "+(g.timings[ii]/1e6)+" for: "+g.timings2[ii]);
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
