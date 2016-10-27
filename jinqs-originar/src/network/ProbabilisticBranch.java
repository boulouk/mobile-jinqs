package network ;

import tools.* ;

public class ProbabilisticBranch extends Link {
  DiscreteSampler dist ;
  Node[] nodes ;

  public ProbabilisticBranch( double[] probs, Node[] nodes ) {
    this.nodes = nodes ;
    dist = new DiscreteSampler( probs ) ;
  }

  public void move( Customer c ) {
    send( c, nodes[ dist.next() ] ) ;
  }
}

