package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import tools.DistributionSampler;
import tools.Exp;
import tools.Sim;
import network.*;

public class OnOffUQN extends OnOffRQN {

	public OnOffUQN(String s, Delay d, int n, DistributionSampler on, DistributionSampler off, double dur) {
		super(s, d, n, on, off, dur);
	}

	public OnOffUQN(String s, Delay d, int n, Queue q, DistributionSampler on, DistributionSampler off, double dur) {
		super(s, d, n, q, on, off, dur);
	}

	@Override
	protected void accept(Customer c) {
		if (resources.resourceIsAvailable()) {
			Debug.trace("Resource claimed");
			resources.claim();

			double serveTime = c.getServiceDemand();
			if (!this.isCon()) {
				c.setMwdExpiration(true);
				// TODO : I may need to change the service demand here
			} else {

				if ((Sim.now() + serveTime) > serverOnOff.getNextOff()) {
					c.setMwdExpiration(true);
					// TODO : I may need to change the service demand here
				}
			}

			invokeService(c);
		} else {
			if (queue.canAccept(c)) {
				Debug.trace("No resources. Enqueueing customer...");
				queue.enqueue(c);
			} else {
				losses++;
				Debug.trace("No resources. Queue full - customer sent to " + lossNode.getId());
				lossNode.enter(c);
			}
		}
	}

	@Override
	public void releaseResource() {
		Debug.trace(this + " releasing resource");
		if (!queue.isEmpty()) {
			Customer c = queue.dequeue();

			double serveTime = c.getServiceDemand();

			if (!this.isCon()) {
				c.setMwdExpiration(true);
				// TODO : I may need to change the service demand here
			} else {

				if ((Sim.now() + serveTime) > serverOnOff.getNextOff()) {
					c.setMwdExpiration(true);
					// TODO : I may need to change the service demand here
				}
			}

			invokeService(c);
		} else {
			resources.release();
		}
	}

}
