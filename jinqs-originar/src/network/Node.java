package network ;

import tools.* ;

public class Node {
  int id ;
  String name ;
  Link link = Link.earth ;
  int arrivals = 0 ;

  public Node() {
    name = "Base Node" ;
    id = Network.add( this ) ;
  }

  public Node( String s ) {
    name = s ;
    id = Network.add( this ) ;
  }

  public String toString() {
    return name ;
  }

  public String getName() {
    return name ;
  }

  public int getId() {
    return id ;
  }

  public void setLink( Link r ) {
    link = r ;
    link.setOwner( this ) ;
  }
  
  public void resetMeasures() {
  }

  public void logResults() {
    Logger.logResult( name + " arrivals", arrivals ) ;
  }

  public void displayResults() {
    System.out.println( name + ":" ) ;
    System.out.println( "  Number of arrivals: " + arrivals ) ;
  }

// ----------------------------------------------------------------------

//
// Customers enter from the outside, are then processed by accept
// and then forwarded to the next node by forward.
// These methods can variously be overridden to effect different
// behaviour.
//
  public void enter( Customer c ) {
    Debug.trace( c + " entering " + this ) ;
    arrivals++ ;
    c.setLocation( this ) ;
    accept( c ) ;
  }

  protected void accept( Customer c ) {
    forward( c ) ;
  }

  protected void forward( Customer c ) {
    link.move( c ) ;
  }

}


