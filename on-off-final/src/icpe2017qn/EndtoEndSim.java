package icpe2017qn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import examples.OnOffRQN;
import examples.OnOffUQN;
import extensions.LftLsesBranch;
import extensions.MdwLsesBranch;
import extensions.SinkMdwLses;
import extensions.SinkOvrlNet;
import extensions.SinkLftLses;
import network.*;
import tools.*;


class EndtoEndSim extends Sim {
	
	public static double duration = 0;
	public static Exp processingServiceTime;
	public static Exp transmissionServiceTime;
	public static double averageLifetime = 0;
	
	public static double averageOnEndToEnd = 0;
	public static double averageOffEndToEnd = 0;
	
	public static double noOfCust = 0;
	public static double avgTimeinQueue = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}


	// Here, the constructor starts the simulation.
	public EndtoEndSim(double d) {

		duration = d;

		Network.initialise();
		
		double lambda = 3.9;
		int servers = 2;
		
		Source source = new Source("Source", new Exp(lambda));
		
		processingServiceTime = new Exp(16);
		transmissionServiceTime = new Exp(8);
		Delay prMsg = new Delay(processingServiceTime);
		Delay trMsg = new Delay(transmissionServiceTime);

		Exp ONOvrl = new Exp(0.01666);
		Exp OFFOvrl = new Exp(0.01666);
		
		
		QueueingNode b3_in = new QueueingNode("B3-IN", prMsg, servers);
		QueueingNode b3_out = new QueueingNode("B3-OUT", trMsg, 1);
		
		QueueingNode b10_in = new QueueingNode("B10-IN", prMsg, servers);
		QueueingNode b10_out = new QueueingNode("B10-OUT", trMsg, 1);
		
		QueueingNode b19_in = new QueueingNode("B19-IN", prMsg, servers);
		OnOffRQN b19_out = new OnOffRQN("B19-OUT", trMsg, 1, ONOvrl, OFFOvrl, duration);
			
		SinkOvrlNet sinkEnd = new SinkOvrlNet("SINK-CON-END");
		
		source.setLink(new Link(b3_in));
		b3_in.setLink(new Link(b3_out));
		b3_out.setLink(new Link(b10_in));
		b10_in.setLink(new Link(b10_out));
		b10_out.setLink(new Link(b19_in));
		b19_in.setLink(new Link(b19_out));
		b19_out.setLink(new Link(sinkEnd));
		

		simulate();
		
		averageOnEndToEnd = ONOvrl.average();
		averageOffEndToEnd = OFFOvrl.average();
		
		
//		averageLifetime = lifetime.average();
		
		System.err.println("ON average End-to-end : " + averageOnEndToEnd);
		System.err.println("OFF average End-to-end : " + averageOffEndToEnd);
		
	
		System.out.println("Processing Avg Service Time: " + processingServiceTime.average());
		System.out.println("Transmission Avg Service Time: " + transmissionServiceTime.average());
				
		Network.logResult("CompletionsExpired", Network.completionsExpired);
		Network.logResult("ResponseTimeExpired", Network.responseTimeExpired.mean());
		
		Network.logResult("Completions", Network.completions);
		Network.logResult("ResponseTime", Network.responseTime.mean());
//		
		System.out.println("SuccesRate: " + ((double)(Network.completions) / (double) (Network.completions + Network.completionsExpired)));
		
//		Network.responseTime.saveResponseMeasures();
	}

	public static void main(String args[]) {
//		new Pattern1UnreliableSim(500000);
	
		new EndtoEndSim(500000);
		
		Network.displayResults( 0.01 ) ;
		
		Network.logResults();
	}
}
