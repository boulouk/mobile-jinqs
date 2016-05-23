package extensions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import tools.Deterministic;
import tools.DistributionSampler;
import tools.Event;
import tools.Exp;
import tools.Sim;
import network.*;

public class LifetimeQN extends QueueingNode {
	public static boolean con;
	public DistributionSampler lifetimeDistrib;
	public String QueueingNodeName;

	public LifetimeQN(String s, Delay d, int n, String distribType, double value) {
		super(s, d, n);
		con = true;
		if (distribType.equals("Exp")) {
			lifetimeDistrib = new Exp(value);
		} else if (distribType.equals("Det")) {
			lifetimeDistrib = new Deterministic(value);
		} else {
			lifetimeDistrib = null;
		}

	}

	@Override
	protected void accept(Customer c) {
		if(lifetimeDistrib != null) {
			c.setQueueingNodeInsertionTime(Sim.now());
			c.setLifetime(lifetimeDistrib.next());
			c.setWithLifetime(true);
		}
		if (resources.resourceIsAvailable()) {
			Debug.trace("Resource claimed");
			resources.claim();
			invokeService(c);
		} else {
			if (queue.canAccept(c)) {
				Debug.trace("No resources. Enqueueing customer...");
				queue.enqueue(c);
			} else {
				losses++;
				Debug.trace("No resources. Queue full - customer sent to "
						+ lossNode.getId());
				lossNode.enter(c);
			}
		}
	}

}
