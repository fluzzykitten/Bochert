package Clique;

import java.io.*;



public class graph {

	private int[][] graph; // the adjacency matrix
	private int nodes; // total number of nodes
	private int BK_iteration_deep = 0; // iterations deep into BronKerbosch
	private long BK_calls = 0; // calls to BronKerbosch
	private int B_iteration_deep = -1; //iterations deep into Bochert
	private long B_calls = 0; // calls to Bochert
	private boolean verboseBK = false; // Verbosity of output... verbosity should be a word... it sounds cool
	//	private int[] B_it_calls = new int[10]; //DELETE???? Huh... I have no idea what this is used for
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
	//	private boolean use_node_edge_count = true; // if the sorting mechanism is looking at edge count, or actual array values
	//	private boolean use_ind_sets = false; // so that bochert can first find the ind sets without getting confused and trying to use them in bochert
	private node[] memory = new node[1];
	private boolean optimize_all_set_memory = false;
	private boolean optimize_ind_sets = true;
	

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

	private void double_memory(){
		node[] new_memory = new node[memory.length*2];
		System.arraycopy(memory, 0, new_memory, 0, memory.length);
		memory = new_memory;

	}

	private void add_memory(node new_element,int level){
		//Now, it's important to note that this function being run, presupposes that
		//the find_memory() function has already been run and found that not only 
		//does this set need to be looked at, but it already has removed all sets 
		//that are contained in this set
		if (memory.length <= level)
			double_memory();

		node temp = memory[level];
		node copy_new_element = new_element.copy();

		if(temp == null){
			memory[level] = copy_new_element;
			return;
		}
		else if(temp.get_length() <= copy_new_element.get_length()){
			copy_new_element.set_memory_next(temp);
			memory[level] = copy_new_element;
			return;
		}

		while ((temp.get_memory_next()!=null)&&(temp.get_memory_next().get_length() > copy_new_element.get_length())){
			temp = temp.get_memory_next(); 
		}
		copy_new_element.set_memory_next(temp.get_memory_next());
		temp.set_memory_next(copy_new_element);

	}

	private boolean find_memory(node element, int level){
		if (memory.length <= level)
			double_memory();

		node temp = memory[level];
		int[] found = {};

		if (temp == null){
//			System.out.println("starting position is blank");
			return false;
		}
		else{ 
			if (temp.get_length() >= element.get_length()){
//				System.out.println("temp.l >= elm.l");
				if (temp.contains(element)){
//					System.out.println("temp contains elm");
					return true;//temp.get_max_star();
				}
			}
			else{
//				System.out.println("elm.l > temp.l");
				while ((temp != null) && (element.contains(temp))){
//					System.out.println("temp != null && elm contains temp");
					memory[level] = memory[level].get_memory_next();
					temp = memory[level];
				}
				if (memory[level] == null){
//					System.out.println("mem[lev] now equals null");
					return false;
				}
			}
		}

		//at this point, the first element of the list of memory has been passed over
		while (temp.get_memory_next() != null){
//			System.out.println("first elem of list has been passed over");

			if (temp.get_memory_next().get_length() >= element.get_length()){
//				System.out.println("temp.n.l >= elm.l");
				if (temp.get_memory_next().contains(element)){
//					System.out.println("temp.n contains elm");
					return true;//temp.get_max_star();
				}
			}
			else{
//				System.out.println("elm.l > temp.n.l");
				while ((temp.get_memory_next() != null) && (element.contains(temp.get_memory_next()))){
//					System.out.println("temp.n != null && elm contains temp.n");
					temp.set_memory_next(temp.get_memory_next().get_memory_next());
				}
			}
			if (temp.get_memory_next() != null){
//				System.out.println("incrimenting temp");
				temp = temp.get_memory_next(); 
			}
		}

		return false;
	}

	private void add_imemory(node new_element,node imemory, int new_head){
		//Now, it's important to note that this function being run, presupposes that
		//the find_memory() function has already been run and found that not only 
		//does this set need to be looked at, but it already has removed all sets 
		//that are contained in this set
		
		node temp = imemory.memory_next;
		node copy_new_element = new_element.copy();
		copy_new_element.set_head(new_head);
		
		
		if(temp == null){
			imemory.set_memory_next(copy_new_element);
			return;
		}
		else if(temp.get_length() <= copy_new_element.get_length()){
			copy_new_element.set_memory_next(temp);
			imemory.set_memory_next(copy_new_element);
			return;
		}

		while ((temp.get_memory_next()!=null)&&(temp.get_memory_next().get_length() > copy_new_element.get_length())){
			temp = temp.get_memory_next(); 
		}
		copy_new_element.set_memory_next(temp.get_memory_next());
		temp.set_memory_next(copy_new_element);
		
		
	}
	
