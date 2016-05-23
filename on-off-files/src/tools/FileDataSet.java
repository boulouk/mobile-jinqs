package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class FileDataSet extends DistributionSampler {
	private String file;

	private double initialTimestamp = 0;
	private double step = 0;
	private double numberOfCustomers = 0;

	private int currentLine = 0;
	private double lineNumber = 0;
	private LineNumberReader lineNumberReader = null;

	public FileDataSet(String fl) {
		file = fl;		
		
		try {

			// Construct the LineNumberReader object
			lineNumberReader = new LineNumberReader(new FileReader(file));
			LineNumberReader tmpLineNumberReader = new LineNumberReader(new FileReader(file));

			String firstLine = tmpLineNumberReader.readLine();
			String secondLine = tmpLineNumberReader.readLine();

			String[] firstLineParts = firstLine.split(",");
			String[] secondLineParts = secondLine.split(",");

			initialTimestamp = Double.parseDouble(firstLineParts[0]);
			step = (Double.parseDouble(secondLineParts[0])) - (Double.parseDouble(firstLineParts[0]));

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public double next() {

		double noOfCustomers = 0;
		try {
			lineNumberReader.setLineNumber(currentLine);
			
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
//				String antennaID = lineParts[0];
				String timestamp = lineParts[0];
				noOfCustomers = Double.parseDouble(lineParts[1]);
				numberOfCustomers = numberOfCustomers + noOfCustomers;
				currentLine++;
				break;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return noOfCustomers;
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
