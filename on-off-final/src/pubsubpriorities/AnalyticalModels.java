package pubsubpriorities;

import java.util.Iterator;
import java.util.Map;

public class AnalyticalModels {
	
	public static double getOveralLambda (Map lambdamap) {
		double overallambda = 0;
		
		Iterator entries = lambdamap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Double lambdavalue = (Double) entry.getValue();
			overallambda = overallambda + lambdavalue;
		}
		
		return overallambda;
	}
	
	public static double getOveralRodevMu (Map lambdamap, double rate) {
		double overalrodevmu = 0;
		
		Iterator entries = lambdamap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Double lambdavalue = (Double) entry.getValue();
			overalrodevmu = overalrodevmu + ((lambdavalue/rate)/rate);
		}
		
		return overalrodevmu;
	}
	
	public static double getOveralRodevMu (Map lambdamap, Map ratepriomap) {
		double overalrodevmu = 0;
		
		Iterator entries = lambdamap.entrySet().iterator();
		Iterator m_entries = ratepriomap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Map.Entry m_entry = (Map.Entry) m_entries.next();
			Double lambdavalue = (Double) entry.getValue();
			Double ratepriovalue = (Double) m_entry.getValue();
			
			overalrodevmu = overalrodevmu + ((lambdavalue/ratepriovalue)/ratepriovalue);
		}
		
		return overalrodevmu;
	}
	
	public static double getSigmaRobyPrio (Map lambdamap, int prio, double rate) {
		double overalro = 0;
		
		if (prio < 0) {
			overalro = 0;
		} else {
			int i = 0;
			Iterator entries = lambdamap.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry entry = (Map.Entry) entries.next();
				Double lambdavalue = (Double) entry.getValue();
				
				if (i == prio) {
					overalro = overalro + (lambdavalue/rate);
					break;
				}
				
				overalro = overalro + (lambdavalue/rate);
				i++;
			}
		}
		
		
		return overalro;
	}
	
	public static double getSigmaRobyPrio (Map lambdamap, int prio, Map ratepriomap) {
		double overalro = 0;
		
		if (prio < 0) {
			overalro = 0;
		} else {
			int i = 0;
			Iterator entries = lambdamap.entrySet().iterator();
			Iterator m_entries = ratepriomap.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry entry = (Map.Entry) entries.next();
				Map.Entry m_entry = (Map.Entry) m_entries.next();
				Double lambdavalue = (Double) entry.getValue();
				Double ratepriovalue = (Double) m_entry.getValue();
				
				if (i == prio) {
					overalro = overalro + (lambdavalue/ratepriovalue);
					break;
				}
				
				overalro = overalro + (lambdavalue/ratepriovalue);
				i++;
			}
		}
		return overalro;
	}
	
	public static double r_mm1(Map lambdamap, double rate) {
		double r = 0;
		double overallambda = getOveralLambda(lambdamap); 

		r = (1 / rate) / (1 - (overallambda * (1 / rate)));

		return r;
	}

	
	
	public static double r_prio(Map lambdapriomap, int priority, double priorate) {
		double r = 0;
		double numerator = getOveralRodevMu(lambdapriomap,priorate); 

		double denominator = (1-getSigmaRobyPrio(lambdapriomap, priority-1, priorate)) * (1-getSigmaRobyPrio(lambdapriomap, priority, priorate));
		
		r = (numerator/denominator) + 1/priorate; 
		return r;
	}
	
	public static double r_prio(Map lambdapriomap, int priority, Map<Integer, Double> prioratemap) {
		double r = 0;
		double r_q = AnalyticalModels.r_q_prio(lambdapriomap, priority, prioratemap);
		
		r = r_q + (1/prioratemap.get(priority)); 
		return r;
	}
	
	public static double l_q_prio(Map<Integer, Double> lambdapriomap, int priority, Map prioratemap) {
		double l_q = 0;
		double r_q = AnalyticalModels.r_q_prio(lambdapriomap, priority, prioratemap);
		
		l_q = r_q * lambdapriomap.get(priority); 
		return l_q;
	}
	
	public static double r_q_prio(Map lambdapriomap, int priority, Map prioratemap) {
		double r = 0;
		double numerator = getOveralRodevMu(lambdapriomap,prioratemap); 
		
		double sigma1 = getSigmaRobyPrio(lambdapriomap, priority-1, prioratemap);
		double sigma2 = getSigmaRobyPrio(lambdapriomap, priority, prioratemap);
		
		double denominator = (1-sigma1) * (1-sigma2);
		
		r = (numerator/denominator); 
		return r;
	}
	
	public static double r_prio_old(Map lambdapriomap, int priority, double priorate) {
		double r = 0;
		double overallambda = getOveralLambda(lambdapriomap); 
		double lambdasum1 = 0;
		double lambdasum2 = 0;
		
		Iterator entries2 = lambdapriomap.entrySet().iterator();
		int j = 0;
		while (entries2.hasNext()) {
			Map.Entry entry = (Map.Entry) entries2.next();
			Integer key = (Integer) entry.getKey();
			Double lambdavalue = (Double) entry.getValue();

			if(j<priority) {
				lambdasum1 = lambdasum1 + lambdavalue;
			} else if (j==priority) {
				lambdasum2 = lambdasum2 + lambdavalue;
				break;
			} else
				System.err.println("There is a priority issue...");
			
			j++;
		}
		
		r = (overallambda / ((priorate - lambdasum1) * (priorate - lambdasum2))) + (1/priorate);

		return r;
	}
	
	public static double r_multiclass(int topicID, Map lambdamap, Map<Integer, Double> topicratemap) {
		double r = 0;
		double r_numerator = 0;
		double r_denominator_part = 0;
		double r_denominator = 0;

		r_numerator = 1 / topicratemap.get(topicID);

		Iterator entries = lambdamap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Integer key = (Integer) entry.getKey();
			Double lambdavalue = (Double) entry.getValue();

			r_denominator_part = r_denominator_part + (lambdavalue * (1 / topicratemap.get(key)));
		}

		r_denominator = 1 - r_denominator_part;
		r = r_numerator / r_denominator;

		return r;
	}
	

	public static double ro_mm1(Map lambdamap, double rate) {
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

	public static double ro_multiclass(Map lambdamap, Map<Integer, Double> topicratemap) {
		double ro = 0;

		Iterator entries = lambdamap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Integer key = (Integer) entry.getKey();
			Double lambdavalue = (Double) entry.getValue();

			ro = ro + (lambdavalue / topicratemap.get(key));
		}

		return ro;
	}

	public static double ro_prio(Map lambdamap, double priorate) {
		double ro = 0;

		Iterator entries = lambdamap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Integer key = (Integer) entry.getKey();
			Double lambdavalue = (Double) entry.getValue();

			ro = ro + lambdavalue / priorate;
		}

		return ro;
	}

}
