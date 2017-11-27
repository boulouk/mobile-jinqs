package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import extensions.LftLsesBranch;
import extensions.MdwLsesBranch;
import extensions.SinkMdwLses;
import extensions.SinkOvrlNet;
import extensions.SinkLftLses;
import network.*;
import tools.*;


class UTC_UnreliableSim extends Sim {
	
	public static double duration = 0;
	public static Exp processingServiceTime;
	public static Exp processingServiceTimeBC;
	public static Exp transmissionServiceTime;
	public static double averageLifetime = 0;
	
	public static double averageOnNav = 0;
	public static double averageOffNav = 0;
	
	public static double durationOnNav = 0;
	public static double durationOffNav = 0;
	
	public static double noOfCust = 0;
	public static double avgTimeinQueue = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}


	// Here, the constructor starts the simulation.
	public UTC_UnreliableSim(double d) {

		duration = d;

		Network.initialise();
		
//		Source source = new Source("Source", new Exp(1));
		
//		Exp lifetime = new Exp(0.1);
		Deterministic lifetime = new Deterministic(10);
		Source source = new Source("Source", new Exp(1), lifetime, "lifetime");
		
		processingServiceTime = new Exp(32);
		processingServiceTimeBC = new Exp(7.7);
		transmissionServiceTime = new Exp(16);
		Delay processingServeTime = new Delay(processingServiceTime);
		Delay processingServeTimeBC = new Delay(processingServiceTimeBC);
		Delay transmissionServeTime = new Delay(transmissionServiceTime);
		
//		10=0.1
//		20=0.05
//		30=0.033
//		40=0.025
//		50=0.02
//		60=0.0166
		
		Exp onlinePeriodNav = new Exp(0.0166);
		Exp offlinePeriodNav = new Exp(0.1);
		
		QueueingNode mm1_bridge_tr = new QueueingNode("MM1-BRIDGE", transmissionServeTime, 1);
		
		QueueingNode mm1_bc1_pr = new QueueingNode("MM1-BC1-PR", processingServeTime, 1);
		QueueingNode mm1_bc1_app = new QueueingNode("MM1-BC1-APP", processingServeTimeBC, 1);
		QueueingNode mm1_bc1_tr = new QueueingNode("MM1-BC1-TR", transmissionServeTime, 1);
		
		QueueingNode mm1_cd_seatsa_pr = new QueueingNode("MM1-CD-SEATSA-PR", processingServeTime, 1);
		QueueingNode mm1_cd_seatsa_app = new QueueingNode("MM1-CD-SEATSA-APP", processingServeTime, 1);
		QueueingNode mm1_cd_seatsa_tr = new QueueingNode("MM1-CD-SEATSA-TR", transmissionServeTime, 1);
		
		QueueingNode mm1_cd_traffic_pr = new QueueingNode("MM1-CD-TRAFFIC-PR", processingServeTime, 1);
		QueueingNode mm1_cd_traffic_app = new QueueingNode("MM1-CD-TRAFFIC-APP", processingServeTime, 1);
		QueueingNode mm1_cd_traffic_tr = new QueueingNode("MM1-CD-TRAFFIC-TR", transmissionServeTime, 1);
		
		QueueingNode mm1_bc2_pr = new QueueingNode("MM1-BC2-PR", processingServeTime, 1);
		QueueingNode mm1_bc2_app = new QueueingNode("MM1-BC2-APP", processingServeTimeBC, 1);		
		OnOffUQN on_off_un_bc2_tr = new OnOffUQN("ON-OFF-UN-BC2-TR", transmissionServeTime, 1, onlinePeriodNav, offlinePeriodNav, duration);
		
		
		QueueingNode mm1_nav = new QueueingNode("MM1-NAVIGATION", processingServeTime, 1);
		
		
		SinkLftLses sinkBridge = new SinkLftLses("sinkBridgeLifetimeLosses");
		SinkLftLses sinkBC1 = new SinkLftLses("sinkBC1LifetimeLosses");
		SinkLftLses sinkCDSeatsa = new SinkLftLses("sinkCDSeatsaLifetimeLosses");
		SinkLftLses sinkCDTraffic = new SinkLftLses("sinkCDTrafficLifetimeLosses");
		SinkLftLses sinkBC2 = new SinkLftLses("sinkBC2LifetimeLosses");
		SinkMdwLses sinkBC2MdwLosses = new SinkMdwLses("SINK-BC2-MDW-LOSSES");
		SinkOvrlNet sinkNavEnd = new SinkOvrlNet("sinkNavEnd");
		
		LftLsesBranch branchBridge = new LftLsesBranch(new Node[] {sinkBridge, mm1_bc1_pr});
		LftLsesBranch branchBC1 = new LftLsesBranch(new Node[] {sinkBC1, mm1_bc1_tr});
		LftLsesBranch branchCDSeatsa = new LftLsesBranch(new Node[] {sinkCDSeatsa, mm1_cd_seatsa_tr});
		LftLsesBranch branchCDTraffic = new LftLsesBranch(new Node[] {sinkCDTraffic, mm1_cd_traffic_tr});
		LftLsesBranch branchBC2 = new LftLsesBranch(new Node[] {sinkBC2, on_off_un_bc2_tr});
		MdwLsesBranch branchBC2MdwLosses = new MdwLsesBranch(new Node[] {sinkBC2MdwLosses, mm1_nav}) ;

		source.setLink(new Link(mm1_bridge_tr));
		mm1_bridge_tr.setLink(branchBridge);
		mm1_bc1_pr.setLink(new Link(mm1_bc1_app));
		mm1_bc1_app.setLink(branchBC1);
		mm1_bc1_tr.setLink(new Link(mm1_cd_seatsa_pr));
		mm1_cd_seatsa_pr.setLink(new Link(mm1_cd_seatsa_app));
		mm1_cd_seatsa_app.setLink(branchCDSeatsa);
		mm1_cd_seatsa_tr.setLink(new Link(mm1_cd_traffic_pr));
		mm1_cd_traffic_pr.setLink(new Link(mm1_cd_traffic_app));
		mm1_cd_traffic_app.setLink(branchCDTraffic);
		mm1_cd_traffic_tr.setLink(new Link(mm1_bc2_pr));
		mm1_bc2_pr.setLink(new Link(mm1_bc2_app));
		mm1_bc2_app.setLink(branchBC2);
		on_off_un_bc2_tr.setLink(branchBC2MdwLosses);
		
		mm1_nav.setLink(new Link(sinkNavEnd));

		simulate();
		
		averageOnNav = onlinePeriodNav.average();
		averageOffNav = offlinePeriodNav.average();
		
//		averageLifetime = lifetime.average();
		
		System.out.println("Processing Avg Service Time: " + processingServiceTime.average());
		System.out.println("Transmission Avg Service Time: " + transmissionServiceTime.average());
		
		System.err.println("ON average Producer App : " + averageOnNav);
		System.err.println("OFF average Producer App : " + averageOffNav);
	
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
		new UTC_UnreliableSim(50000);
		
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
