package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import network.* ;
import tools.* ;

class MM1DataSetSim extends Sim {
	
	public static double noOfCust = 0;
	public static double serverUtilization = 0;
	public static double queueTime = 0;
	public static double duration = 0;
	public static Exp serviceTime;

  public MM1DataSetSim() {
    Network.initialise() ;
    
    serviceTime = new Exp(0.075);
	Delay serveTime = new Delay(serviceTime);

//    Source source    = new Source( "Source", new Exp( 2 ) ) ;
    FileDataSet antenna1 = new FileDataSet("antenna1_2weeks1_step25.txt");
    
    Source source    = new Source( "Source", antenna1 , "Det") ;
    QueueingNode mm1 = new QueueingNode( "MM1", serveTime, 1 ) ;
    Sink sink        = new Sink( "Sink" ) ;
  
    source.setLink( new Link( mm1 ) ) ;
    mm1.setLink( new Link( sink ) ) ;

    simulate() ;

    noOfCust = mm1.meanNoOfQueuedCustomers();
    serverUtilization = mm1.serverUtilisation();
    queueTime = mm1.meanTimeInQueue();
    duration = antenna1.getCurrentLine() * antenna1.getStep();
    
    System.err.println("Duration: " + duration);
    System.err.println("Lines: " + antenna1.getCurrentLine());
    
    Network.logResults() ;
  }

  public boolean stop() {
//	  return Network.completions == 259;
	  return now() > 2.49246E7;
  }

  public static void main( String args[] ) {
    new MM1DataSetSim() ;
//    new MM1Sim() ;
//    new MM1Sim() ;
    Network.displayResults( 0.01 ) ;
   
    
  }
}
