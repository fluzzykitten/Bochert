package Clique;

//import OldClique.node;

public class node2 {

	int node;
	node2 memory_next = null;
	private int[] array;
	private int length;
	int meta_data = 0;// 1 == A, 2 == B
	char side;
	
	
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
		
	public node2 copy(){
		node2 result = new node2();
		result.node = node;
		result.memory_next = memory_next;
		result.length = length;
		result.array = new int[get_length()];
		System.arraycopy(array, 0, result.array, 0, get_length());
		
		return result;
	}

	public int get_last(){
		return array[get_length()-1];
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

	
	public boolean find(int n){
		
		if(length == 0){
			return false;
		}
		
		int index = 0;
//		int[] new_array = new int[get_length()+1];
		
		while(index < length){
			
			if(array[index] == n){
				return true;
			}
			else
				index++;
		}

		return false;
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

	
	public void delete_last(){
		length--;
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

	public int[] get_array_min_size(){
		
		int[] result = new int[length];
		
		System.arraycopy(array, 0, result, 0, length);
		
		return result;
	}

	
	
public boolean delete(int n){
			
		if(length == 0)
			return false;
		
		
		int index = 0;

		boolean found = false;
		
		while(((found)&&(index < (length)))||(index < (length-1))){
			
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
		else if (found){
			array[index-1] = array[index];
		}
		else{
			
		}
			
		
		
		return found;
		
	}



public void copy_array(node2 source){
	
	length = source.length;
	if (array.length < length)
		array = new int[length];
	
	for(int i = 0; i<length; i++){
		
		array[i] = source.array[i];
		
	}
	
	
	
	
}

public void similar_differences(node2 element, node2 memory_unique, node2 element_unique){
	//returns if there are similarities
	memory_unique.length = 0;
	element_unique.length = 0;
	
	if (length == 0){
		element_unique.copy_array(element);
		return;
	}
	
	if(element.length == 0){
		memory_unique.copy_array(this);
		return;
	}

	
	int memory_diff = 0;
	int element_diff = 0;

	int i_element = 0;
	int i_memory = 0;
	
	while((i_memory < get_length())&&(i_element < element.get_length())){//&&
//			((element.get_previous() != null)&&(((element_diff+element.get_previous().get_length()) <= previous.get_length()))||
//			((previous != null)&&((memory_diff+previous.get_length()) <= element.previous.get_length())))){			
		
		if(element.array[i_element] < array[i_memory]){
			element_unique.add_to_end(element.array[i_element]);
			i_element++;
		}
		else if(array[i_memory] < element.array[i_element]){
			memory_unique.add_to_end(array[i_memory]);
			i_memory++;
			
		}
		else if(array[i_memory] == element.array[i_element]){
			i_element++;
			i_memory++;
		}
	}		


while (i_memory < get_length()){
	memory_unique.add_to_end(array[i_memory]);
	i_memory++;	
}

while (i_element < element.get_length()){	
	element_unique.add_to_end(element.array[i_element]);
	i_element++;
}

}



}
