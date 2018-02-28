package pubsubpriorities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import extensions.ClassSource;
import extensions.PrioSource;
import extensions.SinkMulticlass;
import extensions.SinkPriorities;
import network.*;
import tools.*;

class PubsubV2Sim extends Sim {

	public static double duration = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}

	public PubsubV2Sim(double d) {
		duration = d;
		Network.initialise();
		
		int numOfTopics = 120;
		int numOfPrio = 5;
		
		int dev  = numOfTopics / numOfPrio;
//		double percentage = (((double) numOfTopics / (double) numOfPrio)) / (double) numOfTopics;
//		Geometric prob = new Geometric(percentage);
		
//		double lambda1 = 1;
//		double lambda2 = 2;
//		double lambda3 = 3;
//		double lambda4 = 4;
		
//		initiate lambdas and topic-rates
		Map<Integer,Double> lambdaMap = new HashMap<Integer,Double>();
		Map<Integer,Double> topicRateMap = new HashMap<Integer,Double>();
		for(int i=1; i<=numOfTopics; i++) {
			lambdaMap.put(i, (double) 0.1);
			topicRateMap.put(i, 16.0);
		}
		
//		initiate sources and and priorities
		Map<Integer,Double> lambdaPriorityMap = new HashMap<Integer,Double>();
		Map<Integer,Node> sourceMap = new HashMap<Integer,Node>();
		int tempPriority = 0;
		int tempDev = dev;
		double tempLambdaPrio = 0;
		for(int i=1; i<=numOfTopics; i++) {
			Node source = null;
			
			source = new PrioSource("Topic Source "+i, new Exp(lambdaMap.get(i)), i, tempPriority);
			
			if (lambdaPriorityMap.get(tempPriority) == null) {
				lambdaPriorityMap.put(tempPriority, lambdaMap.get(i));
			} else {
				tempLambdaPrio = lambdaPriorityMap.get(tempPriority);
				lambdaPriorityMap.put(tempPriority, (tempLambdaPrio+lambdaMap.get(i)));
			}
		
			System.out.println("Topic Source "+i+" Priority: "+tempPriority);
			sourceMap.put(i, source);
			
			if(i>=tempDev && tempDev+dev <= numOfTopics) {
				tempPriority++;
				tempDev = tempDev+dev;
			}
			
		}
		
		double BRInPrRate = 64;
		double BROutTrRate = 64;
		double SDNInPrRate = 16;
		
		Exp BRInPrDistrib = new Exp(BRInPrRate);
		Delay BRInPrDelay = new Delay(BRInPrDistrib);
		QueueingNode broker_in = new QueueingNode("MM1 BROKER IN", BRInPrDelay, 1);
		
		Exp BROutTrDistrib = new Exp(BROutTrRate);
		Delay BROutTrDelay = new Delay(BROutTrDistrib);
		QueueingNode broker_out = new QueueingNode("MM1 BROKER OUT", BROutTrDelay, 1);
		
		PriorityQueue prioq = new PriorityQueue(numOfPrio);
		Exp SDNInPrDistrib = new Exp(SDNInPrRate);
		Delay SDNInPrDelay = new Delay(SDNInPrDistrib);
		QueueingNode sdn_in = new QueueingNode("PRIORITY SDN IN", SDNInPrDelay, 1, prioq);
		
//		initiate multiclass queue
		int[] arrayOfTopics = new int[numOfTopics];
		
		DistributionSampler[] arrayOfTopicRateDistrib = new DistributionSampler[numOfTopics];
		for(int i=0; i<arrayOfTopics.length; i++) {
			arrayOfTopics[i] = i+1;
			arrayOfTopicRateDistrib[i] = new Exp(topicRateMap.get(i+1));
		}
					
		ClassDependentDelay SDNOutTrOveralDelay = new ClassDependentDelay(arrayOfTopics, arrayOfTopicRateDistrib);
		QueueingNode sdn_out = new QueueingNode("MULTICLASS SDN OUT", SDNOutTrOveralDelay, 1);
		
		SinkMulticlass sinkMulticlass = new SinkMulticlass("Sink Overal");
		
//		set link from sources to 1st queue		
		for(int i=1; i<=numOfTopics; i++) {
			sourceMap.get(i).setLink(new Link(broker_in));
		}
		
		broker_in.setLink(new Link(broker_out));
		broker_out.setLink(new Link(sdn_in));
		sdn_in.setLink(new Link(sdn_out));
		sdn_out.setLink(new Link(sinkMulticlass));

		simulate();

		double R_broker_in = r_mm1(lambdaMap, BRInPrRate);
		double R_broker_out = r_mm1(lambdaMap, BROutTrRate);
		
		double ro_broker_in = ro_mm1(lambdaMap, BRInPrRate);
		double ro_broker_out = ro_mm1(lambdaMap, BROutTrRate);
		System.out.println("ro broker in: " + ro_broker_in);
		System.out.println("ro broker out: " + ro_broker_out);		
		
		double ro_sdn_in = ro_prio(lambdaMap, SDNInPrRate);
		System.out.println("ro sdn in: " + ro_sdn_in);	
		
		double ro_sdn_out = ro_multiclass(lambdaMap, topicRateMap);
		System.out.println("ro sdn out: " + ro_sdn_out);	
		
		
		
//		double ro0 = (lambda1+lambda2+lambda3) / SDNInPrRate;
//		double ro1 = lambda4 / SDNInPrRate;
		
//		double R_SDN_IN_PRIO_0 = ((1 + ro1) / SDNInPrRate) / (1 - ro0);
//		double R_SND_IN_PRIO_1 = ((1 - (ro0 * (1 - ro0 - ro1))) / SDNInPrRate) / ((1 - ro0)*(1 - ro0 - ro1));
		
//		estimate R of analytical model
//		TOCO add priorities
		double R_model_of_topic = 0;
		for(int i=1; i<=numOfTopics; i++) {
			R_model_of_topic = R_broker_in + R_broker_out + r_multiclass(i, lambdaMap, topicRateMap);
//			System.out.println("SIM: Response Time Topic TEST "+ i +": "+ R_model_of_topic);
		}
		
//		System.out.println("SIM: Response Time Topic 1: "+ (R_broker_in + R_broker_out + R_SDN_IN_PRIO_0 + r_multiclass(1, lambdaMap, topicRateMap)));
//		System.out.println("SIM: Response Time Topic 1: "+ (R_broker_in + R_broker_out + R_SDN_IN_PRIO_0 + r_multiclass(2, lambdaMap, topicRateMap)));
//		System.out.println("SIM: Response Time Topic 1: "+ (R_broker_in + R_broker_out + R_SDN_IN_PRIO_0 + r_multiclass(3, lambdaMap, topicRateMap)));
//		System.out.println("SIM: Response Time Topic 1: "+ (R_broker_in + R_broker_out + R_SND_IN_PRIO_1 + r_multiclass(4, lambdaMap, topicRateMap)));
		
		Iterator entries = Network.responseTimeClassMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Integer key = (Integer) entry.getKey();
			CustomerMeasure value = (CustomerMeasure) entry.getValue();
			System.out.println("SIM: Response Time Topic " + key + ", Value = " + value.mean());
		}

