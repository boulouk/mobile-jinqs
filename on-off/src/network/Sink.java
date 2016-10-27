package network ;
import tools.* ;

/**
 * A Sink node absrobs customers from a queueing network.
 * Departing customers are registered with the {@link Network}
 * class, which records the customer's sojourn time.
 * @param name The name of the source node
 * @param d The {@link DistributionSampler} used to generate the
 *          inter-arrival times
 * @param b The {@link DistributionSampler} used to generate the
            batch sizes
*/
public class Sink extends Node {
  public Sink() {
    super( "Sink" ) ;
  }

  public Sink( String name ) {
    super( name ) ;
  }

//
// Do nothing here - customer is absorbed...
//
  protected void accept( Customer c ) {
    Network.completions++ ;
    Network.registerCompletion( Sim.now() - c.getArrivalTime() ) ;
    System.out.println("hello: " +c.getId());
    
    if (c.isOff()) {
    	Network.completionsOFF++ ;
		Network.registerCompletionOFF(Sim.now() - c.getArrivalTime());
	} else {
		Network.completionsON++ ;
		Network.registerCompletionON(Sim.now() - c.getArrivalTime());
	}
    
  }

}

