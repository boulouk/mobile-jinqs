package tools ;

public class Normal extends DistributionSampler {
  private static final double twoPI = 2 * Math.PI ;

  private double mu, sigma, r1, r2, k ;
  private boolean mustRedo = false ;

  public Normal( double mu, double sigma ) {
    this.mu = mu ;
    this.sigma = sigma ;
  }

  public double next() {
    mustRedo = !mustRedo ;
    if ( mustRedo ) {
      r1 = Math.random() ;
      r2 = Math.random() ;
      k = Math.sqrt( -2 * Math.log( r1 ) ) ;
      return k * Math.cos( twoPI * r2 ) * sigma + mu ;
    }
    else
      return k * Math.sin( twoPI * r2 ) * sigma + mu ;
  }

  public static double normal(  double m, double s ) {
    double r1 = Math.random() ;
    double r2 = Math.random() ;
    double k  = Math.sqrt( -2 * Math.log( r1 ) ) ;
    return k * Math.cos( twoPI * r2 ) * s + m ;
  }
}

