package examples;

import network.* ;
import tools.* ;

class CPUSim extends Sim {

  public CPUSim() {
    Network.initialise() ;
    Delay cpuTime   = new Delay( new Exp( 1/0.005 ) ) ;
    Delay disk1Time = new Delay( new Exp( 1/0.03 ) ) ;
    Delay disk2Time = new Delay( new Exp( 1/0.027 ) ) ;

    Source source      = new Source( "Source", new Exp( 0.1 ) ) ;
    QueueingNode cpu   = new QueueingNode( "CPU", cpuTime, 1 ) ;
    QueueingNode disk1 = new QueueingNode( "Disk 1", disk1Time, 1 ) ;
    QueueingNode disk2 = new QueueingNode( "Disk 2", disk2Time, 1 ) ;
    Sink sink          = new Sink( "Sink" ) ;
  
    double[] routingProbs = { 1.0/121.0, 70.0/121.0, 50.0/121.0 } ;
    ProbabilisticBranch cpuOutputLink
      = new ProbabilisticBranch( routingProbs,
                                 new Node[] { sink, disk1, disk2 } ) ;

    source.setLink( new Link( cpu ) ) ;
    cpu.setLink( cpuOutputLink ) ;
    disk1.setLink( new Link( cpu ) ) ;
    disk2.setLink( new Link( cpu ) ) ;

    simulate() ;

    Network.logResults() ;
  }

  public boolean stop() {
    return Network.completions == 10000 ;
  }

  public static void main( String args[] ) {
    new CPUSim() ;
    Network.displayResults() ;
  }
}

