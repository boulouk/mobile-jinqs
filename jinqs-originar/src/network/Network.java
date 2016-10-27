package network ;
import tools.* ;

public class Network {
  static final int maxNodes = 1000 ;
  static final int maxClasses = 100 ;
  static final int maxPriorities = 100 ;
 
  static Node[] nodes = new Node[ maxNodes ] ;
  static int nodeCount = 0 ;
  public static int completions = 0 ;
  public static CustomerMeasure responseTime = new CustomerMeasure() ;
  static boolean initialised = false ;

  public static Node nullNode ;

  public static void initialise() {
    nodes = new Node[1000] ;
    nodeCount = 0 ;
    completions = 0 ;
    responseTime = new CustomerMeasure() ;
    initialised = true ;
    nullNode = new NullNode() ;
  }

  public static int add( Node n ) {
    Check.check( initialised, 
                 "Class Network has not been initialised.\n" +
                 "This may cause problems with replicated runs.\n" +
                 "Simulation aborting." ) ;
    nodes[ nodeCount++ ] = n ;
    return nodeCount-1 ;
  }

  public static void resetMeasures() {
    for ( int i = 0 ; i < nodeCount ; i++ ) {
      nodes[ i ].resetMeasures() ;
    }
  }

  public static void registerCompletion( double t ) {
    responseTime.add( t ) ;
  }

  public static void displayResults() {
    Logger.displayResults() ;
  }

  public static void displayResults( double alpha ) {
    Logger.displayResults( alpha ) ;
  }

  public static void logResult( String id, double result ) {
    Logger.logResult( id, result ) ;
  }

  public static void logResults() {
    Logger.logResult( "Completion time", Sim.now() ) ;
    Logger.logResult( "Completed customers", Network.completions ) ;
    Logger.logResult( "Mean time in network", responseTime.mean() ) ;
    Logger.logResult( "Variance of time in network", responseTime.variance() ) ;
    for ( int i = 0 ; i < nodeCount ; i++ ) {
      nodes[ i ].logResults() ;
    }
  }

}
