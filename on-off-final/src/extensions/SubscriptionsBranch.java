package extensions ;

import java.util.ArrayList;

import network.Customer;
import network.Link;
import network.Node;

public class SubscriptionsBranch extends Link {
  Node[] nodes ;
  ArrayList<Integer> subslist;

  /*
   * @nodes: Node1: the node the expired messages will be sent
   * 		 Node2: node that the non-expired messages continue to the network
   * 
   */
  public SubscriptionsBranch(ArrayList<Integer> subsl, Node[] nodes) {
    this.nodes = nodes ;
    this.subslist = subsl;
  }
 
  public void move(Customer c) {
	  if(!subslist.contains(c.getclass()))
		  send( c, nodes[0]) ;  
	  else
		  send( c, nodes[1]) ; 
  }
}

