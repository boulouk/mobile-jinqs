package tools ;

public class Uniform extends DistributionSampler {
  private double a, b ;

  public Uniform( double a, double b ) {
    this.a = a ;
    this.b = b ;
  }

  public double next() {
    return Math.random() * ( b - a ) + a ;
  }

  public static double uniform(  double a, double b ) {
    return Math.random() * ( b - a ) + a ;
  }
}

