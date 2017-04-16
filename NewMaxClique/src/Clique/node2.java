package Clique;

//import OldClique.node;

public class node2 {

	int node;
	node2 previous;
	node2 memory_next = null;
	int[] array;
	int meta_data = 0;
//	int[] max_star = {};
	



	public node2() {
		node = 0;
		array = new int[0];
		previous = null;
		
	}

	public node2(int n){
		node = n;
		array = new int[0];
		previous = null;
		

	}

	public node2(int[] n){

		node = -1;
		previous = null;
		array = n;

		if ((n == null)|| (n.length == 0)){		
			array = new int[0];
		}
	}
		

	public void set_node(int n){
		node = n;
	}

	public void set_meta_data(int n){
		meta_data = n;
	}

	public int get_meta_data(){
		return meta_data;
	}

	
	public void decriment_node(){
		node--;
	}

	public void incriment_node(){
		node++;
	}
	
	
	public void set_memory_next(node2 mem){
		memory_next = mem;
	}

	public node2 get_memory_next(){
		return memory_next;
	}
	
	
	public int get_value(){
		return node;
	}

	public int get_length(){
		return array.length;
	}

	
	public node2 get_previous(){
		return previous;
	}
	
	public void set_previous(node2 prev){
		previous = prev;
	}
	
	public String print_list(){

			String out = "";

			if (array != null)
				for(int i = 0; i < array.length; i++)
					out = out + array[i] + " ";


			return out;
		}

	public int[] get_array(){
		return array;
	}

	public int[] print_array(){
		return array;
	}


	public void delete_first(){

		int[] array2 = new int[array.length - 1];
		System.arraycopy(array, 1, array2, 0, array.length-1);
		array = array2;
		
	}

/*	public boolean delete (int n){
		if (length == null || head == null)
			return false;

		node2 finder = head;
		for(int i = 0; i<length.node; i++){
			if (finder.next.node == n){
				finder.delete_next();
				return true;
			}
			finder = finder.next;
		}

		return false;

	}
*/
	
/*	public boolean find (int n){
		if (length == null || head == null)
			return false;

		node2 finder = head;
		for(int i = 0; i<length.node; i++){
			if(finder.next.node > n)
				return false;

			if (finder.next.node == n)
				return true;
			
			finder = finder.next;
		}

		return false;

	}
*/
	
/*	public boolean delete (int[] n){ //this presupposes that both the array are in increasing order and the delete int[] is in increasing order
		if (length == null || head == null || n == null)
			return false;

		int index = 0;
		boolean all_found = true;
		node2 finder = head;
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
*/
	public node2 copy(){

		int[] array2 = new int[array.length];
		
		System.arraycopy(array, 0, array2, 0, array.length);
		node2 temp = new node2(array2);
		temp.node = node;
		
		
		return temp;
	}

/*
	public node2 split_nodes(int[] split_int){ //remove out listed nodes into separate node list - assumes both are in increasing order


		node2 new_head = new node2();
		node2 temp;

		if (split_int == null || split_int.length == 0)
			return new_head;

		int index = 0;
		node2 current = this;	
		node2 new_head_current = new_head;
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
*/

	public void add(int n){
		
		int index = 0;
		int[] new_array = new int[array.length+1];
		
		while(index < array.length){
			
			if(array[index] < n){
				new_array[index] = array[index];
				index++;
			}
			else
				break;
		}

		new_array[index] = n;

		while(index < array.length){
			
				new_array[index+1] = array[index];
				index++;
		}

		array = new_array;
		
		
	}

	
	public boolean delete(int[] n){
		
		int index = 0;
		int index_this = 0;
		int index_n = 0;
		int[] new_array = new int[array.length+n.length];
		boolean foundall = true;
		
		while((index_this < array.length)&&((index_n < n.length))){
			
			if(array[index_this] == n[index_n]){
				index_n++;
				index_this++;
			}
			else if (array[index_this] < n[index_n]){
				new_array[index] = array[index_this];
				index++;
				index_this++;
			}
			else if (array[index_this] > n[index_n]){
				index_n++;
				foundall = false;
			}
			
		}

		while(index_this < array.length){
			new_array[index] = array[index_this];
			index++;
			index_this++;
		}
		
		if(index_n < n.length){
			foundall = false;
	}

		int[] new_new_array = new int[index];
		System.arraycopy(new_array, 0, new_new_array, 0, index);
		
		array = new_new_array;
		
		return foundall;
		
	}
	
