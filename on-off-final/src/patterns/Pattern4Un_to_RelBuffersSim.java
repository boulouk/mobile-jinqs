package patterns;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import examples.OnOffRQN;
import examples.OnOffUQN;
import extensions.BothLsesBranch;
import extensions.LftLsesBranch;
import extensions.MdwLsesBranch;
import extensions.SinkBothLses;
import extensions.SinkMdwLses;
import extensions.SinkOvrlNet;
import extensions.SinkLftLses;
import network.*;
import tools.*;


class Pattern4Un_to_RelBuffersSim extends Sim {
	
	public static double duration = 0;
	public static Exp processingServiceTime;
	public static Exp transmissionServiceTime;
	public static double averageLifetime = 0;
	
	public static double averageOnEndToEndLink1 = 0;
	public static double averageOffEndToEndLink1 = 0;
	public static double averageOnEndToEndLink2 = 0;
	public static double averageOffEndToEndLink2 = 0;

	
	public static double noOfCust = 0;
	public static double avgTimeinQueue = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}


	// Here, the constructor starts the simulation.
	public Pattern4Un_to_RelBuffersSim(double d) {

		duration = d;
		int bufferSize = 10;

		Network.initialise();
		
		Source src = new Source("Source", new Exp(15));
		
//		Exp lifetime = new Exp(0.1);
//		Deterministic lifetime = new Deterministic(10);
//		Source src = new Source("Source", new Exp(2), lifetime, "lifetime");
		
		processingServiceTime = new Exp(64);
		transmissionServiceTime = new Exp(32);
		Delay prMsg = new Delay(processingServiceTime);
		Delay trMsg = new Delay(transmissionServiceTime);
	
//		10=0.1
//		15=0.066
//		20=0.05
//		30=0.033
//		40=0.025
//		50=0.02
//		60=0.0166
//		70=0.0142
		
		Exp ONOvrlPubBr = new Exp(0.02);
		Exp OFFOvrlPubBr = new Exp(0.033);
		
		Exp ONOvrlBrSub = new Exp(0.05);
		Exp OFFOvrlBrSub = new Exp(0.1);
		
		FIFOQueue fq1 = new FIFOQueue(bufferSize) ;
		OnOffRQN pub_app = new OnOffRQN("PUB-APP", prMsg, 1, fq1, ONOvrlPubBr, OFFOvrlPubBr, duration);
		FIFOQueue fq2 = new FIFOQueue(bufferSize) ;
		QueueingNode pub_mdw = new QueueingNode("PUB-MDW", trMsg, 1, fq2);
		FIFOQueue fq3 = new FIFOQueue(bufferSize) ;
		QueueingNode br_in = new QueueingNode("BR-IN", prMsg, 1, fq3);
//		FIFOQueue fq4 = new FIFOQueue(bufferSize) ;
		OnOffUQN br_out = new OnOffUQN("BR-OUT", trMsg, 1, ONOvrlBrSub, OFFOvrlBrSub, duration);
		FIFOQueue fq5 = new FIFOQueue(bufferSize) ;
		QueueingNode sub_mdw = new QueueingNode("SUB-MDW", prMsg, 1, fq5);
		FIFOQueue fq6 = new FIFOQueue(bufferSize) ;
		QueueingNode sub_app = new QueueingNode("SUB-APP", prMsg, 1, fq6);
		
		
		SinkLftLses sinkPubApp = new SinkLftLses("SINK-PUB-APP");		
		SinkLftLses sinkBrIn = new SinkLftLses("SINK-BR-IN");
		
		SinkBothLses sinkBrOut = new SinkBothLses("SINK-BR-OUT");
		
		SinkOvrlNet sinkSubEnd = new SinkOvrlNet("SINK-SUB-APP");
	
		LftLsesBranch branchPubApp = new LftLsesBranch(new Node[] {sinkPubApp, pub_mdw});
		LftLsesBranch branchBrIn = new LftLsesBranch(new Node[] {sinkBrIn, br_out});
//		LftLsesBranch branchBrOut = new LftLsesBranch(new Node[] {sinkBrOut, sub_mdw});
		BothLsesBranch branchBrOut = new BothLsesBranch(new Node[] {sinkBrOut, sub_mdw});

		
		src.setLink(new Link(pub_app));
		pub_app.setLink(branchPubApp);
		pub_mdw.setLink(new Link(br_in));
		br_in.setLink(branchBrIn);
		br_out.setLink(branchBrOut);
		sub_mdw.setLink(new Link(sub_app));
		sub_app.setLink(new Link(sinkSubEnd));

		simulate();
		
		averageOnEndToEndLink1 = ONOvrlPubBr.average();
		averageOffEndToEndLink1 = OFFOvrlPubBr.average();
		
		averageOnEndToEndLink2 = ONOvrlBrSub.average();
		averageOffEndToEndLink2 = OFFOvrlBrSub.average();
		
//		averageLifetime = lifetime.average();
		
		System.err.println("ON average End-to-end Link 1 : " + averageOnEndToEndLink1);
		System.err.println("OFF average End-to-end Link 1 : " + averageOffEndToEndLink1);
		
		System.err.println("ON average End-to-end Link 2 : " + averageOnEndToEndLink2);
		System.err.println("OFF average End-to-end Link 2 : " + averageOffEndToEndLink2);
	
		System.out.println("Processing Avg Service Time: " + processingServiceTime.average());
		System.out.println("Transmission Avg Service Time: " + transmissionServiceTime.average());
				
		Network.logResult("CompletionsExpired", Network.completionsExpired);
		Network.logResult("ResponseTimeExpired", Network.responseTimeExpired.mean());
		
		Network.logResult("Completions", Network.completions);
		Network.logResult("ResponseTime", Network.responseTime.mean());
		
//		System.out.println("SuccesRate: " + ((double)(Network.completions) / (double) (Network.completions + Network.completionsExpired)));
		
		double ov_buffer_losses = pub_app.getLosses() + pub_mdw.getLosses() + br_in.getLosses() + br_out.getLosses() + sub_mdw.getLosses() + sub_app.getLosses();
		System.out.println("middleware: " + Network.completionsExpired);
		System.out.println("buffer losses: " + ov_buffer_losses);

		
		System.out.println("SuccesRateBufferLosses: " + ((double)(Network.completions) / (double) (Network.completions + ov_buffer_losses + Network.completionsExpired)));
		
//		Network.responseTime.saveResponseMeasures();
	}

	public static void main(String args[]) {
//		new Pattern1UnreliableSim(500000);
		
		new Pattern4Un_to_RelBuffersSim(200000);
		
		
		Network.displayResults( 0.01 ) ;
		
		Network.logResults();
		
//		try {
//
//			AnalyticalModelsONOFF an = new AnalyticalModelsONOFF(Network.responseTime.mean(), Network.responseTimeON.mean(),
//					Network.responseTimeOFF.mean(), Network.completions, Network.completionsON, Network.completionsOFF, duration,
//					serviceTime.average(), Network.virtualServiceTime.mean(), Network.virtualServiceTime.variance(),
//					Network.serviceTimeON.mean(), Network.serviceTimeOFF.mean(), durationOn, durationOff, averageOn, averageOff);
//
//			String data = "Lsim: " + an.computeL() + " -- S-sim: " + serviceTime.average() + " -- avgON: " + averageOn
//					+ " -- avgOFF: " + averageOff + " -- Arrivals: " + Network.completions + " -- Duration: " + duration;
//
//			String simulator = "R-sim (mean resp time in system): " + Network.responseTime.mean() + " -- R-model (mean resp time in system): " + an.computeR()
//					+ " -- R-sim-queue (mean resp time in queue): " + avgTimeinQueue + " -- R-model-mosxolios (mean resp time in system): " + an.computeR_mosxolios();
//			String model = " Q-sim (num of cust in queue): " + noOfCust + " -- Q-sim (num of cust in system): " + an.computeQsim();
//			
//			String R_paper = " -- R_paper: " + an.computeR_paper();
//			
//			File file = new File("results_onoff.txt");
//
//			if (!file.exists()) {
//				file.createNewFile();
//			}
//
//			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//			BufferedWriter bw = new BufferedWriter(fw);
//			bw.write(data);
//			bw.write("\n");
//			bw.write(simulator);
//			bw.write("\n");
//			bw.write(model);
//			bw.write("\n");
//			bw.write(R_paper);
//			bw.write("\n");
//			bw.close();
//
//			System.out.println("Done");
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
