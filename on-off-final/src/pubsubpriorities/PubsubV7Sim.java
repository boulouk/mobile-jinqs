package pubsubpriorities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import extensions.ClassSource;
import extensions.PrioSource;
import extensions.QNPrioProbs;
import extensions.SinkErrorRate;
import extensions.SinkLackSubs;
import extensions.SinkMulticlass;
import extensions.SinkOvrlNet;
import extensions.SinkPriorities;
import extensions.SubscriptionsBranch;
import network.*;
import tools.*;

class PubsubV7Sim extends Sim {

	public static double duration = 0;
	public static int completions = 0;
	public static Map<Integer, Double> respTimeClassModelMap;
	
//	TODO sort hashmaps

	// Example termination function
	public boolean stop() {
		return now() > duration;
//		return Network.completions == completions;
	}
	
	public PubsubV7Sim(double d, int c, String inParams) {
		duration = d;
		completions = c;
		Network.initialise();
		respTimeClassModelMap = new HashMap<Integer, Double>();

		double BRInPrRate = 64000;
		double BROutTrRate = 64000;
		double SDNInPrRate = 64000;
		
		int sdnoutBS = 2000;

		// get number of topics from JSON input file
		int numOfTopics = JsonRead.getJSONArray(inParams, "lambdas").size();
		int numOfPrios = JsonRead.getJSONArray(inParams, "prio_probs").size();
		double errorRate = JsonRead.getParam(inParams, "error_rate");

		// initialization of list of subscriptions
		Iterator<Long> subsIterator = JsonRead.getJSONArray(inParams, "subscriptions").iterator();
		ArrayList<Integer> listOfSubsptions = new ArrayList<Integer>();
		
		while (subsIterator.hasNext()) {
			Long lsub = (Long) subsIterator.next();
			int intsub = lsub.intValue();
			listOfSubsptions.add(intsub);
		}
		

		// initialization of list of probabilities for each priority
		Iterator<Double> prioProbsIterator = JsonRead.getJSONArray(inParams, "prio_probs").iterator();
		ArrayList<Double> prioProbs = new ArrayList<Double>();
		while (prioProbsIterator.hasNext()) {
			double prob = (double) prioProbsIterator.next();
			prioProbs.add(prob);
		}

		// initialization of lambdas, lambdas with subscriptions and topic-rates from
		// JSON input file
		Map<Integer, Double> lambdaMap = new HashMap<Integer, Double>();
		Map<Integer, Double> lambdaSubscriptionsMap = new HashMap<Integer, Double>();
		Map<Integer, Double> topicRateMap = new HashMap<Integer, Double>();

		Iterator<Double> getLambdasIterator = JsonRead.getJSONArray(inParams, "lambdas").iterator();
		Iterator<Double> getMusIterator = JsonRead.getJSONArray(inParams, "mus").iterator();

		for (int topic = 0; topic < numOfTopics; topic++) {
			if (getLambdasIterator.hasNext() && getMusIterator.hasNext()) {
				double lamb = getLambdasIterator.next();
				lambdaMap.put(topic, lamb);
				topicRateMap.put(topic, getMusIterator.next());

				
				if (listOfSubsptions.contains(topic)) {
					lambdaSubscriptionsMap.put(topic, lamb);
				} else
					lambdaSubscriptionsMap.put(topic, (double) 0);
			}
		}

		// initialization of sources and and priorities
		Map<Integer, Double> lambdaPriorityMap = new HashMap<Integer, Double>();
		Map<Integer, Integer> prioritiesMap = new HashMap<Integer, Integer>();
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

				if (lambdaMap.get(topic) != 0)
					source = new PrioSource("Topic Source " + topic, new Exp(lambdaMap.get(topic)), topic, intprio);

				prioritiesMap.put(topic, intprio);

				// initialization of lambda priority rates. If there is not subscription for a topic, I do not add its rate to the lambda prio map
				if (lambdaPriorityMap.get(intprio) == null)
					lambdaPriorityMap.put(intprio, lambdaSubscriptionsMap.get(topic));
				else
					lambdaPriorityMap.put(intprio, lambdaPriorityMap.get(intprio) + lambdaSubscriptionsMap.get(topic));
			}

