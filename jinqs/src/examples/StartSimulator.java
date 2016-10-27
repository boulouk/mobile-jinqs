/**
 * StartSimulator.java
 * Created on: 15 mars 2016
 */
package examples;

import java.util.concurrent.TimeUnit;

import tools.QueueProbs;
import network.Monitor;
import network.Network;

/**
 * @author Georgios Bouloukakis (boulouk@gmail.com)
 *
 */
public class StartSimulator {

	public static void main(String args[]) {
		Runnable r0, r1;

		r0 = new SimulatorTh(); // init Runnable
		r1 = new MonitorTh(); // init Runnable

		final Thread t0, t1;

		t0 = new Thread(r0);// init thread object, but haven't started yet
		t1 = new Thread(r1);// init thread object, but haven't started yet

		t0.start();// start the thread simultaneously
		t1.start();// start the thread simultaneously

	}

}

class SimulatorTh implements Runnable {

	public static volatile Boolean flagON = true;

	public SimulatorTh() {

	}

	public void run() {
		MM1Sim mm1start = new MM1Sim();
		mm1start.start();
		setFlagON(false);
		// new OnOffSim(1000000);
	}

	public static Boolean getFlagON() {
		return flagON;
	}

	public static void setFlagON(Boolean flagON) {
		SimulatorTh.flagON = flagON;
	}

}

class MonitorTh implements Runnable {
	public static StringBuilder cusProbs2;
	
	public static double meanCusProbs2 = 0;
	

	public MonitorTh() {
		
	}

	public void run() {
		
		Monitor monitor = Monitor.getInstance();
		try {
			
				while (SimulatorTh.getFlagON()) {
					if(Network.completions > 2) {
						monitor.checkOnoffServerStatus();
						Thread.sleep(1);
//						TimeUnit.NANOSECONDS.sleep(1);
					}
				}
			
			QueueProbs probs = monitor.getProbs();
			

			cusProbs2 = probs.getProbabilities();
			System.out.println("----NEWWAY------probs:");
			System.out.println(cusProbs2);
			
			meanCusProbs2 = probs.getMeanProbability();
			System.out.println("----NEWWAY------mean:");
			System.out.println(meanCusProbs2);
	
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}