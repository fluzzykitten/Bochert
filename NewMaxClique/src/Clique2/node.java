package Clique2;

//import OldClique.node;

public class node {

	int node;
	node length;
	node head;
	node next;
	node previous;
	node memory_next = null;
	node memory_previous = null;
	node memory_head = null;
	int meta_data = 0;
	//when head.meta_data=0 means "not a new star (at least not yet)"
	//when head.meta_data=1 means "new star"
	//when head.meta_data=-1 means that this is the memory_head and the length of all the memory and "previous" points to the first node in memory and "next" points to the last node 
	//



	public node() {
		node = -1;
		next = null;
		previous = null;
		head = this;

		length = new node(0);
		length.head = this;
		length.length = length;
		length.next = null;
		length.previous = null;

	}

	public node(int n){

		if (n == -1){
			node = -1;
			next = null;
			previous = null;
			head = this;
			memory_head = this;
			meta_data = -1;
			length = new node(0);

			length.head = this;
			length.length = length;
			length.next = null;
			length.previous = null;
			length.memory_head = this;

		}
		else {
			node = n;
			next = this;
			previous = this;
			length = null;
			head = null;
		}

	}

	public node(node nodes, node exempt){

		node = -1;
		next = null;
		previous = null;
		head = this;
		node previousnode = null;
		node currentnode = null;
		node endnode;

		length = new node(0);
		length.head = this;
		length.length = length;
		length.next = null;
		length.previous = null;

		if (nodes != null){		

			if (nodes.node == -1){
				nodes = nodes.next;
			}
			endnode = nodes;



			if ((exempt == null) || (nodes != exempt)){
				currentnode = new node(nodes.node);
				next = currentnode;
				currentnode.head = this;
				currentnode.length = length;
				length.node++;
				currentnode.memory_next = nodes.memory_next;
				//currentnode.meta_data = nodes.meta_data;
				previousnode = currentnode;						
			}
			else{
				nodes = nodes.next;

				//the only node in the attached nodes is the exempt node, so get out of this
				if (nodes == endnode)
					return;

					currentnode = new node(nodes.node);
					next = currentnode;
					currentnode.head = this;
					currentnode.length = length;
					length.node++;
					currentnode.memory_next = nodes.memory_next;
					//currentnode.meta_data = nodes.meta_data;
					previousnode = currentnode;						
				
			}
			
				nodes = nodes.next;
			
			while ((nodes != null) && (nodes != endnode)){
				if ((exempt == null) || (nodes != exempt)){
					currentnode = new node(nodes.node);
					previousnode.next = currentnode;
					currentnode.previous = previousnode;
					currentnode.head = this;
					currentnode.length = length;
					length.node++;
					currentnode.memory_next = nodes.memory_next;
					//currentnode.meta_data = nodes.meta_data;
					previousnode = currentnode;						
				}
				nodes = nodes.next;
			}
			next.previous = currentnode;
			currentnode.next = next;
		}
	}


	public node(int[] n){

		node = -1;
		next = null;
		previous = null;
		head = this;


		if ((n == null)|| (n.length == 0)){		
			length = new node(0);
			length.head = this;
			length.length = length;
			length.next = null;
			length.previous = null;
		}
		else{
			length = new node(n.length);
			length.head = this;
			length.length = length;
			length.next = null;
			length.previous = null;

			node currentnode = new node(n[0]);
			next = currentnode;
			currentnode.head = this;
			currentnode.length = length;
			node previousnode = currentnode;						

			for(int i = 1; i<n.length ; i++){
				currentnode = new node(n[i]);
				previousnode.next = currentnode;
				currentnode.previous = previousnode;
				currentnode.head = this;
				currentnode.length = length;
				previousnode = currentnode;						

			}

			next.previous = currentnode;
			currentnode.next = next;
		}

	}



	private void set_memory_next(node mem){
		memory_next = mem;
		mem.memory_head = memory_head;
	}

	public void set_meta_data(int n){
		meta_data = n;
	}

