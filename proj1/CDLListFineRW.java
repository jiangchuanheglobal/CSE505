//Filename: CDLListFineRW.java
//Function: Fine grained and read/write lock class declaration
//Author:   jiangchuan he
//UBemail: 	jiangchu@buffalo.edu
//history:  09/21/13


package testMain;



public class CDLListFineRW<T> extends CDLList<T> {
	//create a FineLock object
	private RWLock<T> rwLock = new RWLock<T>();
	public CDLListFineRW(T V) {
		super(V);
	}
	public class Cursor extends CDLList<T>.Cursor{
		private Writer writer;
		
		public Cursor(Element e){
			super(e);
			writer = new Writer(this);
		}
		
		public void previous(){
			Element curElmt = current();
			if(rwLock.lockRead(curElmt))
			{
				Element prevElmt = current().getPrevElement();
				//check if only one element in the list
				if(curElmt==prevElmt){
					rwLock.unlockRead(curElmt);
					return;
				}
				else{
					if(rwLock.lockRead(prevElmt)){
						super.previous();
						rwLock.unlockRead(curElmt);
						rwLock.unlockRead(prevElmt);
						return;
					}
					rwLock.unlockRead(curElmt);
					return;
				}
			}
		}
		
		public void next(){
			Element curElmt = current();
			if(rwLock.lockRead(curElmt))
			{
				Element nextElmt = current().getPrevElement();
				//check if only one element in the list
				if(curElmt==nextElmt){
					rwLock.unlockRead(curElmt);
					return;
				}
				else{
					if(rwLock.lockRead(nextElmt)){
						super.next();
						rwLock.unlockRead(curElmt);
						rwLock.unlockRead(nextElmt);
						return;
					}
					rwLock.unlockRead(curElmt);
					return;
				}
			}
		}
		
		public Writer writer(){
			return writer;
		}
	}
	public class Writer extends CDLList<T>.Writer{

		public Writer(Cursor c) {
			super(c);
		}
		public boolean insertBefore(T val){
			//get current element which the cursor refer to
			Element curElmt = getCursor().current();
			//lock this element in case of being locked by other threads
			if(rwLock.lockWrite(curElmt))
			{
				//get the previous element of current element
				Element preElmt = getCursor().current().getPrevElement();
				//check if it is the condition of only one element
				if(curElmt==preElmt){
					super.insertBefore(val);
					rwLock.unlockWrite(curElmt);
					return true;
				}
				else{
					//we have at least two elements
					//lock the previous element
					if(rwLock.lockWrite(preElmt))
					{
						super.insertBefore(val);
						rwLock.unlockWrite(curElmt);
						rwLock.unlockWrite(preElmt);
						return true;
					}
					rwLock.unlockWrite(curElmt);
					return false;	
				}
			}
			//if we fail to lock the current element, return false
			else return false;
		}
		public boolean insertAfter(T val){
			//get current element which the cursor refer to
			Element curElmt = getCursor().current();
			//lock this element in case of being locked by other threads
			if(rwLock.lockWrite(curElmt))
			{
				//get the previous element of current element
				Element nextElmt = getCursor().current().getNextElement();
				//check if it is the condition of only one element
				if(curElmt==nextElmt){
					super.insertAfter(val);
					rwLock.unlockWrite(curElmt);
					return true;
				}
				else{
					//we have at least two elements
					//lock the next element
					if(rwLock.lockWrite(nextElmt))
					{
						super.insertAfter(val);
						rwLock.unlockWrite(curElmt);
						rwLock.unlockWrite(nextElmt);
						return true;
					}
					rwLock.unlockWrite(curElmt);
					return false;	
				}
			}
			else return false;
		}
	}
	public CDLListFineRW<T>.Cursor reader(Element from){
		CDLListFineRW<T>.Cursor cursor = new Cursor(from);
		return cursor;
	}
}
