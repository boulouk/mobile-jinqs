package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class FileDataSetOnOff extends DistributionSampler {
	private String file;

	private double initialTimestamp = 0;
	private double step = 0;
	private double numberOfCustomers = 0;

	private int currentLine = 0;
	private double lineNumber = 0;
	private LineNumberReader lineNumberReader = null;
	
	private List<Double> values = new ArrayList<Double>();

	public FileDataSetOnOff(String fl) {
		file = fl;		
		
		try {
			// Construct the LineNumberReader object
			lineNumberReader = new LineNumberReader(new FileReader(file));

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public double next() {

		double delay = 0;
		try {
			lineNumberReader.setLineNumber(currentLine);
			
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
				delay = Double.parseDouble(lineParts[0]);
				
				currentLine++;
				break;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		values.add(delay);
		
		return delay;
	}

	public double average() {
	  double average = 0;
	  for (Double d : values) {
		  average += d;
	  }
	  return average / values.size();
	  
  }
	
	public double getStep() {
		return step;
	}

	public double getNumberOfCustomers() {
		return numberOfCustomers;
	}

	public int getCurrentLine() {
		return currentLine;
	}
	
	
	
	
	

}
