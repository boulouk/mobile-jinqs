package tools ;

public class Pareto extends DistributionSampler {
  private double k, a, b, ak, bk ;

  public Pareto( double k, double a, double b ) {
    this.k = k ;
    this.a = a ;
    this.b = b ;
    ak = Math.pow( a, -k ) ;
    bk = Math.pow( b, -k ) ;
  }

  public double next() {
    return Math.pow( ak - Math.random() * ( ak - bk ), -1/k ) ;
  }

  public static double pareto( double k, double a, double b ) {
    double ak = Math.pow( a, -k ) ;
    double bk = Math.pow( b, -k ) ;
    return Math.pow( ak - Math.random() * ( ak - bk ), -1/k ) ;
  }
}
