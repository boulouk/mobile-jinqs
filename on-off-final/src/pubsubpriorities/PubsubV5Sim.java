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

class PubsubV5Sim extends Sim {

	public static double duration = 0;
	public static Map<Integer, Double> respTimeClassModelMap;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}

	public PubsubV5Sim(double d, String inParams) {
		duration = d;
		Network.initialise();
		respTimeClassModelMap = new HashMap<Integer, Double>();

		double BRInPrRate = 64000;
		double BROutTrRate = 64000;
		double SDNInPrRate = 64000;

		// get number of topics from JSON input file
		int numOfTopics = JsonRead.getJSONArray(inParams, "lambdas").size();
		double errorRate = JsonRead.getParam(inParams, "error_rate");

		// initialization of list of subscriptions
		Iterator<Long> subsIterator = JsonRead.getJSONArray(inParams, "subscriptions").iterator();
		ArrayList<Integer> listOfSubsptions = new ArrayList<>();
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
		if((maxPrio+1) != prioProbs.size()) {
			System.err.println("There are not probs for each priority class!");
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
		
		// UPDATE lambdaPriorityMap based on probs for each prio
		for (int prio = 0; prio < lambdaPriorityMap.size(); prio++) {
			if (lambdaPriorityMap.get(prio) == null)
				lambdaPriorityMap.put(prio, 0.0);
			else
				lambdaPriorityMap.put(prio, lambdaPriorityMap.get(prio) * prioProbs.get(prio));
		}
		
		double ro3rdPRIOqueue = AnalyticalModels.ro_prio(lambdaPriorityMap, SDNInPrRate);
		System.out.println("RO on 3rd (NON-preemptive Priority) queue: " + ro3rdPRIOqueue);
		
		if (ro3rdPRIOqueue >= 1) {
			System.err.println("RO > 1 on 3rd (NON-preemptive Priority) Queue");
			System.exit(1);
		} 
		
		// UPDATE lambda subscriptions based on the priority probabilities
		for (int topic = 0; topic < numOfTopics; topic++) {
			lambdaSubscriptionsMap.put(topic, lambdaSubscriptionsMap.get(topic) * prioProbs.get(prioritiesMap.get(topic)));

			// initialization of resp times, drops, etc of the simulator
//			TODO: do this initialization in another point
			Network.dropsClassMap.put(topic, 0);
			Network.dropPrioClassMap.put(topic, 0);
			Network.completionsClassMap.put(topic, 0);
			CustomerMeasure responseTime = new CustomerMeasure();
			Network.responseTimeClassMap.put(topic, responseTime);
			Network.responseTimeClassMap.get(topic).add(0);
			
//			System.out.println("Final Lambda for each topic: " + topic + ": " + lambdaSubscriptionsMap.get(topic));

		}
		
		double ro4thMULTIqueue = AnalyticalModels.ro_multiclass(lambdaSubscriptionsMap, topicRateMap);
		System.out.println("RO on 4th (Multi-class) queue: " + ro4thMULTIqueue);
		
		if (ro4thMULTIqueue >= 1) {
			System.err.println("RO > 1 on 4th (Multi-class) Queue");
			System.exit(1);
		} else
			System.out.println("RO at every queue is OK");

		Exp BRInPrDistrib = new Exp(BRInPrRate);
		Delay BRInPrDelay = new Delay(BRInPrDistrib);
		QueueingNode broker_in = new QueueingNode("MM1 BROKER IN", BRInPrDelay, 1);

		Exp BROutTrDistrib = new Exp(BROutTrRate);
		Delay BROutTrDelay = new Delay(BROutTrDistrib);
		QueueingNode broker_out = new QueueingNode("MM1 BROKER OUT", BROutTrDelay, 1);

		PriorityQueue prioq = new PriorityQueue(maxPrio + 1);
		Exp SDNInPrDistrib = new Exp(SDNInPrRate);
		Delay SDNInPrDelay = new Delay(SDNInPrDistrib);

		QNPrioProbs sdn_in = new QNPrioProbs("PRIORITY SDN IN", SDNInPrDelay, 1, prioq, prioProbs);

		// initialization of multiclass queue
		int[] arrayOfTopics = new int[numOfTopics];

		DistributionSampler[] arrayOfTopicRateDistrib = new DistributionSampler[numOfTopics];
		for (int topic = 0; topic < arrayOfTopics.length; topic++) {
			arrayOfTopics[topic] = topic;
			arrayOfTopicRateDistrib[topic] = new Exp(topicRateMap.get(topic));
		}

		ClassDependentDelay SDNOutTrOveralDelay = new ClassDependentDelay(arrayOfTopics, arrayOfTopicRateDistrib);
		QueueingNode sdn_out = new QueueingNode("MULTICLASS SDN OUT", SDNOutTrOveralDelay, 1);

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

		simulate();

		double R_broker_in = AnalyticalModels.r_mm1(lambdaMap, BRInPrRate);
		double R_broker_out = AnalyticalModels.r_mm1(lambdaSubscriptionsMap, BROutTrRate);

		double R_model_of_topic = 0;
		for (int topic = 0; topic < numOfTopics; topic++) {

			if (listOfSubsptions.contains(topic) && lambdaSubscriptionsMap.get(topic) != 0) {
				R_model_of_topic = R_broker_in + R_broker_out
						+ AnalyticalModels.r_prio(lambdaPriorityMap, prioritiesMap.get(topic), SDNInPrRate)
						+ AnalyticalModels.r_multiclass(topic, lambdaSubscriptionsMap, topicRateMap);
			} else if (!listOfSubsptions.contains(topic) && lambdaMap.get(topic) != 0) {
				R_model_of_topic = R_broker_in;
			} else
				R_model_of_topic = 0;

			System.out.println("MODEL: Response Time Topic " + topic + ": " + R_model_of_topic);
			respTimeClassModelMap.put(topic, R_model_of_topic);
		}

		Iterator entries = Network.responseTimeClassMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Integer key = (Integer) entry.getKey();
			CustomerMeasure value = (CustomerMeasure) entry.getValue();
			System.out.println("SIM: Response Time Topic " + key + ", Value = " + value.mean());

		}

		System.out.println("Completions: " + Network.completions);
		System.out.println("Overal packets dropped (error rate): " + Network.dropPackets);
		System.out.println("Overal packets dropped (prio probs): " + Network.dropPrioPackets);
		System.out.println("Overal Events dropped (lack of subs): " + Network.eventsDrop);

		// Network.logResults();
	}

	public static void main(String args[]) {

		new PubsubV5Sim(400000, args[0]);
		// Network.displayResults(0.01);

		try {

			File file = new File(args[1]);

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
			BufferedWriter bw = new BufferedWriter(fw);

			Iterator entries = Network.responseTimeClassMap.entrySet().iterator();
			double drops = 0;
			while (entries.hasNext()) {
				Map.Entry entry = (Map.Entry) entries.next();
				Integer key = (Integer) entry.getKey();
				CustomerMeasure value = (CustomerMeasure) entry.getValue();
				if (Network.completionsClassMap.get(key) == 0)
					bw.write(Double.toString(value.mean()) + ",0.0" + "," + respTimeClassModelMap.get(key));
				else {
					drops = Network.dropsClassMap.get(key) + Network.dropPrioClassMap.get(key);
					bw.write(Double.toString(value.mean()) + ","
							+ ((double) Network.completionsClassMap.get(key)
									/ ((double) Network.completionsClassMap.get(key) + drops) + ","
									+ respTimeClassModelMap.get(key)));
				}
				bw.write("\n");

			}

			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
