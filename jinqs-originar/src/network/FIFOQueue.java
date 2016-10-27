package network ;

import tools.* ;

public class FIFOQueue extends Queue {
  private List q = new List( "FIFO Queue" ) ;

  public FIFOQueue() {
    super() ;
  }

  public FIFOQueue( int cap ) {
    super( cap ) ;
  }
  
  protected void insertIntoQueue( Customer e ) {
    q.insertAtBack( e ) ;
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



