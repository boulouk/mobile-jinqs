package examples;

import network.*;
import tools.*;

class MM1NetworkSim extends Sim {

	public static double duration = 0;
	public static double noOfCust = 0;
	public static Exp serviceTime1;
	public static Exp serviceTime2;

	public MM1NetworkSim(double d) {

		duration = d;

		Network.initialise();

		serviceTime1 = new Exp(8);
//		serviceTime2 = new Exp(8);
		
		Delay serveTime1 = new Delay(serviceTime1);
		Delay serveTime2 = new Delay(serviceTime2);
		

		Source source = new Source("Source", new Exp(1));

		QueueingNode mm11 = new QueueingNode("MM11", serveTime1, 1);
		QueueingNode mm12 = new QueueingNode("MM12", serveTime1, 1);
		QueueingNode mm13 = new QueueingNode("MM13", serveTime1, 1);
		QueueingNode mm14 = new QueueingNode("MM14", serveTime1, 1);
		
		
		Sink sink = new Sink("Sink");

		source.setLink(new Link(mm11));
		mm11.setLink(new Link(mm12));
		mm12.setLink(new Link(mm13));
		mm13.setLink(new Link(mm14));
		mm14.setLink(new Link(sink));

		simulate();
		
		Network.logResult("Response Time", Network.responseTime.mean());
		
	}

	public boolean stop() {
		return now() > duration;
	}

	public static void main(String args[]) {
		new MM1NetworkSim(3000000);
		Network.displayResults(0.01);
	}
}

