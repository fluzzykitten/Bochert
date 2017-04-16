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

	private int[][] mushroom;


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

	private void add_imemory(node2 new_element,node2 imemory, int new_head){
		//Now, it's important to note that this function being run, presupposes that
		//the find_memory() function has already been run and found that not only 
		//does this set need to be looked at, but it already has removed all sets 
		//that are contained in this set

		node2 temp = imemory.memory_next;
		node2 copy_new_element = new_element.copy();
		copy_new_element.set_node(new_head);
		imemory.incriment_node();


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


	private boolean find_imemory(node imemory, node element, /*node pergatory,*/ int toptop/*, int[] who_was_bigger*/){
		//true means the memory was found, don't add it
		//false means add the element, it isn't currently in memory

		if (start_showing_crap)
			System.out.println(toptop+" is length: "+element.get_length()+" connected to: "+element.print_list());


		node temp = imemory;
		int[] new_toptop = new int[1];

		if (temp.get_memory_next() == null){
			if (start_showing_crap)
				System.out.println(toptop+" Contains: BLANK");			
			return false;
		}

		if  (element.get_length() == 0){

			if (start_showing_crap)
				System.out.println(toptop+" (being empty) is contained in: "+temp.get_memory_next().get_head());			

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
				System.out.println(toptop+" (being empty) is contained in: "+temp.get_memory_next().get_head());			

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



	private boolean find_imemory(node2 imemory, node2 element, node2 pergatory, int toptop){
		//true means the memory was found, don't add it
		//false means add the element, it isn't currently in memory


		//		if (B_calls >= 9372)
		//			start_showing_crap = true;


		node2 temp = imemory;
		//		int[] new_toptop = new int[1];

		if (temp.get_memory_next() == null){
			return false;
		}

		if  (element.get_length() == 0){

			//			new_toptop[0] = toptop;
			pergatory.add(toptop);

			//			who_was_bigger[new_toptop[0]-1] = temp.get_memory_next().get_head();


			return true;
		}

		if (temp.get_memory_next().get_length() == 0){
			//element can't be of zero length now - because it would've returned already

			//there can be only one in memory of size zero...
			//if this hasn't been checked yet
			//if it has been checked already, then don't delete it, leave it where it is
			if (temp.get_memory_next().get_meta_data() == 0){//0 means not checked


				imemory.decriment_node();
				pergatory.add(temp.get_memory_next().get_value());

				//				who_was_bigger[new_toptop[0]-1] = toptop;

				imemory.set_memory_next(null);
			}
			return false;

		}		
		else{ 				

			if (temp.get_memory_next().get_length() >= element.get_length()){
				//				System.out.println("temp.l >= elm.l");
				if (temp.get_memory_next().contains(element)){

					pergatory.add(toptop);

					return true;//temp.get_max_star();
				}
			}
			else{

				while ((temp.get_memory_next() != null) && (element.contains(temp.get_memory_next()))){

					if (temp.get_memory_next().get_meta_data() == 0){



						imemory.decriment_node();

						pergatory.add(temp.get_memory_next().get_value());

						imemory.set_memory_next(imemory.get_memory_next().get_memory_next());
					}
					else
						temp = temp.get_memory_next();
				}
				if (imemory.get_memory_next() == null){
					return false;
				}
			}
		}

		//at this point, the first element of the list of memory has been passed over
		while (temp.get_memory_next() != null){

			if (temp.get_memory_next().get_length() >= element.get_length()){
				if (temp.get_memory_next().contains(element)){

					pergatory.add(toptop);

					return true;//temp.get_max_star();
				}
			}
			else{
				while ((temp.get_memory_next() != null) && (element.contains(temp.get_memory_next()))){


					if (temp.get_memory_next().get_meta_data() == 0){



						imemory.decriment_node();
						pergatory.add(temp.get_memory_next().get_value());

						//						who_was_bigger[new_toptop[0]-1] = toptop;


						temp.set_memory_next(temp.get_memory_next().get_memory_next());
					}
					else
						temp = temp.get_memory_next();

				}
			}
			if (temp.get_memory_next() != null){
				//				System.out.println("incrimenting temp");
				temp = temp.get_memory_next(); 
			}
		}

		return false;
	}

	private boolean find_imemory_with_end_independent_set(node2 imemory, node2 element, int toptop){

		//true means the memory was found, don't add it
		//false means add the element, it isn't currently in memory


		//		if (B_calls >= 9372)
		//			start_showing_crap = true;


		node2 index_imemory = imemory;
		node2 unique_head = new node2();
		node2 unique_array = new node2();
		//		int[] new_toptop = new int[1];

		if (index_imemory.get_memory_next() == null){
			return false;
		}

		if  (element.get_length() == 0){

			return true;
		}

		if (index_imemory.get_memory_next().get_length() == 0){
			//element can't be of zero length now - because it would've returned already

			//there can be only one in memory of size zero...
			//if this hasn't been checked yet
			//if it has been checked already, then don't delete it, leave it where it is

			imemory.decriment_node();
			imemory.set_memory_next(null);

			return false;

		}		
		else{ 				

			while (index_imemory.get_memory_next() != null){

				
				//						&& (element.contains(index_imemory.get_memory_next()))){

				juxtapose(index_imemory.get_memory_next().get_array(), element.get_array(), unique_array, unique_head);
//				System.out.println("comparing array: "+index_imemory.get_memory_next().print_list()+" element: "+element.print_list()+" ua: "+unique_array.print_list()+" uh: "+unique_head.print_list());

				if(this.is_star(unique_head.get_array(), false)){
//					System.out.println("found unique head to contain element, unique_head: "+unique_head.print_list());
					return true;
				}
				else if (this.is_star(unique_array.get_array(), false)){
//					System.out.println("Erasing: "+index_imemory.get_memory_next().print_list());
					
					imemory.decriment_node();
					
					if(imemory == index_imemory){//still on the first node
						imemory.set_memory_next(imemory.get_memory_next().get_memory_next());
					}
					else
						index_imemory.set_memory_next(index_imemory.get_memory_next().get_memory_next());
				}		
				else
					index_imemory = index_imemory.get_memory_next();
			}


		}


		return false;

	}


	private void juxtapose(int[] array, int[] head, node2 unique_array, node2 unique_head){


		int index_array = 0;
		int index_head = 0;
		int index_ua = 0;
		int index_uh = 0;
		int[] ua = new int[array.length];
		int[] uh = new int[head.length];

		while((index_array < array.length)||(index_head < head.length)){
			if(((index_array < array.length)&&(index_head < head.length))&&(array[index_array] == head[index_head])){
				index_array++;
				index_head++;
			}
			else if((index_head >= head.length)||((index_array < array.length)&&(array[index_array] < head[index_head]))){
				ua[index_ua] = array[index_array];
				index_array++;
				index_ua++;
			}
			else if (index_head < head.length){
				uh[index_uh] = head[index_head];
				index_head++;
				index_uh++;				
			}

		}	

		unique_array.array = new int[index_ua];
		System.arraycopy(ua, 0, unique_array.array, 0, index_ua);

		unique_head.array = new int[index_uh];
		System.arraycopy(uh, 0, unique_head.array, 0, index_uh);

	}

	private boolean find_imemory(node2 imemory, node2 element, int toptop){

		//true means the memory was found, don't add it
		//false means add the element, it isn't currently in memory


		//	if (B_calls >= 9372)
		//		start_showing_crap = true;


		node2 temp = imemory;
		//	int[] new_toptop = new int[1];

		if (temp.get_memory_next() == null){
			return false;
		}

		if  (element.get_length() == 0){

			return true;
		}

		if (temp.get_memory_next().get_length() == 0){
			//element can't be of zero length now - because it would've returned already

			//there can be only one in memory of size zero...
			//if this hasn't been checked yet
			//if it has been checked already, then don't delete it, leave it where it is
			if (temp.get_memory_next().get_meta_data() == 0){//0 means not checked


				imemory.decriment_node();
				imemory.set_memory_next(null);
			}
			return false;

		}		
		else{ 				

			if (temp.get_memory_next().get_length() >= element.get_length()){
				//				System.out.println("temp.l >= elm.l");
				if (temp.get_memory_next().contains(element)){

					return true;//temp.get_max_star();
				}
			}
			else{

				while ((temp.get_memory_next() != null) && (element.contains(temp.get_memory_next()))){

					if (temp.get_memory_next().get_meta_data() == 0){



						imemory.decriment_node();

						imemory.set_memory_next(imemory.get_memory_next().get_memory_next());
					}
					else
						temp = temp.get_memory_next();
				}
				if (imemory.get_memory_next() == null){
					return false;
				}
			}
		}

		//at this point, the first element of the list of memory has been passed over
		while (temp.get_memory_next() != null){

			if (temp.get_memory_next().get_length() >= element.get_length()){
				if (temp.get_memory_next().contains(element)){

					return true;//temp.get_max_star();
				}
			}
			else{
				while ((temp.get_memory_next() != null) && (element.contains(temp.get_memory_next()))){


					if (temp.get_memory_next().get_meta_data() == 0){



						imemory.decriment_node();


						temp.set_memory_next(temp.get_memory_next().get_memory_next());
					}
					else
						temp = temp.get_memory_next();

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

		return Bochert(all_nodes, 0, null);
	}

	public int[] pre_New_Bochert(){


		int[] all_nodes = all_neighbors(-1);

		return New_Bochert(all_nodes, 0, null);
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

	private int measure(int set, node nodes_left){
		int ret = 0;

		for(int i = 0; i<nodes_left.get_length(); i++){
			if(mushroom[set-1][i-1] == 1)
				ret++;
		}


		return ret;
	}

	private node find_new_sets(int current_node, node memory, int[] largest){
		if((memory == null)||(memory.get_memory_next() == null)||(memory.get_memory_next().get_length() == 0)){
			largest[0] = -1;
			return new node();
		}

		//	System.out.println("in find_new_sets, printing mem:");
		//	memory.print_memory();

		node new_sets = new node();
		node temp = null;
		node index_new_sets = new_sets;
		node index_memory = memory.get_memory_next();
		int max = -1;
		node new_element = null;

		while(index_memory != null){
			if(index_memory.get_length() == 0){
				index_memory = index_memory.get_memory_next();
			}
			else{
				//		System.out.println("current set: "+index_memory.print_list());

				//go through all sets

				if(index_memory.find(current_node)){
					//if the current set is connected to current_node...
					index_memory.delete(current_node);

					new_element = new node();

					if(index_memory.get_length() != 0){
						//			index_new_sets.get_memory_next().set_meta_data(index_memory.get_meta_data());

						temp = index_memory.get_next();//CHECK first node
						if (graph[current_node-1][temp.get_value()-1] == 1){
							new_element.add_to_end(temp.get_value());
						}
						temp = temp.get_next();

						while(temp != index_memory.get_next()){
							//cycle through the nodes that are left to look at (all the nodes past the current_node)
							if (graph[current_node-1][temp.get_value()-1] == 1){
								//if the set's current node being looked at is connected to the current node, add it to the new set
								new_element.add_to_end(temp.get_value());
							}
							temp = temp.get_next();
						}
					}
					else{

					}

					if((new_element.get_length() != 0)||(max <= 0)){

						if ((max == 0) && (new_element.get_length() > 0)){
							new_sets = new node();
							index_new_sets = new_sets;
						}

						new_element.set_head(index_memory.get_head());
						index_new_sets.set_memory_next(new_element);//add it...
						new_sets.incriment_length();

						index_new_sets = index_new_sets.get_memory_next();
						if (index_new_sets.get_length() > max){
							max = index_new_sets.get_length();
							//				set_finder[0] = index_new_sets.
						}
					}
				}

				index_memory = index_memory.get_memory_next();

			}
		}
		largest[0] = max;
		return new_sets;

	}


	private int[] New_Bochert(int[] nodes, int current_max, int[] current_max_star){


//System.out.println("Start");
		//not original Bochert


		if ((nodes == null)||(nodes.length == 0))
			return current_max_star;

		int[] temp_connected_nodes = null;
		int[] max_star;
		node2 star_memory = new node2();

		if (current_max_star == null){
			max_star = new int[0];
		}
		else{
			max_star = current_max_star;
			star_memory.set_memory_next(new node2 (max_star));
		}

		int[] temp_max = null;
		int temp_current_max = current_max;
		int current_node;
		int node_that_found_max_star = -1;


		B_calls++;
		B_iteration_deep++;


		node2 nodes_to_consider = new node2();
		node2 head_max_star = new node2(max_star);
		node2 imemory = new node2();

		//	node2 pergatory_and_max_star = new node2();
		node2 memory_element = new node2();
		node2 index_imemory = imemory;
		//	node2 deleted_nodes = new node2();
		int[] temp_connected_star= null;

		node2 temp_star_element = null;
		node2 index_star_memory = star_memory;
		int[] temp_connected_star_temp = null;
		int[] another_temp = null;

		int index_nodes_to_consider = -1;
		

		current_node = nodes[0];

		index_star_memory  = Special_Bochert_neighbor(current_node, nodes);
		
		temp_connected_nodes = index_star_memory.get_array();
		nodes_to_consider = index_star_memory.get_memory_next();
		int[] pergatory = temp_connected_nodes; 
		
		memory_element = new node2(temp_connected_nodes);//new_element.copy();
		memory_element.set_node(current_node);
		memory_element.set_meta_data(1);
		
		imemory.set_memory_next(memory_element);
		
		//this.add_imemory(memory_element, imemory, current_node);			

		
		do{				



				//Find Star
				index_star_memory = star_memory;
				temp_connected_star = new int[0];

				while(index_star_memory.get_memory_next() != null){
					another_temp = this.intersection(temp_connected_nodes, index_star_memory.get_memory_next().get_array());
					temp_connected_star_temp = Bochert_neighbor(current_node, another_temp);
					if (temp_connected_star_temp.length > temp_connected_star.length){
						temp_connected_star = temp_connected_star_temp;
					}
					index_star_memory = index_star_memory.get_memory_next(); 
				}

				temp_star_element = new node2(temp_connected_nodes);
				temp_star_element.delete(temp_connected_star);
				temp_connected_nodes = temp_star_element.get_array();		




				if(B_iteration_deep <= 0){//this.nodes/100){
					this.insert_spaces_for_iteration("B");
					System.out.println(current_node+" "+B_calls);			
				}

				if ((temp_connected_nodes.length+temp_connected_star.length)== 0 && temp_current_max == 0 && node_that_found_max_star == -1){
					node_that_found_max_star = current_node;
					temp_current_max = 1;
					max_star = new int[0];
				}
				else if (((temp_connected_nodes.length+temp_connected_star.length) >= temp_current_max) && ((temp_connected_nodes.length+temp_connected_star.length) != 0)) {

					if ((temp_connected_nodes.length+temp_connected_star.length) == 1) {

						if (temp_connected_nodes.length == 1)
							temp_max = temp_connected_nodes;
						else
							temp_max = temp_connected_star;
					}
					else{

						if (temp_current_max == 0){
							temp_max = New_Bochert(temp_connected_nodes, temp_current_max, temp_connected_star);
						}
						else{
							temp_max = New_Bochert(temp_connected_nodes, temp_current_max-1, temp_connected_star);
						}
					}
					if ((temp_max != null) && (temp_max.length >= temp_current_max)){

						nodes_to_consider.add(head_max_star.get_array());
						node_that_found_max_star = current_node;
						max_star = temp_max;
						temp_current_max = max_star.length+1;

						head_max_star = new node2(max_star);

						nodes_to_consider.delete(max_star);

					}		

					temp_star_element = new node2(temp_max);
					if (!this.find_imemory(star_memory, temp_star_element, current_node)){
						this.add_imemory(temp_star_element, star_memory, current_node);
					}


				}


			

			//nodes_to_consider.delete(current_node);

while((index_nodes_to_consider+1) < nodes_to_consider.get_length()){				
				
				index_nodes_to_consider++;
				current_node = nodes_to_consider.get_array()[index_nodes_to_consider];

				temp_connected_nodes = Bochert_neighbor(current_node, nodes_to_consider.get_array(),pergatory);

				memory_element = new node2(temp_connected_nodes);//new_element.copy();
				memory_element.set_node(current_node);
				memory_element.set_meta_data(1);

				
				if(this.find_imemory_with_end_independent_set(index_imemory, memory_element, current_node)){
					//nodes_to_consider.delete(current_node);
				}
				else{
					this.add_imemory(memory_element, imemory, current_node);			
					break;
				}

			}
			

			
//			System.out.println(2);

//			System.out.println("Current_nodes: "+nodes_to_consider.print_list());

		}while (((index_nodes_to_consider+1) < nodes_to_consider.get_length())&&((nodes_to_consider.get_length()!=0)&&((nodes_to_consider.get_length()+pergatory.length) > current_max)));


		if (node_that_found_max_star == -1){
			B_iteration_deep--;		
			return max_star;
		}


		//	int[] temp_finder = {node_that_found_max_star};
		//	node finder = new node2(temp_finder);

		head_max_star.add(node_that_found_max_star);

		B_iteration_deep--;

		return head_max_star.print_array();

	}


	private int[] Bochert(int[] nodes, int current_max, int[] current_max_star){
		//not original Bochert


		if (nodes == null)
			return current_max_star;

		int[] temp_connected_nodes = null;
		int[] max_star;
		node2 star_memory = new node2();

		if (current_max_star == null){
			max_star = new int[0];
		}
		else{
			max_star = current_max_star;
			star_memory.set_memory_next(new node2 (max_star));
		}

		int[] temp_max = null;
		int temp_current_max = current_max;
		int current_node;
		int node_that_found_max_star = -1;


		B_calls++;
		B_iteration_deep++;


		node2 nodes_to_consider = new node2(nodes);
		node2 head_max_star = new node2(max_star);
		node2 imemory = new node2();

		boolean found_new_set = true;
		node2 pergatory = new node2();
		node2 dantes_inferno = new node2();
		int[] index_dante = null;
		//		node2 pergatory_and_max_star = new node2();
		node2 memory_element = new node2();
		int[] delete_this = new int[1];
		node2 index_imemory = imemory;
		//		node2 deleted_nodes = new node2();
		int[] temp_connected_star= null;

		node2 temp_star_element = null;
		node2 index_star_memory = star_memory;
		int[] temp_connected_star_temp = null;
		int[] another_temp = null;

		do{

			//////////////////////////////////////////////////
			/**************** check function ****************/
			//////////////////////////////////////////////////

			if (found_new_set == true){

				dantes_inferno = nodes_to_consider;
				pergatory = new node2(); //this is a waste... oh well... I'm trying this out quick, not really worth the effort
				// so now all the nodes are in temp, and pergatory and nodes to consider are empty
				index_dante = dantes_inferno.print_array();
				index_imemory = imemory;


				//this function cycles through imemory and pulls out all the nodes that haven't be checked yet to decide if they should still be in imemory
				while (index_imemory.get_memory_next() != null){
					if (index_imemory.get_memory_next().get_meta_data() == 0){//if it hasn't been checked yet
						//						where_is_that_darn_node[index_imemory.get_memory_next().get_head()-1] = 0;
						index_imemory.set_memory_next(index_imemory.get_memory_next().get_memory_next());
						//imemory.decriment_length();
						imemory.decriment_node();
					}
					else{
						index_imemory = index_imemory.get_memory_next();
					}
				}


				for (int i = 0; i < index_dante.length; i++){


					memory_element = new node2(Bochert_neighbor(index_dante[i], head_max_star.get_array(), dantes_inferno.get_array()));


					if (!this.find_imemory(imemory, memory_element, pergatory, index_dante[i]/*,who_was_bigger*/)){

						this.add_imemory(memory_element, imemory, index_dante[i]);
					}
					else {
					}

					dantes_inferno.delete(index_dante[i]);
				}

				//				pergatory_and_max_star = pergatory.copy();
				//				pergatory_and_max_star.add(head_max_star.copy());
				nodes_to_consider = imemory.print_memory_to_consider();
				found_new_set = false;
				index_imemory = imemory;

				while((index_imemory.get_memory_next() != null)&&(index_imemory.get_memory_next().get_meta_data() == 1)){
					index_imemory = index_imemory.get_memory_next();
				}

			}

			///////////////////////////////////////////////////////////
			//               	END CHECK FUNCTION                   //
			///////////////////////////////////////////////////////////


			if (((nodes_to_consider.get_length()+pergatory.get_length()+max_star.length) <= current_max)||(index_imemory.get_memory_next() == null)){

				if (node_that_found_max_star == -1){
					B_iteration_deep--;		
					return max_star;
				}


				//				int[] temp_finder = {node_that_found_max_star};
				//				node2 finder = new node2(temp_finder);

				head_max_star.add(node_that_found_max_star);

				B_iteration_deep--;

				return head_max_star.print_array();

			}


			current_node = index_imemory.get_memory_next().get_value();
			//			temp_connected_nodes = Bochert_neighbor(current_node, nodes_to_consider.combine(pergatory_and_max_star));


			//inefficient, should make a Bochert_neighbor(int, int[], int[], int[])
			temp_connected_nodes = Bochert_neighbor(current_node, nodes_to_consider.get_array(),pergatory.get_array());
			temp_connected_nodes = Bochert_neighbor(current_node, temp_connected_nodes, max_star);

			index_star_memory = star_memory;
			temp_connected_star = new int[0];

			//			System.out.println("Starting star loop:");

			while(index_star_memory.get_memory_next() != null){

				//				System.out.println("temp_connected_nodes: "+array2string(temp_connected_nodes)+" index_star_memory.get_memory_next().get_array(): "+array2string(index_star_memory.get_memory_next().get_array()));

				another_temp = this.intersection(temp_connected_nodes, index_star_memory.get_memory_next().get_array());

				//				System.out.println("intersection: "+array2string(another_temp));

				temp_connected_star_temp = Bochert_neighbor(current_node, another_temp);

				//				System.out.println("temp_connected_star_temp: "+array2string(temp_connected_star_temp));

				if (temp_connected_star_temp.length > temp_connected_star.length){
					//					System.out.println(" was bigger than temp_connected_star: "+array2string(temp_connected_star));
					temp_connected_star = temp_connected_star_temp;
				}

				index_star_memory = index_star_memory.get_memory_next(); 

			}

			temp_star_element = new node2(temp_connected_nodes);
			temp_star_element.delete(temp_connected_star);
			temp_connected_nodes = temp_star_element.get_array();



			if(B_iteration_deep <= 0){//this.nodes/100){
				this.insert_spaces_for_iteration("B");
				System.out.println(current_node+" "+B_calls);			
			}


			if ((temp_connected_nodes.length+temp_connected_star.length)== 0 && temp_current_max == 0 && node_that_found_max_star == -1){
				node_that_found_max_star = current_node;
				temp_current_max = 1;
				max_star = null;
			}
			else if (((temp_connected_nodes.length+temp_connected_star.length) >= temp_current_max) && ((temp_connected_nodes.length+temp_connected_star.length) != 0)) {

				if ((temp_connected_nodes.length+temp_connected_star.length) == 1) {

					if (temp_connected_nodes.length == 1)
						temp_max = temp_connected_nodes;
					else
						temp_max = temp_connected_star;
				}
				else{


					if (temp_current_max == 0){
						temp_max = Bochert(temp_connected_nodes, temp_current_max, temp_connected_star);
					}
					else{
						temp_max = Bochert(temp_connected_nodes, temp_current_max-1, temp_connected_star);
					}
				}
				if ((temp_max != null) && (temp_max.length >= temp_current_max)){


					nodes_to_consider.add(pergatory.get_array());
					nodes_to_consider.add(head_max_star.get_array());
					node_that_found_max_star = current_node;
					max_star = temp_max;
					temp_current_max = max_star.length+1;

					head_max_star = new node2(max_star);

					nodes_to_consider.delete(max_star);

					found_new_set = true;




				}


				temp_star_element = new node2(temp_max);
				if (!this.find_imemory(star_memory, temp_star_element, current_node)){
					this.add_imemory(temp_star_element, star_memory, current_node);
				}


			}


			delete_this[0] = index_imemory.get_memory_next().get_value();

			nodes_to_consider.delete(index_imemory.get_memory_next().get_value());
			//			deleted_nodes.add(index_imemory.get_memory_next().get_value());// .delete(index_imemory.get_memory_next().get_value());
			//deleted_nodes.add(nodes_to_consider.split_nodes(delete_this));// .delete(index_imemory.get_memory_next().get_value());

			memory_element = new node2(temp_connected_nodes);//new_element.copy();
			memory_element.set_node(current_node);
			memory_element.set_meta_data(1);
			memory_element.set_memory_next(index_imemory.get_memory_next().get_memory_next());		
			index_imemory.set_memory_next(memory_element);

			while((index_imemory.get_memory_next() != null)&&(index_imemory.get_memory_next().get_meta_data() == 1)){
				index_imemory = index_imemory.get_memory_next();
			}

			imemory.decriment_node();


		}while (index_imemory.get_memory_next() != null);


		if (node_that_found_max_star == -1){
			B_iteration_deep--;		
			return max_star;
		}


		//		int[] temp_finder = {node_that_found_max_star};
		//		node finder = new node2(temp_finder);

		head_max_star.add(node_that_found_max_star);

		B_iteration_deep--;

		return head_max_star.print_array();

	}








	private int[] Bochert_neighbor(int n, int[] nodes){

		if (nodes == null || nodes.length == 0 || nodes[0] == -1)
			return new int[0];

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

	private int[] Bochert_neighbor(int n, int[] nodes, int[] nodes2){

		if (((nodes == null)||(nodes.length == 0)) && ((nodes2 == null)||(nodes2.length == 0)))
			return new int[0];
		if ((nodes == null)||(nodes.length == 0))
			return Bochert_neighbor(n,nodes2);
		if ((nodes2 == null)||(nodes2.length == 0))
			return Bochert_neighbor(n,nodes);

		int [] temp = new int[nodes.length+nodes2.length];
		int size = 0;
		int nodes_index = 0;
		int nodes2_index = 0;

		while((nodes_index < nodes.length)||(nodes2_index < nodes2.length)){

			if((nodes_index < nodes.length)&&((nodes2_index >= nodes2.length)||(nodes[nodes_index] < nodes2[nodes2_index]))){
				if (((n-1) != (nodes[nodes_index ]-1)) && (graph[n-1][nodes[nodes_index ]-1] == internal_connected)){
					temp[size] = nodes[nodes_index ];
					size++;
				}
				nodes_index++;
			}
			else if((nodes2_index < nodes2.length)&&((nodes_index >= nodes.length)||(nodes[nodes_index] > nodes2[nodes2_index]))){
				//System.out.println("nodes2[nodes2_index]: "+nodes2[nodes2_index]+" n: "+n);
				if (((n-1) != (nodes2[nodes2_index]-1)) && (graph[n-1][nodes2[nodes2_index]-1] == internal_connected)){
					temp[size] = nodes2[nodes2_index];
					size++;
				}
				nodes2_index++;
			}

			else if((nodes2_index < nodes2.length)&&((nodes_index >= nodes.length)||(nodes[nodes_index] == nodes2[nodes2_index]))){
				if (((n-1) != (nodes2[nodes2_index]-1)) && (graph[n-1][nodes2[nodes2_index]-1] == internal_connected)){
					temp[size] = nodes2[nodes2_index];
					size++;
				}
				nodes2_index++;
				nodes_index++;
			}

		}

		int[] result = new int[size];
		System.arraycopy(temp, 0, result, 0, size);
		return result;

	}

	private node2 Special_Bochert_neighbor(int n, int[] nodes){

		node2 node_result = new node2();
		
		if (nodes == null || nodes.length == 0 || nodes[0] == -1){
			node_result.set_memory_next(new node2());
			return node_result;
		}
		
		int[] different = new int[nodes.length];
		int [] temp = new int[nodes.length];
		int size = 0;
		int size_diff = 0;

		for (int i = 0; i<nodes.length; i++){

			if (((n-1) != (nodes[i]-1)) && (graph[n-1][nodes[i]-1] == internal_connected)){
				temp[size] = nodes[i];
				size++;
			}
			else{
				different[size_diff] = nodes[i];
				size_diff++;
			}
		}

		int[] result = new int[size];
		System.arraycopy(temp, 0, result, 0, size);

		node_result.array = result;
		
		result = new int[size_diff];
		System.arraycopy(different, 0, result, 0, size_diff);

		node_result.set_memory_next(new node2(result));
		
		return node_result;
		
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
		int[] isstar = {26,47,54,69,104,119,120,134,144,148,157,182};

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



		//graph g = new graph(15,5);
		//graph g = new graph("brock200_1.clq"); //causes an error
		//  graph g = new graph("hamming6-2.clq"); // good small testing graph
		//	graph g = new graph("c-fat500-1.clq");
		//	graph g = new graph("c-fat500-5.clq.b"); //dne
		//graph g = new graph("c-fat200-5.clq.b"); //dne
		//	graph g = new graph("keller6.clq");	
		graph g = new graph(testie3);

/*		int[] a = {2,3,4,5,6,7,8};
		int[] b = {3,4,5,6,7,8,10};
		int[] c = {4,5,6,7,9};
		int[] d = {1,4,5,6,7,9,10};

		
		node2 memory = new node2();
		memory.set_memory_next(new node2(a));
		memory.get_memory_next().set_memory_next(new node2(b));
		memory.get_memory_next().get_memory_next().set_memory_next(new node2(c));
		
		
		memory.print_memory();

		
		node2 element = new node2(d);
		System.out.println("Element: "+element.print_list());
		
		boolean what = g.find_imemory_with_end_independent_set(memory, element, 0);
		System.out.println("what: "+what);
		memory.print_memory();
		g.pause();
	*/	
		//System.out.println("a: "+g.array2string(a)+" b: "+g.array2string(b)+" ut: "+ut.print_list()+" uh: "+uh.print_list());

		//g.pause();


		/*		int[] al1 = {1,2,3,4};
		int[] al2 = {5,6,7};

		int[] ar1 = {8,9,11};
		int[] ar2 = {7,9,10};

		node l1 = new node(al1);
		node r1 = new node(ar1);
		node l2 = new node(al2);
		node r2 = new node(ar2);

		r1.set_previous(l1);
		r2.set_previous(l2);

		int[] memory_unique = new int[1];
		int[] element_unique = new int[1];
		boolean sim;
		sim = r1.similar_differences(r2, memory_unique, element_unique);

		System.out.println("Mem: "+r1.get_previous().print_list()+" | "+r1.print_list()+"\nElem: "+r2.get_previous().print_list()+" | "+r2.print_list());
		System.out.println("similar: "+sim+" mem: "+memory_unique[0]+" elem: "+element_unique[0]);
		 */


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

			if ((i == 0) && (i != 18) && (i != 19) && (i != 21) && (i != 22)){
				System.out.println("***********************************************************************************************************");
				System.out.println(i+" "+s[i]);
				g = new graph(s[i]);
				//g = new graph(testie);

				//		for(int n = 1; n<g.nodes; n++)
				//			System.out.println("Node: "+n+" - "+g.array2string(g.Bochert_neighbor(n, g.all_neighbors(-1))));
				//		g.pause();

				//		System.out.println("check to see if provided solution is a clique: "+g.is_star(isstar,true));


				System.out.println("Number of nodes: "+g.nodes);

				g.start_showing_crap = false;
				long start = System.currentTimeMillis();
				g.B_calls = 0;
				int [] temp = g.pre_New_Bochert();
				long elapsedTimeMillis = System.currentTimeMillis()-start;

				System.out.println();
				System.out.println();
				System.out.println("NOW FOR THE NEW VERSION");
				System.out.println("max clique from optimized Bochert is: ");
				System.out.println(g.array2string(temp));
				System.out.println("total calls to Bochert: "+g.B_calls);
				System.out.println("__ it took:"+elapsedTimeMillis+" miliseconds");

				if (temp != null)
					System.out.println("is star?: "+g.is_star(temp, true)+" and length is: "+temp.length);

				System.out.println();




				System.out.println();
				System.out.println();
				System.out.println("AND NOW THE OLDER VERSION");
				g.start_showing_crap = false;

				start = System.currentTimeMillis();
				g.B_calls = 0;
				temp = g.pre_Bochert();
				elapsedTimeMillis = System.currentTimeMillis()-start;

				System.out.println("max clique from un-optimized Bochert is: ");
				System.out.println(g.array2string(temp));
				System.out.println("total calls to Bochert: "+g.B_calls);
				System.out.println("__ it took:"+elapsedTimeMillis+" miliseconds");

				System.out.println("is star?: "+g.is_star(temp, true)+" and length is: "+temp.length);
				
			}
		}

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