			System.out.println("Topic Source " + topic + " Priority: " + intprio);
			sourceMap.put(topic, source);

		}
		
		// check if there are probabilities for each priority class
		if((maxPrio+1) > prioProbs.size()) {
			System.err.println("There are more priorities then probs for each priority class!");
			System.exit(1);
		}
		
		
		double ro1stMM1queue = AnalyticalModels.ro_mm1(lambdaMap, BRInPrRate);
		System.out.println("RO on 1st (MM1) queue: " + ro1stMM1queue);
		
		if (ro1stMM1queue >= 1) {
			System.err.println("RO > 1 on 1st (MM1) Queue");
			System.exit(1);
		}
		
		double ro2ndMM1queue = AnalyticalModels.ro_mm1(lambdaSubscriptionsMap, BRInPrRate);
		System.out.println("RO on 2nd (MM1) queue: " + ro2ndMM1queue);
		
		if (ro2ndMM1queue >= 1) {
			System.err.println("RO > 1 on 2nd (MM1) Queue");
			System.exit(1);
		}
		
		double ro3rdPRIOqueue = AnalyticalModels.ro_mm1(lambdaSubscriptionsMap, SDNInPrRate);
		System.out.println("RO on 3rd (MM1) queue: " + ro3rdPRIOqueue);
		
		if (ro3rdPRIOqueue >= 1) {
			System.err.println("RO > 1 on 3rd (MM1) Queue");
			System.exit(1);
		}
		
		// UPDATE lambdaPriorityMap based on the priority probabilities
		for (int prio = 0; prio < lambdaPriorityMap.size(); prio++) {
			if (lambdaPriorityMap.get(prio) == null)
				lambdaPriorityMap.put(prio, 0.0);
			else
				lambdaPriorityMap.put(prio, lambdaPriorityMap.get(prio) * prioProbs.get(prio));
		}
		
		// UPDATE lambda subscriptions based on the priority probabilities
		for (int topic = 0; topic < numOfTopics; topic++) {
			
			lambdaSubscriptionsMap.put(topic, lambdaSubscriptionsMap.get(topic) * prioProbs.get(prioritiesMap.get(topic)));

			// initialization of resp times, drops, etc of the simulator
//			TODO: do this initialization in another point
			Network.dropsClassMap.put(topic, 0);
			Network.dropPrioClassMap.put(topic, 0);
			Network.dropsBufferClassMap.put(topic, 0);
			Network.completionsClassMap.put(topic, 0);
			CustomerMeasure responseTime = new CustomerMeasure();
			Network.responseTimeClassMap.put(topic, responseTime);
			Network.responseTimeClassMap.get(topic).add(0);
			
//			System.out.println("Final Lambda for each topic: " + topic + ": " + lambdaSubscriptionsMap.get(topic));

		}
		
		double ro4thMULTIqueue = AnalyticalModels.ro_multiclass(lambdaSubscriptionsMap, topicRateMap);
		System.out.println("RO on 4th (NON-preemptive Priority Multi-class) queue: " + ro4thMULTIqueue);
		
		if (ro4thMULTIqueue >= 1) {
			System.err.println("RO > 1 on 4th (NON-preemptive Priority Multi-class) Queue");
//			System.exit(1);
		} else
			System.out.println("RO at every queue is OK");

		Exp BRInPrDistrib = new Exp(BRInPrRate);
		Delay BRInPrDelay = new Delay(BRInPrDistrib);
		QueueingNode broker_in = new QueueingNode("MM1 BROKER IN", BRInPrDelay, 1);

		Exp BROutTrDistrib = new Exp(BROutTrRate);
		Delay BROutTrDelay = new Delay(BROutTrDistrib);
		QueueingNode broker_out = new QueueingNode("MM1 BROKER OUT", BROutTrDelay, 1);

		Exp SDNInPrDistrib = new Exp(SDNInPrRate);
		Delay SDNInPrDelay = new Delay(SDNInPrDistrib);

		//Below is an MM1 queue since we do not introduce any priority queue in the params of QNPrioProbs
		QueueingNode sdn_in = new QueueingNode("MM1 SDN IN", SDNInPrDelay, 1);

		// initialization of multiclass queue
		int[] arrayOfTopics = new int[numOfTopics];

		DistributionSampler[] arrayOfTopicRateDistrib = new DistributionSampler[numOfTopics];
		for (int topic = 0; topic < arrayOfTopics.length; topic++) {
			arrayOfTopics[topic] = topic;
			arrayOfTopicRateDistrib[topic] = new Exp(topicRateMap.get(topic));
		}

		
		PriorityQueue prioq = new PriorityQueue(numOfPrios, sdnoutBS);
		ClassDependentDelay SDNOutTrOveralDelay = new ClassDependentDelay(arrayOfTopics, arrayOfTopicRateDistrib);
