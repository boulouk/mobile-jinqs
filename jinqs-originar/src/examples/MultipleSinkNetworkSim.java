package examples;

import network.*;
import tools.*;

class MultipleSinkNetworkSim extends Sim {

	public static double duration = 0;
	public static double noOfCust = 0;
	public static Exp serviceTime1;
	public static Exp serviceTime2;

	public MultipleSinkNetworkSim(double d) {

		duration = d;

		Network.initialise();

		serviceTime1 = new Exp(16);
//		serviceTime2 = new Exp(8);
		
		Delay serveTime1 = new Delay(serviceTime1);
		Delay serveTime2 = new Delay(serviceTime2);
		

		Source source = new Source("Source", new Exp(1));

		QueueingNode mm11 = new QueueingNode("MM11", serveTime1, 1);
//		QueueingNode mm12 = new QueueingNode("MM12", serveTime1, 1);
//		QueueingNode mm13 = new QueueingNode("MM13", serveTime1, 1);
//		QueueingNode mm14 = new QueueingNode("MM14", serveTime1, 1);
		
		
		
		Sink mainSink = new Sink("MainSink");
		Sink tempSink = new Sink("TempSink");
		
		double[] routingProbs = { 1.0/2.0, 1.0/2.0 } ;
	    ProbabilisticBranch sinksLink
	      = new ProbabilisticBranch( routingProbs,
	                                 new Node[] { mainSink, tempSink } ) ;

		source.setLink(new Link(mm11));
		mm11.setLink(sinksLink);
//		mm11.setLink(new Link(mainSink));

		simulate();
		
		Network.logResult("Response Time", Network.responseTime.mean());
		
		Network.logResults();
	}

	public boolean stop() {
		return now() > duration;
	}

	public static void main(String args[]) {
		new MultipleSinkNetworkSim(300000);
		Network.displayResults(0.01);
	}
}

