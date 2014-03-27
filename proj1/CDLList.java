//Filename: CDLList.java
//Function: base class of this project
//Author:   jiangchuan he
//UBemail: 	jiangchu@buffalo.edu
//history:  09/21/13

package testMain;



public class CDLList<T>{
	//each CDLList object need to have a head variable refers to the head of the list  
	private Element head;
	//CDLList constructor, need to create a head element when being instantiated
	public CDLList(T V){
		head = new Element(V);
		//initialize prev and next member variable of head object.
		head.setPrevElement(head);
		head.setNextElement(head);
		
	}
	//class Element, implements the function of node
	public class Element{
		//data stores the value of element
		protected T data;
		//next and prev object point to the previous and next element
		private Element next;
		private Element prev;
		//isLocked variable is for future use (fine grained locking)
		//note: in fact designing a isLocked variable is the easiest way i find on implementing fine grained locking
		//actually, since you must note whether a specific element is currently being locked or not, then you must 
		//assign a variable to it to mark the locking state
		//if not, you might need to use hashcode or something else to achieve that
		public volatile boolean isLocked;
		//this field is reserved for future R/W locked based on per element
		//it records the number of read locking times
		//specifically, this field allows multiply readers get the access, while only one writer can get
		//the access
		public volatile int	cntLocked;
		//fair lock boolean variable for future use
		public volatile boolean fairness;
		//constructor of class element
		public Element(T D){
			data=D;
			next=null;
			prev=null;
			isLocked = false;
			cntLocked = 0;
			fairness = false;
		}
		//return the value stored in element object
		public T value(){return data;}
		//set the next element object of current element 
		public void setNextElement(Element e)
		{next = e;}	
	        //return the next element 
		public Element getNextElement()
		{return next;}
		//set the previous object of current element
		public void setPrevElement(Element e)
		{prev=e;}
		//return the previous element
		public Element getPrevElement()
		{return prev;}
		//set the value of element
		public void setData(T D)
		{data = D;}
			
	}
	
	//class Cursor, move cursor to a specified position
	public class Cursor{
		//cursor object refers to the specific element object currently it points to
		private Element curElmt;
		//it seems in the project specification, there is no explicit requirement on whether a writer should be fixed to a element after being created
		//in my mind, i prefer it's associated with a cursor, both of them move together, which is similar to a text editor:)
		private Writer  writer;
		//constructor of Cursor class	
		public Cursor(Element e){
			assert(e!=null);
			curElmt = e;
			writer = new Writer(this);
		}
		//return the current element the cursor points to
		public Element current(){return curElmt;}
		//move the cursor one step forward
		public void previous(){curElmt = curElmt.getPrevElement();}
		//move the cursor one step afterward
		public void next(){curElmt = curElmt.getNextElement();}
		//as what I said above, I don't want to make as many writer object as we want. I just think each cursor has a writer object, so that it
		//can modify the list.
		//maybe because java has a garbage collector, so we don't need to care much on the memory allocation and thus there is no risk on making
		//many new object.
		//however, for a c++ programmer like me, I don't like to use much new operation:)
		public Writer writer(){return writer;}
		//an extra test function	
		public void traverse(){
			Element tmp = head;
			do{
				System.out.print(tmp.value());
				tmp = tmp.getNextElement();
			}while(tmp!=head);
			
		}
		//get the length of list, used to test this program
		public int getLength(){
			Element tmp = head;
			int number=0;
			do{
				tmp = tmp.getNextElement();
				number++;
			}while(tmp!=head);
			return number;
		}
	}
	//inner class writer
	public class Writer{
		//store a reference of its cursor object
		private Cursor cursor;
		//constructor of writer	
		public  Writer(Cursor c){cursor = c;}
		//return writer's cursor reference
		public Cursor getCursor(){assert(cursor!=null);return cursor;}
		//insert a element before current element
		public boolean insertBefore(T val){
			Element curElmt = cursor.current();
			Element newElmt = new Element(val);
			Element preElmt = curElmt.getPrevElement();
			preElmt.setNextElement(newElmt);
			curElmt.setPrevElement(newElmt);
			newElmt.setPrevElement(preElmt);
			newElmt.setNextElement(curElmt);
			return true;
		}
		//insert a element after current element
		public boolean insertAfter(T val){
			Element curElmt = cursor.current();
			Element newElmt = new Element(val);	
			Element nextElmt = curElmt.getNextElement();
			newElmt.setNextElement(nextElmt);
			nextElmt.setPrevElement(newElmt);
			curElmt.setNextElement(newElmt);
			newElmt.setPrevElement(curElmt);
			return true;
		}
	}
	//return the head element of the list
	public Element head(){return head;}
	//reader method will return a new cu8rsor when each call to it
	public Cursor reader(Element from){
		//create cursor object and let it point to head element position
		Cursor cursor = new Cursor(from);
		return cursor;
	}
}

