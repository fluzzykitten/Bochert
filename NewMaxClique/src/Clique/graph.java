package Clique;

import java.io.*;

//import Clique2.graph;

//import OldClique.node;


public class graph {

	private int[][] graph; // the adjacency matrix
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
	private node2 empty_node = new node2(new int[0]);




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




	private boolean is_there_another(final node2 nodes_a, final node2 nodes_b, int node, node2 result){

		//int[] check_set = this.Bochert_neighbor(node, all_nodes, internal_connected);
		Bochert_neighbor(result,node,nodes_a, nodes_b, internal_connected);

//		System.out.println("Is there another, result A: "+result.print_list());
		
		if(result.get_length() == 0)
			return false;

		node2 check_set = result.copy_double_mem();
		Bochert_neighbor(result,check_set.get_last(),nodes_a, nodes_b, internal_connected);

//		System.out.println("Is there another, result B: "+result.print_list());

		
		if(result.get_length() <= 1)
			return false;


		for(int i = check_set.get_length()-2; i>=0; i--){
			Bochert_neighbor(result,check_set.get_full_array()[i],empty_node, result, internal_connected);
//			System.out.println("Is there another, result C: "+result.print_list());
			if(result.get_length() <= 1){
				return false;
			}

		}

		return true;
	}



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



	public int[] pre_New_Bochert(){


		int[] all_nodes = all_neighbors(-1);

		sort_nodes();
		reorganize_nodes();

		//		System.out.println("displaying ordered list of nodes:");
		//		for(int i = 0; i<nodes_ordered_increasing.length; i++){
		//			System.out.print(nodes_ordered_increasing[nodes_ordered_increasing.length-i-1]+" ");
		//		}


		return unreorganize_nodes(New_Bochert(new node2(all_nodes), 0, new node2()));
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

		old_graph = graph;
		graph = newgraph;

	}