	private boolean find_imemory(node element, node imemory){

		node temp = imemory.get_memory_next();
		int[] found = {};

		
		if (temp.get_length() == 0){
			if  (element.get_length() == 0){
				return true;
			}
			else{
				imemory.set_memory_next(null);
				temp = imemory.get_memory_next();
			}
		}
		
		if (temp == null){
//			System.out.println("starting position is blank");
			return false;
		}
		else{ 				
			if (temp.get_length() >= element.get_length()){
//				System.out.println("temp.l >= elm.l");
				if (temp.contains(element)){
//					System.out.println("temp contains elm");
					return true;//temp.get_max_star();
				}
			}
			else{
//				System.out.println("elm.l > temp.l");
				while ((temp != null) && (element.contains(temp))){
//					System.out.println("temp != null && elm contains temp");
					imemory = imemory.get_memory_next();
					temp = imemory;
				}
				if (imemory == null){
//					System.out.println("mem[lev] now equals null");
					return false;
				}
			}
		}

		//at this point, the first element of the list of memory has been passed over
		while (temp.get_memory_next() != null){
//			System.out.println("first elem of list has been passed over");

			if (temp.get_memory_next().get_length() >= element.get_length()){
//				System.out.println("temp.n.l >= elm.l");
				if (temp.get_memory_next().contains(element)){
//					System.out.println("temp.n contains elm");
					return true;//temp.get_max_star();
				}
			}
			else{
//				System.out.println("elm.l > temp.n.l");
				while ((temp.get_memory_next() != null) && (element.contains(temp.get_memory_next()))){
//					System.out.println("temp.n != null && elm contains temp.n");
					temp.set_memory_next(temp.get_memory_next().get_memory_next());
				}
			}
			if (temp.get_memory_next() != null){
//				System.out.println("incrimenting temp");
				temp = temp.get_memory_next(); 
			}
		}

		return false;
	}

	
	public int[] pre_Bochert(){


		int[] all_nodes = all_neighbors(-1);

		return Bochert(all_nodes, 0);
	}

	
	public int[] pre_iBochert(){

		make_independent_sets();

/*		for (int i = 0; i<node_ind_set.length; i++)
			System.out.println("node: "+i+" is in set: "+node_ind_set[i]);

		System.out.println("Node independent sets are:");
		for (int i = 0; i<independent_sets.length ; i++)
			System.out.println("Set: "+i+" is: "+array2string(independent_sets[i].print_array()));
*/
		
		

		int[] all_nodes = all_neighbors(-1);

		return iBochert(all_nodes, 0);
	}

	public int[] oldversion_pre_Bochert(){
		int[] max_clique = null;

		//		B_calls = 0;


		node_index_increasing = new int[nodes];
		for(int i = 0; i<nodes; i++)
			node_index_increasing[i] = i;

		node_index_increasing = merge_sort(node_index_increasing);
		nodes_with_over_half_edges = find_nodes_with_over_half();
		for(int i = 0; i<nodes_with_over_half_edges.length; i++)
			nodes_with_over_half_edges[i]++;


		boolean use_node_edge_count = false; //was global
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
		boolean use_ind_sets = true; //was global

		//		B_calls = 0;

		max_clique = Bochert2(all_neighbors(-1), 0);
		System.out.println("Exiting Bochert with max_clique:"+array2string(max_clique));
		//max_clique = Bochert_Ind_Control(all_neighbors(-1), null, 0);

		return max_clique;
	}

	
	private int[] merge_sort(int[] list){
		return null;
	}

	private int[] find_nodes_with_over_half(){
		return null;
	}
	
