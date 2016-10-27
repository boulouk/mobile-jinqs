package tools ; 

public class Resource {
  boolean resourceAvailable ;
  SystemMeasure resourceCount = new SystemMeasure() ;
  int resources ;
  int nresources ;
 
  public Resource() {
    resources = 1 ;
    nresources = 1 ;
  }

  public Resource( int n ) {
    resources = n ;
    nresources = n ;
  }

  public void claim() {
	 
    Check.check( resourceIsAvailable(),
                 "Attempt to claim unavailable resource" ) ;
    resources-- ;
//    System.out.println("claim resources = " + resources);
    resourceCount.add( (float)( nresources - resources ) ) ;
  }

  public void release() {
//	  System.out.println("before release resources = " + resources);
    Check.check( resources < nresources,
                 "Attempt to release non-existent resource" 
+ " " + resources + " " + nresources 
) ;
    resources++ ;
    resourceCount.add( (float)( nresources - resources ) ) ;
  } 

  public int numberOfAvailableResources() {
    return resources ;
  }

  public boolean resourceIsAvailable() {
    return resources > 0 ;
  } 

  public double utilisation() {
    return resourceCount.mean() / nresources ; 
  }

  public void resetMeasures() {
    resourceCount.resetMeasures() ;
  }

}


