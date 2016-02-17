package network ;

import tools.* ;

public class LIFOQueue extends Queue {
  private List q = new List( "LIFO Queue" ) ;

  public LIFOQueue() {
    super() ;
  }

  public LIFOQueue( int cap ) {
    super( cap ) ;
  }
  
  protected void insertIntoQueue( Customer e ) {
    q.insertAtFront( e ) ;
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