	public void add_to_imemory(int node, node neighbor_set, node all_neighbors){
		//add stuff
		//neighbor_set.meta_data lets you know if it's a new star or not
		//neighbor_set.memory_next is the pointer to the set in main memory that spanned this lil guy

		int[] anode = {node};
		node new_head = new node(anode);
		new_head.memory_head = this.memory_head;
		memory_head.length.node++;
		
		//add to the end of the list, update the previous last to have the new last after it
		if (memory_head.previous != null){
			//ie. if this isn't the first node
			memory_head.previous.memory_next = new_head;
		}
		else{
			memory_head.memory_next = new_head;
		}
		new_head.memory_head.previous = new_head;		

		//this might add an extra n to the "big O"...
		//this creates the list of "attached to" nodes
		new_head.previous = new node(all_neighbors, neighbor_set);

		//link the set in imem to the set in main mem
		new_head.previous.memory_next = neighbor_set.memory_next.head;
		//link the set in main mem to the set in imem
		neighbor_set.memory_next.head.previous = new_head;

		//if the reference to the set in the main memory is not the top of it's set, then this will be a "new" set
		if (neighbor_set.meta_data == 0)
			new_head.meta_data = 1;
		//otherwise the new set is not new... at least not yet
		else
			new_head.meta_data = 0;


	}

	public void imemory_append_to_set(int n, int meta, node all_neighbors){
		//add the node n to the end of the current set (node that "this" is probably not the head yet)
		//set the meta data for the new node to meta
		//check the list of current contained nodes against the list of head other contianed

		node top = this.head;
		node member = new node(n);
		
		top.next.previous.next = member;
		member.previous = top.next.previous;
		member.next = top.next;
		top.next.previous = member;

		member.length = length;
		member.head = top;
		top.length.node++;
		//if it's not a new star, then the status can change, but don't let it change back again
		if (top.meta_data == 0)
			top.meta_data = meta;

		node contained = top.previous;
		node neighbors = all_neighbors;
		boolean contained_start = true;
		boolean neighbors_start = true;

		//check the "contained" nodes vs the "neighbors" and resolve missing "neighbors"
		while (((neighbors_start)||(neighbors.next != all_neighbors.next)) && ((contained.next != null)&&((contained_start)||(contained.next != top.previous.next)))){
			if (contained.next.node == neighbors.next.node){
				//keep it
				contained = contained.next;
				neighbors = neighbors.next;
				neighbors_start = false;
				contained_start = false;
			}
			else if (contained.next.node > neighbors.next.node){
				//so it still might match one... let's check the next
				neighbors = neighbors.next;
				neighbors_start = false;
			}
			else{ //if (contained.next.node < neighbors.next.node){
				//so the node in contained isn't in the neighbors, so drop it...
				contained.delete_next();
			}
			//shouldn't need to increment anything, that should be taken care of in the if statements
		}

		while ((contained.next != null)&&((contained_start)||(contained.next != top.previous.next))){
			contained.delete_next();
		}
		


	}

	public void add_node_to_end_of_set_in_main_memory(int n, node[] mem_nodes){
		//this function is used during the "update" portion, 
		//it adds the new node value (n) to the end of the set in
		//main memory (cliques) to the end of the set that is currently being pointed to
		//by "this"

		node new_element = new node(n);

				
		head.next.previous.next = new_element;
		new_element.previous = head.next.previous;
		head.next.previous = new_element;
		new_element.next = head.next;
		new_element.head = head;
		new_element.length = length;
		length.node++;

		//update mem_nodes with the new elements in memory
		add_to_end_of_mem_nodes(mem_nodes[n-1],new_element,false);


	}

