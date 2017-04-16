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

	boolean start_showing_crap = false;
	private int[] current_best;
	private int node_augment = 0;



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




	public int[] pre_Bochert(){


		for(int i = nodes; i>0; i--){

			Bochert();
			node_augment++;

			pause();
		}
		return current_best;

	}


	private void Bochert(){


		int current = nodes;	
		int[] connected;
		node[] bestest = new node[nodes];
		int[] winner = null;
		node best_result;

		do{

			connected = Bochert_neighbor(current);
			bestest[current-1] = new node();//initialize the node
			
			System.out.println("current: "+current+" connected to: "+array2string(connected));

			if(connected != null){
				for(int i = 0; i<connected.length; i++){
					
					System.out.println("running compare on bestest["+(connected[i]-1)+"] and connected: "+array2string(connected));
					if(bestest[connected[i]-1].array == null)
						System.out.println("bestest["+(connected[i]-1)+"] is NULL");
					else
						System.out.println("bestest["+(connected[i]-1)+"] is: "+array2string(bestest[connected[i]-1].array));
					
					best_result = compare(bestest[connected[i]-1],connected,connected[i]-1);
					
					if (best_result == null){
						System.out.println("BochertA");
						System.out.println("Best_result: null");
						
						if (bestest[current-1].array == null){
							System.out.println("BochertA1");
							bestest[current-1].array = new int[1];
							bestest[current-1].array[0] = connected[i];
							
						}
						else if (bestest[current-1].array.length == 1){
							System.out.println("BochertA2");
							bestest[current-1].end.next = new node(connected[i]);							
							bestest[current-1].end = best_result.end.next;							
						}
					}
					else{
						System.out.println("BochertB");
						System.out.println("Best_result: "+array2string(best_result.array));
					
					if((bestest[current-1].array == null) || ((bestest[current-1].array.length-1) < best_result.array.length)){
						System.out.println("BochertB1");
						bestest[current-1] = best_result;
					}
					else if ((bestest[current-1].array != null) && ((bestest[current-1].array.length-1) == best_result.array.length)){
						System.out.println("BochertB2");
						bestest[current-1].end.next = best_result;
						bestest[current-1].end = best_result.end;
					}
					}
					System.out.println("at end, bestest["+(current-1)+"] is: "+array2string(bestest[current-1].array));

				}

			}





			current--;

		}while (current > 0);		


	}


	private node compare(node sets, int[] connected,int toptop){

		if (sets == null)
			System.out.println("poo");
		
		if (sets.array != null){
		System.out.print("Bestest set are: "+array2string(sets.array));
		}
		else
			System.out.print("Bestest set are: NULL");
		
		System.out.println(" and connected is: "+array2string(connected));

		if ((sets.array == null)||(connected == null))
			return null;
		
		
		node current_set = sets;
		int current_set_index = 0;
		int connected_index = 0;
		
		node running_result = null;
		node running_result_index = null;
		
		int[] running_set = null;
		int running_set_index = 0;
		
		while(current_set != null){
		System.out.println("A");

		running_set = new int[current_set.array.length];
		running_set_index = 0;
		current_set_index = 0;
		connected_index = 0;
		
			while((current_set_index < current_set.array.length) && (connected_index < connected.length)){
				System.out.println("B connected_set_index: "+current_set_index+" and connected_index: "+connected_index);
							
			if(current_set.array[current_set_index] == connected[connected_index]){
				System.out.println("C");
				running_set[running_set_index] = connected[connected_index];
				running_set_index++;
				current_set_index++;
				connected_index++;
			}
			else if(current_set.array[current_set_index] < connected[connected_index]){
				System.out.println("D");
				current_set_index++;
			}
			else{
				System.out.println("E");
				connected_index++;
			}
			
			
			}

			System.out.println("F");
			
			if((running_set != null) && (running_result == null)){
				System.out.println("G");
				running_result = new node();
				running_result.array = new int[running_set_index+1];
				running_result.next = null;
				running_result.end = running_result;
				
				running_result.array[0] = toptop;
				System.arraycopy(running_set, 0, running_result.array, 1, running_set_index);				
			}
			else if((running_set != null) && (running_result.array.length < running_set.length)){
				System.out.println("H");
				running_result.array = new int[running_set_index+1];
				running_result.next = null;
				running_result.end = running_result;
				
				running_result.array[0] = toptop;
				System.arraycopy(running_set, 0, running_result.array, 1, running_set_index);				
			}
			else if((running_set != null) && (running_result.array.length == running_set.length)){
				System.out.println("I");
				running_result.end.next = new node(); 
				running_result.end.next.array = new int[running_set_index+1];
				running_result.end.next.next = null;
				
				running_result.end.next.array[0] = toptop;
				System.arraycopy(running_set, 0, running_result.end.next.array, 1, running_set_index);				

				running_result.end = running_result.end.next;


			}


			current_set = current_set.next;
		}
		
		
		return running_result;
	}



	private int[] Bochert_neighbor(int n){

		int real_n;

		if(n<=node_augment)
			real_n = (n+nodes)-node_augment;
		else 
			real_n = n-node_augment;

		int [] temp = new int[nodes - n];
		int size = 0;
		int real_i = 0;

		for(int i = n; i<nodes+1; i++){

			if(i<=node_augment)
				real_i = (i+nodes)-node_augment;
			else 
				real_i = i-node_augment;

			//System.out.println("i: "+i+" real_i: "+real_i);

			if ((n != i) && (graph[real_n-1][real_i-1] == internal_connected)){
				temp[size] = i;
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

		int[][] testie={{0, 1, 1, 1, 0, 0},
				{1, 0, 1, 1, 0, 0},
				{1, 1, 0, 1, 1, 1},
				{1, 1, 1, 0, 0, 0},
				{0, 0, 0, 0, 0, 1},
				{0, 0, 0, 0, 1, 0}};




		graph g;

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
		s[34] = "";


		for(int i = 34; i<s.length; i++){

			if ((i != 18) && (i != 19) && (i != 21) && (i != 22)){

				System.out.println("***********************************************************************************************************");
				System.out.println(i+" "+s[i]);
				if (i != 34)
					g = new graph(s[i]);
				else
					g = new graph(testie);


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
