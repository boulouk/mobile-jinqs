package extensions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;

import tools.Deterministic;
import tools.DistributionSampler;
import tools.Event;
import tools.Exp;
import tools.Sim;
import network.*;

public class LifetimeDataSetQN extends QueueingNode {
	public static boolean con;
	public DistributionSampler lifetimeDistrib;
	public int maxPopoulation;
	public int line = 0;
	public int currentLine = 1;
	public double demandPopulation = 0;
	public String antenna;
	private LineNumberReader lineNumberReader = null;
	private LineNumberReader lineNumberReaderInput = null;

	public LifetimeDataSetQN(String s, Delay d, int n, String distribType, double value, String an, int maxPop) {
		super(s, d, n);
		con = true;
		maxPopoulation =  maxPop;
		antenna = an;
		
		if(!antenna.equals("Not")) {
			try {
				lineNumberReader = new LineNumberReader(new FileReader(antenna));
				lineNumberReaderInput = new LineNumberReader(new FileReader("SET2_AntennaFull_248.txt"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (distribType.equals("Exp")) {
			lifetimeDistrib = new Exp(value);
		} else if (distribType.equals("Det")) {
			lifetimeDistrib = new Deterministic(value);
		} else {
			lifetimeDistrib = null;
		}

	}

	@Override
	protected void accept(Customer c) {
		double demandRate = 0;
		if (lifetimeDistrib != null) {
			c.setQueueingNodeInsertionTime(Sim.now());
			c.setLifetime(lifetimeDistrib.next());
			c.setWithLifetime(true);
		}
		if (resources.resourceIsAvailable()) {
			Debug.trace("Resource claimed");
			resources.claim();
			
			if (antenna.equals("Not")){
				invokeService(c);
				
			} else {
				int now = (int) Sim.now();
				int lineNumber = (now / 600) + 1;
				if (lineNumber != line) {
					line = lineNumber;
					demandPopulation = getServicePopulation(lineNumber);
//					demandRate = getAntennaRate(lineNumber);
				}
				
				invokeService(c, demandPopulation);
//				invokeService(c, demandPopulation, demandRate);
			}
			
		} else {
			if (queue.canAccept(c)) {
				Debug.trace("No resources. Enqueueing customer...");
				queue.enqueue(c);
			} else {
				losses++;
				Debug.trace("No resources. Queue full - customer sent to "
						+ lossNode.getId());
				lossNode.enter(c);
			}
		}
	}

	@Override
	public void releaseResource() {
		Debug.trace(this + " releasing resource");
		if (!queue.isEmpty()) {
			Customer c = queue.dequeue();
			double demandRate = 0;
			
			if (antenna.equals("Not")){
				invokeService(c);
				
			} else {
				int now = (int) Sim.now();
				int lineNumber = (now / 600) + 1;
				if (lineNumber != line) {
					line = lineNumber;
					demandPopulation = getServicePopulation(lineNumber);
//					demandRate = getAntennaRate(lineNumber);
				}
				
//				invokeService(c, demandPopulation, demandRate);
				invokeService(c, demandPopulation);
			}
			
//		    try {
//				String invokeTimeStr = String.valueOf(Sim.now());
//		
//				String content = "Line: " + lineNumber + " Population: " + demandPopulation;
//		
//				File file = new File("diary.txt");
//		
//				if (!file.exists()) {
//					file.createNewFile();
//				}
//		
//				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//				BufferedWriter bw = new BufferedWriter(fw);
//				bw.write(content);
//				bw.write("\n");
//				bw.close();
//		
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
			
			
		} else {
			resources.release();
		}
	}
	
	public double getServicePopulation (int position) {
		
		double noOfCustomers = 0;
		
		try {
			
			lineNumberReader.setLineNumber(currentLine);
			
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
//				String antennaID = lineParts[0];
				String timestamp = lineParts[0];
				noOfCustomers = Double.parseDouble(lineParts[1]);
				if (position == currentLine) {
					currentLine++;
					break;
				}
				currentLine++;
			}
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		double newDemand = noOfCustomers;
		return newDemand;
	}
	
	public double getAntennaRate (int position) {
		
		double noOfCustomers = 0;
		
		try {
			
			lineNumberReader.setLineNumber(currentLine);
			
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] lineParts = line.split(",");
				
//				String antennaID = lineParts[0];
				String timestamp = lineParts[0];
				noOfCustomers = Double.parseDouble(lineParts[1]);
				if (position == currentLine) {
					currentLine++;
					break;
				}
				currentLine++;
			}
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		double newDemand = noOfCustomers;
		return 600/newDemand;
	}

}
