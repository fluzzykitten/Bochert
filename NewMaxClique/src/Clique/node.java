package Clique;

//import OldClique.node;

public class node {

	int node;
	node length;
	node head;
	node next;
	node previous;
	node memory_next = null;
//	int[] max_star = {};
	



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
		node = n;
		next = this;
		previous = this;
		length = null;
		head = null;

	}

	public node(int[] n){

		node = -1;
		next = null;
		previous = null;
		head = this;


		if (n == null){		
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

	public void set_memory_next(node mem){
		memory_next = mem;
	}

	public node get_memory_next(){
		return memory_next;
	}
	
//	public int[] get_max_star(){
//		return max_star;
//	}
	
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

	public String print_list(){

		if (length == null){
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

		return new node(this.print_array());

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
		
		node current_this = this.next;
		node current_head = head.next;
		
		boolean on_first_this = true;
		boolean on_first_head = true;
		
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
