package tools ;

public abstract class OrderedList extends List {

  public OrderedList() {
    super() ;
  }

  public OrderedList( String name ) {
    super( name ) ;
  }

  public abstract boolean before( Object x, Object y ) ;

  public void insertInOrder( Object o ) {
    ListIterator it = this.getIterator() ;
    while ( it.canAdvance() && before( it.getValue(), o ) ) {
      it.advance() ;
    }
    it.add( o ) ;
  }

}




