package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import network.*;
import tools.*;

public class ServerOnOff {

	protected DistributionSampler delayOn;
	protected DistributionSampler delayOff;
	protected OnOffRQN queueingNode;
	protected double duration = 0;
	protected double durationOn = 0;
	protected double durationOff = 0;

	protected double nextOff = 0;
	protected double nextOn = 0;

	protected double offTime = 0;
	protected double onTime = 0;

	protected double counter1 = 0;
	protected double counter2 = 0;

	protected double atOn = 0;
	protected double atOff = 0;

	public double getNextOff() {
		return nextOff;
	}

	public double getNextOn() {
		return nextOn;
	}
	
	public OnOffRQN getQueueingNode() {
		return queueingNode;
	}

	public ServerOnOff(DistributionSampler dOn, DistributionSampler dOff, double d, OnOffRQN qn) {
		delayOn = dOn;
		delayOff = dOff;
		duration = d;
		queueingNode = qn;

		double delay = delayOn.next();
		setDurationOn(delay);

		nextOff = Sim.now() + delay;
		Sim.schedule(new Off((Sim.now() + delay), duration));
	}

	class On extends Event {
		double time = 0;
		double duration = 0;

		public On(double t, double d) {
			super(t);
			time = t;
			duration = d;
		}

		public void invoke() {
//			System.out.println("Server goes online at: " + time);

			getQueueingNode().setCon(true);
			onTime = Sim.now();
			Queue queue = getQueueingNode().getQueue();
			Resource resources = getQueueingNode().getResources();

			double newDelay = 0;
			double now = Sim.now();
			double delay = delayOn.next();
			double addition = now + delay;

			if (addition > duration) {
				newDelay = duration - now;
				setDurationOn(newDelay);
				atOff = duration - now;
				nextOff = now + newDelay;
				Sim.schedule(new Off((now + newDelay), duration));
			} else {
				setDurationOn(delay);
				atOff = addition;
				nextOff = addition;
				Sim.schedule(new Off(addition, duration));
			}
			
			synchronized (OnOffRQN.resourcesLock) {

				if (!resources.resourceIsAvailable() && getQueueingNode().getCurrentCustomer().isMiddleCustomer()) {
					
//					System.out.println("Queue: " + getQueueingNode().getName() + " ID: " + getQueueingNode().getCurrentCustomer().getId());
	
					getQueueingNode().serveMiddleCustomers();
					
				} else if (resources.resourceIsAvailable() && (!queue.isEmpty())) {
					
					resources.claim();
					getQueueingNode().releaseResource();
				}
			
			}

		}
	}

	class Off extends Event {
		double time = 0;
		double duration = 0;

		public Off(double t, double d) {
			super(t);
			time = t;
			duration = d;
		}

		public void invoke() {
//			System.out.println("Server goes offline at: " + time);

			getQueueingNode().setCon(false);
			offTime = Sim.now();
			double now = Sim.now();

			Queue queue = getQueueingNode().getQueue();
			if (!queue.isEmpty()) {
				Customer head = queue.head();
				if (!head.isOff()) {
					head.setOff(true);
					head.setArriveForService(now);
				}
			}

			double newDelay = 0;
			double delay = delayOff.next();
			double addition = now + delay;

			if (addition > duration) {
				newDelay = duration - now;
				setDurationOff(newDelay);
				atOn = duration - now;
				Sim.schedule(new On(addition, duration));
			} else {
				setDurationOff(delay);
				atOn = addition;
				Sim.schedule(new On((now + delay), duration));
			}

		}
	}

	public double getDurationOn() {
		return durationOn;
	}

	public void setDurationOn(double durationOn) {
		this.durationOn = this.durationOn + durationOn;
	}

	public double getDurationOff() {
		return durationOff;
	}

	public void setDurationOff(double durationOff) {
		this.durationOff = this.durationOff + durationOff;
	}

}
