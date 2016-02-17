package tools ;

//
// Generic code for the (acceptance-)rejection method...
//

abstract class RejectionMethod {
  private double a, b, m ;

  public RejectionMethod( double a, double b, double m ) {
    this.a = a ;
    this.b = b ;
    this.m = m ;
  }

  abstract double density( double x ) ;

  public double next() {
    double x = Uniform.uniform( a, b ) ;
    double y = Uniform.uniform( 0, m ) ;
    int nrej = 0 ;
    while ( y > density( x ) ) {
       x = Uniform.uniform( a, b ) ;
       y = Uniform.uniform( 0, m ) ;
       nrej++ ;
    }
    return x ;
  }
}

