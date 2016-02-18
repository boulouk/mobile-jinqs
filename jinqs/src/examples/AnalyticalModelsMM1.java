package examples;

public class AnalyticalModelsMM1 {
	public double meanResponseTime;
	public double networkCompletions;
	public double duration;
	public double Ssim;
	public double LambdaRate;
	public double serviceRate;
	
	public AnalyticalModelsMM1(double mD, double netC, double d, double Stsim, double LR, double SR) {
		meanResponseTime = mD;
		networkCompletions = netC;
		duration = d;
		Ssim = Stsim;
		LambdaRate = LR;
		serviceRate = SR;
	}
	
	public double computeRmodel() {
		double R = 0;
		double S = 1/serviceRate;
		R = S / (1 - (LambdaRate * S));

		return R;
	}
	
	public double computeQModel() {
		double N = 0;
		double R = computeRmodel();
		N = R * LambdaRate;

		return N;
	}
	
	public double computePModel() {
		double P = 0;
		P = LambdaRate/serviceRate;
		
		return P;
	}
	

	public double computeLsim() {
		double L = 0;
		L = networkCompletions / duration;

		return L;
	}
	
	
	
	public double computeRSimModel() {
		double R = 0;
		double L = computeLsim();

		R = Ssim / (1 - (L * Ssim));

		return R;
	}
	
	public double computeQsim() {
		double N = 0;
		double L = computeLsim();

		N = L * meanResponseTime;

		return N;
	}
	
	

}