//		QueueingNode sdn_out = new QueueingNode("MULTICLASS SDN OUT", SDNOutTrOveralDelay, 1, prioq);
		QNPrioProbs sdn_out = new QNPrioProbs("MULTICLASS SDN OUT", SDNOutTrOveralDelay, 1, prioq, prioProbs);

		SinkLackSubs sinkSubscriptions = new SinkLackSubs("Sink Subscriptios Lack");
		SinkErrorRate sinkError = new SinkErrorRate("Sink Error Rate");
		SinkMulticlass sinkMulticlass = new SinkMulticlass("Sink Overal");

		// initialization of branch for the dropping of events due to lack of subscriptions and packets due to error rate
		SubscriptionsBranch br_subs = new SubscriptionsBranch(listOfSubsptions,
				new Node[] { sinkSubscriptions, broker_out });
		ProbabilisticBranch pb_error = new ProbabilisticBranch(new double[] { errorRate, 1 - errorRate },
				new Node[] { sinkError, sinkMulticlass });

		// set link from sources to 1st queue
		for (int topic = 0; topic < numOfTopics; topic++) {
			if (sourceMap.get(topic) != null)
				sourceMap.get(topic).setLink(new Link(broker_in));
		}

		// broker_in.setLink(new Link(broker_out));
		broker_in.setLink(br_subs);
		broker_out.setLink(new Link(sdn_in));
		sdn_in.setLink(new Link(sdn_out));
		sdn_out.setLink(pb_error);

//		simulate();
		
		
//		-------------------------------------
//		ANALYTICAL MODELS
		
		Map<Integer, Double> prioRateMap = new HashMap<Integer, Double>();

		for (int topic = 0; topic < numOfTopics; topic++) {
			if (prioRateMap.get(prioritiesMap.get(topic)) == null){
				prioRateMap.put(prioritiesMap.get(topic), (lambdaSubscriptionsMap.get(topic)/topicRateMap.get(topic)));
			} else {
				double value = prioRateMap.get(prioritiesMap.get(topic));
				prioRateMap.put(prioritiesMap.get(topic), value + (lambdaSubscriptionsMap.get(topic)/topicRateMap.get(topic)));
			}
		}
		
		// UPDATE ratePriorityMap
		for (int prio = 0; prio < lambdaPriorityMap.size(); prio++) {
						
			prioRateMap.put(prio, Math.pow((prioRateMap.get(prio)/lambdaPriorityMap.get(prio)), -1));
			System.out.println("Lambda of prio "+prio+": "+lambdaPriorityMap.get(prio));
			System.out.println("Mu of prio "+prio+": "+prioRateMap.get(prio));
		}
		
		Map<Integer, Double> prioDeltaMap = new HashMap<Integer, Double>();
		Map<Integer, Double> prioLqMap = new HashMap<Integer, Double>();
		
		// Estimate Lq and Delta for each prio
		for (int prio = 0; prio < lambdaPriorityMap.size(); prio++) {			
//			System.out.println("Delta_q estimated "+prio+" = " + AnalyticalModels.r_q_prio(lambdaPriorityMap, prio, prioRateMap));

			prioLqMap.put(prio, AnalyticalModels.l_q_prio(lambdaPriorityMap, prio, prioRateMap));
//			System.out.println("Lq estimated "+prio+" = " + prioLqMap.get(prio));
			
//			prioDeltaMap.put(prio, AnalyticalModels.r_prio(lambdaPriorityMap, prio, prioRateMap));
//			System.out.println("AN: Delta of prio "+prio+" = "+prioDeltaMap.get(prio));
		}
			
//		TODO: change names of these params
		double lq = 0;
		double ls = 0;
		double deltas = 0;
		
		double R_broker_in = AnalyticalModels.r_mm1(lambdaMap, BRInPrRate);
		double R_broker_out = AnalyticalModels.r_mm1(lambdaSubscriptionsMap, BROutTrRate);

		double R_model_of_topic = 0;
		
		StringBuilder an_delta = new StringBuilder();
		StringBuilder topics = new StringBuilder();
		an_delta.append("[");
		topics.append("[");
		
		for (int topic = 0; topic < numOfTopics; topic++) {
			
			
			lq = (lambdaSubscriptionsMap.get(topic)/lambdaPriorityMap.get(prioritiesMap.get(topic)))*prioLqMap.get(prioritiesMap.get(topic));
			
			
			ls = lq+(lambdaSubscriptionsMap.get(topic)/topicRateMap.get(topic));
			deltas = ls / lambdaSubscriptionsMap.get(topic);
			
//			System.out.println("AN: Delta for topic "+topic +" = "+deltas);

			if (listOfSubsptions.contains(topic) && lambdaSubscriptionsMap.get(topic) != 0) {
				R_model_of_topic = R_broker_in + R_broker_out
						+ AnalyticalModels.r_mm1(lambdaSubscriptionsMap, SDNInPrRate)
						+ deltas;
			} else if (!listOfSubsptions.contains(topic) && lambdaMap.get(topic) != 0) {
				R_model_of_topic = R_broker_in;
			} else
				R_model_of_topic = 0;

			System.out.println("MODEL: Response Time Topic " + topic + ": " + R_model_of_topic);
			respTimeClassModelMap.put(topic, R_model_of_topic);
			
			an_delta.append(R_model_of_topic+",");
			topics.append(topic+",");
		}
		
		an_delta.append("]");
		topics.append("]");
		
		System.out.println("MODEL: "+an_delta);
		System.out.println("TOPICS: "+topics);
		
		simulate();

		StringBuilder sim_delta = new StringBuilder();
		sim_delta.append("[");
		
