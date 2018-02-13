package examples;

import network.*;
import tools.*;

class PrioritySim extends Sim {
	
	public DistributionSampler insertProb;

	public PrioritySim() {
		Network.initialise();
		Delay serveTime = new Delay(new Exp(8));

//		Source source = new Source("Source", new Exp(1));
		
		double prob = 0.5;
		insertProb = new Geometric(prob);
		Node source = new PrioSource( "Source", new Exp( 1 ) ) ;

		QueueingNode mm1 = new QueueingNode("MM1", serveTime, 1);
		Sink sink = new Sink("Sink");

		PriorityQueue pq = new PriorityQueue(1);
		QueueingNode pr_qn = new QueueingNode("PR_QN", serveTime, 1, pq);

		source.setLink(new Link(pr_qn));
		pr_qn.setLink(new Link(sink));

		simulate();

		Network.logResult("Utilisation", mm1.serverUtilisation());
		Network.logResult("Response Time", Network.responseTime.mean());
		Network.logResult("Mean Queue Size", mm1.meanNoOfQueuedCustomers());
		// Network.logResults();
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

	class PrioCustomer extends Customer {

		public PrioCustomer(int customerClass, int priority) {
			super(customerClass, priority);
		}
	}

	class PrioSource extends Source {
		public PrioSource(String name, DistributionSampler d) {
			super(name, d);
		}

		protected Customer buildCustomer() {
			if (insertProb.next() == 0) {
				return new PrioCustomer(1, 0);
			} else 
				return new PrioCustomer(1, 1);
			
		}
	}
}
