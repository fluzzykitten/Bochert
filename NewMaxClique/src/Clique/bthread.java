package Clique;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
//import java.util.*;

public class bthread implements Runnable  {


	private int[][] graph; // the adjacency matrix
	private node3[] graph3; // the adjacency matrix
	private int nodes; // total number of nodes
	private int BK_iteration_deep = 0; // iterations deep into BronKerbosch
	private int B_iteration_deep = -1; //iterations deep into Bochert
	private long B_calls = 0; // calls to Bochert
	boolean start_showing_crap = false;
	private int display_level = 0;
	private node3 empty_node;
	private boolean degressive_display = false;
	private node3[] reach_back;
	private node3 find;
	private boolean display = false;
	private long[] reach_back_B_calls;
	private int whoami;
	private node3[] previous_nodes;
	private int previously_known_max;
	
	static boolean fing_semaphore = false;
	static int thread_count = 1;
	static int thread_pool = 1;
	static int[] status;//0 means free, 1 means running
	semaphore semasema = null;
	semaphore stillrunning = null;



	bthread(node3[] reach_back1, int thread_pool1, node3[] graph31, int[][] graph1, node3 find1, boolean display1, int nodes1, long[] reach_back_B_calls1, int display_level1, node3 empty_node1, boolean degressive_display1, int whoami1, int[] status1, node3[] previous_nodes1, int previously_known_max1, semaphore semasema1, semaphore stillrunning1){
		reach_back = reach_back1;
		graph3 = graph31;
		find = find1;
		display = display1;
		nodes = nodes1;
		graph = graph1;
		reach_back_B_calls = reach_back_B_calls1;
		display_level = display_level1;
		empty_node = empty_node1;
		degressive_display = degressive_display1;
		whoami = whoami1;
		if(thread_pool1 > 0) thread_pool = thread_pool1;
		if(status1 != null) status = status1;
		previous_nodes = previous_nodes1;
		previously_known_max = previously_known_max1;
		if(semasema1 != null) semasema = semasema1;
		stillrunning = stillrunning1;
	}

	@Override
	public void run() {

		
		
		if(display)		System.out.println("++++++++++engtering run... whoami: "+whoami+" thread_count: "+thread_count);

		node3 result = Newer_Bochert(find,previously_known_max,nodes,display,null);

			reach_back[whoami] = result;
			reach_back_B_calls[0] += B_calls;

		if(display)		System.out.println("----------exiting run... whoami: "+whoami+" thread_count: "+thread_count);

		stillrunning.release();

	}


	public int available_thread() throws InterruptedException{
		int new_thread = 0;
		
		semasema.take();
		//if(nonzeros(status) != thread_count) {System.out.println("status nonzeros ("+nonzeros(status)+") != thread count ("+thread_count+")"); System.out.println(status[-1]);}
		if(fing_semaphore) {System.out.println("fing_semaphore not false at start"); System.out.println(status[-1]);}
		fing_semaphore = true;
		
		if((thread_count) < thread_pool){
			thread_count++;

			for(int i = 0; i<thread_pool; i++){
				if(status[i] == 0){//it's free
					new_thread = i;
					status[i] = 1;
					i = thread_pool;
				}
			}


		}

		if(display){
			this.insert_spaces_for_iteration("B");
			System.out.println("Available_thread, whoami: "+whoami+" returning: "+new_thread+" and thread count is: "+thread_count+" thread_pool: "+thread_pool);
		}

		//if(nonzeros(status) != thread_count) {System.out.println("status nonzeros ("+nonzeros(status)+") != thread count ("+thread_count+")"); System.out.println(status[-1]);}
		if(!fing_semaphore) {System.out.println("fing_semaphore not true at end"); System.out.println(status[-1]);}
		fing_semaphore = false;
		
		semasema.release();
		
		return new_thread;
	}


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

	
	private int nonzeros(int[] countme){
		int length = 0;
		
		for(int i = 0; i<countme.length; i++){
			if(countme[i]!=0)
				length++;
		}
		
		return length;
		
	}
	

	private void Bochert_neighbor(node3 result, int n, node3 array){

		result.use_me_and(graph3[n-1], array);


		return;

	}





