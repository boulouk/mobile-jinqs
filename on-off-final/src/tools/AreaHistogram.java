package tools ; 

import java.text.DecimalFormat ;

public class AreaHistogram extends SystemMeasure {
  private double bucket[] ;
  private double low, high, width ;
  private int n ;
  private int underflows, overflows = 0 ;

  public AreaHistogram( double l, double h, int b ) {
    super() ;
    bucket = new double [b] ;
    low = l ;
    high = h ;
    n = b ;
    width = ( high - low ) / n ;
  }

  public void add( double t ) {
    double current = super.currentValue() ;
    double lastChange = super.timeLastChanged() ;
    if ( current < low ) 
      underflows++ ;
    else if ( current >= high )
      overflows++ ;
    else {
      int index = (int)( ( current - low ) / width ) ;
      bucket[ index ] += Sim.vtime - lastChange ;
    }
    super.add( t ) ;
  }

  public double bucketContent( int i ) {
    return bucket[ i ] ;
  }

  public void display() {
    DecimalFormat decimal = new DecimalFormat( "000000.00" ) ;
    DecimalFormat area    = new DecimalFormat( "0000000.00" ) ;
    DecimalFormat general = new DecimalFormat( "######.##" ) ;
    final int maxHeight = 20 ;
    System.out.println( "\nNo. of state changes = " + super.count() + 
                        "   Mean = " + general.format( super.mean() ) +
                        "   Variance = " + general.format( super.variance() ) ) ;
    double max = 0 ;
    for ( int i = 0 ; i < n ; i++ ) 
      if ( bucket[i] > max )
        max = bucket[i] ;
    if ( max == 0 ) 
      System.out.print( "Histogram is empty\n" ) ;
    else {
      for ( int i = 0 ; i < n ; i++ ) {
        System.out.print( decimal.format( i * width ) + " - " +
                          decimal.format( ( i + 1 ) * width ) + "   " +
                          area.format( (double) bucket[i] ) + "  |" ) ;
        String stars = "" ;
        for ( int j = 0 ; j < (double) bucket[i] / max * maxHeight ; j++ )
          stars += "*" ;
        System.out.print( stars + "\n" ) ;
      }
      System.out.print( "Underflows = " + underflows +
                        "   Overflows = " + overflows + "\n" ) ;
    }
  }    

}
