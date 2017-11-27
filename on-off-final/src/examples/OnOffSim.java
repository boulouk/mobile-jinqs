package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import network.*;
import tools.*;


class OnOffSim extends Sim {
	
	public static double duration = 0;
	public static Exp serviceTime;
	public static Exp serviceTime2;
	public static double averageOn = 0;
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
	public OnOffSim(double d) {

		duration = d;

		Network.initialise();

		
		serviceTime = new Exp(8);
		serviceTime2 = new Exp(16);


		Exp onlineTime1 = new Exp(0.05);
//		Weibull onlineTime1 = new Weibull(20.9807, 1.1571);

		Exp offlineTime1 = new Exp(0.05);
//		Pareto onlineTime1 = new Pareto(-0.1388, 2, 160);
//		Weibull offlineTime1 = new Weibull(100.9807, 1.157194);
//		Gamma offlineTime1 = new Gamma(0.793, 90);
		
		Exp onlineTime2 = new Exp(0.05);
		Exp offlineTime2 = new Exp(0.05);

		Delay serveTime = new Delay(serviceTime);
		Delay serveTime2 = new Delay(serviceTime2);
		
		Source source = new Source("Source", new Exp(1));


		
		OnOffRQN on0ff_1 = new OnOffRQN("ON-OFF-1", serveTime, 1, onlineTime1, offlineTime1, duration);
		
//		QueueingNode mm1_1 = new QueueingNode( "MM1-1", serveTime, 1 ) ;
//		QueueingNode mm1_2 = new QueueingNode( "MM1-2", serveTime, 1 ) ;
		
		
//		QueueingNode mm1_3 = new QueueingNode( "MM1-3", serveTime2, 1 ) ;
		OnOffRQN on0ff_2 = new OnOffRQN("ON-OFF-2", serveTime2, 1, onlineTime2, offlineTime2, duration);
		
		QueueingNode mm1_4 = new QueueingNode( "MM1-4", serveTime, 1 ) ;
		
		Sink sink = new Sink("Sink");

		source.setLink(new Link(on0ff_1));
//		on0ff_1.setLink(new Link(on0ff_2));
//		mm1_1.setLink(new Link(mm1_2));
//		mm1_2.setLink(new Link(mm1_3));
//		mm1_3.setLink(new Link(on0ff_2));
//		on0ff_2.setLink(new Link(mm1_4));
		on0ff_1.setLink(new Link(sink));
//		on0ff_2.setLink(new Link(sink));

		simulate();
		
		averageOn = onlineTime1.average();
		averageOff = offlineTime1.average();
		
//		durationOn = serverOnOff.getDurationOn();
//		durationOff = serverOnOff.getDurationOff();
		
		System.err.println("ON average 1: " + averageOn);
		System.err.println("OFF average 1 : " + averageOff);
		
		System.err.println("ON average 2: " + onlineTime2.average());
		System.err.println("OFF average 2 : " + offlineTime2.average());

//		System.out.println("ON duration: " + durationOn);
//		System.out.println("OFF duration: " + durationOff);
		
		System.out.println("Avg Service Time: " + serviceTime.average());
		
		
		System.out.println("Customers at the End of ON - ON-OFF-1: " + on0ff_1.getCustomersEndOn());
//		System.out.println("Customers at the End of ON - ON-OFF-2: " + on0ff_2.getCustomersEndOn());
		
//		System.out.println("Duration of Cust at the End of ON - ON-OFF-1: " + on0ff_1.getDurationCustomersEndOn()/on0ff_1.getCustomersEndOn());
//		System.out.println("Duration of Cust at the End of ON - ON-OFF-2: " + on0ff_2.getDurationCustomersEndOn()/on0ff_2.getCustomersEndOn());
		
//		System.out.println("Normal duration of Cust - ON-OFF-1: " + on0ff_1.getServeTimeCustEndOn()/on0ff_1.getCustomersEndOn());
//		System.out.println("Normal duration of Cust - ON-OFF-2: " + on0ff_2.getServeTimeCustEndOn()/on0ff_2.getCustomersEndOn());
		
//		Network.logResult("Avg Queue", mm1.meanNoOfQueuedCustomers());
		
//		Network.logResult("Virtual Service Time", Network.virtualServiceTime.mean());
//		Network.logResult("ST ON", Network.serviceTimeON.mean());
//		Network.logResult("ST OFF", Network.serviceTimeOFF.mean());
//		Network.logResult("ST OFF Service Time", Network.serviceTimeOFFServiceTime.mean());
//		
		Network.logResult("Response Time", Network.responseTime.mean());
//		Network.logResult("Response Time ON", Network.responseTimeON.mean());
//		Network.logResult("Response Time OFF", Network.responseTimeOFF.mean());
//
//	
		Network.logResult("Completions", Network.completions);
//		Network.logResult("CompletionsON", Network.completionsON);
//		Network.logResult("CompletionsOFF", Network.completionsOFF);
		
//		noOfCust = on0ff_1.meanNoOfQueuedCustomers();
//		avgTimeinQueue = on0ff_1.meanTimeInQueue();
		
	}

	public static void main(String args[]) {
		new OnOffSim(1000000);
//		new OnOffSim(200);
		
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
//			// String data = "Lsim: " + an.computeL() + " -- Lon-sim: " +
//			// an.computeLon() + " -- Loff-sim: " + an.computeLoff() + " -- S-sim: " +
//			// serviceTime.average() + " -- Arrivals: " + Network.completions +
//			// " -- Duration: " + duration;
//
//			// String new_metrics = "Lon-model: " + an.computeLOnModel() +
//			// " -- Loff-model: " + an.computeLOffModel();
//			// String simulator = "R-sim: " + Network.responseTime.mean() +
//			// " -- Ns (queue): " + noOfCust;
//			String simulator = "R-sim (mean resp time in system): " + Network.responseTime.mean() + " -- R-model (mean resp time in system): " + an.computeR()
//					+ " -- R-sim-queue (mean resp time in queue): " + avgTimeinQueue + " -- R-model-mosxolios (mean resp time in system): " + an.computeR_mosxolios();
//			String model = " Q-sim (num of cust in queue): " + noOfCust + " -- Q-sim (num of cust in system): " + an.computeQsim();
//			// String onoff = "Ron-sim " + Network.responseTimeON.mean() +
//			// " -- Son-sim: " + Network.serviceTimeON.mean() + " -- Roff-sim " +
//			// Network.responseTimeOFF.mean() + " -- Soff-sim: " +
//			// Network.serviceTimeOFF.mean();
//			// String model = "Ron-model: " + an.computeRon() + " -- Roff-model: " +
//			// an.computeRoff() + " -- R-model: " + an.computeR();
////			String prob = "Psim (server utilization) : " + util;
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
//			// bw.write(new_metrics);
//			// bw.write("\n");
//			bw.write(simulator);
//			bw.write("\n");
//			// bw.write(onoff);
//			// bw.write("\n");
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
