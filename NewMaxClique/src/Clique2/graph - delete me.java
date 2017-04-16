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
	
	
	private node memory = new node(-1);
	private node[] node_locations = new node[nodes];
	
	
	
	public int[] Bochert(){
		
		int[] neighbors = null;
		int index_neighbors = 0;
		node internal_memory = new node(-1);
		node index_internal_memory = internal_memory;
		node temp = null;
		int[] temp_array = null;
		
		for (int i = 0; i<node_locations.length; i++)
			node_locations[i] = new node(-1);
		
		for (int i = 1; i<graph.length+1; i++){

			System.out.println("memory is currently: ");
			memory.print_memory();
			
			neighbors = Bochert_neighbor(i);
			index_neighbors = 0;
			internal_memory.erase_memory();
			index_internal_memory = internal_memory;
			
			System.out.println("looking at node: "+i+" connected nodes: "+array2string(neighbors));
			
			
		//run through all the existing stars as part of "memory", identify new stars
		//creates "internal_memory" while "comparing memory" vs "neighbors"
		while (index_neighbors != neighbors.length){

			

			for(int j = 0; j < node_locations[neighbors[index_neighbors]].get_length(); j++){
				
				temp_array = new int[1];
				temp_array[0] = i;
				temp = new node(temp_array);
				temp.get_next().set_meta_data(1);				
				internal_memory.add_memory()
				
			}
			
			
			index_neighbors++;
		
		}
			
		//add new stars, and add current node to old stars that aren't new
		//updates "memory" off of "internal_memory"
		if(index_internal_memory.get_memory_next()== null){
		//this is the case that there isn't anything else it's attached to when it's looked at, for ex, this would also be the first node, because there aren't anything other sets out there yet	
			temp_array = new int[1];
			temp_array[0] = i;
			temp = new node(temp_array);
			temp.get_next().set_meta_data(1);
			memory.add_memory(temp);
			node_locations[i-1].add_memory(temp.next);
		}
		
		while(index_internal_memory.get_memory_next() != null){
		
			break;
		}
		
		}
		
		
		return null;
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
		graph g = new graph(testie);
		
		
		//graph g = new graph(15,5);
		//	graph g = new graph("brock200_1.clq");
		//  graph g = new graph("hamming6-2.clq"); // good small testing graph
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
