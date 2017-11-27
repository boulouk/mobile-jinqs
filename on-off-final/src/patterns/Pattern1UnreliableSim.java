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


class Pattern1UnreliableSim extends Sim {
	
	public static double duration = 0;
	public static Exp processingServiceTime;
	public static Exp transmissionServiceTime;
	public static double averageLifetime = 0;
	
	public static double averageOnProducerApp = 0;
	public static double averageOffProducerApp = 0;
	public static double averageOnMdw = 0;
	public static double averageOffMdw = 0;
	public static double averageOnConsumerApp = 0;
	public static double averageOffConsumerApp = 0;
	
	public static double noOfCust = 0;
	public static double avgTimeinQueue = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}


	// Here, the constructor starts the simulation.
	public Pattern1UnreliableSim(double d) {

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
		
		Exp ONProd = new Exp(Params.ON_PROD_RATE);
		Exp OFFProd = new Exp(Params.OFF_PROD_RATE);

		Exp ONMdw = new Exp(Params.ON_MDW_RATE);
		Exp OFFMdw = new Exp(Params.OFF_MDW_RATE);
		
		Exp ONCon = new Exp(Params.ON_CON_RATE);
		Exp OFFCon = new Exp(Params.OFF_CON_RATE);
		
		OnOffRQN prod_app = new OnOffRQN("PROD-APP", prMsg, 1, ONProd, OFFProd, duration);
		OnOffUQN prod_mdw = new OnOffUQN("PROD-MDW", trMsg, 1, ONMdw, OFFMdw, duration);
		OnOffUQN con_mdw = new OnOffUQN("CON-MDW", prMsg, 1, ONCon, OFFCon, duration);
		QueueingNode con_app = new QueueingNode("CON-MDW", prMsg, 1);
		
		
		SinkLftLses sinkProdApp = new SinkLftLses("SINK-PROD-APP");
		SinkMdwLses sinkProdMdw = new SinkMdwLses("SINK-PROD-MDW");
		SinkMdwLses sinkConMdw = new SinkMdwLses("SINK-CON-MDW");
		SinkOvrlNet sinkConEnd = new SinkOvrlNet("SINK-CON-END");
		
		LftLsesBranch branchProdApp = new LftLsesBranch(new Node[] {sinkProdApp, prod_mdw});
		MdwLsesBranch branchProdMdw = new MdwLsesBranch(new Node[] {sinkProdMdw, con_mdw}) ;
		MdwLsesBranch branchConMdw = new MdwLsesBranch(new Node[] {sinkConMdw, con_app}) ;

		src.setLink(new Link(prod_app));
		prod_app.setLink(branchProdApp);
		prod_mdw.setLink(branchProdMdw);
		con_mdw.setLink(branchConMdw);
		con_app.setLink(new Link(sinkConEnd));

		simulate();
		
		averageOnProducerApp = ONProd.average();
		averageOffProducerApp = OFFProd.average();
		
		averageOnMdw = ONMdw.average();
		averageOffMdw = OFFMdw.average();
		
		averageOnConsumerApp = ONCon.average();
		averageOffConsumerApp = OFFCon.average();
		
//		averageLifetime = lifetime.average();
		
		System.err.println("ON average Producer App : " + averageOnProducerApp);
		System.err.println("OFF average Producer App : " + averageOffProducerApp);
		
		System.err.println("ON average Mdw : " + averageOnMdw);
		System.err.println("OFF average Mdw : " + averageOffMdw);
		
		System.err.println("ON average Consumer App : " + averageOnConsumerApp);
		System.err.println("OFF average Consumer App : " + averageOffConsumerApp);
	
		System.out.println("Processing Avg Service Time: " + processingServiceTime.average());
		System.out.println("Transmission Avg Service Time: " + transmissionServiceTime.average());
				
		Network.logResult("CompletionsExpired", Network.completionsExpired);
		Network.logResult("ResponseTimeExpired", Network.responseTimeExpired.mean());
		
		Network.logResult("Completions", Network.completions);
		Network.logResult("ResponseTime", Network.responseTime.mean());
		
		System.out.println("SuccesRate: " + ((double)(Network.completions) / (double) (Network.completions + Network.completionsExpired)));
		
//		Network.responseTime.saveResponseMeasures();
	}

	public static void main(String args[]) {
//		new Pattern1UnreliableSim(500000);
		
		
		new Pattern1UnreliableSim(500000);
		
		
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
