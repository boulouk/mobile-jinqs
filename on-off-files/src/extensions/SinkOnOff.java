package extensions ;
import network.Customer;
import network.Network;
import network.Node;
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
public class SinkOnOff extends Node {
  public SinkOnOff() {
    super( "Sink" ) ;
  }

  public SinkOnOff( String name ) {
    super( name ) ;
  }

//
// Do nothing here - customer is absorbed...
//
  protected void accept( Customer c ) {
    Network.completions++ ;
    Network.registerCompletion( Sim.now() - c.getArrivalTime() ) ;
    
    if (c.isOff()) {
    	Network.completionsOFF++ ;
		Network.registerCompletionOFF(Sim.now() - c.getArrivalTime());
	} else {
		Network.completionsON++ ;
		Network.registerCompletionON(Sim.now() - c.getArrivalTime());
	}
    
  }

}

