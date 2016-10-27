package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import network.*;
import tools.*;

class OnOffNetworkSim extends Sim {

	public static double duration = 0;
	public static Exp serviceTime;
	public static double averageOn = 0;
	public static double averageOff = 0;
	public static double durationOn = 0;
	public static double durationOff = 0;
	public static double noOfCust = 0;
	public static double avgTimeinQueue = 0;
	public static double LambdaRate = 0;
	public static double serviceRate = 0;
	public static double util = 0;
	
	public static Exp inputServiceTime;
	public static Exp outputServiceTime;


	// Example termination function
	public boolean stop() {
		return now() > duration;
	}

	// Here, the constructor starts the simulation.
	public OnOffNetworkSim() {
		
	}

	public OnOffNetworkSim(double d) {

		duration = d;

		Network.initialise();

		serviceRate = 8;
		serviceTime = new Exp(serviceRate);

		Exp onlineTime1 = new Exp(1);
		Exp offlineTime1 = new Exp(1);
		
		Exp onlineTime20 = new Exp(0.05);
		Exp offlineTime20 = new Exp(0.05);

		inputServiceTime = new Exp(1);
		Delay inputServeTime = new Delay(inputServiceTime);

		outputServiceTime = new Exp(20);
		Delay outputServeTime = new Delay(outputServiceTime);
		
		Delay serveTime = new Delay(serviceTime);
		Delay serveTime2 = new Delay(serviceTime);
		
		LambdaRate = 1;
		Source source = new Source("Source", new Exp(LambdaRate));
		
		QueueingNode cpu   = new QueueingNode( "CPU", inputServeTime, 1 ) ;
		
		QueueingNode cpu2   = new QueueingNode( "CPU2", outputServeTime, 1 ) ;

//		OnOffQN onOff1st = new OnOffQN("onOff1st", serveTime, 1);
//		ServerOnOff serverOnOff1st = new ServerOnOff(onlineTime1, offlineTime1, duration, onOff1st);
//
//		OnOffQN onOff2nd = new OnOffQN("onOff2nd", serveTime, 1);
//		ServerOnOff serverOnOff2nd = new ServerOnOff(onlineTime1, offlineTime1, duration, onOff2nd);
		
//		OnOffQN onOff3rd = new OnOffQN("onOff3rd", serveTime, 1);
//		ServerOnOff serverOnOff3rd = new ServerOnOff(onlineTime20, offlineTime20, duration, onOff3rd);
		
		Sink subscriber = new Sink("Sink");

		
		source.setLink(new Link(cpu));
		
		cpu.setLink(new Link(cpu2));
//		onOff2nd.setLink(new Link(onOff3rd));
	
		cpu2.setLink(new Link(subscriber));

		simulate();

		averageOn = onlineTime20.average();
		averageOff = offlineTime20.average();

//		durationOn = serverOnOff3rd.getDurationOn();
//		durationOff = serverOnOff3rd.getDurationOff();

		System.err.println("ON average: " + averageOn);
		System.err.println("OFF average: " + averageOff);

		System.out.println("Avg Service Time: " + serviceTime.average());

//		Network.logResult("Avg Queue", onOff3rd.meanNoOfQueuedCustomers());


		Network.logResult("Response Time", Network.responseTime.mean());
//		Network.logResult("Utilisation", onOff3rd.serverUtilisation());
		
		Network.logResult("Completions", Network.completions);
		Network.logResult("CompletionsON", Network.completionsON);
		Network.logResult("CompletionsOFF", Network.completionsOFF);

//		noOfCust = onOff3rd.meanNoOfQueuedCustomers();

	}

	public static void main(String args[]) {
		new OnOffNetworkSim(1000000);

		Network.displayResults(0.01);
		
		try {

			AnalyticalModelsONOFF an = new AnalyticalModelsONOFF(Network.responseTime.mean(), Network.responseTimeON.mean(),
					Network.responseTimeOFF.mean(), Network.completions, Network.completionsON, Network.completionsOFF, duration,
					serviceTime.average(), Network.virtualServiceTime.mean(), Network.virtualServiceTime.variance(),
					Network.serviceTimeON.mean(), Network.serviceTimeOFF.mean(), durationOn, durationOff, averageOn, averageOff);

			String data = "Lsim: " + an.computeL() + " -- S-sim: " + serviceTime.average() + " -- avgON: " + averageOn
					+ " -- avgOFF: " + averageOff + " -- Arrivals: " + Network.completions + " -- Duration: " + duration;

			String simulator = "R-sim (mean resp time in system): " + Network.responseTime.mean() + " -- R-model (mean resp time in system): " + an.computeR()
					+ " -- R-sim-queue (mean resp time in queue): " + avgTimeinQueue;
			String model = " Q-sim (num of cust in queue): " + noOfCust + " -- Q-sim (num of cust in system): " + an.computeQsim();
		
			// String R_paper = " -- R_paper: " + an.computeR_paper();
		
			File file = new File("results_onoff.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.write("\n");
			bw.write(simulator);
			bw.write("\n");
			bw.write(model);
			bw.write("\n");
			// bw.write(R_paper);
			// bw.write("\n");
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


