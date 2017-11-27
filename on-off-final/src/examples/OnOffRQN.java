package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import tools.DistributionSampler;
import tools.Exp;
import tools.Sim;
import network.*;

public class OnOffRQN extends QueueingNode {
	public boolean con;
	public static double lifetime;
	public ServerOnOff serverOnOff;
	public int customersEndOn;
	public double serveTimeCustEndOn;
	public double durationCustomersEndOn;

	public static final Object resourcesLock = new Object();

	public double getServeTimeCustEndOn() {
		return serveTimeCustEndOn;
	}

	public void setServeTimeCustEndOn(double serveTimeCustEndOn) {
		this.serveTimeCustEndOn = serveTimeCustEndOn;
	}

	public double getDurationCustomersEndOn() {
		return durationCustomersEndOn;
	}

	public void setDurationCustomersEndOn(double durationCustomersEndOn) {
		this.durationCustomersEndOn = durationCustomersEndOn;
	}

	public int getCustomersEndOn() {
		return customersEndOn;
	}

	public void setCustomersEndOn(int customersEndOn) {
		this.customersEndOn = customersEndOn;
	}

	public static double getLifetime() {
		return lifetime;
	}

	public OnOffRQN(String s, Delay d, int n, DistributionSampler on, DistributionSampler off, double dur) {
		super(s, d, n);
		con = true;
		serverOnOff = new ServerOnOff(on, off, dur, this);
	}

	public OnOffRQN(String s, Delay d, int n, Queue q, DistributionSampler on, DistributionSampler off, double dur) {
		super(s, d, n, q);
		con = true;
		serverOnOff = new ServerOnOff(on, off, dur, this);
	}

	public boolean isCon() {
		return this.con;
	}

	public void setCon(boolean con) {
		this.con = con;
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

						// double dur = this.getDurationCustomersEndOn();
						// this.setDurationCustomersEndOn(dur +
						// c.getMiddleServiceDemand1() +
						// c.getMiddleServiceDemand2());

						int cust = this.getCustomersEndOn();
						this.setCustomersEndOn(cust + 1);

						// double ser = this.getServeTimeCustEndOn();
						// this.setServeTimeCustEndOn(ser + serveTime);

						invokeService(c);

						// c.setServiceDemand(serveTime - delayDif);

						// resources.release();
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
				
				if (queue.isEmpty()) {
					queue.enqueue(c);
					c.setOff(true);
					double now = Sim.now();
					c.setArriveForService(now);

				} else {
					queue.enqueue(c);
				}

			} else {
				losses++;
				Debug.trace("No resources. Queue full - customer sent to " + lossNode.getId());
				lossNode.enter(c);
			}
		}

	}

	public void serveMiddleCustomers() {

		if (this.getCurrentCustomer() != null && this.getCurrentCustomer().isMiddleCustomer()) {

			Customer c = this.getCurrentCustomer();

			c.setServiceDemand(c.getMiddleServiceDemand2());

			synchronized (Customer.middleLock) {
				c.setMiddleCustomer(false);
			}

			// System.out.println("after on: " + c.getId() + "time: " +
			// Sim.now() + " queue " + this.getName() + " middle: " +
			// c.isMiddleCustomer());

			invokeService(c);

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

				// double dur = this.getDurationCustomersEndOn();
				// this.setDurationCustomersEndOn(dur +
				// c.getMiddleServiceDemand1() + c.getMiddleServiceDemand2());

				int cust = this.getCustomersEndOn();
				this.setCustomersEndOn(cust + 1);

				// double ser = this.getServeTimeCustEndOn();
				// this.setServeTimeCustEndOn(ser + serveTime);

				invokeService(c);

				//
				// double delayDif = serverOnOff.getNextOff() - Sim.now();
				// double dur = this.getDurationCustomersEndOn();
				// this.setDurationCustomersEndOn(dur + delayDif);

				// This is the case that an on off period is really small and
				// messages can not served during that period
				// (ServerOnOff.nextOff - ServerOnOff.onTime)
				// if (tempHead.isOff()) {
				// tempHead.setBadLack(true);
				// }

				// tempHead.setServiceDemand(serveTime - delayDif);
				// queue.head().setServiceDemand(serveTime - delayDif);

				// queue.dequeue();
				// invokeService(c);

				// resources.release();
			}

		} else {

			synchronized (resourcesLock) {
				if (!resources.resourceIsAvailable())
					resources.release();

			}

		}
	}

}
