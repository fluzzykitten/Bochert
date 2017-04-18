package Clique;

public class node3 {

	int[] array;
	node3 memory_next = null;
	node3 memory_previous = null;
	int length = 0;
	int meta_data = 0;// 1 == not been run yet
	char side;
	public node3 alpha_next = null;
	public node3 alpha_previous = null;

	
	
	public node3(int[] input, int max_nodes) {
		//assuming ordered index 0 is smallest and index length is biggest
			
		length = input.length;
		int length_total = (max_nodes % 32 == 0?max_nodes/32:max_nodes/32+1);
		array = new int[length_total];
		
		int index_array = input.length-1;
		
		for(int i = length_total-1; i>=0; i--){
			
			for(int j = 31; j>= 0; j--){
				
				if(index_array < 0){
				//	i = -1;
				}
				else if((i*32+j)==(input[index_array]-1)){
					
					array[i]++;					
					index_array--;
				}
				else{
				}
				
				if((i >= 0)&&(j != 0))
					array[i] = array[i]<<1;
			}			
		}	
	}

	
	public node3(int[] input, int max_nodes, boolean graph) {
		//assuming ordered index 0 is smallest and index length is biggest
			
		length = input.length;
		int length_total = (max_nodes % 32 == 0?max_nodes/32:max_nodes/32+1);
		array = new int[length_total];
		
		int index_array = input.length-1;
		
		for(int i = length_total-1; i>=0; i--){
			
			for(int j = 31; j>= 0; j--){
				
				if((i*32+j)==index_array){
					if(input[index_array] == 1){
						array[i]++;					
					}
					index_array--;
				}
				
				if(j != 0)
					array[i] = array[i]<<1;
			}			
		}	
	}

	
	public node3(node2 input, int max_nodes) {
		//assuming ordered index 0 is smallest and index length is biggest
				
		length = input.get_length();
		int length_total = (max_nodes % 32 == 0?max_nodes/32:max_nodes/32+1);
		array = new int[length_total];
		
		int index_array = input.get_length()-1;
		
		for(int i = length_total-1; i>=0; i--){
			
			for(int j = 31; j>= 0; j--){
				
				if(index_array < 0){
				//	i = -1;
				}
				else if((i*32+j)==(input.get_full_array()[index_array]-1)){
					
					array[i]++;					
					index_array--;
				}
				else{
				}
				
				if((i >= 0)&&(j != 0))
					array[i] = array[i]<<1;
			}			
		}	
	}

	public node3(int max_nodes) {
		//assuming ordered index 0 is smallest and index length is biggest
				
		length = 0;
		int length_total = (max_nodes % 32 == 0?max_nodes/32:max_nodes/32+1);
		array = new int[length_total];

	}
	
	public node3(){
		array = new int[0];
	}

	private int find_new_length(){
		
		int result = 0;
		
		for(int i = array.length-1; i>= 0; i--){
			result = result + Integer.bitCount(array[i]);
		}

		return result;
	}
	

	public void use_me_and(node3 a, node3 b){
		//use the smaller size
		
		for(int i = array.length-1; i>=0; i--){
			array[i] = a.array[i]&b.array[i];
		}

		length = find_new_length();
		
	}

	public void use_me_and_not_first(node3 a, node3 b){
		//use the smaller size
		
		for(int i = array.length-1; i>=0; i--){
			array[i] = (~a.array[i])&b.array[i];
		}

		length = find_new_length();
		
	}

	
	public void use_me_or(node3 a, node3 b){
		//use the biggest
		
		
		for(int i = array.length-1; i>=0; i--){
			array[i] = a.array[i]|b.array[i];
		}

		length = find_new_length();

		
	}

	public void invert(){
		//use the biggest
		
		
		for(int i = array.length-1; i>=0; i--){
			array[i] = ~array[i];
		}

		length = find_new_length();

		
	}

	
	public node2 to_new_node2(){
		
		node2 result = new node2(array.length*32);
		int temp = 0;
		
		for(int i = 0; i<array.length; i++){
			temp = array[i];
			
			for(int j = 0; j< 32; j++){
				
				if((temp&1)==1){
					result.add_to_end(i*32+j+1);
				}
				temp = temp>>>1;
			}			
		}	

		return result;
		
	}

	public node2 to_old_node2(node2 result){

		if(result.get_full_array().length < length){
			result.zero_and_resize(length);
		}
			
		result.set_length(0);
		
		int temp = 0;
		int bit_count = 0;
		
		for(int i = 0; i<array.length; i++){
			
			temp = array[i];
			bit_count = Integer.bitCount(temp);
			
			for(int j = 0; j< 32; j++){
				
				if((temp&1)==1){
					result.add_to_end(i*32+j+1);
					bit_count--;
				}
				if(bit_count < 1)
					j=32;
				
				temp = temp>>>1;
			}			
		}	

		return result;
		
	}

	public String print_list(){

		String result = "";
		
		int temp = 0;
		int bit_count = 0;
		
		for(int i = 0; i<array.length; i++){
			
			temp = array[i];
			bit_count = Integer.bitCount(temp);
			
			for(int j = 0; j< 32; j++){
				
				if((temp&1)==1){
					result = result + " "+(i*32+j+1);
					bit_count--;
				}
				if(bit_count < 1)
					j=32;
				
				temp = temp>>>1;
			}			
		}	

		return result;
		
	}

	
	public int[] to_int(){
		
		int[] result = new int[length];
		int index = 0;
		
		int temp = 0;
		int bit_count;
		
		for(int i = 0; i<array.length; i++){
			temp = array[i];
			bit_count = Integer.bitCount(temp);
			
			for(int j = 0; j< 32; j++){
				
				if((temp&1)==1){
					result[index] = (i*32+j+1);
					index++;
					bit_count--;
				}
				if(bit_count < 1)
					j=32;
				
				temp = temp>>>1;
			}			
		}	
		
		return result;
	}
	
