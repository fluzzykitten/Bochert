package OldClique;

import java.io.*;

public class graph {

	private int[][] graph; // the adjacency matrix
	private int nodes; // total number of nodes
	private int BK_iteration_deep = 0; // iterations deep into BronKerbosch
	private long BK_calls = 0; // calls to BronKerbosch
	private int B_iteration_deep = 0; //iterations deep into Bochert
	private long B_calls = 0; // calls to Bochert
	private boolean verboseBK = false; // Verbosity of output... verbosity should be a word... it sounds cool
	private int[] B_it_calls = new int[10]; //DELETE???? Huh... I have no idea what this is used for
	private int edges = 0; // total edges in graph
	private int initial_connected = 1; // used when functions are looking creating the initial matrix, for finding max independant set instead of max clique, this value is inverted
	private int initial_not_connected = 0; // used when functions are looking creating the initial matrix, for finding max independant set instead of max clique, this value is inverted
	private int internal_connected = 1; // used when functions are looking at internal connected ness
	private int internal_not_connected = 0; // used when functions are looking at internal connected ness
	private node[] independent_sets; // each index in the array (except for index 0) represents an independent set, and the nodes in that set
	private int[] node_ind_set; // where each node is an index, value is ind set node is in. 0 denotes no specified ind set
	private int[] node_edge_count; // number of edges each node has connected to it
	private int[] node_index_increasing; // array of nodes with increasing edge count - first node has lowest num edges
	private int[] nodes_with_over_half_edges; // edges with nodes that have over half edges needed to be considered for ind sets
	private boolean use_node_edge_count = true; // if the sorting mechanism is looking at edge count, or actual array values
	private boolean use_ind_sets = false; // so that bochert can first find the ind sets without getting confused and trying to use them in bochert

	public int[] pre_Bochert(){
		int[] max_clique = null;

		//		B_calls = 0;


		node_index_increasing = new int[nodes];
		for(int i = 0; i<nodes; i++)
			node_index_increasing[i] = i;

		node_index_increasing = merge_sort(node_index_increasing);
		nodes_with_over_half_edges = find_nodes_with_over_half();
		for(int i = 0; i<nodes_with_over_half_edges.length; i++)
			nodes_with_over_half_edges[i]++;


		use_node_edge_count = false;
		//		System.out.println(array2string(nodes_with_over_half_edges));
		nodes_with_over_half_edges = merge_sort(nodes_with_over_half_edges);
		//		System.out.println("Nodes with over half: "+array2string(nodes_with_over_half_edges));		

		make_independent_sets();

		/*		for (int i = 0; i<node_ind_set.length; i++)
			System.out.println("node: "+i+" is in set: "+node_ind_set[i]);

		System.out.println("Node independent sets are:");
		for (int i = 0; i<independent_sets.length ; i++)
			System.out.println("Set: "+i+" is: "+array2string(independent_sets[i].print_array()));
		 */
		use_ind_sets = true;

		//		B_calls = 0;

		max_clique = Bochert2(all_neighbors(-1), 0);
		System.out.println("Exiting Bochert with max_clique:"+array2string(max_clique));
		//max_clique = Bochert_Ind_Control(all_neighbors(-1), null, 0);

		return max_clique;
	}


	private int[] Bochert_Ind_Control(int[] nodes_to_check, node nodes_been_checked, int max){

		B_calls++;
		B_iteration_deep++;

		if (B_calls > 300)
			return null;

		if (B_iteration_deep >= 20)
			System.out.println(nodes_to_check[-1]);

		int[] result = new int[max];
		int[] temp_result = null;

		if (nodes_to_check == null || nodes_to_check.length == 0 || nodes_to_check[0] == -1){
			B_iteration_deep--;
			temp_result = nodes_been_checked.print_array();
			if (temp_result == null || temp_result[0] == -1)
				return null;
			else
				return temp_result;
			//return Bochert(nodes_been_checked.print_array(),max);
		}


		node nodes_left = new node(nodes_to_check);
		node temp_node = new node();
		node_int_array arrays_to_check = new node_int_array();
		int[][] sets;
		node internal_nodes_to_be_checked;
		int[] fix_this = null;
		node_int_array temp; 


		if (nodes_been_checked == null)
			internal_nodes_to_be_checked = new node();
		else
			internal_nodes_to_be_checked = nodes_been_checked.copy();


		while (nodes_left != null && nodes_left.print_array()[0] != -1){


			//			System.out.println("  looking at "+nodes_left.print_array()[0]+" which is in ind set: "+node_ind_set[nodes_left.print_array()[0]-1]+" with nodes: "+independent_sets[node_ind_set[nodes_left.print_array()[0]-1]].print_list());

			//			System.out.println("nodes_left: "+nodes_left.print_list());
			if (node_ind_set[nodes_left.print_array()[0]-1] == 0){
				System.out.println("!!!!!"+nodes_left.print_array()[0]+" is in ind set 0");
				fix_this = new int[1];
				fix_this[0] = nodes_left.print_array()[0];
				temp_node.create_nodes(fix_this);

				internal_nodes_to_be_checked.add(temp_node);

				arrays_to_check.add_in_order_of_length(Bochert_neighbor(nodes_left.print_array()[0], nodes_left.print_array()),internal_nodes_to_be_checked);

				internal_nodes_to_be_checked.delete(nodes_left.print_array()[0]);

				nodes_left.delete(temp_node);

			}
			else{
				//				this.insert_spaces_for_iteration("B");
				//			System.out.println("before sets to check, nodes left are: "+nodes_left.print_list());
				sets = find_sets_to_check_all(nodes_left.print_array()[0], nodes_left, null, max);
				//		this.insert_spaces_for_iteration("B");
				//	System.out.println("after sets to check, nodes left are: "+nodes_left.print_list());

				if (sets != null){
					this.insert_spaces_for_iteration("B");
					System.out.println("calls: "+B_calls);

					System.out.println("length of last value of sets is: "+sets[sets.length-1]);

					for (int j = 0; j<sets.length; j++){
						System.out.println("sets index: "+j+" of size "+sets[j].length+" is array: "+array2string(sets[j]));
						if (sets[j].length > 1 && sets[j][0] == 1 && sets[j][1] == 2)
							System.out.println(sets[-1]);
					}

					for (int i = 1; i<sets.length; i++){
						fix_this = new int[1];
						fix_this[0] = sets[0][i];
						temp_node.create_nodes(fix_this);
						internal_nodes_to_be_checked.add(temp_node);

						arrays_to_check.add_in_order_of_length(sets[i],internal_nodes_to_be_checked);

						internal_nodes_to_be_checked.delete(sets[0][i]);

					}
				}
			}

		}	


		temp = arrays_to_check;

		while (temp != null && temp.get_array() != null){
			temp = temp.get_next();
		}


		temp = arrays_to_check;

		while (temp != null && temp.get_array() != null){

			if (((temp.get_node().get_length()-1)+temp.get_array().length) > result.length)
				temp_result = Bochert_Ind_Control(temp.get_array(),temp.get_node(),result.length);

			if (temp_result != null && temp_result.length > result.length)
				result = temp_result;

			temp = temp.get_next();


		}

		insert_spaces_for_iteration("B");
		System.out.println("Returning: "+array2string(result));

		B_iteration_deep--;
		return result;
	}


	private void make_independent_sets(){

		internal_connected = 0;
		internal_not_connected = 1;

		node[] ind_sets = new node[nodes];
		ind_sets[0] = new node();
		int index_ind_sets = 1;
		node_ind_set = new int[nodes];

		int[] all_nodes = nodes_with_over_half_edges;//all_neighbors(-1);
		int length_nodes_left = nodes_with_over_half_edges.length;//all_nodes.length;
		int[] found_ind_set, temp;
		node nodes_to_consider = new node();
		nodes_to_consider.create_nodes(all_nodes);

		while (nodes_to_consider.get_next().get_next().get_value() != -1){

			found_ind_set = Bochert(all_nodes, 0);
			ind_sets[index_ind_sets] = new node(found_ind_set);

			for (int i = 0; i < found_ind_set.length; i++)
				node_ind_set[found_ind_set[i]-1] = index_ind_sets;


			index_ind_sets++;
			nodes_to_consider.split_nodes(found_ind_set);
			length_nodes_left = length_nodes_left - found_ind_set.length;
			all_nodes = new int[length_nodes_left];
			temp = nodes_to_consider.print_array();
			if (length_nodes_left != 0)
				System.arraycopy(temp, 0, all_nodes, 0, length_nodes_left);
		}



		independent_sets = new node[index_ind_sets];
		System.arraycopy(ind_sets, 0, independent_sets, 0, index_ind_sets);

		/*		for (int i = 0; i<independent_sets.length; i++){
			System.out.println("Set Num:"+i+" contains: "+independent_sets[i].print_list());

		}

		for (int i = 0; i<node_ind_set.length; i++)
			System.out.println("Node:"+i+" is in ind set:"+node_ind_set[i]);
		 */

		internal_connected = 1;
		internal_not_connected = 0;
		B_calls = 0;
	}




