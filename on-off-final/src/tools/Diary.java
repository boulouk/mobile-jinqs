package tools ;

public class Diary extends OrderedList implements DiaryInterface {
  public boolean before( Object x, Object y ) {
    return ( ((Event)x).invokeTime <= ((Event)y).invokeTime ) ;
  }

  public void insertInOrder( Event e ) {
    super.insertInOrder( e ) ;
  }

  public void remove( Event e ) {
    super.remove( e ) ;
  }

  public Event removeFromFront() {
    Event e = (Event)super.removeFromFront() ;
    return e ;
  }
}


