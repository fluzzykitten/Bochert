package Clique;

//import OldClique.node;

public class node2 {

	int node;
	node2 memory_previous = null;
	node2 memory_next = null;
	private int[] array;
	private int length;
	
	
	public node2() {
		node = 0;
		array = new int[0];
		length = 0;
		
	}

	public node2(int n) {
		node = 0;
		array = new int[n];
		length = 0;
		
	}

	public node2(int[] n){

		node = -1;
		array = n;
		length = n.length;

		if ((n == null)|| (n.length == 0)){		
			array = new int[0];
		}
	}
		
	public node2 copy_double_mem(){
		node2 result = new node2();
		result.node = node;
		result.memory_next = memory_next;
		result.length = length;
		result.memory_next = memory_next;
		result.memory_previous = memory_previous;
		
		result.array = new int[get_length()];		
		System.arraycopy(array, 0, result.array, 0, get_length());
		
		return result;
	}
	
	public void copy(node2 result){
		node= result.node;
		length = result.length;
		memory_next = result.memory_next;
		memory_previous = result.memory_previous;

		if(array.length < length)
			array = new int[length];
		
		System.arraycopy(result.array, 0, array, 0, get_length());
	}

	public int get_last(){
		return array[get_length()-1];
	}

	public int get_first(){
		return array[0];
	}
	
	public void incriment_length(){
		length++;
	}
	
	public void set_memory_next(node2 mem){
		memory_next = mem;
	}

	public node2 get_memory_next(){
		return memory_next;
	}

	public void set_memory_previous(node2 mem){
		memory_previous = mem;
	}

	public node2 get_memory_previous(){
		return memory_previous;
	}

	
	public int get_length(){
		return length;
	}

	
	public int[] get_full_array(){
		return array;
	}
	
	public void set_array(int[] arr){
		array = arr;
		length = arr.length;
	}

	public void set_length(int n){
		length = n;
		
	}
	
	public String print_list(){

			String out = "";

			if (array != null)
				for(int i = 0; i < get_length(); i++)
					out = out + array[i] + " ";


			return out;
		}

	public void add_to_end(int n){
		array[length] = n;
		length++;
	}

	private void add_size_increase(int n){
		
		int index = 0;
		int[] new_array = new int[get_length()+1];
		
		while(index < get_length()){
			
			if(array[index] < n){
				new_array[index] = array[index];
				index++;
			}
			else
				break;
		}

		new_array[index] = n;

		while(index < get_length()){
			
				new_array[index+1] = array[index];
				index++;
		}

		array = new_array;
		length = array.length;
		
		
	}

	
	public void add(int n){
		
		if(length == array.length){
			add_size_increase(n);
			return;
		}
		
		int index = length;
//		int[] new_array = new int[get_length()+1];
		
		while(index > 0){
			
			if(array[index-1] > n){
				array[index] = array[index-1];
				index--;
			}
			else
				break;
		}

		array[index] = n;
		length++;
		
	}

	public void negcheck(){
		if (length < 0)
			System.out.println(array[length]);
	}
	
	public void dupceck(){
		for(int i = 0; i< length -1; i++){
			if(array[i] == array[i+1]){
				System.out.println("duplicate of: "+array[i]);
				System.out.println(array[-1]);
			}
		}
	}
	
