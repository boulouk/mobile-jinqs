package examples;

import network.* ;
import tools.* ;

class MM1Sim extends Sim {

  public MM1Sim() {
    Network.initialise() ;
    Delay serveTime = new Delay( new Exp( 8 ) ) ;

    Source source    = new Source( "Source", new Exp( 1 ) ) ;
    QueueingNode mm1 = new QueueingNode( "MM1", serveTime, 1 ) ;
    Sink sink        = new Sink( "Sink" ) ;
  
    source.setLink( new Link( mm1 ) ) ;
    mm1.setLink( new Link( sink ) ) ;

    simulate() ;

    Network.logResult( "Utilisation", mm1.serverUtilisation() ) ;
		Network.logResult("Response Time", Network.responseTime.mean());
  }

  public boolean stop() {
    return Network.completions == 1000000 ;
  }

  public static void main( String args[] ) {
    new MM1Sim() ;
    new MM1Sim() ;
    new MM1Sim() ;
    Network.displayResults( 0.01 ) ;
  }
}

