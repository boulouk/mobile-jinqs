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
public class SinkOvrlNet extends Node {
  public SinkOvrlNet() {
    super( "Sink" ) ;
  }

  public SinkOvrlNet( String name ) {
    super( name ) ;
  }

//
// Do nothing here - customer is absorbed...
//
  protected void accept( Customer c ) {    
    
    if (c.isExpired()) {
    	this.setLifetimeLosses(this.getLifetimeLosses()+1);
    	Network.completionsExpired++ ;
		Network.registerCompletionExpired(Sim.now() - c.getArrivalTime());
	} else if (c.isMwdExpiration()) {
    	this.setMdwLosses(this.getMdwLosses()+1);
    	Network.completionsExpired++ ;
		Network.registerCompletionExpired(Sim.now() - c.getArrivalTime());
	} else {
		Network.completions++ ;
		Network.registerCompletion( Sim.now() - c.getArrivalTime() ) ;
	}
    
    Network.registerCompletionON(0);
    Network.registerCompletionOFF(0);
   
  }

}

