package pubsubpriorities;

import java.util.Iterator;
import java.util.Map;

import extensions.ClassSource;
import extensions.PrioSource;
import extensions.SinkMulticlass;
import extensions.SinkPriorities;
import network.*;
import tools.*;

class PubsubV1Sim extends Sim {

	public static double duration = 0;

	// Example termination function
	public boolean stop() {
		return now() > duration;
	}

	public PubsubV1Sim(double d) {
		duration = d;
		Network.initialise();
		
		int numOfTopics = 4;
		int numOfPrio = 4;
		
		double lambda1 = 1;
		double lambda2 = 1;
		double lambda3 = 2;
		double lambda4 = 1;
		
		double BRInPrRate = 64;
		double BROutTrRate = 64;
		double SDNInPrRate = 16;
		
		double SDNOutTrTopicRate1 = 16;
		double SDNOutTrTopicRate2 = 16;
		double SDNOutTrTopicRate3 = 16;
		double SDNOutTrTopicRate4 = 16;
		
		Node source1 = new PrioSource("Source", new Exp(lambda1), 1, 0);
		Node source2 = new PrioSource("Source", new Exp(lambda2), 2, 0);
		Node source3 = new PrioSource("Source", new Exp(lambda3), 3, 0);
		Node source4 = new PrioSource("Source", new Exp(lambda4), 4, 1);
		
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
		
		int[] SDNOutTrOveralClasses = {1, 2, 3, 4};
		DistributionSampler[] SDNOutTrOveralDistrib = {new Exp(SDNOutTrTopicRate1), new Exp(SDNOutTrTopicRate2), new Exp(SDNOutTrTopicRate3), new Exp(SDNOutTrTopicRate4)};
		ClassDependentDelay SDNOutTrOveralDelay = new ClassDependentDelay(SDNOutTrOveralClasses, SDNOutTrOveralDistrib);

		QueueingNode sdn_out = new QueueingNode("MULTICLASS SDN OUT", SDNOutTrOveralDelay, 1);
		
		
		SinkMulticlass sinkMulticlass = new SinkMulticlass("Sink Overal");

		source1.setLink(new Link(broker_in));
		source2.setLink(new Link(broker_in));
		source3.setLink(new Link(broker_in));
		source4.setLink(new Link(broker_in));
		broker_in.setLink(new Link(broker_out));
		broker_out.setLink(new Link(sdn_in));
		sdn_in.setLink(new Link(sdn_out));
		sdn_out.setLink(new Link(sinkMulticlass));

		simulate();

		double R_broker_in = (1/BRInPrRate) / (1 - ((lambda1+lambda2+lambda3+lambda4) * (1/BRInPrRate)));
		
		double R_broker_out = (1/BROutTrRate) / (1 - ((lambda1+lambda2+lambda3+lambda4) * (1/BROutTrRate)));
		
		double ro0 = (lambda1+lambda2+lambda3) / SDNInPrRate;
		double ro1 = lambda4 / SDNInPrRate;
		
		double R_SDN_IN_PRIO_0 = ((1 + ro1) / SDNInPrRate) / (1 - ro0);
		double R_SND_IN_PRIO_1 = ((1 - (ro0 * (1 - ro0 - ro1))) / SDNInPrRate) / ((1 - ro0)*(1 - ro0 - ro1));
		
		
		double R_SDN_OUT_TOPIC_1 = (1/SDNOutTrTopicRate1) / (1 - ((lambda1 * (1/SDNOutTrTopicRate1)) + (lambda2 * (1/SDNOutTrTopicRate2)) + (lambda3 * (1/SDNOutTrTopicRate3)) + (lambda4 * (1/SDNOutTrTopicRate4))));
		double R_SDN_OUT_TOPIC_2 = (1/SDNOutTrTopicRate2) / (1 - ((lambda1 * (1/SDNOutTrTopicRate1)) + (lambda2 * (1/SDNOutTrTopicRate2)) + (lambda3 * (1/SDNOutTrTopicRate3)) + (lambda4 * (1/SDNOutTrTopicRate4))));
		double R_SDN_OUT_TOPIC_3 = (1/SDNOutTrTopicRate3) / (1 - ((lambda1 * (1/SDNOutTrTopicRate1)) + (lambda2 * (1/SDNOutTrTopicRate2)) + (lambda3 * (1/SDNOutTrTopicRate3)) + (lambda4 * (1/SDNOutTrTopicRate4))));
		double R_SDN_OUT_TOPIC_4 = (1/SDNOutTrTopicRate4) / (1 - ((lambda1 * (1/SDNOutTrTopicRate1)) + (lambda2 * (1/SDNOutTrTopicRate2)) + (lambda3 * (1/SDNOutTrTopicRate3)) + (lambda4 * (1/SDNOutTrTopicRate4))));
		
		System.out.println("SIM: Response Time Topic 1: "+ (R_broker_in + R_broker_out + R_SDN_IN_PRIO_0 + R_SDN_OUT_TOPIC_1));
		System.out.println("SIM: Response Time Topic 1: "+ (R_broker_in + R_broker_out + R_SDN_IN_PRIO_0 + R_SDN_OUT_TOPIC_2));
		System.out.println("SIM: Response Time Topic 1: "+ (R_broker_in + R_broker_out + R_SDN_IN_PRIO_0 + R_SDN_OUT_TOPIC_3));
		System.out.println("SIM: Response Time Topic 1: "+ (R_broker_in + R_broker_out + R_SND_IN_PRIO_1 + R_SDN_OUT_TOPIC_4));
		
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

		new PubsubV1Sim(100000);
//		Network.displayResults(0.01);

	}

}
