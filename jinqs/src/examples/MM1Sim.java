package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import network.*;
import tools.*;

class MM1Sim extends Sim {

	public static double duration = 0;
	public static double noOfCust = 0;
	public static Exp serviceTime;

	public MM1Sim(double d) {

		duration = d;

		Network.initialise();

		serviceTime = new Exp(8);
		Delay serveTime = new Delay(serviceTime);

		Source source = new Source("Source", new Exp(2));

		QueueingNode mm1 = new QueueingNode("MM1", serveTime, 1);
		Sink sink = new Sink("Sink");

		source.setLink(new Link(mm1));
		mm1.setLink(new Link(sink));

		simulate();

		Network.logResult("Utilisation", mm1.serverUtilisation());
		Network.logResult("Avg Queue", mm1.meanNoOfQueuedCustomers());
		Network.logResult("Response Time", Network.responseTime.mean());
		
		noOfCust = mm1.meanNoOfQueuedCustomers();
	}

	public boolean stop() {
		return now() > duration;
	}

	public static void main(String args[]) {
		new MM1Sim(2000000);
		Network.displayResults(0.01);

		try {

			AnalyticalModelsMM1 an = new AnalyticalModelsMM1(Network.responseTime.mean(), Network.completions, duration, serviceTime.average());

			String data = "L: " + an.computeL() + " -- Sft: " + serviceTime.average() + " -- Arrivals: " + Network.completions + " -- Duration: " + duration;
			String simulator = "Rs: " + Network.responseTime.mean() + " -- Ss: " + an.computeS() + " -- Ns (num of cust in the queue): " + noOfCust;
			String model = "Rm: " + an.computeR_Sft()  + " -- Nm: " + an.computeN();

			File file = new File("mm1.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.write("\n");
			bw.write(simulator);
			bw.write("\n");
			bw.write(model);
			bw.write("\n");
			bw.write("\n");
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