		public int[] to_int(int size){
			int[] result;
			if(size > length)
				result = new int[size];
			else			
				result = new int[length];
			int index = 0;
			
			int temp = 0;
			int bit_count;
			
			for(int i = 0; i<array.length; i++){
				temp = array[i];
				bit_count = Integer.bitCount(temp);
				
				for(int j = 0; j< 32; j++){
					
					if((temp&1)==1){
						result[index] = (i*32+j+1);
						index++;
						bit_count--;
					}
					if(bit_count < 1)
						j=32;
					
					temp = temp>>>1;
				}			
			}			
		
		
		return result;
	}
	
	public String print_literal(){
		
		
		String result = "";
		int current;
		
		for(int i = array.length-1; i>=0; i--){
			current = array[i];
			for(int j = 31; j>=0; j--){
				
				result = result + ((current>>>j)&1);
			}
		}
		
//			for(int i = array.length-1; i>=0; i--){
//				result = result + ":" + Integer.toBinaryString(array[i]);
//			}
		
		
		return result;
		
	}
	
	
	public int get_length(){
		return length;
	}
	
	public node3 copy_by_erasing(){
		node3 result = new node3();
		//result.memory_next = memory_next;
		//result.memory_previous = memory_previous;
		result.length = length;
		result.array = new int[array.length];
		System.arraycopy(array, 0, result.array, 0, array.length);
		//result.side = side;
		//result.meta_data = meta_data;
		
		return result;
	}


public void copy_array(node3 source){
	
	length = source.length;
//	meta_data = source.meta_data;
//	side = source.side;

	if (array.length < source.array.length)
		array = new int[source.array.length];
	
	System.arraycopy(source.array, 0, array, 0, array.length);
		
	}

public int pop_first(){
	
	int array_index = 0;
	
	while((array_index < array.length)&&(array[array_index] == 0)){
		array_index++;
	}
	if(array_index >= array.length)
		return -1;
	
	int shift = 0;
	int temp = array[array_index];

	while((temp&0x1) != 1){
		temp = temp>>>1;
		shift++;
	}
	
	temp--;
	length--;
	temp = temp<<shift;
	
	array[array_index] = temp;
		
	return(array_index*32+shift+1);
	
}

public boolean find(int n){

	if(n<0)
		return false;

	int array_index = (n-1)/32;
	int shift = (n-1)%32;
	
	int insert = (1<<shift);
	insert = array[array_index]&insert;

	if(insert!=0)
		return true;
	else
		return false;
}


public void add(int n){
	
	if(n<=0){
		System.out.println("in node3:add function, add was called to add a number <= 0, system set to halt");
		System.out.println(array[-1]);
	}
	
	int array_index = (n-1)/32;
	int shift = (n-1)%32;
	int bit_count = Integer.bitCount(array[array_index]);
	
	int insert = (1<<shift);
	array[array_index] = array[array_index]|insert;

	if(bit_count != Integer.bitCount(array[array_index]))
		length++;
	
}


public void delete(node3 del){
	
	for(int i = 0; i<array.length; i++){
		
		array[i] = array[i]&(~del.array[i]);
		
	}
	
	length = find_new_length();
	
}


public void delete(int n){
	
	int array_index = (n-1)/32;
	int shift = (n-1)%32;
	int bit_count = Integer.bitCount(array[array_index]);
	
	int insert = (1<<shift);
	insert =~insert;
	array[array_index] = array[array_index]&insert;

	if(bit_count != Integer.bitCount(array[array_index]))
		length--;
	
}


public void zero(){
	for(int i = 0; i<array.length; i++)
		array[i] = 0;
	length = 0;
}
	

public boolean set_equals(node3 o) {

	//System.out.println("in equals function");
			

		if(length != o.length)
			return false;
		
		for(int i = 0; i<array.length; i++){
			if(array[i] != o.array[i])
				return false;
		}
		return true;
	
}

public int get_index(int n){
	
	if((n>=length)||(n<0))
		return -1;
	
	
	int array_index = -1;
	
	while((array_index < (array.length))&&(n >= 0)){
		array_index++;
		n=n-Integer.bitCount(array[array_index]);
	}
	
	if(n >= 0)
		return -1;
	
	n=n+Integer.bitCount(array[array_index]);

	
	int shift = 0;
	int temp = array[array_index];

	while((shift < 32)&&(n >= 0)){
		if((temp&0x1) == 1){
			n--;
		}
		temp = temp>>>1;
		shift++;
	}

	shift--;
		
	if(n>0)
		return -1;
	
	return(array_index*32+shift+1);
	
	
}

public void similar_differences(node3 element, node3 memory_unique, node3 element_unique){
	
	element.invert();
	
	memory_unique.use_me_and(element, this);
	
	element.invert();
	this.invert();
	
	element_unique.use_me_and(element, this);
	
	this.invert();
	
}

public void set_memory_next(node3 next){
	memory_next = next;
}


//results_array[i].similar_differences(results_array[j], puttyA, puttyB, deleted_nodes);

public void similar_differences(node3 element, node3 results_array, node3 results_element, node3 deleted_nodes){

	this.similar_differences(element, results_array, results_element);
	
	deleted_nodes.invert();
	results_array.use_me_and(results_array, deleted_nodes);
	results_element.use_me_and(results_element, deleted_nodes);	
	deleted_nodes.invert();

	
	
}

}
