package Clique;

/*
 * JOCL - Java bindings for OpenCL
 * 
 * Copyright 2009 Marco Hutter - http://www.jocl.org/
 */

import static org.jocl.CL.*;

import org.jocl.*;

/**
 * A small JOCL sample.
 */
public class gpu
{
    /**
     * The source code of the OpenCL program to execute
     * 
     * 
int AddVector(int a, int b)
{
    return a + b;
}

kernel void VectorAdd(
    global read_only int* a,
    global read_only int* b,
    global write_only int* c )
{
    int index = get_global_id(0);
    //c[index] = a[index] + b[index];
    c[index] = AddVector(a[index], b[index]);
}

typedef struct Params
{
    float A;
    float B;
    int C;
} Params;

     */
    private static String programSource =
/******/"void Bochert_neighbor(" +
		"__global int * results, " +
		"int results_start, " +
		"__global int * results_length, " +
		"int results_length_index, " +
		"__global int * nodes, " +
		"__global int* graph, " +
		"int n, " +
		"__global int* array, " +
		"int array_start, " +
		"int array_length){" +
 		"" +
		"results_length[results_length_index] = 0;" +
    	"" +
		"if (array_length == 0){" +			
		"	return;" +
		"}" +
		"" +
		"int nodes_index = array_start;" +
		"" +
		"while((nodes_index-array_start) < array_length){" +
		"" +
		"		if (((n-1) != (array[nodes_index ]-1)) && (graph[(n-1)*nodes[0]+(array[nodes_index]-1)] == 1)){" +
		"			results[results_start+results_length[results_length_index]] = array[nodes_index];" +
		"			results_length[results_length_index]++;" +
		"		}" +
		"		nodes_index++;" +
		"" +
		"}" +
		"" +
		"return;" +
		"" +
		"}" +
   	    "" +
/******/"__kernel void is_there_another(" +
		"__global int* check_set, " +
		"__global int* check_set_length, " +
		"__global int* results, " +
		"__global int* results_length, " +
		"__global int* nodes, " +
		"__global int* graph, " +
		"__global int* nodes_to_consider, " +
		"__global int * nodes_to_consider_length){" +
		"" +
		"int gid = get_global_id(0);"+
		"check_set_length[gid] = 0;" +
    	"" +
		"Bochert_neighbor(check_set, gid*nodes[0], check_set_length, gid, nodes, graph, nodes_to_consider[gid], nodes_to_consider, 0, nodes_to_consider_length[0]);" +
		"" +
		"if(check_set_length[gid] == 0)" +
		"	return;" +
		"" +
		"Bochert_neighbor(results,gid*nodes[0], results_length, gid, nodes, graph, check_set[gid*nodes[0]+check_set_length[gid]-1], nodes_to_consider, 0, nodes_to_consider_length[0]);" +
		"" +
		"if(results_length[gid] <= 1)" +
		"	return;" +
		"" +
		"for(int i = check_set_length[gid]-2; i>=0; i--){" +
		"	Bochert_neighbor(results,gid*nodes[0], results_length, gid, nodes, graph, check_set[gid*nodes[0]+i], results, nodes[0]*gid, results_length[gid]);" +
		"	" +
		"	if(results_length[gid] <= 1){" +
		"		return;" +
		"	}" +
		"" +
		"}" +
		"" +
		"	return;" +
		"}" +
		"" +
/******/"__kernel void is_there_another_self_contained(" +
		"__global int* check_set, " +
		"__global int* check_set_length, " +
		"__global int* results, " +
		"__global int* results_length, " +
		"__global int* nodes, " +
		"__global int* graph, " +
		"__global int* nodes_to_consider, " +
		"__global int * nodes_to_consider_length){" +
		"" +
		"int gid = get_global_id(0);"+
		"check_set_length[gid] = 0;" +
    	"" +
		"Bochert_neighbor(check_set, gid*nodes[0], check_set_length, gid, nodes, graph, nodes_to_consider[gid], nodes_to_consider, 0, nodes_to_consider_length[0]);" +
		"" +
		"if(check_set_length[gid] == 0)" +
		"	return;" +
		"" +
		"Bochert_neighbor(results,gid*nodes[0], results_length, gid, nodes, graph, check_set[gid*nodes[0]+check_set_length[gid]-1], nodes_to_consider, 0, nodes_to_consider_length[0]);" +
		"" +
		"if(results_length[gid] <= 1)" +
		"	return;" +
		"" +
		"for(int i = check_set_length[gid]-2; i>=0; i--){" +
		"	Bochert_neighbor(results,gid*nodes[0], results_length, gid, nodes, graph, check_set[gid*nodes[0]+i], results, nodes[0]*gid, results_length[gid]);" +
		"	" +
		"	if(results_length[gid] <= 1){" +
		"		return;" +
		"	}" +
		"" +
		"}" +
		"" +
		"	return;" +
		"}" +
		"" +
   	    "";
/*   	    "int test(__global int *graph){" +
   	    "int gid = get_global_id(0);" +
   	    "return graph[gid];" +
   	    "}" +
   	    "" +
        "__kernel void "+
        "sampleKernel(__global int *results,"+
        "             __global const int *graph,"+
        "             __global const int *nodes,"+
        "             __global int *results_length)"+
        "{" +
        "		int gid = get_global_id(0);"+
        "		int lid = get_local_id(0);"+
        "		results[gid] = test(graph);"+
        "		results_length[lid] = lid;"+

        //        "	 node temp;" +
//        "	temp.array = c;"+
//        "    int gid = get_global_id(0);"+
//        "    c[gid] = c[gid]*multiply(a[gid],b[gid]);"+
        //"    c[gid] = a[gid] * b[gid];"+
        "}";
*/    

int internal_connected = 1;
int[] check_set;
int[] check_set_length;
int[] results;
int[] results_length;
int[] nodes = new int[1];
int[] graph;
node2 nodes_to_consider;
int[] nodes_to_consider_length = new int[1];
node2 empty_node = new node2();
int square_size;
int[] synch;
boolean run_once = false;


final int platformIndex = 0;
final long deviceType = CL_DEVICE_TYPE_ALL;
final int deviceIndex = 0;
int numPlatformsArray[] = new int[1];
int numDevicesArray[] = new int[1];
int numPlatforms;
int numDevices;
long global_work_size[];
long local_work_size[];
cl_mem memObjects[];
cl_platform_id platform;
cl_platform_id platforms[];
cl_context_properties contextProperties;
Pointer src_check_set;// = Pointer.to(check_set);
Pointer src_check_set_length;//= Pointer.to(check_set_length);
Pointer src_results;// = Pointer.to(results);
Pointer src_results_length;// = Pointer.to(results_length);
Pointer src_nodes;// = Pointer.to(nodes);
Pointer src_graph;// = Pointer.to(graph);
Pointer src_nodes_to_consider;// = Pointer.to(nodes_to_consider);
Pointer src_nodes_to_consider_length;// = Pointer.to(nodes_to_consider_length);
cl_device_id devices[];
cl_device_id device;
cl_command_queue commandQueue;
cl_context context;
cl_program program;
cl_kernel kernel;


