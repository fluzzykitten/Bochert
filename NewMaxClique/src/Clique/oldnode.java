package Clique;

public class oldnode {

	int node;
	int length = 1;
	oldnode next;
	oldnode previous;

	public oldnode() {
		node = -1;
		next = this;
		previous = this;
	}

	public oldnode(int n){
		node = n;
		next = this;
		previous = this;

	}

	public oldnode(int[] n){
		node = -1;
		next = this;
		previous = this;
		length = n.length+1;

		this.create_nodes(n);

	}

	public void set_length(int n){
		length = n;
	}

	public void set_next(oldnode n){
		next = n;
	}

	public void set_previous(oldnode n){
		previous = n;
	}

	public void set_value(int n){
		node = n;
	}

	public oldnode get_next(){
		return next;
	}

	public int get_length(){
		return length;
	}

	public int get_value(){
		return node;
	}

	public oldnode get_previous(){
		return previous;
	}

	public String print_list(){
		String list = "";
		oldnode temp = next;

		if (this == next)
			return Integer.toString(node);

		else{
			list = list + Integer.toString(temp.get_value())+" ";
			temp = next.get_next();

			while (temp != next){
				list = list + Integer.toString(temp.get_value())+" ";
				temp = temp.get_next();

			}
		}	

		return list;
	}

	public int[] print_array(){
		int[] list;



		if (length>1)
			list = new int[length-1];
		else
			list = new int[1];

		//System.out.println("entering print array on:"+this.print_list()+" and length is set to:"+list.length);


		int index = 0;

		oldnode temp = next;

		if (length == 0)
			return null;

		if (this == next){
			list[index] = node;
			index++;
		}
		else{
			//System.out.println("adding: "+temp.get_value()+" with index: "+index);
			list[index] = temp.get_value();
			index++;
			temp = next.get_next();

			while (temp != next){
				//System.out.println("adding: "+temp.get_value()+" with index: "+index);
				list[index] = temp.get_value();
				index++;
				temp = temp.get_next();

			}
		}	

		//		int[] out = new int[index];
		//		System.arraycopy(list, 0, out, 0, index);

		return list;
	}


	public boolean delete_next(boolean decriment){

		if (this == next){
			return false;
		}
		else if (next == previous){
			if (next.get_next()!=next){
				next.get_next().set_previous(next.get_previous());
				next.get_previous().set_next(next.get_next());
				next = next.get_next();
				previous = next;
				if (decriment)
					length--;
				return true;
			}
			else{
				next = this;
				previous = this;
				if (decriment)
					length--;
				return true;
			}}
		else {
			next.next.previous = this;
			//			node delete_this_var = next;
			next = next.next;
			//			delete_this_var.next = null;
			//			delete_this_var.previous = null;

			//			previous = next.get_next();
			if (decriment)
				length--;
			return true;
		}		

	}

	public oldnode create_nodes(int[] values){


		if ((values == null) || (values.length == 0))
			return null;

		node = -1;
		length = values.length+1;
		oldnode temp2, temp = new oldnode(values[0]);		
		next = temp;
		previous =temp;		

		for (int i = 1; i<values.length ; i++){
			temp2 = new oldnode(values[i]);
			temp.set_next(temp2);
			temp2.set_previous(temp);
			temp = temp2;
		}

		temp.set_next(next);
		next.set_previous(temp);

		return this;
	}
	
	public void verbose_list(){
		String list = "";
		oldnode temp = next;

		System.out.println("Node: "+node);
		System.out.println("Next: "+next.get_value());
		System.out.println("previous: "+previous.get_value());

		
		if (this == next){
			return;
		}
		else{

			System.out.println("Node: "+temp.node);
			System.out.println("Next: "+temp.next.get_value());
			System.out.println("previous: "+temp.previous.get_value());

			
			temp = next.get_next();
			
			while (temp != next){

				System.out.println("Node: "+temp.node);
				System.out.println("Next: "+temp.next.get_value());
				System.out.println("previous: "+temp.previous.get_value());


				temp = temp.get_next();

			}
		}	

	}

