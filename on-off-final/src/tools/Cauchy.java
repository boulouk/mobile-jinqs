package tools ;

public class Cauchy extends DistributionSampler {
  private double alpha, beta ;
  private Normal norm = new Normal( 0, 1 ) ;

  public Cauchy( double a, double b ) {
    alpha = a ;
    beta = b ;
  }

  public double next() {
    return ( norm.next() / norm.next() ) * beta + alpha ;
  }

  public static double cauchy( double a, double b ) {
    return ( Normal.normal( 0, 1 ) / Normal.normal( 0, 1 ) ) * b + a ;
  }
}
