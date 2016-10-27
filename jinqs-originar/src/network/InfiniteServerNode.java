package network ;

import tools.* ;

public class InfiniteServerNode extends Node {
  Delay serviceTime ;
  EndServiceEvent lastEndServiceEvent ;
  
  public InfiniteServerNode( Delay d ) {
    super( "Delay Node" ) ;
    serviceTime = d ;
  }
 
  public InfiniteServerNode( String s, Delay d ) {
    super( s ) ;
    serviceTime = d ;
  }
 
//
// Invokes a service delay when called. After the delay, the method
// forward is called, which can be overridden to effect
// special behaviours.
//
  protected final void invokeService( Customer c ) {
    double serveTime = c.getServiceDemand() ;
    Debug.trace( "Customer " + c.getId() + " entering service, " +
                 "service time = " + serveTime ) ;
    lastEndServiceEvent = new EndServiceEvent( c, Sim.now() + serveTime ) ;
    Sim.schedule( lastEndServiceEvent ) ;
  }

  class EndServiceEvent extends Event {
    Customer customer ;
    public EndServiceEvent( Customer c, double t ) {
      super( t ) ;
      customer = c ;
    }
    public void invoke() {
      forward( customer ) ;
    }
    public Customer getCustomer() {
      return customer ;
    }
  }
  
//
// The service demand is set on entry as the customer may be preempted
// by a subclass.  In preemptive-resume strategies, this case the
// demand needs to be reduced to reflect the residual service time.
// This method is finalised - subclasses should modify behaviour via 
// accept().
//
  public final void enter( Customer c ) {
    c.setServiceDemand( serviceTime.sample( c ) ) ;
    super.enter( c ) ;
  }
  
//
// Overrides superclass method.  An arriving customer is now subject to 
// delay.  
//
  protected void accept( Customer c ) {
    invokeService( c ) ;
  }
  
}

