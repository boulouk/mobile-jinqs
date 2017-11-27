package network ;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import tools.* ;

/**
 * A Source node injects customers into a queueing network.
 * The inter-arrival time (a {@link DistributionSampler})
 * must be specified.  Arrivals may
 * optionally be batched, with the batch size specified by
 * a second {@link DistributionSampler}.
*/
public class Source extends Node {
  protected DistributionSampler delay ;
  protected DistributionSampler batchsize ;
  protected DistributionSampler lifetime ;
  
  

public DistributionSampler getDelay() {
	return delay;
}

/**
 * @param name The name of the source node
 * @param d The {@link DistributionSampler} used to generate the
 *          inter-arrival times
*/
  public Source( String name, DistributionSampler d ) {
    super( name ) ;
    delay = d ;
    batchsize = new Deterministic( 1 ) ;
    Sim.schedule( new Arrival( Sim.now() + delay.next() ) ) ;
  }
  
  /**
   * @param name The name of the source node
   * @param d The {@link DistributionSampler} used to generate the
   *          inter-arrival times
   * @param l The {@link DistributionSampler} used to generate the
   *          lifetimes
   * @param lifetimeName The name of the lifetime periods
  */
    public Source( String name, DistributionSampler d, DistributionSampler l, String lifetimeName) {
      super( name ) ;
      delay = d ;
      batchsize = new Deterministic( 1 ) ;
      lifetime = l;
      Sim.schedule( new Arrival( Sim.now() + delay.next() ) ) ;
    }

/**
 * @param name The name of the source node
 * @param d The {@link DistributionSampler} used to generate the
 *          inter-arrival times
 * @param b The {@link DistributionSampler} used to generate the
            batch sizes
*/
  public Source( String name, DistributionSampler d, DistributionSampler b ) {
    super( name ) ;
    delay = d ;
    batchsize = b ;
    Sim.schedule( new Arrival( Sim.now() + delay.next() ) ) ;
  }

/**
 * Builds a new customer. This can be overridden to support 
 * specialised {@link Customer} subclasses.
 * @return a customer
 *
*/
  protected Customer buildCustomer() {
	  if(lifetime != null)
		  return new Customer(lifetime.next());
	  else 
		  return new Customer();
  }

/** 
 * Injects customers into the network using forward.
 * The initial location of the customer is the source node.
*/
  void injectCustomer() {
    Customer c =  buildCustomer() ;
    c.setLocation( this ) ;  
    forward( c ) ;
  }

  void injectCustomers() {
    int nArrivals = (int) batchsize.next() ;
    for ( int i = 0 ; i < nArrivals ; i++ ) {
      injectCustomer() ;
    }
  }

/**
 * One arrival prompts the next...
*/
  class Arrival extends Event {
    public Arrival( double t ) {
      super( t ) ;
    }
    public void invoke() {
      injectCustomers() ;
      Sim.schedule( new Arrival( Sim.now() + delay.next() ) ) ;
    }
  }

}