	private void make_independent_sets(){

		internal_connected = 0;
		internal_not_connected = 1;

		node[] ind_sets = new node[nodes];
		ind_sets[0] = new node();
		int index_ind_sets = 1;
		node_ind_set = new int[nodes];

		int[] all_nodes = all_neighbors(-1);//nodes_with_over_half_edges;//all_neighbors(-1);
		int[] found_ind_set;
		node nodes_to_consider = new node(all_nodes);

		while (nodes_to_consider.get_length() != 0){
			
			found_ind_set = Bochert(nodes_to_consider.print_array(), 0);
			ind_sets[index_ind_sets] = new node(found_ind_set);
			
			for (int i = 0; i < found_ind_set.length; i++)
				node_ind_set[found_ind_set[i]-1] = index_ind_sets;


			index_ind_sets++;
			nodes_to_consider.split_nodes(found_ind_set);
		}



		independent_sets = new node[index_ind_sets];
		System.arraycopy(ind_sets, 0, independent_sets, 0, index_ind_sets);


		internal_connected = 1;
		internal_not_connected = 0;
		B_calls = 0;
	}	

	private int[] Bochert2(int[] nodes, int current_max){
		return null;
	}
	
	private int[] iBochert(int[] nodes, int current_max){
		//original Bochert


		int[] temp_connected_nodes = null;
		int[] max_star = null;
		int[] temp_max = null;
		int temp_current_max = current_max;
		int current_node;
		int current_ind_set_num;
		int[] current_ind_set;
		int node_that_found_max_star = -1;


		B_calls++;
		B_iteration_deep++;

		if (B_iteration_deep < 3){
			System.out.println("B_iteration_deep: "+B_iteration_deep+" B_calls: "+B_calls+" and length nodes left: "+nodes.length);
		}

		node nodes_to_consider = new node(nodes); 
		node head_max_star = new node();
		node temp = null;
		node imemory = new node();
		
		//		int length_nodes_left = nodes.length; 

//		System.out.println("Level: "+B_iteration_deep+" Looking for: "+nodes_to_consider.print_list());

//		if (memory.length > B_iteration_deep){
//			node delete = memory[B_iteration_deep];
//			System.out.println("Contents of memory:");
//			while (delete != null){
//				System.out.println(delete.print_list());
//				delete = delete.get_memory_next();
//			}
//		}
if (optimize_all_set_memory == true){
		if (this.find_memory(nodes_to_consider, B_iteration_deep)){
//			System.out.println("FOUND");
			B_iteration_deep--;
//			pause();
			return null; //this set has already been considered
		}
//		System.out.println("ADDING");
		this.add_memory(nodes_to_consider, B_iteration_deep);
//		pause();
}

		while (nodes_to_consider.get_length() != 0)
		{


			if (nodes_to_consider.get_length() <= current_max){
				B_iteration_deep--;
				return null; //that is to say there aren't enough nodes left to make a star big enough to beat the current max
			}


			current_node = nodes_to_consider.get_next().get_value();
			temp_connected_nodes = Bochert_neighbor(current_node, nodes_to_consider.combine(head_max_star));

			
//			System.out.println("Problem: when memory gets optimized out, node should be deleted from ")
			if (temp_connected_nodes.length >= temp_current_max) {
				
//				insert_spaces_for_iteration("B");
//				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//				System.out.println("entering optimization with current node: "+current_node+" nodes_to_consider: "+nodes_to_consider.print_list()+" head_max_star: "+head_max_star.print_list()+" node_that_found_max_star: "+node_that_found_max_star);
				
				current_ind_set_num = node_ind_set[current_node-1];
				current_ind_set = independent_sets[current_ind_set_num].print_array();

//				System.out.println("independent set includes: "+array2string(current_ind_set));
				
				temp = new node(temp_connected_nodes);
				add_imemory(temp, imemory, current_node);
				
				for(int i = 0; i < current_ind_set.length; i++){
					
					if (current_ind_set[i] != current_node){
						if (nodes_to_consider.find(current_ind_set[i])){
							temp = new node(Bochert_neighbor(current_ind_set[i], nodes_to_consider.combine(head_max_star)));						
							if (temp.get_length() >= temp_current_max){								
							
						
						if (!find_imemory(temp, imemory)){
//							System.out.println("node: "+current_ind_set[i]+" was added to the memory");
							add_imemory(temp, imemory, current_ind_set[i]);
						}
						else{
							nodes_to_consider.delete(current_ind_set[i]);
						}
					}
						}
					}
				}
			
				current_node = imemory.get_memory_next().get_head();
				temp_connected_nodes = Bochert_neighbor(current_node, nodes_to_consider.combine(head_max_star));
				imemory.set_memory_next(null);
//				System.out.println("IT WAS DECIDED THAT NODE: "+current_node+" with set: "+array2string(temp_connected_nodes)+" was worth looking into");
			}
			

			
			 //imemory = new node[1]
			

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

					if (temp_current_max == 0){
//						insert_spaces_for_iteration("B");
//						System.out.println("========calling Bochert again on current node: "+current_node);
//						System.out.println("sending to Bochert temp_connected_nodes of: "+array2string(temp_connected_nodes)+" and temp_max of: "+temp_current_max);
						temp_max = iBochert(temp_connected_nodes, temp_current_max);
					}
					else{
//						insert_spaces_for_iteration("B");
//						System.out.println("========calling Bochert again on current node: "+current_node);
//						System.out.println("sending to Bochert temp_connected_nodes of: "+array2string(temp_connected_nodes)+" and temp_max of: "+temp_current_max);
						temp_max = iBochert(temp_connected_nodes, temp_current_max-1);
					}
				}
				if ((temp_max != null) && (temp_max.length >= temp_current_max)){



					nodes_to_consider.add(head_max_star);
					node_that_found_max_star = current_node;
					max_star = temp_max;
					temp_current_max = max_star.length+1;


					head_max_star = nodes_to_consider.split_nodes(max_star);




				}
			}


