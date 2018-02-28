package examples;

import java.util.Iterator;
import java.util.Map;

import extensions.SinkMulticlass;
import extensions.SinkPriorities;
import network.*;
import tools.*;

class MulticlassSim extends Sim {
	
	public DistributionSampler insertProb;

	public MulticlassSim() {
		Network.initialise();
		
		double mu1 = 8;
		double mu2 = 16;
		double mu3 = 16;
		
		double D1 = 1/mu1;
		double D2 = 1/mu2;
		double D3 = 1/mu3;
		
		int[] cs = { 1, 2, 3 } ;
		DistributionSampler[] ds = { new Exp(mu1), new Exp(mu2), new Exp(mu3) } ;
		ClassDependentDelay overalDelay = new ClassDependentDelay( cs, ds ) ;

		double lambda1 = 0.5;
		double lambda2 = 1;
		double lambda3 = 2;
		
		Node source1 = new ClassSource( "Source", new Exp( lambda1 ), 1 ) ;
		Node source2 = new ClassSource( "Source", new Exp( lambda2 ), 2 ) ;
		Node source3 = new ClassSource( "Source", new Exp( lambda3 ), 3 ) ;
		
		QueueingNode mm1 = new QueueingNode("MM1", overalDelay, 1);
		SinkMulticlass sinkMulticlass = new SinkMulticlass("Sink Multiclass");


		source1.setLink(new Link(mm1));
		source2.setLink(new Link(mm1));
		source3.setLink(new Link(mm1));
		mm1.setLink(new Link(sinkMulticlass));

		simulate();
		
		double r1 = D1 / (1 - ((lambda1 * D1) + (lambda2 * D2) + (lambda3 * D3)));
		double r2 = D2 / (1 - ((lambda1 * D1) + (lambda2 * D2) + (lambda3 * D3)));
		double r3 = D3 / (1 - ((lambda1 * D1) + (lambda2 * D2) + (lambda3 * D3)));
		
		System.out.println("MODEL: Response Time class 1: " + r1);
		System.out.println("MODEL: Response Time class 2: " + r2);
		System.out.println("MODEL: Response Time class 3: " + r3);

		
		Network.logResult("Mean Queue Size", mm1.meanNoOfQueuedCustomers());
	
		Iterator entries = Network.responseTimeClassMap.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry entry = (Map.Entry) entries.next();
		    Integer key = (Integer)entry.getKey();
		    CustomerMeasure value = (CustomerMeasure)entry.getValue();
		    System.out.println("SIM: Response Time Class " + key + ", Value = " + value.mean());
		}
		
//		Network.logResults();
	}

	public boolean stop() {
		return Network.completions == 1000000;
	}

	public static void main(String args[]) {

		new MulticlassSim();
		Network.displayResults(0.01);

	}

	class ClassSource extends Source {
		int cType = 0;
		public ClassSource(String name, DistributionSampler d, int classType) {
			super(name, d);
			cType = classType;
		}

		protected Customer buildCustomer() {
				return new Customer(cType);
			
		}
	}
}
