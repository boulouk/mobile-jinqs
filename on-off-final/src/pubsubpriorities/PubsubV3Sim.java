package pubsubpriorities;

import java.io.FileReader;
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
import extensions.SinkMulticlass;
import extensions.SinkPriorities;
import network.*;
import tools.*;

class PubsubV3Sim extends Sim {

	public static double duration = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}

	public PubsubV3Sim(double d) {
		duration = d;
		Network.initialise();
		
//		get number of topics from JSON input file
		int numOfTopics = getJSONArray("example_input_params.json", "lambdas").size();
		
		double BRInPrRate = 64;
		double BROutTrRate = 64;
		double SDNInPrRate = 16;
		
//		initiate lambdas and topic-rates from JSON input file
		Map<Integer,Double> lambdaMap = new HashMap<Integer,Double>();
		Map<Integer,Double> topicRateMap = new HashMap<Integer,Double>();
		
		Iterator<Double> getLambdasIterator = getJSONArray("example_input_params.json", "lambdas").iterator();
		Iterator<Long> getMusIterator = getJSONArray("example_input_params.json", "mus").iterator();
		
		for(int i=1; i<=numOfTopics; i++) {
			if (getLambdasIterator.hasNext() && getMusIterator.hasNext()) {
				lambdaMap.put(i, getLambdasIterator.next());
				topicRateMap.put(i, (double) getMusIterator.next());
			}
		}
		
		if (ro_mm1(lambdaMap, BRInPrRate) >= 1) {
			System.err.println("RO > 1 on 1st MM1 queue");
			System.exit(0);
		} else if (ro_mm1(lambdaMap, BRInPrRate) >=1) {
			System.err.println("RO > 1 on 2nd MM1 queue");
			System.exit(0);
		} else if (ro_prio(lambdaMap, SDNInPrRate) >=1) {
			System.err.println("RO > 1 on 3rd Priority queue");
			System.exit(0);
		} else if (ro_multiclass(lambdaMap, topicRateMap) >=1) {
			System.err.println("RO > 1 on 4th Multiclass queue");
			System.exit(0);
		} else
			System.out.println("RO at every queue is OK");		
		
//		initiate sources and and priorities
		Map<Integer,Double> lambdaPriorityMap = new HashMap<Integer,Double>();
		Map<Integer,Node> sourceMap = new HashMap<Integer,Node>(); 

		
		Iterator<Long> getPrioIterator = getJSONArray("example_input_params.json", "priorities").iterator();
		
		int intprio = 0;
		int maxPrio = 0;
		
		for(int i=1; i<=numOfTopics; i++) {
			Node source = null;
			
			if (getPrioIterator.hasNext()) {
				Long lprio = (Long) getPrioIterator.next();
				intprio = lprio.intValue();
				if(intprio > maxPrio)
					maxPrio = intprio;
				source = new PrioSource("Topic Source "+i, new Exp(lambdaMap.get(i)), i, intprio);
			}
	
			System.out.println("Topic Source "+i+" Priority: "+ intprio);
			sourceMap.put(i, source);

		}
		
		
		Exp BRInPrDistrib = new Exp(BRInPrRate);
		Delay BRInPrDelay = new Delay(BRInPrDistrib);
		QueueingNode broker_in = new QueueingNode("MM1 BROKER IN", BRInPrDelay, 1);
		
		Exp BROutTrDistrib = new Exp(BROutTrRate);
		Delay BROutTrDelay = new Delay(BROutTrDistrib);
		QueueingNode broker_out = new QueueingNode("MM1 BROKER OUT", BROutTrDelay, 1);
		
		PriorityQueue prioq = new PriorityQueue(maxPrio+1);
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

		new PubsubV3Sim(400000);
//		Network.displayResults(0.01);

	}
	
	public JSONArray getJSONArray (String file, String listname) {
		
		JSONParser parser = new JSONParser();
        Object obj;
        JSONArray listArray = null;
        
		try {
			obj = parser.parse(new FileReader(file));
			JSONObject jsonObject = (JSONObject) obj;
	        
			listArray = (JSONArray) jsonObject.get(listname);
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listArray;
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
