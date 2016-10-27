package tools ; 

import java.util.concurrent.Semaphore;

public class Resource {
  boolean resourceAvailable ;
  SystemMeasure resourceCount = new SystemMeasure() ;
  int resources ;
  int nresources ;
  protected static Semaphore semaphore ;
 
  public Resource() {
    resources = 1 ;
    nresources = 1 ;
  }

  public Resource( int n ) {
    resources = n ;
    semaphore = new Semaphore(resources);
    nresources = n ;
  }

  public void claim() {
    Check.check( resourceIsAvailable(),
                 "Attempt to claim unavailable resource" ) ;
    resources-- ;
    try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    resourceCount.add( (float)( nresources - resources ) ) ;
  }

  public void release() {
    Check.check( resources < nresources,
                 "Attempt to release non-existent resource" 
+ " " + resources + " " + nresources 
) ;
    resources++ ;
    semaphore.release();
    resourceCount.add( (float)( nresources - resources ) ) ;
  } 
  
  public static int getSemaphore() {
    return semaphore.availablePermits() ;
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


