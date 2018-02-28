package icpe2017qn;

import network.* ;
import tools.* ;

class MMcSim extends Sim {

  public MMcSim() {
    Network.initialise() ;
    
    int servers = 3;
    double mu = 8;
    Delay serveTime = new Delay( new Exp(mu) ) ;

    double lambda1 = 20; 
    
    Source source    = new Source( "Source", new Exp(lambda1)) ;
//    Source source2    = new Source( "Source", new Exp(lambda2)) ;
    QueueingNode mm1 = new QueueingNode( "MM1", serveTime, servers) ;
    Sink sink        = new Sink( "Sink" ) ;
  
    source.setLink( new Link( mm1 ) ) ;
//    source2.setLink( new Link( mm1 ) ) ;
    mm1.setLink( new Link( sink ) ) ;

    simulate() ;

    Network.logResult( "Utilisation", mm1.serverUtilisation() ) ;
	Network.logResult("Response Time", Network.responseTime.mean());
	Network.logResult("Mean Queue Size", mm1.meanNoOfQueuedCustomers());
//	Network.logResults();
		
//	Network.responseTime.saveCDFResponseMeasures();
//	Network.responseTime.saveResponsesAndTimestamps();
	
  }

  public boolean stop() {
    return Network.completions == 1000000 ;
  }

  public static void main( String args[] ) {
    new MMcSim() ;
    Network.displayResults( 0.01 ) ;
    
  }
}

