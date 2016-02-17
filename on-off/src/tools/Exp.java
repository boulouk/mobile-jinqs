package tools ;

import java.util.ArrayList;
import java.util.List;

public class Exp extends DistributionSampler {
  private double rate ;
  private List<Double> values = new ArrayList<Double>();

  public Exp( double r ) {
    rate = r ;
    values.add(r);
  }

  public double next() {
	  double next =  -Math.log( Math.random() ) / rate ;
	    values.add(next);
	    return next;
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
