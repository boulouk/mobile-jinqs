package tools;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Georgios Bouloukakis (boulouk@gmail.com)
 * 
 *         QueueProbs.java Created: 5 févr. 2016 Description:
 */
public class QueueProbs {

	private HashMap<Integer, Integer> customersMap;
	private Iterator iterator;
	private double probsSum = 0;

	public QueueProbs() {
		customersMap = new HashMap<Integer, Integer>();
	}

	public void add(int num) {

		Integer value = (Integer) customersMap.get(num);

		if (value == null) {
			customersMap.put(num, 1);
		} else {
			customersMap.put(num, value + 1);
		}
	}

	public StringBuilder getProbabilities() {
		String probs = null;
		StringBuilder sb = new StringBuilder();

		int sum = this.getSum();
		Iterator it = this.getIterator();

		while (it.hasNext()) {
			int key = (Integer) it.next();
			int value = this.getCustomersMap().get(key);

			Double valueDouble = (double) value;
			double prob = valueDouble / sum;
			sb.append(key + ": " + Double.toString(prob) + ": " + value + "/" + sum);
			sb.append("\n");

		}

		return sb;
	}

	public Double getMeanProbability() {
		Double prob = null;

		int sum = this.getSum();
		Iterator it = this.getIterator();
		double sumProbs = 0;
		while (it.hasNext()) {
			int key = (Integer) it.next();
			int value = this.getCustomersMap().get(key);

			Double valueDouble = (double) value;

			sumProbs = sumProbs + ((valueDouble / sum) * key);
		}

		return sumProbs;
	}

	public int getSum() {
		int sum = 0;
		Iterator it = this.getIterator();

		while (it.hasNext()) {
			Integer key = (Integer) it.next();
			Integer value = this.getCustomersMap().get(key);
			sum = sum + value;
		}
		return sum;
	}

	public HashMap<Integer, Integer> getCustomersMap() {
		return customersMap;
	}

	public Iterator getIterator() {
		iterator = this.getCustomersMap().keySet().iterator();
		return iterator;
	}

	public double getProbsSum() {
		return probsSum;
	}

	public void setProbsSum(double probsSum) {
		this.probsSum = probsSum;
	}

}
