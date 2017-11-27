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

public class OnOffRInsProbsQN extends OnOffQN {
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
		if (con) {
			if (resources.resourceIsAvailable() && queue.isEmpty()) {

				Queue.getProbsON().add(0);
				Queue.getProbs().add(0);
				Debug.trace("Resource claimed");
				resources.claim();

				c.setArriveForService(Sim.now());
				c.setArriveForServiceON(Sim.now());
				c.setOff(false);
				
				double serveTime = c.getServiceDemand();
				if ((Sim.now() + serveTime) < ServerOnOff.nextOff) {
					invokeService(c);
				} else {
					
//					c.setServiceDemand(Sim.now() + serveTime + ServerOnOff.nextOff);
//					invokeService(c);
					resources.release();
				}

			} else if (resources.resourceIsAvailable() && (!queue.isEmpty())) {
				
				Queue.getProbsON().add(Queue.getPop()+1);
				Queue.getProbs().add(Queue.getPop()+1);
				Debug.trace("No resources. Enqueueing customer...");
				queue.enqueue(c);

				Debug.trace("Resource claimed");
				resources.claim();
				releaseResource();
			} else if (!resources.resourceIsAvailable()) {
				Queue.getProbsON().add(Queue.getPop()+1);
				Queue.getProbs().add(Queue.getPop()+1);
				Debug.trace("No resources. Enqueueing customer...");
				queue.enqueue(c);
			}

		} else {
			
			if (queue.canAccept(c)) {
				Debug.trace("No resources. Enqueueing customer...");

				
				if (insertProb.next() == 0) {
					if (queue.isEmpty()) {
						
						if (resources.resourceIsAvailable()) {
							Queue.getProbsOFF().add(0);
							Queue.getProbs().add(0);
						} else {
							Queue.getProbsOFF().add(1);
							Queue.getProbs().add(1);
						}
							
						queue.enqueue(c);
						c.setOff(true);
						double now = Sim.now();
						c.setArriveForService(now);
						
					} else {
						Queue.getProbsOFF().add(Queue.getPop()+1);
						Queue.getProbs().add(Queue.getPop()+1);
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
			if ((now + serveTime) < ServerOnOff.nextOff) {

				Customer c = queue.dequeue();

				if (!c.isOff()) {
					c.setArriveForService(now);
					c.setArriveForServiceON(now);
				}
				invokeService(c);
			} else {
//				Customer c = queue.dequeue();
//				c.setServiceDemand(now + serveTime + ServerOnOff.nextOff);
//				invokeService(c);
				
				resources.release();
			}

		} else {
			resources.release();
		}
	}

}
