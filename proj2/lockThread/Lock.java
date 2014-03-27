//Filename: Lock.java
//Function: several types of Java locks used to implement different lock mechanism
//Author:   jiangchuan he
//UBemail: 	jiangchu@buffalo.edu
//history:  09/21/13

package lockThread;
import java.util.concurrent.atomic.AtomicInteger;


/*
 *<=-2		 			-1				0				1			>=2
 *reader(s) 			|free for 		|writer			|free     	|reader(s) locked
 *locked,				|writer  		|locked			|			|
 *but no more			|to take 
 *readers are allowed	|lock
 *to get the lock		|
 */

/*
 * Java atomicInteger package API we will use
 * get()
 * compareAndSet()
 */
class RWLock{
	public AtomicInteger lock;
	RWLock(){
		lock = new AtomicInteger(1);
	}
	   
	public void lockRead()throws InterruptedException{
		for(;;){
			int cur  = lock.get();
			if(cur>=1){
				if(lock.compareAndSet(cur, cur+1))break;
			}
			synchronized(this){wait();}
		}
	}
	public void unlockRead(){
		for(;;){
			int cur = lock.get();
			if(cur<-1)
				if(lock.compareAndSet(cur, cur+1))break;
			if(cur>1)
				if(lock.compareAndSet(cur, cur-1))break;
		}
		
		int cur = lock.get();
		if(cur==-1 || cur==1){
			synchronized(this){notifyAll();}
		}
		
	}
	
	public void lockWrite()throws InterruptedException{
		for(;;){
			int cur = lock.get();
			if(cur == -1||cur == 1){
				if(lock.compareAndSet(cur, 0))
					break;
			}
			if(cur>1){
				lock.compareAndSet(cur, -cur);
			}

			synchronized(this){wait();}
		}
	}
	
	public void unlockWrite(){
		for(;;){
			int cur = lock.get();
			if(cur==0)
				if(lock.compareAndSet(cur, 1))break;
		}
		synchronized(this){notifyAll();}
	}
}
