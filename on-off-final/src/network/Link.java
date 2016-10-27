package network ;

public class Link {
  Node n ;
  Node owner ;
  public static Earth earth = new Earth() ;

  public Link() {
    this.n = Network.nullNode ;
  }

  public Link( Node n ) {
    this.n = n ;
  }

  public void setOwner( Node n ) {
    owner = n ;
  }

  public Node getOwner() {
    return owner ;
  }

  //
  // Can be called by subclasses that have more than one target
  // node
  //
  protected void send( Customer c, Node n ) {
    Debug.traceMove( c, n ) ;
    Network.nodes[ n.id ].enter( c ) ;
  }

  //
  // Can be overridden in subclasses that have more than one target
  // node
  //
  protected void move( Customer c ) {
    send( c, n ) ;
  }

  static class Earth extends Link {
    public void move( Customer c ) {
      send( c, Network.nullNode ) ;
  }

}


}

