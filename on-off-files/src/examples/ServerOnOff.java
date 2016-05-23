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
	protected OnOffQN queueingNode;
	protected double duration = 0;
	protected double durationOn = 0;
	protected double durationOff = 0;
	
	public static double nextOff = 0;
	public static double nextOn = 0;
	
	public static double offTime = 0;
	public static double onTime = 0;
	
	public static double counter1 = 0;
	public static double counter2 = 0;
	
	public static double atOn = 0;
	public static double atOff = 0;

	public ServerOnOff(DistributionSampler dOn, DistributionSampler dOff, double d, OnOffQN qn) {
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
			
			OnOffQN.con = true;		
			onTime = Sim.now();
			Queue queue = queueingNode.getQueue();
			Resource resources = queueingNode.getResources();
						
//			double time = Sim.now();
//		    try {
//				String invokeTimeStr = String.valueOf(time);
//
//				String content = "Invoke Time: " + invokeTimeStr + " ON";
//
//				File file = new File("diary.txt");
//
//				if (!file.exists()) {
//					file.createNewFile();
//				}
//
//				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//				BufferedWriter bw = new BufferedWriter(fw);
//				bw.write(content);
//				bw.write("\n");
//				bw.close();
//
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}

			double newDelay = 0;
			double now = Sim.now();
			double delay = delayOn.next();
			if(delay == 0) {
				delay = (duration - now) + 1;
			}
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
			
			if (resources.resourceIsAvailable() && (!queue.isEmpty())) {
				resources.claim();
				
				queueingNode.releaseResource();
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
			OnOffQN.con = false;
			offTime = Sim.now();
			double now = Sim.now();
			
			
			Queue queue = queueingNode.getQueue();
			if(!queue.isEmpty()){
				Customer head = queue.head();
				if(!head.isOff()){
					head.setOff(true);
					head.setArriveForService(now);
					head.setArriveForServiceOFF(now);
					
					
					
//				    try {
//						String invokeTimeStr = String.valueOf(Sim.now());
//	
//						String content = "Invoke Time: " + invokeTimeStr + " Change in HEAD off: " + head.getId()  + " In OFF state";
//						String content2 = "Service demand: " + head.getServiceDemand();
//	
//						File file = new File("diary.txt");
//	
//						if (!file.exists()) {
//							file.createNewFile();
//						}
//	
//						FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//						BufferedWriter bw = new BufferedWriter(fw);
//						bw.write(content);
//						bw.write("\n");
//						bw.write(content2);
//						bw.write("\n");
//						bw.close();
//	
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
					
				}
			}
			
			
//			double time = Sim.now();
//		    try {
//				String invokeTimeStr = String.valueOf(time);
//
//				String content = "Invoke Time: " + invokeTimeStr + " OFF";
//
//				File file = new File("diary.txt");
//
//				if (!file.exists()) {
//					file.createNewFile();
//				}
//
//				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//				BufferedWriter bw = new BufferedWriter(fw);
//				bw.write(content);
//				bw.write("\n");
//				bw.close();
//
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
			
			double newDelay = 0;
			double delay = delayOff.next();
			if(delay == 0) {
				delay = 5;
			}
			double addition = now + delay;
			
			
			if (addition > duration){
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
