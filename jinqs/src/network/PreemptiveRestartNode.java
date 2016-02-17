package network ;

import tools.* ;

public class PreemptiveRestartNode extends PreemptiveResumeNode {

  public PreemptiveRestartNode( String s, Delay d ) {
    super( s, d ) ;
  }

  public PreemptiveRestartNode( String s, Delay d, Queue q ) {
    super( s, d, q ) ;
  }

//
// In the restart policy the remaining service time is the same
// as the original service demand...
//
  double remainingServiceTime( Customer c ) {
    return c.getServiceDemand() ;
  }
}

