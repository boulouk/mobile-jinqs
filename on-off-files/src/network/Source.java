package network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import tools.*;

/**
 * A Source node injects customers into a queueing network. The inter-arrival
 * time (a {@link DistributionSampler}) must be specified. Arrivals may
 * optionally be batched, with the batch size specified by a second
 * {@link DistributionSampler}.
 */
public class Source extends Node {
	protected DistributionSampler delay;
	protected DistributionSampler batchsize;
	protected FileDataSet dataFile;
	protected QueueingNode queueingNode;
	protected QueueingNodeDataSet queueingNodeDataSet;
	protected String arrivalsType;
	
	protected DistributionSampler distrib = null;

	/**
	 * @param name
	 *            The name of the source node
	 * @param d
	 *            The {@link DistributionSampler} used to generate the
	 *            inter-arrival times
	 */
	public Source(String name, DistributionSampler d) {
		super(name);
		delay = d;
		batchsize = new Deterministic(1);
		Sim.schedule(new Arrival(Sim.now() + delay.next()));
	}

	/**
	 * @param name
	 *            The name of the source node
	 * @param d
	 *            The {@link DistributionSampler} used to generate the
	 *            inter-arrival times
	 * @param b
	 *            The {@link DistributionSampler} used to generate the batch
	 *            sizes
	 */
	public Source(String name, DistributionSampler d, DistributionSampler b) {
		super(name);
		delay = d;
		batchsize = b;
		Sim.schedule(new Arrival(Sim.now() + delay.next()));
	}

	/**
	 * @param name
	 *            The name of the source node
	 * @param df
	 *            The {@link DistributionSampler} used to generate the
	 *            inter-arrival times from a file
	 * @param fileTrue 
	 * 			  A boolean parameter in order to separate the constructors
	 */
	public Source(String name, FileDataSet df, String arrType) {
		super(name);

		dataFile = df;
		arrivalsType = arrType;
		batchsize = new Deterministic(1);
		Sim.schedule(new ArrivalFile(Sim.now()));
	}

	/**
	 * Builds a new customer. This can be overridden to support specialised
	 * {@link Customer} subclasses.
	 * 
	 * @return a customer
	 * 
	 */
	protected Customer buildCustomer() {
		return new Customer();
	}

	/**
	 * Injects customers into the network using forward. The initial location of
	 * the customer is the source node.
	 */
	void injectCustomer() {
		Customer c = buildCustomer();
		c.setLocation(this);	
		forward(c);
	}

	void injectCustomers() {
		int nArrivals = (int) batchsize.next();
		for (int i = 0; i < nArrivals; i++) {
			injectCustomer();
		}
	}
	
	/**
	 * Injects exponential or deterministic customers step by step based on the file's input. 
	 */
	void injectCustomersFile() {
		double time = Sim.now();
		//This is the next time interval for the simulator
		
		if (time == 1209600 || time == 2419200 || time == 3628800 || time == 4837800 || time == 6048000 || time == 7257600 || time == 8467200 || time == 9676800 || time == 1.08864E7 || time == 1.2096E7 
				|| time == 1.33056E7 || time == 1.45152E7 || time == 1.57248E7 || time == 1.69344E7 || time == 1.8144E7 || time == 1.93536E7 || time == 2.05632E7 || time == 2.17728E7 || time == 2.29824E7
				|| time == 2.4192E7 || time == 2.54016E7 || time == 2.66112E7 || time == 2.78208E7 || time == 2.90304E7 || time == 3.024E7) {
			Queue queue = queueingNode.getQueue();
//			Queue queueDataSet = queueingNodeDataSet.getQueue();
			
		    try {
		
		    	String content = String.valueOf("L: " + (Network.completions/time) + " --- Response Time: " + Network.responseTime.mean() + " --- Avg Queue Input: " + queue.meanQueueLength() + " --- CurrentLine: " + dataFile.getCurrentLine());
//				String content = String.valueOf("L: " + (Network.completions/time) + " --- Response Time: " + Network.responseTime.mean() + " --- Avg Queue Input: " + queue.meanQueueLength() + " --- Avg Queue Subscriber: " + queueDataSet.meanQueueLength());
//				double result = ((double) Network.completionsDelivered / (double) Network.completions) * 100;
//				System.out.println(result);
//				String content1 = String.valueOf("Success Rate: " + (result) + " --- Response Time Delivered: " + Network.responseTimeDelivered.mean());

				File file = new File("samples.txt");
		
				if (!file.exists()) {
					file.createNewFile();
				}
		
				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.write("\n");
//				bw.write(content1);
//				bw.write("\n");
				bw.write("-------");
				bw.write("\n");
				bw.close();
		
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
		}
		
	    double dataFileNext = dataFile.next();
		
		if(arrivalsType.equals("Exp")){
			//For Exponential
			double rate = dataFileNext/dataFile.getStep();
//		    try {
//				File file = new File("samples_for_rates_an24.txt");
//		
//				if (!file.exists()) {
//					file.createNewFile();
//				}
//		
//				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//				BufferedWriter bw = new BufferedWriter(fw);
//				bw.write(String.valueOf(rate));
//				bw.write("\n");
//				bw.close();
//		
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
			
			
			distrib = new Exp(rate);
		} else if (arrivalsType.equals("Det")) {
			//For Deterministic
			double num = dataFile.getStep()/(dataFileNext + 1);
			distrib = new Deterministic(num);
		} 

		//The next interarrival
		double next = distrib.next();
		
		if(arrivalsType.equals("Exp")){
		//For Exponential
			while (next < dataFile.getStep()) {
	//			if (next < dataFile.getStep()) {
					Sim.schedule(new ArrivalFromLine(Sim.now() + next));
	//			}
				next = next + distrib.next();
			}
		} else if (arrivalsType.equals("Det")) {
		
			//For Deterministic
			int nArrivals = (int) dataFileNext;
			for (int i = 0; i < nArrivals; i++) {
//				if (next < dataFile.getStep()) {
					Sim.schedule(new ArrivalFromLine(Sim.now() + next));
//				}
				next = next + distrib.next();
			}
		}
			
	}

	/**
	 * One arrival prompts the next...
	 */
	class Arrival extends Event {
		public Arrival(double t) {
			super(t);
		}

		public void invoke() {
			
			injectCustomers();
			Sim.schedule(new Arrival(Sim.now() + delay.next()));
		}
	}

	/**
	 * Is providing the steps based on the file's input
	 */
	class ArrivalFile extends Event {
		public ArrivalFile(double t) {
			super(t);
		}

		public void invoke() {
			distrib = null;
			
			injectCustomersFile();
			Sim.schedule(new ArrivalFile(Sim.now() + dataFile.getStep()));
		}
	}
	
	/**
	 * Arrivals are scheduled based on the line numbers of the file
	 */
	class ArrivalFromLine extends Event {
		public ArrivalFromLine(double t) {
			super(t);
		}

		public void invoke() {
			injectCustomers();
		}
	}

}
