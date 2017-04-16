package Clique;

public class semaphore {
	  private boolean signal = false;
	  public long total_time_waiting = 0;
	  private String name = "";
	  
	  semaphore(){}
	  
	  semaphore(String name1){
		  name = name1;
	  }
	  
	  public synchronized void take() throws InterruptedException{
		  long start = System.currentTimeMillis();
		  
		while(this.signal) wait();
	    this.signal = true;
	    start = System.currentTimeMillis() - start;
	    //if(name != "") System.out.println("Semaphore:"+name+" prev ttw: "+total_time_waiting+" now adding: "+(start));
	    total_time_waiting = total_time_waiting + (start);
	  }

	  public synchronized void release() {
	    this.signal = false;
	    this.notify();
	  }

	}

