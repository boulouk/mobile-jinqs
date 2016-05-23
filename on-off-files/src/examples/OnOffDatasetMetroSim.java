package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import network.*;
import tools.*;


class OnOffDatasetMetroSim extends Sim {
	
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
	public OnOffDatasetMetroSim(double d) {

		duration = d;

		Network.initialise();

		
		serviceTime = new Exp(8);

//		Exp onlineTime = new Exp(0.00826446280991735537190082644628);
		FileDataSetOnOff onlineTime = new FileDataSetOnOff("cite_u_dugomierTon_2sec.txt");
//		FileDataSetOnOff onlineTime = new FileDataSetOnOff("allTon_2sec.txt");
//		FileDataSetOnOff onlineTime = new FileDataSetOnOff("dugomier_cite_uTon.txt");
//		FileDataSetOnOff onlineTime = new FileDataSetOnOff("dugomier_cite_uTon_2sec.txt");
//		Exp offlineTime = new Exp(0.01086956521739130434782608695652);
//		FileDataSetOnOff offlineTime = new FileDataSetOnOff("cite_u_dugomierToff.txt");
		FileDataSetOnOff offlineTime = new FileDataSetOnOff("cite_u_dugomierToff_remOFF.txt");
//		FileDataSetOnOff offlineTime = new FileDataSetOnOff("allToff_2sec.txt");
		
//		FileDataSetOnOff offlineTime = new FileDataSetOnOff("dugomier_cite_uToff.txt");
//		FileDataSetOnOff offlineTime = new FileDataSetOnOff("dugomier_cite_uToff_2sec.txt");
//		FileDataSetOnOff offlineTime = new FileDataSetOnOff("dugomier_cite_uToff_1OFF.txt");

		Delay serveTime = new Delay(serviceTime);
		
		
//		FileDataSet antenna1 = new FileDataSet("SET2_AntennaFull_248.txt");
//	  Source source    = new Source( "Source", antenna1 , "Exp") ;
	    
		Source source = new Source("Source", new Exp(3));
	    
		OnOffQN mm1 = new OnOffQN("MM1", serveTime, 1, "Not", 0.05);

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
		
		
//		Network.logResult("Avg Queue", mm1.meanNoOfQueuedCustomers());
//		
//		Network.logResult("Virtual Service Time", Network.virtualServiceTime.mean());
//		Network.logResult("ST ON", Network.serviceTimeON.mean());
//		Network.logResult("ST OFF", Network.serviceTimeOFF.mean());
//		Network.logResult("ST OFF Service Time", Network.serviceTimeOFFServiceTime.mean());
//		
//		Network.logResult("Response Time", Network.responseTime.mean());
//		Network.logResult("Response Time ON", Network.responseTimeON.mean());
//		Network.logResult("Response Time OFF", Network.responseTimeOFF.mean());
//
//	
//		Network.logResult("Completions", Network.completions);
//		Network.logResult("CompletionsON", Network.completionsON);
//		Network.logResult("CompletionsOFF", Network.completionsOFF);
		
		Network.logResults();
		
		noOfCust = mm1.meanNoOfQueuedCustomers();
		
	}

	public static void main(String args[]) {
//		new OnOffDatasetMetroSim(4000000);
		//98329 all
//		new OnOffDatasetMetroSim(98529);
		//cite_u_dugomier
	new OnOffDatasetMetroSim(54766);
	new OnOffDatasetMetroSim(54766);
	new OnOffDatasetMetroSim(54766);
	new OnOffDatasetMetroSim(54766);
	new OnOffDatasetMetroSim(54766);
	new OnOffDatasetMetroSim(54766);
	//processed
//	new OnOffDatasetMetroSim(51117);
		//processed rem files
//		new OnOffDatasetMetroSim(45487);
	

//dugomier_cite_u
//	new OnOffDatasetMetroSim(45407);
//	new OnOffDatasetMetroSim(43762);
//	new OnOffDatasetMetroSim(43762);
//	new OnOffDatasetMetroSim(43762);
//	new OnOffDatasetMetroSim(43762);
//	new OnOffDatasetMetroSim(43762);

		//processed
//		new OnOffDatasetMetroSim(40560);
	
	
	
	Network.displayResults( 0.01 ) ;
	
//	try {
//
//		AnalyticalModelsONOFF an = new AnalyticalModelsONOFF(Network.responseTime.mean(), Network.responseTimeON.mean(), Network.responseTimeOFF.mean(), Network.completions, Network.completionsON, Network.completionsOFF, duration, serviceTime.average(), Network.virtualServiceTime.mean(), Network.serviceTimeON.mean(), Network.serviceTimeOFF.mean(), durationOn, durationOff, averageOn, averageOff);
//		
//		String data = "L: " + an.computeL() + " -- Lon: " + an.computeLon() + " -- Loff: " + an.computeLoff() + " -- Sft: " + serviceTime.average() + " -- Arrivals: " + Network.completions + " -- Duration: " + duration;
//		String simulator = "Rs: " + Network.responseTime.mean() + " -- VSTs: " + Network.virtualServiceTime.mean() + " -- Ss (formula): " + an.computeS() + " -- Ns (queue): " + noOfCust;
//		String onoff = "RsOn " + Network.responseTimeON.mean() + " -- SsOn: " + Network.serviceTimeON.mean() + " -- RsOff " + Network.responseTimeOFF.mean() + " -- SsOff: " + Network.serviceTimeOFF.mean();
//		String model = "Ron: " + an.computeRon()  + " -- Roff: " + an.computeRoff() + " -- Nm: " + an.computeN() + " -- R_HV: " + an.computeR_HV();
//		String new_metrics = "Queue OFF: " + an.computeQueueSize();
//
//		File file = new File("results_onoff.txt");
//
//		if (!file.exists()) {
//			file.createNewFile();
//		}
//
//		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//		BufferedWriter bw = new BufferedWriter(fw);
//		bw.write(data);
//		bw.write("\n");
//		bw.write(simulator);
//		bw.write("\n");
//		bw.write(onoff);
//		bw.write("\n");
//		bw.write(model);
//		bw.write("\n");
//		bw.write(new_metrics);
//		bw.write("\n");
//		bw.write("\n");
//		bw.close();
//
//		System.out.println("Done");
//
//	} catch (IOException e) {
//		e.printStackTrace();
//	}
	}
}
