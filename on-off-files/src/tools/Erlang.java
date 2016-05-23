package tools ;

public class Erlang extends DistributionSampler {
  private int k ;
  private double theta ;
  private double acc ;

  public Erlang( int k, double theta ) {
    this.k = k ;
    this.theta = theta ;
  }

  public double next() {
    acc = 1.0 ;
    for ( int i = 1 ; i <= k ; i++ )
      acc *= Math.random() ;
    return -Math.log( acc ) / ( k * theta ) ;
  }

  public static double erlang( int k, double theta ) {
      double acc = 1.0 ;
      for ( int i = 1 ; i <= k ; i++ )
        acc *= Math.random() ;
      return -Math.log( acc ) / ( k * theta ) ;
  }
}


