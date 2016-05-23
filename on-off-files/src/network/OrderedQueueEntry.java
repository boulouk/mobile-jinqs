package network ;

import tools.* ;

/**
 * Some queues may be built using other queues (e.g. a priority
 * queue).  It is therefore convenient for queue entries to be
 * customers themselves. An outer enqueue can then pass its
 * argument directly to the inner queue, and so on.
 * To simplify the definition of ordered queues, it is also
 * useful for a default ordering to be based on time.  For example, 
 * in a PS queue each queue entry will be paired up with a finish time.
 * It is then important to order the queue entries by finish time.
*/
class OrderedQueueEntry extends Customer implements Ordered {
  double time ;
  public Customer entry ;

  public OrderedQueueEntry( Customer c, double t ) {
    time = t ;
    entry = c ;
  }

  public boolean smallerThan( Customer e ) {
    return ((OrderedQueueEntry)this).time <= ((OrderedQueueEntry)e).time ;
  }
}