	public void move_memory(node set, int value, node[] mem_nodes){
		//this function is going to be used to move a totally new set from imemory to cliques
		//format is going to be cliques.move_memory(imemory.set)
		//note, I'm going to reuse the node, no reason to make a new one, right? It's just going to get erased anyway...

		boolean dont_do_me = false;
		if (set == null){			
			int [] temp = {value};
			set = new node(temp);
			dont_do_me = true;
		}

		node top = this.memory_head;

		//first one in memory
		if (top.memory_next == null){
			top.memory_next = set;
			top.previous = set;
		}
		else{
			top.previous.memory_next = set;
			top.previous = set;
		}

		top.length.node++;
		set.meta_data = top.length.node;
		set.previous = null;
		set.memory_next = null;
		set.memory_head = top;

		//update mem_nodes with the new elements in memory
		node index = set.next;
		add_to_end_of_mem_nodes(mem_nodes[index.node-1],index,true);

		index = index.next;

		while((index != set.next)&&(index != null)){
			add_to_end_of_mem_nodes(mem_nodes[index.node-1],index,false);
			index = index.next;
		}

		//don't forget to add the actual node being originally looked at lol
		if (!dont_do_me){
			set.add_node_to_end_of_set_in_main_memory(value, mem_nodes);
		}
	}

	private void add_to_end_of_mem_nodes(node node_head, node element, boolean top){

		node new_element = new node(element.head.meta_data);

		//first one
		if (node_head.next == null){
			node_head.next = new_element;		
			new_element.next = new_element;
			new_element.previous = new_element;
		}
		else{
			node_head.next.previous.next = new_element;
			new_element.previous = node_head.next.previous;
			node_head.next.previous = new_element;
			new_element.next = node_head.next;
		}
		new_element.head = node_head;
		new_element.memory_next = element;
		node_head.length.node++;

		//if it's the first in the set
		if (top){
			new_element.meta_data = 1;
		}



	}

	//public void add_to_set_in_memory(node set, int value){
	//this function is going to be used to add a new value to a set, the node passed to this was the existing set in imemory 
	//format is going to be cliques.add_to_set_in_memory(imemory.set, value)
	//System.out.println("add stuff, K?");

	//}

	public void add_memory(node mem){
		if (mem == null)
			return;

		if(memory_head == this){			
			previous.memory_next = mem;
			previous = mem;
			mem.memory_head = memory_head;
			length.node++;
		}
		else{
			memory_head.previous.memory_next = mem;
			memory_head.previous = mem;
			mem.memory_head = memory_head;
			memory_head.length.node++;
		}
	}

	public void erase_memory(){
		previous = null;
		next = null;
		memory_next = null;
		length.node = 0;
	}

	public node get_contained(){
		System.out.println("don't forget this code");
		return null;
	}

	public int get_meta_data(){
		return meta_data;
	}

	public node get_memory_next(){
		return memory_next;
	}

	public node get_memory_head(){
		return memory_head;
	}

	public void set_previous(node n){
		previous = n;
	}

	public void set_head(int new_head){
		head.node = new_head;
	}

	public node get_head(){
		return head;
	}

	public node get_next(){
		return next;
	}

	public int get_length(){
		if (length == null)
			return -1;
		return length.node;
	}

	public int get_value(){
		return node;
	}

	public node get_previous(){
		return previous;
	}

	public void print_memory(){
		System.out.println("Head: "+head.node+" Lenght: "+length.node+" Meta: "+meta_data+" and contents: "+print_list());

		node mem_next = memory_next;

		while (mem_next != null){
			System.out.println("mem_next: Head: "+mem_next.head.node+" Lenght: "+mem_next.length.node+" Meta: "+mem_next.meta_data+" and contents: "+mem_next.print_list());
			mem_next = mem_next.memory_next;			
		}

	}

	public String print_list(){

		if (length == null || head.next == null){
			return Integer.toString(node);			
		}

		else if (length.node == 0){
			return Integer.toString(node);
		}

		else{
			node currentnode = head.next;
			String list = Integer.toString(currentnode.node);			
			currentnode = currentnode.next;

			while (currentnode != next){
				list = list+" "+Integer.toString(currentnode.node);			
				currentnode = currentnode.next;				
			}

			return list;

		}

	}

	public int[] print_array(){

		int[] list;

		if (length == null){
			list = new int[1];
			list[0] = node;
			return list;			
		}

		else if (length.node == 0){
			list = new int[1];
			list[0] = node;
			return list;			
		}

		else{
			node currentnode = head.next;
			list = new int[length.node];			
			list[0] = currentnode.node;
			int index = 1;
			currentnode = currentnode.next;

			while (currentnode != next){
				list[index] = currentnode.node;			
				index++;
				currentnode = currentnode.next;				
			}

			return list;

		}

	}


