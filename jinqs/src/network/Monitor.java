/**
 * Monitor.java
 * Created on: 15 mars 2016
 */
package network;

import tools.QueueProbs;

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

	public void checkOnoffServerStatus() throws InterruptedException {
		int len = Queue.getPop();
		probs2.add(len);
	}

	public QueueProbs getProbs() {
		return probs2;
	}

	
	
	

}
