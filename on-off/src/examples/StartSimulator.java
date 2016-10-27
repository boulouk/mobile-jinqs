/**
 * StartSimulator.java
 * Created on: 15 mars 2016
 */
package examples;

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
		OnOffSim onOff = new OnOffSim();
//		onOff.start();
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
	public static StringBuilder cusProbs;
	public static StringBuilder cusProbsON;
	public static StringBuilder cusProbsOFF;
	
	public static double meanCusProbs = 0;
	public static double meanCusProbsON = 0;
	public static double meanCusProbsOFF = 0;
	

	public MonitorTh() {
		
	}

	public void run() {
		Monitor monitor = Monitor.getInstance();
		try {
			while (SimulatorTh.getFlagON()) {
				if(Network.completions > 2) {
					monitor.checkOnoffServerStatus();
					Thread.sleep(2);
				}
			}
			
			QueueProbs probs = monitor.getProbs();
			QueueProbs probsON = monitor.getProbsON();
			QueueProbs probsOFF = monitor.getProbsOFF();
			

			cusProbs = probs.getProbabilities();
			System.out.println("----NEWWAY------probs:");
			System.out.println(cusProbs);
			
			meanCusProbs = probs.getMeanProbability();
			System.out.println("----NEWWAY------mean:");
			System.out.println(meanCusProbs);
	
			int sum = probs.getSum();
			cusProbsON = probsON.getProbabilities(sum);
			System.out.println("----NEWWAY------ON probs:");
			System.out.println(cusProbsON);
			
			meanCusProbsON = probsON.getMeanProbability(sum);
			System.out.println("----NEWWAY------ON mean:");
			System.out.println(meanCusProbsON);
			
			
			System.out.println("----NEWWAY------OFF probs:");
			cusProbsOFF = probsOFF.getProbabilities(sum);
			System.out.println(cusProbsOFF);
	
			System.out.println("----NEWWAY------OFF mean:");
			meanCusProbsOFF = probsOFF.getMeanProbability(sum);
			System.out.println(meanCusProbsOFF);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}