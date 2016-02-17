package network ;

import tools.* ;

public class OrderedQueue extends Queue {
  private OrderedCustomerList q = new OrderedCustomerList() ;

  public OrderedQueue() {
    super() ;
  }

  public OrderedQueue( int cap ) {
    super( cap ) ;
  }
  
  protected void insertIntoQueue( Customer e ) {
    q.insertInOrder( e ) ;
  }

  protected void insertAtHeadOfQueue( Customer e ) {
    q.insertAtFront( e ) ;
  }

  protected Customer headOfQueue() {
    return (Customer)q.first() ;
  }

  protected Customer removeFromQueue() {
    return (Customer)q.removeFromFront() ;
  }

// 
// The items inserted into the queue above comprise a customer
// and an insertion time.  The customer must implement the
// Ordered interface. The before method in OrderedList is
// given two queue entries.  It extracts the customer fields and
// then applies the ordering (smallerThan).  The ordering can be
// based on any attribute.
//
  class OrderedCustomerList extends OrderedList {
    public boolean before( Object x, Object y ) {
      //Ordered x1 = (Ordered)((Customer)x) ;
      return ((Customer)x).smallerThan( (Customer)y ) ;
    }
  }

}



