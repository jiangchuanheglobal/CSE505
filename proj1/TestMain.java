//Filename: TestMain.java
//Function: entry of this program, main method
//Author:   jiangchuan he
//UBemail: 	jiangchu@buffalo.edu
//history:  09/21/13

package testMain;


import java.util.List;
import java.util.ArrayList;


public class TestMain{
	public static void main(String args[]){
//      USE THIS FOR COARSELIST AND FINELIST WITH MODIFICATIONS
     
      CDLList<String> list = new CDLCoarse<String>("hi");
      CDLList<String>.Element head = list.head();
      CDLList<String>.Cursor c = list.reader(head);
     
      for(int i = 74; i >= 65; i--) {
          char val = (char) i;
         c.writer().insertAfter("" + val);
      }
      
      List<Thread> threadList = new ArrayList<Thread>();
      for (int i = 0; i < 10; i++) {
          NormalThread nt = new NormalThread(list, i);
          threadList.add(nt);
      }
          
	RandomThread rt = new RandomThread(list);
	threadList.add(rt);
	//begin timer and start threads1
	long begin = System.currentTimeMillis();  
      try {
          for(Thread t : threadList){
          	t.start();
          }
          for (Thread t : threadList) {
          	t.join();
          }
      } catch(InterruptedException e) {
          System.err.println(e.getMessage());
          e.printStackTrace();
      }
    //end timer
  	long time = System.currentTimeMillis() - begin; 
  	System.out.printf("time consuming£º%dms\n", time);
  
  	//c.traverse();
  	
  	//list.reader(list.head()).traverse();
  	System.out.print(list.reader(list.head()).getLength());
  	//  YOU MAY WANT TO INCLUDE A PRINT METHOD TO VIEW ALL THE ELEMENTS
  	//      list.print();
	}
}
