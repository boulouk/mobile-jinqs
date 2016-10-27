package tools ;

import java.util.ArrayList;
import java.util.List;

public class Pareto extends DistributionSampler {
  private double k, a, b, ak, bk ;
  private List<Double> values = new ArrayList<Double>();


  public Pareto( double k, double a, double b ) {
    this.k = k ;
    this.a = a ;
    this.b = b ;
    ak = Math.pow( a, -k ) ;
    bk = Math.pow( b, -k ) ;
  }

  public double next() {
  	
  	
  	double next =  Math.pow( ak - Math.random() * ( ak - bk ), -1/k );
    values.add(next);
    return  next;
  }

  public static double pareto( double k, double a, double b ) {
    double ak = Math.pow( a, -k ) ;
    double bk = Math.pow( b, -k ) ;
    return Math.pow( ak - Math.random() * ( ak - bk ), -1/k ) ;
  }
  
//modification
  public double average() {
	  double average = 0;
	  for (Double d : values) {
		  average += d;
	  }
	  return average / values.size();
  }

  public static double exp( double lam ) {
    return -Math.log( Math.random() ) / lam ;
  }
  
}
