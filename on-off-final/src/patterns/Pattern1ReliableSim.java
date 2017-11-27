package patterns;

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


class Pattern1ReliableSim extends Sim {
	
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
	public Pattern1ReliableSim(double d) {

		duration = d;

		Network.initialise();
		
//		Source source = new Source("Source", new Exp(1));
		
//		Exp lifetime = new Exp(0.1);
		Deterministic lifetime = new Deterministic(Params.LIFETIME);
		Source src = new Source("Source", new Exp(Params.SRC_RATE), lifetime, "lifetime");
		
		processingServiceTime = new Exp(64);
		transmissionServiceTime = new Exp(16);
		Delay prMsg = new Delay(processingServiceTime);
		Delay trMsg = new Delay(transmissionServiceTime);
		
//		Delay prMsg = new Delay(new Exp(Params.PR_MSG_RATE));
//		Delay trMsg = new Delay(new Exp(Params.TR_MSG_RATE));
		
		Exp ONOvrl = new Exp(Params.ON_OVRL_RATE);
		Exp OFFOvrl = new Exp(Params.OFF_OVRL_RATE);
		
		OnOffRQN prod_app = new OnOffRQN("PROD-APP", prMsg, 1, ONOvrl, OFFOvrl, duration);
		QueueingNode prod_mdw = new QueueingNode("PRO-MDW", trMsg, 1);
		QueueingNode con_mdw = new QueueingNode("CON-MDW", prMsg, 1);
		QueueingNode con_app = new QueueingNode("CON-MDW", prMsg, 1);
		
		
		SinkLftLses sinkProdApp = new SinkLftLses("SINK-PROD-APP");
		SinkOvrlNet sinkConEnd = new SinkOvrlNet("SINK-CON-END");
		
		LftLsesBranch branchProdApp = new LftLsesBranch(new Node[] {sinkProdApp, prod_mdw});

		src.setLink(new Link(prod_app));
		prod_app.setLink(branchProdApp);
		prod_mdw.setLink(new Link(con_mdw));
		con_mdw.setLink(new Link(con_app));
		con_app.setLink(new Link(sinkConEnd));

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
		
		
		new Pattern1ReliableSim(500000);
		
		
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
