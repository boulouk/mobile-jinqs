package examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import extensions.PrioSource;
import extensions.QNPrioProbs;
import extensions.SinkMulticlass;
import extensions.SinkPriorities;
import network.*;
import pubsubpriorities.AnalyticalModels;
import tools.*;

class PrioritySim extends Sim {

	public PrioritySim() {
		Network.initialise();

		double mu = 8;
		double D = 1 / mu;
		Delay serveTime = new Delay(new Exp(mu));

		// Source source = new Source("Source", new Exp(1));

		Map<Integer, Double> lambdaMap = new HashMap<Integer, Double>();

		double lambda0 = 1;
		lambdaMap.put(0, lambda0);
		double lambda1 = 1;
		lambdaMap.put(1, lambda1);
		double lambda2 = 1;
		lambdaMap.put(2, lambda2);
		double lambda3 = 1;
		lambdaMap.put(3, lambda3);
		double lambda4 = 1;
		lambdaMap.put(4, lambda4);
		double lambda5 = 1;
		lambdaMap.put(5, lambda5);

		Node source0 = new PrioSource("Source", new Exp(lambda0), 0, 0);
		Node source1 = new PrioSource("Source", new Exp(lambda1), 1, 1);
		Node source2 = new PrioSource("Source", new Exp(lambda2), 2, 2);
		Node source3 = new PrioSource("Source", new Exp(lambda3), 3, 3);
		Node source4 = new PrioSource("Source", new Exp(lambda3), 4, 4);
		Node source5 = new PrioSource("Source", new Exp(lambda3), 5, 5);

		// QueueingNode mm1 = new QueueingNode("MM1", serveTime, 1);
		SinkPriorities sink_prio = new SinkPriorities("Sink Priorities");

		// SinkMulticlass sink_prio = new SinkMulticlass("Sink Priorities");

		int noprio = 5;
		ArrayList<Double> prioprobs = new ArrayList<Double>(noprio);
		prioprobs.add(1.0);
		prioprobs.add(1.0);
		prioprobs.add(1.0);
		prioprobs.add(1.0);
		prioprobs.add(1.0);
		prioprobs.add(1.0);

		PriorityQueue prioq = new PriorityQueue(6);
		QNPrioProbs prio_qn = new QNPrioProbs("PR_QN", serveTime, 1, prioq, prioprobs);
//		QueueingNode prio_qn = new QueueingNode("PR_QN", serveTime, 1, prioq);

		source0.setLink(new Link(prio_qn));
		source1.setLink(new Link(prio_qn));
		source2.setLink(new Link(prio_qn));
		source3.setLink(new Link(prio_qn));
		source4.setLink(new Link(prio_qn));
		source5.setLink(new Link(prio_qn));
		prio_qn.setLink(new Link(sink_prio));

		simulate();

		Network.logResult("Response Time", Network.responseTime.mean());
		Network.logResult("Mean Queue Size", prio_qn.meanNoOfQueuedCustomers());


		Iterator entries = Network.responseTimePrioMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Integer key = (Integer) entry.getKey();
			CustomerMeasure value = (CustomerMeasure) entry.getValue();
			System.out.println("SIM: Response Time Prio " + key + ", Value = " + value.mean());
		}

		double overalambda = 0;
		double sum_ro_div_mu = 0;
		Iterator entries2 = lambdaMap.entrySet().iterator();
		while (entries2.hasNext()) {
			Map.Entry entry = (Map.Entry) entries2.next();
			Integer key = (Integer) entry.getKey();
			Double lambdavalue = (Double) entry.getValue();

			overalambda = overalambda + lambdavalue;
		}

		System.out.println("Overal lambda: " + overalambda);

		System.out.println("MM1: " + D/(1-(overalambda*D)));
		
		Iterator entries3 = lambdaMap.entrySet().iterator();
		double denominator_part1 = 0;
		double denominator_part2 = 0;
		int j = 0;
		int size = lambdaMap.size();
		double R_prio = 0;
		System.out.println(size);
		while (entries3.hasNext()) {
			Map.Entry entry = (Map.Entry) entries3.next();
			Integer key = (Integer) entry.getKey();
			Double lambdavalue = (Double) entry.getValue();
					
			System.out.println("MODEL: Response Time Prio " + key + " Value = " + AnalyticalModels.r_prio(lambdaMap, key, mu));

		}

		
		System.out.println("Completions: " + Network.completions);
		System.out.println("Losses: " + prio_qn.getLosses());

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
