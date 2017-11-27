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


class Pattern2UnreliableSim extends Sim {
	
	public static double duration = 0;
	
	public static Exp processingReqServiceTime;
	public static Exp transmissionReqServiceTime;
	public static Exp processingServerTime;
	public static Exp processingMsgServiceTime;
	public static Exp transmissionMsgServiceTime;
	
	public static double averageLifetime = 0;
	
	public static double averageOnClientApp = 0;
	public static double averageOffClientApp = 0;
	public static double averageOnMdw = 0;
	public static double averageOffMdw = 0;
	public static double averageOnServerApp = 0;
	public static double averageOffServerApp = 0;
	
	public static double noOfCust = 0;
	public static double avgTimeinQueue = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}


	// Here, the constructor starts the simulation.
	public Pattern2UnreliableSim(double d) {

		duration = d;

		Network.initialise();
		
//		Source source = new Source("Source", new Exp(1));
		
//		Exp lifetime = new Exp(0.1);
		Deterministic timeout = new Deterministic(Params.TIMEOUT);
		Source source = new Source("Source", new Exp(Params.SRC_RATE), timeout, "timeout");
		
		processingReqServiceTime = new Exp(128);
		transmissionReqServiceTime = new Exp(32);
		Delay prReq = new Delay(processingReqServiceTime);
		Delay trReq = new Delay(transmissionReqServiceTime);
		
//		Delay prReq = new Delay(new Exp(Params.PR_REQ_RATE));
//		Delay trReq = new Delay(new Exp(Params.TR_REQ_RATE));
		
		processingServerTime = new Exp(16);
		Delay prServerApp = new Delay(processingServerTime);
		
//		Delay prServerApp = new Delay(new Exp(Params.PR_SERVER_APP_RATE));
		
		processingMsgServiceTime = new Exp(64);
		transmissionMsgServiceTime = new Exp(16);
		Delay prMsg = new Delay(processingMsgServiceTime);
		Delay trMsg = new Delay(transmissionMsgServiceTime);
		
//		Delay prMsg = new Delay(new Exp(Params.PR_MSG_RATE));
//		Delay trMsg = new Delay(new Exp(Params.TR_MSG_RATE));
		
		Exp ONCl = new Exp(Params.ON_CL_RATE);
		Exp OFFCl = new Exp(Params.OFF_CL_RATE);

		Exp ONMdw = new Exp(Params.ON_MDW_RATE);
		Exp OFFMdw = new Exp(Params.OFF_MDW_RATE);
		
		Exp ONSer = new Exp(Params.ON_SER_RATE);
		Exp OFFSer = new Exp(Params.OFF_SER_RATE);		
		
		OnOffRQN cl_app1 = new OnOffRQN("CL-APP-1", prReq, 1, ONCl, OFFCl, duration);
		OnOffUQN cl_mdw1 = new OnOffUQN("CL-MDW-1", trReq, 1, ONMdw, OFFMdw, duration);
		OnOffUQN ser_mdw1 = new OnOffUQN("SER-MDW-1", prReq, 1, ONSer, OFFSer, duration);
		
		QueueingNode ser_app = new QueueingNode("SER-MDW-1", prServerApp, 1);
		
		OnOffUQN ser_mdw2 = new OnOffUQN("SER-MDW-2", trMsg, 1, ONMdw, OFFMdw, duration);		
		QueueingNode cl_mdw2 = new QueueingNode("CL-MDW-2", prMsg, 1);
		QueueingNode cl_app2 = new QueueingNode("CL-APP-2", prMsg, 1);
		
		SinkLftLses sinkClApp1 = new SinkLftLses("SINK-CL-APP-1");
		SinkMdwLses sinkClMdw1 = new SinkMdwLses("SINK-CL-MDW-1");
		SinkMdwLses sinkSerMdw1 = new SinkMdwLses("SINK-SER-MDW-1");
		SinkLftLses sinkSerApp1 = new SinkLftLses("SINK-SER-APP-1");
		
		SinkMdwLses sinkSerMdw2 = new SinkMdwLses("SINK-SER-MDW-2");
		SinkOvrlNet sinkClEnd = new SinkOvrlNet("SINK-CL-END");
		
		LftLsesBranch branchClApp1 = new LftLsesBranch(new Node[] {sinkClApp1, cl_mdw1});
		MdwLsesBranch branchClMdw1 = new MdwLsesBranch(new Node[] {sinkClMdw1, ser_mdw1});
		MdwLsesBranch branchSerMdw1 = new MdwLsesBranch(new Node[] {sinkSerMdw1, ser_app});
		LftLsesBranch branchSerApp1 = new LftLsesBranch(new Node[] {sinkSerApp1, ser_mdw2});
		
		MdwLsesBranch branchSerMdw2 = new MdwLsesBranch(new Node[] {sinkSerMdw2, cl_mdw2});
		

		source.setLink(new Link(cl_app1));
		cl_app1.setLink(branchClApp1);
		cl_mdw1.setLink(branchClMdw1);
		ser_mdw1.setLink(branchSerMdw1);
		ser_app.setLink(branchSerApp1);
		ser_mdw2.setLink(branchSerMdw2);
		cl_mdw2.setLink(new Link(cl_app2));
		cl_app2.setLink(new Link(sinkClEnd));

		simulate();
		
		averageOnClientApp = ONCl.average();
		averageOffClientApp = OFFCl.average();
		
		averageOnMdw = ONMdw.average();
		averageOffMdw = OFFMdw.average();
		
		averageOnServerApp = ONSer.average();
		averageOffServerApp = OFFSer.average();
		
//		averageLifetime = lifetime.average();
		
		System.err.println("ON average Producer App : " + averageOnClientApp);
		System.err.println("OFF average Producer App : " + averageOffClientApp);
		
		System.err.println("ON average Mdw : " + averageOnMdw);
		System.err.println("OFF average Mdw : " + averageOffMdw);
		
		System.err.println("ON average Consumer App : " + averageOnServerApp);
		System.err.println("OFF average Consumer App : " + averageOffServerApp);
	
		System.out.println("Processing Avg Req Service Time: " + processingReqServiceTime.average());
		System.out.println("Transmission Avg Req Service Time: " + transmissionReqServiceTime.average());
		
		System.out.println("Processing Avg Server Time: " + processingServerTime.average());
		
		System.out.println("Processing Avg Msg Service Time: " + processingMsgServiceTime.average());
		System.out.println("Transmission Avg Msg Service Time: " + transmissionMsgServiceTime.average());
		
		Network.logResult("CompletionsExpired", Network.completionsExpired);
		Network.logResult("ResponseTimeExpired", Network.responseTimeExpired.mean());
		
		Network.logResult("Completions", Network.completions);
		Network.logResult("ResponseTime", Network.responseTime.mean());
		
		System.out.println("SuccesRate: " + ((double)(Network.completions) / (double) (Network.completions + Network.completionsExpired)));
		
//		Network.responseTime.saveResponseMeasures();
	}

	public static void main(String args[]) {
//		new Pattern1UnreliableSim(500000);
		
		
		new Pattern2UnreliableSim(500000);
		
		
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
