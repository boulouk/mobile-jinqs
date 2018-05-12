package examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import extensions.PrioSource;
import extensions.SinkMulticlass;
import extensions.SinkMulticlassPrio;
import extensions.SinkPriorities;
import network.*;
import pubsubpriorities.AnalyticalModels;
import pubsubpriorities.JsonRead;
import tools.*;

class MulticlassPrioSim extends Sim {

	public DistributionSampler insertProb;

	public MulticlassPrioSim() {
		Network.initialise();

		String inParams = "multiclass_prio/temp1.json";

		int numOfTopics = JsonRead.getJSONArray(inParams, "lambdas").size();
		int numOfPrios = JsonRead.getJSONArray(inParams, "prio_probs").size();
	
		Map<Integer, Double> topicRateMap = new HashMap<Integer, Double>();
		Map<Integer, Double> topicLambdaMap = new HashMap<Integer, Double>();

		Iterator<Double> getLambdasIterator = JsonRead.getJSONArray(inParams, "lambdas").iterator();
		Iterator<Double> getMusIterator = JsonRead.getJSONArray(inParams, "mus").iterator();

		for (int topic = 0; topic < numOfTopics; topic++) {
			if (getLambdasIterator.hasNext() && getMusIterator.hasNext()) {
				double lamb = getLambdasIterator.next();
				topicLambdaMap.put(topic, lamb);
				topicRateMap.put(topic, getMusIterator.next());
			}
		}

		Map<Integer, Integer> prioritiesMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> prioSizeMap = new HashMap<Integer, Integer>();
		Map<Integer, Double> lambdaPriorityMap = new HashMap<Integer, Double>();

		Map<Integer, Node> sourceMap = new HashMap<Integer, Node>();

		Iterator<Long> getPrioIterator = JsonRead.getJSONArray(inParams, "priorities").iterator();

		int intprio = 0;
		int maxPrio = 0;

		for (int topic = 0; topic < numOfTopics; topic++) {
			Node source = null;

			if (getPrioIterator.hasNext()) {
				Long lprio = (Long) getPrioIterator.next();
				intprio = lprio.intValue();
				if (intprio > maxPrio)
					maxPrio = intprio;

				if (topicLambdaMap.get(topic) != 0)
					source = new PrioSource("Topic Source " + topic, new Exp(topicLambdaMap.get(topic)), topic,
							intprio);

				prioritiesMap.put(topic, intprio);
				
				if(prioSizeMap.get(intprio) == null) {
					prioSizeMap.put(intprio, 1);
				} else {
					prioSizeMap.put(intprio, prioSizeMap.get(intprio)+1);
				}			

				// initialization of lambda priority rates. If there is not subscription for a
				// topic, I do not add its rate to the lambda prio map
				if (lambdaPriorityMap.get(intprio) == null)
					lambdaPriorityMap.put(intprio, topicLambdaMap.get(topic));
				else
					lambdaPriorityMap.put(intprio, lambdaPriorityMap.get(intprio) + topicLambdaMap.get(topic));
			}

			System.out.println("Topic Source " + topic + " Priority: " + intprio);
			sourceMap.put(topic, source);

		}
		
		Map<Integer, Double> prioRateMap = new HashMap<Integer, Double>();

		for (int topic = 0; topic < numOfTopics; topic++) {
			if (prioRateMap.get(prioritiesMap.get(topic)) == null){
				prioRateMap.put(prioritiesMap.get(topic), (topicLambdaMap.get(topic)/topicRateMap.get(topic)));
			} else {
				double value = prioRateMap.get(prioritiesMap.get(topic));
				prioRateMap.put(prioritiesMap.get(topic), value + (topicLambdaMap.get(topic)/topicRateMap.get(topic)));
			}		
		}
		
		// UPDATE ratePriorityMap
		for (int prio = 0; prio < lambdaPriorityMap.size(); prio++) {
						
			prioRateMap.put(prio, Math.pow((prioRateMap.get(prio)/lambdaPriorityMap.get(prio)), -1));
			System.out.println("Lambda of prio "+prio+": "+lambdaPriorityMap.get(prio));
			System.out.println("Mu of prio "+prio+": "+prioRateMap.get(prio));
//			System.out.println("Size of prio "+prio+": "+prioSizeMap.get(prio));
			
		}
		
		Map<Integer, Double> prioDeltaMap = new HashMap<Integer, Double>();
		Map<Integer, Double> prioLqMap = new HashMap<Integer, Double>();
		
		// Estimate Lq and Delta for each prio
		for (int prio = 0; prio < lambdaPriorityMap.size(); prio++) {			
			System.out.println("Delta_q estimated "+prio+" = " + AnalyticalModels.r_q_prio(lambdaPriorityMap, prio, prioRateMap));

			prioLqMap.put(prio, AnalyticalModels.l_q_prio(lambdaPriorityMap, prio, prioRateMap));
			System.out.println("Lq estimated "+prio+" = " + prioLqMap.get(prio));
			
			prioDeltaMap.put(prio, AnalyticalModels.r_prio(lambdaPriorityMap, prio, prioRateMap));
			System.out.println("AN: Delta of prio "+prio+" = "+prioDeltaMap.get(prio));
			
			
		}
		
		StringBuilder an_delta = new StringBuilder();
		StringBuilder topics = new StringBuilder();
		an_delta.append("[");
		topics.append("[");
				
		double lq = 0;
		double ls = 0;
		double deltas = 0;
		for (int topic = 0; topic < numOfTopics; topic++) {		
			lq = (topicLambdaMap.get(topic)/lambdaPriorityMap.get(prioritiesMap.get(topic)))*prioLqMap.get(prioritiesMap.get(topic));
			ls = lq+(topicLambdaMap.get(topic)/topicRateMap.get(topic));
			deltas = ls / topicLambdaMap.get(topic);
			
			System.out.println("AN: Delta for topic "+topic +" = "+deltas);
			Double y = new Double (deltas);
			if(!y.isNaN()) {
				an_delta.append(deltas+",");
				topics.append(topic+",");
			}
			
		}
		
		an_delta.append("]");
		topics.append("]");
		
		System.out.println(an_delta);
		System.out.println(topics);
		
		double ro4thMULTIqueue = AnalyticalModels.ro_multiclass(topicLambdaMap, topicRateMap);
		System.out.println("RO on 4th (NON-preemptive Priority Multi-class) queue: " + ro4thMULTIqueue);
		
		if (ro4thMULTIqueue >= 1) {
			System.err.println("RO > 1 on 4th (NON-preemptive Priority Multi-class) Queue");
			System.exit(1);
		} else
			System.out.println("RO is OK");
		
		
		// initialization of multiclass queue
		int[] arrayOfTopics = new int[numOfTopics];
		DistributionSampler[] arrayOfTopicRateDistrib = new DistributionSampler[numOfTopics];
		
		for (int topic = 0; topic < arrayOfTopics.length; topic++) {
			arrayOfTopics[topic] = topic;
			arrayOfTopicRateDistrib[topic] = new Exp(topicRateMap.get(topic));
		}

		PriorityQueue prioq = new PriorityQueue(numOfPrios);
		ClassDependentDelay overalDelay = new ClassDependentDelay(arrayOfTopics, arrayOfTopicRateDistrib);
		QueueingNode multi_prio = new QueueingNode("MULTICLASS PRIO OUT", overalDelay, 1, prioq);
		
		SinkMulticlassPrio sinkMulticlass = new SinkMulticlassPrio("Sink Multiclass");

		
		for (int topic = 0; topic < numOfTopics; topic++) {
			if (sourceMap.get(topic) != null)
				sourceMap.get(topic).setLink(new Link(multi_prio));
		}
		multi_prio.setLink(new Link(sinkMulticlass));

		simulate();

		Network.logResult("Mean Queue Size", multi_prio.meanNoOfQueuedCustomers());

//		Iterator entriesTopic = Network.responseTimeClassMap.entrySet().iterator();
//		while (entriesTopic.hasNext()) {
//			Map.Entry entry = (Map.Entry) entriesTopic.next();
//			Integer key = (Integer) entry.getKey();
//			CustomerMeasure value = (CustomerMeasure) entry.getValue();
//			System.out.println("SIM: Delta Topic " + key + ", Value = " + value.mean());
//		}
		
		StringBuilder sim_delta = new StringBuilder();

		sim_delta.append("[");
		
		Map<Integer,CustomerMeasure> treeMap = new TreeMap<>(Network.responseTimeClassMap);
		Iterator entriesTopic = treeMap.entrySet().iterator();
		while (entriesTopic.hasNext()) {
			Map.Entry entry = (Map.Entry) entriesTopic.next();
			Integer key = (Integer) entry.getKey();
			CustomerMeasure value = (CustomerMeasure) entry.getValue();
			System.out.println("SIM: Delta Topic " + key + ", Value = " + value.mean());
			sim_delta.append(value.mean()+",");
		}
		
		sim_delta.append("]");
		
		System.out.println(sim_delta);
		
		Iterator entriesPrio = Network.responseTimePrioMap.entrySet().iterator();
		while (entriesPrio.hasNext()) {
			Map.Entry entry = (Map.Entry) entriesPrio.next();
			Integer key = (Integer) entry.getKey();
			CustomerMeasure value = (CustomerMeasure) entry.getValue();
			System.out.println("SIM: Delta Prio " + key + ", Value = " + value.mean());
		}

		// Network.logResults();
	}

	public boolean stop() {
		return Network.completions == 4000000;
	}

	public static void main(String args[]) {

		new MulticlassPrioSim();
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
