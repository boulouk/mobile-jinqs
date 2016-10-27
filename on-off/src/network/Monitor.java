/**
 * Monitor.java
 * Created on: 15 mars 2016
 */
package network;

import tools.QueueProbs;
import tools.Resource;
import examples.OnOffQN;

/**
 * @author Georgios Bouloukakis (boulouk@gmail.com)
 *
 */
public class Monitor {
	private static Monitor instance = null;

	private QueueProbs probs;
	private QueueProbs probsON;
	private QueueProbs probsOFF;

	protected Monitor() {
		// Exists only to defeat instantiation.
		probs = new QueueProbs();
		probsON = new QueueProbs();
		probsOFF = new QueueProbs();
	}

	public static Monitor getInstance() {
		if (instance == null) {
			instance = new Monitor();
		}
		return instance;
	}

	public void checkOnoffServerStatus() throws InterruptedException {
		boolean con = OnOffQN.isCon();
		int len = Queue.getPop();
		if (con) {
			if(Resource.getSemaphore() == 1 && Queue.getPop() == 0){
				probsON.add(0);
				probs.add(0);
			} else {
				probsON.add(len + 1);
				probs.add(len + 1);
			}
		} else {
			if(Resource.getSemaphore() == 1 && Queue.getPop() == 0){
				probsOFF.add(0);
				probs.add(0);
			} else {
				probsOFF.add(len + 1);
				probs.add(len + 1);
			}
		}
		
		
	}

	public QueueProbs getProbs() {
		return probs;
	}

	public QueueProbs getProbsON() {
		return probsON;
	}

	public QueueProbs getProbsOFF() {
		return probsOFF;
	}
	
	

}
