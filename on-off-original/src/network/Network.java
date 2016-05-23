package network ;
import tools.* ;

public class Network {
  static final int maxNodes = 1000 ;
  static final int maxClasses = 100 ;
  static final int maxPriorities = 100 ;
 
  static Node[] nodes = new Node[ maxNodes ] ;
  static int nodeCount = 0 ;
  public static int completions = 0;
  public static int completionsON = 0;
  public static int completionsOFF = 0;
  
  public static CustomerMeasure responseTime = new CustomerMeasure();
  public static CustomerMeasure responseTimeON = new CustomerMeasure();
  public static CustomerMeasure responseTimeOFF = new CustomerMeasure();
  
  public static CustomerMeasure virtualServiceTime = new CustomerMeasure();
  public static CustomerMeasure serviceTimeON = new CustomerMeasure();
  public static CustomerMeasure serviceTimeOFF = new CustomerMeasure();
  
  public static CustomerMeasure serviceTimeOFFServiceTime = new CustomerMeasure();
  
  static boolean initialised = false ;

  public static Node nullNode ;

  public static void initialise() {
    nodes = new Node[1000] ;
    nodeCount = 0 ;
    completions = 0 ;
    completionsON = 0;
    completionsOFF = 0;
    responseTime = new CustomerMeasure();
    responseTimeON = new CustomerMeasure();
    responseTimeOFF = new CustomerMeasure();
    virtualServiceTime = new CustomerMeasure();
    serviceTimeON = new CustomerMeasure();
    serviceTimeOFF = new CustomerMeasure();
    serviceTimeOFFServiceTime = new CustomerMeasure();
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
  
  public static void registerCompletionON( double t ) {
	    responseTimeON.add( t ) ;
  }
  
  public static void registerCompletionOFF( double t ) {
	    responseTimeOFF.add( t ) ;
	  }
  
  public static void computeVirtualST ( double t ) {
	  virtualServiceTime.add( t ) ;
  }
  
  public static void computeSTON ( double t ) {
	  serviceTimeON.add( t ) ;
  }
  
  public static void computeSTOFF ( double t ) {
	  serviceTimeOFF.add( t ) ;
  }
  
  public static void computeSTOFFServiceTime ( double t ) {
	  serviceTimeOFFServiceTime.add( t ) ;
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
//    Logger.logResult( "Completion time", Sim.now() );
//    Logger.logResult( "Completed customers", Network.completions );
//    Logger.logResult( "Completed customers ON", Network.completionsON );
//    Logger.logResult( "Completed customers OFF", Network.completionsOFF );
//    
//    Logger.logResult( "Variance of time in network", responseTime.variance() );
//    Logger.logResult( "Mean time in network", responseTime.mean() );
//    Logger.logResult( "Mean time in network ON", responseTimeON.mean() );
//    Logger.logResult( "Mean time in network OFF", responseTimeOFF.mean() );
//    
//    Logger.logResult( "Virtual Service Time", virtualServiceTime.mean() );
//    Logger.logResult( "Service Time ON", serviceTimeON.mean() );
//    Logger.logResult( "Service Time OFF", serviceTimeOFF.mean() );
    
    for ( int i = 0 ; i < nodeCount ; i++ ) {
//      nodes[ i ].logResults() ;
      nodes[ i ].displayResults();
    }
  }

}
