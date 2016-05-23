package network;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import tools.*;

public class ResourcePoolDataSet extends InfiniteServerNode {
	protected int noOfResources;
	protected static Queue queue;
	protected Resource resources;
	protected int losses = 0;
	protected Node lossNode = Network.nullNode;

	public static boolean con;
	public DistributionSampler lifetimeDistrib;
	public int maxPopoulation;
	public int line = 0;
	public int currentLine = 1;
	public double demandPopulation = 0;
	public String antenna;
	private LineNumberReader lineNumberReader = null;
	public static Exp outputServiceTime;

	public ResourcePoolDataSet(String s, Delay d, int n, String distribType,
			double value, String an, int maxPop) {
		super(s, d);
		noOfResources = n;
		queue = new FIFOQueue();
		resources = new Resource(noOfResources);

		//maxPopoulation = maxPop;
		antenna = an;

		if (!antenna.equals("Not")) {
			try {
				lineNumberReader = new LineNumberReader(new FileReader(antenna));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (distribType.equals("Exp")) {
			lifetimeDistrib = new Exp(value);
		} else if (distribType.equals("Det")) {
			lifetimeDistrib = new Deterministic(value);
		} else {
			lifetimeDistrib = null;
		}
	}

	public void setLossNode(Node n) {
		lossNode = n;
	}

	public String toString() {
		// return name + ", resources = " +
		// resources.numberOfAvailableResources() +
		// ", queue length = " + queue.queueLength() ;
		return name;

	}

	protected void accept(Customer c) {
		double demandRate = 0;
		if (lifetimeDistrib != null) {
			c.setQueueingNodeInsertionTime(Sim.now());
			c.setLifetime(lifetimeDistrib.next());
			c.setWithLifetime(true);
		}
		if (resources.resourceIsAvailable()) {
			Debug.trace("Resource claimed");
			resources.claim();

			if (antenna.equals("Not")) {
				invokeService(c);

			} else {
				int now = (int) Sim.now();
				int lineNumber = (now / 600) + 1;
				if (lineNumber != line) {
					line = lineNumber;
					demandPopulation = getServicePopulation(lineNumber);
//					demandRate = getAntennaRate(lineNumber);
//					getServicePopulation(lineNumber);
				}

				invokeService(c, demandPopulation);
//				invokeService(c, demandPopulation, demandRate);
//				invokeService(c, outputServiceTime.next());
			}

		} else {
			if (queue.canAccept(c)) {
				Debug.trace("No resources. Enqueueing customer...");
				queue.enqueue(c);
			} else {
				losses++;
				Debug.trace("No resources. Queue full - customer sent to "
						+ lossNode.getId());
				lossNode.enter(c);
			}
		}
	}

	public void releaseResource() {
		double demandRate = 0;
		Debug.trace(this + " releasing resource");
		if (!queue.isEmpty()) {
			Customer c = queue.dequeue();

			if (antenna.equals("Not")) {
				invokeService(c);

			} else {
				int now = (int) Sim.now();
				int lineNumber = (now / 600) + 1;
				if (lineNumber != line) {
					line = lineNumber;
					demandPopulation = getServicePopulation(lineNumber);
//					demandRate = getAntennaRate(lineNumber);
//					getServicePopulation(lineNumber);
				}

				invokeService(c, demandPopulation);
//				invokeService(c, demandPopulation, demandRate);
//				invokeService(c, outputServiceTime.next());
			}

		} else {
			resources.release();
		}
	}

	//
	// Useful additional methods
	//

	public int queueLength() {
		return queue.queueLength();
	}

	//
	// Measurement stuff...
	//
	public static Queue getQueue() {
		return queue;
	}

	public Resource getResources() {
		return resources;
	}

	public int getLosses() {
		return losses;
	}

	public double getLossProbability() {
		return (double) losses / (double) arrivals;
	}

	public double serverUtilisation() {
		return resources.utilisation();
	}

	public double meanNoOfQueuedCustomers() {
		return queue.meanQueueLength();
	}

	public double varianceOfNoOfQueuedCustomers() {
		return queue.varQueueLength();
	}

	public double meanTimeInQueue() {
		return queue.meanTimeInQueue();
	}

	public double varianceOfTimeInQueue() {
		return queue.varTimeInQueue();
	}

	public void resetMeasures() {
		queue.resetMeasures();
		resources.resetMeasures();
	}

	public void logResults() {
		Logger.logResult(name + ", Server utilisation", serverUtilisation());
		Logger.logResult(name + ", Mean number of customers in queue", meanNoOfQueuedCustomers());
//		Logger.logResult(name + ", Variance of number of customers in queue", varianceOfNoOfQueuedCustomers());
		Logger.logResult(name + ", Conditional mean queueing time", meanTimeInQueue());
//		Logger.logResult(name + ", Conditional variance of queueing time", varianceOfTimeInQueue());
		Logger.logResult(name + ", Losses", getLosses());
//		Logger.logResult(name + ", Proportion of customers lost", getLossProbability());
	}

	public double getServicePopulation(int position) {

		double noOfCustomers = 0;

		try {

			lineNumberReader.setLineNumber(currentLine);

			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");

				// String antennaID = lineParts[0];
				String timestamp = lineParts[0];
				noOfCustomers = Double.parseDouble(lineParts[1]);
				if (position == currentLine) {
					currentLine++;
					break;
				}
				currentLine++;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
//		outputServiceTime = new Exp(noOfCustomers/600);
		double newDemand = noOfCustomers;
		return newDemand;
	}
	
	public double getAntennaRate (int position) {
		
		double noOfCustomers = 0;
		
		try {
			
			lineNumberReader.setLineNumber(currentLine);
			
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
//				String antennaID = lineParts[0];
				String timestamp = lineParts[0];
				noOfCustomers = Double.parseDouble(lineParts[1]);
				if (position == currentLine) {
					currentLine++;
					break;
				}
				currentLine++;
			}
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		double newDemand = noOfCustomers;
		return 600/newDemand;
	}

}