			//			length_nodes_left--;
			nodes_to_consider.delete_next();


		}	


		if (node_that_found_max_star == -1){
			//			System.out.println("Returning null");
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


		int[] temp_connected_nodes = null;
		int[] max_star = null;
		int[] temp_max = null;
		int temp_current_max = current_max;
		int current_node;
		int node_that_found_max_star = -1;


		B_calls++;
		B_iteration_deep++;

//		if (B_iteration_deep < 4){
//			System.out.println("B_iteration_deep: "+B_iteration_deep+" B_calls: "+B_calls);
//		}

		node nodes_to_consider = new node(nodes); 
		node head_max_star = new node();

		//		int length_nodes_left = nodes.length; 

//		System.out.println("Level: "+B_iteration_deep+" Looking for: "+nodes_to_consider.print_list());

//		if (memory.length > B_iteration_deep){
//			node delete = memory[B_iteration_deep];
//			System.out.println("Contents of memory:");
//			while (delete != null){
//				System.out.println(delete.print_list());
//				delete = delete.get_memory_next();
//			}
//		}
if (optimize_all_set_memory == true){
		if (this.find_memory(nodes_to_consider, B_iteration_deep)){
//			System.out.println("FOUND");
			B_iteration_deep--;
//			pause();
			return null; //this set has already been considered
		}
//		System.out.println("ADDING");
		this.add_memory(nodes_to_consider, B_iteration_deep);
//		pause();
}

		while (nodes_to_consider.get_length() != 0)
		{


			if (nodes_to_consider.get_length() <= current_max){
				B_iteration_deep--;
				return null; //that is to say there aren't enough nodes left to make a star big enough to beat the current max
			}


			current_node = nodes_to_consider.get_next().get_value();
			temp_connected_nodes = Bochert_neighbor(current_node, nodes_to_consider.combine(head_max_star));


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

					if (temp_current_max == 0){
//						System.out.println("sending to Bochert temp_connected_nodes of: "+array2string(temp_connected_nodes)+" and temp_max of: "+temp_current_max);
						temp_max = Bochert(temp_connected_nodes, temp_current_max);
					}
					else{
//						System.out.println("sending to Bochert temp_connected_nodes of: "+array2string(temp_connected_nodes)+" and temp_max of: "+temp_current_max);
						temp_max = Bochert(temp_connected_nodes, temp_current_max-1);
					}
				}
				if ((temp_max != null) && (temp_max.length >= temp_current_max)){



					nodes_to_consider.add(head_max_star);
					node_that_found_max_star = current_node;
					max_star = temp_max;
					temp_current_max = max_star.length+1;


					head_max_star = nodes_to_consider.split_nodes(max_star);




				}
			}


			//			length_nodes_left--;
			nodes_to_consider.delete_next();


		}	


		if (node_that_found_max_star == -1){
			//			System.out.println("Returning null");
			B_iteration_deep--;		
			return null;
		}


		int[] temp_finder = {node_that_found_max_star};
		node finder = new node(temp_finder);

		head_max_star.add(finder);

		B_iteration_deep--;
		return head_max_star.print_array();

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
			
			File myFile = new File("src\\Clique\\graph_binaries\\"+file_name);
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

		
		
		
		
		System.out.println("Hello?");

		
		
		
		
		//graph g = new graph(15,5);
			graph g = new graph("brock200_1.clq");
		//  graph g = new graph("hamming6-2.clq"); // good small testing graph
		//	graph g = new graph("c-fat500-1.clq");
		//	graph g = new graph("keller6.clq");		int[] r = null, x = null, p = g.find_P();

		  
