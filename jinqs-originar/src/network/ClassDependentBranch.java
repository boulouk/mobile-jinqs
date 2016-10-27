package network ;

import tools.* ;

public class ClassDependentBranch extends Link {
  int[] classes ;
  Node[] nodes ;

  public ClassDependentBranch( int[] classes, Node[] nodes ) {
    this.classes = classes ;
    this.nodes = nodes ;
  }

  private Node nextNode( Customer c ) {
    for ( int i = 0 ; i < classes.length ; i++ ) {
      if ( classes[i] == c.getclass() ) {
        return nodes[i] ;
      }
    }
    Check.check( false, "ClassDependentBranch - class lookup failed.\n" +
                        "Associated node: " + owner.getName() + "\n" +
                        "Offending customer class: " + c.getclass() ) ;
    return Network.nullNode ;
  }

  protected void move( Customer c ) {
    send( c, nextNode( c ) ) ;
  }
}

