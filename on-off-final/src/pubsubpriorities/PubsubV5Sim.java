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
		Iterator<Double> prioProbsIterator = JsonRead.getJSONArray(inParams, "prio_drops").iterator();
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

		for (int i = 0; i < numOfTopics; i++) {
			if (getLambdasIterator.hasNext() && getMusIterator.hasNext()) {
				double lamb = getLambdasIterator.next();
				lambdaMap.put(i, lamb);
				topicRateMap.put(i, getMusIterator.next());

				if (listOfSubsptions.contains(i)) {
					lambdaSubscriptionsMap.put(i, lamb);
				} else
					lambdaSubscriptionsMap.put(i, (double) 0);
			}
		}

		// initialization of sources and and priorities
		Map<Integer, Double> lambdaPriorityMap = new HashMap<Integer, Double>();
		Map<Integer, Integer> prioritiesMap = new HashMap<Integer, Integer>();
		Map<Integer, Node> sourceMap = new HashMap<Integer, Node>();

		Iterator<Long> getPrioIterator = JsonRead.getJSONArray(inParams, "priorities").iterator();

		int intprio = 0;
		int maxPrio = 0;

		for (int i = 0; i < numOfTopics; i++) {
			Node source = null;

			if (getPrioIterator.hasNext()) {
				Long lprio = (Long) getPrioIterator.next();
				intprio = lprio.intValue();
				if (intprio > maxPrio)
					maxPrio = intprio;

				if (lambdaMap.get(i) != 0)
					source = new PrioSource("Topic Source " + i, new Exp(lambdaMap.get(i)), i, intprio);

				prioritiesMap.put(i, intprio);

				// initialization of lambda priority rates
				if (lambdaPriorityMap.get(intprio) == null)
					lambdaPriorityMap.put(intprio, lambdaSubscriptionsMap.get(i));
				else
					lambdaPriorityMap.put(intprio, lambdaPriorityMap.get(intprio) + lambdaSubscriptionsMap.get(i));
			}

			System.out.println("Topic Source " + i + " Priority: " + intprio);
			sourceMap.put(i, source);

		}

		// update lambda prio and subscriptions based on the priority probabilities
		for (int i = 0; i < numOfTopics; i++) {
			lambdaSubscriptionsMap.put(i, lambdaSubscriptionsMap.get(i) * prioProbs.get(prioritiesMap.get(i)));

			// initialization of resp times, drops, etc for the simulation results
			Network.dropsClassMap.put(i, 0);
			Network.dropPrioClassMap.put(i, 0);
			Network.completionsClassMap.put(i, 0);
			CustomerMeasure responseTime = new CustomerMeasure();
			Network.responseTimeClassMap.put(i, responseTime);
			Network.responseTimeClassMap.get(i).add(0);

		}
		for (int i = 0; i < lambdaPriorityMap.size(); i++) {
			lambdaPriorityMap.put(i, lambdaPriorityMap.get(i) * prioProbs.get(i));
		}

		if (AnalyticalModels.ro_mm1(lambdaMap, BRInPrRate) >= 1) {
			System.err.println("RO > 1 on 1st Queue (MM1)");
			System.exit(1);
		} else if (AnalyticalModels.ro_mm1(lambdaSubscriptionsMap, BRInPrRate) >= 1) {
			System.err.println("RO > 1 on 2nd Queue (MM1)");
			System.exit(1);
		} else if (AnalyticalModels.ro_prio(lambdaPriorityMap, SDNInPrRate) >= 1) {
			System.err.println("RO > 1 on 3rd Queue (NON-preemptive Priority)");
			System.exit(1);
		} else if (AnalyticalModels.ro_multiclass(lambdaSubscriptionsMap, topicRateMap) >= 1) {
			System.err.println("RO > 1 on 4th Queue (Multiclass)");
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
		for (int i = 0; i < arrayOfTopics.length; i++) {
			arrayOfTopics[i] = i;
			arrayOfTopicRateDistrib[i] = new Exp(topicRateMap.get(i));
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
		for (int i = 0; i < numOfTopics; i++) {
			if (sourceMap.get(i) != null)
				sourceMap.get(i).setLink(new Link(broker_in));
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
		for (int i = 0; i < numOfTopics; i++) {

			if (listOfSubsptions.contains(i) && lambdaSubscriptionsMap.get(i) != 0) {
				R_model_of_topic = R_broker_in + R_broker_out
						+ AnalyticalModels.r_prio(lambdaPriorityMap, prioritiesMap.get(i), SDNInPrRate)
						+ AnalyticalModels.r_multiclass(i, lambdaSubscriptionsMap, topicRateMap);
			} else if (!listOfSubsptions.contains(i) && lambdaMap.get(i) != 0) {
				R_model_of_topic = R_broker_in;
			} else
				R_model_of_topic = 0;

			System.out.println("MODEL: Response Time Topic " + i + ": " + R_model_of_topic);
			respTimeClassModelMap.put(i, R_model_of_topic);
		}

		Iterator entries = Network.responseTimeClassMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Integer key = (Integer) entry.getKey();
			CustomerMeasure value = (CustomerMeasure) entry.getValue();
			System.out.println("SIM: Response Time Topic " + key + ", Value = " + value.mean());
			// System.out.println("SIM: Completions of Topic " + key + ", Value = " +
			// Network.completionsClassMap.get(key));
			// System.out.println("SIM: Prio drops of Topic " + key + ", Value = " +
			// Network.dropPrioClassMap.get(key));
			// System.out.println("SIM: Drops of Topic " + key + ", Value = " +
			// Network.dropsClassMap.get(key));
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