	private int[] find_nodes_with_over_half(){
		boolean finding = true;
		node node_index = new node(node_index_increasing);
		int[] node_edge_count_temp = new int[nodes];
		System.arraycopy(node_edge_count,0,node_edge_count_temp,0,nodes);
		int[][] graph_temp = new int[nodes][nodes];
		for (int i=0;i<nodes;i++){
			System.arraycopy(graph[i],0,graph_temp[i],0,nodes);
		}

		int nodes_left = nodes;
		node nodes_to_return = new node();
		int total_returning_nodes = 0;
		int current_node = 0;
		node index = null;

		//		System.out.println("node edge count"+array2string(node_edge_count_temp));
		//		System.out.println("node index incr"+node_index.print_list());

		while (finding){
			while (nodes_left > 7 && finding){
				//				System.out.println("examining: "+node_index.get_next().get_value()+" with "+node_edge_count_temp[node_index.get_next().get_value()]+" edges");
				if (node_edge_count_temp[node_index.get_next().get_value()] > (nodes_left-1)/2){
					//					System.out.println("Found that the node "+node_index.get_next().get_value()+" with size: "+node_edge_count_temp[node_index.get_next().get_value()]+" is at least half of needed size of "+((nodes_left-1)/2));
					//					System.out.println("nodes to return is currently of length: "+nodes_to_return.get_length()+" and nodes: "+ nodes_to_return.print_list());
					//					System.out.println("node_index is currently of length: "+node_index.get_length()+" and nodes: "+ node_index.print_list());
					nodes_to_return.add_unordered(node_index);
					//					System.out.println("nodes to return is currently of length: "+nodes_to_return.get_length()+" and nodes: "+ nodes_to_return.print_list());
					total_returning_nodes = total_returning_nodes + nodes_left;
					finding = false;
				}
				else{
					current_node = node_index.get_next().get_value();
					//					System.out.println("node_index before deleting next is length: "+node_index.get_length()+" and nodes: "+ node_index.print_list());					
					node_index.delete_next(true);
					//					System.out.println("node_index after deleting next is length: "+node_index.get_length()+" and nodes: "+ node_index.print_list());					
					nodes_left--;
					for(int i=0; i<nodes; i++){
						if(graph_temp[current_node][i] == 1){
							graph_temp[current_node][i] = 0;
							graph_temp[i][current_node] = 0;
							node_edge_count_temp[i]--;
							//							System.out.println("Subracting one edge from: "+i+" new edge count is: "+node_edge_count_temp[i]);
							index = node_index.get_next();
							do{
								if (index.get_value() == i){
									while((index != node_index.get_next()) && (node_edge_count_temp[index.get_previous().get_value()] > node_edge_count_temp[index.get_value()])){
										//										System.out.println("Node "+index.get_previous().get_value()+" with "+node_edge_count_temp[index.get_previous().get_value()]+" edges has more edges than node "+index.get_value()+" which has only "+node_edge_count_temp[index.get_value()]+" edges");
										index.swap_with_previous();
										index = index.get_previous();
									}				
									index = node_index.get_next();
								}
								else
									index = index.get_next();

							}while(index != node_index.get_next());

						}

					}

				}

			}
			//			System.out.println("******An iteration has completed, with length: "+nodes_to_return.get_length()+" current nodes to return is: "+nodes_to_return.print_list());

			if (nodes_left <= 7)
				return nodes_to_return.print_array();


			for (int i=0;i<nodes;i++)
				for (int j=0; j<nodes; j++){
					graph_temp[i][j]=graph[i][j];
				}

			System.arraycopy(node_edge_count,0,node_edge_count_temp,0,nodes);
			node_index = new node(node_index_increasing);
			//			System.out.println("Node_index length:"+node_index.get_length()+" nodes: "+node_index.print_list());			
			node_index.delete(nodes_to_return);
			//			System.out.println("Node_index length:"+node_index.get_length()+" nodes: "+node_index.print_list());
			nodes_left = nodes - total_returning_nodes;
			//			System.out.println("node edge count"+array2string(node_edge_count_temp));
			//			System.out.println("node index incr"+node_index.print_list());
			//			disp_graph();

			if (nodes_left > 7)
				finding = true;

		}
		return nodes_to_return.print_array();

	}

	private void pause() 
	{
		try
		{
			System.in.read();
		}
		catch(IOException exe)
		{
		}
	}

	private int[] merge_sort(int[] list)
	{
		if (list.length <= 1)
			return list;
		int[] left, right, result;

		int middle = list.length / 2;
		left = new int[middle];
		right = new int[list.length - middle];
		System.arraycopy(list, 0, left, 0, middle);
		System.arraycopy(list, middle, right, 0, list.length - middle);

		left = merge_sort(left);
		right = merge_sort(right);
		result = merge(left, right);
		return result;
	}

	

