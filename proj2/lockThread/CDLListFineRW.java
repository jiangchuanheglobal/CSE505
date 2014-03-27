//Filename: CDLListFineRW.java
//Function: Fine grained and read/write lock class declaration
//Author:   jiangchuan he
//UBemail: 	jiangchu@buffalo.edu
//history:  09/21/13


package lockThread;



public class CDLListFineRW<T> extends CDLList<T> {
	
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
			try{
			curElmt.getLock().lockRead();
			Element prevElmt = current().getPrevElement();
			//check if only one element in the list
			if(curElmt==prevElmt){
					curElmt.getLock().unlockRead();
					return;
			}
			super.previous();
		    curElmt.getLock().unlockRead();
			}catch(InterruptedException e){}
		}
		
		public void next(){
			Element curElmt = current();
			try{
			curElmt.getLock().lockRead();
			Element nextElmt = current().getNextElement();
				//check if only one element in the list
			if(curElmt==nextElmt){
					curElmt.getLock().unlockRead();
					return;
			}
			super.next();
		    curElmt.getLock().unlockRead();
			}catch(InterruptedException e){}
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
			try{
				curElmt.getLock().lockWrite();
				//get the previous element of current element
				Element preElmt = getCursor().current().getPrevElement();
				//check if it is the condition of only one element
				if(curElmt==preElmt){
					super.insertBefore(val);
					curElmt.getLock().unlockWrite();
					return true;
				}
				
				//we have at least two elements
				//lock the previous element
				preElmt.getLock().lockWrite();
				super.insertBefore(val);
				preElmt.getLock().unlockWrite();
				curElmt.getLock().unlockWrite();
				return true;
			}catch(InterruptedException e){return false;}
		}
		public boolean insertAfter(T val){
			//get current element which the cursor refer to
			Element curElmt = getCursor().current();
			//lock this element in case of being locked by other threads
			try{
				curElmt.getLock().lockWrite();
				//get the previous element of current element
				Element nextElmt = getCursor().current().getNextElement();
				//check if it is the condition of only one element
				if(curElmt==nextElmt){
					super.insertAfter(val);
					curElmt.getLock().unlockWrite();
					return true;
				}
				
				//we have at least two elements
				//lock the previous element
				nextElmt.getLock().lockWrite();
				super.insertAfter(val);
				nextElmt.getLock().unlockWrite();
				curElmt.getLock().unlockWrite();
				return true;
			}catch(InterruptedException e){return false;}

		}
}
	public CDLListFineRW<T>.Cursor reader(Element from){
		CDLListFineRW<T>.Cursor cursor = new Cursor(from);
		return cursor;
	}
}
