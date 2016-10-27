package tools ;

public class Logger {
  static final int maxEntries = 500 ;
  static final int maxReplications = 21 ;
  static int logEntries = 0 ;
  static int[] entries = new int[ maxEntries ] ;
  static String[] ids = new String[ maxEntries ] ;
  static double[][] values = new double[ maxEntries ][ maxReplications ] ;
  
  static public void logResult( String id, double value ) {
    int i ;
    for ( i = 0 ; i < logEntries ; i++ ) {
      if ( ids[ i ].equals( id ) ) {
        break ;
      }
    }
    if ( i == logEntries ) {
      entries[ i ] = 1 ;
      ids[ i ] = id ;
      logEntries++ ;
    } else {
      entries[ i ] += 1 ;
    }
    values[ i ][ entries[ i ] - 1 ] = value ;
  }

  static double sampleMean( double[] xs, int n ) {
    double acc = 0.0 ;
    for ( int i = 0 ; i < n ; i++ ) {
      acc += xs[ i ] ;
    }
    return acc / n ;
  }

  static double sampleVariance( double[] xs, int n, double mean ) {
    double acc = 0.0 ;
    for ( int i = 0 ; i < n ; i++ ) {
      double diff = xs[ i ] - mean ;
      acc += diff * diff ;
    }
    return acc / ( n - 1 ) ;
  }

  static public void displayResults() {
    System.out.println( "\nConfidence level not supplied\n" ) ;
    display( 0.05, true ) ;
  }
    
  static public void displayResults( double alpha ) {
    if ( !StudentstTable.checkConfidenceLevel( alpha ) ) {
      System.out.println( "\nNOTE: no table data is available for the " +
                          "specified confidence level (" + alpha + ")" ) ;
      display( alpha, false ) ;
    } else {
      display( alpha, true ) ;
    }
  }

  static void display( double alpha, boolean alphaOK ) {
    if ( logEntries > 0 ) {
      double mean ;
      System.out.println( "\nSUMMARY OF STATISTICS\n" ) ;
      if ( alphaOK ) {
        System.out.println( "Confidence level: " + alpha*100 + "%\n" ) ;
      }
      for ( int i = 0 ; i < logEntries ; i++ ) {
        mean = sampleMean( values[ i ], entries[ i ] ) ;
        System.out.println( ids[ i ] ) ;
        System.out.println( "   Point estimate:  " + mean ) ;
        int n = entries[ i ] ;
        if ( n > 1 ) {
          System.out.println( "   Degrees of freedom: " + ( n-1 ) ) ;
        } 
        boolean dofOK = StudentstTable.checkDegreesOfFreedom( n - 1 ) ;
        if ( n > 1 && !dofOK ) {
          System.out.println( "No table data is available for the " +
                              "degrees of freedom (" + ( n-1 ) + ")" ) ;
        }
        if ( alphaOK && dofOK ) {
          double s = Math.sqrt( sampleVariance( values[ i ], n, mean ) ) ;
          double z = StudentstTable.table( n - 1, alpha ) ; 
          if ( z > 0.0 ) {
            System.out.println( "   C.I. half width: " +
                                z * s / Math.sqrt( (double)n ) ) ;
          }
        }
        System.out.println() ;
      }
    } else {
      System.out.println( "No results were logged" ) ;
    }
  }


}
