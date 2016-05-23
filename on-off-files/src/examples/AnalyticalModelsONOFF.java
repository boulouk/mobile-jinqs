package examples;

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
	public double stOn;
	public double stOff;
	public double durationON;
	public double durationOFF;
	public double avgON;
	public double avgOFF;

	public AnalyticalModelsONOFF(double mD, double mDOn, double mDOff, double netC, double netCOn, double netCOff, double d, double sft, double vst, double ston, double stoff, double dOn, double dOoff, double aOn, double aOff) {
		meanResponseTime = mD;
		meanResponseTimeOn = mDOn;
		meanResponseTimeOff = mDOff;
		networkCompletions = netC;
		networkCompletionsOn = netCOn;
		networkCompletionsOff = netCOff;
		duration = d;
		Sft = sft;
		virtualServiceTime = vst;
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
	
	public double computeQueueSize() {
		double L = 0;
		L = networkCompletions / duration;
		
		double part1 = ((networkCompletionsOff * (avgON/stOn)) * (L * stOn));
		double part2 = part1 ;

		return part2;
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
		
		Lon = computeLon();
		Loff = computeLoff();
		L= computeL();
		//double soff = Sft + avgOFF;
		
		//Ron = stOn / (1 - (L * stOn));
//		Ron = stOn / (1 - (Lon * stOn) - (Loff * stOff));
		Ron = (stOn - (stOn * stOff * Loff) + (Loff * Math.pow(stOff, 2))) / (1 - (Lon * stOn) - (Loff * stOff));
		//Ron = stOn / (1 - (L * virtualServiceTime));
		
		return Ron;
	}
	
	public double computeRoff() {
		double Roff = 0;
		double Lon = 0;
		double Loff = 0;
		double L = 0;
		
		Lon = computeLon();
		Loff = computeLoff();
		L = computeL();
		
		//double soff = Sft + avgOFF;
		
		Roff = (stOff - (stOn * stOff * Lon) + (Lon * Math.pow(stOn, 2))) / (1 - (Lon * stOn) - (Loff * stOff));
		//Roff = stOff / (1 - (L * virtualServiceTime));
		
		return Roff;
	}
	
	public double computeST() {
		double ST = 0;
		double rtON = 0;

		rtON = computeRtON();
		ST = Sft + (avgOFF * (1-rtON));
		//ST = Sft + (avgOff * (1 - (rtON * (Math.pow(Math.exp(1.0), (-Sft/avgOn))))));
		
		//ST = Sft + (avgOff * (2 - rtON - (Math.pow(Math.exp(1.0), (- ((1/avgOn) * Sft))))));
		
		//ST = Sft + (avgOff * (1 - rtON + (Math.ceil(Sft/avgOn)) - ((Math.ceil(Sft/avgOn)) * (Math.pow(Math.exp(1.0), (- ((1/avgOn) * Sft)))))));
		
		return ST;
	}
	
	public double computeR_HV() {
		double R = 0;
		double L = computeL();
		double ST = computeST();
		

		R = stOn + ((L * Math.pow(stOn, 2)) / (1 - (L * stOn))) - ((L * Math.pow(stOn, 2)) / 2) + ((L * stOff)/2);
		//R = ST / (1 - (L * ST));

		return R;
	}
	
	public double computeS_From_RT() {
		double S_From_RT = 0;
		double RT = 0;
		double rtON = 0;

		rtON = computeRtON();
		RT = Sft + (avgOFF * (1-rtON));
		
		double L = 0;
		
		L = computeL();		
		S_From_RT = RT / (1 + (RT * L));
		
		return S_From_RT;
	}

	
	public double computeN() {
		double N = 0;
		double L = computeL();

		N = L * meanResponseTime;

		return N;
	}

}
