package examples;

import tools.* ;

class MM1Simple extends Sim {
  Resource server = new Resource() ;
  int pop = 0 ;

  class Arrival extends Event {
    public Arrival( double t ) {
      super( t ) ;
    }
    public void invoke() {
      schedule( new Arrival( now() + Exp.exp( 2 ) ) ) ;
      pop++ ;
      if ( server.resourceIsAvailable() ) {
        server.claim() ;
        schedule( new Departure( now() + Exp.exp( 4 ) ) ) ;
      }
    }
  }

  class Departure extends Event {
    public Departure( double t ) {
      super( t ) ;
    }
    public void invoke() {
      pop-- ;
      if ( pop > 0 ) 
        schedule( new Departure( now() + Exp.exp( 4 ) ) ) ;
      else
        server.release() ;
    }
  }

  public void resetMeasures() {
    server.resetMeasures() ;
  }

  public boolean stop() {
    return now() > 100000 ;
  }

  public MM1Simple() {
    schedule( new Arrival( now() + Exp.exp( 2 ) ) ) ;
    simulate( 10000 ) ;
    Logger.logResult( "Utilisation", server.utilisation() ) ;
  }

  public static void main( String args[] ) {
    new MM1Simple() ;
    new MM1Simple() ;
    new MM1Simple() ;
    Logger.displayResults( 0.01 ) ;
  }
}


