package tools ; 

public class SystemMeasure extends Measure {
  private double lastChange = 0.0 ;
  private double current = 0.0 ;

  public SystemMeasure() {
    super() ;
  } ;
      
  public SystemMeasure( int m ) {
    super( m ) ;
  } ;
      
  public void add( double x ) {
    for ( int i = 1 ; i <= moments ; i++ )
      moment[ i ] += ( Math.pow( current, (double) i ) ) * 
                      ( Sim.now() - lastChange ) ;
    current = x ;
    lastChange = Sim.now() ;
    n++ ;
  } 

  public double mean() {
    return moment[1] / ( Sim.now() - resetTime ) ;
  }

  public double variance() {
    double mean = this.mean() ;
    return moment[2] / ( Sim.now() - resetTime ) - mean * mean ;
  }

  public double currentValue() {
    return current ;
  }

  public double timeLastChanged() {
    return lastChange ;
  }
      
}   
