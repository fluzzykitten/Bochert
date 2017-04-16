package OldClique;

public class node_int_array {

	private int[] array = null;
	private node_int_array next = this;
	private node_int_array previous = this;
	private node node = null;

	
	public node_int_array(){
		array = null;
		next = this;
		previous = this;
	}

	public node_int_array(int size){
		array = new int[size];
		next = this;
		previous = this;
	}

	public node_int_array(int a, node n){
		array = new int[1];
		array[0] = a;
		node = n;
		next = this;
		previous = this;
	}

	
	public node_int_array(int[] n){
		set_array(n);
		next = this;
		previous = this;
	}

	
	public void set_node(node n){
		node = n;
	}

	public node get_node(){
		return node;
	}
	
	public void set_array(int[] new_array){
		array = new int[new_array.length];
		System.arraycopy(new_array, 0, array, 0, new_array.length);		
	}

	public void set_array(int new_array){
		array = new int[1];
		array[0] = new_array;
	}

	public int[] get_array(){
		return array;
	}

	public void set_next(node_int_array new_next){
		next = new_next;
	}

	public void set_previous(node_int_array new_prev){
		previous = new_prev;
	}

	public void insert_next(node_int_array new_next){
		next.previous = new_next;
		new_next.next = next;
		new_next.previous = this;
		next = new_next;
	}

	public void insert_previous(node_int_array new_prev){
		previous.next = new_prev;
		new_prev.previous = previous;
		new_prev.next = this;
		previous = new_prev;
	}

	public void replace_and_make_current_next(node_int_array new_array_node){
		node_int_array temp = new node_int_array(array); //dethrone the current node
		temp.node = node;
		temp.next = next;
		temp.previous = this;
		next = temp;
		array = new_array_node.array;
		node = new_array_node.node;
	}

	public boolean delete_this(){
		if (next == this)
			return false;
		next.previous = previous;
		previous.next = next;
		return true;
	}
	
	public node_int_array get_next(){
		return next;
	}

	public node_int_array get_previous(){
		return previous;
	}

	
	public void add_in_order_of_length(int[] a, node n){ //largest first
		if (a == null)
			return;
		
		if (array == null){
			this.set_array(a);
			this.node = n;
		}		
		else if (array.length <= a.length){
			node_int_array temp = new node_int_array(array); //dethrone the current node
			temp.node = node;
			temp.next = next;
			temp.previous = this;
			next = temp;
			this.set_array(a);			
			this.node = n;
		}
		else if (next == this){
			node_int_array temp = new node_int_array(a);
			temp.set_node(n);
			this.insert_next(temp);
		}
		else if (array.length >= a.length){
			next.add_in_order_of_length(a, n);
		}
		
	}
}
