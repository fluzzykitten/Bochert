

		    bool delete(__global int* array, __global int* array_length, int n){
if(array_length[0] == 0)
	return false;+
int index = 0;
bool found = false;
while(((found)&&(index < (array_length[0])))||(index < (array_length[0]-1))){
if(array[index] == n){
		found = true;
		index++;
		array_length[0]--;
	}
	else{
		if(found)
			array[index-1] = array[index];
		index++;
	}			
}	
if((!found)&&(array[index]==n)){
	array_length[0]--;
	found = true;
}
else if (found){
	array[index-1] = array[index];
}
return found;
	}


		
		void Bochert_neighbor(
		__global int * results, 
		int results_start, 
		__global int * results_length, 
		int results_length_index, 
		__global int * nodes, 
		__global int* graph, 
		int n, 
		__global int* array, 
		int array_start, 
		int array_length){
 		
		results_length[results_length_index] = 0;
    	
		if (array_length == 0){			
			return;
		}
		
		int nodes_index = array_start;
		
		while((nodes_index-array_start) < array_length){
		
				if (((n-1) != (array[nodes_index ]-1)) && (graph[(n-1)*nodes[0]+(array[nodes_index]-1)] == 1)){
					results[results_start+results_length[results_length_index]] = array[nodes_index];
					results_length[results_length_index]++;
				}
				nodes_index++;
		
		}
		
		return;
		
		}
   	    
/******/__kernel void is_there_another(
		__global int* check_set, 
		__global int* check_set_length, 
		__global int* results, 
		__global int* results_length, 
		__global int* nodes_to_consider, 
		__global int * nodes_to_consider_length,
		__global int* nodes, 
		__global int* graph, 
		__global int * synch){
		
		int gid = get_global_id(0);
		check_set_length[gid] = 0;
    	
		Bochert_neighbor(check_set, gid*nodes[0], check_set_length, gid, nodes, graph, nodes_to_consider[gid], nodes_to_consider, 0, nodes_to_consider_length[0]);
		
		if(check_set_length[gid] == 0)
			return;
		
		Bochert_neighbor(results,gid*nodes[0], results_length, gid, nodes, graph, check_set[gid*nodes[0]+check_set_length[gid]-1], nodes_to_consider, 0, nodes_to_consider_length[0]);
		
		if(results_length[gid] <= 1)
			return;
		
		for(int i = check_set_length[gid]-2; i>=0; i--){
			Bochert_neighbor(results,gid*nodes[0], results_length, gid, nodes, graph, check_set[gid*nodes[0]+i], results, nodes[0]*gid, results_length[gid]);
			
			if(results_length[gid] <= 1){
				return;
			}
		
		}
		
			return;
		}
		
/******/__kernel void is_there_another_self_contained(
		__global int* check_set, 
		__global int* check_set_length, 
		__global int* results, 
		__global int* results_length, 
		__global int* nodes_to_consider, 
		__global int * nodes_to_consider_length,
		__global int* nodes, 
		__global int* graph, 
		__global int * synch){
		
		int gid = get_global_id(0);
		check_set_length[gid] = 0;
		synch[gid] = 0;
    	
		Bochert_neighbor(check_set, gid*nodes[0], check_set_length, gid, nodes, graph, nodes_to_consider[gid], nodes_to_consider, 0, nodes_to_consider_length[0]);
		
		if(check_set_length[gid] == 0)
			return;
		
		Bochert_neighbor(results,gid*nodes[0], results_length, gid, nodes, graph, check_set[gid*nodes[0]+check_set_length[gid]-1], nodes_to_consider, 0, nodes_to_consider_length[0]);
		
		if(results_length[gid] <= 1)
			return;
		
		for(int i = check_set_length[gid]-2; i>=0; i--){
			Bochert_neighbor(results,gid*nodes[0], results_length, gid, nodes, graph, check_set[gid*nodes[0]+i], results, nodes[0]*gid, results_length[gid]);
			
			if(results_length[gid] <= 1){
				return;
			}
		
		}
		
		synch[0] = 1;
		
			return;
		}
		
   	    ;
