package tools ;

public class Weibull extends DistributionSampler {
  private double alpha, beta ;

  public Weibull( double alpha, double beta ) {
    this.alpha = alpha ;
    this.beta = beta ;
  }

  public double next() {
    return alpha * Math.pow( -Math.log( Math.random() ), 1.0 / beta ) ;
  }

  public static double weibull(  double alpha, double beta ) {
      return alpha * Math.pow( -Math.log( Math.random() ), 1.0 / beta ) ;
  }
}

