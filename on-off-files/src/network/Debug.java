package network ;

import tools.* ;

public class Debug {
  static boolean debug = false ;

  public static void setDebugOn() {
    debug = true ;
  }

  public static void setDebugOff() {
    debug = false ;
  }

  public static void dumpState() {
    if ( debug ) {
      System.out.println( "-----------------" ) ;
      for ( int i = 0 ; i < Network.nodeCount ; i++ ) {
        System.out.println( Network.nodes[ i ] ) ;
      }
      System.out.println( "-----------------" ) ;
    }
  }

  public static void traceMove( Customer c, Node to ) {
    if ( debug ) {
      System.out.println( "Time: " + Sim.now() + ", " + c +
                          " moving from " +
                          c.getLocation().name + " to " + to.name ) ;
    }
  }
 
  public static void trace( String s ) {
    if ( debug ) {
      System.out.println( s ) ;
    }
  }
}

