package extensions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import tools.DistributionSampler;
import tools.Exp;
import tools.Geometric;
import tools.Sim;
import network.*;

public class QNPrioProbs extends QueueingNode {

	ArrayList<Double> prioprobs;
	ArrayList<Geometric> prioDistrib;

	public QNPrioProbs(String s, Delay d, int n, Queue q, ArrayList<Double> probs) {
		super(s, d, n, q);

		prioprobs = probs;
		prioDistrib = new ArrayList<Geometric>();

		for (int i = 0; i < prioprobs.size(); i++) {
			prioDistrib.add(new Geometric(prioprobs.get(i)));
		}
	}

	@Override
	protected void accept(Customer c) {
		
		
		if (checkInsertProb(c.getPriority())) {
			if (resources.resourceIsAvailable()) {
				Debug.trace("Resource claimed");
				resources.claim();
				invokeService(c);
			} else {
				
					if (queue.canAccept(c)) {
						Debug.trace("No resources. Enqueueing customer...");
						queue.enqueue(c);
					} else {
						losses++;
						Debug.trace("No resources. Queue full - customer sent to " + lossNode.getId());
						lossNode.enter(c);
					}				
			}
		} else {
//			TODO count losses
			
			Network.dropPrioPackets++;

			// completions for each class
//			if (Network.dropPrioClassMap.get(c.getclass()) == null) {
//				Network.dropPrioClassMap.put(c.getclass(), 1);
//			} else
				Network.dropPrioClassMap.put(c.getclass(), Network.dropPrioClassMap.get(c.getclass()) + 1);
		}
			
	}

	private boolean checkInsertProb(int prio) {
		if (prioDistrib.get(prio).next() == 0) {
			return true;
		} else
			return false;

	}

}
