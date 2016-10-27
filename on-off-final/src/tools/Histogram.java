package tools ;

import java.text.DecimalFormat ;

public class Histogram extends CustomerMeasure {
  private int bucket[] ;
  private double low, high, width ;
  private int n ;
  private int underflows, overflows = 0 ;

  public Histogram( double l, double h, int b ) {
    super() ;
    bucket = new int [b] ;
    low = l ;
    high = h ;
    n = b ;
    width = ( high - low ) / n ;
  }

  public void add( double x ) {
    super.add( x ) ;
    if ( x < low ) 
      underflows++ ;
    else if ( x >= high )
      overflows++ ;
    else {
      int index = (int)( ( x - low ) / width ) ;
      bucket[ index ]++ ;
    }
  }

  public int bucketContent( int i ) {
    return bucket[ i ] ;
  }

  public void display() {
    DecimalFormat decimal = new DecimalFormat( "000000.000" ) ;
    DecimalFormat integer = new DecimalFormat( "0000000" ) ;
    DecimalFormat general = new DecimalFormat( "######.###" ) ;
    final int maxHeight = 20 ;
    System.out.println( "\nObservations = " + super.count() + 
                        "   Mean = " + general.format( super.mean() ) +
                        "   Variance = " + general.format( super.variance() ) ) ;
    int max = 0 ;
    for ( int i = 0 ; i < n ; i++ ) 
      if ( bucket[i] > max )
        max = bucket[i] ;
    if ( max == 0 ) 
      System.out.print( "Histogram is empty\n" ) ;
    else {
      for ( int i = 0 ; i < n ; i++ ) {
        System.out.print( decimal.format( low + i * width ) + " - " +
                          decimal.format( low + ( i + 1 ) * width ) + "   " +
                          integer.format( (double) bucket[i] ) + "  |" ) ;
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
