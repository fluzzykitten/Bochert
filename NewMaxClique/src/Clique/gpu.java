package Clique;

/*
 * JOCL - Java bindings for OpenCL
 * 
 * Copyright 2009 Marco Hutter - http://www.jocl.org/
 */

import static org.jocl.CL.*;

import java.awt.image.Kernel;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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

	private static final String KERNEL_SOURCE_FILE_NAME = 
			"kernels/program.cl";


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

cl_command_queue commandQueue;
cl_context context;
cl_kernel kernel;



 	
    private static String readFile(String fileName)
    {
        try
        {
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName)));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while (true)
            {
                line = br.readLine();
                if (line == null)
                {
                    break;
                }
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
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

	
        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];
        
        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        context = clCreateContext(
            contextProperties, 1, new cl_device_id[]{device}, 
            null, null, null);
        
        // Create a command-queue for the selected device
        commandQueue = 
            clCreateCommandQueue(context, device, 0, null);

        
        // Create the OpenCL kernel from the program
        String source = readFile(KERNEL_SOURCE_FILE_NAME);
        cl_program program = clCreateProgramWithSource(context, 1, 
            new String[]{ source }, null, null);
        String compileOptions = "";
        clBuildProgram(program, 0, null, compileOptions, null, null);
        kernel = clCreateKernel(program, "pretest", null);

        
        
        clReleaseProgram(program);
    }
    


void release_crap()  {

	// Release kernel, program, and memory objects
    clReleaseKernel(kernel);
    clReleaseCommandQueue(commandQueue);
    clReleaseContext(context);
    

}


    public static void main(String args[])
    {
    	
    	System.out.println("hello");
    	
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

        
    	gpu paralyze = new gpu(graph,10);
    	
    	
    	
    	paralyze.release_crap();

    	
    	System.out.println("G'byebye");
    }
    
}
