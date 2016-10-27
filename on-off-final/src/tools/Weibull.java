package tools ;

import java.util.ArrayList;
import java.util.List;

public class Weibull extends DistributionSampler {
  private double alpha, beta ;
  private List<Double> values = new ArrayList<Double>();

  public Weibull( double alpha, double beta ) {
    this.alpha = alpha ;
    this.beta = beta ;
  }

  public double next() {
  	
  	double next =  alpha * Math.pow( -Math.log( Math.random() ), 1.0 / beta );
    values.add(next);
    return  next;
  	
  }

  public static double weibull(  double alpha, double beta ) {
      return alpha * Math.pow( -Math.log( Math.random() ), 1.0 / beta ) ;
  }
  
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