//		Iterator entries = Network.responseTimeClassMap.entrySet().iterator();
		Map<Integer,CustomerMeasure> treeMap2 = new TreeMap<>(Network.responseTimeClassMap);
		Iterator entries = treeMap2.entrySet().iterator();	
		
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Integer key = (Integer) entry.getKey();
			CustomerMeasure value = (CustomerMeasure) entry.getValue();
			System.out.println("SIM: Response Time Topic " + key + ", Value = " + value.mean());
			
			sim_delta.append(value.mean()+",");

		}
		
		sim_delta.append("]");
		
		System.out.println("SIM: "+sim_delta);

		System.out.println("Completions: " + Network.completions);
		System.out.println("Overal packets dropped (error rate): " + Network.dropPackets);
		System.out.println("Overal packets dropped (prio probs): " + Network.dropPrioPackets);
		System.out.println("Overal Events dropped (lack of subs): " + Network.eventsDrop);
				
		System.out.println("Buffer capacity: " + sdn_out.meanNoOfQueuedCustomers());
		System.out.println("MAX Buffer capacity: " + sdn_out.maxQueueLenght());
		
		System.out.println("Overal Multiclass-prio Buffer Losses: " + sdn_out.getLosses());


		// Network.logResults();
	}

	public static void main(String args[]) {		

//		args[0] = "tests/100topics_noprios.json";
//		args[0] = "tests/100topics_prioprobs.json";
//		args[0] = "tests/100topics_prios.json";
//		args[0] = "tests/100topics_prios_v2.json";
		
//		args[0] = "tests/200topics_noprios.json";
//		args[0] = "tests/200topics_prioprobs.json";
//		args[0] = "tests/200topics_prios.json";
//		args[0] = "tests/200topics_prios_v2.json";
		
//		args[0] = "tests/sat_200_topics_noprios.json";
//		args[0] = "tests/sat_200_topics_prios.json";
//		args[0] = "tests/sat_200_topics_prioprobs.json";
		
//		args[0] = "tests/sat_200_topics_noprios_v3.json";
//		args[0] = "tests/sat_200_topics_prios_v3.json";
		args[0] = "tests/sat_200_topics_prioprobs_v3.json";
		
		
		new PubsubV7Sim(3000, 2000000, args[0]);
		// Network.displayResults(0.01);

		try {

			File file = new File(args[1]);

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
			BufferedWriter bw = new BufferedWriter(fw);

			StringBuilder sucrate = new StringBuilder();
			sucrate.append("[");
			
			Map<Integer,CustomerMeasure> treeMap = new TreeMap<>(Network.responseTimeClassMap);
			Iterator entries = treeMap.entrySet().iterator();			
			
			double drops = 0;
			double succesrate = 0;
			while (entries.hasNext()) {
				Map.Entry entry = (Map.Entry) entries.next();
				Integer key = (Integer) entry.getKey();
				CustomerMeasure value = (CustomerMeasure) entry.getValue();
				if (Network.completionsClassMap.get(key) == 0) {
					bw.write(Double.toString(value.mean()) + ",0.0" + "," + respTimeClassModelMap.get(key));
					sucrate.append(0+",");
				} else {
					drops = Network.dropsClassMap.get(key) + Network.dropPrioClassMap.get(key) + Network.dropsBufferClassMap.get(key);
					succesrate = (double) Network.completionsClassMap.get(key) / ((double) Network.completionsClassMap.get(key) + drops);
					bw.write(Double.toString(value.mean()) + "," + succesrate + "," + respTimeClassModelMap.get(key));
					sucrate.append(succesrate+",");
				}
//				if (Network.completionsClassMap.get(key) == 0)
//					bw.write(Double.toString(value.mean()) + ",0.0");
//				else {
//					drops = Network.dropsClassMap.get(key) + Network.dropPrioClassMap.get(key);
//					bw.write(Double.toString(value.mean()) + ","
//							+ ((double) Network.completionsClassMap.get(key)
//									/ ((double) Network.completionsClassMap.get(key) + drops)));
//				}
				bw.write("\n");

			}

			bw.close();
			
			sucrate.append("]");
			System.out.println("SR: "+sucrate);

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
