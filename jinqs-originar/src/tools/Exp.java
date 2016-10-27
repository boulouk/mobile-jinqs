package tools ;

public class Exp extends DistributionSampler {
  private double rate ;

  public Exp( double r ) {
    rate = r ;
  }

  public double next() {
    return -Math.log( Math.random() ) / rate ;
  }

  public static double exp( double lam ) {
    return -Math.log( Math.random() ) / lam ;
  }
}
