package tools ; 

public class CustomerMeasure extends Measure {
 
  public CustomerMeasure() {
    super() ;
  } ;

  public CustomerMeasure( int m ) {
    super( m ) ;
  } ;

  public void add( double x ) {
    for ( int i = 1 ; i <= moments ; i++ )
      moment[ i ] += Math.pow( x, (double) i ) ;
    n += 1 ;
  }

  public double mean() {
    return moment[1] / n ;
  }

  public double variance() {
    double mean = this.mean() ;
    return ( moment[2] - n * mean * mean ) / ( n - 1 ) ;
  }

}