//		  int[] test_star = {2,3,5,8,9,12,14,15,17,20,22,23,26,27,29,32,33,36,38,39,42,43,45,48,50,51,53,56,57,60,62,63};
//		  int[] test_star2 = {1,4,6,7,10,11,13,16,18,19,21,24,25,28,30,31,34,35,37,40,41,44,46,47,49,52,54,55,58,59,61,64};
//		  System.out.println("length star 1: "+test_star.length+" and is "+g.is_star(test_star, true)+" length of star 2: "+test_star2.length+" and is "+g.is_star(test_star2, true));
		  
			long start = System.currentTimeMillis();
			int [] temp = g.pre_iBochert();
			long elapsedTimeMillis = System.currentTimeMillis()-start;
			
			System.out.println("max clique from optimized Bochert is: ");
			System.out.println(g.array2string(temp));
			System.out.println("total calls to Bochert: "+g.B_calls);
			System.out.println("__ it took:"+elapsedTimeMillis/1000+" seconds");
		  
		  
/*			start = System.currentTimeMillis();
			temp = g.pre_Bochert();
			elapsedTimeMillis = System.currentTimeMillis()-start;
			
			System.out.println("max clique from un-optimized Bochert is: ");
			System.out.println(g.array2string(temp));
			System.out.println("total calls to Bochert: "+g.B_calls);
			System.out.println("__ it took:"+elapsedTimeMillis/1000+" seconds");
*/		  
		  
			if (1 == 2){



		//		int[] temp = g.BronKerbosch(r,p,x);
		//		System.out.println("max clique from BronKerbosch is: ");
		//		System.out.println(g.array2string(temp));

/*		g.optimize_all_set_memory = true;
		long start = System.currentTimeMillis();
		int[] temp = g.pre_Bochert();
		long elapsedTimeMillis = System.currentTimeMillis()-start;
		
		System.out.println("max clique from optimized Bochert is: ");
		System.out.println(g.array2string(temp));
		System.out.println("total calls to Bochert: "+g.B_calls);
		System.out.println("__ it took:"+elapsedTimeMillis/1000+" seconds");
*/
		
		g.optimize_ind_sets = true;
		start = System.currentTimeMillis();
		temp = g.pre_Bochert();
		elapsedTimeMillis = System.currentTimeMillis()-start;
		
		System.out.println("max clique from un-optimized Bochert is: ");
		System.out.println(g.array2string(temp));
		System.out.println("total calls to Bochert: "+g.B_calls);
		System.out.println("__ it took:"+elapsedTimeMillis/1000+" seconds");

		/*		int[] a = {1,2,3}, b={2};
		int[] c = {1,2,3,4,7}, d={7};
		node na = new node(a), nb = new node(b);
		node nc = new node(c), nd = new node(d);

		g.add_memory(na, 1);		
		g.add_memory(nb, 1);		
		g.add_memory(nd, 1);		
		node delete = g.memory[1];
		System.out.println("Contents of memory:");
		while (delete != null){
			System.out.println(delete.print_list());
			delete = delete.get_memory_next();
		}
		System.out.println();


		System.out.println("looking for: "+g.array2string(c));
		boolean res = g.find_memory(nc, 1);
		if (res == false)
			System.out.println("not found");
		else 
			System.out.println("found");


		delete = g.memory[1];
		System.out.println("Contents of memory:");
		while (delete != null){
			System.out.println(delete.print_list());
			delete = delete.get_memory_next();
		}
		System.out.println();
		 */
		/*		g.add_memory(nb, 0);
		delete = g.memory[0];
		while (delete != null){
			System.out.println(delete.print_list());
			delete = delete.get_memory_next();
		}
		System.out.println();

		g.add_memory(nc, 0);
		delete = g.memory[0];
		while (delete != null){
			System.out.println(delete.print_list());
			delete = delete.get_memory_next();
		}
		System.out.println();

		g.add_memory(nd, 0);		
		delete = g.memory[0];
		while (delete != null){
			System.out.println(delete.print_list());
			delete = delete.get_memory_next();
		}
		System.out.println();


		int[] tt = {};
		if (tt == null)
			System.out.println("null");
		else if (tt.length == 0)
			System.out.println("length = 0");
		 */
	}
	}

}
