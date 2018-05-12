package network;

import tools.*;

public class PriorityQueue extends Queue {
	int nqueues;
	protected Queue[] qs;

	//
	// Customer priorities must be 0, 1, .., nqueues-1
	// Priority 0 is the highest priority
	//
	public PriorityQueue(int n) {
		nqueues = n;
		buildPriorityQueue();
	}

	public PriorityQueue(int n, int bcap) {
		nqueues = n;
		buildPriorityQueue(bcap);
	}

	//
	// By default each individual queue is FIFO...
	//
	protected Queue buildOneQueue() {
		return new FIFOQueue();
	}
	
	protected Queue buildOneQueue(int bcap) {
		return new FIFOQueue(bcap);
	}

	void buildPriorityQueue() {
		qs = new Queue[nqueues];
		for (int i = 0; i < nqueues; i++) {
			qs[i] = buildOneQueue();
		}
	}

	void buildPriorityQueue(int bcap) {
		qs = new Queue[nqueues];
		for (int i = 0; i < nqueues; i++) {
			qs[i] = buildOneQueue(bcap);
		}
	}

	//
	// Overrides superclass method.
	//
	public boolean canAccept(Customer c) {
		return qs[c.getPriority()].canAccept(c);
	}

	//
	// Define superclass abstract methods...
	//

	protected void insertIntoQueue(Customer e) {
		int priority = e.getPriority();
		qs[priority].enqueue(e);
	}

	protected void insertAtHeadOfQueue(Customer e) {
		int priority = e.getPriority();
		qs[priority].enqueueAtHead(e);
	}

	protected Customer headOfQueue() {
		for (int i = 0; i < nqueues; i++) {
			if (qs[i].queueLength() > 0) {
				return qs[i].head();
			}
		}
		Check.check(false, "Priority queue - " + "all queues empty during head\n" + "(This cannot happen!)");
		return null;
	}

	protected Customer removeFromQueue() {
		for (int i = 0; i < nqueues; i++) {
			if (qs[i].queueLength() > 0) {
				return qs[i].dequeue();
			}
		}
		Check.check(false, "Priority queue - " + "all queues empty during remove\n" + "(This cannot happen!)");
		return null;
	}

}
