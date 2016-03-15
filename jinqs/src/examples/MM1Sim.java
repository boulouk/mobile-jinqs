package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import network.*;
import tools.*;

class MM1Sim extends Sim {

	public static double duration = 0;
	public static double noOfCust = 0;
	public static double util = 0;
	public static Exp serviceTime;
	public static double lambdaRate = 0;
	public static double serviceRate = 0;
	public static StringBuilder cusProbs;
	public static double meanCusProbs = 0;
	
	public MM1Sim() {
		
	}

	public MM1Sim(double d) {

		duration = d;

		Network.initialise();

		serviceRate = 8;
		serviceTime = new Exp(serviceRate);
		Delay serveTime = new Delay(serviceTime);

		lambdaRate = 4;
		Source source = new Source("Source", new Exp(lambdaRate));

		QueueingNode mm1 = new QueueingNode("MM1", serveTime, 1);
		Sink sink = new Sink("Sink");

		source.setLink(new Link(mm1));
		mm1.setLink(new Link(sink));

		simulate();

		QueueProbs probs = mm1.getQueueProbs();

		Network.logResult("Utilisation", mm1.serverUtilisation());
		Network.logResult("Avg Queue", mm1.meanNoOfQueuedCustomers());
		Network.logResult("Response Time", Network.responseTime.mean());

		cusProbs = probs.getProbabilities();
		System.out.println(cusProbs);

		meanCusProbs = probs.getMeanProbability();
		System.out.println(meanCusProbs);

		noOfCust = mm1.meanNoOfQueuedCustomers();
		util = mm1.serverUtilisation();
	}

	public boolean stop() {
		return now() > duration;
	}

//	public static void main(String args[]) {
	public void start() {
		new MM1Sim(900000);
		Network.displayResults(0.01);

		try {

			AnalyticalModelsMM1 an = new AnalyticalModelsMM1(Network.responseTime.mean(), Network.completions, duration, serviceTime.average(),
					lambdaRate, serviceRate);

			String data = "LambdaRate: " + lambdaRate + " - ServiceTime: " + 1 / serviceRate + " - Completions: " + Network.completions
					+ " - Duration: " + duration;
			String model = "Rmodel: " + an.computeRmodel() + " - QModel (no-of-cust in the system): " + an.computeQModel() + " - Pmodel: "
					+ an.computePModel();
			String simulator = "Lsim: " + an.computeLsim() + " - Ssim: " + serviceTime.average();
			String resutlsSim = "Rsim: " + Network.responseTime.mean() + " - QSim (no-of-cust in the system): " + an.computeQsim() + " - PSim: "
					+ util;
			String other = "no-of-cust (in the queue): " + noOfCust;
			String probab = "Customers Probabilities:\n" + cusProbs;
			String meanProbab = "Mean Customers Prob: " + meanCusProbs;

			File file = new File("mm1.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.write("\n");
			bw.write(model);
			bw.write("\n");
			bw.write(simulator);
			bw.write("\n");
			bw.write(resutlsSim);
			bw.write("\n");
			bw.write(other);
			bw.write("\n");
			bw.write(probab);
			bw.write("\n");
			bw.write(meanProbab);
			bw.write("\n");
			bw.write("\n");
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
