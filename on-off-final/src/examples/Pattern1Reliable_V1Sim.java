package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import extensions.LftLsesBranch;
import extensions.SinkOvrlNet;
import extensions.SinkLftLses;
import network.*;
import tools.*;


class Pattern1Reliable_V1Sim extends Sim {
	
	public static double duration = 0;
	public static Exp processingServiceTime;
	public static Exp transmissionServiceTime;
	public static double averageLifetime = 0;
	
	public static double averageOnProducerApp = 0;
	public static double averageOffProducerApp = 0;
	public static double averageOnProducerMdw = 0;
	public static double averageOffProducerMdw = 0;
	
	public static double durationOnProducerApp = 0;
	public static double durationOffProducerApp = 0;
	public static double durationOnProducerMdw = 0;
	public static double durationOffProducerMdw = 0;
	
	public static double noOfCust = 0;
	public static double avgTimeinQueue = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}


	// Here, the constructor starts the simulation.
	public Pattern1Reliable_V1Sim(double d) {

		duration = d;

		Network.initialise();
		
//		Source source = new Source("Source", new Exp(1));
		
//		Exp lifetime = new Exp(0.1);
		Deterministic lifetime = new Deterministic(60);
		Source source = new Source("Source", new Exp(2), lifetime, "lifetime");
		
		processingServiceTime = new Exp(64);
		transmissionServiceTime = new Exp(16);
		Delay processingServeTime = new Delay(processingServiceTime);
		Delay transmissionServeTime = new Delay(transmissionServiceTime);
		
		Exp onlinePeriodProducerApp = new Exp(0.014);
		Exp offlinePeriodProducerApp = new Exp(0.1);

		Exp onlinePeriodProducerMdw = new Exp(0.1);
		Exp offlinePeriodProducerMdw = new Exp(0.05);
		
		OnOffRQN on0ff_producer_app = new OnOffRQN("ON-OFF-PRODUCER-APP", processingServeTime, 1, onlinePeriodProducerApp, offlinePeriodProducerApp, duration);
		OnOffRQN on0ff_producer_mdw = new OnOffRQN("ON-OFF-PRODUCER-MDW", transmissionServeTime, 1, onlinePeriodProducerMdw, offlinePeriodProducerMdw, duration);
				
		QueueingNode mm1_consumer_mdw = new QueueingNode("MM1-CONSUMER-MDW", processingServeTime, 1);
		
		
		SinkLftLses sinkProducerApp = new SinkLftLses("sinkProducerAppLifetimeLosses");
		SinkLftLses sinkProducerMdw = new SinkLftLses("sinkProducerMdwLifetimeLosses");
		SinkOvrlNet sinkConsumerEnd = new SinkOvrlNet("sinkConsumerEnd");
		
		LftLsesBranch branchProcuserApp = new LftLsesBranch(new Node[] {sinkProducerApp, on0ff_producer_mdw});
		LftLsesBranch branchProcuserMdw = new LftLsesBranch(new Node[] {sinkProducerMdw, mm1_consumer_mdw}) ;

		source.setLink(new Link(on0ff_producer_app));
		on0ff_producer_app.setLink(branchProcuserApp);
		on0ff_producer_mdw.setLink(branchProcuserMdw);
		mm1_consumer_mdw.setLink(new Link(sinkConsumerEnd));

		simulate();
		
		averageOnProducerApp = onlinePeriodProducerApp.average();
		averageOffProducerApp = offlinePeriodProducerApp.average();
		
		averageOnProducerMdw = onlinePeriodProducerMdw.average();
		averageOffProducerMdw = offlinePeriodProducerMdw.average();
		
//		averageLifetime = lifetime.average();
		
		System.out.println("Processing Avg Service Time: " + processingServiceTime.average());
		System.out.println("Transmission Avg Service Time: " + transmissionServiceTime.average());
		
		System.err.println("ON average Producer App : " + averageOnProducerApp);
		System.err.println("OFF average Producer App : " + averageOffProducerApp);
		
		System.err.println("ON average Producer Mdw : " + averageOnProducerMdw);
		System.err.println("OFF average Producer Mdw : " + averageOffProducerMdw);
	
		System.out.println("Processing Avg Service Time: " + processingServiceTime.average());
		System.out.println("Transmission Avg Service Time: " + transmissionServiceTime.average());
		
//		System.out.println("Customers at the End of ON - ON-OFF-1: " + on0ff_1.getCustomersEndOn());
		
		Network.logResult("CompletionsExpired", Network.completionsExpired);
		Network.logResult("ResponseTimeExpired", Network.responseTimeExpired.mean());
		
		Network.logResult("Completions", Network.completions);
		Network.logResult("ResponseTime", Network.responseTime.mean());
		
		System.out.println("SuccesRate: " + ((double)(Network.completions) / (double) (Network.completions + Network.completionsExpired)));
		
//		Network.responseTime.saveResponseMeasures();
	}

	public static void main(String args[]) {
		new Pattern1Reliable_V1Sim(500000);
		
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