	private int[] merge(int[] left,int [] right)
	{

		int lefti = 0, righti = 0, resulti = 0;
		int leftl = left.length, rightl = right.length;
		int resultl = leftl + rightl;
		int[] result = new int[resultl];


		if (use_node_edge_count)
			while ((lefti < leftl) && (righti < rightl)){
				if (node_edge_count[left[lefti]] <= node_edge_count[right[righti]]){ 
					result[resulti] = left[lefti];
					resulti++;
					lefti++;
				}
				else{
					result[resulti] = right[righti];
					resulti++;
					righti++;        }
			}
		else{
			while ((lefti < leftl) && (righti < rightl)){
				if (left[lefti] <= right[righti]){ 
					result[resulti] = left[lefti];
					resulti++;
					lefti++;
				}
				else{
					result[resulti] = right[righti];
					resulti++;
					righti++;        }
			}	
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

	private int[][] find_sets_to_check_all_old(int initiator, node nodes, node head_max_star, int min){
		//node nodes_copy = nodes.copy();
		if (min < 0)
			min = 0;

		node fellow_independants = null;
		int independants;
		int deleted_nodes = 0;

		node_int_array sets_found_head = null;
		//		node_int_array node_array_pointer_to_empty_last_set = null;
		node_int_array index_bigger_than_this_set_array = null;
		node index_bigger_than_next_set_node = null;
		node_int_array dummy_var_for_sets_traversal = null;
		int[] node_pool = null;
		int[] current_neighbors = null;
		int compare_val;
		int next_set_index = 0;

		node ind_set_node_that_owns_set_to_check = null;
		node dummy_var_for_array_node_owner_of_found_set = null;
		node dummy_var_for_nodes_traversal = null;

		ind_set_node_that_owns_set_to_check = new node();
		dummy_var_for_array_node_owner_of_found_set = null;

		next_set_index = 1;

		node_int_array test_array = null; //delete, just for testing
		node test_node = null; //delete, just for testing


		while(nodes.print_array()[0] != -1){

			independants = 0;

			fellow_independants =  nodes.split_nodes(independent_sets[node_ind_set[nodes.get_next().get_value()-1]].print_array());
			node_pool = nodes.combine(head_max_star);//nodes.print_array();

			independants = fellow_independants.get_length()-1;
			deleted_nodes += independants;

			//		System.out.println("fellow independents: "+array2string(fellow_independants.print_array()));



			compare_val = 1;



			//		System.out.println("Nodes_pool: "+array2string(node_pool)+" and fellow_ind is: "+fellow_independants.print_list());

			if (next_set_index == 1 && node_pool[0] == -1){ //first node
				//			System.out.println("num sets = 1 and nodes_copy_arr[0] = -1");

				sets_found_head = new node_int_array(-1,null);
				next_set_index++;

				ind_set_node_that_owns_set_to_check.add_unordered(new node(fellow_independants.get_next().get_value()));


			}
			else{

				while(independants > 0){


					current_neighbors = Bochert_neighbor(fellow_independants.get_next().get_value(), node_pool);

					//				System.out.println();
					//				System.out.println(">>looking at: "+fellow_independants.get_next().get_value()+" Current_neighbors: "+array2string(current_neighbors)+" independants left: "+independants);

					if (((current_neighbors == null) && (min == 0)) || ((current_neighbors != null) && (current_neighbors.length >= min))){ //if it's attached to something and attached to enough of something :)
						if (next_set_index == 1){//(current_neighbors.length == 0) && (index_sets == sets_to_check.get_next())){
							//						System.out.println("initial set made");
							if (sets_found_head != null) // make error if it's not null
								System.out.println(sets_found_head.get_array()[-1]); 

							if (current_neighbors == null || current_neighbors.length == 0){
								sets_found_head = new node_int_array(-1,null);
							}
							else{
								sets_found_head = new node_int_array(current_neighbors);
							}

							next_set_index++;
							ind_set_node_that_owns_set_to_check.add_unordered(new node(fellow_independants.get_next().get_value()));


						}
						else if ((current_neighbors != null) && (current_neighbors.length != 0)){
							dummy_var_for_nodes_traversal = ind_set_node_that_owns_set_to_check.get_next(); // initial node that owns set to check, first in set is always -1
							dummy_var_for_sets_traversal = sets_found_head; // initial set to check, because the first one is the control array


							index_bigger_than_this_set_array = sets_found_head;
							index_bigger_than_next_set_node = ind_set_node_that_owns_set_to_check.get_next(); // initial node that owns set to check, first in set is always -1

							/*						System.out.println("dummy_var_for_nodes_traversal    = "+dummy_var_for_nodes_traversal.get_value());
						System.out.println("dummy_var_for_sets_traversal     = "+this.array2string(dummy_var_for_sets_traversal.get_array()));
						System.out.println("index_bigger_than_next_set_node  = "+index_bigger_than_next_set_node.get_value());
						System.out.println("index_bigger_than_next_set_array = "+this.array2string(index_bigger_than_this_set_array .get_array()));
						System.out.println();
						//pause();

						System.out.println("press the any key to cycle through current sets_found_head and ind_set_node_that_owns_set_to_check");
						pause();

						test_array = sets_found_head;
						test_node = ind_set_node_that_owns_set_to_check.get_next();

						do{

						System.out.println("sets_found_head (length = "+test_array.get_array().length+" is the set: "+this.array2string(test_array.get_array()));
						System.out.println("ind_set_node_that_owns_set_to_check = "+test_node.get_value());

						test_array = test_array.get_next();
						test_node = test_node.get_next();

						}while(test_array != sets_found_head);
						System.out.println();
						System.out.println();
						pause();
							 */

							do{

								//							System.out.println("Entering do loop");

								compare_val = sub_set(dummy_var_for_sets_traversal.get_array(),current_neighbors);							



								if (compare_val == 0){ // is contained in or matches a set already in there
									//								System.out.println("*0* Set was found contained in another set");
									break;
								}
								else if (compare_val == -1){ // the set conatins the set that's already saved
									//								System.out.println("*-1* Set was found to contain given set");
									if ((dummy_var_for_sets_traversal == sets_found_head) || (!dummy_var_for_sets_traversal.delete_this())){//there was previously only one node
										//									System.out.println("was found to actually contain the first set, so it's being taken care of in the early loop");
										sets_found_head.set_array(current_neighbors);
										ind_set_node_that_owns_set_to_check.set_value(fellow_independants.get_next().get_value());
										compare_val = 0;//taken care of									
									}
									else{
										//									System.out.println("was not found to contain the first set, but a not first set");
										dummy_var_for_nodes_traversal.get_next().set_previous(dummy_var_for_nodes_traversal.get_previous());
										dummy_var_for_nodes_traversal.get_previous().set_next(dummy_var_for_nodes_traversal.get_next());
										ind_set_node_that_owns_set_to_check.set_length(ind_set_node_that_owns_set_to_check.get_length()-1);
									}
									break;

								}

								dummy_var_for_sets_traversal = dummy_var_for_sets_traversal.get_next(); // move to the next set
								dummy_var_for_nodes_traversal = dummy_var_for_nodes_traversal.get_next();

								if ((index_bigger_than_this_set_array.get_array().length >= current_neighbors.length)){
									index_bigger_than_this_set_array = index_bigger_than_this_set_array.get_next();
									index_bigger_than_next_set_node = index_bigger_than_next_set_node.get_next();
								}
								else{

								}
								//								System.out.println("current_neighbors.length is bigger than: "+index_bigger_than_this_set_array.get_array().length);

							}while(dummy_var_for_sets_traversal != sets_found_head);

							if (compare_val != 0){

								if (compare_val == 1){
									//								System.out.println("was found to be a new set");
									next_set_index++;
								}

								if (sets_found_head == null){
									//								System.out.println("After checking all sets, is being added, aren't any in list yet");
									sets_found_head = new node_int_array(current_neighbors);
									ind_set_node_that_owns_set_to_check.add_unordered(new node(fellow_independants.get_next().get_value()));
								}
								else if ((index_bigger_than_this_set_array == sets_found_head) && (current_neighbors.length > sets_found_head.get_array().length)){// index_bigger_than_next_set_array.get_next() || ind_set_node_that_owns_set_to_check.get_length() == 1){ // so if the new set is going to be added onto the end of the sets 
									//								System.out.println("After checking all sets, is being added and was found to be the first value of the list");
									sets_found_head = new node_int_array(current_neighbors);
									index_bigger_than_this_set_array.insert_previous(sets_found_head);

									dummy_var_for_nodes_traversal = new node(fellow_independants.get_next().get_value());
									dummy_var_for_nodes_traversal.set_next(ind_set_node_that_owns_set_to_check.get_next());
									dummy_var_for_nodes_traversal.set_previous(ind_set_node_that_owns_set_to_check.get_next().get_previous());

									ind_set_node_that_owns_set_to_check.get_next().get_previous().set_next(dummy_var_for_nodes_traversal);
									ind_set_node_that_owns_set_to_check.get_next().set_previous(dummy_var_for_nodes_traversal);


									ind_set_node_that_owns_set_to_check.set_next(dummy_var_for_nodes_traversal);
									ind_set_node_that_owns_set_to_check.set_previous(dummy_var_for_nodes_traversal);

									ind_set_node_that_owns_set_to_check.set_length(ind_set_node_that_owns_set_to_check.get_length()+1);

								}
								else {
									//								System.out.println("After checking all sets, is being added somewhere in the list");
									dummy_var_for_sets_traversal = new node_int_array(current_neighbors);
									dummy_var_for_nodes_traversal = new node(fellow_independants.get_next().get_value());

									//								System.out.println("BTNA.length: "+index_bigger_than_this_set_array.get_array().length);
									//								System.out.println("BTNA.next.length: "+index_bigger_than_this_set_array.get_next().get_array().length);

									//								System.out.println("DV.next.length was:"+dummy_var_for_sets_traversal.get_next().get_array().length);								
									dummy_var_for_sets_traversal.set_next(index_bigger_than_this_set_array);
									//								System.out.println("DV.next.length is now:"+dummy_var_for_sets_traversal.get_next().get_array().length);

									//								System.out.println("DV.prev.length was:"+dummy_var_for_sets_traversal.get_previous().get_array().length);
									dummy_var_for_sets_traversal.set_previous(index_bigger_than_this_set_array.get_previous());
									//								System.out.println("DV.prev.length is now:"+dummy_var_for_sets_traversal.get_previous().get_array().length);

									//								System.out.println("BiggerNS.prev.next.length was:"+index_bigger_than_this_set_array.get_next().get_array().length);
									index_bigger_than_this_set_array.get_previous().set_next(dummy_var_for_sets_traversal);
									//								System.out.println("BiggerNS.prev.next.length is now:"+index_bigger_than_this_set_array.get_next().get_array().length);

									//								System.out.println("BiggerNS.prev.length was:"+index_bigger_than_this_set_array.get_next().get_array().length);
									index_bigger_than_this_set_array.set_previous(dummy_var_for_sets_traversal);
									//								System.out.println("BiggerNS.prev.length is now:"+index_bigger_than_this_set_array.get_next().get_array().length);

									dummy_var_for_nodes_traversal.set_next(index_bigger_than_next_set_node);
									dummy_var_for_nodes_traversal.set_previous(index_bigger_than_next_set_node.get_previous());
									index_bigger_than_next_set_node.get_previous().set_next(dummy_var_for_nodes_traversal);
									index_bigger_than_next_set_node.set_previous(dummy_var_for_nodes_traversal);

									ind_set_node_that_owns_set_to_check.set_length(ind_set_node_that_owns_set_to_check.get_length()+1);


								}

							}
						}
					}
					else{
						//					System.out.println("turns out the size of the found set: "+current_neighbors.length+" is < to the current max: "+max);
					}
					independants--;
					fellow_independants.delete_next(true);


				}
			}
		}

		if (sets_found_head == null)
			return null;
		else{
			int[][] return_value = new int[next_set_index-1][];
			//			System.out.println(" currently ind set node that owns set to check is: "+ind_set_node_that_owns_set_to_check.print_list()+" and length is: "+ind_set_node_that_owns_set_to_check.get_length());
			return_value[0] = new int[ind_set_node_that_owns_set_to_check.length];
			return_value[0][0] = deleted_nodes;//deleted_nodes;
			System.arraycopy(ind_set_node_that_owns_set_to_check.print_array(), 0, return_value[0], 1, (return_value[0].length-1));


			dummy_var_for_sets_traversal = sets_found_head;
			int i = 1;

			do{
				return_value[i] = dummy_var_for_sets_traversal.get_array();
				dummy_var_for_sets_traversal = dummy_var_for_sets_traversal.get_next();
				i++;
			}while(dummy_var_for_sets_traversal.get_next() != sets_found_head);


			return return_value;
		}
	}

	
	
	///////////////////////////////////////////////////////////////////////////


	private int[][] find_sets_to_check_all(int initiator, node nodes, node head_max_star, int min){

		if (min < 0)
			min = 0;

		node fellow_independants = null;
		int independants;
		int deleted_nodes = 0;

		node_int_array sets_found_head = null;

		node_int_array index_bigger_than_this_set_array = null;
		node index_bigger_than_this_set_node = null;
		node_int_array dummy_var_for_sets_traversal = null;
//		int[] node_pool_array = null;
		int[] current_neighbors = null;
		int[] current_neighbors_enlargement_temp_variable = null;
		int compare_val;
		int next_set_index = 0;

		node ind_set_node_that_owns_set_to_check = null;
		node dummy_var_for_array_node_owner_of_found_set = null;
		node dummy_var_for_nodes_traversal = null;

		ind_set_node_that_owns_set_to_check = new node();
		dummy_var_for_array_node_owner_of_found_set = null;

		next_set_index = 1;

		node_int_array test_array = null; //delete, just for testing
		node test_node = null; //delete, just for testing
		int ii=1;

		independants = 0;

		fellow_independants =  nodes.copy();//nodes.split_nodes(independent_sets[node_ind_set[nodes.get_next().get_value()-1]].print_array());

//		node_pool_array = nodes.combine(head_max_star);//nodes.print_array();
		node node_pool_node = new node(nodes.combine(head_max_star)); 
		
		independants = fellow_independants.get_length()-1;
		deleted_nodes += independants;

		compare_val = 1;


		if (next_set_index == 1 && node_pool_node.get_next().get_value() == -1){ //first node

			sets_found_head = new node_int_array(-1,null);
			next_set_index++;

			ind_set_node_that_owns_set_to_check.add_unordered(new node(fellow_independants.get_next().get_value()));


		}
		else{

			while(independants > 0){

				current_neighbors = Bochert_neighbor(fellow_independants.get_next().get_value(), node_pool_node.print_array());

				if (((current_neighbors == null) && (min == 0)) || ((current_neighbors != null) && (current_neighbors.length >= min))){ //if it's attached to something and attached to enough of something :)
					if (next_set_index == 1){//(current_neighbors.length == 0) && (index_sets == sets_to_check.get_next())){
						if (sets_found_head != null) // make error if it's not null
							System.out.println(sets_found_head.get_array()[-1]); 

						if (current_neighbors == null || current_neighbors.length == 0){
							sets_found_head = new node_int_array(-1,null);
						}
						else{
							sets_found_head = new node_int_array(current_neighbors);
						}

						next_set_index++;
						ind_set_node_that_owns_set_to_check.add_unordered(new node(fellow_independants.get_next().get_value()));


					}
					else if ((current_neighbors != null) && (current_neighbors.length != 0)){

						dummy_var_for_nodes_traversal = ind_set_node_that_owns_set_to_check.get_next(); // initial node that owns set to check, first in set is always -1
						dummy_var_for_sets_traversal = sets_found_head; // initial set to check, because the first one is the control array


						index_bigger_than_this_set_array = sets_found_head;
						index_bigger_than_this_set_node = ind_set_node_that_owns_set_to_check.get_next(); // initial node that owns set to check, first in set is always -1


						do{
							compare_val = sub_set(dummy_var_for_sets_traversal.get_array(),current_neighbors);							



							if (compare_val == 0){ // is contained in or matches a set already in there
								break;
							}
							else if (compare_val == -1){ // the set conatins the set that's already saved
								
								//Need to add the node if it's connected to the new node with a larger package
								if (graph[fellow_independants.get_next().get_value()-1][dummy_var_for_nodes_traversal.get_value()-1]==1){
									System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
									current_neighbors = insert_value(current_neighbors,dummy_var_for_nodes_traversal.get_value());
									}
								if (dummy_var_for_sets_traversal == sets_found_head){
									if (sets_found_head.get_next() == sets_found_head){
										sets_found_head = null;
										ind_set_node_that_owns_set_to_check = null;
										ind_set_node_that_owns_set_to_check = new node();
										
										sets_found_head = new node_int_array(current_neighbors);
										ind_set_node_that_owns_set_to_check.add_unordered(new node(fellow_independants.get_next().get_value()));
										
										compare_val = 0;
										break;
									}
									else{
										sets_found_head = dummy_var_for_sets_traversal.get_next(); 
										dummy_var_for_sets_traversal.get_previous().set_next(dummy_var_for_sets_traversal.get_next());
										dummy_var_for_sets_traversal.get_next().set_previous(dummy_var_for_sets_traversal.get_previous());

										ind_set_node_that_owns_set_to_check.set_next(dummy_var_for_nodes_traversal.get_next());
										ind_set_node_that_owns_set_to_check.set_previous(dummy_var_for_nodes_traversal.get_next());
										dummy_var_for_nodes_traversal.get_previous().set_next(dummy_var_for_nodes_traversal.get_next());
										dummy_var_for_nodes_traversal.get_next().set_previous(dummy_var_for_nodes_traversal.get_previous());
										ind_set_node_that_owns_set_to_check.set_length(ind_set_node_that_owns_set_to_check.get_length()-1);

									}
								}
								else{
									
									dummy_var_for_sets_traversal.get_previous().set_next(dummy_var_for_sets_traversal.get_next());
									dummy_var_for_sets_traversal.get_next().set_previous(dummy_var_for_sets_traversal.get_previous());

									dummy_var_for_nodes_traversal.get_previous().set_next(dummy_var_for_nodes_traversal.get_next());
									dummy_var_for_nodes_traversal.get_next().set_previous(dummy_var_for_nodes_traversal.get_previous());
									ind_set_node_that_owns_set_to_check.set_length(ind_set_node_that_owns_set_to_check.get_length()-1);

								}
								
								//reset values and loop again
								dummy_var_for_nodes_traversal = ind_set_node_that_owns_set_to_check.get_next(); // initial node that owns set to check, first in set is always -1
								dummy_var_for_sets_traversal = sets_found_head; // initial set to check, because the first one is the control array


								index_bigger_than_this_set_array = sets_found_head;
								index_bigger_than_this_set_node = ind_set_node_that_owns_set_to_check.get_next(); // initial node that owns set to check, first in set is always -1


							}

							dummy_var_for_sets_traversal = dummy_var_for_sets_traversal.get_next(); // move to the next set
							dummy_var_for_nodes_traversal = dummy_var_for_nodes_traversal.get_next();

							if ((index_bigger_than_this_set_array.get_array().length >= current_neighbors.length)){
								index_bigger_than_this_set_array = index_bigger_than_this_set_array.get_next();
								index_bigger_than_this_set_node = index_bigger_than_this_set_node.get_next();
							}
							else{

							}

						}while((dummy_var_for_sets_traversal != sets_found_head) && (compare_val != -1));

						if (compare_val != 0){
							if (compare_val == 1){
								next_set_index++;
							}

							if (sets_found_head == null){
								sets_found_head = new node_int_array(current_neighbors);
								ind_set_node_that_owns_set_to_check.add_unordered(new node(fellow_independants.get_next().get_value()));
								
								ind_set_node_that_owns_set_to_check.verbose_test();
							}
							else if ((index_bigger_than_this_set_array == sets_found_head) && (current_neighbors.length > sets_found_head.get_array().length)){// index_bigger_than_next_set_array.get_next() || ind_set_node_that_owns_set_to_check.get_length() == 1){ // so if the new set is going to be added onto the end of the sets 


								sets_found_head = new node_int_array(current_neighbors);								

								sets_found_head.set_next(index_bigger_than_this_set_array);
								sets_found_head.set_previous(index_bigger_than_this_set_array.get_previous());
								index_bigger_than_this_set_array.get_previous().set_next(sets_found_head);
								index_bigger_than_this_set_array.set_previous(sets_found_head);

								dummy_var_for_nodes_traversal = new node(fellow_independants.get_next().get_value());
								dummy_var_for_nodes_traversal.set_next(ind_set_node_that_owns_set_to_check.get_next());
								dummy_var_for_nodes_traversal.set_previous(ind_set_node_that_owns_set_to_check.get_next().get_previous());

								ind_set_node_that_owns_set_to_check.get_next().get_previous().set_next(dummy_var_for_nodes_traversal);
								ind_set_node_that_owns_set_to_check.get_next().set_previous(dummy_var_for_nodes_traversal);


								ind_set_node_that_owns_set_to_check.set_next(dummy_var_for_nodes_traversal);
								ind_set_node_that_owns_set_to_check.set_previous(dummy_var_for_nodes_traversal);

								ind_set_node_that_owns_set_to_check.set_length(ind_set_node_that_owns_set_to_check.get_length()+1);

							}
							else {
								dummy_var_for_sets_traversal = new node_int_array(current_neighbors);
								dummy_var_for_nodes_traversal = new node(fellow_independants.get_next().get_value());

								dummy_var_for_sets_traversal.set_next(index_bigger_than_this_set_array);
								dummy_var_for_sets_traversal.set_previous(index_bigger_than_this_set_array.get_previous());
								index_bigger_than_this_set_array.get_previous().set_next(dummy_var_for_sets_traversal);
								index_bigger_than_this_set_array.set_previous(dummy_var_for_sets_traversal);

								
								dummy_var_for_nodes_traversal.set_next(index_bigger_than_this_set_node);
								dummy_var_for_nodes_traversal.set_previous(index_bigger_than_this_set_node.get_previous());
								index_bigger_than_this_set_node.get_previous().set_next(dummy_var_for_nodes_traversal);
								index_bigger_than_this_set_node.set_previous(dummy_var_for_nodes_traversal);

								ind_set_node_that_owns_set_to_check.set_length(ind_set_node_that_owns_set_to_check.get_length()+1);

								ind_set_node_that_owns_set_to_check.verbose_test();

							}

						}
					}
				}
				else{
				}
				independants--;
				node_pool_node.delete(fellow_independants.get_next().get_value());
				fellow_independants.delete_next(true);
			}
		}


		if (sets_found_head == null)
			return null;
		else{


			int[][] return_value = new int[next_set_index][];
			return_value[0] = new int[ind_set_node_that_owns_set_to_check.length];
			return_value[0][0] = deleted_nodes;//deleted_nodes;
			System.arraycopy(ind_set_node_that_owns_set_to_check.print_array(), 0, return_value[0], 1, (return_value[0].length-1));


			dummy_var_for_sets_traversal = sets_found_head;
			int i = 1;

			do{
				//				System.out.println("Currently Dummy Var is length: "+dummy_var_for_sets_traversal.get_array().length+" and is: "+array2string(dummy_var_for_sets_traversal.get_array()));
				return_value[i] = dummy_var_for_sets_traversal.get_array();
				dummy_var_for_sets_traversal = dummy_var_for_sets_traversal.get_next();
				i++;
			}while(dummy_var_for_sets_traversal != sets_found_head);


			return return_value;
		}
	}

	

	///////////////////////////////////////////////////////////////////////////


	private int[][] find_sets_to_check(int initiator, node nodes, node head_max_star, int min){

		if (min < 0)
			min = 0;

		node fellow_independants = null;
		int independants;
		int deleted_nodes = 0;

		node_int_array sets_found_head = null;

		node_int_array index_bigger_than_this_set_array = null;
		node index_bigger_than_this_set_node = null;
		node_int_array dummy_var_for_sets_traversal = null;
		int[] node_pool = null;
		int[] current_neighbors = null;
		int compare_val;
		int next_set_index = 0;

		node ind_set_node_that_owns_set_to_check = null;
		node dummy_var_for_array_node_owner_of_found_set = null;
		node dummy_var_for_nodes_traversal = null;

		ind_set_node_that_owns_set_to_check = new node();
		dummy_var_for_array_node_owner_of_found_set = null;

		next_set_index = 1;

		node_int_array test_array = null; //delete, just for testing
		node test_node = null; //delete, just for testing
		int ii=1;

		independants = 0;

		fellow_independants =  nodes.split_nodes(independent_sets[node_ind_set[nodes.get_next().get_value()-1]].print_array());

		node_pool = nodes.combine(head_max_star);//nodes.print_array();

		independants = fellow_independants.get_length()-1;
		deleted_nodes += independants;

		compare_val = 1;


		if (next_set_index == 1 && node_pool[0] == -1){ //first node

			sets_found_head = new node_int_array(-1,null);
			next_set_index++;

			ind_set_node_that_owns_set_to_check.add_unordered(new node(fellow_independants.get_next().get_value()));


		}
		else{

			while(independants > 0){

				current_neighbors = Bochert_neighbor(fellow_independants.get_next().get_value(), node_pool);

				if (((current_neighbors == null) && (min == 0)) || ((current_neighbors != null) && (current_neighbors.length >= min))){ //if it's attached to something and attached to enough of something :)
					if (next_set_index == 1){//(current_neighbors.length == 0) && (index_sets == sets_to_check.get_next())){
						if (sets_found_head != null) // make error if it's not null
							System.out.println(sets_found_head.get_array()[-1]); 

						if (current_neighbors == null || current_neighbors.length == 0){
							sets_found_head = new node_int_array(-1,null);
						}
						else{
							sets_found_head = new node_int_array(current_neighbors);
						}

						next_set_index++;
						ind_set_node_that_owns_set_to_check.add_unordered(new node(fellow_independants.get_next().get_value()));


					}
					else if ((current_neighbors != null) && (current_neighbors.length != 0)){

						dummy_var_for_nodes_traversal = ind_set_node_that_owns_set_to_check.get_next(); // initial node that owns set to check, first in set is always -1
						dummy_var_for_sets_traversal = sets_found_head; // initial set to check, because the first one is the control array


						index_bigger_than_this_set_array = sets_found_head;
						index_bigger_than_this_set_node = ind_set_node_that_owns_set_to_check.get_next(); // initial node that owns set to check, first in set is always -1


						do{
							compare_val = sub_set(dummy_var_for_sets_traversal.get_array(),current_neighbors);							



							if (compare_val == 0){ // is contained in or matches a set already in there
								break;
							}
							else if (compare_val == -1){ // the set conatins the set that's already saved
								
								if (dummy_var_for_sets_traversal == sets_found_head){
									if (sets_found_head.get_next() == sets_found_head){
										sets_found_head = null;
										ind_set_node_that_owns_set_to_check = null;
										ind_set_node_that_owns_set_to_check = new node();
										
										sets_found_head = new node_int_array(current_neighbors);
										ind_set_node_that_owns_set_to_check.add_unordered(new node(fellow_independants.get_next().get_value()));
										
										compare_val = 0;
										break;
									}
									else{
										sets_found_head = dummy_var_for_sets_traversal.get_next(); 
										dummy_var_for_sets_traversal.get_previous().set_next(dummy_var_for_sets_traversal.get_next());
										dummy_var_for_sets_traversal.get_next().set_previous(dummy_var_for_sets_traversal.get_previous());

										ind_set_node_that_owns_set_to_check.set_next(dummy_var_for_nodes_traversal.get_next());
										ind_set_node_that_owns_set_to_check.set_previous(dummy_var_for_nodes_traversal.get_next());
										dummy_var_for_nodes_traversal.get_previous().set_next(dummy_var_for_nodes_traversal.get_next());
										dummy_var_for_nodes_traversal.get_next().set_previous(dummy_var_for_nodes_traversal.get_previous());
										ind_set_node_that_owns_set_to_check.set_length(ind_set_node_that_owns_set_to_check.get_length()-1);

									}
								}
								else{
									
									dummy_var_for_sets_traversal.get_previous().set_next(dummy_var_for_sets_traversal.get_next());
									dummy_var_for_sets_traversal.get_next().set_previous(dummy_var_for_sets_traversal.get_previous());

									dummy_var_for_nodes_traversal.get_previous().set_next(dummy_var_for_nodes_traversal.get_next());
									dummy_var_for_nodes_traversal.get_next().set_previous(dummy_var_for_nodes_traversal.get_previous());
									ind_set_node_that_owns_set_to_check.set_length(ind_set_node_that_owns_set_to_check.get_length()-1);

								}
								
								//reset values and loop again
								dummy_var_for_nodes_traversal = ind_set_node_that_owns_set_to_check.get_next(); // initial node that owns set to check, first in set is always -1
								dummy_var_for_sets_traversal = sets_found_head; // initial set to check, because the first one is the control array


								index_bigger_than_this_set_array = sets_found_head;
								index_bigger_than_this_set_node = ind_set_node_that_owns_set_to_check.get_next(); // initial node that owns set to check, first in set is always -1


							}

							dummy_var_for_sets_traversal = dummy_var_for_sets_traversal.get_next(); // move to the next set
							dummy_var_for_nodes_traversal = dummy_var_for_nodes_traversal.get_next();

							if ((index_bigger_than_this_set_array.get_array().length >= current_neighbors.length)){
								index_bigger_than_this_set_array = index_bigger_than_this_set_array.get_next();
								index_bigger_than_this_set_node = index_bigger_than_this_set_node.get_next();
							}
							else{

							}

						}while((dummy_var_for_sets_traversal != sets_found_head) && (compare_val != -1));

						if (compare_val != 0){
							if (compare_val == 1){
								next_set_index++;
							}

							if (sets_found_head == null){
								sets_found_head = new node_int_array(current_neighbors);
								ind_set_node_that_owns_set_to_check.add_unordered(new node(fellow_independants.get_next().get_value()));
								
								ind_set_node_that_owns_set_to_check.verbose_test();
							}
							else if ((index_bigger_than_this_set_array == sets_found_head) && (current_neighbors.length > sets_found_head.get_array().length)){// index_bigger_than_next_set_array.get_next() || ind_set_node_that_owns_set_to_check.get_length() == 1){ // so if the new set is going to be added onto the end of the sets 


								sets_found_head = new node_int_array(current_neighbors);								

								sets_found_head.set_next(index_bigger_than_this_set_array);
								sets_found_head.set_previous(index_bigger_than_this_set_array.get_previous());
								index_bigger_than_this_set_array.get_previous().set_next(sets_found_head);
								index_bigger_than_this_set_array.set_previous(sets_found_head);

								dummy_var_for_nodes_traversal = new node(fellow_independants.get_next().get_value());
								dummy_var_for_nodes_traversal.set_next(ind_set_node_that_owns_set_to_check.get_next());
								dummy_var_for_nodes_traversal.set_previous(ind_set_node_that_owns_set_to_check.get_next().get_previous());

								ind_set_node_that_owns_set_to_check.get_next().get_previous().set_next(dummy_var_for_nodes_traversal);
								ind_set_node_that_owns_set_to_check.get_next().set_previous(dummy_var_for_nodes_traversal);


								ind_set_node_that_owns_set_to_check.set_next(dummy_var_for_nodes_traversal);
								ind_set_node_that_owns_set_to_check.set_previous(dummy_var_for_nodes_traversal);

								ind_set_node_that_owns_set_to_check.set_length(ind_set_node_that_owns_set_to_check.get_length()+1);

							}
							else {
								dummy_var_for_sets_traversal = new node_int_array(current_neighbors);
								dummy_var_for_nodes_traversal = new node(fellow_independants.get_next().get_value());

								dummy_var_for_sets_traversal.set_next(index_bigger_than_this_set_array);
								dummy_var_for_sets_traversal.set_previous(index_bigger_than_this_set_array.get_previous());
								index_bigger_than_this_set_array.get_previous().set_next(dummy_var_for_sets_traversal);
								index_bigger_than_this_set_array.set_previous(dummy_var_for_sets_traversal);

								
								dummy_var_for_nodes_traversal.set_next(index_bigger_than_this_set_node);
								dummy_var_for_nodes_traversal.set_previous(index_bigger_than_this_set_node.get_previous());
								index_bigger_than_this_set_node.get_previous().set_next(dummy_var_for_nodes_traversal);
								index_bigger_than_this_set_node.set_previous(dummy_var_for_nodes_traversal);

								ind_set_node_that_owns_set_to_check.set_length(ind_set_node_that_owns_set_to_check.get_length()+1);

								ind_set_node_that_owns_set_to_check.verbose_test();

							}

						}
					}
				}
				else{
				}
				independants--;
				fellow_independants.delete_next(true);


			}
		}


		if (sets_found_head == null)
			return null;
		else{


			int[][] return_value = new int[next_set_index][];
			return_value[0] = new int[ind_set_node_that_owns_set_to_check.length];
			return_value[0][0] = deleted_nodes;//deleted_nodes;
			System.arraycopy(ind_set_node_that_owns_set_to_check.print_array(), 0, return_value[0], 1, (return_value[0].length-1));


			dummy_var_for_sets_traversal = sets_found_head;
			int i = 1;

			do{
				//				System.out.println("Currently Dummy Var is length: "+dummy_var_for_sets_traversal.get_array().length+" and is: "+array2string(dummy_var_for_sets_traversal.get_array()));
				return_value[i] = dummy_var_for_sets_traversal.get_array();
				dummy_var_for_sets_traversal = dummy_var_for_sets_traversal.get_next();
				i++;
			}while(dummy_var_for_sets_traversal != sets_found_head);


			return return_value;
		}
	}


	private int[][] find_sets_to_check_old(int initiator, node nodes, node head_max_star, int min){
		//node nodes_copy = nodes.copy();
		if (min < 0)
			min = 0;

		//		System.out.println("Entering find_sets_to_check on: "+initiator+" nodes: "+nodes.print_list()+" head max star: "+head_max_star.print_list()+" max: "+max);

		//		System.out.println("nodes before deletion are: "+array2string(nodes.print_array())+" and length is: "+nodes.length);
		node fellow_independants =  nodes.split_nodes(independent_sets[node_ind_set[initiator-1]].print_array());
		//		System.out.println("nodes after deletion are: "+array2string(nodes.print_array())+" and length is: "+nodes.length+" and there are "+fellow_independants.get_length()+" fellow independs");


		int independants = fellow_independants.get_length()-1;
		int deleted_nodes = independants;
		//System.out.println("fellow independents: "+array2string(fellow_independants.print_array()));

		node_int_array sets_to_check = new node_int_array(independants+1);
		node_int_array index_sets = new node_int_array();
		node_int_array index_bigger_than_next_set = null;
		sets_to_check.insert_next(index_sets);
		node_int_array temp_index_node_array = null;
		int[] temp_index_array = null;

		int[] nodes_copy_arr = nodes.combine(head_max_star);//nodes.print_array();

		int[] current_neighbors;

		int compare_val = 1;

		int num_sets = 1;

		node ind_set_node_that_owns_set_to_check = new node();
		node temp_head;
		node temp_traverse = null;


		//		System.out.println("Nodes_copy_arr: "+array2string(nodes_copy_arr)+" and fellow_ind is: "+fellow_independants.print_list());

		if (nodes_copy_arr[0] == -1){
			//			System.out.println("index sets = "+index_sets);

			temp_index_array = new int[1];
			temp_index_array[0] = -1;
			index_sets.set_array(temp_index_array);
			temp_index_node_array = new node_int_array(); 
			index_sets.insert_next(temp_index_node_array);
			index_sets = index_sets.get_next();
			num_sets++;

			temp_head = new node(fellow_independants.get_next().get_value());
			ind_set_node_that_owns_set_to_check.add_unordered(temp_head);


		}
		else{

			while(independants > 0){


				current_neighbors = Bochert_neighbor(fellow_independants.get_next().get_value(), nodes_copy_arr);


				//				System.out.println("looking at: "+fellow_independants.get_next().get_value()+" Current_neighbors: "+array2string(current_neighbors)+" independants left: "+independants);


				if (current_neighbors.length >= min){
					if (ind_set_node_that_owns_set_to_check.get_length() == 1){//(current_neighbors.length == 0) && (index_sets == sets_to_check.get_next())){

						if (current_neighbors.length == 0){
							temp_index_array = new int[1];
							temp_index_array[0] = -1;
							index_sets.set_array(temp_index_array);
						}
						else
							index_sets.set_array(current_neighbors);

						temp_index_node_array = new node_int_array(); 
						index_sets.insert_next(temp_index_node_array);
						index_sets = index_sets.get_next();
						num_sets++;


						temp_head = new node(fellow_independants.get_next().get_value());
						ind_set_node_that_owns_set_to_check.add_unordered(temp_head);

					}
					else if (current_neighbors.length != 0){
						temp_traverse = ind_set_node_that_owns_set_to_check.get_next();
						temp_index_node_array = sets_to_check.get_next();
						index_bigger_than_next_set = sets_to_check;
						while(temp_index_node_array.get_next() != null){


							compare_val = sub_set(temp_index_node_array.get_array(),current_neighbors);

							if (compare_val == 0)
								break;
							else if (compare_val == -1){

								temp_index_node_array.set_array(current_neighbors);

								temp_traverse.set_value(fellow_independants.get_next().get_value());


							}

							temp_index_node_array = temp_index_node_array.get_next();

							if (index_bigger_than_next_set.get_next().get_array().length >= current_neighbors.length){
								index_bigger_than_next_set = index_bigger_than_next_set.get_next();
								temp_traverse = temp_traverse.get_next();
							}

						}
						if (compare_val == 1){
							//						System.out.println("index sets = "+index_sets);
							if (temp_index_node_array == index_bigger_than_next_set.get_next() || ind_set_node_that_owns_set_to_check.get_length() == 1){

								index_sets.set_array(current_neighbors);
								//index_sets.copy_array(current_neighbors);

								temp_index_node_array = new node_int_array(); 
								index_sets.insert_next(temp_index_node_array);
								index_sets = index_sets.get_next();

								temp_head = new node(fellow_independants.get_next().get_value());
								ind_set_node_that_owns_set_to_check.add_unordered(temp_head);

							}
							else {
								temp_index_node_array = new node_int_array(current_neighbors);
								temp_index_node_array.insert_next(index_bigger_than_next_set.get_next());

								index_bigger_than_next_set.insert_next(temp_index_node_array);

								temp_head = new node(fellow_independants.get_next().get_value());
								temp_head.set_next(temp_traverse.get_next());
								temp_head.set_previous(temp_traverse);
								temp_traverse.get_next().set_previous(temp_head);
								temp_traverse.set_next(temp_head);
								ind_set_node_that_owns_set_to_check.set_length(ind_set_node_that_owns_set_to_check.get_length()+1);


							}

							num_sets++;

						}
					}
				}
				else{
					//					System.out.println("turns out the size of the found set: "+current_neighbors.length+" is < to the current max: "+max);
				}
				independants--;
				fellow_independants.delete_next(true);


			}
		}

		if (sets_to_check.get_next() == index_sets)
			return null;
		else{
			int[][] return_value = new int[num_sets][];
			//			System.out.println(" currently ind set node that owns set to check is: "+ind_set_node_that_owns_set_to_check.print_list()+" and length is: "+ind_set_node_that_owns_set_to_check.get_length());
			return_value[0] = new int[ind_set_node_that_owns_set_to_check.length];
			return_value[0][0] = deleted_nodes;//deleted_nodes;
			System.arraycopy(ind_set_node_that_owns_set_to_check.print_array(), 0, return_value[0], 1, (return_value[0].length-1));


			temp_index_node_array = sets_to_check.get_next();
			int i = 1;

			while(temp_index_node_array.get_next() != null){

				return_value[i] = temp_index_node_array.get_array();
				temp_index_node_array = temp_index_node_array.get_next();
				i++;
			}

			return return_value;
		}
	}

	
	private int[] insert_value(int[] array, int value){
		int parser=array.length;
		int[] return_array = new int[array.length+1];
		
		for (int i = 0; i < array.length; i++){
			if (array[i] > value){
				parser = i;
				i = array.length;
			}			
		}
		
//		System.arraycopy(src, srcPos, dest, destPos, length);
		System.arraycopy(array, 0, return_array, 0, parser);
		return_array[parser] = value;
		System.arraycopy(array, parser, return_array, parser+1, array.length-parser);
		
		return return_array;
	}
	
	
	private int sub_set(int[] current_list, int[] new_list){
		int new_i = 0;
		int old_i = 0;
		boolean identical = true;

		if ((current_list.length > 0) && (current_list[0] == -1))
			return -1;

		while ((old_i < current_list.length) && (new_i < new_list.length)){
			if (current_list[old_i] == new_list[new_i]){
				new_i++;
			}
			else{
				identical = false;
			}
			old_i++;
		}

		if (new_i == new_list.length)
			return 0; // new_list is contained in the old list
		else
			if (identical)
				return -1; // the old list is contained in the new list
			else
				return 1; // the new list is not contained in the old list or vise versa
	}

	
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	private int[] Bochert3(int[] nodes, int current_max){


		int[] connected_nodes = nodes;
		int[] temp_connected_nodes = null;
		int[] max_star = null;
		int[] temp_max = null;
		int temp_current_max = current_max;
		int current_node;
		int node_that_found_max_star = -1;

		B_calls++;
		B_iteration_deep++;
		
		node nodes_to_consider = new node(), head_max_star = new node();
		nodes_to_consider.create_nodes(nodes);
		int length_nodes_left = nodes.length; 
		int[][] sets_to_check;
		int index_ind_sets = 0;

		
//		while (nodes_to_consider.get_next().get_value() != -1)
//		{

			
			if (length_nodes_left <= current_max){
				B_iteration_deep--;
				return null; //that is to say there aren't enough nodes left to make a star big enough to beat the current max
			}

			current_node = nodes_to_consider.get_next().get_value();


				sets_to_check = find_sets_to_check_all(current_node, nodes_to_consider, head_max_star, temp_current_max);
				
				
				if (sets_to_check != null){

					length_nodes_left = length_nodes_left - sets_to_check[0][0];

					index_ind_sets = 1;

					if (index_ind_sets<sets_to_check.length){
						if (sets_to_check[index_ind_sets][0] == -1 && temp_current_max == 0 && node_that_found_max_star == -1){
							node_that_found_max_star = sets_to_check[0][index_ind_sets];
							temp_current_max = 1;
							max_star = null;
							index_ind_sets++;

						}
						else if (sets_to_check[index_ind_sets][0] != -1){
							while ((sets_to_check != null) && (index_ind_sets < sets_to_check.length)){

								if (B_iteration_deep <= 3){
									this.insert_spaces_for_iteration("B");
									System.out.println("Calls: "+B_calls+" Nodes left to consider: "+array2string(nodes_to_consider.print_array()));
								}

								
								current_node = sets_to_check[0][index_ind_sets];

//								if (B_iteration_deep <= 3){
//									this.insert_spaces_for_iteration("B");
//									System.out.println("Looking at node: "+current_node+" which is connected to: "+array2string(sets_to_check[index_ind_sets]));
//								}
								
								if(sets_to_check[index_ind_sets].length >= temp_current_max) {
									if (sets_to_check[index_ind_sets].length == 1) {
										temp_max = sets_to_check[index_ind_sets];
									}
									else{
										
										if (temp_current_max == 0){
											temp_max = Bochert3(sets_to_check[index_ind_sets], temp_current_max);
										}
										else{
											temp_max = Bochert3(sets_to_check[index_ind_sets], temp_current_max-1);
										}
									}
									if ((temp_max != null)  && (temp_max.length >= temp_current_max)){

										nodes_to_consider.add(head_max_star);
										node_that_found_max_star = current_node;
										max_star = temp_max;
										temp_current_max = max_star.length+1;
										head_max_star = nodes_to_consider.split_nodes(max_star);
										
										//sets_to_check = find_sets_to_check_all(current_node, nodes_to_consider, head_max_star, temp_current_max);
										//index_ind_sets = 1;
										
									}
								}
								index_ind_sets++;
							}
						}
					}

				}

			
		//}	

		if (node_that_found_max_star == -1){
			B_iteration_deep--;		
			return null;
		}

		int[] temp_finder = {node_that_found_max_star};
		node finder = new node(temp_finder);

		head_max_star.add(finder);

		B_iteration_deep--;
		return head_max_star.print_array();

	}



	
	


/////////////////////////////////////////////////////////////////////////////////////////////////////////
	private int[] Bochert2(int[] nodes, int current_max){
		//Bochert, uses find sets but not "don't check nodes already in max"


		int[] connected_nodes = nodes;
		int[] temp_connected_nodes = null;
		int[] max_star = null;
		int[] temp_max = null;
		int temp_current_max = current_max;
		int current_node;
		int node_that_found_max_star = -1;

		B_calls++;
		B_iteration_deep++;

//		System.out.println("Entering Bochert2, current number of calls is: "+B_calls);
		
		node nodes_to_consider = new node(), head_max_star = new node();
		nodes_to_consider.create_nodes(nodes);
		int length_nodes_left = nodes.length; 
		int[][] sets_to_check;
		int index_ind_sets = 0;

		while (nodes_to_consider.get_next().get_value() != -1)
		{

			if (B_iteration_deep <= 3){
				this.insert_spaces_for_iteration("B");
				System.out.println("Calls: "+B_calls+" Nodes left to consider: "+array2string(nodes_to_consider.print_array()));
			}
			
			if (length_nodes_left <= current_max){
				B_iteration_deep--;
				return null; //that is to say there aren't enough nodes left to make a star big enough to beat the current max
			}

			current_node = nodes_to_consider.get_next().get_value();

			if (use_ind_sets){

				if (node_ind_set[current_node-1] != 0)
					sets_to_check = find_sets_to_check(current_node, nodes_to_consider, head_max_star, temp_current_max);
				else {
					sets_to_check = new int[2][];
					sets_to_check[1] = Bochert_neighbor(current_node, nodes_to_consider.print_array());
					sets_to_check[0] = new int[2];
					sets_to_check[0][0] = 1;
					sets_to_check[0][1] = current_node;
				}

/*				if (B_iteration_deep <= 3){
					if (sets_to_check != null){
						this.insert_spaces_for_iteration("B");
						System.out.println("Sets_to_check:");
						for (int i = 0; i < sets_to_check.length; i++)
							System.out.println(array2string(sets_to_check[i]));
					}
				}
*/
				
				if (sets_to_check != null){

					length_nodes_left = length_nodes_left - sets_to_check[0][0];

					index_ind_sets = 1;

					if (index_ind_sets<sets_to_check.length){
						if (sets_to_check[index_ind_sets][0] == -1 && temp_current_max == 0 && node_that_found_max_star == -1){
							node_that_found_max_star = sets_to_check[0][index_ind_sets];
							temp_current_max = 1;
							max_star = null;
							index_ind_sets++;

						}
						else if (sets_to_check[index_ind_sets][0] != -1){
							for(int s = index_ind_sets; s<sets_to_check.length; s++){

								current_node = sets_to_check[0][index_ind_sets];

//								if (B_iteration_deep <= 3){
//									this.insert_spaces_for_iteration("B");
//									System.out.println("Looking at node: "+current_node+" which is connected to: "+array2string(sets_to_check[index_ind_sets]));
//								}
								
								if(sets_to_check[index_ind_sets].length >= temp_current_max) {
									if (sets_to_check[index_ind_sets].length == 1) {
										temp_max = sets_to_check[index_ind_sets];
									}
									else{
										
										
										if (temp_current_max == 0){
											temp_max = Bochert2(sets_to_check[index_ind_sets], temp_current_max);
										}
										else{
											temp_max = Bochert2(sets_to_check[index_ind_sets], temp_current_max-1);
										}
									}
									if ((temp_max != null)  && (temp_max.length >= temp_current_max)){
										nodes_to_consider.add(head_max_star);
										node_that_found_max_star = current_node;
										max_star = temp_max;
										temp_current_max = max_star.length+1;
										head_max_star = nodes_to_consider.split_nodes(max_star);

									}
								}
								index_ind_sets++;
							}
						}
					}

				}

			}
		}	

		if (node_that_found_max_star == -1){
			B_iteration_deep--;		
			return null;
		}

		int[] temp_finder = {node_that_found_max_star};
		node finder = new node(temp_finder);

		head_max_star.add(finder);

		B_iteration_deep--;
		return head_max_star.print_array();

	}








	private int[] Bochert(int[] nodes, int current_max){
		//original Bochert



		int[] connected_nodes = nodes;
		int[] temp_connected_nodes = null;
		int[] max_star = null;
		int[] temp_max = null;
		int temp_current_max = current_max;
		int current_node;
		int node_that_found_max_star = -1;

		//		System.out.println(B_calls);

		//		if (use_ind_sets && (B_calls > 1000000))
		//			return null;

		B_calls++;
		B_iteration_deep++;

		//		insert_spaces_for_iteration("B");
		//		System.out.println("Bochert being called with nodes: "+array2string(nodes)+" and max of: "+current_max);

		node nodes_to_consider = new node(), head_max_star = new node();
		nodes_to_consider.create_nodes(nodes);
		int length_nodes_left = nodes.length; 
		int[][] sets_to_check;
		int index_ind_sets = 0;

		if (B_iteration_deep < 10){
			B_it_calls[B_iteration_deep]=0;
		}

		while (nodes_to_consider.get_next().get_value() != -1)
		{
			//			insert_spaces_for_iteration("B");
			//			System.out.println("A New nodes to consider loop with NtC of: "+nodes_to_consider.print_list()+" and length is: "+nodes_to_consider.get_length());
			//						System.out.println("Looking at:"+(nodes_to_consider.get_next().get_value())+" Finding_N:"+node_that_found_max_star+" M_Star:"+array2string(head_max_star.print_array(0))+" Nodes2Consider:"+nodes_to_consider.print_list()+" and temp_current_max="+temp_current_max);

			if (length_nodes_left <= current_max){
				B_iteration_deep--;
				return null; //that is to say there aren't enough nodes left to make a star big enough to beat the current max
			}

			current_node = nodes_to_consider.get_next().get_value();

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (use_ind_sets && (node_ind_set[current_node-1] != 0)){


				this.insert_spaces_for_iteration("B");
				System.out.println("entering Bochert, B-calls: "+B_calls);
				System.out.println("Nodes: "+array2string(nodes)+" and current_max: "+current_max);


				//				insert_spaces_for_iteration("B");
				//				System.out.println("B Entering sets to check - nodes to consider is currently: "+array2string(nodes_to_consider.print_array()));
				sets_to_check = find_sets_to_check(current_node, nodes_to_consider, head_max_star, temp_current_max);
				//				insert_spaces_for_iteration("B");
				//				System.out.println("B Just ran find sets, nodes to consider is now: "+array2string(nodes_to_consider.print_array()));

				System.out.println("Sets to check returned:");
				if(sets_to_check == null)
					System.out.println("null");
				else
					for(int i=0; i<sets_to_check.length; i++)
						System.out.println(array2string(sets_to_check[i]));



				if (sets_to_check != null){

					length_nodes_left = length_nodes_left - sets_to_check[0][0];

					//					for (int i = 0; i<sets_to_check.length; i++){
					//						insert_spaces_for_iteration("B");
					//						System.out.println("C sets to check index: "+i+" is "+array2string(sets_to_check[i]));
					//					}




					index_ind_sets = 1;

					if (sets_to_check[index_ind_sets][0] == -1 && temp_current_max == 0 && node_that_found_max_star == -1){
						//						insert_spaces_for_iteration("B");
						//						System.out.println("In sets to check area, entered special case that null set is best set");
						//						current_node = sets_to_check[0][index_ind_sets];
						node_that_found_max_star = sets_to_check[0][index_ind_sets];
						temp_current_max = 1;
						max_star = null;
						index_ind_sets++;

					}
					else if ((sets_to_check[index_ind_sets][0] != -1) && (index_ind_sets<sets_to_check.length))
						for(int s = index_ind_sets; s<sets_to_check.length; s++){

							current_node = sets_to_check[0][index_ind_sets];

							//							insert_spaces_for_iteration("B");
							//							System.out.println("looking into for loop. Looking first at: "+current_node+" , index_ind_sets: "+index_ind_sets+" sets_to_check.length: "+sets_to_check.length);

							if(sets_to_check[index_ind_sets].length >= temp_current_max) {

								//								insert_spaces_for_iteration("B");
								//								System.out.println("sets_to_check[index_ind_sets].length >= temp_current_max (temp_current_max="+temp_current_max);

								if (sets_to_check[index_ind_sets].length == 1) {

									//									insert_spaces_for_iteration("B");
									//									System.out.println("sets_to_check[index_ind_sets].length == 1");

									temp_max = sets_to_check[index_ind_sets];
								}
								else{
									if (B_iteration_deep < 10){
										B_it_calls[B_iteration_deep]++;
									}


									if (temp_current_max == 0){

										//										insert_spaces_for_iteration("B");
										//										System.out.println("D In ind_set section, calling Bochert. Nodes calling: "+current_node+" with the set to check of: "+array2string(sets_to_check[index_ind_sets])+" with the max star set to be: "+node_that_found_max_star+" "+array2string(max_star));
										temp_max = Bochert(sets_to_check[index_ind_sets], temp_current_max);
									}
									else{
										//										insert_spaces_for_iteration("B");
										//										System.out.println("D In ind_set section, calling Bochert. Nodes calling: "+current_node+" with the set to check of: "+array2string(sets_to_check[index_ind_sets])+" with the max star set to be: "+node_that_found_max_star+" "+array2string(max_star));
										temp_max = Bochert(sets_to_check[index_ind_sets], temp_current_max-1);
									}

									//									insert_spaces_for_iteration("B");
									//									System.out.println("AIn sets to check area, set: "+array2string(sets_to_check[index_ind_sets])+" returned a star of: "+array2string(temp_max));

								}
								//System.out.println("poo");
								//								if ((temp_max != null) && (temp_max[0] == -1)){
								//									System.out.println(array2string(temp_max)+" and length is "+temp_max.length);
								//									System.out.println(temp_max[temp_max[0]]);
								//								}

								if ((temp_max != null)  && (temp_max.length >= temp_current_max)){

									//									insert_spaces_for_iteration("B");
									//									System.out.println("BIn sets to check area, found new max star, "+array2string(sets_to_check[index_ind_sets])+" returned a star of: "+array2string(temp_max)+" which is greater than: "+array2string(max_star));

									nodes_to_consider.add(head_max_star);
									node_that_found_max_star = current_node;
									max_star = temp_max;
									temp_current_max = max_star.length+1;
									head_max_star = nodes_to_consider.split_nodes(max_star);

								}
							}
						}

				}

				//				insert_spaces_for_iteration("B");
				//				System.out.println("2nodes to consider are: "+nodes_to_consider.print_list()+" and length is: "+nodes_to_consider.get_length());

			}
			/////////////////////////////////////////////////////////////////////////////////////////////////////	
			else{
				if (use_ind_sets){
					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					System.out.println("In the else statement of Bochert, so node "+current_node+" is in set 0?");
				}
				//				insert_spaces_for_iteration("B");
				//				System.out.println("3nodes to consider are: "+nodes_to_consider.print_list()+" and length is: "+nodes_to_consider.get_length());
				temp_connected_nodes = Bochert_neighbor(current_node, nodes_to_consider.combine(head_max_star));
				//				insert_spaces_for_iteration("B");
				//				System.out.println("4nodes to consider are: "+nodes_to_consider.print_list()+" and length is: "+nodes_to_consider.get_length());

				if (temp_connected_nodes.length == 0 && temp_current_max == 0 && node_that_found_max_star == -1){
					node_that_found_max_star = current_node;
					temp_current_max = 1;
					max_star = null;
				}
				else if ((temp_connected_nodes.length >= temp_current_max) && (temp_connected_nodes.length != 0)) {

					if (temp_connected_nodes.length == 1) {
						temp_max = temp_connected_nodes;
					}
					else{
						if (B_iteration_deep < 10){
							B_it_calls[B_iteration_deep]++;
						}

						if (temp_current_max == 0){
							//							insert_spaces_for_iteration("B");
							//							System.out.println("F In regular section, calling Bochert. Nodes calling: "+current_node+" with the set to check of: "+array2string(temp_connected_nodes)+" with the max star set to be: "+node_that_found_max_star+" "+array2string(max_star));
							temp_max = Bochert(temp_connected_nodes, temp_current_max);
						}
						else{
							//							insert_spaces_for_iteration("B");
							//							System.out.println("F In regular section, calling Bochert. Nodes calling: "+current_node+" with the set to check of: "+array2string(temp_connected_nodes)+" with the max star set to be: "+node_that_found_max_star+" "+array2string(max_star));
							temp_max = Bochert(temp_connected_nodes, temp_current_max-1);
						}
					}
					if ((temp_max != null) && (temp_max.length >= temp_current_max)){
						//						insert_spaces_for_iteration("B");
						//						System.out.println("in non ind set section, clique: "+array2string(temp_max)+" found by looking at "+current_node+" is larger than the previous star: "+array2string(head_max_star.print_array())+" found by "+node_that_found_max_star+" max was: "+temp_current_max+" but it's now: "+(temp_max.length+1));

						nodes_to_consider.add(head_max_star);
						node_that_found_max_star = current_node;
						max_star = temp_max;
						temp_current_max = max_star.length+1;
						head_max_star = nodes_to_consider.split_nodes(max_star);

					}
				}


				length_nodes_left--;
				nodes_to_consider.delete_next(true);

			}
		}	

		if (node_that_found_max_star == -1){
			B_iteration_deep--;		
			return null;
		}

		int[] temp_finder = {node_that_found_max_star};
		node finder = new node(temp_finder);

		head_max_star.add(finder);

		//		if (head_max_star.print_array()[0] == -1){
		//			System.out.println(head_max_star.print_array()[-1]);
		//		}

		//		insert_spaces_for_iteration("B");
		//		System.out.println("Z Returning: "+head_max_star.print_list());
		B_iteration_deep--;
		return head_max_star.print_array();

	}

	private void insert_spaces_for_iteration(String mode){
		if (mode == "B"){
			if (use_ind_sets)
				for (int i = 0; i<=B_iteration_deep; i++)
					System.out.print("*");
			else
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


	private String translate(int[] nums){
		int next_value, current_consideration, current_set = 1;
		int Max_star = 4;

		node head = new node(nums);
		String return_value = "";
		int max_value = head.get_next().get_previous().get_value();


		//		System.out.println(head.print_list());
		//		System.out.println("max value is:"+max_value);

		while (head.get_next().get_value() != -1){

			current_set = 1;
			next_value = head.get_next().get_value();
			head.delete_next(true);
			//			System.out.println(next_value);



			current_consideration = next_value+Max_star;

			while (current_consideration <= max_value){
				if (head.delete(current_consideration)){
					//					System.out.println("found and deleted:"+current_consideration);
					current_set++;
				}
				current_consideration+=Max_star;
			}

			return_value = return_value + Integer.toString(current_set) + " ";

		}

		return_value = return_value + "**" +this.array2string(nums); 
		return return_value;

	}

	private int[] Bochert_neighbor(int n, int[] nodes){

		if (nodes == null || nodes[0] == -1)
			return null;

		int [] temp = new int[nodes.length];
		int size = 0;

		for (int i = 0; i<nodes.length; i++){

			if (((n-1) != (nodes[i]-1)) && (graph[n-1][nodes[i]-1] == internal_connected)){
				temp[size] = nodes[i];
				size++;
			}
		}

		int[] result = new int[size];
		System.arraycopy(temp, 0, result, 0, size);
		return result;

	}



	public int[] BronKerbosch (int[] R, int[] P, int[] X){

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


	}

	public graph(String file_name){

		try {

			File myFile = new File("Clique\\graph_binaries\\"+file_name);
			FileReader fileReader = new FileReader(myFile);

			BufferedReader reader = new BufferedReader(fileReader);

			String line;

			int count = 0, total_nodes, node1, node2;


			while (((line = reader.readLine()) != null) && (line.charAt(0) != 'p')){
				System.out.println(line);
			}

			System.out.println(line);
			line = line.substring(7);


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

	public static void main(String args[]) throws Exception
	{


		int[][] testie = 
		{		{0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0}}; 

		int[][] WorstCase = {
				{0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},

				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,1,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,0},

				{1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};


		//		graph g = new graph(WorstCase);
		//		graph g = new graph(testie);
		//				graph g = new graph(16, 4);
			graph g = new graph("brock200_1.clq");
		//	graph g = new graph("hamming6-2.clq");
		//	graph g = new graph("c-fat500-1.clq");
		//	graph g = new graph("keller6.clq");

//		g.disp_graph();	
//		g.pause();
		
		int[] r = null, x = null;
		int[] p = g.find_P();

		
		
//		int[] a = {1,2,3}, b={4,5,6,7}, c={1,2}, d={1,2,3}, e={1,2,3,4};		
		
		//System.out.println(g.sub_set(a,b));
		//System.out.println(g.sub_set(a,c));
		//System.out.println(g.sub_set(a,d));
		//System.out.println(g.sub_set(a,e));


		/*		node an = new node(a), bn = new node(b), cn = new node(c), dn = new node(d);
		node bn1 = new node(b);
		node an1 = an.copy(), an2 = an.copy();
		bn1.add(an1);
		node bn2 = new node(b);
		bn2.add_unordered(an2);
		node dn1 = new node(d), dn2 = new node(d), dn3 = new node(d);
		dn1.delete(2);
		dn2.split_nodes(d);
		dn3.delete(dn);

		System.out.println("a="+g.array2string(an.print_array(0)));
		System.out.println("b="+g.array2string(bn.print_array(0)));
		System.out.println("c="+g.array2string(cn.print_array(0)));
		System.out.println("d="+g.array2string(dn.print_array(0)));
		System.out.println("b+a=int[]="+g.array2string(bn.combine(an, 0)));
		System.out.println("b+a=add="+g.array2string(bn1.print_array(0)));
		System.out.println("b+a=add_unordered="+g.array2string(bn2.print_array(0)));
		System.out.println("d(del 1)="+g.array2string(dn1.print_array(0)));
		System.out.println("d(del d)="+g.array2string(dn2.print_array(0)));
		System.out.println("d(del dn)="+g.array2string(dn3.print_array(0)));
		 */		

		/*		node a = new node();
		node b = new node(5);
		System.out.println("a: "+a.print_list());
		a.add(b);
		System.out.println("a: "+a.print_list());
		System.out.println(a.delete(b));
		System.out.println("a: "+a.print_list());
		 */

		

		int[] solution = {17,19,38,67,72,80,84,86,89,92,91,93,101,107,133,134,135,141,149,177,185};
		int[] test_nodes = {1,2,6,8,13,15,16,20,27,33,45,53,84,121,155,165,190};
		int[] test_nodes_bochert = {4,26,32,41,46,48,83,100,103,104,107,120,122,132,137,138,144,175,180,191,199};
		int[] test_nodes_bochert_w_findsets = {1,8,10,13,18,20,28,32,36,48,75,79,81,108,155,159,167,190,194};
		int[] solution_2 = {4,26,32,41,46,48,83,100,103,104,107,120,122,132,137,138,144,175,180,191,199};
		int[] solution_3 = {18,20,39,68,73,81,85,87,90,92,93,94,102,108,134,135,136,142,150,178,186};
		
		System.out.println(g.is_star(solution_3,true));
		//g.pause();
		
		long b_calls1, b_calls2;
		
		int[] bochresult1 = null;
		int[] bochresult2 = null;

		//	g.disp_graph();

//		bochresult1 = g.Bochert(g.all_neighbors(-1), 0);
//		b_calls1 = g.B_calls;
//		g.B_calls = 0;
		bochresult2 = g.Bochert3(g.all_neighbors(-1), 0);
		b_calls2 = g.B_calls;


//		System.out.println("Bochert (psudo-poly) took "+Long.toString(b_calls1)+" found max star to be: "+g.array2string(bochresult1));
		System.out.println("Bochert (poly?) took "+Long.toString(b_calls2)+" found max star to be: "+g.array2string(bochresult2));

		//int[] test1;
		//int[] test2 = {};
		//test1 = g.Bochert_neighbor(4, test2);
		//System.out.println("Neighbors: "+g.array2string(test1));

		//int[] bronresult = g.BronKerbosch(r, p, x);	
		//long start = System.currentTimeMillis();
		//		bochresult = g.pre_Bochert();
		//long elapsedTimeMillis = System.currentTimeMillis()-start;
		//System.out.println("__ it took:"+elapsedTimeMillis/1000+" seconds");
		//System.out.println("BronKerbosch took "+Long.toString(g.BK_calls)+" found max star to be: "+g.array2string(bronresult));	
		//	System.out.println("Bochert took "+Long.toString(g.B_calls)+" found max star to be: "+g.array2string(bochresult));
		//System.out.println("Bron: "+g.BK_calls+" Bochert: "+g.B_calls);

		//int[] a = {2,3,4,6,7,8,10,11,12,14,15,16};
		//System.out.println(g.translate(a));

		/*		int[] a = {1,3,5};
		int[] b = {3,1,5};


		node an = new node(a);
		node bn = new node(b);
		node n = new node();

		System.out.println(an.print_list());
		System.out.println(an.delete(bn));
		System.out.println(an.print_list());*/



		//System.out.println("Head:"+head.get_value()+" next:"+head.get_previous().get_value()+" next:"+head.get_previous().get_previous().get_value()+" next:"+head.get_previous().get_previous().get_previous().get_value()+" next:"+head.get_previous().get_previous().get_previous().get_previous().get_value()+" next:"+head.get_previous().get_previous().get_previous().get_previous().get_previous().get_value());

		//System.out.println("Printing head after creating nodes: "+head.print_list());
		//System.out.println("Printing split after creating nodes: "+split.print_list());

		//System.out.println("r before combine: "+g.array2string(r));
		//r = head.combine(split, test.length+split_int.length);// .split_nodes(split_int);//.get_next().delete_next();	
		//System.out.println("r after combine: "+g.array2string(r));
		//head.delete_next();
		//System.out.println("Printing again after head.delete_next(): "+head.print_list());
		//head.get_next().delete_next();
		//System.out.println("Printing again after head.get_next().delete_next(): "+head.print_list());
		//head.delete_next();
		//System.out.println("Printing again after head.delete_next(): "+head.print_list());





		//	int[] temp = g.BronKerbosch(r,p,x);
		//	int[] temp = g.pre_Bochert();


		//	System.out.println("max clique is: ");
		//	System.out.println(g.array2string(temp));



	}

}
