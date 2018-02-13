package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import tools.DistributionSampler;
import tools.Exp;
import tools.Geometric;
import tools.Sim;
import network.*;

public class OnOffRInsProbsQN extends OnOffRQN {
	public DistributionSampler insertProb;
	

	public OnOffRInsProbsQN(String s, Delay d, int n, DistributionSampler on, DistributionSampler off, double insProb, double dur) {
		super(s, d, n, on, off, dur);
		insertProb = new Geometric(insProb);
	}

	public OnOffRInsProbsQN(String s, Delay d, int n, Queue q, DistributionSampler on, DistributionSampler off, double insProb, double dur) {
		super(s, d, n, q, on, off, dur);
		insertProb = new Geometric(insProb);
	}

	@Override
	protected void accept(Customer c) {
		if (this.isCon()) {

			synchronized (resourcesLock) {

				if (resources.resourceIsAvailable() && queue.isEmpty()) {
					Debug.trace("Resource claimed");

					resources.claim();

					c.setArriveForService(Sim.now());
					c.setOff(false);

					double serveTime = c.getServiceDemand();
					if ((Sim.now() + serveTime) < serverOnOff.getNextOff()) {
						invokeService(c);
					} else {
						synchronized (Customer.middleLock) {
							c.setMiddleCustomer(true);
						}

						double delayDif = serverOnOff.getNextOff() - Sim.now();
						c.setMiddleServiceDemand1(delayDif);
						c.setMiddleServiceDemand2(serveTime - delayDif);
						c.setServiceDemand(delayDif);

						int cust = this.getCustomersEndOn();
						this.setCustomersEndOn(cust + 1);

						invokeService(c);

					}

				} else if (resources.resourceIsAvailable() && (!queue.isEmpty())) {
					if (queue.canAccept(c)) {
						Debug.trace("No resources. Enqueueing customer...");
						queue.enqueue(c);

						Debug.trace("Resource claimed");
						resources.claim();
						releaseResource();
					} else {
						losses++;
						Debug.trace("No resources. Queue full - customer sent to " + lossNode.getId());
						lossNode.enter(c);
					}
				} else if (!resources.resourceIsAvailable()) {
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

		} else {
			// OFF
			if (queue.canAccept(c)) {
				Debug.trace("No resources. Enqueueing customer...");
				
				if (insertProb.next() == 0) {
					
					if (queue.isEmpty()) {
						queue.enqueue(c);
						c.setOff(true);
						double now = Sim.now();
						c.setArriveForService(now);
	
					} else {
						queue.enqueue(c);
					}

				} else {
//					TODO
				}
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

			Customer tempHead = queue.head();

			double serveTime = tempHead.getServiceDemand();
			double now = Sim.now();

			Customer c = queue.dequeue();
			if ((now + serveTime) < serverOnOff.getNextOff()) {

				if (!c.isOff()) {
					c.setArriveForService(now);
				}
				invokeService(c);
			} else {

				synchronized (Customer.middleLock) {
					c.setMiddleCustomer(true);
				}

				double delayDif = serverOnOff.getNextOff() - Sim.now();
				c.setMiddleServiceDemand1(delayDif);
				c.setMiddleServiceDemand2(serveTime - delayDif);
				c.setServiceDemand(delayDif);

				int cust = this.getCustomersEndOn();
				this.setCustomersEndOn(cust + 1);

				invokeService(c);

			}

		} else {

			synchronized (resourcesLock) {
				if (!resources.resourceIsAvailable())
					resources.release();

			}

		}
	}

}
