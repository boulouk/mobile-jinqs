package tools ;

interface DiaryInterface {
  public void insertInOrder( Event e ) ;

  public void remove( Event e ) ;

  public Event removeFromFront() ;
}
