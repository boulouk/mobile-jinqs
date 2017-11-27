package extensions ;

import network.Customer;
import network.Link;
import network.Node;

public class MdwLsesBranch extends Link {
  Node[] nodes ;

  /*
   * @nodes: Node1: the node the expired messages will be sent
   * 		 Node2: node that the non-expired messages continue to the network
   * 
   */
  public MdwLsesBranch(Node[] nodes) {
    this.nodes = nodes ;
  }
 
  public void move(Customer c) {
	  if(c.isMwdExpiration())
		  send( c, nodes[0]) ;  
	  else
		  send( c, nodes[1]) ;
    
  }
}

