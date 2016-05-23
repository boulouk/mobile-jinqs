package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import extensions.LifetimeDataSetQN;
import extensions.SinkLifetime;

import network.*;
import tools.*;

class NetworkOnOffDataSetSim extends Sim {

	public static double noOfCustIn = 0;
	public static double noOfCustOut = 0;
	public static double serverUtilization = 0;
	public static double queueTime = 0;
	public static double duration = 0;
	public static Exp inputServiceTime;
	public static Exp outputServiceTime;
	public static double expCompletions = 0;
	
	public NetworkOnOffDataSetSim(String inAntenna, String outAntenna, double durationLocal) {
		Network.initialise();
		duration = durationLocal;
		
		inputServiceTime = new Exp(1);
		Delay inputServeTime = new Delay(inputServiceTime);

		outputServiceTime = new Exp(20);
		Delay outputServeTime = new Delay(outputServiceTime);

		FileDataSet inputAntenna = new FileDataSet(inAntenna);
		Source publishers = new Source("Source", inputAntenna, "Exp");

		QueueingNode inputQueueingNode = new QueueingNode("inputQueueingNode", inputServeTime, 1);

		QueueingNodeDataSet outputQueueingNode = new QueueingNodeDataSet("outputQueueingNode", outputServeTime, 1, "Exp", 0.017, outAntenna, 499);

		SinkLifetime subscribers = new SinkLifetime("subscribers");

		publishers.setLink(new Link(inputQueueingNode));
		inputQueueingNode.setLink(new Link(outputQueueingNode));

		outputQueueingNode.setLink(new Link(subscribers));

		simulate();

		// Network.logResult("Mean Time Input Queue:",
		// inputQueueingNode.meanTimeInQueue());
		// Network.logResult("Mean Time Output Queue:",
		// outputQueueingNode.meanTimeInQueue());

		noOfCustIn = inputQueueingNode.meanNoOfQueuedCustomers();
		noOfCustOut = outputQueueingNode.meanNoOfQueuedCustomers();
		Network.logResults();
	}

	public boolean stop() {
		// return Network.completions == 259;
		return now() > duration;
	}

	public static void main(String args[]) {
		
		String inputAntenna = "SET2_AntennaFull_9.txt";
		String outputAntenna = "SET2_AntennaFull_161.txt";
		BeforeExec beforeExec = new BeforeExec();

		double durationInput = beforeExec.getDuration(inputAntenna);
		double durationOutput = beforeExec.getDuration(outputAntenna);
		expCompletions = beforeExec.getCompletions(inputAntenna);
		
		if(durationInput <= durationOutput) {
			System.out.println("Expected completions: " + expCompletions);
			duration = durationInput;
			new NetworkOnOffDataSetSim(inputAntenna, outputAntenna, durationInput);
			Network.displayResults(0.01);
		} else {
			System.err.println("Duration of: " + inputAntenna + " is longer then the output data");
			System.out.println("Expected completions: " + expCompletions);
			duration = durationOutput;
			new NetworkOnOffDataSetSim(inputAntenna, outputAntenna, durationOutput);
			Network.displayResults(0.01);
		}
		
		
		 try {
				String content2 = "InputAntenna: " + inputAntenna + " OutputAntenna: " + outputAntenna; 
				String content = String.valueOf("L: " + (Network.completions/duration) + " --- Response Time: " + Network.responseTime.mean() + " --- Avg Queue Input: " + noOfCustIn + " --- Avg Queue Subscriber: " + noOfCustOut);
				double result = ((double) Network.completionsDelivered / Network.completions) * 100;
//				System.out.println(result);
				String content1 = String.valueOf("Success Rate: " + result + "--- Response Time: " + Network.responseTimeDelivered.mean());

				File file = new File("samples.txt");
		
				if (!file.exists()) {
					file.createNewFile();
				}
		
				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content2);
				bw.write("\n");
				bw.write(content);
				bw.write("\n");
				bw.write(content1);
				bw.write("\n");
				bw.write("-------");
				bw.write("\n");
				bw.close();
		
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		


	}
}
