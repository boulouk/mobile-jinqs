package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;

import tools.Exp;

public class BeforeExec {
	
	protected static double numberOfCustomers = 0;
	public static double duration = 0;
	private static int currentLine = 0;
	private static int currentLine2 = 0;
	
	public static void main(String args[]) {

		computeRespTime("responseMeasures.txt");
		
	}
	
	public static void computeRespTime(String filename) {
		double sum = 0;
		
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;
			
			lineNumberReader.setLineNumber(currentLine);
//			StringBuilder plot = new StringBuilder("responses = [");
			
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(" ");
				
				String value = lineParts[0];
				double valueDouble = Double.parseDouble(value);
				
				sum = sum + valueDouble;
				
//				plot.append(value);
//				plot.append(", ");
					
				
				currentLine++;
			}
			
//			plot.replace(plot.length() - 2, plot.length(), "");
//			plot.append("]");
			
//			System.out.println(plot.toString());
			System.out.println(sum/currentLine);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
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
	
}
