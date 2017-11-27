package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import network.*;
import tools.*;

class OnOffInsProbsSim extends Sim {

	public static double duration = 0;
	public static Exp serviceTime;
	public static double averageOn = 0;
	public static double averageOff = 0;
	public static double durationOn = 0;
	public static double durationOff = 0;
	public static double noOfCust = 0;
	public static double avgTimeinQueue = 0;
	public static double LambdaRate = 0;
	public static double serviceRate = 0;
	public static double util = 0;

	public static StringBuilder cusProbs;
	public static StringBuilder cusProbsON;
	public static StringBuilder cusProbsOFF;

	public static double meanCusProbs = 0;
	public static double meanCusProbsON = 0;
	public static double meanCusProbsOFF = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}

	public OnOffInsProbsSim(double d) {

		duration = d;

		Network.initialise();

		serviceRate = 20;
		serviceTime = new Exp(serviceRate);

		Exp onlineTime = new Exp(0.001);
		Exp offlineTime = new Exp(0.019);

		Delay serveTime = new Delay(serviceTime);
		LambdaRate = 0.1;
		Source source = new Source("Source", new Exp(LambdaRate));

		OnOffRInsProbsQN mm1 = new OnOffRInsProbsQN("ON-OFF-1", serveTime, 1, onlineTime, offlineTime, 0.5, duration);

		Sink sink = new Sink("Sink");

		source.setLink(new Link(mm1));
		mm1.setLink(new Link(sink));

		simulate();

		averageOn = onlineTime.average();
		averageOff = offlineTime.average();

		System.err.println("ON average: " + averageOn);
		System.err.println("OFF average: " + averageOff);

		// System.out.println("ON duration: " + durationOn);
		// System.out.println("OFF duration: " + durationOff);
		System.out.println("P-model: " + LambdaRate / serviceRate);

		System.out.println("Avg Service Time: " + serviceTime.average());

		Network.logResult("Avg Queue", mm1.meanNoOfQueuedCustomers());

		// Network.logResult("Virtual Service Time",
		// Network.virtualServiceTime.mean());
		// Network.logResult("ST ON", Network.serviceTimeON.mean());
		// Network.logResult("ST OFF", Network.serviceTimeOFF.mean());
		// Network.logResult("ST OFF Service Time",
		// Network.serviceTimeOFFServiceTime.mean());

		Network.logResult("Response Time", Network.responseTime.mean());
		Network.logResult("Utilisation", mm1.serverUtilisation());
		util = mm1.serverUtilisation();
		// Network.logResult("Response Time ON", Network.responseTimeON.mean());
		// Network.logResult("Response Time OFF", Network.responseTimeOFF.mean());

		Network.logResult("Completions", Network.completions);
		Network.logResult("CompletionsON", Network.completionsON);
		Network.logResult("CompletionsOFF", Network.completionsOFF);

		noOfCust = mm1.meanNoOfQueuedCustomers();

		QueueProbs probs = mm1.getQueueProbs();
		QueueProbs probsON = mm1.getQueueProbsON();
		QueueProbs probsOFF = mm1.getQueueProbsOFF();
		
		
		avgTimeinQueue = mm1.meanTimeInQueue();

		cusProbs = probs.getProbabilities();
		System.out.println("-------------probs:");
		System.out.println(cusProbs);

		int sum = probs.getSum();
		
		int sum2 = probsON.getSum() + probsOFF.getSum();
		
		cusProbsON = probsON.getProbabilities(sum2);
		System.out.println(cusProbsON);
		cusProbsOFF = probsOFF.getProbabilities(sum2);
		System.out.println(cusProbsOFF);

		meanCusProbs = probs.getMeanProbability();
		System.out.println("----------mean:");
		System.out.println(meanCusProbs);
		meanCusProbsON = probsON.getMeanProbability(sum2);
		System.out.println(meanCusProbsON);
		meanCusProbsOFF = probsOFF.getMeanProbability(sum2);
		System.out.println(meanCusProbsOFF);
		
		double p01 = probsON.getValue(0)/(double)sum;
		System.out.println("prob_p01:" +p01);
		double p00 = probsOFF.getValue(0)/(double)sum;
		System.out.println("prob_p00:" +p00);
		double p11 = probsON.getValue(1)/(double)sum;
		System.out.println("prob_p11:" +p11);
		double p10 = probsOFF.getValue(1)/(double)sum;
		System.out.println("prob_p10:" +p10);
		double p21 = probsON.getValue(2)/(double)sum;
		System.out.println("prob_p21:" +p21);
		
		System.out.println("1: " + (0.1 * p01) +" = "+ (1+0.1)*p00);
		System.out.println("2: " + ((0.1 * p00) + (8*p11)) +" = "+ (1+0.1)*p01);
		System.out.println("3: " + ((1 * p00) + (0.1*p11)) +" = "+ (1+0.1)*p10);
		
		System.out.println("4: " + ((1 * p01) + (8*p21) + (0.1*p10)) +" = "+ (8+1+0.1)*p11);
		

	}

	public static void main(String args[]) {
//public void start() {
		new OnOffInsProbsSim(5000000);

		Network.displayResults(0.01);
		
		try {

			AnalyticalModelsONOFF an = new AnalyticalModelsONOFF(Network.responseTime.mean(), Network.responseTimeON.mean(),
					Network.responseTimeOFF.mean(), Network.completions, Network.completionsON, Network.completionsOFF, duration,
					serviceTime.average(), Network.virtualServiceTime.mean(), Network.virtualServiceTime.variance(),
					Network.serviceTimeON.mean(), Network.serviceTimeOFF.mean(), durationOn, durationOff, averageOn, averageOff);

			String data = "Lsim: " + an.computeL() + " -- S-sim: " + serviceTime.average() + " -- avgON: " + averageOn
					+ " -- avgOFF: " + averageOff + " -- Arrivals: " + Network.completions + " -- Duration: " + duration;
			// String data = "Lsim: " + an.computeL() + " -- Lon-sim: " +
			// an.computeLon() + " -- Loff-sim: " + an.computeLoff() + " -- S-sim: " +
			// serviceTime.average() + " -- Arrivals: " + Network.completions +
			// " -- Duration: " + duration;

			// String new_metrics = "Lon-model: " + an.computeLOnModel() +
			// " -- Loff-model: " + an.computeLOffModel();
			// String simulator = "R-sim: " + Network.responseTime.mean() +
			// " -- Ns (queue): " + noOfCust;
			String simulator = "R-sim (mean resp time in system): " + Network.responseTime.mean() + " -- R-model (mean resp time in system): " + an.computeR()
					+ " -- R-sim-queue (mean resp time in queue): " + avgTimeinQueue;
			String model = " Q-sim (num of cust in queue): " + noOfCust + " -- Q-sim (num of cust in system): " + an.computeQsim();
			// String onoff = "Ron-sim " + Network.responseTimeON.mean() +
			// " -- Son-sim: " + Network.serviceTimeON.mean() + " -- Roff-sim " +
			// Network.responseTimeOFF.mean() + " -- Soff-sim: " +
			// Network.serviceTimeOFF.mean();
			// String model = "Ron-model: " + an.computeRon() + " -- Roff-model: " +
			// an.computeRoff() + " -- R-model: " + an.computeR();
			String prob = "Psim (server utilization) : " + util;
			// String R_paper = " -- R_paper: " + an.computeR_paper();
			String probab = "Customers Probabilities:\n" + cusProbs;
			String meanProbab = "Mean Customers Prob: " + meanCusProbs;
			String probabON = "Customers Probabilities ON:\n" + cusProbsON;
			String meanProbabON = "Mean Customers Prob ON: " + meanCusProbsON;
			String probabOFF = "Customers Probabilities OFF:\n" + cusProbsOFF;
			String meanProbabOFF = "Mean Customers Prob OFF: " + meanCusProbsOFF;
			File file = new File("results_onoff.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.write("\n");
			// bw.write(new_metrics);
			// bw.write("\n");
			bw.write(simulator);
			bw.write("\n");
			// bw.write(onoff);
			// bw.write("\n");
			bw.write(model);
			bw.write("\n");
			bw.write(prob);
			bw.write("\n");
			bw.write(probab);
			bw.write("\n");
			bw.write(meanProbab);
			bw.write("\n");
			bw.write(probabON);
			bw.write("\n");
			bw.write(meanProbabON);
			bw.write("\n");
			bw.write(probabOFF);
			bw.write("\n");
			bw.write(meanProbabOFF);
			bw.write("\n");
			// bw.write(R_paper);
			// bw.write("\n");
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


