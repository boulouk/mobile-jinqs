package network ;

import tools.* ;

public class QueueingNode extends ResourcePool {

  public QueueingNode( String s, Delay d, int n ) {
    super( s, d, n ) ;
  }

  public QueueingNode( String s, Delay d, int n, Queue q ) {
    super( s, d, n, q ) ;
  }

//
// The only difference between this and a resource pool is that 
// it releases a resource as soon as the customer leaves...
//
  protected void forward( Customer c ) {
    super.forward( c ) ;
    releaseResource() ;
  }

}

