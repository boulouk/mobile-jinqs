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
	Customer currentCustomer;

	public InfiniteServerNode(Delay d) {
		super("Delay Node");
		serviceTime = d;
		currentCustomer = null;
	}

	public InfiniteServerNode(String s, Delay d) {
		super(s);
		serviceTime = d;
		currentCustomer = null;
	}
	
	

	public Customer getCurrentCustomer() {
		return currentCustomer;
	}

	public void setCurrentCustomer(Customer currentCustomer) {
		this.currentCustomer = currentCustomer;
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
		
		
//		if (c.isMiddleCustomer()) {
//			lastEndServiceEvent = new EndServiceEvent(c, Sim.now() + c.getMiddleServiceDemand1());
//			Sim.schedule(lastEndServiceEvent);
//		} else {
//			lastEndServiceEvent = new EndServiceEvent(c, Sim.now() + serveTime);
//			Sim.schedule(lastEndServiceEvent);
//		}

	}

	class EndServiceEvent extends Event {
		Customer customer;

		public EndServiceEvent(Customer c, double t) {
			super(t);
			customer = c;
			currentCustomer = customer;
		}

		public void invoke() {
			if (!customer.isMiddleCustomer()){
				if (customer.getLifetime() != -1) {
					if ((Sim.now() - customer.getArrivalTime()) >= customer.getLifetime()) {
						customer.setExpired(true);
					}
				}
				
				
				forward(customer);
			}
			
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
