/**
 * Monitor.java
 * Created on: 15 mars 2016
 */
package network;

import tools.QueueProbs;
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
		if (con)
			probsON.add(len);
		else
			probsOFF.add(len);

		probs.add(len);
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
