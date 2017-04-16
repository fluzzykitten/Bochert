package Clique;

public class semaphore {
	  private boolean signal = false;

	  public synchronized void take() throws InterruptedException{
	    while(this.signal) wait();
	    this.signal = true;
	  }

	  public synchronized void release() {
	    this.signal = false;
	    this.notify();
	  }

	}

