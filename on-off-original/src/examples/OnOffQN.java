package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import tools.DistributionSampler;
import tools.Exp;
import tools.Sim;
import network.*;


public class OnOffQN extends QueueingNode {
	public boolean con;
	public static double lifetime;
	public ServerOnOff serverOnOff;
	public int customersEndOn;
	public double serveTimeCustEndOn;
	public double durationCustomersEndOn;
	
	
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

	public OnOffQN(String s, Delay d, int n, DistributionSampler on, DistributionSampler off, double dur) {
		super(s, d, n);
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
		if (con) {
			if (resources.resourceIsAvailable() && queue.isEmpty()) {
				Debug.trace("Resource claimed");
				resources.claim();
				
				c.setArriveForService(Sim.now());
				c.setArriveForServiceON(Sim.now());
				c.setOff(false);
				
				double serveTime = c.getServiceDemand() ;
			   if ((Sim.now() + serveTime) < serverOnOff.getNextOff()) {
				   invokeService(c);
			   }else {
			  	 
			  	 int cust = this.getCustomersEndOn();
			  	 this.setCustomersEndOn(cust + 1);
			  	 
			  	 double delayDif = serverOnOff.getNextOff() - Sim.now();
			  	 double dur = this.getDurationCustomersEndOn();
			  	 this.setDurationCustomersEndOn(dur + delayDif);
			  	 
			  	 double ser = this.getServeTimeCustEndOn();
			  	 this.setServeTimeCustEndOn(ser + serveTime);
			  	 
				   resources.release();
			   }
				
			} else if (resources.resourceIsAvailable() && (!queue.isEmpty())) {
				Debug.trace("No resources. Enqueueing customer...");
				queue.enqueue(c);
				
				Debug.trace("Resource claimed");
				resources.claim();
				releaseResource();
			} else if (!resources.resourceIsAvailable()) {
				Debug.trace("No resources. Enqueueing customer...");		
				queue.enqueue(c);
				
			}
			
		} else {
			if (queue.canAccept(c)) {
				Debug.trace("No resources. Enqueueing customer...");			
				
				if(queue.isEmpty()) {
					queue.enqueue(c);
					//--- Virtual Service Time ----------
//					Customer head = queue.head();
//					if(c.getId() == head.getId()){
					c.setOff(true);
					double now = Sim.now();
					c.setArriveForService(now);
					c.setArriveForServiceOFF(now);
					
					
				} else {
					queue.enqueue(c);
				}

			} else {
				losses++;
				Debug.trace("No resources. Queue full - customer sent to "
						+ lossNode.getId());
				lossNode.enter(c);
			}
		}

	}
	


	
	@Override
	public void releaseResource() {
			Debug.trace(this + " releasing resource");
			if (!queue.isEmpty()) {
				
				Customer tempHead = queue.head();
				
				double serveTime = tempHead.getServiceDemand() ;
				double now = Sim.now();
			   if ((now + serveTime) < serverOnOff.getNextOff()) {
				   
					Customer c = queue.dequeue();
					
					if (!c.isOff()) {
						c.setArriveForService(now);
						c.setArriveForServiceON(now);
					} 
					invokeService(c);
			   } else {
			  	 
			  	 int cust = this.getCustomersEndOn();
			  	 this.setCustomersEndOn(cust + 1);
			  	 
			  	 double delayDif = serverOnOff.getNextOff() - Sim.now();
			  	 double dur = this.getDurationCustomersEndOn();
			  	 this.setDurationCustomersEndOn(dur + delayDif);
			  	 
			  	 double ser = this.getServeTimeCustEndOn();
			  	 this.setServeTimeCustEndOn(ser + serveTime);
				   
				 //This is the case that an on off period is really small and messages can not served during that period (ServerOnOff.nextOff - ServerOnOff.onTime)
				 if(tempHead.isOff()){
					   tempHead.setBadLack(true);

				   }
				   resources.release();
			   }
				
			} else {
				resources.release();
			}
	}
	
	
	
}
