package tools ;

public abstract class Sim {

  static Diary diary ;
  static double vtime ;

  public Sim() {
    diary = new Diary() ;
    vtime = 0.0 ;
  }

  public static double now() {
    return vtime ;
  }
  
  public static void schedule( Event e ) {
    diary.insertInOrder( e ) ;
  }

  public static void deschedule( Event e ) {
    diary.remove( e ) ;
  }

  public void simulate() {
    go() ;
  }

  public void simulate( double t ) {
    schedule( new EndOfWarmUp( now() + t ) ) ;
    go() ;
  }

  public void go() {
    while ( !diary.isEmpty() && !stop() ) {
      Event e = (Event) diary.removeFromFront() ;
      vtime = e.invokeTime ;
      if ( !stop() )
        e.invoke() ;
    }
  }

  public void resetMeasures() {
    System.out.println( "WARNING: resetMeasures() has not been overridden" ) ;
  }

  public abstract boolean stop() ;

  class EndOfWarmUp extends Event {
    public EndOfWarmUp( double t ) {
      super( t ) ;
    }
    public void invoke() {
      resetMeasures() ;
    }
  }

}

  







