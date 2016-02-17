package examples;

public class AnalyticalModelsMM1 {
	public double meanResponseTime;
	public double networkCompletions;
	public double duration;
	public double Sft;
	
	public AnalyticalModelsMM1(double mD, double netC, double d, double sft) {
		meanResponseTime = mD;
		networkCompletions = netC;
		duration = d;
		Sft = sft;
	}

	public double computeL() {
		double L = 0;
		L = networkCompletions / duration;

		return L;
	}
	
	public double computeS() {
		double S = 0;
		double L = 0;
		
		L = computeL();		
		S = meanResponseTime / (1 + (meanResponseTime * L));
		
		return S;
	}
	
	public double computeR_Sft() {
		double R = 0;
		double L = computeL();

		R = Sft / (1 - (L * Sft));

		return R;
	}
	
	public double computeN() {
		double N = 0;
		double L = computeL();

		N = L * meanResponseTime;

		return N;
	}

}