	public void verbose_test(){
		String list = "";
		oldnode center = next;
		oldnode back = next.previous;
		oldnode forward = next.next;
		int[] error = new int[1];
		int real_length = 1;
		
		if (this == next){
			return;
		}
		else{

			do{
				center = center.next;
				real_length++;
			}while(center != next);
			
			if (back.next != center || forward.previous != center || real_length != length){
				System.out.println("length: "+length+" actual length: "+real_length);
				this.verbose_list();
				System.out.println(error[-1]);
				}

			center = center.next;
			back = center.previous;
			forward = center.next;
			
			while (center != next){

				if (back.next != center || forward.previous != center){
					this.verbose_list();
					System.out.println(error[-1]);
					}

				center = center.next;
				back = center.previous;
				forward = center.next;

			}
		}	

	}

	
	public oldnode split_nodes(int[] split_int){ //remove out listed nodes into seperate node list
		oldnode split_head = new oldnode();
		oldnode temp_main_1=next, temp_main_2; 
		oldnode temp_split_head = split_head, temp_split_tail = split_head;
		int i = 0;


		if ((split_int == null) || (split_int.length == 0))
			return new oldnode();


		if (next.get_value() == split_int[i]){
			temp_main_2 = next.get_next();
			length--;
			split_head.length++;

			if (next.get_next() == next){
				next = this;
				previous = this;				
			}
			else {
				next = temp_main_2;
				previous = temp_main_2;
			}
			temp_main_1.get_next().set_previous(temp_main_1.get_previous());
			temp_main_1.get_previous().set_next(temp_main_1.get_next());

			split_head.set_next(temp_main_1);
			split_head.set_previous(temp_main_1);
			temp_split_head = temp_main_1;
			temp_split_tail = temp_main_1;
			temp_main_1.set_next(temp_main_1);
			temp_main_1.set_previous(temp_main_1);

			temp_main_1 = temp_main_2;
			i++;
		}
		else
			temp_main_1 = temp_main_1.get_next();

		for (; i<split_int.length; i++){
			do{
				if (temp_main_1.get_value() == split_int[i]){
					temp_main_2 = temp_main_1.get_next();
					length--;
					split_head.length++;
					if (temp_main_1==next){
						if (next.get_next() == next){
							next = this;
							previous = this;
						}
						else {
							next = temp_main_2;
							previous = temp_main_2;
						}
					}
					temp_main_1.get_next().set_previous(temp_main_1.get_previous());
					temp_main_1.get_previous().set_next(temp_main_1.get_next());
					if (temp_split_head.get_value()== -1){
						split_head.set_next(temp_main_1);
						split_head.set_previous(temp_main_1);
						temp_split_head = temp_main_1;
						temp_split_tail = temp_main_1;
						temp_main_1.set_next(temp_main_1);
						temp_main_1.set_previous(temp_main_1);
					}
					else{
						temp_split_head.set_next(temp_main_1);
						temp_split_tail.set_previous(temp_main_1);
						temp_main_1.set_next(temp_split_tail);
						temp_main_1.set_previous(temp_split_head);
						temp_split_head = temp_main_1;
					}
					temp_main_1 = temp_main_2;	
				}
				else
					temp_main_1 = temp_main_1.get_next();

				if (next == this){
					i = split_int.length;
					break;
				}


			} while ((temp_main_1 != next) && (temp_split_head.get_value() != split_int[i]));
		}

		return split_head;
	}

	public boolean insert_single_node_infront_of_this_node(oldnode head){
		if ((head.length == 1) && (head.node != -1)){
			
			head.next = this;
			head.previous = previous;
			previous.next = head;
			previous = head;
			length++;
			return true;
		}
		else
			return false;
	}
	
	public boolean add_unordered(oldnode head){
		if ((head.length == 1) && (head.node == -1)){
			return true;
		}
		else if (head.length == 1){
			if (head.node != -1){
				length = length + head.length;

				if (next == this){
					next = head;
					previous = head;
				}
				else{
					head.next = next;
					head.previous = next.previous;
					next.previous.next = head;
					next.previous = head;
				}
			}
		}
		else{
			length = length + head.length -1;		

			if (next == this){
				next = head.next;
				previous = head.next;
			}
			else{
				oldnode temp = head.next.previous;

				next.previous.next = head.next;
				head.next.previous.next = next;
				head.next.previous = next.previous;
				next.previous = temp;
			}
		}

		return true;
	}

