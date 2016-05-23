package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;

import tools.Exp;
import tools.FileDataSet;

public class BeforeExec {
	
	protected static double numberOfCustomers = 0;
	protected static double maxNumberOfCustomers = 0;
	protected static String Maxline;
	public static double duration = 0;
	private static int currentLine = 0;
	private static int currentLine2 = 0;
	
	public static void main(String args[]) {

		
//		plotInputRate("antenna1_2weeks1_step25.txt", 600, "antenna2");
//		plotWeeklyInputRate("SET2_AntennaFull_96.txt", 600, "antenna96");
//		plotServiceRate("antenna11weeks25.txt", 151, 4, "antenna11");
//		plotWeeklyServiceRate("SET2_AntennaFull_50.txt", 458, 8, "antenna50");
//		getElements("SET2_AntennaFull_24.txt");
//		printOnOff("SET2_AntennaFull_24.txt", 499);
		compureRespAnModel("samples_for_rates_an24.txt", "161_TonToff.txt");
//		showAntennas("antennalist.txt");
		
	}
	
	public static void showAntennas(String filename) {
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;
			
			lineNumberReader.setLineNumber(currentLine);
			StringBuilder antennas = new StringBuilder("antennaIds = [");
			
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(" ");
				
				String antennaID = lineParts[0];
				double noOfCustomers = Double.parseDouble(lineParts[1]);
				
				if(noOfCustomers > 47000 ) {
					antennas.append(antennaID);
					antennas.append(", ");
					
				}
				currentLine++;
			}
			
			antennas.replace(antennas.length() - 2, antennas.length(), "");
			antennas.append("]");
			
			System.out.println(antennas.toString());

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
	}
	
	public static void getElements(String filename) {
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;
			
			lineNumberReader.setLineNumber(currentLine);
			
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
//				String antennaID = lineParts[0];
				String timestamp = lineParts[0];
				double noOfCustomers = Double.parseDouble(lineParts[1]);
				numberOfCustomers = numberOfCustomers + noOfCustomers;
				
				if(noOfCustomers > maxNumberOfCustomers ) {
					maxNumberOfCustomers = noOfCustomers;
					Maxline = line;
					
				}
				currentLine++;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Total Customers: " + numberOfCustomers);
		System.out.println("Max Customers: " + maxNumberOfCustomers);
		System.out.println("Max Line: " + Maxline);
		System.out.println("Lines: " + currentLine);
		duration = (currentLine) * 600;
	    
	    System.err.println("Duration: " + duration);
	    
	    System.err.println("Rate: " + (numberOfCustomers/duration));
	    System.err.println("Avg Customers: " + (numberOfCustomers/currentLine));
	}
	
	public static void printOnOff(String filename, int maxPop) {
		
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;
			
			PrintStream out = new PrintStream(filename + "Toff" + ".txt");
			
			lineNumberReader.setLineNumber(currentLine);
			
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
				String timestamp = lineParts[0];
				double noOfCustomers = Double.parseDouble(lineParts[1]);
				
				double TON = 600 * (noOfCustomers/maxPop);
				double TOFF = 600 * (1 - (noOfCustomers/maxPop));
				
				out.println(TOFF);
//				out.print(",");
//				out.print(TOFF);
//				out.print(",");
//				out.println(600/noOfCustomers);
				
				currentLine++;
			}
			out.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
	    System.err.println("Duration: " + duration);
	   
	}
	
public static void compureRespAnModel(String ratesFile, String connectFiles) {
	
	double TON = 0;
	double TOFF = 0;
	double stime = 0;
	double het_s = 0;
	double l = 0;
	
	double R_part1 = 0;
	double R_part2 = 0;
	double R_part3 = 0;
	double R = 0;
	
	double Rtotal=0;
	double Stotal=0;
	
	double l_total = 0;
	double TON_total = 0;
	double TOFF_total = 0;
	
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(ratesFile));
			LineNumberReader lineNumberReader2 = new LineNumberReader(new FileReader(connectFiles));
			currentLine = 0;
			currentLine2 = 0;
			
			PrintStream out = new PrintStream("ResponseTimes.txt");
			
			lineNumberReader.setLineNumber(currentLine);
			lineNumberReader2.setLineNumber(currentLine2);
			
//			Exp serve = new Exp(1);
//			
//			for(int j=0; j<=1000000; j++){
//				stime = serve.next();
//				Stotal = Stotal + stime;
//			}
			
			
			String line = null;
			String line2 = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				l = Double.parseDouble(lineParts[0]);
				
				l_total = l_total + l;
				
				//currentLine2 = 0;
//				lineNumberReader2.setLineNumber(currentLine2);
//				while ((line2 = lineNumberReader2.readLine()) != null) {
//					
//					if(currentLine == currentLine2) {
//						String[] lineParts2 = line2.split(",");
//						
//						TON = Double.parseDouble(lineParts2[0]);
//						TOFF = Double.parseDouble(lineParts2[1]);
//						
//						het_s = Double.parseDouble(lineParts2[2]);
//						
//						currentLine2++;
//						
//						break;
//					}
//					
//					
//					currentLine2++;
//				}
//				stime = serve.next();
				
