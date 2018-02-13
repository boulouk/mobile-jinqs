package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import extensions.SinkOvrlNet;
import extensions.SinkBothLses;
import network.*;
import tools.*;


class UnreliableOneOnOffSim extends Sim {
	
	public static double duration = 0;
	public static Exp serviceTime;
	public static Exp serviceTime2;
	public static double averageOn = 0;
	public static double averageLifetime = 0;
	public static double averageOff = 0;
	public static double durationOn = 0;
	public static double durationOff = 0;
	public static double noOfCust = 0;
	public static double avgTimeinQueue = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}


	// Here, the constructor starts the simulation.
	public UnreliableOneOnOffSim(double d) {

		duration = d;

		Network.initialise();

		
		serviceTime = new Exp(8);
		Delay serveTime = new Delay(serviceTime);
		
		Exp onlineTime1 = new Exp(0.05);
//		Deterministic onlineTime1 = new Deterministic(30);
		Exp offlineTime1 = new Exp(0.05);
		
//		10 = 0.1
//		20 = 0.05
//		30 = 0.033
		
//		timeouts40 = [10, 15, 20, 25, 30, 35];
		
//		Exp lifetime = new Exp(0.1);
//		Deterministic lifetime = new Deterministic(0.3);

		double L = 3;
		Source source = new Source("Source", new Exp(3));
//		
//		Source source = new Source("Source", new Exp(2), lifetime, "lifetime");
		
		OnOffUQN on0ff_1 = new OnOffUQN("Unreliable Queue", serveTime, 1, onlineTime1, offlineTime1, duration);
		
		
//		Sink sink = new Sink("Sink");
		
		SinkOvrlNet sink = new SinkOvrlNet("Sink Losses");

		source.setLink(new Link(on0ff_1));
		on0ff_1.setLink(new Link(sink));
		
		simulate();
		
		averageOn = onlineTime1.average();
		averageOff = offlineTime1.average();
		
//		averageLifetime = lifetime.average();
		
		System.err.println("ON average 1: " + averageOn);
		System.err.println("OFF average 1 : " + averageOff);
		
	
		System.out.println("Avg Service Time: " + serviceTime.average());
		
//		System.out.println("Avg Lifetime: " + lifetime.average());
		
		
		System.out.println("Customers at the End of ON - ON-OFF-1: " + on0ff_1.getCustomersEndOn());
		
		Network.logResult("CompletionsExpired", Network.completionsExpired);
		Network.logResult("ResponseTimeExpired", Network.responseTimeExpired.mean());
		
		Network.logResult("Completions", Network.completions);
		Network.logResult("ResponseTime", Network.responseTime.mean());
		
		System.out.println("SuccesRate: " + ((double)(Network.completions) / (double) (Network.completions + Network.completionsExpired)));
		
//		Network.responseTime.saveResponseMeasures();
	}

	public static void main(String args[]) {
		new UnreliableOneOnOffSim(1000000);
		
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