	private void add_size_increase(int[] n){
		
		int index = 0;
		int index_this = 0;
		int index_n = 0;
		int[] new_array = new int[get_length()+n.length];
		
		while((index_this < get_length())&&((index_n < n.length))){
			
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
	
		while(index_this < get_length()){
			
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
		length = array.length;
		
		
	}

	public void add(int[] n){

		
		if((length+n.length) > array.length){
			add_size_increase(n);
			return;
		}

		
		int index = length+n.length-1;
		int index_this = length-1;
		int index_n = n.length-1;
//		int[] new_array = new int[get_length()+n.length];
		
		while((index_this >= 0)&&((index_n >= 0))){
			
			if(array[index_this] > n[index_n]){
				array[index] = array[index_this];
				index_this--;
			}
			else{
				array[index] = n[index_n];
				index_n--;
			}
			index--;				
		}
	
/*		while(index_this >= 0){
			
				array[index] = array[index_this];
				index--;
				index_this--;
		}
	*/
		while(index_n >= 0){
			
			array[index] = n[index_n];
			index--;
			index_n--;
	}
	
		
		length = length+n.length;
		
	}

	
	private void add_size_increase(node2 n){
		
		int index = 0;
		int index_this = 0;
		int index_n = 0;
		int[] new_array = new int[get_length()+n.get_length()];
		
		while((index_this < get_length())&&((index_n < n.get_length()))){
			
			if(array[index_this] < n.array[index_n]){
				new_array[index] = array[index_this];
				index_this++;
			}
			else{
				new_array[index] = n.array[index_n];
				index_n++;
			}
			index++;				
		}
	
		while(index_this < get_length()){
			
				new_array[index] = array[index_this];
				index++;
				index_this++;
		}
	
		while(index_n < n.get_length()){
			
			new_array[index] = n.array[index_n];
			index++;
			index_n++;
	}
	
		
		array = new_array;
		length = array.length;
		
		
	}
	
	public void add(node2 n){

		
		if((length+n.get_length()) > array.length){
			add_size_increase(n);
			return;
		}

		
		int index = length+n.get_length()-1;
		int index_this = length-1;
		int index_n = n.get_length()-1;
//		int[] new_array = new int[get_length()+n.get_length()];
		
		while((index_this >= 0)&&((index_n >= 0))){
			
			if(array[index_this] > n.array[index_n]){
				array[index] = array[index_this];
				index_this--;
			}
			else{
				array[index] = n.array[index_n];
				index_n--;
			}
			index--;				
		}
	
/*		while(index_this >= 0){
			
				array[index] = array[index_this];
				index--;
				index_this--;
		}
	*/
		while(index_n >= 0){
			
			array[index] = n.array[index_n];
			index--;
			index_n--;
	}
	
		
		length = length+n.get_length();
		
	}

	
	public void delete_last(){
		length--;
		
		if(length<0)
			System.out.println(array[length]);
		
	}
	
	public boolean delete(node2 n){
		
		int index = 0;
		int index_this = 0;
		int index_n = 0;
		int previous_length = length;
//		int[] new_array = new int[this.get_length()+n.get_length()];
		boolean foundall = true;
		
		while((index_this < previous_length)&&((index_n < n.get_length()))){
			
			if(array[index_this] == n.array[index_n]){
				index_n++;
				index_this++;
				length--;				
			}
			else if (array[index_this] < n.array[index_n]){
				array[index] = array[index_this];
				index++;
				index_this++;
			}
			else if (array[index_this] > n.array[index_n]){
				index_n++;
				foundall = false;
			}
			
		}

		while(index_this < previous_length){
			array[index] = array[index_this];
			index++;
			index_this++;
		}
		
		if(index_n < n.get_length()){
			foundall = false;
		}

//		int[] new_new_array = new int[index];
//		System.arraycopy(new_array, 0, new_new_array, 0, index);
		
//		array = new_new_array;
//		length = array.length;
		
		return foundall;
		
	}

	
public boolean delete(int n){
		
		if(array.length == 0)
			return false;
		
		
		int index = 0;

		boolean found = false;
		
		while(((found)&&(index < (array.length)))||(index < (array.length-1))){
			
			if(array[index] == n){
				found = true;
				index++;
				length--;
			}
			else{
				if(!found);
					//new_array[index] = array[index];
				else
					array[index-1] = array[index];
				index++;
			}
					
		}
	
		if((!found)&&(array[index]==n)){
			length--;
			found = true;
		}
			
		
		
		return found;
		
	}
	
public void pull_out_intersection(node2 set, node2 star){
	
	length = 0;
	
	int index_set = 0;
	int index_star = 0;

	
	while((index_set < set.get_length())&&((index_star < star.get_length()))){
		
		if(set.array[index_set] == star.array[index_star]){
			array[length] = set.array[index_set];
			index_star++;
			index_set++;
			length++;				
		}
		else if (set.array[index_set] < star.array[index_star]){
			index_set++;
		}
		else if (set.array[index_set] > star.array[index_star]){
			index_star++;
		}
		
	}		
	

	
}


public void print_stack(){
	node2 index = this;
	int i = 0;
	
	while (index != null){
		System.out.println("iteration: "+i+" node: "+index.get_last());
		i++;
		index = index.get_memory_next();
		
	}
	
}

public int[] get_array_min_size(){
	
	int[] result = new int[length];
	
	System.arraycopy(array, 0, result, 0, length);
	
	return result;
}

}