	private void insert_spaces_for_iteration(String mode){
		if (mode == "B"){
			System.out.print(whoami);
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
					if(deletable(set.get_full_array()[i], all_nodes,null,false)){
						deleted = i;
						i = -1;
					}
				}
				else{
					if(multi_node){
						if(deletable(set.get_full_array()[i], all_nodes,null,false)){
							check_nodes.delete(set.get_full_array()[i]);
							set.delete(set.get_full_array()[i]);
							deleted--;
							i = -1;					
						}
					}
					else if(graph[i][deleted] == 1){//not connected so worth considering again
						if(deletable(set.get_full_array()[i], all_nodes,null,false)){
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

	private boolean deletable(int n, node3 all_nodes, node3 lost_nodes,boolean save){

		node3 connected = new node3(nodes);
		node3 test = new node3(nodes);

		connected.use_me_and(graph3[n-1], all_nodes);

		test.use_me_and_not_first(connected, all_nodes);//no need to check the nodes that it's connected to, they can't be connected to the same because they cannot be connected to themself		
		test.delete(n);//just in case, current implementation doesn't need this tho, later ones might
		int[] int_nodes = test.to_int();

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
		boolean I_was_deleted = false;
		Runnable task;
		Thread worker;
		Thread thread_index; 
		List<Thread> thread_ownership = new ArrayList<Thread>();
		List<semaphore> stillrunninglist = new ArrayList<semaphore>();
		semaphore stillrunning1;



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
					System.out.println(" NO  WHILE,  B_calls: "+B_calls+" toptop (which is: "+toptop+") connected to all other nodes (which are: "+result.print_list()+"), calling Bochert("+result.print_list()+" ,cm: "+(current_max==0?0:current_max-1)+" ,sm: "+(sought_max==0?0:sought_max-1)+" , abc: null; (ntc node was connected to all nodes)");
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
				if(display)
					System.out.println(" TOP WHILE  B_calls: "+B_calls+" starting on, check set is "+(1+0)+" out of "+(nodes_to_consider.get_length()+length_extra_alredy_been_checked)+" which is node "+DCC[0].meta_data+" with DCC of: "+DCC[0].print_list()+" with no comp_set but current max of: "+max_star.meta_data);
				else
					System.out.println(" TOP WHILE time: "+this.disp_time()+" B_calls: "+B_calls+" starting on, check set is "+(1+0)+" out of "+(nodes_to_consider.get_length()+length_extra_alredy_been_checked)+" which is node "+DCC[0].meta_data+" with DCC size of: "+DCC[0].get_length()+" with no comp_set but current max of: "+max_star.meta_data);
			}

//				Thread.sleep(10);

				try {
			temp = available_thread();
				} catch(InterruptedException e) {
				} 
			//			if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("temp = "+temp);
			}
			//			}

			if(temp == 0){//no new threads

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
			else{
				
				
				//				task = new bthread(reach_back, thread_pool, graph3, graph, memory_element.copy_by_erasing(), display, nodes, reach_back_B_calls, display_level, empty_node, degressive_display,temp,status,previous_nodes);
				stillrunning1 = new semaphore();
				try{stillrunning1.take();} catch(InterruptedException e){}

				task = new bthread(reach_back, -1, graph3, graph, DCC[0].copy_by_erasing(), false, nodes, reach_back_B_calls, -1, empty_node, degressive_display,temp,null,previous_nodes,(max_star.meta_data==0?0:max_star.meta_data-1),semasema,stillrunning1);
				worker = new Thread(task);
				worker.setName(String.valueOf(temp));

				previous_nodes[temp].zero();
				previous_nodes[temp].add(toptop);

				if(display){
					this.insert_spaces_for_iteration("B");
					System.out.println("!!!Make a new THREAD!!! Telling thread to find max clique in: "+memory_element.print_list()+" and prev_nodes is: "+previous_nodes[temp].print_list());
				}


				worker.start();

				thread_ownership.add(worker);
				stillrunninglist.add(stillrunning1);


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
				if(display)
					System.out.println(" MAIN WHILE  B_calls: "+B_calls+" starting on, check set is "+(1+check_set)+" out of "+(nodes_to_consider.get_length()+length_extra_alredy_been_checked)+" which is node "+alpha[check_set].meta_data+" with alpha of: "+alpha[check_set].print_list()+" with the comp_set "+comp_set+" which is node "+alpha[comp_set].meta_data+" with alpha of: "+alpha[comp_set].print_list()+" and common nodes are: "+memory_element.print_list()+" max_star: "+max_star.print_list()+" max_star.md: "+max_star.meta_data);
				else
					System.out.println(" MAIN WHILE time: "+this.disp_time()+" B_calls: "+B_calls+" starting on, check set is "+(1+check_set)+" out of "+(nodes_to_consider.get_length()+length_extra_alredy_been_checked)+" which is node "+alpha[check_set].meta_data+" with alpha length of: "+alpha[check_set].get_length()+" with the comp_set "+comp_set+" which is node "+alpha[comp_set].meta_data+" with alpha length of: "+alpha[comp_set].get_length()+" and common nodes length of: "+memory_element.get_length()+" max_star: "+max_star.print_list()+" max_star.md: "+max_star.meta_data);


				if(degressive_display){
					display_level = B_iteration_deep; 
				}

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
				temp = all_nodes_in_set.pop_first();

				lost_nodes.memory_next = new node3(nodes);
				lost_nodes.memory_next.memory_previous = lost_nodes;
				lost_nodes = lost_nodes.memory_next;
				Bochert_neighbor(temp_element, temp, memory_element);
				lost_nodes.use_me_and_not_first(temp_element, memory_element);


				nodes_to_consider.memory_next = new node3(nodes);
				nodes_to_consider.memory_next.memory_previous = nodes_to_consider; 
				nodes_to_consider = nodes_to_consider.memory_next;
				//nodes_to_consider.copy_array(current_alpha);
				Bochert_neighbor(temp_element,temp,all_nodes_in_set);
				nodes_to_consider.use_me_and_not_first(temp_element, all_nodes_in_set);
				nodes_to_consider.add(temp);

				if(temp_element.get_length()<lost_nodes.get_length()){//if it would be more benificial to just check all nodes, not just not connected nodes
					if(display){
						this.insert_spaces_for_iteration("B");
						System.out.println("chose to do exhaustive search because dcc: "+temp_element.get_length()+" and lost_nodes: "+lost_nodes.get_length());
					}
					lost_nodes.zero();
					nodes_to_consider.use_me_or(nodes_to_consider, temp_element);
					lost_nodes.meta_data = 1;
				}else{
					if(display){
						this.insert_spaces_for_iteration("B");
						System.out.println("chose to NOT do an exhaustive search because dcc: "+temp_element.get_length()+" and lost_nodes: "+lost_nodes.get_length()+" which were: "+lost_nodes.print_list());
					}

				}
				//lost_nodes_from_ntc

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




				deepness++;

				//if(B_calls > 100)
				//pause();

				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//                 START SUPER WHILE
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

				while(TOP_nodes_to_consider != nodes_to_consider){
					I_was_deleted = true;
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
							temp_element.use_me_or(memory_element, all_nodes_in_set);//is this true????!!!!
							I_was_deleted = deletable(nodes_to_consider.meta_data,temp_element,lost_nodes,true);
							if(display){
								this.insert_spaces_for_iteration("B");
								System.out.println("nodes_to_consider.meta_data: "+nodes_to_consider.meta_data+" was found unneeded?: "+I_was_deleted+" by node: "+(int)temp_element.side);
							}
						}while(I_was_deleted&&((nodes_to_consider.get_length()+lost_nodes.get_length()) > 0));
					}
					if(!I_was_deleted){// || (nodes_to_consider.get_length()+lost_nodes.get_length()) > 0){

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

						if(all_nodes_in_set.get_length() > 0){
							temp = all_nodes_in_set.get_index(0);//.pop_first();


							lost_nodes.memory_next = new node3(nodes);
							lost_nodes.memory_next.memory_previous = lost_nodes;
							lost_nodes = lost_nodes.memory_next;
							Bochert_neighbor(temp_element, temp, memory_element);
							lost_nodes.use_me_and_not_first(temp_element, memory_element);


							nodes_to_consider.memory_next = new node3(nodes);
							nodes_to_consider.memory_next.memory_previous = nodes_to_consider; 
							nodes_to_consider = nodes_to_consider.memory_next;
							Bochert_neighbor(temp_element,temp,all_nodes_in_set);
							nodes_to_consider.use_me_and_not_first(temp_element, all_nodes_in_set);
							nodes_to_consider.add(temp);


							if(temp_element.get_length()<lost_nodes.get_length()){//if it would be more benificial to just check all nodes, not just not connected nodes
								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println("connected to ntc.mp.md: "+nodes_to_consider.memory_previous.meta_data+" chose to do exhaustive search because dcc length: "+temp_element.get_length()+" and lost_nodes length: "+lost_nodes.get_length());
								}
								lost_nodes.zero();
								nodes_to_consider.use_me_or(nodes_to_consider, temp_element);
								lost_nodes.meta_data = 1;
								lost_nodes.memory_previous.meta_data = 1;
							}else{
								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println("connected to ntc.mp.md: "+nodes_to_consider.memory_previous.meta_data+" chose to NOT do an exhaustive search because dcc: "+temp_element.get_length()+" and lost_nodes: "+lost_nodes.get_length()+" which were: "+lost_nodes.print_list());
								}

							}



							comp_set_solution.memory_next = comp_set_solution.copy_by_erasing();//new node3(nodes);
							comp_set_solution.memory_next.memory_previous = comp_set_solution;
							comp_set_solution = comp_set_solution.memory_next;

							lost_nodes = lost_nodes.memory_previous;
							nodes_to_consider = nodes_to_consider.memory_previous;
							comp_set_solution = comp_set_solution.memory_previous;
						}


						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println("deepness: "+deepness+" considering node "+nodes_to_consider.meta_data+" common nodes of ("+memory_element.get_length()+"): "+memory_element.print_list()+" and ntc: "+nodes_to_consider.print_list()+" and alpha_index: "+alpha_index.print_list()+" and all_nodes_in_set: "+all_nodes_in_set.print_list()+" temp (next ntc.md): "+temp);
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
								System.out.println("ELIMINATED OUT  ms.md: "+max_star.meta_data+" > me.gl: "+memory_element.get_length()+" all_nodes: "+all_nodes_in_set.get_length()+" + deepness: "+deepness);
							}

							memory_element = memory_element.memory_previous;								
							alpha_index = alpha_index.memory_previous;
							all_nodes_in_set = all_nodes_in_set.memory_previous;



						}
						else{
							//System.out.println("ntc.md: "+nodes_to_consider.meta_data+"lost_nodes.meta_data == "+lost_nodes.meta_data);

							if(/*(memory_element.get_length()<all_nodes_in_set.get_length())||*/(((all_nodes_in_set.get_length() == 0)||(lost_nodes.meta_data == 1))&&((deepness > comp_set_solution.get_length())/*&&run_it_down(DCC[check_set].to_new_node2(), memory_element, deepness, check_set, display, TOP_nodes_to_consider, check_set,new node3())*/&&run_it_down(/*alpha_index*/DCC[comp_set].to_new_node2(), memory_element, deepness, check_set, display, TOP_nodes_to_consider, comp_set,comp_set_solution)))){

								//								just_a_pointer = ideal_comp(alpha_index, comp_set_solution, memory_element, deepness);


								just_a_pointer = new node3(alpha_index, nodes);
								//node2 prev_set2 = comp_set_solution.to_new_node2();//= new node2(nodes);
								temp_element.zero();

								/*							for(int i = 0; i<prev_set2.get_length(); i++){
							Bochert_neighbor(temp_element, prev_set2.get_full_array()[i], temp_element);
						}
						just_a_pointer = comp_set_solution;

						just_a_pointer.meta_data = just_a_pointer.get_length();
						while(just_a_pointer.meta_data < deepness){//find the last set where it was found
							all_nodes_in_set = all_nodes_in_set.memory_previous;
							nodes_to_consider = nodes_to_consider.memory_previous;
							memory_element = memory_element.memory_previous;
							comp_set_solution = comp_set_solution.memory_previous;
							alpha_index = alpha_index.memory_previous;
							lost_nodes = lost_nodes.memory_previous;
							deepness--;
							//just_a_pointer.meta_data++;
						}
								 */							

								//add back in unconsidered nodes, aka DCC2/1
								//Bochert_neighbor(temp_element, nodes_to_consider.meta_data, all_nodes_in_set);
								memory_element.use_me_or(memory_element, all_nodes_in_set);

								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println(">> B_calls: "+B_calls+" calling Bochert("+memory_element.print_list()+" ,cm: "+(max_star.meta_data-deepness<=1?0:max_star.meta_data-deepness-1)+"(aka: max_star is: "+max_star.print_list()+") ,sm: "+nodes+" , abc: "+(just_a_pointer == null?"null":just_a_pointer.print_list())+"; ");
								}

								//								just_a_pointer = Newer_Bochert(memory_element, (max_star.meta_data-deepness-1<=0?0:max_star.meta_data-deepness-1), nodes, display, just_a_pointer);

if((1+check_set)==(nodes_to_consider.get_length()+length_extra_alredy_been_checked)){
	temp = 0;//no need to start a new thread, this guy is done...
}
else{
									try {
										temp = available_thread();
											} catch(InterruptedException e) {
											} 
}
								//if((all_nodes.get_length() != 0)&&(B_iteration_deep < (display_level+1))){
								if(display){
									this.insert_spaces_for_iteration("B");
									System.out.println("temp = "+temp);
									//}
								}

								if(temp == 0){//no new threads




									just_a_pointer = Newer_Bochert(memory_element, (max_star.meta_data-deepness-1<=1?0:max_star.meta_data-deepness-1), nodes, display, null);//just_a_pointer);

									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println(">> returned with: "+just_a_pointer.print_list()+" FYI tho, just_a_pointer.get_length: "+just_a_pointer.get_length()+" deepness: "+deepness+" <?> max_star.md: "+max_star.meta_data);
									}


									if((just_a_pointer.get_length()+deepness)>=max_star.meta_data){
										if(display){
											this.insert_spaces_for_iteration("B");
											System.out.println("found new max star!! just_a_pointer: "+just_a_pointer.print_list()+" deepness: "+deepness+" previous max_star.md: "+max_star.meta_data+" max_star: "+max_star.print_list());
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
											System.out.println("just added alpha.md: "+alpha[check_set].meta_data+" so max_star is now: "+max_star.print_list());
										}
										if(!this.is_star(max_star.to_int(), true)){System.out.println("not star anymore :(");alpha[-1]=null;}
									}
									else{
									}

								}
								else{

									
									stillrunning1 = new semaphore();
									try{stillrunning1.take();} catch(InterruptedException e){}

									task = new bthread(reach_back, -1, graph3, graph, memory_element.copy_by_erasing(), false, nodes, reach_back_B_calls, -1, empty_node, degressive_display,temp,null,previous_nodes,(max_star.meta_data-deepness-1<=1?0:max_star.meta_data-deepness-1),semasema,stillrunning1);	
									worker = new Thread(task);
									worker.setName(String.valueOf(temp));

									previous_nodes[temp].zero();
									just_a_pointer = nodes_to_consider;
									while(just_a_pointer != TOP_nodes_to_consider){
										previous_nodes[temp].add(just_a_pointer.meta_data);
										just_a_pointer = just_a_pointer.memory_previous;
									}
									previous_nodes[temp].add(alpha[check_set].meta_data);

									if(display){
										this.insert_spaces_for_iteration("B");
										System.out.println("!!!Make a new THREAD!!! Telling thread to find max clique in: "+memory_element.print_list()+" and prev_nodes is: "+previous_nodes[temp].print_list());
									}


									worker.start();

									thread_ownership.add(worker);
									stillrunninglist.add(stillrunning1);
								}


								memory_element = memory_element.memory_previous;
								alpha_index = alpha_index.memory_previous;
								all_nodes_in_set = all_nodes_in_set.memory_previous;								




							}
							else{
								//go deeper

								if(all_nodes_in_set.get_length() > 0){
									temp = all_nodes_in_set.pop_first();//becuse it wasn't pulled off above
									lost_nodes = lost_nodes.memory_next;
									nodes_to_consider = nodes_to_consider.memory_next;
									comp_set_solution = comp_set_solution.memory_next;

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
						//System.out.println("going back");
						deepness--;
					}
				}

			}

			check_set++;


			//				for (Thread thread : thread_ownership) {
			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("Checking threads for finished threads");
			}

			for (int i = 0; i< thread_ownership.size(); i++) {

				thread_index = thread_ownership.get(i); 


				if(!thread_index.isAlive()){

					temp = Integer.parseInt(thread_index.getName());		

					thread_ownership.remove(thread_index);
					stillrunninglist.remove(stillrunninglist.get(i));
					i--;//because there is one less now


					just_a_pointer = reach_back[temp];

					if(display){
						this.insert_spaces_for_iteration("B");
						System.out.print("!!! "+thread_index.getName()+" is no longer alive, and it returned: "+just_a_pointer.print_list()+" with prev_nodes of: "+previous_nodes[temp].print_list());
						System.out.println(" Did it return a star? "+this.is_star(just_a_pointer.to_int(), true));
					}



					if((just_a_pointer.get_length()+previous_nodes[temp].get_length())>max_star.meta_data){
						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println("found new max star!! just_a_pointer: "+just_a_pointer.print_list()+" previous_nodes: "+previous_nodes[temp].print_list()+" previous max_star.md: "+max_star.meta_data);
						}
						max_star.copy_array(just_a_pointer);

						max_star.use_me_or(max_star, previous_nodes[temp]);

						max_star.meta_data = max_star.get_length();

						if(!this.is_star(max_star.to_int(), true)){System.out.println("not star anymore :(");alpha[-1]=null;}
					}
					else{
					}


					status[temp] = 0;
					thread_count--;


				}
			}



		}


			if(display){
				this.insert_spaces_for_iteration("B");
				System.out.println("Checking threads for finished threads");
			}

			for (int i = 0; i< thread_ownership.size(); i++) {

				thread_index = thread_ownership.get(i); 


				if(thread_index.isAlive()){
					try{stillrunninglist.get(i).take();} catch(InterruptedException e) {}
				}

					temp = Integer.parseInt(thread_index.getName());		

					thread_ownership.remove(thread_index);
					stillrunninglist.remove(stillrunninglist.get(i));

					i--;//because there is one less now

					just_a_pointer = reach_back[temp];

										
					
					if(display){
						this.insert_spaces_for_iteration("B");
						System.out.println("!!! "+thread_index.getName()+" is no longer alive, and it returned: "+just_a_pointer.print_list()+" with prev_nodes of: "+previous_nodes[temp].print_list());
						System.out.println(" Did it return a star? "+this.is_star(just_a_pointer.to_int(), true));
					}

					if(!this.is_star(just_a_pointer.to_int(), true)){System.out.println("not star anymore :(");alpha[-1]=null;}
					
					
					temp_element.use_me_or(just_a_pointer, previous_nodes[temp]);
					if(!this.is_star(temp_element.to_int(), true)){System.out.println("not star anymore :(");alpha[-1]=null;}


					if((just_a_pointer.get_length()+previous_nodes[temp].get_length())>max_star.meta_data){
						if(display){
							this.insert_spaces_for_iteration("B");
							System.out.println("found new max star!! just_a_pointer: "+just_a_pointer.print_list()+" previous_nodes: "+previous_nodes[temp].print_list()+" previous max_star.md: "+max_star.meta_data);
						}
						max_star.copy_array(just_a_pointer);

						max_star.use_me_or(max_star, previous_nodes[temp]);

						max_star.meta_data = max_star.get_length();

						if(!this.is_star(max_star.to_int(), true)){System.out.println("not star anymore :(");alpha[-1]=null;}
					}
					else{
					}


					status[temp] = 0;
					thread_count--;

			}


		if(display){
			this.insert_spaces_for_iteration("B");
			System.out.println("Returning: "+max_star.print_list());
		}

		B_iteration_deep--;
		return max_star;

	}


}
