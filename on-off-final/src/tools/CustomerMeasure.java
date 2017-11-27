package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CustomerMeasure extends Measure {

	public CustomerMeasure() {
		super();
	};

	public CustomerMeasure(int m) {
		super(m);
	};

	public void add(double x) {
		for (int i = 1; i <= moments; i++)
			moment[i] += Math.pow(x, (double) i);
		n += 1;

	}

	public double mean() {
		return moment[1] / n;

	}

	public double variance() {
		double mean = this.mean();
		return (moment[2] - n * mean * mean) / (n - 1);
	}

	public void addResponseTime(double x) {

		responseMeasures.append(truncateDecimal(x, 3));
		responseMeasures.append(", ");
	}

	public void addTimestamp(double x) {
		timestamps.append(truncateDecimal(x, 3));
		timestamps.append(", ");
	}

	public void saveResponsesAndTimestamps() {
		try {
			
			StringBuilder data = new StringBuilder();
			File file = new File("responseAndTimeMeasures.m");

			if (!file.exists()) {
				file.createNewFile();
			}

			responseMeasures.replace(responseMeasures.length() - 2, responseMeasures.length(), "");
			responseMeasures.append("];");
			
			data.append(responseMeasures.toString());
			data.append("\n");
			
			timestamps.replace(timestamps.length() - 2, timestamps.length(), "");
			timestamps.append("];");
			
			data.append(timestamps.toString());
			
			data.append("\n");
			data.append("plot(times,responses);");

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data.toString());
			bw.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public void saveCDFResponseMeasures() {
		try {

			File file = new File("responseMeasures.m");

			if (!file.exists()) {
				file.createNewFile();
			}

			responseMeasures.replace(responseMeasures.length() - 2,
					responseMeasures.length(), "");
			responseMeasures.append("];");
			responseMeasures.append("\n");
			responseMeasures.append("[f,x]=ecdf(responses);");
			responseMeasures.append("\n");
			responseMeasures.append("plot(x,f);");

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(responseMeasures.toString());
			bw.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private static BigDecimal truncateDecimal(double x, int numberofDecimals) {
		if (x > 0) {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals,
					BigDecimal.ROUND_FLOOR);
		} else {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals,
					BigDecimal.ROUND_CEILING);
		}
	}

}
