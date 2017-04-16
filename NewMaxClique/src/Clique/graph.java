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
	private int previous_depth = 0;
	private int count_down = 20;
	private boolean trigger = false;




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




	private boolean is_there_another(final node2 check_set, final node2 nodes_to_consider, final node2 dont_consider_connected, node2 result, boolean display){

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

	private int check_extra_nodes(node2 base, node2 next, boolean display, int deep){
		//return 1 - delete base
		//return 2 - do nothing
		//return 3 - delete next


		//		putty.set_length(0);

		node2 base_uniq = new node2(base.get_length());
		node2 next_uniq = new node2(next.get_length());

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

		node2 base_temp = new node2(nodes);
		node2 next_temp = new node2(nodes);

		node2 base_temp_uniq = new node2(nodes);
		node2 next_temp_uniq = new node2(nodes);

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

			this.Bochert_neighbor(next_temp, next_uniq.get_full_array()[n], empty_node, next, internal_connected);

			for(int i = 0; i<base_uniq.get_length(); i++){

				this.Bochert_neighbor(base_temp, base_uniq.get_full_array()[i], empty_node, base, internal_connected);

				base_temp.similar_differences(next_temp, base_temp_uniq, next_temp_uniq);

				if(next_temp_uniq.get_length() == 0){
					next.delete(next_uniq.get_full_array()[n]);
					next_uniq.delete(next_uniq.get_full_array()[n]);
					n--;
					i=base_uniq.get_length();
				}
				else if(base_temp_uniq.get_length() == 0){
					base.delete(base_uniq.get_full_array()[i]);
					base_uniq.delete(base_uniq.get_full_array()[i]);
					i--;
					//					i=base_uniq.get_length();
				}
				else if((base_temp_uniq.get_length() == 1)&&(next_temp_uniq.get_length() == 1)){

					recursion = check_extra_nodes(base_temp, next_temp, display,deep+1);

					if(recursion == 3){
						next.delete(next_uniq.get_full_array()[n]);
						next_uniq.delete(next_uniq.get_full_array()[n]);
						n--;
						i=base_uniq.get_length();
					}
					else if(recursion == 1){
						base.delete(base_uniq.get_full_array()[i]);
						base_uniq.delete(base_uniq.get_full_array()[i]);
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

	public int[] pre_New_Bochert(boolean display){


		int[] all_nodes = all_neighbors(-1);


		//		return New_Bochert(new node2(all_nodes), 0, new node2(),false).get_array_min_size();

		sort_nodes();
		reorganize_nodes();

		return merge_sort(unreorganize_nodes(New_Bochert(new node2(all_nodes), 0, new node2(),display)), all_nodes);


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

	private int[] unreorganize_nodes(node2 result){
		//find out what the real nodes are

		int[] real_result = new int[result.get_length()];

		for(int i = 0; i<result.get_length(); i++)
			real_result[i] = nodes_ordered_increasing[result.get_full_array()[i]-1];

		graph = old_graph;

		return real_result;
	}

	private int[] merge_sort(int[] list, int[] weight)
	{
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
			if (weight[left[lefti]-1] <= weight[right[righti]-1]){ 
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

	private node2 New_Bochert(node2 nodes, int current_max, node2 current_max_star, boolean display){

		//not original Bochert

		/*		if(B_calls >= 152255)
			display_level = 25;
		if(B_calls >= 3131480)
			display_level = 35;		
//		if(B_calls >= 5664814)
//			display_level = 45;		


		if(B_calls >= 10000000){
			System.out.println("paused");
			pause();
		}



		if(B_calls % 1000000 == 0)
			System.out.println(B_calls);
		if((B_calls > -10000000) && B_iteration_deep < 30){
//			display=true;
		}
		else{
//			display = false;
		}

			if (display && (B_calls > -10000000) &&(B_calls % 300 == 0)){
				System.out.println("Stop, colaborate and listen, Ice is back with a brand new invention");
				pause();
			}

//		if (B_calls == 4067400){
//			display=true;
//		}
//		else
			display = false;
		 */

		/*			if(B_iteration_deep < previous_depth){
				pause();
				trigger = true;
				count_down = -1;
			}
			else if(trigger && (count_down < 0)){
				trigger = false;
				previous_depth = B_iteration_deep;
				pause();
			}
			else if(trigger){
				count_down--;
				previous_depth = B_iteration_deep;
			}
			else{
				previous_depth = B_iteration_deep;
			}
		 */
		//		}			
		//		previous_depth = B_iteration_deep;


		if ((nodes == null)||(nodes.get_length() == 0))
			return current_max_star;


		node2 head_max_star;
		if (current_max_star == null){
			head_max_star = new node2();
		}
		else{
			head_max_star = current_max_star;
		}

		node2 temp_max = new node2();
		int temp_current_max = current_max;
		int current_node;
		int node_that_found_max_star = -1;


		B_calls++;
		B_iteration_deep++;


		node2 nodes_to_consider = nodes;


		node2 memory_element = new node2(new int[nodes.get_length()+current_max_star.get_length()]); //will not be passed and will not be corrupted
		node2 temp_connected_nodes_N = null; //will be passed down the chain and thus might be corrupted
		node2 temp_connected_star_N= new node2(new int[nodes.get_length()+current_max_star.get_length()]); //will be passed down the chain but won't be corrupted

		node2 temp_element_P = new node2(new int[nodes.get_length()+current_max_star.get_length()]); //will be protected
		//int[] temp_connected_star_temp = null;

		node2 dont_consider_connected = current_max_star.copy();

		//		if (dont_consider_connected == null)
		//			dont_consider_connected = new node2(new int[0]);

		//		int[] super_star = {108,20,73,18,93,178,90,94,142,135,186,87,81,150,92,39,68,102,85,136,134};
		//		int[] super_star = {1,2,4,5,10,11,15,18,24,30,33,36,39,42,45,48,51,54,57,60,63,66,67,71,74,78,80,84,87,90,93,96,97,102,104,108,111,114,117,120,123,126,129,132,135,138,140,144,147,150,153,156,159,162,165,168,171,174,177,180,183,185,189,191,195,198,201,204,205,210,212,216,219,222,225,228,231,234,237,240,243,246,249,252,255,258,261,264,267,270,273,276,279,282,285,288,291,292,297,298,303,306,309,312,314,318,321,324,327,330,333,336,339,342,345,348,351,354,357,360,363,366,367,372,375,376};
		//		int[] super_star = {378,30,32,34,37,40,43,46,49,52,55,58,61,65,68,70,74,77,79,82,85,88,93,94,97,100,103,106,109,112,115,118,121,124,127,130,133,136,140,142,145,148,151,154,157,160,163,166,169,172,176,178,181,184,187,190,193,196,199,202,205,208,211,214,217,220,224,226,230,232,237,238,241,244,247,250,253,256,261,262,267,268,272,274,277,280,283,286,289,292,295,298,301,304,307,310,313,316,319,322,325,328,331,334,337,340,343,346,349,352,355,358,363,364,368,370,373,4,10,13,17,18,23,24,26,27};
		//		int[] super_star = {7,17,21,28,42,46,55,86,97,117,138,143,149,154,162,167,171,176,179,187,199};
		//		int[] super_star = {187,176,185,196,194,192,184,180,172,171,165,162,7,17,21,28,42,46,55,86,97,117,138,143,149,154,167,179,199};
		//		int[] super_star = {4,26,32,48,58,62,79,83,103,108,116,122,131,138,144,145,176,179,185,190};
		//		56 85 191
//		int[] super_star = {200,199,173,190,183,175,178,133,168,157,137,148,136,93,47,66,76,91,186,195};
		int[] super_star = {1,2,4,5,34,214,286,10,11,16,100,115,121,22,118,   37,40,45,47,55,60,67,74,78,80,84,92,95,106,110,114,135,136,140,145,148,152,156,163,167,172,177,181,184,189,191,199,203,210,212,219,225,230,233,237,240,245,249,255,261,266,269,272,275,352,357,362,365,378};
		//already in star: 13 14 17 23 25 26 27 109 112 133 376
		//removed ones that matter: 15,18,24,30,33,49,52,61,66,85,88,97,124,127,130,142,157,160,169,178,193,196,205,220,226,241,250,256,262,277,280,283,289,292,295,298,301,304,307,310,313,316,319,322,325,328,331,334,337,340,343,346,349,358,367,370,373,
		//combined already in star: 13 14 15 17 18 23 24 25 26 27 30 33 49 52 61 66 85 88 97 109 112 124 127 130 133 142 157 160 169 178 193 196 205 220 226 241 250 256 262 277 280 283 289 292 295 298 301 304 307 310 313 316 319 322 325 328 331 334 337 340 343 346 349 358 367 370 373 376
		//at .9 replaced 70 with 16
		//at .13 replaced 103 with 22
		//at .0 replace 39 with 37, 164 to 163, 183 to 181, 200 to 199
		//42 to 40, 138 to 136, 146 to 145, 173 to 172, 185 to 184, 354 to 352
		//57 to 55, 150 to 148
		
		
		
		
		//insert 185,196,194,192,184,180,172,165		
		
		boolean another = false;
		//		boolean checked = false;
		boolean disp = false;
		//		node2 result_from_others = new node2();

		//		node2 checked_nodes = new node2();

		/*		System.out.println("before reduction, length ntc: "+nodes_to_consider.get_length());

		for(int i = nodes_to_consider.get_length()-1; i >= 0; i--){
			if(is_there_another(nodes_to_consider,dont_consider_connected, nodes_to_consider.get_full_array()[i],temp_element_P)){
				System.out.println("deleting "+nodes_to_consider.get_full_array()[i]+" because node exists: "+temp_element_P.print_list());
				nodes_to_consider.delete(nodes_to_consider.get_full_array()[i]);
			}
		}

		System.out.println("after reduction, length ntc: "+nodes_to_consider.get_length());
		pause();
		 */		


		Bochert_neighbor(memory_element, nodes_to_consider.get_full_array()[0], dont_consider_connected, nodes_to_consider,internal_connected);
		Bochert_neighbor(temp_element_P, nodes_to_consider.get_full_array()[0], dont_consider_connected, nodes_to_consider, internal_not_connected);
		temp_element_P.add(nodes_to_consider.get_full_array()[0]);
		//		if(nodes_to_consider.get_length() > temp_element_P.get_length()){
		dont_consider_connected = memory_element.copy();
		nodes_to_consider = temp_element_P.copy();

		if(display){
			this.insert_spaces_for_iteration("B");
			System.out.println(" NEW ntc("+nodes_to_consider.get_length()+" NEW dcc("+dont_consider_connected.get_length()+" ntc is: "+nodes_to_consider.print_list());
		}

		//		}

		int numnum;
		int result;

		node2 run_me = new node2(nodes.get_length()+current_max_star.get_length());
		node2 putty = new node2(nodes.get_length()+current_max_star.get_length());

		do{

			current_node = nodes_to_consider.get_full_array()[0];
			nodes_to_consider.delete(current_node);

			Bochert_neighbor(memory_element, current_node, dont_consider_connected, nodes_to_consider,internal_connected);

			another = false;

			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("Current_node: "+current_node+" length: "+memory_element.get_length());
			}

			//			another = is_there_another(memory_element, nodes_to_consider, dont_consider_connected, temp_element_P, false);

			/*			while(is_there_another(memory_element, nodes_to_consider, dont_consider_connected, temp_element_P, false)){

			current_node = temp_element_P.get_full_array()[0];
			nodes_to_consider.delete(current_node);
			dont_consider_connected.delete(current_node);
//			numnum = 0;

			Bochert_neighbor(memory_element, current_node, dont_consider_connected, nodes_to_consider,internal_connected);
			Bochert_neighbor(temp_element_P, current_node, dont_consider_connected, nodes_to_consider, internal_not_connected);
			if(nodes_to_consider.get_length() > temp_element_P.get_length()){
				dont_consider_connected = memory_element.copy();
				nodes_to_consider = temp_element_P.copy();

				if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println(" NEW ntc("+nodes_to_consider.get_length()+" NEW dcc("+dont_consider_connected.get_length()+" ntc is: "+nodes_to_consider.print_list());
				}
			}

			}
			 */

			numnum = 0;
			//numnum = nodes_to_consider.get_length();
			while(numnum < nodes_to_consider.get_length()){
				//for(int i = 0; i<nodes_to_consider.get_length(); i++){

				Bochert_neighbor(temp_element_P, nodes_to_consider.get_full_array()[numnum], dont_consider_connected, nodes_to_consider,internal_connected);							

				if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println("In NewBochert, current_node is: "+current_node+" looking at ntc node of: "+nodes_to_consider.get_full_array()[numnum]);
				}



				//another = is_there_another(memory_element, nodes_to_consider, dont_consider_connected, temp_element_P, false);
				result = check_extra_nodes(memory_element, temp_element_P,false,0);
				if(result == 3){
					if(graph[current_node-1][nodes_to_consider.get_full_array()[numnum]-1] == 0){
						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println(current_node+"**deleting from ntc: "+nodes_to_consider.get_full_array()[numnum]+" ntc length was: "+nodes_to_consider.get_length());
						}
						nodes_to_consider.delete(nodes_to_consider.get_full_array()[numnum]);
					}
					else{
						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println(current_node+"**moving from ntc to dcc: "+nodes_to_consider.get_full_array()[numnum]);
						}
						dont_consider_connected.add(nodes_to_consider.get_full_array()[numnum]);
						nodes_to_consider.delete(nodes_to_consider.get_full_array()[numnum]);
					}
				}
				else if (result == 1){
					if(display){
						this.insert_spaces_for_iteration("B");
						System.out.println("deleting base ("+current_node+") because it was beat out by: "+nodes_to_consider.get_full_array()[numnum]);
					}


					current_node = nodes_to_consider.get_full_array()[numnum];
					nodes_to_consider.delete(nodes_to_consider.get_full_array()[numnum]);
					numnum = 0;

					Bochert_neighbor(memory_element, current_node, dont_consider_connected, nodes_to_consider,internal_connected);
					Bochert_neighbor(temp_element_P, current_node, dont_consider_connected, nodes_to_consider, internal_not_connected);
					if(nodes_to_consider.get_length() > temp_element_P.get_length()){
						dont_consider_connected = memory_element.copy();
						nodes_to_consider = temp_element_P.copy();

						for(int k = 0; k<run_me.get_length(); k++){
							if(graph[run_me.get_full_array()[k]-1][current_node-1]==0){
								nodes_to_consider.add(run_me.get_full_array()[k]);
							}
							else{
								dont_consider_connected.add(run_me.get_full_array()[k]);
							}

						}
						run_me.set_length(0);


						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println(" NEW ntc("+nodes_to_consider.get_length()+" NEW dcc("+dont_consider_connected.get_length()+" ntc is: "+nodes_to_consider.print_list());
						}
					}


					//another = true;
					//break;
				}
				else{
					//							System.out.println("nope, node "+nodes_to_consider.get_full_array()[numnum]+" has to stay in ntc");
					numnum++;
				}

			}


			run_me.add(current_node);

		}while (nodes_to_consider.get_length() > 0);





		//				if(!another)
		//					checked = this.already_checked_with_out_memory(checked_nodes, memory_element,temp_element_P);

		nodes_to_consider = run_me;

		node2 tempa = new node2(this.nodes);
		node2 tempb = new node2(this.nodes);


		if(nodes_to_consider.get_length() >= 2){

			Bochert_neighbor(memory_element, nodes_to_consider.get_full_array()[0], dont_consider_connected, nodes_to_consider,internal_connected);
			Bochert_neighbor(temp_element_P, nodes_to_consider.get_full_array()[1], dont_consider_connected, nodes_to_consider,internal_connected);							

			memory_element.similar_differences(temp_element_P, tempa, tempb);

			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println(nodes_to_consider.get_full_array()[0]+" base_uniq("+tempa.get_length()+"): "+tempa.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println(nodes_to_consider.get_full_array()[1]+" next_uniq("+tempb.get_length()+"): "+tempb.print_list());

				memory_element.delete(tempa);
				this.insert_spaces_for_iteration("B");
				System.out.println("same("+memory_element.get_length()+"): "+memory_element.print_list());
			}

		}




if(false){
		for(int i = 0; i<dont_consider_connected.get_length(); i++){

			Bochert_neighbor(memory_element, dont_consider_connected.get_full_array()[i], empty_node, dont_consider_connected,internal_connected);
			if (this.is_there_another(memory_element, empty_node, dont_consider_connected, temp_element_P, false)){
				for(int j = 0; j < temp_element_P.get_length(); j++){
					if((dont_consider_connected.get_full_array()[i] != temp_element_P.get_full_array()[j])&&(graph[dont_consider_connected.get_full_array()[i]-1][temp_element_P.get_full_array()[j]-1] == 0)){
						Bochert_neighbor(memory_element, temp_element_P.get_full_array()[j], empty_node, nodes_to_consider,internal_connected);

						for(int k = 0; k<memory_element.get_length(); k++){
							if(graph[memory_element.get_full_array()[k]-1][dont_consider_connected.get_full_array()[i]-1] == 1){

								if(display && B_iteration_deep <= display_level){
									this.insert_spaces_for_iteration("B");
									System.out.println("Just deleted from dcc: "+dont_consider_connected.get_full_array()[i]+" because it is equally connected to node: "+temp_element_P.get_full_array()[j]+" and from the nodes in ntc, they're both connected to: "+memory_element.get_full_array()[k]);
								}
								
								
								dont_consider_connected.delete(dont_consider_connected.get_full_array()[i]);
								i--;
								j = temp_element_P.get_length();
								k = memory_element.get_length();
								
								
								
							}

						}


					}

				}
			}



		}


}






		while(nodes_to_consider.get_length() != 0){
			//for(int i = 0; i<run_me.get_length(); i++){

			
			
			current_node = nodes_to_consider.get_full_array()[0];
			nodes_to_consider.delete(current_node);

			Bochert_neighbor(memory_element, current_node, dont_consider_connected, nodes_to_consider,internal_connected);

			/////////////////////////////////////////////////////////////////////////
			//                 RUN THIS NODE
			/////////////////////////////////////////////////////////////////////////

			
	if (true){
			for(int i = 0; i<memory_element.get_length(); i++){

				Bochert_neighbor(putty, memory_element.get_full_array()[i], empty_node, memory_element,internal_connected);

				if (this.is_there_another(putty, empty_node, memory_element, temp_element_P, false)){

//NOTE: They cannot be connected because you pass it the connected nodes and it cycles through all of them, and no node is connected to itself so the returned result cannot be a node that is connected to current node
//					for(int j = 0; j < temp_element_P.get_length(); j++){
//						if((memory_element.get_full_array()[i] != temp_element_P.get_full_array()[j])&&(graph[memory_element.get_full_array()[i]-1][temp_element_P.get_full_array()[j]-1] == 0)){

									if(display && B_iteration_deep <= display_level){
										this.insert_spaces_for_iteration("B");
										System.out.println("Just deleted from dcc: "+memory_element.get_full_array()[i]+" because it is equally connected to node: "+temp_element_P.print_list());
									}
									
									
									memory_element.delete(memory_element.get_full_array()[i]);
									i--;
//									j = temp_element_P.get_length();

//						}

//					}
				}



			}
	}
			

			////////////////////////////////////////////////////
			//new crazy check since ind set thing didn't work
			///////////////////////////////////////////////////
			if (((1+nodes_to_consider.get_length()+dont_consider_connected.get_length()) <= current_max)){
				if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println("returning in while, returning length: "+(node_that_found_max_star == -1?("none found so length: "+head_max_star.get_length()):("found a node so length: "+head_max_star.get_length()+1)));
				}
				
				
				if (node_that_found_max_star == -1){
					B_iteration_deep--;		
					return head_max_star;
				}
				head_max_star.add(node_that_found_max_star);
				B_iteration_deep--;
				return head_max_star;
			}




			temp_connected_nodes_N = memory_element.copy();
			Bochert_neighbor(temp_connected_star_N, current_node, empty_node, head_max_star, internal_connected);

			temp_connected_nodes_N.delete(temp_connected_star_N);

			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("INSIDE real function of "+current_node+" ntc ("+nodes_to_consider.get_length()+"): and dcc ("+dont_consider_connected.get_length()+" mem_elm ("+memory_element.get_length()+"): "+memory_element.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println("INSIDE real function of "+current_node+" tcn ("+temp_connected_nodes_N.get_length()+"): "+temp_connected_nodes_N.print_list());
				this.insert_spaces_for_iteration("B");
				System.out.println("INSIDE real function of "+current_node+" tcs ("+temp_connected_star_N.get_length()+"): "+temp_connected_star_N.print_list());
			}


			//					if(B_iteration_deep <= display_level){
			//					if(this.run_this_star(memory_element, current_node, super_star)){								
			//if(((display)&&(super_star[B_iteration_deep] == current_node))||(((B_iteration_deep == 0)&&(super_star[B_iteration_deep] == current_node)))){

			if((display)||(B_iteration_deep <= display_level)){
				System.out.print(">>");
				this.insert_spaces_for_iteration("B");
				System.out.println(current_node+" "+B_calls+" total connected: "+memory_element.get_length()+" ntc: "+nodes_to_consider.get_length()+" dc: "+dont_consider_connected.get_length()+" tcs: "+temp_connected_star_N.get_length()+" tcn: "+temp_connected_nodes_N.get_length()+" base_uniq: "+tempa.get_length()+" next_uniq: "+tempb.get_length());
			}
			//if(B_iteration_deep > 1)
			//	pause();

			/////////////////////////////////////////////////////////////
			//      pass it down the chain?
			////////////////////////////////////////////////////////////


			if ((temp_connected_nodes_N.get_length()+temp_connected_star_N.get_length())== 0 && temp_current_max == 0 && node_that_found_max_star == -1){
				node_that_found_max_star = current_node;
				temp_current_max = 1;
				//head_max_star = new node2(new int[0]);
			}
			else if (((temp_connected_nodes_N.get_length()+temp_connected_star_N.get_length()) >= temp_current_max) && ((temp_connected_nodes_N.get_length()+temp_connected_star_N.get_length()) != 0)) {

				if ((temp_connected_nodes_N.get_length()) == 1) {
					Bochert_neighbor(temp_element_P,temp_connected_nodes_N.get_full_array()[0], empty_node,temp_connected_star_N,internal_connected);
					temp_max = temp_element_P.copy();
					temp_max.add(temp_connected_nodes_N.get_full_array()[0]);
					
					if(display){
						this.insert_spaces_for_iteration("B");
						System.out.println("using tcs to reduce runs, result is temp_max length: "+temp_max.get_length()+" and is: "+temp_max.print_list());
					}
				}
				else{
					//if(display){
					if(display && (super_star[B_iteration_deep] == current_node)){
						System.out.println("** setting next display to true as well because current node: "+current_node);
						disp = true;
					}
					else
						disp = false;


					if (temp_current_max == 0){
						temp_max = New_Bochert(temp_connected_nodes_N, temp_current_max, temp_connected_star_N,disp);
					}
					else{
						temp_max = New_Bochert(temp_connected_nodes_N, temp_current_max-1, temp_connected_star_N, disp);
					}
					//}

				}

				//***********************************************************//
				//           check for max clique
				//***********************************************************//



				if ((temp_max != null) && (temp_max.get_length() >= temp_current_max)){

					head_max_star = temp_max;												


					node_that_found_max_star = current_node;

					temp_current_max = head_max_star.get_length()+1;


				}




			}


		}

		//nodes_to_consider.delete(current_node);




		//		}


		if((false)&&(super_star[B_iteration_deep] == current_node)){
			this.insert_spaces_for_iteration("B");
			System.out.println("returning from OUTSIDE the while loop");
			this.insert_spaces_for_iteration("B");
			System.out.println("ntc.l: "+nodes_to_consider.get_length()+" dcc.l: "+dont_consider_connected.get_length()+"hms.l"+head_max_star.get_length()+" ntfms: "+node_that_found_max_star);
		}



		if (node_that_found_max_star == -1){


			B_iteration_deep--;		
			return head_max_star;
		}


		//		int[] temp_finder = {node_that_found_max_star};
		//		node finder = new node2(temp_finder);

		head_max_star.add(node_that_found_max_star);



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


	private void Bochert_neighbor(node2 result, int n, node2 array, node2 node_array, int connection){


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

		/*
		int[] a = new int[0];
		int[] b = {0,1,6,7};
		node2 na = new node2(a);
		node2 nb = new node2(b);
		node2 uniqa = new node2(10);
		node2 uniqb = new node2(10);

		na.similar_differences(nb, uniqa, uniqb);

		System.out.println("uniqa: "+uniqa.print_list());
		System.out.println("uniqb: "+uniqb.print_list());

		g.pause();
		 */		

		for(int i = 0; i<s.length; i++){

			if ((i == 31) || (i == -32)){// && (i != 18) && (i != 19)){
				System.out.println("***********************************************************************************************************");
				System.out.println(i+" "+s[i]);
				g = new graph(s[i]);
				//g = new graph(testie4);

				if ((i > 31))
					g.display_level = 10;
				else
					g.display_level = 4;



				System.out.println("Number of nodes: "+g.nodes);


				System.out.println();
				System.out.println();
				System.out.println("AND NOW THE NEWER VERSION");
				g.start_showing_crap = false;

				start = System.currentTimeMillis();
				g.B_calls = 0;
				temp = g.pre_New_Bochert(false);

				elapsedTimeMillis = System.currentTimeMillis()-start;

				System.out.println("max clique from un-optimized Bochert is: ");
				System.out.println(g.array2string(temp));
				System.out.println("total calls to Bochert: "+g.B_calls);
				System.out.println("__ it took:"+elapsedTimeMillis+" miliseconds");

				System.out.println("is star?: "+g.is_star(temp, true)+" and length is: "+temp.length);


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
