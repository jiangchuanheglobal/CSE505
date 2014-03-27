//Filename: CDLCoarse.java
//Function: Coarse grained lock class declaration
//Author:   jiangchuan he
//UBemail: 	jiangchu@buffalo.edu
//history:  09/21/13


package testMain;

//CDLCoarse, the subclass of CDLList<T>
//is used to implement coarse grained lock
//coarse grained lock is actually a global lock, in this example, it means lock the entire list object.
//in my project, I just use a single lock to simulate the global lock
//all the threads which needs to access to the list must get this global lock.
//if one of the thread is currently holding the lock, then all others cannot get//get access to the list until the lock has been release!
public class CDLCoarse<T> extends CDLList<T>{
	//I create a simple global lock object to simulate the global lock instead
	//of creating a primitive object and using synchronized keyword to lock that object. 
	private CoarseLock coarseLock = new CoarseLock();
	//constructor of CDLCoarse	
	public CDLCoarse(T V){super(V);}
	//override the methods of super Cursor class to implement the mechanism of coarse lock
	public class Cursor extends CDLList<T>.Cursor{
		private Writer  writer;
		//constructor of Cursor class
		public Cursor(Element e){
			super(e);
			//create a subclass writer
			writer = new Writer(this);
		}
		
		public void previous(){
			try{
				coarseLock.lock();
				super.previous();
				coarseLock.unLock();
			}catch(InterruptedException e){
				coarseLock.unLock();
			}
		}
		public void next(){
			try{
				coarseLock.lock(); 			//get the global lock
				super.next();				//move the cursor to the next position
				coarseLock.unLock();		//release global lock
			}catch(InterruptedException e){
				coarseLock.unLock();		//if there is a exception, release the lock
			}
		}
		public Writer writer(){
			return writer;
		}
		
		public void traverse(){
			try{
			coarseLock.lock();
			super.traverse();
			System.out.println();
			coarseLock.unLock();
			}catch(InterruptedException e){
				coarseLock.unLock();	
			}
		}
	}
	//override the methods of super class Writer to implement the mechanism of coarse lock
	public class Writer extends CDLList<T>.Writer{
		//constructor of writer class
		public Writer(Cursor c){super(c);}
		
		public boolean insertBefore(T val){
			try{
				coarseLock.lock();
				super.insertBefore(val);
				coarseLock.unLock();
				return true;
			}
			catch(InterruptedException e){
				coarseLock.unLock();
				return false;
			}
		}
		
		public boolean insertAfter(T val){
			try{
				coarseLock.lock();
				super.insertAfter(val);
				coarseLock.unLock();
				return true;
			}
			catch(InterruptedException e){
				coarseLock.unLock();
				return false;
			}
			
		}
	}
	
	//override reader method to get subclass cursor
	public Cursor reader(Element from){
		Cursor cursor = new Cursor(from);
		return cursor;
	}
}

