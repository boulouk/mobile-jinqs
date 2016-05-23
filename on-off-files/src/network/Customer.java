package network;

import tools.*;

public class Customer implements Ordered {
	private static int customerId = 0;
	private int id;
	private int type;
	private int priority;
	private double arrivalTime;
	private double serviceDemand;
	private double queueInsertionTime;
	private Node location = Network.nullNode;

	// -----------------------------------
	private double lifetime;
	private double queueingNodeInsertionTime;
	private double arriveForService = 0;
	private double arriveForServiceOFF = 0;
	private double arriveForServiceON = 0;

	private boolean off;
	private boolean badLack;
	private boolean expired;
	private boolean withLifetime;
	
	
	
	

	// -----------------------------------

	public Customer() {
		id = customerId++;
		priority = 0;
		arrivalTime = Sim.now();
		off = false;
		badLack = false;
		withLifetime = false;
		expired = false;
	}

	public Customer(int type) {
		id = customerId++;
		priority = 0;
		Check.check(type >= 0 && type <= Network.maxClasses, "ERROR: Customer "
				+ id + " class out of bounds");
		this.type = type;
		arrivalTime = Sim.now();
		off = false;
		badLack = false;
		withLifetime = false;
		expired = false;
	}

	public Customer(int type, int priority) {
		id = customerId++;
		Check.check(type >= 0 && type <= Network.maxClasses, "ERROR: Customer "
				+ id + " class out of bounds");
		Check.check(priority >= 0 && priority <= Network.maxPriorities,
				"ERROR: Customer " + id + " priority out of bounds");
		this.type = type;
		this.priority = priority;
		arrivalTime = Sim.now();
	}

	public String toString() {
		return ("Customer " + id + " (class " + type + ", priority " + priority + ")");
	}

	public int getId() {
		return id;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setServiceDemand(double d) {
		serviceDemand = d;
	}

	public double getServiceDemand() {
		return serviceDemand;
	}

	public void setQueueInsertionTime(double d) {
		queueInsertionTime = d;
	}

	public double getQueueInsertionTime() {
		return queueInsertionTime;
	}

	public Node getLocation() {
		return location;
	}

	public void setLocation(Node n) {
		location = n;
	}

	public int getclass() {
		return type;
	}

	public void setclass(int t) {
		type = t;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int p) {
		priority = p;
	}

	
	//new customer's vars

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public boolean isWithLifetime() {
		return withLifetime;
	}

	public void setWithLifetime(boolean withLifetime) {
		this.withLifetime = withLifetime;
	}

	public double getLifetime() {
		return lifetime;
	}

	public void setLifetime(double lifetime) {
		this.lifetime = lifetime;
	}

	public double getQueueingNodeInsertionTime() {
		return queueingNodeInsertionTime;
	}

	public void setQueueingNodeInsertionTime(double queueingNodeInsertionTime) {
		this.queueingNodeInsertionTime = queueingNodeInsertionTime;
	}

	public boolean isBadLack() {
		return badLack;
	}

	public void setBadLack(boolean badLack) {
		this.badLack = badLack;
	}

	public double getArriveForService() {
		return arriveForService;
	}

	public void setArriveForService(double arriveForService) {
		this.arriveForService = arriveForService;
	}

	public double getArriveForServiceOFF() {
		return arriveForServiceOFF;
	}

	public void setArriveForServiceOFF(double arriveForServiceOFF) {
		this.arriveForServiceOFF = arriveForServiceOFF;
	}

	public double getArriveForServiceON() {
		return arriveForServiceON;
	}

	public void setArriveForServiceON(double arriveForServiceON) {
		this.arriveForServiceON = arriveForServiceON;
	}

	public boolean isOff() {
		return off;
	}

	public void setOff(boolean off) {
		this.off = off;
	}
	
	//
	// Implement abstract method from Ordered. By default, the ordering
	// in based on the customer's priority.
	//
	public boolean smallerThan(Customer e) {
		return type <= e.getclass();
	}

}
