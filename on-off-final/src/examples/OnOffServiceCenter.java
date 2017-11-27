/**
 * OnOffServiceCenter.java
 * Created on: 20 avr. 2016
 */
package examples;

import network.Delay;
import tools.Exp;

/**
 * @author Georgios Bouloukakis (boulouk@gmail.com)
 *
 */
public class OnOffServiceCenter {
	
	OnOffRQN mm1;
	ServerOnOff serverOnOff;
	
	public OnOffServiceCenter (String s, Delay d, int n, Exp on, Exp off, double dur) {
		
		mm1 = new OnOffRQN("MM1", d, n, on, off, dur);
		serverOnOff = new ServerOnOff(on, off, dur, mm1);
		
	}
	
	
	
	

}
