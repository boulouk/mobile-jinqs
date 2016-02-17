package examples;

import network.*;
import tools.*;

class MM1MultipleClassSim extends Sim {

	public static double duration = 0;
	public static double noOfCust = 0;
	public static Exp serviceTime1;
	public static Exp serviceTime2;

	public MM1MultipleClassSim(double d) {

		duration = d;

		Network.initialise();

		serviceTime1 = new Exp(8);
		serviceTime2 = new Exp(8);
		int[] cs = {1,2};
		DistributionSampler[] ds = {serviceTime1, serviceTime2};
		
		ClassDependentDelay serveTime = new ClassDependentDelay(cs , ds);
		

		Source source1 = new Source("Source", new Exp(2), 1);
		Source source2 = new Source("Source", new Exp(2), 2);

		QueueingNode mm1 = new QueueingNode("MM1", serveTime, 1);
		Sink sink = new Sink("Sink");

		source1.setLink(new Link(mm1));
		source2.setLink(new Link(mm1));
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
		new MM1MultipleClassSim(3000000);
		Network.displayResults(0.01);
	}
}

