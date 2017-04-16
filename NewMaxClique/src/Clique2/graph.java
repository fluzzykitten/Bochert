package Clique2;

import java.io.*;



public class graph {

	private int[][] graph; // the adjacency matrix
	private int nodes; // total number of nodes
	private int BK_iteration_deep = 0; // iterations deep into BronKerbosch
	private int B_iteration_deep = -1; //iterations deep into Bochert
	private long B_calls = 0; // calls to Bochert
	private int edges = 0; // total edges in graph
	private int initial_connected = 1; // used when functions are looking creating the initial matrix, for finding max independant set instead of max clique, this value is inverted
	private int initial_not_connected = 0; // used when functions are looking creating the initial matrix, for finding max independant set instead of max clique, this value is inverted
	private int internal_connected = 1; // used when functions are looking at internal connected ness
	private int[] node_edge_count; // number of edges each node has connected to it
	
	
	private node cliques;// = new node(-1);
	private node[] mem_nodes;// = new node[nodes];
	
	
	
	public int[] Bochert(){
		
		int[] neighbors = null;
		node imemory = new node(-1);
		node current_neighbor = null;
		node index_imemory = null;
		
		
		node temp = null;
		node index_temp = null;
		int[] temp_array = null;
		boolean initial_run = true;
		
		boolean display = false;
		
		cliques = new node(-1);
		mem_nodes = new node[nodes];
		
		for (int i = 0; i<mem_nodes.length; i++)
			mem_nodes[i] = new node();
		
		for (int i = 1; i<graph.length+1; i++){
//System.out.println("**START**");
			
			//initalize
			imemory.erase_memory();

			//get neighbors of i
			neighbors = Bochert_neighbor(i);
			
			System.out.println("looking at node: "+i+" mem size: "+cliques.get_length()+" connected nodes: "+array2string(neighbors));

/*************************************/
/*THIS IS THE START OF CHECK FUNCTION*/
/*************************************/

			if (i >= 42)
				display = true;
			else
				display = false;


			
			//for neighbors of i
			for(int n = 0; n < neighbors.length; n++){				
				
				//get list of current nodes in cliques
				current_neighbor = mem_nodes[neighbors[n]-1].get_next();

				if (display)
					System.out.println("Z - neighbor: "+n+" and memory currently has: "+cliques.get_length()+" and imemory has: "+imemory.get_length());

				if (display)
					pause();

				if(i == 40)
					System.out.println("neighbor: "+n+" imem: "+imemory.get_length());
				
				//check against current nodes in imemory
				//				index_imemory = imemory.get_memory_next();
				//				btw, current_neighbor should never start off being null, if it does, something is wrong - the previous nodes should always be in a max star, even if it's in a star all by itself
				while((current_neighbor != null)&&((initial_run)||(current_neighbor != mem_nodes[neighbors[n]-1].get_next()))){
					initial_run = false;
			
					if (display)
						System.out.println("A - Also connected to: "+current_neighbor.get_value());

					
					index_imemory = current_neighbor.get_memory_next().get_head().get_previous();
					
					//that is to say that this particular star in cliques hasn't made it's way into imemory yet
					if (index_imemory == null){
						if (display)
							System.out.println("A.1");
						
						//add the new set
						imemory.add_to_imemory(neighbors[n], current_neighbor, mem_nodes[neighbors[n]-1]);
					}
					//else index_imemory points to the current star in imemory
					else {
						//check to see if new node (n) is the next node in the set in imem
						//that is to say, if the actual node in the set in cliques (current_neighbor.get_memory_next()) has the same node before it in the set as what the current last node in the set in imemory is
						if (display)
							System.out.println("A.2");
						
						if (index_imemory.get_next().get_previous().get_value() == current_neighbor.get_memory_next().get_previous().get_value()){
							if (display)
								System.out.println("A.3");

							//add new node, but since it is the next in the set, node in imem isn't new
							index_imemory.imemory_append_to_set(neighbors[n],0,mem_nodes[neighbors[n-1]]);
						}
						else{ //new node in set skips a node in between
							if (display)
								System.out.println("A.4");

							//add the node, but bear in mind that it is a new set
							index_imemory.imemory_append_to_set(neighbors[n],1,mem_nodes[neighbors[n-1]]);
						}
					}
					if (display)
						System.out.println("A.5");

					current_neighbor = current_neighbor.get_next();
					
					
				}
				initial_run = true;
					
				
				
			}
			
			/*************************************/
			/*THIS IS THE START OF UPDATE FUNCTION*/
			/*************************************/

			if (display)
				System.out.println("B");

		//start on first set	
		index_imemory = imemory.get_memory_next();
				
		//updates "memory" off of "internal_memory"
		if(index_imemory == null){
		//this is the case that there isn't anything else it's attached to when it's looked at, for ex, this would also be the first node, because there aren't anything other sets out there yet	
			cliques.move_memory(null, i, mem_nodes);
		}
	

		//cycle through all the nodes in imemory and check if they should be added
		while(index_imemory != null){
			if (display)
				System.out.println("C");

			//this link back pointer is no longer necessary and this is a convenient spot to reset it
			index_imemory.get_previous().get_memory_next().set_previous(null);			
			
			//this is the current node, temp is used to cycle through the "contained in" sets
			temp = index_imemory.get_previous();
			


			
			//if this set is not contained anywhere else
			if ((temp.get_next() == null)){

				if (index_imemory.get_meta_data() == 1){
					//meaning this is a new set
					//I think at this point I can corrupt temp rather than making a new variable
					temp = index_imemory.get_memory_next();
					cliques.move_memory(index_imemory, i, mem_nodes);
					index_imemory = temp;
				}
				else{ //meaning that it isn't a new set and will need to be added to another set
					//index_imemory.get_previous().get_memory_next() should be pointing to the node in the main memory (cliques) to which to append this new node
					index_imemory.get_previous().get_memory_next().add_node_to_end_of_set_in_main_memory(i, mem_nodes);
					index_imemory = index_imemory.get_memory_next();
				}
			}
			//if it does have sets in the "contained in"
			else{
				//get the first one
				index_temp = temp.get_next();
				//cycle through all of them
				while(index_temp != null){
					if (display)
						System.out.println("D");

					if (index_temp.get_memory_next().get_length() > temp.get_length()){
						//it is contained in a set that already is bigger and thus preferred
						break;
					}
					else if (index_temp.get_memory_next().get_meta_data() == 0){
						//it is contained in a set that is not new
						//btw, it implies and assumes that there can only be one set that is "not new" in a mutually "contained in" set pair, never more than one, though it can be less than one
						break;
					}
					//look at the next one
					index_temp = index_temp.get_next();
				}
				if (index_temp == null){
					//that means all of the sets that it's "contained in" are equal or lesser size and none are "new"
					index_imemory.set_meta_data(0); //say it's not a new set... just so the others won't accidentally add the same set again...
					
					temp = index_imemory.get_memory_next();
					cliques.move_memory(index_imemory, i, mem_nodes);
					index_imemory = temp;
				}
				else
					index_imemory = index_imemory.get_memory_next();

				if (display)
					System.out.println("E");

			}
			
			if (display)
				System.out.println("F");


		}


		
		}
		
		System.out.println("END OF THE POOPY FUNCTION");
		cliques.print_memory();
		//find the max set by cycling through all sets
		index_imemory = cliques.get_memory_next();
		temp = cliques.get_memory_next();	
		while (index_imemory != null){
			if(index_imemory.get_length() > temp.get_length())
				temp = index_imemory;			
			index_imemory = index_imemory.get_memory_next();			
		}
		
		return temp.print_array();
	}
	
