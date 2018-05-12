package network ;
import java.util.HashMap;
import java.util.Map;

import tools.* ;

public class Network {
  static final int maxNodes = 1000 ;
  static final int maxClasses = 1000 ;
  static final int maxPriorities = 100 ;
 
  static Node[] nodes = new Node[ maxNodes ] ;
  static int nodeCount = 0 ;
  public static int completions = 0;
  
  public static int dropPackets = 0;
  public static int dropPrioPackets = 0;
  public static int eventsDrop = 0;
  
  public static int completionsON = 0;
  public static int completionsOFF = 0;
  
  public static int completionsExpired = 0;
  
  public static CustomerMeasure responseTime = new CustomerMeasure();
  public static CustomerMeasure responseTimeON = new CustomerMeasure();
  public static CustomerMeasure responseTimeOFF = new CustomerMeasure();
  
  public static CustomerMeasure responseTimeExpired = new CustomerMeasure();
  
  public static Map<Integer,CustomerMeasure> responseTimePrioMap = new HashMap<Integer,CustomerMeasure>();
  public static Map<Integer,CustomerMeasure> responseTimeClassMap = new HashMap<Integer,CustomerMeasure>();
  
  public static Map<Integer,Integer> completionsPrioMap = new HashMap<Integer,Integer>();
  public static Map<Integer,Integer> completionsClassMap = new HashMap<Integer,Integer>();
  public static Map<Integer,Integer> dropsClassMap = new HashMap<Integer,Integer>();
  public static Map<Integer,Integer> dropPrioClassMap = new HashMap<Integer,Integer>();

  public static Map<Integer,Integer> dropsBufferClassMap = new HashMap<Integer,Integer>();
  
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
    dropPackets = 0;
    eventsDrop = 0;
    completionsON = 0;
    completionsOFF = 0;
    responseTime = new CustomerMeasure();
    responseTimeON = new CustomerMeasure();
    responseTimeOFF = new CustomerMeasure();
    
    responseTimePrioMap = new HashMap<Integer,CustomerMeasure>();
    responseTimeClassMap = new HashMap<Integer,CustomerMeasure>();
    
    completionsPrioMap = new HashMap<Integer,Integer>();
    completionsClassMap = new HashMap<Integer,Integer>();
    dropsClassMap = new HashMap<Integer,Integer>();
    dropPrioClassMap = new HashMap<Integer,Integer>();
    
    dropsBufferClassMap = new HashMap<Integer,Integer>();
    
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
    responseTime.addResponseTime(t);
    responseTime.addTimestamp(Sim.now());
  }
  
  public static void registerCompletionExpired(double t) {
		responseTimeExpired.add(t);
  }
  
  public static void registerCompletionON( double t ) {
	    responseTimeON.add( t ) ;
  }
  
  public static void registerCompletionOFF( double t ) {
	    responseTimeOFF.add( t ) ;
  }
  
  public static void registerPrio( double t, int priority ) {
	  if(responseTimePrioMap.get(priority) == null) {
		  CustomerMeasure responseTimePrio = new CustomerMeasure();
		  responseTimePrioMap.put(priority, responseTimePrio);
		  responseTimePrioMap.get(priority).add(t);	  
	  } else 
		  responseTimePrioMap.get(priority).add(t);
  }
  
  public static void registerClass(double t, int typeClass) {
	  if(responseTimeClassMap.get(typeClass) == null) {
		  CustomerMeasure responseTimeClass = new CustomerMeasure();
		  responseTimeClassMap.put(typeClass, responseTimeClass);
		  responseTimeClassMap.get(typeClass).add(t);	  
	  } else 
		  responseTimeClassMap.get(typeClass).add(t);
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
      nodes[ i ].logResults() ;
      nodes[ i ].displayResults();
    }
  }

}
