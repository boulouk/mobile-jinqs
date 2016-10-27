package tools ; 

public class CopyOfResource {
  boolean resourceAvailable ;
  SystemMeasure resourceCount = new SystemMeasure() ;
  protected static transient int resources ;
  int nresources ;
 
  public CopyOfResource() {
    resources = 1 ;
    nresources = 1 ;
  }

  public CopyOfResource( int n ) {
    resources = n ;
    nresources = n ;
  }
  

  public synchronized void claim() {
    Check.check( resourceIsAvailable(),
                 "Attempt to claim unavailable resource" ) ;
    resources-- ;
    resourceCount.add( (float)( nresources - resources ) ) ;
  }

  public synchronized void release() {
    Check.check( resources < nresources,
                 "Attempt to release non-existent resource" 
+ " " + resources + " " + nresources 
) ;
    resources++ ;
    resourceCount.add( (float)( nresources - resources ) ) ;
  } 

  public synchronized static int numberOfAvailableResources() {
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


