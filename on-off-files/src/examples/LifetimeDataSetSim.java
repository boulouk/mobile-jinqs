package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import extensions.LifetimeDataSetQN;
import extensions.SinkLifetime;

import network.* ;
import tools.* ;

class LifetimeDataSetSim extends Sim {
	
	public static double noOfCust = 0;
	public static double serverUtilization = 0;
	public static double queueTime = 0;
	public static double duration = 0;
	public static Exp serviceTime;

	public LifetimeDataSetSim() {
		Network.initialise();

		serviceTime = new Exp(16);
		Delay serveTime = new Delay(serviceTime);

		FileDataSet antenna1 = new FileDataSet("SET2_AntennaFull_9.txt");
		Source source = new Source("Source", antenna1, "Exp");

		LifetimeDataSetQN mm1 = new LifetimeDataSetQN("MM1", serveTime, 1, "Exp", 0.0016, "SET2_AntennaFull_161.txt", 499);

		SinkLifetime sink = new SinkLifetime("Sink");

		source.setLink(new Link(mm1));
		mm1.setLink(new Link(sink));

		simulate();

		noOfCust = mm1.meanNoOfQueuedCustomers();
		serverUtilization = mm1.serverUtilisation();
		queueTime = mm1.meanTimeInQueue();
		duration = antenna1.getCurrentLine() * antenna1.getStep();

		
		Network.logResults();
	}

  public boolean stop() {
//	  return Network.completions == 259;
	  return now() > 2.84814E7;
  }

  public static void main( String args[] ) {
    new LifetimeDataSetSim() ;

    Network.displayResults( 0.01 ) ;
   
    
  }
}
