package network ;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import tools.* ;

public abstract class Queue {
  protected int pop = 0 ;
  private CustomerMeasure queueingTime = new CustomerMeasure() ;
  private SystemMeasure popMeasure = new SystemMeasure() ;
  private int capacity ;

  public Queue() {
    capacity = Integer.MAX_VALUE ;
  }
 
  public Queue( int cap ) {
    capacity = cap ;
  }

  public int getCapacity() {
    return capacity ;
  }

  public boolean isInfinite() {
    return ( capacity == Integer.MAX_VALUE ) ;
  }

  public boolean isEmpty() {
    return ( pop == 0 ) ;
  }

  public boolean canAccept( Customer c ) {
    return pop < capacity ;
  }

  public int queueLength() {
    return pop ;
  }

  public void enqueue( Customer c ) {
    Check.check( canAccept( c ), "Attempt to add to a full queue" ) ;
    c.setQueueInsertionTime( Sim.now() ) ;
    insertIntoQueue( c ) ;
    pop++ ;
    popMeasure.add( (float)pop ) ;
  }

//
// The check isn't necessary as this can only be called after preemption
// i.e. after an arrival; the arrival will have checked the queue
// for spare capacity
//
  public void enqueueAtHead( Customer c ) {
    Check.check( canAccept( c ), "Attempt to add to a full queue" ) ;
    c.setQueueInsertionTime( Sim.now() ) ;
    insertAtHeadOfQueue( c ) ;
    pop++ ;
    popMeasure.add( (float)pop ) ;
  }

  public Customer head() {
    Check.check( pop > 0, "Attempt to take the head of an empty queue" ) ;  
    Customer c = headOfQueue() ;
    return c ;
  }

  public Customer dequeue() {
    Check.check( pop > 0, "Attempt to dequeue an empty queue!" ) ;
    Customer c = removeFromQueue() ;
    pop-- ;
    popMeasure.add( (float)pop ) ;
    queueingTime.add( Sim.now() - c.getQueueInsertionTime() ) ;
    return c ;
  }

/**
 * These abstract methods allow different queueing disciplines
 * to be supported - see the various subclasses
*/
 

  protected abstract void insertIntoQueue( Customer o ) ;

  protected abstract void insertAtHeadOfQueue( Customer o ) ;

  protected abstract Customer headOfQueue() ;

  protected abstract Customer removeFromQueue() ; 

/**
 * Generic measures
*/

  public double meanQueueLength() {
    return popMeasure.mean() ;
  }

  public double varQueueLength() {
    return popMeasure.variance() ;
  }

  public double meanTimeInQueue() {
    return queueingTime.mean() ;
  }

  public double varTimeInQueue() {
    return queueingTime.variance() ;
  }

  public void resetMeasures() {
    queueingTime.resetMeasures() ;
    popMeasure.resetMeasures() ;
  }

}