    private void is_there_another(int[] check_set, int[] check_set_length, int[] results, int[] results_length, int[] nodes, int[] graph, int[] nodes_to_consider, int nodes_to_consider_length, int lid){

		check_set_length[lid] = 0;

		Bochert_neighbor(check_set, lid*nodes[0], check_set_length, lid, nodes, graph, nodes_to_consider[lid], nodes_to_consider, 0, nodes_to_consider_length);

		if(check_set_length[lid] == 0)
			return;

		Bochert_neighbor(results,lid*nodes[0], results_length, lid, nodes, graph, check_set[lid*nodes[0]+check_set_length[lid]-1], nodes_to_consider, 0, nodes_to_consider_length);
		
		if(results_length[lid] <= 1)
			return;

		for(int i = check_set_length[lid]-2; i>=0; i--){
			Bochert_neighbor(results,lid*nodes[0], results_length, lid, nodes, graph, check_set[lid*nodes[0]+i], results, nodes[0]*lid, results_length[lid]);
			
			if(results_length[lid] <= 1){
				return;
			}

		}

		return;
	}
 	private void Bochert_neighbor(int[] results, int results_start, int[] results_length, int results_length_index, int[] nodes, int[] graph, int n, int[] array, int array_start, int array_length){
 		
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


    
    
public gpu(int[] new_graph, int setnodes){
    	
	square_size = setnodes*setnodes;	
	
	check_set = new int[square_size];
	check_set_length= new int[setnodes];
	results = new int[square_size];
	results_length = new int[setnodes];
	nodes[0] = setnodes;
	graph = new_graph;
	synch = new int[setnodes];
	nodes_to_consider = new node2(setnodes);


	String temp = "" +
			"__global int* check_set, " +
			"__global int* check_set_length, " +
			"__global int* results, " +
			"__global int* results_length, " +
			"__global int* nodes, " +
			"__global int* graph, " +
			"__global int* nodes_to_consider, " +
			"__global int * nodes_to_consider_length){";

	
	
	for(int i = 0; i<square_size; i++)
		results[0] = 0;
	
    // Create input- and output data 
    
    
    
    

    

    // The platform, device type and device number
    // that will be used

    // Enable exceptions and subsequently omit error checks in this sample
    CL.setExceptionsEnabled(true);

    // Obtain the number of platforms
    clGetPlatformIDs(0, null, numPlatformsArray);
    numPlatforms = numPlatformsArray[0];

    // Obtain a platform ID
    platforms = new cl_platform_id[numPlatforms];
    clGetPlatformIDs(platforms.length, platforms, null);
    platform = platforms[platformIndex];

    // Initialize the context properties
    contextProperties = new cl_context_properties();
    contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
    
    // Obtain the number of devices for the platform
    clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
    numDevices = numDevicesArray[0];
    
    // Obtain a device ID 
    devices = new cl_device_id[numDevices];
    clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
    device = devices[deviceIndex];

    // Create a context for the selected device
    context = clCreateContext(
        contextProperties, 1, new cl_device_id[]{device}, 
        null, null, null);
    
    // Create a command-queue for the selected device
    commandQueue = 
        clCreateCommandQueue(context, device, 0, null);

        
    
    // Create the program from the source code
    program = clCreateProgramWithSource(context,
        1, new String[]{ programSource }, null, null);
    
    // Build the program
    clBuildProgram(program, 0, null, null, null, null);
    

    
 
        
        

    		


        
    }
    
public void run_it(node2 ntc){

	
	nodes_to_consider.copy_array(ntc);
	nodes_to_consider_length[0] = ntc.get_length();

    global_work_size = new long[]{nodes_to_consider_length[0]};
    local_work_size = new long[]{nodes_to_consider_length[0]};

    
    
    kernel = clCreateKernel(program, "is_there_another", null);

    
	src_check_set = Pointer.to(check_set);
	src_check_set_length= Pointer.to(check_set_length);
    src_results = Pointer.to(results);
    src_results_length = Pointer.to(results_length);
    src_nodes = Pointer.to(nodes);
	src_graph = Pointer.to(graph);
	src_nodes_to_consider = Pointer.to(nodes_to_consider.get_full_array());
	src_nodes_to_consider_length = Pointer.to(nodes_to_consider_length);

	
	   // Allocate the memory objects for the input- and output data
    memObjects = new cl_mem[8];
    memObjects[0] = clCreateBuffer(context, 
        CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
        Sizeof.cl_int * nodes[0] * nodes[0], src_check_set, null);
    memObjects[1] = clCreateBuffer(context, 
         CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
         Sizeof.cl_int * nodes[0], src_check_set_length, null);
    memObjects[2] = clCreateBuffer(context, 
        CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
        Sizeof.cl_int * nodes[0] * nodes[0], src_results, null);
    memObjects[3] = clCreateBuffer(context, 
        CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
        Sizeof.cl_int * nodes[0], src_results_length, null);
    memObjects[4] = clCreateBuffer(context, 
        CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
        Sizeof.cl_int * 1, src_nodes, null);
    memObjects[5] = clCreateBuffer(context, 
        CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
        Sizeof.cl_int * nodes[0] * nodes[0], src_graph, null);
    
        // Set the arguments for the kernel
    clSetKernelArg(kernel, 0, 
            Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernel, 1, 
            Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernel, 2, 
            Sizeof.cl_mem, Pointer.to(memObjects[2]));
        clSetKernelArg(kernel, 3, 
            Sizeof.cl_mem, Pointer.to(memObjects[3]));
        clSetKernelArg(kernel, 4, 
            Sizeof.cl_mem, Pointer.to(memObjects[4]));
        clSetKernelArg(kernel, 5, 
            Sizeof.cl_mem, Pointer.to(memObjects[5]));
        
       
    
	
	memObjects[6] = clCreateBuffer(context, 
	        CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
	        Sizeof.cl_int * nodes[0], src_nodes_to_consider, null);
	    clSetKernelArg(kernel, 6, 
            Sizeof.cl_mem, Pointer.to(memObjects[6]));

    
	    memObjects[7] = clCreateBuffer(context, 
		        CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
		        Sizeof.cl_int * 1, src_nodes_to_consider_length, null);    	

	        clSetKernelArg(kernel, 7, 
	            Sizeof.cl_mem, Pointer.to(memObjects[7]));



	
    // Set the work-item dimensions

    // Execute the kernel
    clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
        global_work_size, local_work_size, 0, null, null);
    
    // Read the output data
    clEnqueueReadBuffer(commandQueue, memObjects[0], CL_TRUE, 0,
        square_size * Sizeof.cl_int, src_check_set, 0, null, null);
    clEnqueueReadBuffer(commandQueue, memObjects[1], CL_TRUE, 0,
        nodes[0] * Sizeof.cl_int, src_check_set_length, 0, null, null);
    clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,
        square_size * Sizeof.cl_int, src_results, 0, null, null);
    clEnqueueReadBuffer(commandQueue, memObjects[3], CL_TRUE, 0,
        nodes[0] * Sizeof.cl_int, src_results_length, 0, null, null);
    
