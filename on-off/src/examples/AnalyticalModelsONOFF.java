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
		
		//LoffModel = (L * (Sft + avgOFF) - (Math.pow(L, 2) * Sft * avgOFF)) / (avgON + (L * Math.pow(avgOFF, 2)));
		
		//LoffModel = ((2 * L * Sft) - (Math.pow(L, 2) * Sft * avgOFF) + (L * avgOFF)) / ((2 * avgON) + (L * Math.pow(avgOFF, 2)));

		//LoffModel = (1 * (0.125 + 20) - (Math.pow(1, 2) * 0.125 * 20)) / (20 + (1 * Math.pow(20, 2))); 
		
		//LoffModel = 0.05 * (20/(20+20));
		
		//LoffModel = ((L*Sft) - (Math.pow(L, 2) * Sft) + L) / (avgON + (L * avgOFF));
		
		//LoffModel = L * ((avgOFF/2) / (avgOFF + avgON));
		LoffModel = 1 / (avgOFF + avgON);
		
		//LoffModel = L * (Sft / avgON);
		
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
		
		//Lon = computeLon();
		//Loff = computeLoff();
		
		Lon = computeLOnModel();
		Loff = computeLOffModel();
		
//		Lon = computeLOnModel();
//		Loff = computeLOffModel();
		
		double son = Sft;
		double soff = avgOFF + Sft;
		
		L= computeL();
		//double soff = Sft + avgOFF;
		
//		Ron = stOn / (1 - (Lon * stOn));
//		Ron = stOn / (1 - (Lon * stOn) - (Loff * stOff));
//		Ron = (stOn - (stOn * stOff * Loff) + (Loff * Math.pow(stOff, 2))) / (1 - (Lon * stOn) - (Loff * stOff));
		
		//pririorities:s
//		Ron = (stOn + (Loff * Math.pow(stOff, 2) * Math.pow((L/stOff), 2))) / (1 - (Lon * Math.pow(stOn, 2)));
//		double part1 = 1 - (Lon * Math.pow(stOn, 2));
//		double part2 = Math.pow((-1 + (Lon * Math.pow(stOn, 2))), 2);
//		double part3 = 4 * (Math.pow(Lon, 2)) * (Math.pow(stOn, 3)) * Loff;
//		double part4 = 2 * (Math.pow(Lon, 2)) * (Math.pow(stOn, 2)) * Loff;
//		
//		Ron = (part1 + Math.sqrt((part2 - part3))) / (part4);
//	
		
		//Ron = stOn / (1 - (L * virtualServiceTime));
		
//		Ron = (stOn - (stOn * stOff * Loff) + (Loff * Math.pow(stOff, 2)) + stOff) / (2 - (2 * (Lon * stOn)) - (2 * (Loff * stOff)));
//		Ron = ((2 * L * stOff) - (Loff * stOn) + (Loff * stOff) + (L * Loff * stOn * stOff) - (L * Loff * Math.pow(stOff, 2))) / ((2*L) - (2 * L * Lon * stOn) - (2 * L * Loff * stOff));
		
//		Ron = ((Loff * stOff) + (Lon * stOn) + (L * stOn) - (L * Loff * stOn * stOff) + (L * Loff * Math.pow(stOff, 2))) / ((2*L) - (2 * L * Lon * stOn) - (2 * L * Loff * stOff));
		
		Ron = ((Loff * soff) + (Lon * son) + (L * son) - (L * Loff * son * soff) + (L * Loff * Math.pow(soff, 2))) / ((2*L) - (2 * L * Lon * son) - (2 * L * Loff * soff));

		//Ron = ((((Loff * soff * (soff - son)) / 2)) + (soff/2) + son) / (1 - (Lon * son) - (Loff * soff));
		
		return Ron;
	}
	
	public double computeRoff() {
		double Roff = 0;
		double Lon = 0;
		double Loff = 0;
		double L = 0;
		
//		Lon = computeLon();
//		Loff = computeLoff();
		
		Lon = computeLOnModel();
		Loff = computeLOffModel();
		
//		Lon = computeLOnModel();
//		Loff = computeLOffModel();
		
		L = computeL();
		double son = Sft;
		double soff = avgOFF + Sft;
		

		//double soff = Sft + avgOFF;
		
//		Roff = stOff / (1 - (Loff * stOff));
//		Roff = (stOff - (stOn * stOff * Lon) + (Lon * Math.pow(stOn, 2))) / (1 - (Lon * stOn) - (Loff * stOff));
		//Roff = stOff / (1 - (L * virtualServiceTime));
		
//		Roff = ((2 * stOff) - (stOn * stOff * Lon) + (Lon * Math.pow(stOn, 2))) / (2 - (2 * (Lon * stOn)) - (2 * (Loff * stOff)));
//		Roff = ((Lon * stOn) + (Loff * stOff) + (L * stOff) - (L * Lon * stOn * stOff) + (L * Lon * Math.pow(stOn, 2))) / ((2*L) - (2 * L * Lon * stOn) - (2 * L * Loff * stOff));
		
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
		
		
		//R = ((Lon / L) * computeRon()) +  ((Loff / L) * computeRoff());
		
		double R1 = (Math.pow(avgOFF, 2) / (avgON + avgOFF)) + (Sft * ((avgON + avgOFF) / avgON));
		double R2 = 1 - (L * Sft * ((avgON + avgOFF) / avgON));
		
		R = R1 / R2;
		
		return R;
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
		
		//R_2 = ((2*stOff) + (L * (Math.pow(stOff, 2) - Math.pow(stOn, 2)))) / (2 * (1 + L * (stOff - stOn)));
		//R = ST / (1 - (L * ST));

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
	

	
	

}
