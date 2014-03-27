//Filename: Lock.java
//Function: several types of Java locks used to implement different lock mechanism
//Author:   jiangchuan he
//UBemail: 	jiangchu@buffalo.edu
//history:  09/21/13

package testMain;



//CoarseLock class is for implementation of coarse grained lock
class CoarseLock{
	//flag indicate whether the coarse grain lock is being locked or not
	private volatile boolean isLocked = false;
	//constructor of CoarseLock, have no usage now.
	CoarseLock(){}
	//if the global lock is being acquired, then all the thread invoke this method
	//will block itself
	public synchronized void lock() throws InterruptedException {	
			while(isLocked){
				wait();
			}
			isLocked = true;
	}
	//when lock is released, notify all waiting threads
	public synchronized void unLock(){
		isLocked = false;
		notifyAll();
	}
}

//FineLock class is for implementation of Fine grained lock
class FineLock<T>{
	//constructor
	public FineLock(){}
	//lock specified element
	//if failed, return false, else return true
	public boolean lock(CDLList<T>.Element e){
		assert(e!=null);
		synchronized(e){
			if(e.isLocked){return false;}
			else{e.isLocked=true; return true;}
		}
	}
	//unlock specified element
 	public  void unLock(CDLList<T>.Element e){
 		assert(e!=null);
		synchronized(e){
			e.isLocked=false;
		}
	}
}

//RWLock is for read/write lock
class RWLock<T>{
	//number of readers
	private int readers;
	//isWLocked variable will be true if there is a writer get the lock
	private volatile boolean isWLocked;
	//fairness is used to implement the fairness feature of the lock
	private volatile boolean fairness;
	//constructor of RWLock
	public RWLock(){
		readers 	= 0;
		isWLocked 	= false;
		fairness 	= false;
	}
	//acquire read lock, when a writer get the lock, reader thread blocks itself
	public synchronized void lockRead()throws InterruptedException{
		while(isWLocked){
			wait();
		}
		//judge fairness is effective or not
		if(fairness)return;
		readers++;
	}
	// read lock for fine grained locking
	public boolean lockRead(CDLList<T>.Element e){
		synchronized(e){
			if(e.isLocked){return false;}
			else{
				if(e.fairness)return false;
				e.cntLocked++;return true;
			}
		}
	}
	//release read lock, since we allow multiple reader threads to concurrently get
	//access to the protected code section, and disallow reader thread and writer
	//thread access currently, so writer thread will be blocked until all the readers
	//exit the protected code section
	public synchronized void unlockRead(){
		readers--;
		if(readers == 0){notifyAll();}
	}
	//decrement locked times
	public void unlockRead(CDLList<T>.Element e){
		synchronized(e){
			e.cntLocked--;
		}
	}
	//allow only one writer thread access the protected code section, so if there is
	//a reader or writer thread already in the code section, this writer will be blocked
	public synchronized void lockWrite()throws InterruptedException{
		while(readers>0 || isWLocked){fairness = true;wait();}
		isWLocked = true;
	}
	//write lock for fine grained locking
	public boolean lockWrite(CDLList<T>.Element e){
		synchronized(e){
			if(e.cntLocked>0||e.isLocked){
				if(e.cntLocked>0){e.fairness=true;}
				return false;
			}
			else{e.isLocked = true;}
		}
		return true;
	}
	//writer unlock action will signal all blocking threads to wake up
	public synchronized void unlockWrite(){
		isWLocked = false;
		fairness = false;
		notifyAll();
	}
	//unlock element
	public void unlockWrite(CDLList<T>.Element e){
		synchronized(e){
			e.isLocked = false;
			e.fairness = false;
		}
	}
}

