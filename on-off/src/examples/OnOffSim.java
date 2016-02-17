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
	public static double averageOn = 0;
	public static double averageOff = 0;
	public static double durationOn = 0;
	public static double durationOff = 0;
	public static double noOfCust = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}


	// Here, the constructor starts the simulation.
	public OnOffSim(double d) {

		duration = d;

		Network.initialise();

		Network.initialise();
		serviceTime = new Exp(8);

		Exp onlineTime = new Exp(0.050);
		Exp offlineTime = new Exp(0.050);

		Delay serveTime = new Delay(serviceTime);
		Source source = new Source("Source", new Exp(2));

		OnOffQN mm1 = new OnOffQN("MM1", serveTime, 1);

		ServerOnOff serverOnOff = new ServerOnOff(onlineTime, offlineTime, duration, mm1);

		Sink sink = new Sink("Sink");

		source.setLink(new Link(mm1));
		mm1.setLink(new Link(sink));

		simulate();
		
		averageOn = onlineTime.average();
		averageOff = offlineTime.average();
		
		durationOn = serverOnOff.getDurationOn();
		durationOff = serverOnOff.getDurationOff();
		
		System.err.println("ON average: " + averageOn);
		System.err.println("OFF average: " + averageOff);

		System.out.println("ON duration: " + durationOn);
		System.out.println("OFF duration: " + durationOff);
		
		System.out.println("Avg Service Time: " + serviceTime.average());
		
		
		Network.logResult("Avg Queue", mm1.meanNoOfQueuedCustomers());
		
		Network.logResult("Virtual Service Time", Network.virtualServiceTime.mean());
		Network.logResult("ST ON", Network.serviceTimeON.mean());
		Network.logResult("ST OFF", Network.serviceTimeOFF.mean());
		Network.logResult("ST OFF Service Time", Network.serviceTimeOFFServiceTime.mean());
		
		Network.logResult("Response Time", Network.responseTime.mean());
		Network.logResult("Response Time ON", Network.responseTimeON.mean());
		Network.logResult("Response Time OFF", Network.responseTimeOFF.mean());

	
		Network.logResult("Completions", Network.completions);
		Network.logResult("CompletionsON", Network.completionsON);
		Network.logResult("CompletionsOFF", Network.completionsOFF);
		
		noOfCust = mm1.meanNoOfQueuedCustomers();
		
	}

	public static void main(String args[]) {
		new OnOffSim(5000000);
		
		Network.displayResults( 0.01 ) ;
		
		try {

			AnalyticalModelsONOFF an = new AnalyticalModelsONOFF(Network.responseTime.mean(), Network.responseTimeON.mean(), Network.responseTimeOFF.mean(), Network.completions, Network.completionsON, Network.completionsOFF, duration, serviceTime.average(), Network.virtualServiceTime.mean(), Network.virtualServiceTime.variance(), Network.serviceTimeON.mean(), Network.serviceTimeOFF.mean(), durationOn, durationOff, averageOn, averageOff);
			
			String data = "L: " + an.computeL() + " -- Lon-sim: " + an.computeLon() + " -- Loff-sim: " + an.computeLoff() + " -- S-sim: " + serviceTime.average() + " -- Arrivals: " + Network.completions + " -- Duration: " + duration;
			String new_metrics = "Lon-model: " + an.computeLOnModel() + " -- Loff-model: " + an.computeLOffModel();
			String simulator = "R-sim: " + Network.responseTime.mean() + " -- Ns (queue): " + noOfCust;
			String onoff = "Ron-sim " + Network.responseTimeON.mean() + " -- Son-sim: " + Network.serviceTimeON.mean() + " -- Roff-sim " + Network.responseTimeOFF.mean() + " -- Soff-sim: " + Network.serviceTimeOFF.mean();
			String model = "Ron-model: " + an.computeRon()  + " -- Roff-model: " + an.computeRoff() + " -- R-model: " + an.computeR();
			String R_paper = " -- R_paper: " + an.computeR_paper();
			File file = new File("results_onoff.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.write("\n");
			bw.write(new_metrics);
			bw.write("\n");
			bw.write(simulator);
			bw.write("\n");
			bw.write(onoff);
			bw.write("\n");
			bw.write(model);
			bw.write("\n");
			bw.write(R_paper);
			bw.write("\n");
			bw.write("\n");
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
