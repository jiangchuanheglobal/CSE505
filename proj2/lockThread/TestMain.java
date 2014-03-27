//Filename: TestMain.java
//Function: entry of this program, main method
//Author:   jiangchuan he
//UBemail: 	jiangchu@buffalo.edu
//history:  09/21/13

package lockThread;


import java.util.List;
import java.util.ArrayList;


public class TestMain{
	public static void main(String args[]){
		
//		        USE THIS FOR COARSELIST AND FINELIST WITH MODIFICATIONS
		        CDLList<String> list = new CDLCoarseRW<String>("hi");
		        CDLList<String>.Element head = list.head();
		        CDLList<String>.Cursor c = list.reader(list.head());
		        
		        for(int i = 74; i >= 65; i--) {
		            char val = (char) i;
		            c.writer().insertAfter("" + val);
		        }
		        
		        List<Thread> threadList = new ArrayList<Thread>();
		        for (int i = 0; i < 500; i++) {
		            NormalThread nt = new NormalThread(list, i);
		            threadList.add(nt);
		        }
		            
			RandomThread rt = new RandomThread(list);
			threadList.add(rt);
			
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
		        System.out.print(list.reader(list.head()).getLength());
		        //c.traverse();
//		    YOU MAY WANT TO INCLUDE A PRINT METHOD TO VIEW ALL THE ELEMENTS
//		        list.print();

	}
}