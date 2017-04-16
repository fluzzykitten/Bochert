package Clique;

//import OldClique.node;

public class node {

	public int[] array;
	public node next;
	public node end;
	
	node(int[] narray){
		array = narray;
	}
	
	node(int a, int[] narray){
		//a<<narray elements
		array = new int[narray.length+1];
		array[0] = a;
		System.arraycopy(array, 1, narray, 0, narray.length);
	}
	
	node(int num){
		array = new int[1];
		array[0] = num;
		
	}

	node(){
		array = null;
		next = null;
		end = null;
		
	}

	
}