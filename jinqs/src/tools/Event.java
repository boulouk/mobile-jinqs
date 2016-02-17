package tools ;

public abstract class Event {
  static int nextId = 0 ;
  double invokeTime ;
  int id = nextId++ ;

  public Event( double time ) {
    invokeTime = time ;
  }

  public double invokeTime() {
    return invokeTime ;
  }

  public abstract void invoke() ;

}


