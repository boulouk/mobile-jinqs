package network ;

import tools.* ;

public class PreemptiveResumeNode extends QueueingNode {
// 
// Note: all queues (FIFO, LIFO, Random...) behave the same
// way here, with the exception of a priority queue. 
// All preemptive nodes must have a single server and 
// infinite queue capacity. Because of the single server assumption
// the lastEndServiceEvent defined in the InfiniteServer superclass
// refers to the the last and only outstanding service completion
// event.  This is the one to deschedule on preemption.
//
  public PreemptiveResumeNode( String s, Delay d ) {
    super( s, d, 1, new LIFOQueue() ) ;
  }

  public PreemptiveResumeNode( String s, Delay d, Queue q ) {
    super( s, d, 1, q ) ;
    Check.check( q.isInfinite(),
                 "Node '" + name + "' must have an infinite-capacity queue" ) ;
  }

//
// In the resume policy the remaining service time can be found
// straightforwardly from the descheduled end service event...
//
  double remainingServiceTime( Customer c ) {
    return lastEndServiceEvent.invokeTime() - Sim.now() ;
  }

  protected void accept( Customer c ) {
    if ( resources.resourceIsAvailable() ) {
      Debug.trace( "Resource claimed" ) ;
      resources.claim() ;
    } else {
      Sim.deschedule( lastEndServiceEvent ) ;
      Customer preemptedCustomer = lastEndServiceEvent.getCustomer() ;
      Debug.trace( "Preempting customer in service (Id " +
                   preemptedCustomer.getId() + ")" ) ;
      double nextServiceDemand = remainingServiceTime( preemptedCustomer ) ;
      preemptedCustomer.setServiceDemand( nextServiceDemand ) ; 
      queue.enqueueAtHead( preemptedCustomer ) ;
    }
    invokeService( c ) ;
  }

}

