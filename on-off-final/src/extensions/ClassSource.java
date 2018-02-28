package extensions ;

import network.Customer;
import network.Source;
import tools.DistributionSampler;

public class ClassSource extends Source {
	int cType = 0;
	public ClassSource(String name, DistributionSampler d, int classType) {
		super(name, d);
		cType = classType;
	}

	protected Customer buildCustomer() {
			return new Customer(cType);
		
	}
}

