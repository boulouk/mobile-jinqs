package extensions;

import network.Customer;
import network.Network;
import network.Node;
import tools.*;

/**
 * A Sink node absrobs customers from a queueing network. Departing customers
 * are registered with the {@link Network} class, which records the customer's
 * sojourn time.
 * 
 * @param name
 *            The name of the source node
 * @param d
 *            The {@link DistributionSampler} used to generate the inter-arrival
 *            times
 * @param b
 *            The {@link DistributionSampler} used to generate the batch sizes
 */
public class SinkMulticlassPrio extends Node {
	public SinkMulticlassPrio() {
		super("SinkMulticlass");
	}

	public SinkMulticlassPrio(String name) {
		super(name);
	}

	//
	// Do nothing here - customer is absorbed...
	//
	protected void accept(Customer c) {
		
		// overall completions
		Network.completions++;
		// register response time for each class
		Network.registerClass(Sim.now() - c.getArrivalTime(), c.getclass());
		
		// register response time for each prio
		Network.registerPrio(Sim.now() - c.getArrivalTime(), c.getPriority());
		
		// completions for each class
		if(Network.completionsClassMap.get(c.getclass()) == null) {
			Network.completionsClassMap.put(c.getclass(), 1);  
		  } else
			  Network.completionsClassMap.put(c.getclass(), Network.completionsClassMap.get(c.getclass())+1);  
		
//		TODO completions for each prio
	}

}
