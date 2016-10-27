package tools ; 

public abstract class Measure {
  private final int maxMeasures = 10 ;
  protected int moments, n = 0 ;
  protected double moment[] = new double[ 100 ] ;
  protected double resetTime = 0.0 ;

  public Measure() {
    moments = 2 ;
  }
 
  public Measure( int m ) {
    if ( m > maxMeasures )
      moments = maxMeasures ;
    else if ( m < 2 ) 
      moments = 2 ;
    else
      moments = m ;
  } ;

//------------------------------------------------

  public abstract void add( double x ) ;

  public int count() {
    return n ;
  }

  public abstract double mean() ;

  public abstract double variance() ;

  public double moment( int n ) {
    return moment[ n ] ;
  }

  public void resetMeasures() {
    resetTime = Sim.now() ;
    n = 0 ;
    for ( int i = 1 ; i <= moments ; i++ )
      moment[ i ] = 0.0 ;
  }

}
