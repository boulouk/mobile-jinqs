package network ;

import tools.* ;

public class ClassDependentDelay extends Delay {
  int[] classes ;
  DistributionSampler[] samplers ;

  public ClassDependentDelay( int[] classes, 
                              DistributionSampler[] samplers ) {
    this.classes = classes ;
    this.samplers = samplers ;
  }

  protected double sample() {
    System.out.println( "ERROR - ClassDependentDelay (no customer argument)" ) ; 
    return 0.0 ;
  }

  protected double sample( Customer c ) {
    for ( int i = 0 ; i < classes.length ; i++ ) {
      if ( classes[i] == c.getclass() ) {
        return samplers[i].next() ;
      }
    }
    System.out.println( "ERROR - ClassDependentDelay class lookup failed" ) ;
    return 0.0 ;
  }

}

