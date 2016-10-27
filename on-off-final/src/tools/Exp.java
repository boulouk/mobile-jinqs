package tools ;

import java.util.ArrayList;
import java.util.List;

public class Exp extends DistributionSampler {
  private double rate ;
  private List<Double> values = new ArrayList<Double>();
  //private StringBuilder arrivals = null;
  
  


//public StringBuilder getArrivals() {
//	return arrivals;
//}

public Exp( double r ) {
    rate = r ;
    //this.arrivals = new StringBuilder();
    values.add(r);
  }

  public double next() {
	  double next =  -Math.log( Math.random() ) / rate ;
	    values.add(next);
	    //arrivals.append(next);
	    //arrivals.append("\n");
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
