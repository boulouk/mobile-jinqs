package examples;

import javax.annotation.processing.Completions;

public class AnalyticalModelsONOFF {
	public double meanResponseTime;
	public double meanResponseTimeOn;
	public double meanResponseTimeOff;
	public double networkCompletions;
	public double networkCompletionsOn;
	public double networkCompletionsOff;
	public double duration;
	public double Sft;
	public double virtualServiceTime;
	public double virtualServiceTimeVariance;
	public double stOn;
	public double stOff;
	public double durationON;
	public double durationOFF;
	public double avgON;
	public double avgOFF;

	public AnalyticalModelsONOFF(double mD, double mDOn, double mDOff, double netC, double netCOn, double netCOff, double d, double sft, double vst, double vstVar, double ston, double stoff, double dOn, double dOoff, double aOn, double aOff) {
		meanResponseTime = mD;
		meanResponseTimeOn = mDOn;
		meanResponseTimeOff = mDOff;
		networkCompletions = netC;
		networkCompletionsOn = netCOn;
		networkCompletionsOff = netCOff;
		duration = d;
		Sft = sft;
		virtualServiceTime = vst;
		virtualServiceTimeVariance = vstVar;
		stOn = ston;
		stOff = stoff;
		durationON = dOn;
		durationOFF = dOoff;
		avgON = aOn;
		avgOFF = aOff;
		
	}

	public double computeRtON() {
		double rtON = 0;
		rtON = durationON / (durationON + durationOFF);
		
		return rtON;
	}
	
	
	public double computeL() {
		double L = 0;
		L = networkCompletions / duration;

		return L;
	}
	
	public double computeLOffModel() {
		double LoffModel = 0;
		double L = computeL();
		
		LoffModel = 1 / (avgOFF + avgON);
			
		return LoffModel;
	}
	
	public double computeLOnModel() {
		double LonModel = 0;
		
		double LoffModel = computeLOffModel();
		double L = computeL();
		
		LonModel = L - LoffModel;

		return LonModel;
	}
	
	public double computeLon() {
		double Lon = 0;
		Lon = networkCompletionsOn / duration;

		return Lon;
	}
	
	public double computeLoff() {
		double Loff = 0;
		Loff = networkCompletionsOff / duration;

		return Loff;
	}
	
	public double computeS() {
		double S = 0;
		double L = 0;
		
		L = computeL();		
		S = meanResponseTime / (1 + (meanResponseTime * L));
		
		return S;
	}
	
	public double computeLonOff() {
		double Result = 0;

		double Lon = 0;
		double Loff = 0;
		
		Lon = computeLon();
		Loff = computeLoff();
		
		Result = (Lon * stOn) + (Loff * stOff);
		
		return Result;
	}
	
	public double computeLS() {
		double Result = 0;

		double L = 0;
		L = computeL();
		
		Result = virtualServiceTime / (1 - (virtualServiceTime * L));
		
		return Result;
	}
	
	
	public double computeRon() {
		double Ron = 0;
		double Lon = 0;
		double Loff = 0;
		double L = 0;
		
		
		
		Lon = computeLOnModel();
		Loff = computeLOffModel();
			
		double son = Sft;
		double soff = avgOFF + Sft;
		
		L= computeL();
	
		Ron = ((Loff * soff) + (Lon * son) + (L * son) - (L * Loff * son * soff) + (L * Loff * Math.pow(soff, 2))) / ((2*L) - (2 * L * Lon * son) - (2 * L * Loff * soff));

		
		return Ron;
	}
	
	public double computeRoff() {
		double Roff = 0;
		double Lon = 0;
		double Loff = 0;
		double L = 0;
		

		Lon = computeLOnModel();
		Loff = computeLOffModel();
		
		L = computeL();
		double son = Sft;
		double soff = avgOFF + Sft;
		
		Roff = ((Lon * son) + (Loff * soff) + (L * soff) - (L * Lon * son * soff) + (L * Lon * Math.pow(son, 2))) / ((2*L) - (2 * L * Lon * son) - (2 * L * Loff * soff));
		
		
		return Roff;
	}
	
	public double computeR() {
		double R = 0;
		double Lon = 0;
		double Loff = 0;
		double L = 0;
		
		Lon = computeLon();
		Loff = computeLoff();
		L = computeL();
		
				
		double R1 = (Math.pow(avgOFF, 2) / (avgON + avgOFF)) + (Sft * ((avgON + avgOFF) / avgON));
		double R2 = 1 - (L * Sft * ((avgON + avgOFF) / avgON));
		
		R = R1 / R2;
		
		return R;
	}
	
	public double computeR_mosxolios() {
		double R = 0;
		double L = 0;
		
		double theta_OFF = 1 / avgOFF;
		double theta_ON = 1 / avgON;
		
		double mu = 1 / Sft;

		L = computeL();
		
				
		double R1 = (L / (mu * (theta_OFF / (theta_ON + theta_OFF)))) + (L * avgOFF * (theta_ON / (theta_ON + theta_OFF)));
		double R2 = 1 - (L / (mu * (theta_OFF / (theta_ON + theta_OFF))));
		
		
		R = (R1 / R2) /  L;
		
		return R;
	}
	
	public double computeEN_mosxolios() {
		double EN = 0;
		double L = 0;
		
		double theta_OFF = 1 / avgOFF;
		double theta_ON = 1 / avgON;
		
		double mu = 1 / Sft;

		L = computeL();
		
				
		double EN1 = (L / (mu * (theta_OFF / (theta_ON + theta_OFF)))) + (L * avgOFF * (theta_ON / (theta_ON + theta_OFF)));
		double EN2 = 1 - (L / (mu * (theta_OFF / (theta_ON + theta_OFF))));
		
		
		EN = (EN1 / EN2);
		
		return EN;
	}
	

	public double computeR_paper() {
		double R_1 = 0;
		double R_2 = 0;
		double b = 0;
		double s = 0;
		double s_sq = 0;
		double ro = 0;
		double L = computeL();
		
		double thetaON = 1/avgON;
		double thetaOFF = 1/avgOFF;
		
		b = (thetaON / thetaOFF) / (thetaON + thetaOFF + L);
		s = (1 + (thetaON / thetaOFF)) * Sft;
		s_sq = (Math.pow((1 + (thetaON / thetaOFF)), 2) * Math.pow(Sft, 2)) + ((2*thetaON / Math.pow(thetaOFF, 2)) * Sft);
		ro = L * Sft * (1 + (thetaON/thetaOFF));
		
		R_1 = (L * s_sq) / (2 * (1 - ro));
		R_2 = (s + (b * (1 + L * (s + (1/thetaOFF))))) / (1 + (L*b)); 
		

		return R_1 + R_2;
	}

	
	public double computeQon() {
		double Qon = 0;
		
		Qon = computeLon() * meanResponseTimeOn;
		
		return Qon;
	}
	
	public double computeQoff() {
		double Qoff = 0;
		
		Qoff = computeLoff() * meanResponseTimeOff;
		
		return Qoff;
	}
	
	public double computeQsim() {
		double N = 0;
		double L = computeL();

		N = L * meanResponseTime;

		return N;
	}
	
	public double computePmodelSim() {
		double P = 0;
		double L = computeL();

		P = L * Sft * ((avgON+avgOFF)/avgON);

		return P;
	}
	

	
	

}
