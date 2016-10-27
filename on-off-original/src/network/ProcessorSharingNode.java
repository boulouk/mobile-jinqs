package network ;

import tools.* ;

/**
 * Implements a processor sharing node with a single server.
 * The processing capacity seen by each customer decreases
 * linearly with the number of customers.
*/
public class ProcessorSharingNode extends ResourcePool {
  Event nextEndServiceEvent ; 
  double vtime = 0.0 ;
  double timeOfLastSchedule ;
 
  public ProcessorSharingNode( String s, Delay d ) {
    super( s, d, 1, new OrderedQueue() ) ;
  }

  class EndOfService extends Event {
    public EndOfService( double t ) {
      super( t ) ;
    }
    public void invoke() {
      vtime += ( Sim.now() - timeOfLastSchedule ) / queue.queueLength() ;
      OrderedQueueEntry e = (OrderedQueueEntry)queue.dequeue() ;
      forward( e.entry ) ;
      if ( queue.queueLength() == 0 ) {
        resources.release() ;
      } else {
        serviceNextCustomer() ;
      }
    }
  }
  
  void serviceNextCustomer() {
    OrderedQueueEntry e = (OrderedQueueEntry)queue.head() ;
    double completionTime = ( e.time - vtime ) * queue.queueLength() ;
    timeOfLastSchedule = Sim.now() ;
    nextEndServiceEvent = new EndOfService( Sim.now() + completionTime ) ;
    Sim.schedule( nextEndServiceEvent ) ;
  }

/**
 * Overrides superclass method.  It uses a devious method
 * to avoid the O(n) cost of updating queue entries after each event.
 * The trick is twofold: 1. Maintain a local virtual time that 
 * increases at a rate that's inversely proportional to the population.
 * 2. Store virtual finish times with each customer in the queue.
 * An arrival to a non-empty system cancels the current end-service
 * event. 
 * The next customer to leave is always the one with the smallest
 * finish time.
 * The queue is an ordered queue, sorted by finish time. Each queue 
 * entry comprises a customer and its finish time.
 * The queue entries are themselves QueueEntries as they contain
 * a customer and a time. A QueueEntry conveniently defines a
 * built-in order based on the time.  Here the times are
 * finish times.  Note that the enqueue method will add an additional
 * wrapper in the form of a QueueEntry in order to track the customer
 * arrival time in the queue.
 *
*/
  protected void accept( Customer c ) {
    if ( queue.canAccept( c ) ) {
      Debug.trace( "Enqueueing customer..." ) ;
      double serviceTime = c.getServiceDemand() ;
      if ( resources.resourceIsAvailable() ) {
        resources.claim() ;
      } else {
        Sim.deschedule( nextEndServiceEvent ) ;
        vtime += ( Sim.now() - timeOfLastSchedule ) / queue.queueLength() ;
      }
      queue.enqueue( new OrderedQueueEntry( c, vtime + serviceTime ) ) ;
      serviceNextCustomer() ;
    } else {
      Debug.trace( "No resources. Queue full - customer sent to " +
                   Network.nullNode.getId() ) ;
      losses++ ;
      Network.nullNode.enter( c ) ;
    }
  }

//
// This could only be called in error from outside the package.
// You cannot release a PS node resource!
//
  public void releaseResource() {
    Check.check( false, "releaseResource() invoked on a PS node" ) ;
  }

}

