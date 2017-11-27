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


class Pattern4UnreliableSim extends Sim {
	
	public static double duration = 0;
	public static Exp processingServiceTime;
	public static Exp transmissionServiceTime;
	public static double averageLifetime = 0;
	
	public static double averageOnPublisherApp = 0;
	public static double averageOffPublisherApp = 0;
	public static double averageOnMdwLink1 = 0;
	public static double averageOffMdwLink1 = 0;
	public static double averageOnMdwLink2 = 0;
	public static double averageOffMdwLink2 = 0;
	public static double averageOnSubscriberApp = 0;
	public static double averageOffSubscriberApp = 0;
	
	public static double noOfCust = 0;
	public static double avgTimeinQueue = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}


	// Here, the constructor starts the simulation.
	public Pattern4UnreliableSim(double d) {

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
		
		Exp ONPub = new Exp(Params.ON_PUB_RATE);
		Exp OFFPub = new Exp(Params.OFF_PUB_RATE);

		Exp ONMdwLink1 = new Exp(Params.ON_MDW_LINK1_RATE);
		Exp OFFMdwLink1 = new Exp(Params.OFF_MDW_LINK1_RATE);
		
		Exp ONMdwLink2 = new Exp(Params.ON_MDW_LINK2_RATE);
		Exp OFFMdwLink2 = new Exp(Params.OFF_MDW_LINK2_RATE);
		
		Exp ONSub = new Exp(Params.ON_SUB_RATE);
		Exp OFFSub = new Exp(Params.OFF_SUB_RATE);
		
		
		OnOffRQN pub_app = new OnOffRQN("PUB-APP", prMsg, 1, ONPub, OFFPub, duration);
		OnOffUQN pub_mdw = new OnOffUQN("PUB-MDW", trMsg, 1, ONMdwLink1, OFFMdwLink1, duration);
		
		QueueingNode br_in = new QueueingNode("BR-IN", prMsg, 1);
		OnOffUQN br_out = new OnOffUQN("BR-OUT", trMsg, 1, ONMdwLink2, OFFMdwLink2, duration);
		
		OnOffUQN sub_mdw = new OnOffUQN("SUB-MDW", prMsg, 1, ONSub, OFFSub, duration);
		QueueingNode sub_app = new QueueingNode("SUB-APP", prMsg, 1);
		
		
		SinkLftLses sinkPubApp = new SinkLftLses("SINK-PUB-APP");
		SinkMdwLses sinkPubMdw = new SinkMdwLses("SINK-PUB-MDW");
		
		SinkLftLses sinkBrIn = new SinkLftLses("SINK-BR-IN");
		SinkBothLses sinkBrOut = new SinkBothLses("SINK-BR-OUT");
		
		SinkMdwLses sinkSubMdw = new SinkMdwLses("SINK-SUB-MDW");
		SinkOvrlNet sinkSubEnd = new SinkOvrlNet("SINK-SUB-END");
		
		LftLsesBranch branchPubApp = new LftLsesBranch(new Node[] {sinkPubApp, pub_mdw});
		MdwLsesBranch branchPubMdw = new MdwLsesBranch(new Node[] {sinkPubMdw, br_in});
		
		LftLsesBranch branchBrIn = new LftLsesBranch(new Node[] {sinkBrIn, br_out});
		BothLsesBranch branchBrOut = new BothLsesBranch(new Node[] {sinkBrOut, sub_mdw});

		MdwLsesBranch branchSubMdw = new MdwLsesBranch(new Node[] {sinkSubMdw, sub_app});

		src.setLink(new Link(pub_app));
		pub_app.setLink(branchPubApp);
		pub_mdw.setLink(branchPubMdw);
		
		br_in.setLink(branchBrIn);
		br_out.setLink(branchBrOut);
		
		sub_mdw.setLink(branchSubMdw);
		sub_app.setLink(new Link(sinkSubEnd));

		simulate();
		
		averageOnPublisherApp = ONPub.average();
		averageOffPublisherApp = OFFPub.average();
		
		averageOnMdwLink1 = ONMdwLink1.average();
		averageOffMdwLink1 = OFFMdwLink1.average();
		
		averageOnMdwLink2 = ONMdwLink2.average();
		averageOffMdwLink2 = OFFMdwLink2.average();
		
		averageOnSubscriberApp = ONSub.average();
		averageOffSubscriberApp = OFFSub.average();
		
//		averageLifetime = lifetime.average();
		
		System.err.println("ON average Publisher App : " + averageOnPublisherApp);
		System.err.println("OFF average Publisher App : " + averageOffPublisherApp);
		
		System.err.println("ON average Mdw Link 1 : " + averageOnMdwLink1);
		System.err.println("OFF average Mdw Link 1 : " + averageOffMdwLink1);
		
		System.err.println("ON average Mdw Link 2 : " + averageOnMdwLink2);
		System.err.println("OFF average Mdw Link 2 : " + averageOffMdwLink2);
		
		System.err.println("ON average Subscriber App : " + averageOnSubscriberApp);
		System.err.println("OFF average Subscriber App : " + averageOffSubscriberApp);
	
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
		
		
		new Pattern4UnreliableSim(500000);
		
		
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
