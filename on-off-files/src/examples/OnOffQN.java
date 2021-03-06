package examples;

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


public class OnOffQN extends QueueingNode {
	public static boolean con;
	public DistributionSampler lifetimeDistrib;
	public String QueueingNodeName;
	
	
	

	public OnOffQN(String s, Delay d, int n, String distribType, double value) {
		super(s, d, n);
		con = true;
		if(distribType.equals("Exp")){
			lifetimeDistrib = new Exp(value);
		} else if (distribType.equals("Det")) {
			lifetimeDistrib = new Deterministic(value);
		} else {
			lifetimeDistrib = null;
		}
		

		QueueingNodeName = this.getName();
		
	}
	

	@Override
	protected void accept(Customer c) {
		if (con) {
			//set timer
			//Sim.schedule(new CheckLifetime(c, (Sim.now() + lifetimeDistrib.next())));
			
			//set lifetime and insertion time to check the condition when served
			if(lifetimeDistrib != null) {
				c.setQueueingNodeInsertionTime(Sim.now());
				c.setLifetime(lifetimeDistrib.next());
				c.setWithLifetime(true);
			}
			
			if (resources.resourceIsAvailable() && queue.isEmpty()) {
				
				Debug.trace("Resource claimed");
				resources.claim();
				
				c.setArriveForService(Sim.now());
				c.setArriveForServiceON(Sim.now());
				c.setOff(false);
				
//				double time = Sim.now();
//			    try {
//					String invokeTimeStr = String.valueOf(time);
//
//					String content = "Invoke Time: " + invokeTimeStr + " ARRIVAL of: " + c.getId() + " on the beginning";
//
//					File file = new File("diary.txt");
//
//					if (!file.exists()) {
//						file.createNewFile();
//					}
//
//					FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//					BufferedWriter bw = new BufferedWriter(fw);
//					bw.write(content);
//					bw.write("\n");
//					bw.close();
//
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
				double serveTime = c.getServiceDemand() ;
			   if ((Sim.now() + serveTime) < ServerOnOff.nextOff) {
				   invokeService(c);
			   }else {
				   resources.release();
			   }
				
			} else if (resources.resourceIsAvailable() && (!queue.isEmpty())) {
				Debug.trace("No resources. Enqueueing customer...");
				queue.enqueue(c);
				
				//--- Virtual Service Time ----------
//				Customer head = queue.head();
//				if(c.getId() == head.getId()){
//					c.setArriveForService(Sim.now());
//					c.setArriveForServiceON(Sim.now());
					
//					double time = Sim.now();
//				    try {
//						String invokeTimeStr = String.valueOf(time);
//
//						String content = "Invoke Time: " + invokeTimeStr + " ARRIVAL is HEAD off: "+ c.getId() + " In state ON aaaaaaaaaaaaaaaaaaa";
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
//						bw.close();
//
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//				}
				//------------------------------------
				
				Debug.trace("Resource claimed");
				resources.claim();
				releaseResource();
			} else if (!resources.resourceIsAvailable()) {
				Debug.trace("No resources. Enqueueing customer...");		
				queue.enqueue(c);
				
				//--- Virtual Service Time ----------
//				Customer head = queue.head();
//				if(c.getId() == head.getId()){
//					c.setArriveForService(Sim.now());
//					c.setArriveForServiceON(Sim.now());
					
//					double time = Sim.now();
//				    try {
//						String invokeTimeStr = String.valueOf(time);
//
//						String content = "Invoke Time: " + invokeTimeStr + " ARRIVAL is HEAD off: "+ c.getId() + " In state ON";
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
//						bw.close();
//
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//				}
				//------------------------------------
				
			}
			
		} else {
			//set timer
			//Sim.schedule(new CheckLifetime(c, (Sim.now() + lifetimeDistrib.next())));
			
			//set lifetime and insertion time to check the condition when served
			if(lifetimeDistrib != null) {
				c.setQueueingNodeInsertionTime(Sim.now());
				c.setLifetime(lifetimeDistrib.next());
				c.setWithLifetime(true);
			}
			
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
					
						
						
//						double time = Sim.now();
//					    try {
//							String invokeTimeStr = String.valueOf(time);
//	
//							String content = "Invoke Time: " + invokeTimeStr + " ARRIVAL in HEAD off: " + c.getId()  + " In OFF state";
//							String content2 = "Service demand: " + c.getServiceDemand();
//	
//							File file = new File("diary.txt");
//	
//							if (!file.exists()) {
//								file.createNewFile();
//							}
//	
//							FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//							BufferedWriter bw = new BufferedWriter(fw);
//							bw.write(content);
//							bw.write("\n");
//							bw.write(content2);
//							bw.write("\n");
//							bw.close();
//	
//						} catch (IOException e1) {
//							e1.printStackTrace();
//						}
						
//					}
					
				} else {
					queue.enqueue(c);
				}
//					Customer head = queue.head();
//					if(!head.isOff()){
//						head.setArriveForService(Sim.now());
//						head.setArriveForServiceOFF(Sim.now());
//						head.setOff(true);
//						
//						double time = Sim.now();
//					    try {
//							String invokeTimeStr = String.valueOf(time);
//
//							String content = "Invoke Time: " + invokeTimeStr + " Change in HEAD off: " + head.getId()  + " In OFF state";
//
//							File file = new File("diary.txt");
//
//							if (!file.exists()) {
//								file.createNewFile();
//							}
//
//							FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//							BufferedWriter bw = new BufferedWriter(fw);
//							bw.write(content);
//							bw.write("\n");
//							bw.close();
//
//						} catch (IOException e1) {
//							e1.printStackTrace();
//						}
//						
//					}
//					
//					
//					
//					
//				}
				
				//------------------------------------
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
			   if ((now + serveTime) < ServerOnOff.nextOff) {
				   
					Customer c = queue.dequeue();
					
//					if(!queue.isEmpty()) {
//						Customer head = queue.head();
//						head.setArriveForService(Sim.now());
//						head.setArriveForServiceON(Sim.now());
						
	//					double time = Sim.now();
	//				    try {
	//						String invokeTimeStr = String.valueOf(time);
	//
	//						String content = "Invoke Time: " + invokeTimeStr + " ARRIVAL in HEAD of: " + head.getId() + " after dequeue customer: " + c.getId();
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
	//						bw.close();
	//
	//					} catch (IOException e1) {
	//						e1.printStackTrace();
	//					}
						
//					}
					if (!c.isOff()) {
						c.setArriveForService(now);
						c.setArriveForServiceON(now);
					} 
					invokeService(c);
			   } else {
				   
				 //This is the case that an on off period is really small and messages can not served during that period (ServerOnOff.nextOff - ServerOnOff.onTime)
				 if(tempHead.isOff()){
					   tempHead.setBadLack(true);
//					   System.out.println(ServerOnOff.nextOff - ServerOnOff.onTime);
//					   Network.computeSTOFF(ServerOnOff.onTime - ServerOnOff.offTime);
//					   ServerOnOff.counter2++;
					   
//					    try {
//							String invokeTimeStr = String.valueOf(Sim.now());
//		
//							String content = "Invoke Time: " + invokeTimeStr + " Back Lack: " + tempHead.getId()  + " In ON state";
//		
//							File file = new File("diary.txt");
//		
//							if (!file.exists()) {
//								file.createNewFile();
//							}
//		
//							FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//							BufferedWriter bw = new BufferedWriter(fw);
//							bw.write(content);
//							bw.write("\n");
//							bw.close();
//		
//						} catch (IOException e1) {
//							e1.printStackTrace();
//						}
				   }
				   resources.release();
			   }
				
			} else {
				resources.release();
			}
	}
	
	
	
	//For the Impatient Customers
	class CheckLifetime extends Event {
		Customer customer ;
		public CheckLifetime(Customer c, double t) {
			super(t);
			customer = c ;
		}

		public void invoke() {
			
			if(((customer.getLocation()).toString()).equals(QueueingNodeName.toString())){
				losses++;
				if(!queue.isEmpty()){
//					if(queue.head().getId() == customer.getId()){
						Customer c = queue.dequeue();
						if(c.getId() == customer.getId())
							System.out.println("anee");
//					}
				}
					
				Debug.trace("No resources. Queue full - customer sent to "
						+ lossNode.getId());
				lossNode.enter(customer);
			}
//			System.err.println(customer.getLocation());
//			System.err.println(QueueingNodeName);
		}
	}
	
	
	
}
