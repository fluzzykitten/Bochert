package Clique;

import java.io.*;

//import Clique2.graph;

//import OldClique.node;



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
	//private int internal_not_connected = 0; // used when functions are looking at internal connected ness
	//private node[] independent_sets; // each index in the array (except for index 0) represents an independent set, and the nodes in that set
	//private int[] node_ind_set; // where each node is an index, value is ind set node is in. 0 denotes no specified ind set
	private int[] node_edge_count; // number of edges each node has connected to it
	private int[] nodes_ordered_increasing; // array of nodes with decreasing edge count - first node has highest num edges
	private int[] index_ordered_nodes; //array of nodes, where int[0] represents the index of the first node into nodes_ordered_decreasing, and int[1] represents the index of the second, etc 
	//private int[] nodes_with_over_half_edges; // edges with nodes that have over half edges needed to be considered for ind sets
	//	private boolean use_node_edge_count = true; // if the sorting mechanism is looking at edge count, or actual array values
	//	private boolean use_ind_sets = false; // so that bochert can first find the ind sets without getting confused and trying to use them in bochert
	//private node[] memory = new node[1];
	//private boolean optimize_all_set_memory = false;
	//private boolean optimize_ind_sets = true;
	private int deepest = 0;
	private int level0nodefinder = -1;
	private int level1nodefinder = -1;
	private int levelneg1nodefinder = -1;
	boolean start_showing_crap = false;
	private int current_level = 1;




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


	private void add_imemory(node new_element,node imemory, int new_head){
		//Now, it's important to note that this function being run, presupposes that
		//the find_memory() function has already been run and found that not only 
		//does this set need to be looked at, but it already has removed all sets 
		//that are contained in this set

		node temp = imemory.memory_next;
		node copy_new_element = new_element.copy();
		copy_new_element.set_head(new_head);
		imemory.incriment_length();


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

	private boolean find_imemory(node imemory, node element, node pergatory, int toptop/*, int[] who_was_bigger*/){
		//true means the memory was found, don't add it
		//false means add the element, it isn't currently in memory

		
		node temp = imemory;
		int[] new_toptop = new int[1];

		if (temp.get_memory_next() == null){
			if (start_showing_crap)
				System.out.println(toptop+" Contains: BLANK");			
			return false;
		}

		if  (element.get_length() == 0){

			if (start_showing_crap)
			System.out.println(toptop+" (being empty) is cOntained in: "+temp.get_memory_next().get_head());			

			new_toptop[0] = toptop;
			pergatory.add(new node(new_toptop));

//			who_was_bigger[new_toptop[0]-1] = temp.get_memory_next().get_head();


			return true;
		}

		if (temp.get_memory_next().get_length() == 0){
			//element can't be of zero length now - because it would've returned already

			//there can be only one in memory of size zero...
			//if this hasn't been checked yet
			//if it has been checked already, then don't delete it, leave it where it is
			if (temp.get_memory_next().get_meta_data() == 0){//0 means not checked

				
				if (start_showing_crap)
				System.out.println(toptop+" coNtains: "+temp.get_memory_next().get_head());			

				
				imemory.decriment_length();
				new_toptop[0] = temp.get_memory_next().get_head();
				pergatory.add(new node(new_toptop));

//				who_was_bigger[new_toptop[0]-1] = toptop;

				imemory.set_memory_next(null);
			}
			return false;

		}		
		else{ 				

			if (temp.get_memory_next().get_length() >= element.get_length()){
				//				System.out.println("temp.l >= elm.l");
				if (temp.get_memory_next().contains(element)){
					//					System.out.println("temp contains elm");


					if (start_showing_crap)
					System.out.println(toptop+" is conTained in: "+temp.get_memory_next().get_head());			
					//					System.out.println("B");
					new_toptop[0] = toptop;
					pergatory.add(new node(new_toptop));

//					who_was_bigger[new_toptop[0]-1] = temp.get_memory_next().get_head();

					return true;//temp.get_max_star();
				}
			}
			else{
				//				System.out.println("elm.l > temp.l");
				while ((temp.get_memory_next() != null) && (element.contains(temp.get_memory_next()))){
					//					System.out.println("temp != null && elm contains temp");
					//					System.out.println("C");

					if (temp.get_memory_next().get_meta_data() == 0){

						if (start_showing_crap)
						System.out.println(toptop+" contAins: "+temp.get_memory_next().get_head());			
						
						imemory.decriment_length();

						new_toptop[0] = temp.get_memory_next().get_head();
						pergatory.add(new node(new_toptop));

//						who_was_bigger[new_toptop[0]-1] = toptop;

						imemory.set_memory_next(imemory.get_memory_next().get_memory_next());
					}
					//temp = imemory.get_memory_next();
				}
				if (imemory.get_memory_next() == null){
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
					//					System.out.println("D");

					if (start_showing_crap)
					System.out.println(toptop+" is contaIned in: "+temp.get_memory_next().get_head());			
					
					new_toptop[0] = toptop;
					pergatory.add(new node(new_toptop));

//					who_was_bigger[new_toptop[0]-1] = temp.get_memory_next().get_head();

					return true;//temp.get_max_star();
				}
			}
			else{
				//				System.out.println("elm.l > temp.n.l");
				while ((temp.get_memory_next() != null) && (element.contains(temp.get_memory_next()))){
					//					System.out.println("temp.n != null && elm contains temp.n");
					if (temp.get_memory_next().get_meta_data() == 0){
						
						if (start_showing_crap)
						System.out.println(toptop+" contaiNs: "+temp.get_memory_next().get_head());			
												
						imemory.decriment_length();
						new_toptop[0] = temp.get_memory_next().get_head();
						pergatory.add(new node(new_toptop));

//						who_was_bigger[new_toptop[0]-1] = toptop;


						temp.set_memory_next(temp.get_memory_next().get_memory_next());
					}
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

//		sort_nodes();
//		reorganize_nodes();
//		return unreorganize_nodes(Bochert(all_neighbors(-1), 0));

		
		return Bochert(all_neighbors(-1), 0);
	}


	public int[] pre_Old_Bochert(){


		int[] all_nodes = all_neighbors(-1);

		return Old_Bochert(all_nodes, 0);
	}

	private void sort_nodes(){

		nodes_ordered_increasing = merge_sort(this.all_neighbors(-1));

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
		
	}
	
	private int[] unreorganize_nodes(int[] result){
		//find out what the real nodes are
		
		int[] real_result = new int[result.length];
		
		for(int i = 0; i<result.length; i++)
			real_result[i] = nodes_ordered_increasing[result[i]-1];

		return real_result;
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


		while ((lefti < leftl) && (righti < rightl)){
			if (node_edge_count[left[lefti]-1] >= node_edge_count[right[righti]-1]){ 
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



	private int[] Bochert(int[] nodes, int current_max){
		//not original Bochert


//		System.out.println("SH*T SH*T!! Fire the MISILES!!");

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

		node imemory = new node();

		//		int length_nodes_left = nodes.length; 


		boolean found_new_set = true;
		node pergatory = new node();
		node dantes_inferno = new node();
		int[] index_dante = null;
		node pergatory_and_max_star = new node();
		node memory_element = new node();
		int[] delete_this = new int[1];
		node index_imemory = imemory;
		node deleted_nodes = new node();
		int[] cesspool = null;
//		int[] where_is_that_darn_node = new int[this.nodes];//BAD BAD BAD!!! YOU SHOULD BE ASHAMED OF YOURSELF... don't forget to go back and use the enumerated values instead of indexes because this wastes so much space
		//0 means it's nowhere
		//-1 means it's in purgatory
		//1 means it's in imemory
//		int[] who_was_bigger = new int[this.nodes];//BAD BAD BAD!!! YOU SHOULD BE ASHAMED OF YOURSELF... don't forget to go back and use the enumerated values instead of indexes because this wastes so much space
		
//		if (B_calls > 17)
//			start_showing_crap = true;
		
		do{

			//////////////////////////////////////////////////
			/**************** check function ****************/
			//////////////////////////////////////////////////

			if (found_new_set == true){
				
				dantes_inferno = nodes_to_consider;
				pergatory = new node(); //this is a waste... oh well... I'm trying this out quick, not really worth the effort
				// so now all the nodes are in temp, and pergatory and nodes to consider are empty
				index_dante = dantes_inferno.print_array();
				index_imemory = imemory;

				//this function cycles through imemory and pulls out all the nodes that haven't be checked yet to decide if they should still be in imemory
				while (index_imemory.get_memory_next() != null){
					if (index_imemory.get_memory_next().get_meta_data() == 0){//if it hasn't been checked yet
//						where_is_that_darn_node[index_imemory.get_memory_next().get_head()-1] = 0;
						index_imemory.set_memory_next(index_imemory.get_memory_next().get_memory_next());
						imemory.decriment_length();
					}
					else{
						index_imemory = index_imemory.get_memory_next();
					}
				}


				cesspool = head_max_star.combine(dantes_inferno);

				for (int i = 0; i < index_dante.length; i++){

//					System.out.print("index_dante[i]: "+index_dante[i]);
//					System.out.print(" who_was_bigger[~-1]: "+who_was_bigger[index_dante[i]-1]);
//					if (who_was_bigger[index_dante[i]-1]!=0)
//						System.out.println(" where_is_that_darn_node[~-1]: "+where_is_that_darn_node[who_was_bigger[index_dante[i]-1]-1]);
//					else
//						System.out.println();
					
//					if (false || ((who_was_bigger[index_dante[i]-1] == 0) || (where_is_that_darn_node[who_was_bigger[index_dante[i]-1]-1] < 1))){
						//ie. the node that was found to bigger than this node last time we went through this, isn't currently in imemory so it's worth looking into

						memory_element = new node(Bochert_neighbor(index_dante[i], head_max_star.combine(dantes_inferno)));
//						memory_element = new node(Bochert_neighbor(index_dante[i], head_max_star.combine(dantes_inferno)),index_dante[i]);

						if (!this.find_imemory(imemory, memory_element, pergatory, index_dante[i]/*,who_was_bigger*/)){
							this.add_imemory(memory_element, imemory, index_dante[i]);
//							where_is_that_darn_node[index_dante[i]-1] = 1;
							if(start_showing_crap)
							System.out.println("added to imemory: "+index_dante[i]+" connected to: "+memory_element.print_list());
						}
						else {
//							where_is_that_darn_node[index_dante[i]-1] = -1;
						}

/*					}
					else {
						if(start_showing_crap)
							System.out.println(index_dante[i]+" was contained in "+who_was_bigger[index_dante[i]-1]+" (which is currently in: "+where_is_that_darn_node[who_was_bigger[index_dante[i]-1]-1]+")");

						delete_this[0] = index_dante[i];
						pergatory.add(new node(delete_this));

						//the node that was bigger last time is still in memory, so don't bother looking again, because you'll find it again
						where_is_that_darn_node[index_dante[i]-1] = -1;
					}
*/					
					dantes_inferno.delete(index_dante[i]);
				}

				pergatory_and_max_star = pergatory.copy();
				pergatory_and_max_star.add(head_max_star.copy());
				nodes_to_consider = imemory.print_memory_to_consider();
				found_new_set = false;
				index_imemory = imemory;

				while((index_imemory.get_memory_next() != null)&&(index_imemory.get_memory_next().get_meta_data() == 1)){
					index_imemory = index_imemory.get_memory_next();
				}

				if (start_showing_crap){
					System.out.println("******************************************************************");
					this.insert_spaces_for_iteration("B");
					System.out.println();
					System.out.println("nodes was originally: "+array2string(nodes));
					System.out.println("nodes to consider is: "+nodes_to_consider.print_list());
					System.out.println("pergatory is:         "+pergatory.print_list());
					System.out.println("maxstar is:           "+head_max_star.print_list());
					System.out.println("pergatory+maxstar is: "+pergatory_and_max_star.print_list());
					System.out.println("node_that_found_max : "+node_that_found_max_star);
					System.out.println("a ref list of nodes : "+array2string(this.all_neighbors(-1)));
//					System.out.println("where_is_that_darn_n: "+array2string(where_is_that_darn_node));					
//					System.out.println("who_was_bigger:       "+array2string(who_was_bigger));
					System.out.println("******************************************************************");
					
				}

				
				
			}

			///////////////////////////////////////////////////////////
			//               	END CHECK FUNCTION                   //
			///////////////////////////////////////////////////////////


			if (((nodes_to_consider.get_length()+pergatory.get_length()) <= current_max)||(index_imemory.get_memory_next() == null)){
				//this.insert_spaces_for_iteration("B");
				//System.out.println("returning null");
				//B_iteration_deep--;
				//return null; //that is to say there aren't enough nodes left to make a star big enough to beat the current max

				if (node_that_found_max_star == -1){
					//			this.insert_spaces_for_iteration("B");
					//			System.out.println("Returning null");
					B_iteration_deep--;		
					return null;
				}


				int[] temp_finder = {node_that_found_max_star};
				node finder = new node(temp_finder);

				head_max_star.add(finder);

				B_iteration_deep--;

				//				this.insert_spaces_for_iteration("B");
				//				System.out.println("Returning: "+head_max_star.print_list());

				return head_max_star.print_array();

			}


			current_node = index_imemory.get_memory_next().get_value();
			temp_connected_nodes = Bochert_neighbor(current_node, nodes_to_consider.combine(pergatory_and_max_star));
			//			insert_spaces_for_iteration("B");
			//			System.out.println("nodes_to_consider: "+array2string(nodes_to_consider.print_array())+" perg&maxS: "+array2string(pergatory_and_max_star.print_array()));
			//			insert_spaces_for_iteration("B");
			//			System.out.println("the neighbors of "+current_node+" were found to be: "+array2string(temp_connected_nodes));

			if(current_level > B_iteration_deep){
				System.out.println();
				System.out.print("*"+B_iteration_deep+"* ");
			}
			
			if(B_iteration_deep < 10){//this.nodes/100){
				if(current_level < B_iteration_deep){
					System.out.println();
					System.out.print("*"+B_iteration_deep+"* ");					
				}				
				current_level = B_iteration_deep;
				
//				imemory.print_memory();
			}
			
			if(B_iteration_deep < 18){//this.nodes/100){
//				this.insert_spaces_for_iteration("B");
				System.out.print(current_node+" ");			
			}
//				if(B_calls > 200)
//					pause();


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
						//	insert_spaces_for_iteration("B");
						//	System.out.println("sending to Bochert temp_connected_nodes of: "+array2string(temp_connected_nodes)+" and temp_max of: "+temp_current_max);
						temp_max = Bochert(temp_connected_nodes, temp_current_max);
					}
					else{
						//	insert_spaces_for_iteration("B");
						//	System.out.println("sending to Bochert temp_connected_nodes of: "+array2string(temp_connected_nodes)+" and temp_max of: "+temp_current_max);
						temp_max = Bochert(temp_connected_nodes, temp_current_max-1);
					}
				}
				if ((temp_max != null) && (temp_max.length >= temp_current_max)){

					
					nodes_to_consider.add(pergatory);
					nodes_to_consider.add(head_max_star);
					node_that_found_max_star = current_node;
					max_star = temp_max;
					temp_current_max = max_star.length+1;

					if(start_showing_crap)
					System.out.println("JUST FOUND A NEW MAX STAR: "+node_that_found_max_star+" is attached to: "+array2string(max_star));
					head_max_star = nodes_to_consider.split_nodes(max_star);

					found_new_set = true;




				}
			}


			//			length_nodes_left--;
			delete_this[0] = index_imemory.get_memory_next().get_value();

			deleted_nodes.add(nodes_to_consider.split_nodes(delete_this));// .delete(index_imemory.get_memory_next().get_value());
			
			//so update imemory with what nodes the checked node was actually connected to, no need to leave that ghost of a connection set
			memory_element = new node(temp_connected_nodes);//new_element.copy();
			memory_element.set_head(current_node);
			memory_element.set_meta_data(1);
			memory_element.set_memory_next(index_imemory.get_memory_next().get_memory_next());		
			index_imemory.set_memory_next(memory_element);
			
//			index_imemory.get_memory_next().set_meta_data(1)


			while((index_imemory.get_memory_next() != null)&&(index_imemory.get_memory_next().get_meta_data() == 1)){
				index_imemory = index_imemory.get_memory_next();
			}

			imemory.decriment_length();

			/*			this.insert_spaces_for_iteration("B");
			System.out.println("**EndLoop: NtoC: "+array2string(nodes_to_consider.print_array())+" and pergatory: "+array2string(pergatory.print_array())+" and max star: "+array2string(head_max_star.print_array())+" and deleted nodes: "+deleted_nodes.print_list());

			temp = new node();
			if (nodes_to_consider.get_length() != 0)
				temp = new node(temp.combine(nodes_to_consider));
			if (deleted_nodes.get_length() != 0)
				temp = new node(temp.combine(deleted_nodes));
			if (head_max_star.get_length() != 0)
				temp = new node(temp.combine(head_max_star));
			if (pergatory.get_length() != 0)
				temp = new node(temp.combine(pergatory));
			if (node_that_found_max_star != -1){				
				delete_this[0] = node_that_found_max_star;
				dantes_inferno = new node(delete_this);
				temp = new node(temp.combine(dantes_inferno));
			}

			this.insert_spaces_for_iteration("B");
			System.out.println("!!!!!!!!!!!all nodes: "+array2string(nodes));
			this.insert_spaces_for_iteration("B");
			System.out.println("!SHOULD BE!all nodes: "+temp.print_list());
			 */		

		}while (index_imemory.get_memory_next() != null);



		if (node_that_found_max_star == -1){
			//	this.insert_spaces_for_iteration("B");
			//	System.out.println("Returning null");
			B_iteration_deep--;		
			return null;
		}


		int[] temp_finder = {node_that_found_max_star};
		node finder = new node(temp_finder);

		head_max_star.add(finder);

		B_iteration_deep--;

		//		this.insert_spaces_for_iteration("B");
		//		System.out.println("Returning: "+head_max_star.print_list());
		
		if (B_iteration_deep == -1)
			levelneg1nodefinder = node_that_found_max_star;
		else if (B_iteration_deep == 0)
			level0nodefinder = node_that_found_max_star;
		else if (B_iteration_deep == 1)
			level1nodefinder = node_that_found_max_star;
		
		
		return head_max_star.print_array();

	}



	private int[] Old_Bochert(int[] nodes, int current_max){
		//Original Bochert (hopefully)


		//System.out.println("SH*T SH*T!! Fire the MISILES!!");


		int[] temp_connected_nodes = null;
		int[] max_star = null;
		int[] temp_max = null;
		int temp_current_max = current_max;
		int current_node;
		int node_that_found_max_star = -1;


		B_calls++;
		B_iteration_deep++;

		if(B_iteration_deep > deepest){
			deepest = B_iteration_deep;
			System.out.println("Deepest is now: "+deepest);			
		}


		node nodes_to_consider = new node(nodes); 
		node head_max_star = new node();





		while (nodes_to_consider.get_next() != null){



			if (nodes_to_consider.get_length() <= (current_max)){
				B_iteration_deep--;		
				return null;
			}

			current_node = nodes_to_consider.get_next().get_value();
			temp_connected_nodes = Bochert_neighbor(current_node, nodes_to_consider.combine(head_max_star));


			if(B_iteration_deep <= 1){
				this.insert_spaces_for_iteration("B");
				System.out.println(current_node);			
			}



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
						temp_max = Old_Bochert(temp_connected_nodes, temp_current_max);
					}
					else{
						temp_max = Old_Bochert(temp_connected_nodes, temp_current_max-1);
					}
				}
				if ((temp_max != null) && (temp_max.length >= temp_current_max)){


					nodes_to_consider.add(head_max_star);
					node_that_found_max_star = current_node;
					max_star = temp_max;
					temp_current_max = max_star.length+1;

					//					System.out.println("JUST FOUND A NEW MAX STAR: "+array2string(max_star)+" NtoC")
					head_max_star = nodes_to_consider.split_nodes(max_star);




				}
			}


			//			length_nodes_left--;
			nodes_to_consider.delete_next();

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




	private int[] Old_Bochert_wrong(int[] nodes, int current_max){
		//original Bochert




		int[] temp_connected_nodes = null;
		int[] max_star = null;
		int[] temp_max = null;
		int temp_current_max = current_max;
		int current_node;
		int node_that_found_max_star = -1;


		B_calls++;
		B_iteration_deep++;


		node nodes_to_consider = new node(nodes); 
		node head_max_star = new node();


		while (nodes_to_consider.get_length() > 0)
		{




			if (nodes_to_consider.get_length() <= current_max){
				B_iteration_deep--;
				return null; //that is to say there aren't enough nodes left to make a star big enough to beat the current max
			}


			current_node = nodes_to_consider.get_next().get_value();
			temp_connected_nodes = Bochert_neighbor(current_node, nodes_to_consider.combine(head_max_star));

			if(B_iteration_deep >= 3){
				this.insert_spaces_for_iteration("B");
				System.out.println(current_node);			
			}


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
						//							System.out.println("sending to Bochert temp_connected_nodes of: "+array2string(temp_connected_nodes)+" and temp_max of: "+temp_current_max);
						temp_max = Old_Bochert(temp_connected_nodes, temp_current_max);
					}
					else{
						//							System.out.println("sending to Bochert temp_connected_nodes of: "+array2string(temp_connected_nodes)+" and temp_max of: "+temp_current_max);
						temp_max = Old_Bochert(temp_connected_nodes, temp_current_max-1);
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

		if(nodes == null)
			return false;

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
		int[][] testie2={	{0,0,1,1,1,1,0},
				{0,0,1,1,1,1,1},
				{1,1,0,1,1,1,1},
				{1,1,1,0,1,1,1},
				{1,1,1,1,0,1,1},
				{1,1,1,1,1,0,1},
				{0,1,1,1,1,1,0}};
		//graph g = new graph(testie2);




		//graph g = new graph(15,5);
		//graph g = new graph("brock200_1.clq"); //causes an error
		//  graph g = new graph("hamming6-2.clq"); // good small testing graph
		//	graph g = new graph("c-fat500-1.clq");
		//	graph g = new graph("c-fat500-5.clq.b"); //dne
		//graph g = new graph("c-fat200-5.clq.b"); //dne
		//	graph g = new graph("keller6.clq");	
		graph g;

		

		
		String s[] = new String[34];
		
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
s[20] = "MANN_a27.clq";
s[21] = "MANN_a45.clq";
s[22] = "MANN_a81.clq";
s[23] = "p_hat300-1.clq";
s[24] = "p_hat300-2.clq";
s[25] = "p_hat300-3.clq";
s[26] = "p_hat500-1.clq";
s[27] = "p_hat500-2.clq";
s[28] = "p_hat700-1.clq";
s[29] = "p_hat1000-1.clq";
s[30] = "p_hat1500-1.clq";
s[31] = "san400_0.5_1.clq";
s[32] = "sanr200_0.7.clq";
s[33] = "sanr400_0.5.clq";
			

for(int i = 0; i<s.length; i++){
	
	if ((i == 20) && (i != 18) && (i != 19) && (i != 21) && (i != 22)){
	
		System.out.println("***********************************************************************************************************");
		System.out.println(i+" "+s[i]);
		g = new graph(s[i]);
	
		
		System.out.println("Number of nodes: "+g.nodes);



		long start = System.currentTimeMillis();
		g.B_calls = 0;
		int [] temp = g.pre_Bochert();
		long elapsedTimeMillis = System.currentTimeMillis()-start;

		System.out.println();
		System.out.println();
		System.out.println("NOW FOR THE OPTIMIZED VERSION");
		System.out.println("max clique from optimized Bochert is: ");
		System.out.println(g.array2string(temp));
		System.out.println("total calls to Bochert: "+g.B_calls);
		System.out.println("__ it took:"+elapsedTimeMillis+" miliseconds");

		System.out.println("is star?: "+g.is_star(temp, true)+" and length is: "+temp.length);

		System.out.println();

//		System.out.println("levelneg1nodefinder: "+g.levelneg1nodefinder);
//		System.out.println("level0nodefinder: "+g.level0nodefinder);
//		System.out.println("level1nodefinder: "+g.level1nodefinder);

}	
}
/*		System.out.println();
		System.out.println();
		System.out.println("AND NOW THE OLDER VERSION");

		start = System.currentTimeMillis();
		g.B_calls = 0;
		//temp = g.pre_Old_Bochert();
		elapsedTimeMillis = System.currentTimeMillis()-start;

		System.out.println("max clique from un-optimized Bochert is: ");
		System.out.println(g.array2string(temp));
		System.out.println("total calls to Bochert: "+g.B_calls);
		System.out.println("__ it took:"+elapsedTimeMillis/1000+" seconds");

		System.out.println("is star?: "+g.is_star(temp, true)+" and length is: "+temp.length);

*/

		/*			System.out.println();
			System.out.println();
			System.out.println("AND NOW THE BRONKERBOSCH");

			int[] r = null, x = null, p = g.find_P();
			start = System.currentTimeMillis();
			g.BK_calls = 0;
			temp = g.BronKerbosch(r, p, x);
			elapsedTimeMillis = System.currentTimeMillis()-start;

			System.out.println("max clique from un-optimized Bochert is: ");
			System.out.println(g.array2string(temp));
			System.out.println("total calls to BronKerbosch: "+g.BK_calls);
			System.out.println("__ it took:"+elapsedTimeMillis/1000+" seconds");

			System.out.println("is star?: "+g.is_star(temp, true)+" and length is: "+temp.length);

		 */			



	}

}