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
public class SinkErrorRate extends Node {
	public SinkErrorRate() {
		super("Sink");
	}

	public SinkErrorRate(String name) {
		super(name);
	}

	//
	// Do nothing here - customer is absorbed...
	//
	protected void accept(Customer c) {

		Network.dropPackets++;

		// completions for each class
		if (Network.dropsClassMap.get(c.getclass()) == null) {
			Network.dropsClassMap.put(c.getclass(), 1);
		} else
			Network.dropsClassMap.put(c.getclass(), Network.dropsClassMap.get(c.getclass()) + 1);

	}

}
