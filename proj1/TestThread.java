//Filename: TestThread.java
//Function: some test threads declaration 
//Author:   jiangchuan he
//UBemail: 	jiangchu@buffalo.edu
//history:  09/21/13

package testMain;



class TestThread<T> extends Thread{
	private CDLList<T>.Cursor cursor;
	private T data;
	public TestThread(CDLList<T>.Cursor C, T D){
		assert(C!=null);
		cursor = C;
		data = D;
	}

	public void run(){
		assert(cursor!=null);
		for(int i=0; i<10; i++){
			cursor.previous();
			for(;;)
				if(cursor.writer().insertBefore(data)==true)
					break;
				
			cursor.next();
		}	
	}
}
	
class TestThread2<T> extends Thread{
	private CDLList<T>.Cursor cursor;
	//private T data;
	public TestThread2(CDLList<T>.Cursor C, T D){
		assert(C!=null);
		cursor = C;
	//	data = D;
	}

	public void run(){
		assert(cursor!=null);
		for(int i=0; i<100000; i++){
			cursor.next();
		}	
	}
}
//********************************************************************//
class NormalThread extends Thread {
    
    CDLList<String> cdl;
    int id;
    CDLList<String>.Cursor cursor;
    public NormalThread(CDLList<String> cdl, int id) {
        this.id = id;
        this.cdl = cdl;
        cursor = cdl.reader(cdl.head());
    }

    @Override
    public void run() {

        int offset = id * 2;
        for(int i = 0; i < offset; i++) {
            cursor.next();
        }
        for(int i=0; i<10000; i++){
        	cursor.writer().insertBefore("(IB - " + id+")");
        	cursor.writer().insertAfter("(IA - " + id+ ")");
        	cursor.next();
        }
    }

}

//INCLUDE PACKAGENAME HERE
class RandomThread extends Thread {

  CDLList<String> cdl;
  CDLList<String>.Cursor cursor;
  public RandomThread(CDLList<String> cdl) {
      this.cdl = cdl;
  }
  
  public void run() {
      cursor = cdl.reader(cdl.head());
      for(int i = 0;i < 10000;i++) {
          double temp = java.lang.Math.random();
          int rand = (int)(temp*10)%4;


          switch(rand) {
          case 0:
              cursor.next();// Go to the next 
              break;
          case 1:
              cursor.previous();
              break;    
          case 2:
              cursor.writer().insertBefore("(RB)");
              break;
          case 3:
              cursor.writer().insertBefore("(RA)");
              break;
          default:
              break;
          }
          yield();
      }
  }
}
