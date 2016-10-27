package network ;

import tools.* ;

//
// Random queue.  Customers are added at a random position
// in the queue.  Customers are removed from the head of the queue.
// Note: this is the same as random removal with insertion at the tail.
//
public class RandomQueue extends Queue {
  private List q = new List( "Random Queue" ) ;

  public RandomQueue() {
    super() ;
  }

  public RandomQueue( int cap ) {
    super( cap ) ;
  }
  
  protected void insertIntoQueue( Customer e ) {
    GeneralIterator it = q.getIterator() ;
    int index = (int)( Math.random() * ( pop + 1 ) ) ;
    for ( int i = 0 ; i < index ; i++ ) {
      it.advance() ;
    }
    it.add( e ) ;
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

}



