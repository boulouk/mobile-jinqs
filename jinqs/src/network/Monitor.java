/**
 * Monitor.java
 * Created on: 15 mars 2016
 */
package network;

import tools.QueueProbs;
import tools.Resource;

/**
 * @author Georgios Bouloukakis (boulouk@gmail.com)
 *
 */
public class Monitor {
	private static Monitor instance = null;

	private QueueProbs probs2;

	protected Monitor() {
		// Exists only to defeat instantiation.
		probs2 = new QueueProbs();
	}

	public static Monitor getInstance() {
		if (instance == null) {
			instance = new Monitor();
		}
		return instance;
	}

	public synchronized void checkOnoffServerStatus() throws InterruptedException {
//		int len = Queue.getPop();
		
		if(Resource.getSemaphore() == 1 && Queue.getPop() == 0){
			probs2.add(0);
		} else 
			probs2.add(Queue.getPop()+1);
		
//		if(Resource.numberOfAvailableResources() == 1) {
//			probs2.add(0);
//		} else 
//			probs2.add(Queue.getPop()+1);
			
		
		
	}

	public QueueProbs getProbs() {
		return probs2;
	}

	
	
	

}
