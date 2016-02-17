package tools ;

//-----------------------------------------------------------------------------
//
//  An implementation of Lists with a usable list iterator
//  Implemented by Tony Field
//
//  Built on the example in Figure 22.3 in Deitel and Deitel's book on
//  Java2 - How to Program.
//
//-----------------------------------------------------------------------------


class ListNode {

  Object data;
  ListNode next;

  ListNode( Object o ) { this( o, null ); }

  ListNode( Object o, ListNode nextNode ) {
    data = o;
    next = nextNode;
  }

  Object getObject() { return data; }

  ListNode getNext() { return next; }
}

public class List {
  private ListNode firstNode;
  private ListNode lastNode;
  private String name ;

  public List( String s ) {
    name = s;
    firstNode = lastNode = null;
  }

  public List() { 
    this( "list" ); 
  }

  public Object first() {
    if ( isEmpty() )
      throw new EmptyListException( name );
    else
      return firstNode.data ;
  }

  public void insertAtFront( Object insertItem ) {
    if ( isEmpty() )
      firstNode = lastNode = new ListNode( insertItem );
    else
      firstNode = new ListNode( insertItem, firstNode );
  }

  public Object last() {
    if ( isEmpty() )
      throw new EmptyListException( name );
    else
      return lastNode.data ;
  }

  public void insertAtBack( Object insertItem ) {
    if ( isEmpty() )
      firstNode = lastNode = new ListNode( insertItem );
    else
      lastNode = lastNode.next = new ListNode( insertItem );
  }

  public void remove( Object o ) {
    ListIterator it = this.getIterator() ;
    while ( it.canAdvance() && !it.getValue().equals( o ) ) {
     it.advance() ;
    }
    if ( it.getValue().equals( o ) ) {
      it.remove() ;
    } else {
      System.out.println( "Error: attempt to remove non-existent object from list" ) ;
    }
  }

  public Object removeFromFront()
         throws EmptyListException {
    Object removeItem = null;

    if ( isEmpty() )
      throw new EmptyListException( name );

    removeItem = firstNode.data;

    if ( firstNode.equals( lastNode ) )
      firstNode = lastNode = null;
    else
      firstNode = firstNode.next;

    return removeItem;
  }

  public Object removeFromBack()
         throws EmptyListException {
    Object removeItem = null;

    if ( isEmpty() )
      throw new EmptyListException( name );

    removeItem = lastNode.data;

    if ( firstNode.equals( lastNode ) )
      firstNode = lastNode = null;
    else {
      ListNode current = firstNode;

      while ( current.next != lastNode )
         current = current.next;

      lastNode = current;
      current.next = null;
    }

    return removeItem;
  }

  public boolean isEmpty() {
    return firstNode == null; 
  }

  public void print() {
    if ( isEmpty() ) {
      System.out.println( "Empty " + name );
      return;
    }

    System.out.print( "The " + name + " is: " );

    ListNode current = firstNode;

    while ( current != null ) {
      System.out.println( current.data.toString() + " " );
      current = current.next;
    }

    System.out.println( "\n" );
  }

  public ListIterator getIterator() {
    return new ListIterator() ;
  }

  public class ListIterator implements GeneralIterator {
    private ListNode p, q ;

    public ListIterator() {
      p = null ;
      q = firstNode ;
    }

    public boolean canAdvance() {
      return !( q == null ) ;
    }

    public void advance() throws EmptyListException {
      if ( q == null )
        throw new EmptyListException( name ) ;
      p = q ;
      q = q.next ;
    }

    public Object getValue() throws EmptyListException {
      if ( q == null )
        throw new EmptyListException( name ) ;
      return q.data ;
    }

    public void replaceValue( Object d ) throws EmptyListException {
      if ( q == null )
        throw new EmptyListException( name ) ;
      q.data = d ;
    }

    public void add( Object o ) {
      ListNode newNode = new ListNode( o, q ) ;
      if ( ( p == null ) && ( q == null ) ) {
        firstNode = lastNode = newNode ;
        q = newNode ; }
      else
        if ( p == null ) {
          firstNode = newNode ;
          p = newNode ; }
        else {
          p.next = newNode ;
          p = newNode ;
          if ( q == null )
            lastNode = newNode ; }
    }

    public void remove() throws EmptyListException {
      if ( q == null )
        throw new EmptyListException( name ) ;
      if ( p == null ) 
       firstNode = q.next ;
      else
       p.next = q.next ;
      q = q.next ;
      if ( q == null )
       lastNode = p ;
    }
  }

}