    // Release kernel, program, and memory objects
    clReleaseMemObject(memObjects[6]);
    clReleaseMemObject(memObjects[7]);
    clReleaseMemObject(memObjects[0]);
    clReleaseMemObject(memObjects[1]);
    clReleaseMemObject(memObjects[2]);
    clReleaseMemObject(memObjects[3]);
    clReleaseMemObject(memObjects[4]);
    clReleaseMemObject(memObjects[5]);
    clReleaseKernel(kernel);
//    clReleaseProgram(program);
//    clReleaseCommandQueue(commandQueue);
//    clReleaseContext(context);
    
    // Verify the result

    

}

public void run_it_self_contained(node2 ntc){

	
	nodes_to_consider.copy_array(ntc);//.get_full_array();
	nodes_to_consider_length[0] = ntc.get_length();

	
/*  	for(int i = 0; i<ntc.get_length(); i++){
	this.is_there_another(check_set, check_set_length, results, results_length, nodes, graph, nodes_to_consider, nodes_to_consider_length[0], i);
 	}	
/*  	int[] resultsa = new int[nodes[0]];
  	int[] rsla = new int[1];
  	Bochert_neighbor(resultsa, 0, rsla, 0, nodes, graph, 28, nodes_to_consider, 0, nodes_to_consider_length[0]);
  	System.out.println("28 is connected to: ");
  	for(int i = 0; i<rsla[0]; i++)
  		System.out.print(resultsa[i]+" ");
  	System.out.println();
  	Bochert_neighbor(resultsa, 0, rsla, 0, nodes, graph, 29, nodes_to_consider, 0, nodes_to_consider_length[0]);
  	System.out.println("29 is connected to: ");
  	for(int i = 0; i<rsla[0]; i++)
  		System.out.print(resultsa[i]+" ");
  	System.out.println();
  	Bochert_neighbor(resultsa, 0, rsla, 0, nodes, graph, 30, nodes_to_consider, 0, nodes_to_consider_length[0]);
  	System.out.println("30 is connected to: ");
  	for(int i = 0; i<rsla[0]; i++)
  		System.out.print(resultsa[i]+" ");
  	System.out.println();
// 	private void Bochert_neighbor(int[] results, int results_start, int[] results_length, int results_length_index, int[] nodes, int[] graph, int n, int[] array, int array_start, int array_length){
*/
  	
//  	if(1!=2)
//  		return;
	
    // Create the kernel
    kernel = clCreateKernel(program, "is_there_another_self_contained", null);

	
	src_check_set = Pointer.to(check_set);
	src_check_set_length= Pointer.to(check_set_length);
    src_results = Pointer.to(results);
    src_results_length = Pointer.to(results_length);
    src_nodes = Pointer.to(nodes);
	src_graph = Pointer.to(graph);
	src_nodes_to_consider = Pointer.to(nodes_to_consider.get_full_array());
	src_nodes_to_consider_length = Pointer.to(nodes_to_consider_length);

	
    // Allocate the memory objects for the input- and output data
    memObjects = new cl_mem[8];
    memObjects[0] = clCreateBuffer(context, 
        CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
        Sizeof.cl_int * nodes[0] * nodes[0], src_check_set, null);
    memObjects[1] = clCreateBuffer(context, 
         CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
         Sizeof.cl_int * nodes[0], src_check_set_length, null);
    memObjects[2] = clCreateBuffer(context, 
        CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
        Sizeof.cl_int * nodes[0] * nodes[0], src_results, null);
    memObjects[3] = clCreateBuffer(context, 
        CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
        Sizeof.cl_int * nodes[0], src_results_length, null);
    memObjects[4] = clCreateBuffer(context, 
        CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
        Sizeof.cl_int * 1, src_nodes, null);
    memObjects[5] = clCreateBuffer(context, 
        CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
        Sizeof.cl_int * nodes[0] * nodes[0], src_graph, null);
    memObjects[6] = clCreateBuffer(context, 
        CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
        Sizeof.cl_int * nodes_to_consider.get_length(), src_nodes_to_consider, null);
    memObjects[7] = clCreateBuffer(context, 
        CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
        Sizeof.cl_int * 1, src_nodes_to_consider_length, null);

	
    // Set the arguments for the kernel
    clSetKernelArg(kernel, 0, 
        Sizeof.cl_mem, Pointer.to(memObjects[0]));
    clSetKernelArg(kernel, 1, 
        Sizeof.cl_mem, Pointer.to(memObjects[1]));
    clSetKernelArg(kernel, 2, 
        Sizeof.cl_mem, Pointer.to(memObjects[2]));
    clSetKernelArg(kernel, 3, 
        Sizeof.cl_mem, Pointer.to(memObjects[3]));
    clSetKernelArg(kernel, 4, 
        Sizeof.cl_mem, Pointer.to(memObjects[4]));
    clSetKernelArg(kernel, 5, 
        Sizeof.cl_mem, Pointer.to(memObjects[5]));
    clSetKernelArg(kernel, 6, 
        Sizeof.cl_mem, Pointer.to(memObjects[6]));
    clSetKernelArg(kernel, 7, 
        Sizeof.cl_mem, Pointer.to(memObjects[7]));
    
    // Set the work-item dimensions
    global_work_size = new long[]{nodes_to_consider.get_length()};
    local_work_size = new long[]{nodes_to_consider.get_length()};
    
    // Execute the kernel
    clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
        global_work_size, local_work_size, 0, null, null);
    
    // Read the output data
    clEnqueueReadBuffer(commandQueue, memObjects[0], CL_TRUE, 0,
        square_size * Sizeof.cl_int, src_check_set, 0, null, null);
    clEnqueueReadBuffer(commandQueue, memObjects[1], CL_TRUE, 0,
        nodes[0] * Sizeof.cl_int, src_check_set_length, 0, null, null);
    clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,
        square_size * Sizeof.cl_int, src_results, 0, null, null);
    clEnqueueReadBuffer(commandQueue, memObjects[3], CL_TRUE, 0,
        nodes[0] * Sizeof.cl_int, src_results_length, 0, null, null);
    