//				stime = 0.1;
				
				TON = 40;
				TOFF = 41;
				stime = 1;
//				
				
				R_part1 = Math.pow(TOFF, 2)/(TON+TOFF);
				R_part2 = stime * ((TON+TOFF) / TON);
				R_part3 = 1 - (l * stime * ((TON+TOFF) / TON));
				
//				if((l * het_s * stime) < 0){
//					R = (het_s * stime) / (1 - (0.1));
//				} else 
					//R = (het_s * stime) / (1 - (l * het_s * stime));
				
				
//				if(R_part3 < 0) {
//					R_part3 = 0.1;
//				}
				R = (R_part1 + R_part2)/R_part3;
				
				Rtotal = Rtotal + R;
				
				if (currentLine == 2016 || currentLine == 4032 || currentLine == 6048 || currentLine == 8063 || currentLine == 10080 || currentLine == 12096 || currentLine == 14112  
					|| currentLine == 16128 || currentLine == 18144 || currentLine == 20160 || currentLine == 22176 || currentLine == 24192 || currentLine == 26208 || currentLine == 28224 || currentLine == 30240
					 || currentLine == 32256 || currentLine == 34272 || currentLine == 36288 || currentLine == 38304 || currentLine == 40320 || currentLine == 42336 || currentLine == 44352 || currentLine == 46368) {
					
					System.out.println("Ravg: " + (Rtotal/currentLine));
				}
				
				
				//Stotal = Stotal + stime;
				//TON_total = TON_total + TON;
				//TOFF_total = TOFF_total + TOFF;
				
				out.println(R);
				
				currentLine++;
			}
			out.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
		
	    System.err.println("fin");
	    System.err.println("currentLine: " + currentLine);
	    System.err.println("Ravg: " + (Rtotal/currentLine));
	    //System.err.println("Savg: " + Stotal/(currentLine+1000000));
	    
	    //System.err.println("Lavg: " + l_total/currentLine);
	}
	
	
	
	
	public double getDuration (String filename) {
		
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;
			
			lineNumberReader.setLineNumber(currentLine);
			
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
//				String antennaID = lineParts[0];
				String timestamp = lineParts[0];
				double noOfCustomers = Double.parseDouble(lineParts[1]);
				numberOfCustomers = numberOfCustomers + noOfCustomers;
				
				if(noOfCustomers > maxNumberOfCustomers ) {
					maxNumberOfCustomers = noOfCustomers;
					Maxline = line;
					
				}
				currentLine++;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		double duration = (currentLine) * 600;
	    
		return duration;
	}
	
	public double getCompletions (String filename) {
		double numOfCustomers = 0;

		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;
			
			lineNumberReader.setLineNumber(currentLine);
			
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
//				String antennaID = lineParts[0];
				String timestamp = lineParts[0];
				double noOfCustomers = Double.parseDouble(lineParts[1]);
				numOfCustomers = numOfCustomers + noOfCustomers;
				
				if(noOfCustomers > maxNumberOfCustomers ) {
					maxNumberOfCustomers = noOfCustomers;
					Maxline = line;
					
				}
				currentLine++;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
		return numOfCustomers;
	}
	
	public static void plotInputRate(String filename, int intervalStep, String outputFilename) {
		
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;

			lineNumberReader.setLineNumber(currentLine);
			

			StringBuilder rate = new StringBuilder("rate = [");
			StringBuilder intervals = new StringBuilder("intervals = [");
			
			String line = null;
			int counter = 1;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
//				String antennaID = lineParts[0];
				String timestamp = lineParts[0];
				double noOfCustomers = Double.parseDouble(lineParts[1]);
				
				
				double rateLine = noOfCustomers/intervalStep;
				rate.append(rateLine);
				rate.append(", ");
				
			
				intervals.append(counter);
				intervals.append(", ");
				
				currentLine++;
				counter++;
			}
			
			rate.replace(rate.length() - 2, rate.length(), "");
			rate.append("]");
			
			intervals.replace(intervals.length() - 2, intervals.length(), "");
			intervals.append("]");
			
		    try {
		    	PrintStream out = new PrintStream(outputFilename + "InputRate" + ".m");
		    	
				out.println(intervals.toString());
				out.println(rate.toString());
				out.println("plot(intervals, rate)");
				
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public static void plotWeeklyInputRate(String filename, int intervalStep, String outputFilename) {
		
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;

			lineNumberReader.setLineNumber(currentLine);
			

			StringBuilder rate = new StringBuilder("rate = [");
			StringBuilder intervals = new StringBuilder("intervals = [");
			
			StringBuilder period = new StringBuilder("period = [");
			
			String line = null;
			int weekCounter = 2;
			int counter = 0;
			int observationPeriod = intervalStep;
			double weeks [] = {1358725800, 1359935400, 1361145000, 1362354000, 1363564200, 1364773800, 1365983400, 1367193000, 1368402600, 1369612200, 1370821800, 1372031400, 1373241000, 1374450600, 1375660200, 1376869800, 1378079400, 1379289000, 1380498600, 1381708200, 1382917800, 1384127400, 1385337000, 1386546600, 1387756200};
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
//				String antennaID = lineParts[0];
				double timestamp = Double.parseDouble(lineParts[0]);
				double noOfCustomers = Double.parseDouble(lineParts[1]);
				numberOfCustomers = numberOfCustomers + noOfCustomers;
				
				if (timestamp == weeks[counter]) {
					
					period.append(weeks[counter] - 1357516200);
					period.append(", ");
					
					double rateLine = numberOfCustomers/observationPeriod;
					rate.append(rateLine);
					rate.append(", ");
					
					intervals.append(weekCounter);
					intervals.append(", ");
					weekCounter = weekCounter + 2;
					if((weeks.length-1) > counter)						
						counter++;
					
					
					
					
					numberOfCustomers = 0;
					observationPeriod = 0;
				}
				
				currentLine++;
				
				observationPeriod = observationPeriod + intervalStep;
			}
			
			rate.replace(rate.length() - 2, rate.length(), "");
			rate.append("]");
			
			intervals.replace(intervals.length() - 2, intervals.length(), "");
			intervals.append("]");
			
			period.replace(period.length() - 2, period.length(), "");
			period.append("]");
			
//			System.out.println(period.toString());
			
		    try {
		    	PrintStream out = new PrintStream(outputFilename + "WeeklyInputRate" + ".m");
		    	
				out.println(intervals.toString());
				out.println(rate.toString());
				out.println("plot(intervals, rate)");
				
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	
	public static void plotServiceRate(String filename, int maxPopulation, double serviceRate, String outputFilename) {
		
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;

			lineNumberReader.setLineNumber(currentLine);
			

			StringBuilder rate = new StringBuilder("serviceRate = [");
			StringBuilder intervals = new StringBuilder("intervals = [");
			
			String line = null;
			int counter = 1;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
//				String antennaID = lineParts[0];
				String timestamp = lineParts[0];
				double noOfCustomers = Double.parseDouble(lineParts[1]);
				
				double rateLine = serviceRate * (noOfCustomers/maxPopulation);
				rate.append(rateLine);
				rate.append(", ");
				
			
				intervals.append(counter);
				intervals.append(", ");
				
				currentLine++;
				counter++;
			}
			
			rate.replace(rate.length() - 2, rate.length(), "");
			rate.append("]");
			
			intervals.replace(intervals.length() - 2, intervals.length(), "");
			intervals.append("]");
			
		    try {
		    	PrintStream out = new PrintStream(outputFilename + "ServiceRate" + ".m");
		    	
				out.println(intervals.toString());
				out.println(rate.toString());
				out.println("plot(intervals, serviceRate)");
				
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public static void plotWeeklyServiceRate(String filename, int maxPopulation, double serviceRate, String outputFilename) {
		
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;

			lineNumberReader.setLineNumber(currentLine);
			

			StringBuilder rate = new StringBuilder("serviceRate = [");
			StringBuilder intervals = new StringBuilder("intervals = [");
			
			String line = null;
			int weekCounter = 2;
			int counter = 0;
			int mean = 0;
			double weeks [] = {1358725800, 1359935400, 1361145000, 1362354000, 1363564200, 1364773800, 1365983400, 1367193000, 1368402600, 1369612200, 1370821800, 1372031400, 1373241000, 1374450600, 1375660200, 1376869800, 1378079400, 1379289000, 1380498600, 1381708200, 1382917800, 1384127400, 1385337000, 1386546600, 1387756200};
			
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
//				String antennaID = lineParts[0];
				double timestamp = Double.parseDouble(lineParts[0]);
				double noOfCustomers = Double.parseDouble(lineParts[1]);
				numberOfCustomers = numberOfCustomers + noOfCustomers;
				mean++;
				
				if (timestamp == weeks[counter]) {
					double rateLine = serviceRate * ((numberOfCustomers/mean)/maxPopulation);
					rate.append(rateLine);
					rate.append(", ");
					
					intervals.append(weekCounter);
					intervals.append(", ");
					weekCounter = weekCounter + 2;
					if((weeks.length-1) > counter)						
						counter++;
					
					numberOfCustomers = 0;
					mean = 0;
				}
				
				currentLine++;
			}
			
			rate.replace(rate.length() - 2, rate.length(), "");
			rate.append("]");
			
			intervals.replace(intervals.length() - 2, intervals.length(), "");
			intervals.append("]");
			
		    try {
		    	PrintStream out = new PrintStream(outputFilename + "WeeklyServiceRate" + ".m");
		    	
				out.println(intervals.toString());
				out.println(rate.toString());
				out.println("plot(intervals, serviceRate)");
				
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

}
