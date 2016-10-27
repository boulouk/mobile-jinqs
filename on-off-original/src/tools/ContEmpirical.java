package tools ;

public class ContEmpirical extends DistributionSampler {
  private double xs[], cs[] ;

  public ContEmpirical( double xs[], double fs[] ) {
    Check.check( xs.length == fs.length + 1 && fs.length > 0,
                  "Empirical distribution array mismatch" ) ;
    this.xs = new double[ xs.length ] ;
    this.xs = xs ;
    this.cs = new double[ xs.length ] ;
    double fTotal = 0.0 ;
    for ( int i = 0 ; i < fs.length ; i++ )
      fTotal += fs[ i ] ;
    cs[ 0 ] = 0 ;
    for ( int i = 0 ; i < fs.length ; i++ )
      cs[ i + 1 ] = cs[ i ] + fs[ i ] / fTotal ;
  }

  public double next() {
    double r = Math.random() ;
    int index = 0 ;
    while ( r >= cs[ index + 1 ] ) {
      index++ ;
    }
    return xs[ index ] +
           ( r - cs[ index ] ) / ( cs[ index + 1 ] - cs[ index ] ) *
           ( xs[ index + 1 ] - xs[ index ] ) ;
  }
}
