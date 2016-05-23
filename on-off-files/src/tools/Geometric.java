package tools ;

public class Geometric extends DistributionSampler {
  private double q ;

  public Geometric( double p ) {
    q = Math.log( 1 - p ) ;
  }

  public double next() {
    return (int) ( Math.log( Math.random() ) / q ) ;
  }

  public static double geometric( double p ) {
    return (int) ( Math.log( Math.random() ) / Math.log( 1 - p ) ) ;
  }
}
