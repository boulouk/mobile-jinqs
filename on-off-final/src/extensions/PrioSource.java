package extensions ;

import network.Customer;
import network.Source;
import tools.DistributionSampler;

public class PrioSource extends Source {
	int cType = 0;
	int prio = 0;
	public PrioSource(String name, DistributionSampler d, int classType, int priotiry) {
		super(name, d);
		cType = classType;
		prio = priotiry;
	}

	protected Customer buildCustomer() {
			return new Customer(cType, prio);
	}
}

