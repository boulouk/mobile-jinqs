package tools ;

import java.util.ArrayList;
import java.util.List;

// Only for integer values of beta, so Gamma( b, t ) is the same as
// Erlang( b, t ) - the code is for demonstration purposes only,
// showing the application of the rejection method and reinforcing
// the tutorial exercise on distribution sampling

public class Gamma extends DistributionSampler {
  private double m, b, theta, betatheta ;
  private int beta ;
  private int fact[] ;
  private GammaSampler gammaSampler ;
  private final double epsilon = 0.00001 ;
  private List<Double> values = new ArrayList<Double>();

  public Gamma( double theta, int beta ) {
    this.theta = theta ;
    this.beta = beta ;
    betatheta = beta * theta ;
    fact = new int[ beta ] ;
    int f = 1 ;
    for ( int i = 0 ; i < beta ; i++ ) {
      fact[ i ] = f ;
      f *= ( i + 1 ) ;
    }
    double y = 0.0 ;
    m = f( ( beta - 1 ) / betatheta ) ;
    double x = 1.0, xold = 2.0 ;
    while ( Math.abs( x - xold ) / x > epsilon ) {
      xold = x ;
      x = xold - ( bigF( xold ) - 0.999 ) / f( xold ) ;
    }
    b = x ;
    gammaSampler = new GammaSampler() ;
  }

  double bigF( double x ) {
    double acc = 0.0 ;
    for ( int i = 0 ; i < beta-1 ; i++ )
      acc += Math.pow( betatheta * x, (float) i ) / fact[ i ] ;
    return 1 - acc * Math.exp( -betatheta * x ) ;
  }

  double f( double x ) {
    return betatheta * Math.pow( betatheta * x, (float) beta - 1 ) *
           Math.exp( -betatheta * x ) / fact[ beta - 1 ] ;
  }

  public double next() {
  	double next = gammaSampler.next();
    values.add(next);
    return  next;
  	
  }

  class GammaSampler extends RejectionMethod {
    public GammaSampler() {
      super( 0, b, m ) ;
    }
    double density( double x ) {
      return f( x ) ;
    }
  }

  public static double gamma( double theta, int beta ) {
    Check.check( false, "Static method for gamma sampling not available\n" +
                         "Use Gamma class instead" ) ;
    return 0.0 ;
  } 
  
  public double average() {
	  double average = 0;
	  for (Double d : values) {
		  average += d;
	  }
	  return average / values.size();
  }
  
}

