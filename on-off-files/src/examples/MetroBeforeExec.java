package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import tools.Exp;

public class MetroBeforeExec {

	protected static double numberOfCustomers = 0;
	protected static double maxNumberOfCustomers = 0;
	protected static String Maxline;
	public static double duration = 0;
	private static int currentLine = 0;
	private static int currentLine2 = 0;

	public static void main(String args[]) {

//		getDuration("cite_u_dugomier.txt");
//		getDuration("dugomier_cite_u.txt");
//		getDuration("all.txt");
		process("metro/dugomier_cite_u.txt");
//		printOnOff("all.txt");
//		getMean("cite_u_dugomierToff_remOFF.txt");
//		getMean("dugomier_cite_uToff_2sec.txt");
		
//		print("cite_u_dugomierToff_remOFF.txt");
		
//		computeMeanR("cite_u_dugomier_proc_rem_paths.txt");
//		computeMeanR("dugomier_cite_u_paths.txt");

	}

	public static void getDuration(String filename) {

		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;

			lineNumberReader.setLineNumber(currentLine);

			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");


				double durDouble = Double.parseDouble(lineParts[3]);

				duration = duration + durDouble;
				currentLine++;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("lines: " + currentLine);
		System.out.println("duration: " + duration);

	}
	
	public static void getMean(String filename) {

		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;

			lineNumberReader.setLineNumber(currentLine);

			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");


				double durDouble = Double.parseDouble(lineParts[0]);

				duration = duration + durDouble;
				currentLine++;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("duration: " + duration);
		System.out.println("mean: " + duration/currentLine);
		
	}
	
	public static void computeMeanR(String filename) {

		List<Double> delayValues = new ArrayList<Double>();
		
		double T_ON = 0;
		int counterON = 0;
		double T_OFF = 0;
		int counterOFF = 0;
		double durDouble = 0;
		String status = "";
		double avgON = 0;
		double avgOFF = 0;
		
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;

			lineNumberReader.setLineNumber(currentLine);

			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");

//				System.out.println(line);
//				System.out.println(lineParts[0]);
				
				
				if(line.equals(",,,,,,,,,,")){
					avgON = T_ON/counterON;
					avgOFF = T_OFF/counterOFF;
					
					double R = 0;
					double L = 3;
					double Sft = 0.125;
					
					double R1 = (Math.pow(avgOFF, 2) / (avgON + avgOFF)) + (Sft * ((avgON + avgOFF) / avgON));
					double R2 = 1 - (L * Sft * ((avgON + avgOFF) / avgON));
					
					R = R1 / R2;
					
					delayValues.add(R);
					
					T_OFF = 0;
					T_ON = 0;
					counterOFF = 0;
					counterON = 0;
					
					
				} else {
					durDouble = Double.parseDouble(lineParts[3]);
					status = lineParts[0];
					if (status.equals("OFF") || status.equals("OFF-ON") || status.equals("OFF-BACK")) {
						T_OFF = T_OFF + durDouble;
						if(durDouble != 0) {
							counterOFF++;
						}
						
						
						
					} else if (status.equals("ON") || status.equals("ON-OFF")) {
						T_ON = T_ON + durDouble;
						counterON++;
						
					}
				}
				
				
				

//				duration = duration + durDouble;
				currentLine++;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("lines: " + currentLine);
		System.out.println("lines: " + delayValues);
		System.out.println("lines: " + average(delayValues));
		

	}

	public static void printOnOff(String filename) {

		//cite_u - dugo
//		Exp offlineTime = new Exp(0.0128);
		//dugo - cite u
		Exp offlineTime = new Exp(0.5);
		
		
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;

			PrintStream outOff = new PrintStream(filename + "Toff_2sec" + ".txt");
			PrintStream outOn = new PrintStream(filename + "Ton_2sec" + ".txt");

			lineNumberReader.setLineNumber(currentLine);

			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				offlineTime.next();
				String[] lineParts = line.split(",");

				double TON_OFF =  Double.parseDouble(lineParts[3]);

				String status = lineParts[0];
				if (status.equals("OFF") || status.equals("OFF-ON") || status.equals("OFF-BACK")) {
					if(TON_OFF == 0){
//						outOff.println(offlineTime.next());
						outOff.println(0);
					} else {
						outOff.println(TON_OFF);
					}
				} else if (status.equals("ON") || status.equals("ON-OFF")) {
					outOn.println(TON_OFF);
				}
				
				currentLine++;
			}
			outOff.close();
			outOn.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.err.println("Done!");

	}
	
	
	public static void process(String filename) {

		//cite_u - dugo
//		Exp offlineTime = new Exp(0.0128);
		//dugo - cite u
		Exp offlineTime = new Exp(0.5);
		
		
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;

			PrintStream out = new PrintStream(filename + "metro_path_2" + ".txt");

			lineNumberReader.setLineNumber(currentLine);

			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				offlineTime.next();
				String[] lineParts = line.split(",");

				double TON_OFF =  Double.parseDouble(lineParts[3]);

				String status = lineParts[0];
				if (status.equals("OFF") || status.equals("OFF-ON") || status.equals("OFF-BACK")) {
					if(TON_OFF != 0){
//						outOff.println(offlineTime.next());
						out.println(status+","+lineParts[1]+","+lineParts[5]);
					} 
				} else if (status.equals("ON") || status.equals("ON-OFF")) {
						out.println(status+","+lineParts[1]+","+lineParts[5]);
				}
				
				currentLine++;
			}
			out.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.err.println("Done!");

	}
	
	
	public static void print(String filename) {

		StringBuilder newString = new StringBuilder();
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;

			lineNumberReader.setLineNumber(currentLine);
			
			newString.append("x = [");

			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");


				double durDouble = Double.parseDouble(lineParts[0]);
				newString.append(lineParts[0]+",");
				
				currentLine++;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("string: " + newString);
			
	}
	
	public static double average(List<Double> values) {
	  double average = 0;
	  for (Double d : values) {
		  average += d;
	  }
	  return average / values.size();
	  
  }


}