	public boolean delete_next(){
		if (length == null || next == null)
			return false;

		if (length.node <= 1){
			if (head == this){
				next = null;
				previous = null;
				length.node = 0;
				return true;
			}
			else if (head == null)
				return false;
			else {
				return false;
			}
		}

		else {
			if (head == this){
				next.previous.next = next.next;
				next.next.previous = next.previous;
				next = next.next;
				length.node = length.node - 1;
			}
			else if (next == head.next){
				head.next.previous.next = head.next.next;
				head.next.next.previous = head.next.previous;
				head.next = head.next.next;
				length.node = length.node - 1;
			}
			else{
				next.next.previous = this;
				next = next.next;
				length.node = length.node -1;
			}

			return true;
		}

	}

	public boolean delete (int n){
		if (length == null || head == null)
			return false;

		node finder = head;
		for(int i = 0; i<length.node; i++){
			if (finder.next.node == n){
				finder.delete_next();
				return true;
			}
			finder = finder.next;
		}

		return false;

	}


	public boolean find (int n){
		if (length == null || head == null)
			return false;

		node finder = head;
		for(int i = 0; i<length.node; i++){
			if (finder.next.node == n){
				return true;
			}
			finder = finder.next;
		}

		return false;

	}


	public boolean delete (int[] n){ //this presupposes that both the array are in increasing order and the delete int[] is in increasing order
		if (length == null || head == null || n == null)
			return false;

		int index = 0;
		boolean all_found = true;
		node finder = head;
		int total_beginning_nodes = length.node;
		for(int i = 0; i<total_beginning_nodes; i++){

			while (n[index] < finder.next.node){
				all_found = false;
				index++;
				if (index >= n.length)
					return all_found;
			}

			if (finder.next.node == n[index]){
				finder.delete_next();
				index++;
				if (index >= n.length){
					return all_found;
				}
			}
			else
				finder = finder.next;
		}
		return false;

	}

	public node copy(){
		node temp;
		if (this.length.node == 0){
			temp = new node();
			temp.head.node = head.node;

		}
		else{
			temp = new node(this.print_array());
			temp.head.node = head.node;
		}
		return temp;

	}


	public node split_nodes(int[] split_int){ //remove out listed nodes into separate node list - assumes both are in increasing order


		node new_head = new node();
		node temp;

		if (split_int == null || split_int.length == 0)
			return new_head;

		int index = 0;
		node current = this;	
		node new_head_current = new_head;
		boolean found_first = false;
		boolean still_on_first = true;


		while (((index != split_int.length)&&((still_on_first)||(current.next!=next))&&(this.length.node!=0))){


			if (current.next.node == split_int[index]){
				temp = current.next;

				if(current.length.node != 1){
					current.next.next.previous = current.next.previous;
					current.next.previous.next = current.next.next;
					current = current.next.previous;
					current.length.node--;
					if(still_on_first)
						next = current.next;
				}
				else {
					current = null;
					this.next = null;
					this.length.node--;						
				}

				if (!found_first){
					new_head.next = temp;
					new_head_current = temp;
					new_head.length.node++;
					temp.length = new_head.length;
					temp.head = new_head;
					temp.next = temp;
					temp.previous = temp;
				}
				else{
					new_head_current.next = temp;
					new_head.length.node++;
					temp.length = new_head.length;
					temp.head = new_head;
					temp.next = new_head.next;//.previous;
					temp.previous = new_head_current;
					new_head.next.previous = temp;
					new_head_current = new_head_current.next;
				}

				index++;
				found_first = true;
			}

			else if (current.next.node < split_int[index]){
				current = current.next;
				still_on_first = false;
			}
			else
				index++;
		}



		return new_head;

	}


