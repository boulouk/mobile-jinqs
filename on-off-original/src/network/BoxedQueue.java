package network ;

import tools.* ;

/**
 * A boxed queue is a node containing just a queue.  As it's a passive 
 * object a method ({@link dequeue}) is needed to dequeue 
 * and forward customers explicitly.
*/
public class BoxedQueue extends Node {
  Queue queue ;

/**
 * @param q The queue to be boxed
*/
  public BoxedQueue( Queue q ) {
    super( "Boxed Queue" ) ;
    queue = q ;
  }

  public BoxedQueue( String name, Queue q ) {
    super( name ) ;
    queue = q ;
  }

  protected void accept( Customer c ) {
    queue.enqueue( c ) ;
  }

  public void dequeue() {
    forward( queue.dequeue() ) ;
  }

/**
 * Generic measures - copied from Queue, as there is no multiple
 * inheritance!
*/
  public double meanQueueLength() {
    return queue.meanQueueLength() ;
  }

  public double varQueueLength() {
    return queue.varQueueLength() ;
  }

  public double meanTimeInQueue() {
    return queue.meanTimeInQueue() ;
  }

  public double varTimeInQueue() {
    return queue.varTimeInQueue() ;
  }

  public void resetMeasures() {
    queue.resetMeasures() ;
  }

  public void logResults() {
    Logger.logResult( name + ", Mean number of customers in queue",
                      meanQueueLength() ) ;
    Logger.logResult( name + ", Variance of number of customers in queue",
                      varQueueLength() ) ;
    Logger.logResult( name + ", Mean time in queue",
                      meanTimeInQueue() ) ;
    Logger.logResult( name + ", Variance of time in queue",
                      varTimeInQueue() ) ;
  }

}
