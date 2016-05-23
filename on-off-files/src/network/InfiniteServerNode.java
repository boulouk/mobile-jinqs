package network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import examples.ServerOnOff;

import tools.*;

public class InfiniteServerNode extends Node {
	Delay serviceTime;
	EndServiceEvent lastEndServiceEvent;

	public InfiniteServerNode(Delay d) {
		super("Delay Node");
		serviceTime = d;
	}

	public InfiniteServerNode(String s, Delay d) {
		super(s);
		serviceTime = d;
	}

	//
	// Invokes a service delay when called. After the delay, the method
	// forward is called, which can be overridden to effect
	// special behaviours.
	//
	protected final void invokeService(Customer c) {

		double serveTime = c.getServiceDemand();
		Debug.trace("Customer " + c.getId() + " entering service, "
				+ "service time = " + serveTime);

		lastEndServiceEvent = new EndServiceEvent(c, Sim.now() + serveTime);
		Sim.schedule(lastEndServiceEvent);

	}

	// Modification for DataSet
//	protected final void invokeService(Customer c, double populationDemand, double demandRate) {
	protected final void invokeService(Customer c, double populationDemand) {
		double populationDemand1 = 600 / populationDemand;
//		double populationDemand1 = (600 * 449) / populationDemand;
//		double serveTime = c.getServiceDemand() * populationDemand1;
		
		double serveTime = 0;
		
//		double TON = 600 * (populationDemand/499);
//		double TOFF = 600 * (1 - (populationDemand/499));
//		double R_part3 = 1 - (demandRate * c.getServiceDemand() * ((TON+TOFF) / TON));
//		if(R_part3 <0){
//			serveTime = 0;
//		} else 
			serveTime = c.getServiceDemand() * populationDemand1;
		
//		double serveTime = populationDemand;
//		double serveTime = c.getServiceDemand();
//		System.err.println(serveTime);
		Network.computeSTOFF(serveTime);
		
		Debug.trace("Customer " + c.getId() + " entering service, "
				+ "service time = " + serveTime);

		lastEndServiceEvent = new EndServiceEvent(c, Sim.now() + serveTime);
		Sim.schedule(lastEndServiceEvent);

	}

	class EndServiceEvent extends Event {
		Customer customer;

		public EndServiceEvent(Customer c, double t) {
			super(t);
			customer = c;
		}

		public void invoke() {
			if (customer.isWithLifetime()) {
				if ((Sim.now() - customer.getQueueingNodeInsertionTime()) >= customer.getLifetime()) {
					customer.setExpired(true);
				}
			}

			forward(customer);
		}

		public Customer getCustomer() {
			return customer;
		}
	}

	//
	// The service demand is set on entry as the customer may be preempted
	// by a subclass. In preemptive-resume strategies, this case the
	// demand needs to be reduced to reflect the residual service time.
	// This method is finalised - subclasses should modify behaviour via
	// accept().
	//
	public final void enter(Customer c) {
		c.setServiceDemand(serviceTime.sample(c));
		super.enter(c);
	}

	//
	// Overrides superclass method. An arriving customer is now subject to
	// delay.
	//
	protected void accept(Customer c) {
		invokeService(c);
	}

}