	public void add(node head){ // put two node lists together, orderly

		node this_current = this;
		node head_current = head;

		boolean this_start = true;
		boolean head_start = true;

		node temp;

		if (head.length.node == 0)
			return;
		if (length.node == 0){
			next = head.next;
			length.node = head.length.node;
			head.length.node = 0;

			temp = this;

			do{

				temp = temp.next;
				temp.head = this;
				temp.length = length;

			}while(next != temp.next);

		}


		while (((this.next != this_current.next)||(this_start))&&(head_current.length.node != 0)){

			if(head_current.next.node > this_current.next.node){
				this_current = this_current.next;
				this_start = false;
			}
			else if(head_current.next.node < this_current.next.node){
				temp = this_current.next;
				this_current.next = head_current.next;

				head_current.next.next.previous = head_current.next.previous;
				head_current.next.previous.next = head_current.next.next;
				head_current.next = head_current.next.next;
				head_current.length.node--;

				if(head_current.length.node == 0){
					head.next = null;
				}

				this_current.next.length = this_current.length;
				this_current.next.head = this_current.head;
				this_current.next.next = temp;
				this_current.next.previous = temp.previous;
				this_current.next.previous.next = this_current.next;
				temp.previous = this_current.next;
				this_current.length.node++;

			}
			else
				System.out.println("DANGER!! DANGER WILL ROBINSON!!!");
		}

		if (head_current.length.node != 0){

			this_current = next.previous;

			temp = head_current.next.previous;

			this_current.next = head_current.next;
			head_current.next.previous = this_current;
			next.previous = temp;
			temp.next = next;
			length.node+=head.length.node;
			head.length.node=0;

			temp = this_current;

			do{

				temp = temp.next;
				temp.head = this;
				temp.length = length;

			}while(next != temp.next);


		}
		//at this point, head_current.length.node should =0
		head.next = null;
		head.previous = null;
		head.length.node = 0;

	}


	public int[] combine(node head){ // put two node lists together, orderly, but don't change the nodes, make a list
		if (head == null)
			return this.print_array();

		node pointA = next, pointB = head.next;
		int combined_length = length.node + head.length.node;


		if (combined_length == 0){
			int[] result = {-1};
			return result;
		}

		//System.out.println("Combined_size is:"+combined_size+"and head:"+head.print_list());
		int[] result = new int[combined_length];
		int i = 0;
		int Alen = 0, Blen = 0;
		boolean Ainc = false, Binc = false;

		if (length.node == 0)
			Ainc = true;
		if (head.length.node == 0)
			Binc = true;

		while (i<combined_length){
			if (Ainc){
				for(;i<combined_length;i++){
					result[i] = pointB.node;
					pointB = pointB.next;
				}
			}
			else if (Binc){
				for(;i<combined_length;i++){
					result[i] = pointA.node;
					pointA = pointA.next;
				}
			}
			else if (pointA.node < pointB.node){
				result[i] = pointA.node;
				i++;

				pointA = pointA.next;	

				Alen++;
				if(Alen >= length.node)
					Ainc=true;
			}
			else{ //if (pointB.node < pointA.node){
				result[i] = pointB.node;
				i++;

				pointB = pointB.next;	

				Blen++;
				if(Blen >= head.length.node)
					Binc=true;
			}
		}

		return result;
	}


	public boolean contains(node head){

		if(head.length.node == 0){
			if (this.length.node == 0)
				return true;
			else
				return false;
		}


		node current_this = this.next;
		node current_head = head.next;

		boolean on_first_this = true;
		boolean on_first_head = true;

		//		System.out.println("in NODE.CONTAINS():");
		//		System.out.println("This: "+this.print_list());
		//		System.out.println("head: "+head.print_list());

		while(((current_this != this.next)||on_first_this)&&((current_head != head.next)||on_first_head)){
			if(current_this.node == current_head.node){
				//				System.out.println(current_this.node+" = "+current_head.node);
				current_this = current_this.next;
				current_head = current_head.next;
				on_first_this = false;
				on_first_head = false;
			}
			else if(current_this.node < current_head.node){
				//				System.out.println(current_this.node+" < "+current_head.node);
				current_this = current_this.next;
				on_first_this = false;
			}
			else
				return false;

		}	

		if (on_first_head||(current_head != head.next))
			return false;

		return true;

	}


}
