package Clique;

import java.util.ArrayList;
import java.util.List;

public class runmulti {


	public static void main(String[] args) {
		// We will store the threads so that we can check if they are done
		List<Thread> threads = new ArrayList<Thread>();
		// We will create 500 threads
		int[] loop_back = new int[25000];
		
//		for (int i = 0; i < 500; i++) {
			Runnable task = new multi(loop_back, 10, new int[1]);
			
			Thread worker = new Thread(task);
			// We can set the name of the thread
			worker.setName(String.valueOf(0));
			// Start the thread, never call method run() direct
			worker.start();
			// Remember the thread for later usage
			threads.add(worker);
//		}
		int running = 0;
		do {
			running = 0;
			for (Thread thread : threads) {
				if (thread.isAlive()) {
					running++;
				}
				try {
					Thread.sleep(1000);
					} catch(InterruptedException e) {
					} 
			}
			System.out.println("We have " + running + " running threads. ");
		} while (running > 0);

		System.out.println("loopback is now: ");
//		for(int i = 0; i<loop_back.length; i++){
			System.out.print(" "+loop_back[0]);
//		}
			
		
	}
}
