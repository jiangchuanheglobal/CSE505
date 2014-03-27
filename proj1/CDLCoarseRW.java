//Filename: CDLCoarseRW.java
//Function: Coarse grained and read/write lock class declaration
//Author:   jiangchuan he
//UBemail: 	jiangchu@buffalo.edu
//history:  09/21/13

package testMain;



public class CDLCoarseRW<T> extends CDLList<T>{
	//create a read/write lock object
	private RWLock<T> rwLock = new RWLock<T>();
	//constructor of CDLCoarseRW class
	public CDLCoarseRW(T V){super(V);}
	//
	public class Cursor extends CDLList<T>.Cursor{
		private Writer  writer;
		
		public Cursor(Element e){
			super(e);
			writer = new Writer(this);
		}
		
		public void previous(){
			try{
				rwLock.lockRead();
				super.previous();
				rwLock.unlockRead();
			}catch(InterruptedException e){
				rwLock.unlockRead();
			}
		}
		public void next(){
			try{
				rwLock.lockRead();
				super.next();
				rwLock.unlockRead();
			}catch(InterruptedException e){
				rwLock.unlockRead();
			}
		}
		public Writer writer(){
			return writer;
		}
		
		public void traverse(){
			try{
			rwLock.lockRead();
			super.traverse();
			System.out.println();
			rwLock.unlockRead();
			}catch(InterruptedException e){
				rwLock.unlockRead();	
			}
		}
	}
	
	public class Writer extends CDLList<T>.Writer{
		
		public Writer(Cursor c){super(c);}
		
		public boolean insertBefore(T val){
			try{
				rwLock.lockWrite();
				super.insertBefore(val);
				rwLock.unlockWrite();
				return true;
			}
			catch(InterruptedException e){
				return false;
			}
		}
		
		public boolean insertAfter(T val){
			try{
				rwLock.lockWrite();
				super.insertAfter(val);
				rwLock.unlockWrite();
				return true;
			}
			catch(InterruptedException e){
				rwLock.unlockWrite();
				return false;
			}
		}
	}
	//override reader method to get subclass cursor
	public CDLCoarseRW<T>.Cursor reader(Element from){
		CDLCoarseRW<T>.Cursor cursor = new Cursor(from);
		return cursor;
	}
}