/*    // Release kernel, program, and memory objects
    clReleaseMemObject(memObjects[0]);
    clReleaseMemObject(memObjects[1]);
    clReleaseMemObject(memObjects[2]);
    clReleaseMemObject(memObjects[3]);
    clReleaseMemObject(memObjects[4]);
    clReleaseMemObject(memObjects[5]);
    clReleaseMemObject(memObjects[6]);
    clReleaseMemObject(memObjects[7]);
    clReleaseKernel(kernel);
    clReleaseProgram(program);
    clReleaseCommandQueue(commandQueue);
    clReleaseContext(context);
*/    
    // Verify the result

    

}

protected void finalize ()  {

	// Release kernel, program, and memory objects
    clReleaseMemObject(memObjects[0]);
    clReleaseMemObject(memObjects[1]);
    clReleaseMemObject(memObjects[2]);
    clReleaseMemObject(memObjects[3]);
    clReleaseMemObject(memObjects[4]);
    clReleaseMemObject(memObjects[5]);
    clReleaseMemObject(memObjects[6]);
    clReleaseMemObject(memObjects[7]);
    clReleaseKernel(kernel);
    clReleaseProgram(program);
    clReleaseCommandQueue(commandQueue);
    clReleaseContext(context);
    

}


    public static void main(String args[])
    {
        int graph[] = {
    			0,1,1,1,1,1,1,1,1,0,
    			1,0,1,1,1,1,1,1,0,1,
    			1,1,0,1,1,1,1,0,1,1,
    			1,1,1,0,1,1,0,1,1,1,
    			1,1,1,1,0,0,1,1,1,1,
    			1,1,1,1,0,0,1,1,1,1,
    			1,1,1,0,1,1,0,1,1,1,
    			1,1,0,1,1,1,1,0,1,1,
    			1,0,1,1,1,1,1,1,0,1,
    			0,1,1,1,1,1,1,1,1,0};

        
        int[] nnodes_to_consider = {1,2,3,4,5,6,7,8,9,10};        
        int[] nnodes_to_consider2 = {4,5,6,7,8,9,10};
    	node2 nodes_to_consider = new node2(nnodes_to_consider);
    	node2 nodes_to_consider2 = new node2(nnodes_to_consider2);

        
    	gpu paralyze = new gpu(graph,10);

    	paralyze.run_it(nodes_to_consider);
    	for(int i = 0; i<nodes_to_consider.get_length(); i++){
    		System.out.print("result for node: "+(i+1)+" is: ");
    		for(int j=0; j<paralyze.results_length[i]; j++){
    			System.out.print(paralyze.results[paralyze.nodes[0]*i+j]+" ");
    			 			
    		}
    		System.out.println();
    	}

    	for(int i = 0; i<nodes_to_consider.get_length(); i++){
    		System.out.print("check_set for node: "+(i+1)+" is: ");
    		for(int j=0; j<paralyze.check_set_length[i]; j++){
    			System.out.print(paralyze.check_set[paralyze.nodes[0]*i+j]+" ");
    			 			
    		}
    		System.out.println();
    	}    

    		System.out.print("ntc is: "+nodes_to_consider.print_list());
    		System.out.println();
    		System.out.println();

    	paralyze.run_it(nodes_to_consider2);

    	for(int i = 0; i<nodes_to_consider2.get_length(); i++){
    		System.out.print("result for node: "+(i+1)+" is: ");
    		for(int j=0; j<paralyze.results_length[i]; j++){
    			System.out.print(paralyze.results[paralyze.nodes[0]*i+j]+" ");
    			 			
    		}
    		System.out.println();
    	}

    	for(int i = 0; i<nodes_to_consider2.get_length(); i++){
    		System.out.print("check_set for node: "+(i+1)+" is: ");
    		for(int j=0; j<paralyze.check_set_length[i]; j++){
    			System.out.print(paralyze.check_set[paralyze.nodes[0]*i+j]+" ");
    			 			
    		}
    		System.out.println();
    	}    
    	
    	int nodes_to_consider_length = 10;
    	int[] check_set = new int[paralyze.nodes[0]*paralyze.nodes[0]];
    	int[] check_set_length = new int[paralyze.nodes[0]];
    	int[] nodes = {10};
    	int[] results = new int[100];
    	int[] results_length = new int[10];
    	    	
/*    	for(int i = 0; i<nodes[0]; i++){
    		paralyze.is_there_another(check_set, check_set_length, results, results_length, nodes, graph, nodes_to_consider, nodes_to_consider_length, i);
//    	    private void is_there_another(int[] results, int[] results_length, int[] nodes, int[] graph, int[] nodes_to_consider, int nodes_to_consider_length, int lid){
   	}

    	for(int i = 0; i<nodes[0]; i++){
    		System.out.print("result for node: "+(i+1)+" is: ");
    		for(int j=0; j<results_length[i]; j++){
    			System.out.print(results[nodes[0]*i+j]+" ");
    			 			
    		}
    		System.out.println();
    	}

    	for(int i = 0; i<nodes[0]; i++){
    		System.out.print("check_set for node: "+(i+1)+" is: ");
    		for(int j=0; j<check_set_length[i]; j++){
    			System.out.print(check_set[nodes[0]*i+j]+" ");
    			 			
    		}
    		System.out.println();
    	}
*/
    }
    
}
