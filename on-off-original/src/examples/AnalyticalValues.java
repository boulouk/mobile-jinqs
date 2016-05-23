/**
 * AnalyticalValues.java
 * Created on: 4 mai 2016
 */
package examples;

/**
 * @author Georgios Bouloukakis (boulouk@gmail.com)
 *
 */
public class AnalyticalValues {
	
	
	public static void main(String args[]) {
		
				System.out.println(new AnalyticalValues().computeR_ON_OFF(2, 1.5, 1.5, 0.0625));
				
				System.out.println(new AnalyticalValues().computeR_MM1(1, 0.0625));
		
	}
	
	public double computeR_MM1(double L, double Sft) {
		double R = 0;
				
		R = Sft / (1 - (L*Sft));
		
		return R;
		
	}
	
	public double computeR_ON_OFF(double L, double avgON, double avgOFF, double Sft) {
		double R = 0;
		
		double R1 = (Math.pow(avgOFF, 2) / (avgON + avgOFF)) + (Sft * ((avgON + avgOFF) / avgON));
		double R2 = 1 - (L * Sft * ((avgON + avgOFF) / avgON));
		
		R = R1 / R2;
		
		return R;
	}
	

}
