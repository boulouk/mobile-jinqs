package tools ;

//
// A more useful list iteration mechanism than is provided in the
// standard Java platform.  It allows straightforward implementation
// of alternative insertion/removal schemes, e.g. ordered, random etc.
//
public interface GeneralIterator {
  public boolean canAdvance() ;

  public void advance() throws EmptyListException ;
  
  public Object getValue() throws EmptyListException ;

  public void replaceValue( Object d ) throws EmptyListException ;

  public void add( Object o ) ;
 
  public void remove() throws EmptyListException ;
}
