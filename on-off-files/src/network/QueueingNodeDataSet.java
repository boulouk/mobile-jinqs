package network ;

import tools.* ;

public class QueueingNodeDataSet extends ResourcePoolDataSet {

  public QueueingNodeDataSet( String s, Delay d, int n, String distribType, double value, String an, int maxPop) {
    super( s, d, n, distribType,  value, an, maxPop) ;
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

