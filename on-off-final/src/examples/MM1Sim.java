package examples;

import network.* ;
import tools.* ;

class MM1Sim extends Sim {

  public MM1Sim() {
    Network.initialise() ;
    double mu = 8;
    Delay serveTime = new Delay( new Exp(mu) ) ;

    double lambda1 = 5;
    double lambda2 = 1.5;
    
    
    Source source    = new Source( "Source", new Exp(lambda1)) ;
//    Source source2    = new Source( "Source", new Exp(lambda2)) ;
    QueueingNode mm1 = new QueueingNode( "MM1", serveTime, 1) ;
    Sink sink        = new Sink( "Sink" ) ;
  
    source.setLink( new Link( mm1 ) ) ;
//    source2.setLink( new Link( mm1 ) ) ;
    mm1.setLink( new Link( sink ) ) ;

    simulate() ;
    
    double R = (1/mu) / (1 - ((lambda1) * (1/mu)));
    System.out.println("MODEL R: " + R);
    
    System.out.println("MODEL R2: " + (1/(mu - lambda1)));

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
    new MM1Sim() ;
    Network.displayResults( 0.01 ) ;
    
  }
}

