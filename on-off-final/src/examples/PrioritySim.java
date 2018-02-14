package examples;

import extensions.SinkPriorities;
import network.*;
import tools.*;

class PrioritySim extends Sim {
	
	public DistributionSampler insertProb;

	public PrioritySim() {
		Network.initialise();
		Delay serveTime = new Delay(new Exp(8));

//		Source source = new Source("Source", new Exp(1));
		
		double prob = 0.8;
		insertProb = new Geometric(prob);
		Node source = new PrioSource( "Source", new Exp( 3.9 ) ) ;

//		QueueingNode mm1 = new QueueingNode("MM1", serveTime, 1);
		SinkPriorities sink_prio = new SinkPriorities("Sink Priorities");

		PriorityQueue prioq = new PriorityQueue(2);
		QueueingNode prio_qn = new QueueingNode("PR_QN", serveTime, 1, prioq);

		source.setLink(new Link(prio_qn));
		prio_qn.setLink(new Link(sink_prio));

		simulate();

		Network.logResult("Response Time", Network.responseTime.mean());
		Network.logResult("Mean Queue Size", prio_qn.meanNoOfQueuedCustomers());
		
		
		Network.logResult("Response Time Prio 0", Network.responseTimePrio0.mean());
		Network.logResult("Response Time Prio 1", Network.responseTimePrio1.mean());
		
		Network.logResult("Completions Prio 0", Network.completionsPrio0);
		Network.logResult("Completions Prio 1", Network.completionsPrio1);
		
		
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
