package examples;

import tools.* ;

class TickerSim extends Sim {

  // Example state variable
  int n = 0 ;

  // Example event
  class Tick extends Event {
    public Tick( double t ) {
      super( t ) ;
    }
    public void invoke() {
      n++ ;
      System.out.println( "Tick " + n + " at time " + now() ) ;
      schedule( new Tick( now() + 10.0 ) ) ;
    }
  }

  // Example termination function
  public boolean stop() {
    return now() > 100 ;
  }

  // Here, the constructor starts the simulation.
  public TickerSim() {
    schedule( new Tick( 0.0 ) ) ;
    simulate() ;
  }
}

class Ticker {
  // Main method simply invokes the constructor
  public static void main( String args[] ) {
    new TickerSim() ;
    new TickerSim() ;
  }

}

