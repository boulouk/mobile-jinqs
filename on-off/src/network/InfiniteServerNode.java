package network ;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import examples.ServerOnOff;

import tools.* ;

public class InfiniteServerNode extends Node {
  Delay serviceTime ;
  EndServiceEvent lastEndServiceEvent ;
  
  public InfiniteServerNode( Delay d ) {
    super( "Delay Node" ) ;
    serviceTime = d ;
  }
 
  public InfiniteServerNode( String s, Delay d ) {
    super( s ) ;
    serviceTime = d ;
  }
 
//
// Invokes a service delay when called. After the delay, the method
// forward is called, which can be overridden to effect
// special behaviours.
//
  protected final void invokeService( Customer c ) {
    double serveTime = c.getServiceDemand() ;
    Debug.trace( "Customer " + c.getId() + " entering service, " + "service time = " + serveTime ) ;
    
	lastEndServiceEvent = new EndServiceEvent( c, Sim.now() + serveTime ) ;
	Sim.schedule( lastEndServiceEvent ) ;
    
    
  }

  class EndServiceEvent extends Event {
    Customer customer ;
    public EndServiceEvent( Customer c, double t ) {
      super( t ) ;
      customer = c ;
    }
    public void invoke() {
    	
    	Network.computeVirtualST(Sim.now() - customer.getArriveForService());
    	if (customer.isOff()) {
    		
    		//This is the case that an on off period is really small and messages can not served during that period
//    		if(!(customer.getArriveForServiceOFF() < ServerOnOff.offTime)){
//    			Network.computeSTOFF(Sim.now() - customer.getArriveForServiceOFF());
//        		Network.computeSTOFFServiceTime(customer.getServiceDemand());
//    		} else {
//    			ServerOnOff.counter1++;
//    		}
    		
    		//This is the case that an on off period is really small and messages can not served during that period
//    		if(!customer.isBadLack()){
			Network.computeSTOFF(Sim.now() - customer.getArriveForServiceOFF());
    		Network.computeSTOFFServiceTime(customer.getServiceDemand());
//    		}
    		
    		
    		
    		
//    		double time = Sim.now();
//    	    try {
//    			String invokeTimeStr = String.valueOf(time);
//
//    			String content = "Invoke Time: " + invokeTimeStr + " SERVE of: " + customer.getId() + " For time: " + (Sim.now() - customer.getArriveForServiceOFF());
//    			String content2 = "ON Off PERIOD: " + ((ServerOnOff.onTime - ServerOnOff.offTime) + customer.getServiceDemand());
//
//    			File file = new File("diary.txt");
//
//    			if (!file.exists()) {
//    				file.createNewFile();
//    			}
//
//    			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//    			BufferedWriter bw = new BufferedWriter(fw);
//    			bw.write(content);
//    			bw.write("\n");
//    			bw.write(content2);
//    			bw.write("\n");
//    			bw.close();
//
//    		} catch (IOException e1) {
//    			e1.printStackTrace();
//    		}
    	} else {
    		Network.computeSTON(Sim.now() - customer.getArriveForServiceON());
    		
    		
//    		double time = Sim.now();
//    	    try {
//    			String invokeTimeStr = String.valueOf(time);
//
//    			String content = "Invoke Time: " + invokeTimeStr + " SERVE of: " + customer.getId() + " For time: " + (Sim.now() - customer.getArriveForServiceON());
//
//    			File file = new File("diary.txt");
//
//    			if (!file.exists()) {
//    				file.createNewFile();
//    			}
//
//    			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//    			BufferedWriter bw = new BufferedWriter(fw);
//    			bw.write(content);
//    			bw.write("\n");
//    			bw.close();
//
//    		} catch (IOException e1) {
//    			e1.printStackTrace();
//    		}
    	}
    	
//		double time = Sim.now();
//	    try {
//			String invokeTimeStr = String.valueOf(time);
//
//			String content = "Invoke Time: " + invokeTimeStr + " SERVE of: " + customer.getId() + " For time: " + (Sim.now() - customer.getArriveForService());
//			String content2 = "service demand: " + customer.getServiceDemand();
//
//			File file = new File("diary.txt");
//
//			if (!file.exists()) {
//				file.createNewFile();
//			}
//
//			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//			BufferedWriter bw = new BufferedWriter(fw);
//			bw.write(content);
//			bw.write("\n");
//			bw.write(content2);
//			bw.write("\n");
//			bw.close();
//
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
    	
    	forward( customer ) ;
    }
    public Customer getCustomer() {
      return customer ;
    }
  }
  
//
// The service demand is set on entry as the customer may be preempted
// by a subclass.  In preemptive-resume strategies, this case the
// demand needs to be reduced to reflect the residual service time.
// This method is finalised - subclasses should modify behaviour via 
// accept().
//
  public final void enter( Customer c ) {
    c.setServiceDemand( serviceTime.sample( c ) ) ;
    super.enter( c ) ;
  }
  
//
// Overrides superclass method.  An arriving customer is now subject to 
// delay.  
//
  protected void accept( Customer c ) {
    invokeService( c ) ;
  }
  
}

