package examples;

import extensions.LftLsesBranch;
import extensions.SinkOvrlNet;
import extensions.SinkLftLses;
import network.*;
import tools.*;

class BranchesMM1NetworkSim extends Sim {

	public static double duration = 0;
	public static double noOfCust = 0;
	public static Exp serviceTime1;
	public static Exp serviceTime2;

	public BranchesMM1NetworkSim(double d) {

		duration = d;

		Network.initialise();

		serviceTime1 = new Exp(8);
//		serviceTime2 = new Exp(8);
		
		Delay serveTime1 = new Delay(serviceTime1);
		Delay serveTime2 = new Delay(serviceTime2);
		
		Deterministic lifetime = new Deterministic(0.2);

//		Source source = new Source("Source", new Exp(2));
//		
		Source source = new Source("Source", new Exp(3), lifetime, "lifetime");


		QueueingNode mm11 = new QueueingNode("MM11", serveTime1, 1);
		QueueingNode mm12 = new QueueingNode("MM12", serveTime1, 1);
//		QueueingNode mm13 = new QueueingNode("MM13", serveTime1, 1);
//		QueueingNode mm14 = new QueueingNode("MM14", serveTime1, 1);
		
		
		
//		Sink mainSink = new Sink("MainSink");
//		Sink tempSink = new Sink("TempSink");
		
		SinkLftLses ltLossesSink = new SinkLftLses("LifetimeLossesSINK");
		SinkOvrlNet mainSink = new SinkOvrlNet("MainSink");
		
//		double[] routingProbs = { 1.0/2.0, 1.0/2.0 } ;
//	    ProbabilisticBranch sinksLink
//	      = new ProbabilisticBranch( routingProbs,
//	                                 new Node[] {ltLossesSink, mainSink } ) ;
	    
		// In LifetimeLossesBranch (Node1, Node2), I have to set the Node1, as the node the expired messages will be sent
		// and Node2 as the node that the non-expired messages continue to the network
		LftLsesBranch branchLink = new LftLsesBranch(new Node[] {ltLossesSink, mm12 } ) ;

		source.setLink(new Link(mm11));
		mm11.setLink(branchLink);
		mm12.setLink(new Link(mainSink));
//		mm11.setLink(new Link(mainSink));

		simulate();
		
		Network.logResult("Response Time", Network.responseTime.mean());
		
		Network.logResults();
	}

	public boolean stop() {
		return now() > duration;
	}

	public static void main(String args[]) {
		new BranchesMM1NetworkSim(300000);
		Network.displayResults(0.01);
	}
}

