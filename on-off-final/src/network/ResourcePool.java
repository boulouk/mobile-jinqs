package network ;

import tools.* ;

public class ResourcePool extends InfiniteServerNode {
  protected int noOfResources ;
  protected Queue queue ;
  protected Resource resources ;
  protected int losses = 0 ;
  protected Node lossNode = Network.nullNode ;

  public ResourcePool( String s, Delay d, int n ) {
    super( s, d ) ;
    noOfResources = n ;
    queue = new FIFOQueue() ;
    resources = new Resource( noOfResources ) ;
  }

  public ResourcePool( String s, Delay d, int n, Queue q ) {
    super( s, d ) ;
    noOfResources = n ;
    queue = q ;
    resources = new Resource( noOfResources ) ;
  }

  public void setLossNode( Node n ) {
    lossNode = n ;
  }

  public String toString() {
    return name + ", resources = " + resources.numberOfAvailableResources() + 
           ", queue length = " + queue.queueLength() ;
  }

//
// Overrides superclass method.  
// Customers queue for resources, if there are none available.
// If the queue is full customers are routed to the loss node.
//
  protected void accept( Customer c ) {
    if ( resources.resourceIsAvailable() ) {
      Debug.trace( "Resource claimed" ) ;
      resources.claim() ;
      invokeService( c ) ;
    } else {
      if ( queue.canAccept( c ) ) {
        Debug.trace( "No resources. Enqueueing customer..." ) ;
        queue.enqueue( c ) ;
      } else {
        losses++ ;
        
     // drops for each class
		if (Network.dropsBufferClassMap.get(c.getclass()) == null) {
			Network.dropsBufferClassMap.put(c.getclass(), 1);
		} else
			Network.dropsBufferClassMap.put(c.getclass(), Network.dropsBufferClassMap.get(c.getclass()) + 1);
        
        Debug.trace( "No resources. Queue full - customer sent to " + 
                     lossNode.getId() ) ;
        lossNode.enter( c ) ;
      }
    }
  }

//
// A released resource is allocated to the next queued customer, if
// there is one.
//
  public void releaseResource() {
    Debug.trace( this + " releasing resource" ) ;
    if ( !queue.isEmpty() ) {
      Customer c = queue.dequeue() ;
      invokeService( c ) ;
    } else {
      resources.release();
    }
  }

//
// Useful additional methods
//

public int queueLength() {
  return queue.queueLength() ;
}

//
// Measurement stuff...
//
	public Queue getQueue() {
		return queue;
	}
	
	public Resource getResources() {
		return resources;
	}



  public int getLosses() {
    return losses ;
  }

  public double getLossProbability() {
    return (double)losses / (double)arrivals ;
  }

  public double serverUtilisation() {
    return resources.utilisation() ;
  }

  public double meanNoOfQueuedCustomers() {
    return queue.meanQueueLength() ;
  }

  public double varianceOfNoOfQueuedCustomers() {
    return queue.varQueueLength() ;
  }

  public double meanTimeInQueue() {
    return queue.meanTimeInQueue() ;
  }

  public double varianceOfTimeInQueue() {
    return queue.varTimeInQueue() ;
  }

  public void resetMeasures() {
    queue.resetMeasures() ;
    resources.resetMeasures() ;
  }

  public void logResults() {
    Logger.logResult( name + ", Server utilisation", 
                      serverUtilisation() ) ;
    Logger.logResult( name + ", Mean number of customers in queue", 
                      meanNoOfQueuedCustomers() ) ;
    Logger.logResult( name + ", Variance of number of customers in queue", 
                      varianceOfNoOfQueuedCustomers() ) ;
    Logger.logResult( name + ", Conditional mean queueing time",
                      meanTimeInQueue() ) ;
    Logger.logResult( name + ", Conditional variance of queueing time",
                      varianceOfTimeInQueue() ) ;
    Logger.logResult( name + ", Losses", getLosses() ) ;
    Logger.logResult( name + ", Proportion of customers lost",
                      getLossProbability() ) ;
  }

}


