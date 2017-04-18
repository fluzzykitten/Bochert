package Clique;

public class multi implements Runnable {

//	static int kitten[] = new int[10];
//	static int thread_number;
	
//	private final long countUntil;
	static int index = 0;
	int run;
	private int[] wayback;
	private int[] who_am_i;

	multi(long countUntil) {
	//	this.countUntil = countUntil;
	}
	
	multi(int[] loopback, int a, int[] i){
		wayback = loopback;
		run = a;
		who_am_i = i;
	}

	@Override
	public void run() {
		/*long sum = 0;
		for (long i = 1; i < countUntil; i++) {
			sum += i;
		}
		System.out.println(sum);
		*/
		
		synchronized(this){
			who_am_i[0] = index;
			index++;
		}
		
		int temp = fib(run);
		System.out.println("thread with index of: "+who_am_i[0]+" just completed");

		synchronized (this){
			wayback[who_am_i[0]] = temp;
		}
	}
	
	public synchronized void save(){
		wayback[index] = index;
	}
	
	public int fib(int n){

		System.out.println("executing fib("+n+")");

		if (n==1) 
			return 1;
		else if (n==0) 
			return 0;
		else{
			
			
			int[] i = new int[1];
			Runnable task = new multi(wayback, n-1, i);			
			Thread worker = new Thread(task);
			worker.start();

			int[] j = new int[1];
			Runnable task2 = new multi(wayback, n-2, j);			
			Thread worker2 = new Thread(task2);
			worker2.start();

			while(worker.isAlive() || worker2.isAlive()){
				try {
					Thread.sleep(100);
					} catch(InterruptedException e) {
					} 

			}
			
			
			return wayback[i[0]]+wayback[j[0]];
			
		}
	}
}