	private int[] unreorganize_nodes(node2 result){
		//find out what the real nodes are

		int[] real_result = new int[result.get_length()];

		for(int i = 0; i<result.get_length(); i++)
			real_result[i] = nodes_ordered_increasing[result.get_full_array()[i]-1];

		graph = old_graph;

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


	private node2 New_Bochert(node2 nodes, int current_max, node2 current_max_star){

		//not original Bochert


		if ((nodes == null)||(nodes.get_length() == 0))
			return current_max_star;




		//MEMORIES memories
		node2 TOP_checked_nodes = new node2();
		node2 TOP_memory_element_P = new node2();
		node2 TOP_dont_consider_connected = new node2();
		node2 TOP_nodes_to_consider = new node2();


		//turned into indicies
		TOP_checked_nodes.set_memory_next(new node2());
		TOP_memory_element_P.set_memory_next(new node2(nodes.get_length()+current_max_star.get_length()));
		TOP_dont_consider_connected.set_memory_next(current_max_star);
		TOP_nodes_to_consider.set_memory_next(nodes);


		node2 checked_nodes = TOP_checked_nodes;
		node2 memory_element = TOP_memory_element_P;
		node2 dont_consider_connected = TOP_dont_consider_connected;
		node2 nodes_to_consider = TOP_nodes_to_consider;

		checked_nodes.get_memory_next().set_memory_previous(TOP_checked_nodes);
		memory_element.get_memory_next().set_memory_previous(TOP_memory_element_P);
		dont_consider_connected.get_memory_next().set_memory_previous(TOP_dont_consider_connected);
		nodes_to_consider.get_memory_next().set_memory_previous(TOP_nodes_to_consider);

		//		int temp_current_max = current_max;
		//int current_node;
		//int node_that_found_max_star = -1;


		B_calls++;
		B_iteration_deep++;





		//no memory needed
		node2 temp_element_P = new node2(nodes.get_length()+current_max_star.get_length()); //will be protected
		node2 temp_connected_nodes = null; //will be passed down the chain and thus might be corrupted
		node2 temp_connected_star= new node2(nodes.get_length()+current_max_star.get_length()); //will be passed down the chain but won't be corrupted
		node2 temp_max = new node2(nodes.get_length());
		node2 head_max_star = current_max_star;

		boolean another = false;
		boolean checked = false;



		while (TOP_nodes_to_consider.get_memory_next().get_length() > 0){

			
			if(temp_max.get_length() < B_iteration_deep)
				System.out.println(temp_max.get_full_array()[-1]);

			

			Bochert_neighbor(memory_element.get_memory_next(), nodes_to_consider.get_memory_next().get_last(), dont_consider_connected.get_memory_next(), nodes_to_consider.get_memory_next(),internal_connected);

			another = is_there_another(nodes_to_consider.get_memory_next(),dont_consider_connected.get_memory_next(), nodes_to_consider.get_memory_next().get_last(),temp_element_P);

			if(!another)
				checked = this.already_checked_with_out_memory(checked_nodes.get_memory_next(), memory_element.get_memory_next(),temp_element_P);

			this.insert_spaces_for_iteration("B");
			System.out.println("------------------");
			this.insert_spaces_for_iteration("B");
			System.out.println("current node: "+nodes_to_consider.get_memory_next().get_last());
			this.insert_spaces_for_iteration("B");
			System.out.println("ntc("+nodes_to_consider.get_memory_next().get_length()+"): "+nodes_to_consider.get_memory_next().print_list());
			this.insert_spaces_for_iteration("B");
			System.out.println("dnc("+dont_consider_connected.get_memory_next().get_length()+"): "+dont_consider_connected.get_memory_next().print_list());
			this.insert_spaces_for_iteration("B");
			System.out.println("memory_element("+memory_element.get_memory_next().get_length()+"): "+memory_element.get_memory_next().print_list());
			this.insert_spaces_for_iteration("B");
			System.out.println("temp_max: "+temp_max.print_list());
			this.insert_spaces_for_iteration("B");
			System.out.println("checked nodes("+checked_nodes.get_memory_next().get_length()+"): "+checked_nodes.get_memory_next().print_list());
			this.insert_spaces_for_iteration("B");
			System.out.println("head_max_star("+head_max_star.get_length()+"): "+head_max_star.print_list());
			this.insert_spaces_for_iteration("B");
			System.out.println("another: "+another+" checked: "+checked);

			
			if((another||(!another && checked))||((memory_element.get_memory_next().get_length()+B_iteration_deep)<head_max_star.get_length())){

				nodes_to_consider.get_memory_next().delete_last();

				while(nodes_to_consider.get_memory_next().get_length() == 0){


					
						if(nodes_to_consider == TOP_nodes_to_consider)
							break;

						checked_nodes = checked_nodes.get_memory_previous();
						memory_element = memory_element.get_memory_previous();
						dont_consider_connected = dont_consider_connected.get_memory_previous();
						nodes_to_consider = nodes_to_consider.get_memory_previous();

						this.insert_spaces_for_iteration("B");
						System.out.println("****Returning A****");
						this.insert_spaces_for_iteration("B");
						System.out.println("ntc("+nodes_to_consider.get_memory_next().get_length()+"): "+nodes_to_consider.get_memory_next().print_list());
						this.insert_spaces_for_iteration("B");
						System.out.println("dnc("+dont_consider_connected.get_memory_next().get_length()+"): "+dont_consider_connected.get_memory_next().print_list());
						this.insert_spaces_for_iteration("B");
						System.out.println("memory_element("+memory_element.get_memory_next().get_length()+"): "+memory_element.get_memory_next().print_list());
						this.insert_spaces_for_iteration("B");
						System.out.println("temp_max: "+temp_max.print_list());
						this.insert_spaces_for_iteration("B");
						System.out.println("checked nodes("+checked_nodes.get_memory_next().get_length()+"): "+checked_nodes.get_memory_next().print_list());

						
						temp_max.delete(temp_max.get_first());//.delete(nodes_to_consider.get_memory_next().get_last());
//						nodes_to_consider.get_memory_next().delete_last();
						B_iteration_deep--;
						temp_max.negcheck();						
					}

				}
			else{

				checked_nodes.get_memory_next().add(nodes_to_consider.get_memory_next().get_last());
				temp_max.add(nodes_to_consider.get_memory_next().get_last());
				nodes_to_consider.get_memory_next().delete_last();

				temp_max.negcheck();
				temp_max.dupceck();

				
				/////////////////////////////////////////////////////////////////////////
				//                 RUN THIS NODE
				/////////////////////////////////////////////////////////////////////////

				
				Bochert_neighbor(temp_element_P, temp_max.get_first(), nodes_to_consider.get_memory_next(), dont_consider_connected.get_memory_next(),internal_not_connected);				
				if(nodes_to_consider.get_memory_next().get_length() > temp_element_P.get_length()){
					temp_element_P.set_memory_previous(nodes_to_consider.get_memory_next().get_memory_previous());							
					nodes_to_consider.set_memory_next(temp_element_P.copy_double_mem());

					temp_element_P.set_memory_previous(dont_consider_connected.get_memory_next().get_memory_previous());
					dont_consider_connected.set_memory_next(memory_element.get_memory_next().copy_double_mem());
					dont_consider_connected.get_memory_next().set_memory_previous(temp_element_P.get_memory_previous());
				}

				

				temp_connected_nodes = memory_element.get_memory_next().copy_double_mem();

				temp_connected_star.pull_out_intersection(temp_connected_nodes, head_max_star);
				temp_connected_nodes.delete(temp_connected_star);




				/////////////////////////////////////////////////////////////
				//      done?
				////////////////////////////////////////////////////////////


				if (temp_connected_nodes.get_length() <= 1){

					if(temp_connected_nodes.get_length() == 1){
						Bochert_neighbor(temp_connected_star,temp_connected_nodes.get_full_array()[0], empty_node,temp_connected_star,internal_connected);
						temp_connected_star.add(temp_connected_nodes.get_full_array()[0]);							
					}


					if(temp_connected_star.get_length()+temp_max.get_length() > head_max_star.get_length()){

						temp_max.negcheck();

						temp_max.add(temp_connected_star);						

						head_max_star = temp_max.copy_double_mem();

						temp_max.delete(temp_connected_star);
						temp_max.delete(temp_max.get_first());//.delete(nodes_to_consider.get_memory_next().get_last());

						temp_max.negcheck();



					}
					else{
						temp_max.delete_last();//.delete(nodes_to_consider.get_memory_next().get_last());
						//nodes_to_consider.get_memory_next().delete_last();						
					}

					while(nodes_to_consider.get_memory_next().get_length() == 0){

							if(nodes_to_consider == TOP_nodes_to_consider)
								break;

							checked_nodes = checked_nodes.get_memory_previous();
							memory_element = memory_element.get_memory_previous();
							dont_consider_connected = dont_consider_connected.get_memory_previous();
							nodes_to_consider = nodes_to_consider.get_memory_previous();

							this.insert_spaces_for_iteration("B");
							System.out.println("****Returning A****");
							this.insert_spaces_for_iteration("B");
							System.out.println("ntc("+nodes_to_consider.get_memory_next().get_length()+"): "+nodes_to_consider.get_memory_next().print_list());
							this.insert_spaces_for_iteration("B");
							System.out.println("dnc("+dont_consider_connected.get_memory_next().get_length()+"): "+dont_consider_connected.get_memory_next().print_list());
							this.insert_spaces_for_iteration("B");
							System.out.println("memory_element("+memory_element.get_memory_next().get_length()+"): "+memory_element.get_memory_next().print_list());
							this.insert_spaces_for_iteration("B");
							System.out.println("temp_max: "+temp_max.print_list());
							this.insert_spaces_for_iteration("B");
							System.out.println("checked nodes("+checked_nodes.get_memory_next().get_length()+"): "+checked_nodes.get_memory_next().print_list());

							
							temp_max.delete(temp_max.get_first());//.delete(nodes_to_consider.get_memory_next().get_last());
//							nodes_to_consider.get_memory_next().delete_last();
							B_iteration_deep--;
							temp_max.negcheck();
							
						}
					}
				else{


					
/*					this.insert_spaces_for_iteration("B");
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
					this.insert_spaces_for_iteration("B");
					System.out.println("current node: "+nodes_to_consider.get_memory_next().get_last());
					this.insert_spaces_for_iteration("B");
					System.out.println("ntc("+nodes_to_consider.get_memory_next().get_length()+"): "+nodes_to_consider.get_memory_next().print_list());
					this.insert_spaces_for_iteration("B");
					System.out.println("dnc("+dont_consider_connected.get_memory_next().get_length()+"): "+dont_consider_connected.get_memory_next().print_list());
					this.insert_spaces_for_iteration("B");
					System.out.println("memory_element("+memory_element.get_memory_next().get_length()+"): "+memory_element.get_memory_next().print_list());
					this.insert_spaces_for_iteration("B");
					System.out.println("checked nodes("+checked_nodes.get_memory_next().get_length()+"): "+checked_nodes.get_memory_next().print_list());
					this.insert_spaces_for_iteration("B");
					System.out.println("tcn("+temp_connected_nodes.get_length()+"): "+temp_connected_nodes.print_list());
					this.insert_spaces_for_iteration("B");
					System.out.println("tcs("+temp_connected_star.get_length()+"): "+temp_connected_star.print_list());
					
					if(B_calls > 40){
						System.out.println("paused");
						pause();
					}
*/
					checked_nodes.get_memory_next().set_memory_next(new node2());
					checked_nodes.get_memory_next().get_memory_next().set_memory_previous(checked_nodes.get_memory_next());
					checked_nodes = checked_nodes.get_memory_next();

					memory_element.get_memory_next().set_memory_next(new node2(temp_connected_nodes.get_length()+temp_connected_star.get_length()));
					memory_element.get_memory_next().get_memory_next().set_memory_previous(memory_element.get_memory_next());
					memory_element = memory_element.get_memory_next();

					dont_consider_connected.get_memory_next().set_memory_next(temp_connected_star.copy_double_mem());
					dont_consider_connected.get_memory_next().get_memory_next().set_memory_previous(dont_consider_connected.get_memory_next());
					dont_consider_connected = dont_consider_connected.get_memory_next();

					nodes_to_consider.get_memory_next().set_memory_next(temp_connected_nodes.copy_double_mem());
					nodes_to_consider.get_memory_next().get_memory_next().set_memory_previous(nodes_to_consider.get_memory_next());
					nodes_to_consider = nodes_to_consider.get_memory_next();


					B_calls++;
					B_iteration_deep++;





				}


			}
		}




		B_iteration_deep--;		
		return head_max_star;


	}


	private void display_down(node2 disp){
		node2 index = disp;

		System.out.println("displaying down");

		while(index != null){
			System.out.println(index.print_list());
			index = index.get_memory_next();
		}

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

	private void Bochert_neighbor(node2 result, int n, node2 array, node2 node_array, int connection){


		int length_array = array.get_length();
		int length_node_array = node_array.get_length();
		result.set_length(0);

		if (((array == null)||(length_array == 0)) && ((node_array == null)||(length_node_array == 0))){			
			return;
		}

		int nodes_index = 0;
		int nodes_array_index = 0;

		while((nodes_index < length_array)||(nodes_array_index < length_node_array)){

			if((nodes_index < length_array)&&((nodes_array_index >= length_node_array)||(array.get_full_array()[nodes_index] < node_array.get_full_array()[nodes_array_index]))){
				if (((n-1) != (array.get_full_array()[nodes_index]-1)) && (graph[n-1][array.get_full_array()[nodes_index]-1] == connection)){
					result.add_to_end(array.get_full_array()[nodes_index]);
				}
				nodes_index++;
			}
			else if((nodes_array_index < length_node_array)&&((nodes_index >= length_array)||(array.get_full_array()[nodes_index] > node_array.get_full_array()[nodes_array_index]))){

				if (((n-1) != (node_array.get_full_array()[nodes_array_index]-1)) && (graph[n-1][node_array.get_full_array()[nodes_array_index]-1] == connection)){
					result.add_to_end(node_array.get_full_array()[nodes_array_index]);
				}
				nodes_array_index++;
			}

			else if((nodes_array_index < length_node_array)&&((nodes_index >= length_array)||(array.get_full_array()[nodes_index] == node_array.get_full_array()[nodes_array_index]))){
				if (((n-1) != (node_array.get_full_array()[nodes_array_index]-1)) && (graph[n-1][node_array.get_full_array()[nodes_array_index]-1] == connection)){
					result.add_to_end(node_array.get_full_array()[nodes_array_index]);
				}
				nodes_array_index++;
				nodes_index++;
			}

		}

		return;

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

		if((nodes == null)||(nodes.length == 0))
			return true;

		int comparitor;

		if (clique)
			comparitor = 0;
		else
			comparitor = 1;


		for (int i = 0; i < nodes.length ; i++){
			for (int j = 0; j<nodes.length ; j++){
				if (i != j && graph[nodes[i]-1][nodes[j]-1] == comparitor){
					//System.out.println(nodes[i]+" "+nodes[j]+" failed");
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



		graph g = new graph(testie3);
		long start;
		int [] temp;
		long elapsedTimeMillis;



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


		/*		int[] a = {1,3,5,6};
		int[] b = {0,3,5,7};
		node2 na = new node2(a);
		node2 nb = new node2(b);
		node2 nc = new node2(10);
		nc.pull_out_intersection(na, nb);
		System.out.println(nc.print_list());

		g.pause();
		 */			
		for(int i = 0; i<s.length; i++){

			if ((i == 11) && (i != 18) && (i != 19)){
				System.out.println("***********************************************************************************************************");
				System.out.println(i+" "+s[i]);
				g = new graph(s[i]);
				g.display_level = -7;



				System.out.println("Number of nodes: "+g.nodes);


				System.out.println();
				System.out.println();
				System.out.println("AND NOW THE NEWER VERSION");
				g.start_showing_crap = false;

				start = System.currentTimeMillis();
				g.B_calls = 0;
				temp = g.pre_New_Bochert();

				elapsedTimeMillis = System.currentTimeMillis()-start;

				System.out.println("max clique from un-optimized Bochert is: ");
				System.out.println(g.array2string(temp));
				System.out.println("total calls to Bochert: "+g.B_calls);
				System.out.println("__ it took:"+elapsedTimeMillis+" miliseconds");

				System.out.println("is star?: "+g.is_star(temp, true)+" and length is: "+temp.length);

			}
		}




	}

}
