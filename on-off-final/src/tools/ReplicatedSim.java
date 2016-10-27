package tools ;

public abstract class ReplicatedSim {

  public ReplicatedSim( int n, double a ) {
    System.out.print( "Progress (" + n + " runs): " ) ;
    for ( int run = 0 ; run < n ; run++ ) {
      runSimulation() ;
      System.out.print( ( run + 1 ) + " " ) ;
    }
    System.out.println() ;
    Logger.displayResults( a ) ;
  }

  public abstract void runSimulation() ;

}
