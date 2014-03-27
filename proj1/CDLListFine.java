//Filename: CDLListFine.java
//Function: Fine grained lock class declaration
//Author:   jiangchuan he
//UBemail: 	jiangchu@buffalo.edu
//history:  09/21/13


package testMain;



//class CDLListFine inherits CDLList class
public class CDLListFine<T> extends CDLList<T>{
	//create a FineLock object
	private FineLock<T> fineLock = new FineLock<T>();
	//constructor of CDLListFine	
	public CDLListFine(T V) {
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
			if(fineLock.lock(curElmt))
			{
				Element prevElmt = current().getPrevElement();
				assert(curElmt!=null);
				assert(prevElmt!=null);
				//check if only one element in the list
				if(curElmt==prevElmt){
					fineLock.unLock(curElmt);
					return;
				}
				else{
					if(fineLock.lock(prevElmt)){
						super.previous();
						fineLock.unLock(curElmt);
						fineLock.unLock(prevElmt);
						return;
					}
					fineLock.unLock(curElmt);
					return;
				}
			}
		}
		public void next(){
			Element curElmt = current();
			if(fineLock.lock(curElmt))
			{
				Element nextElmt = current().getNextElement();
				assert(curElmt!=null);
				assert(nextElmt!=null);
				//check if only one element in the list
				if(curElmt==nextElmt){
					fineLock.unLock(curElmt);
					return;
				}
				else{
					if(fineLock.lock(nextElmt)){
						super.next();
						fineLock.unLock(curElmt);
						fineLock.unLock(nextElmt);
						return;
					}
					fineLock.unLock(curElmt);
					return;
				}
			}
		}
		public Writer writer(){
			return writer;
		}
		
		public void traverse(){
			//try{
				//fineLock.lock((Element)current());
				super.traverse();
			//fineLock.unLock((Element)current());
		//	}catch(InterruptedException e){
				//fineLock.unLock((Element)current());	
			//}
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
			if(fineLock.lock(curElmt))
			{
				//get the previous element of current element
				Element preElmt = getCursor().current().getPrevElement();
				//check if it is the condition of only one element
				if(curElmt==preElmt){
					super.insertBefore(val);
					fineLock.unLock(curElmt);
					return true;
				}
				else{
					//we have at least two elements
					//lock the previous element
					if(fineLock.lock(preElmt))
					{
						super.insertBefore(val);
				 		fineLock.unLock(curElmt);
						fineLock.unLock(preElmt);
						return true;
					}
					fineLock.unLock(curElmt);
					return false;	
				}
			}
			else return false;
		}//getCursor().current().getPrevElement()
		public boolean insertAfter(T val){
			//get current element which the cursor refer to
			Element curElmt = getCursor().current();
			//lock this element in case of being locked by other threads
			if(fineLock.lock(curElmt))
			{
				//get the previous element of current element
				Element nextElmt = getCursor().current().getNextElement();
				//check if it is the condition of only one element in the list
				if(curElmt==nextElmt){
					super.insertAfter(val);
					fineLock.unLock(curElmt);
					return true;
				}
				else{
					//we have at least two elements
					//lock the next element
					if(fineLock.lock(nextElmt))
					{
						super.insertAfter(val);
						fineLock.unLock(nextElmt);
						fineLock.unLock(curElmt);
						return true;
					}
					fineLock.unLock(curElmt);
					return false;	
				}
			}
			else return false;
		}
	}
	public Cursor reader(Element from){
		Cursor cursor = new Cursor(from);
		return cursor;
	}
}