	private int[] Bochert_neighbor(int n){

		int [] temp = new int[nodes];
		int size = 0;

		for (int i = 0; i<nodes; i++){
			if (i+1 == n)
				break;
			else if (graph[n-1][i] == internal_connected){
				temp[size] = i+1;
				size++;
			}
		}

		int[] result = new int[size];
		System.arraycopy(temp, 0, result, 0, size);
		return result;

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

	


	public static void main(String args[]) throws Exception
	{
	
		System.out.println("not Hello?");

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
		//graph g = new graph(testie);
		
		
		//graph g = new graph(15,5);
		//	graph g = new graph("brock200_1.clq");
		  graph g = new graph("hamming6-2.clq"); // good small testing graph
		//	graph g = new graph("c-fat500-1.clq");
		//	graph g = new graph("keller6.clq");		int[] r = null, x = null, p = g.find_P();

		
			long start = System.currentTimeMillis();
			int [] temp = g.Bochert();
			long elapsedTimeMillis = System.currentTimeMillis()-start;
			
			System.out.println("max clique from optimized Bochert is: ");
			System.out.println(g.array2string(temp));
			System.out.println("total calls to Bochert: "+g.B_calls);
			System.out.println("__ it took:"+elapsedTimeMillis/1000+" seconds");
		  
	}

}
