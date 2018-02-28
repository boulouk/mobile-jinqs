package examples;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import extensions.PrioSource;
import extensions.SinkMulticlass;
import extensions.SinkPriorities;
import network.*;
import tools.*;

class PrioritySim extends Sim {

	public PrioritySim() {
		Network.initialise();
		
		double mu = 8;
		double D = 1/mu;
		Delay serveTime = new Delay(new Exp(mu));

//		Source source = new Source("Source", new Exp(1));

		Map<Integer, Double> lambdaMap = new HashMap<Integer, Double>();
		
		double lambda0 = 0.2;
		lambdaMap.put(1, lambda0);
		double lambda1 = 0.5;
		lambdaMap.put(2, lambda1);
		double lambda2 = 1;
		lambdaMap.put(3, lambda2);
		double lambda3 = 1.1;
		lambdaMap.put(4, lambda3);
		
		
		
		
		Node source0 = new PrioSource( "Source", new Exp( lambda0 ), 1, 0 ) ;
		Node source1 = new PrioSource( "Source", new Exp( lambda1 ), 2, 1 );
		Node source2 = new PrioSource( "Source", new Exp( lambda2 ), 3, 2 );
		Node source3 = new PrioSource( "Source", new Exp( lambda3 ), 4, 3 );
		
//		QueueingNode mm1 = new QueueingNode("MM1", serveTime, 1);
		SinkPriorities sink_prio = new SinkPriorities("Sink Priorities");

//		SinkMulticlass sink_prio = new SinkMulticlass("Sink Priorities");

		
		PriorityQueue prioq = new PriorityQueue(4);
		QueueingNode prio_qn = new QueueingNode("PR_QN", serveTime, 1, prioq);

		source0.setLink(new Link(prio_qn));
		source1.setLink(new Link(prio_qn));
		source2.setLink(new Link(prio_qn));
		source3.setLink(new Link(prio_qn));
		prio_qn.setLink(new Link(sink_prio));

		simulate();
		
		double ro0 = lambda0 / mu;
		double ro1 = lambda1 / mu;
		double ro2 = lambda2 / mu;
		double ro3 = lambda3 / mu;
		
//		double R0 = ((1 + ro1) / mu) / (1 - ro0);
//		double R1 = ((1 - (ro0 * (1 - ro0 - ro1))) / mu) / ((1 - ro0)*(1 - ro0 - ro1));
		
		double RESIDUAL = 0.5 * (lambda0*Math.pow(D, 2) + (lambda1*Math.pow(D, 2)));
		
		double L0 = ((ro0/mu)) / (1-ro0); 
		double L1 = (((ro0/mu)+(ro1/mu))) / ((1-ro0) * (1-ro0-ro1)); 
		double L2 = (((ro0/mu)+(ro1/mu)+(ro2/mu))) / ((1-ro0-ro1) * (1-ro0-ro1-ro2));
		double L3 = (((ro0/mu)+(ro1/mu)+(ro2/mu)+(ro3/mu))) / ((1-ro0-ro1-ro2) * (1-ro0-ro1-ro2-ro3)); 
		
		double R0 = (L0 + (lambda0/mu))/lambda0;
		double R1 = (L1 + (lambda1/mu))/lambda1;
		double R2 = (L2 + (lambda2/mu))/lambda2;
		double R3 = (L3 + (lambda3/mu))/lambda3;
		
//		double R0 = RESIDUAL0 / (1 - ro0);
//		double R1 = RESIDUAL1 / (1 - ro0) * (1 - ro0 - ro1);
		
		Network.logResult("Response Time", Network.responseTime.mean());
		Network.logResult("Mean Queue Size", prio_qn.meanNoOfQueuedCustomers());

//		System.out.println("MODEL: Response Time Prio 0 " + "Value = " + R0);
//		System.out.println("MODEL: Response Time Prio 1 " + "Value = " + R1);
//		System.out.println("MODEL: Response Time Prio 2 " + "Value = " + R2);
//		System.out.println("MODEL: Response Time Prio 3 " + "Value = " + R3);
		
		Iterator entries = Network.responseTimePrioMap.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry entry = (Map.Entry) entries.next();
		    Integer key = (Integer)entry.getKey();
		    CustomerMeasure value = (CustomerMeasure)entry.getValue();
		    System.out.println("SIM: Response Time Prio " + key + ", Value = " + value.mean());
		}
		
		double overalambda = 0;
		Iterator entries2 = lambdaMap.entrySet().iterator();
		while (entries2.hasNext()) {
			Map.Entry entry = (Map.Entry) entries2.next();
			Integer key = (Integer) entry.getKey();
			Double lambdavalue = (Double) entry.getValue();

			overalambda = overalambda + lambdavalue;
		}
		
		System.out.println("Overal lambda: " + overalambda);
		

		Iterator entries3 = lambdaMap.entrySet().iterator();
		double denominator_part1 = 0;
		double denominator_part2 = 0;
		int j = 1;
		int size = lambdaMap.size();
		double R_prio = 0;
		System.out.println(size);
		while (entries3.hasNext()) {
			Map.Entry entry = (Map.Entry) entries3.next();
			Integer key = (Integer) entry.getKey();
			Double lambdavalue = (Double) entry.getValue();

			if (j < size) {
				denominator_part1 = denominator_part1 + lambdavalue;
				
			}
			denominator_part2 = denominator_part2 + lambdavalue;
			
			R_prio = (overalambda / ((mu - denominator_part1) *(mu - denominator_part2))) + D;
			
			System.out.println("MODEL: Response Time Prio " + key + " Value = " + R_prio);
			
			j++;
		}
		
		
		
		
		
		
//		Network.logResults();
	}

	public boolean stop() {
		return Network.completions == 1000000;
	}

	public static void main(String args[]) {
		// new PrioritySim() ;
		// new PrioritySim() ;
		new PrioritySim();
		Network.displayResults(0.01);

	}
}
