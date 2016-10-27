package network ;

import tools.* ;

public class Delay {
  DistributionSampler sampler ;

  public Delay() {
  }

  public Delay( DistributionSampler s ) {
    sampler = s ;
  }

  protected double sample() {
    return sampler.next() ;
  }

  protected double sample( Customer c ) {
    return sampler.next() ;
  }

}