//		Network.logResults();
	}


	public static void main(String args[]) {

		new PubsubV2Sim(100000);
//		Network.displayResults(0.01);

	}
	
	public double r_mm1 (Map lambdamap, double rate) {
		double r = 0;
		double overallambda = 0;
		
		Iterator entries = lambdamap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Double lambdavalue = (Double) entry.getValue();	
			overallambda = overallambda + lambdavalue;	
		}
		
		r = (1/rate) / (1 - (overallambda * (1/rate)));
	
		return r;
	}
	
	public double r_multiclass (int topicID, Map lambdamap, Map<Integer,Double> topicratemap) {
		double r = 0;
		double r_numerator = 0;
		double r_denominator_part = 0;
		double r_denominator = 0;
		
		r_numerator = 1/topicratemap.get(topicID);
				
		Iterator entries = lambdamap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Integer key = (Integer) entry.getKey();
			Double lambdavalue = (Double) entry.getValue();	
			
			r_denominator_part = r_denominator_part + (lambdavalue * (1/topicratemap.get(key)));
		}
		
		r_denominator = 1 - r_denominator_part;
		r = r_numerator / r_denominator;
	
		return r;
	}
	
	public double ro_mm1 (Map lambdamap, double rate) {
		double ro = 0;
		double overallambda = 0;
		
		Iterator entries = lambdamap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Double lambdavalue = (Double) entry.getValue();	
			overallambda = overallambda + lambdavalue;	
		}
		
		ro = overallambda / rate;
	
		return ro;
	}
	
	public double ro_multiclass (Map lambdamap, Map<Integer,Double> topicratemap) {
		double ro = 0;
						
		Iterator entries = lambdamap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Integer key = (Integer) entry.getKey();
			Double lambdavalue = (Double) entry.getValue();	
			
			ro = ro + lambdavalue/topicratemap.get(key);
		}
	
		return ro;
	}
	
	public double ro_prio (Map lambdamap, double priorate) {
		double ro = 0;
						
		Iterator entries = lambdamap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Integer key = (Integer) entry.getKey();
			Double lambdavalue = (Double) entry.getValue();	
			
			ro = ro + lambdavalue/priorate;
		}
	
		return ro;
	}

}
