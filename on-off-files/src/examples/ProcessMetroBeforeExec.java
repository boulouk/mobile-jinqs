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

public class ProcessMetroBeforeExec {

	protected static double numberOfCustomers = 0;
	protected static double maxNumberOfCustomers = 0;
	protected static String Maxline;
	public static double duration = 0;
	private static int currentLine = 0;
	private static int currentLine2 = 0;

	public static void main(String args[]) {

//		getDuration("cite_u_dugomier.txt");
//		getDuration("dugomier_cite_u.txt");
//		printOnOff("cite_u_dugomier.txt");
//		printOnOff("dugomier_cite_u.txt");
//		getMean("cite_u_dugomierToff.txt");
//		getMean("dugomier_cite_uTon.txt");
		
		preprocess("cite_u_dugomier_paths.txt");
		preprocess("dugomier_cite_u_paths.txt");
		
		

	}

	
	
		
	

	public static void preprocess(String filename) {

		//cite_u - dugo
//		Exp offlineTime = new Exp(0.0122);
		//dugo - cite u
//		Exp offlineTime = new Exp(0.014);
		
		
		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filename));
			currentLine = 0;

			PrintStream duration = new PrintStream(filename + "Duration" + ".txt");

			lineNumberReader.setLineNumber(currentLine);

			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				
				
				String[] lineParts = line.split(",");

				
				

				if (line.equals(",,,,,,,,,,")) {
					duration.println(" ");
				} else {
					
					String status = lineParts[0];
					double TON_OFF =  Double.parseDouble(lineParts[3]);
					
					duration.println(TON_OFF);
				}
				
				currentLine++;
			}
			duration.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.err.println("Done!");

	}
	
	
	public static double average(List<Double> values) {
	  double average = 0;
	  for (Double d : values) {
		  average += d;
	  }
	  return average / values.size();
	  
  }


}
