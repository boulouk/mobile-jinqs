package tools ;

public class DiscreteSampler {
  double[] probs ;
  public DiscreteSampler( double[] probs ) {
    this.probs = probs ;
  }

  public int next() {
    double acc = probs[ 0 ] ;
    int index = 0 ;
    double r = Math.random() ;
    while ( acc < r ) {
      index++ ;
      acc += probs[ index ] ;
    }
    return index ;
  }
}