	public boolean add(oldnode head){ // put two node lists together, orderly
		oldnode point = next;
		oldnode temp_next;
		length = length + head.length -1;

		//		System.out.println("This: "+this.print_list()+" length: "+length);
		//		System.out.println("head: "+head.print_list()+" length: "+head.length);


		if (this == next){
			next = head.next;
			previous = next;
			head.set_next(head);
			head.set_previous(head);
			return true;
		}


		if (head.get_next() != head && point == next){

			if (head.get_next().get_value() < point.get_value()){

				if (head.get_next() == head.get_next().get_next())
					temp_next = head;	
				else
					temp_next = head.get_next().get_next();
				//if (point == next){
				next = head.get_next();
				previous = head.get_previous();
				//}

				head.get_next().get_next().set_previous(head.get_next().get_previous());
				head.get_next().get_previous().set_next(head.get_next().get_next());

				next.set_previous(point.get_previous());
				next.set_next(point);

				point.get_previous().set_next(next);
				point.set_previous(next);

				head.set_next(temp_next);
				head.set_previous(temp_next);


			}
//			else{
//				if (next == next.next)
//					break;

//			}
		}
		point = point.get_next();
		
		while (head.get_next() != head && point != next){


			if (head.get_next().get_value() < point.get_value()){

				if (head.get_next() == head.get_next().get_next()){
					temp_next = head;	
				}
				else{
					temp_next = head.get_next().get_next();
				}


				head.get_next().get_next().set_previous(head.get_next().get_previous());
				head.get_next().get_previous().set_next(head.get_next().get_next());

				head.get_next().set_previous(point.get_previous());
				head.get_next().set_next(point);

				point.get_previous().set_next(head.get_next());
				point.set_previous(head.get_next());

				head.set_next(temp_next);
				head.set_previous(temp_next);
			}
			else{
				point = point.get_next();
			}
		}

		if (head != head.get_next()){
			point = next.previous;
			next.previous.next = head.next;
			next.previous = head.next.previous;
			next.previous.next = next;
			head.next.previous = point;
			
			head.next = head;
			head.previous = head;

		}




		return true;
	}

	public int[] combine(oldnode head){ //put the two lists together and get array of length combined size
		if (head == null)
			return this.print_array();

		oldnode pointA = next, pointB = head.next;
		int combined_length = length + head.length -2;


		if (combined_length == 0){
			int[] result = {-1};
			return result;
		}

		//System.out.println("Combined_size is:"+combined_size+"and head:"+head.print_list());
		int[] result = new int[combined_length];
		int i = 0;
		boolean Ainc = false, Binc = false;

		if (next.get_value() == -1)
			Ainc = true;
		if (head.next.get_value() == -1)
			Binc = true;

		while (i<combined_length){
			if (pointA.get_value() < pointB.get_value()){
				if ((pointA != next) || (!Ainc)){
					result[i] = pointA.get_value();
					i++;
					pointA = pointA.get_next();	
					Ainc = true;
				}
				else {
					result[i] = pointB.get_value();
					i++;
					pointB = pointB.get_next();
					Binc = true;
				}
			}
			else{
				if ((pointB != head.next) || (!Binc)){
					result[i] = pointB.get_value();
					i++;
					pointB = pointB.get_next();
					Binc = true;
				}
				else {
					result[i] = pointA.get_value();
					i++;
					pointA = pointA.get_next();	
					Ainc = true;
				}
			}
		}

		return result;
	}


	public boolean delete (int n){


		oldnode temp = this;


		if (temp.get_next().get_value() == n){
			temp.delete_next(false);
			length--;
			return true;
		}

		temp = temp.get_next();	

		if (temp.get_next().get_value() == n){
			temp.delete_next(false);
			length--;
			return true;
		}
		temp = temp.get_next();

		while (temp.get_next() != next){
			if (temp.get_next().get_value() == n){
				temp.delete_next(false);
				length--;
				return true;
			}
			temp = temp.get_next();
		}
		return false;

	}

	public boolean nodes_left(int n){
		oldnode temp = next.get_next();
		n--;

		while (n>0){
			if (temp == next)
				return false;
			temp = temp.get_next();
			n--;
		}
		return true;

	}

	public void swap_with_previous(){
		int temp = node;
		node = previous.node;
		previous.node = temp;

	}

	public int delete(oldnode head){


		if (head.next == head)
			return 0;

		int deleted = 0;
		oldnode temp;
		if (head.node == -1)
			temp = head.next;
		else
			temp = head;

		boolean return_val = true;
		boolean temp_return_val = true;

		do{
			temp_return_val = delete(temp.node);

			if (temp_return_val)
				deleted++;			

			temp = temp.next;

		}while (temp != head.next);


		return deleted;
	}


	public oldnode copy(){
		oldnode new_node = new oldnode();

		new_node.create_nodes(this.print_array());

		return new_node;

	}

}
