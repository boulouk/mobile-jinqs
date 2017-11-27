package examples;

import network.* ;
import tools.* ;

class MM1BufferSizeSim extends Sim {

  public MM1BufferSizeSim() {
    Network.initialise() ;
    Delay serveTime = new Delay( new Exp( 8 ) ) ;
    
    FIFOQueue fq = new FIFOQueue( 6 ) ;

    Source source    = new Source( "Source", new Exp( 2 ) ) ;
    QueueingNode mm1 = new QueueingNode( "MM1", serveTime, 1, fq ) ;
    Sink sink        = new Sink( "Sink" ) ;
  
    source.setLink( new Link( mm1 ) ) ;
    mm1.setLink( new Link( sink ) ) ;

    simulate() ;

    Network.logResult( "Utilisation", mm1.serverUtilisation() ) ;
		Network.logResult("Response Time", Network.responseTime.mean());
  
		Network.logResults();
  }

  public boolean stop() {
    return Network.completions == 1000000 ;
  }

  public static void main( String args[] ) {
//    new MM1BufferSizeSim() ;
//    new MM1BufferSizeSim() ;
    new MM1BufferSizeSim() ;
    Network.displayResults( 0.01 ) ;
  }
}