	public void add(int[] n){
		
		int index = 0;
		int index_this = 0;
		int index_n = 0;
		int[] new_array = new int[array.length+n.length];
		
		while((index_this < array.length)&&((index_n < n.length))){
			
			if(array[index_this] < n[index_n]){
				new_array[index] = array[index_this];
				index_this++;
			}
			else{
				new_array[index] = n[index_n];
				index_n++;
			}
			index++;				
		}

		while(index_this < array.length){
			
				new_array[index] = array[index_this];
				index++;
				index_this++;
		}

		while(index_n < n.length){
			
			new_array[index] = n[index_n];
			index++;
			index_n++;
	}

		
		array = new_array;
		
		
	}

	
	public boolean delete(int n){
		
		int index = 0;
		int[] new_array = new int[array.length-1];
		boolean found = false;
		
		while(((found)&&(index < (array.length)))||(index < (array.length-1))){
			
			if(array[index] == n){
				found = true;
				index++;
			}
			else{
				if(!found)
					new_array[index] = array[index];
				else
					new_array[index-1] = array[index];
				index++;
			}
					
		}

		if((!found)&&(array[index]==n))
			found = true;
			
		
		if (found)
			array = new_array;
		
		return found;
		
	}

	
	/*
	public void add(node2 head){ // put two node lists together, orderly

		node2 this_current = this;
		node2 head_current = head;

		boolean this_start = true;
		boolean head_start = true;

		node2 temp;
		
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
*/

/*	public int[] combine(node2 head){ // put two node lists together, orderly, but don't change the nodes, make a list
		if (head == null)
			return this.print_array();

		node2 pointA = next, pointB = head.next;
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
*/

	public boolean contains(node2 head){
		
		if(head.array.length == 0){
			if (array.length == 0)
				return true;
			else
				return false;
		}
		

		int index_this = 0;
		int index_head = 0;
				
		while((index_this < array.length)&&(index_head < head.array.length)){
			if(array[index_this] == head.array[index_head]){
				index_this++;
				index_head++;
			}
			else if(array[index_this] < head.array[index_head]){
				index_this++;
			}
			else
				return false;
			
		}	
		
		if (index_head != head.array.length)
			return false;
		
		return true;
		
	}
	
	public void add_to_end(int n){
		//this presupposes that the nodes are already in order and that this node is larger than the others
		//this helps to cut a lot of the overhead

		int[] array2  = new int[array.length + 1];
		System.arraycopy(array, 0, array2, 0, array.length);
		array2[array2.length-1] = n;
		array = array2;
		
		
	}
	

	public void print_memory(){
		System.out.println("Printing Memory:");
		
		node2 index = this;
				
		while (index != null){
			System.out.println("Node: "+index.node+" length: "+index.array.length+" set: "+index.print_list());

			index = index.memory_next;
		}
		
	}

	public void print_memory2(){
		System.out.println("Printing Memory:");
		
		node2 index = this;
				
		while (index != null){
			System.out.print("Node: "+index.node);
			System.out.print(" length: "+index.array.length);
			if(index.previous != null)
				System.out.print(" set: "+index.previous.print_list());
			else
				System.out.print(" set: NULL");
			System.out.println(" | "+index.print_list());

			index = index.memory_next;
		}
		
	}

	public boolean similar_differences(node2 element, int[] memory_unique, int[] element_unique){
		//returns if there are similarities
		
		if (array.length == 0 || element.array.length == 0){
			memory_unique[0] = array.length;
			element_unique[0] = element.array.length;
			return false;
		}


		
		int memory_diff = 0;
		int element_diff = 0;
		boolean has_similarities = false;		


		int i_element = 0;
		int i_memory = 0;
		
		while((i_memory < array.length)&&(i_element < element.array.length)){//&&
//				((element.get_previous() != null)&&(((element_diff+element.get_previous().get_length()) <= previous.get_length()))||
//				((previous != null)&&((memory_diff+previous.get_length()) <= element.previous.get_length())))){			
			
			if(element.array[i_element] < array[i_memory]){
				i_element++;
				element_diff++;
			}
			else if(array[i_memory] < element.array[i_element]){
				i_memory++;
				memory_diff++;
			}
			else if(array[i_memory] == element.array[i_element]){
				i_memory++;
				i_element++;
				has_similarities = true;
			}
		}		


//		if((i_memory < array.length)&&
//				((previous != null)&&((memory_diff+previous.get_length()) <= element.previous.get_length()))){			
			memory_diff+= (array.length-i_memory);
//		}
		
//		if((i_element < element.array.length)&&
//				((element.get_previous() != null)&&(((element_diff+element.get_previous().get_length()) <= previous.get_length())))){			
			element_diff+= (element.array.length - i_element);
//		}
	

		memory_unique[0] = memory_diff;
		element_unique[0] = element_diff;
		return has_similarities;
	}
	
	public node2 print_memory_to_consider(){
		
		
		node2 head;// = new node2();
		node2 index_this = this;
		
		if ((memory_next == null)||(node == 0))
			return new node2();

		int[] new_array = new int[1];
		
		//find next node that should be considered
		while ((index_this.memory_next != null)&&(index_this.memory_next.meta_data == 1)){
			index_this = index_this.memory_next;
		}
		
		new_array[0] = index_this.memory_next.node;

		head = new node2(new_array);

		index_this = index_this.memory_next;

		while ((index_this.memory_next != null)&&(index_this.memory_next.meta_data == 1))
			index_this = index_this.memory_next;
		
		while (index_this.memory_next != null){
			//find next node that should be considered
			
			head.add(index_this.memory_next.node);
			
			index_this = index_this.memory_next;

			while ((index_this.memory_next != null)&&(index_this.memory_next.meta_data == 1))
				index_this = index_this.memory_next;
			

		}
		

		return head;
	}

	
}
