import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Jama.Matrix;

public class Requioma2 {
	int numRows;
	int numCols;
	private Matrix load(String filename) throws IOException {
		int numRows = 0;
		int numCols = 0;
		String line;
		String line2;
		int ctr = 0;
		BufferedReader in = new BufferedReader(new FileReader(filename));
		line = in.readLine();
		try {
			while ((line = in.readLine()) != null) {
				numRows++;
				String[] values = line.split(",");
				numCols = (values.length);   // number of columns for matrix X
			}
		} catch(NullPointerException npe) {
			//
		} finally {
			in.close();
		}
		Matrix X = new Matrix(numRows, numCols);
		
		// populating Matrix X
		for (int i = 0; i < numRows; i++) {
			X.set(i, 0, 1);   // setting bias
		}
		BufferedReader in2 = new BufferedReader(new FileReader(filename));
		line2 = in2.readLine();
		try {
			while ((line2 = in2.readLine()) != null) {
				String[] values = line2.split(",");
				for (int j = 0; j < numRows; j++) {
					if (ctr > 0) {
						line2 = in2.readLine();
						values = line2.split(",");
					}
					ctr++;
					for (int i = 1; i <= numCols - 1; i++) {
						double intVal = Double.parseDouble(values[i - 1]);
						if (i == numCols) {
							//Y.set(j, 0, intVal);
						} else {
							X.set(j, i, intVal);
						}
					}
				}
			}
		} catch(NullPointerException npe) {
			//
		} finally {
			in2.close();
		}
		this.numRows = numRows;
		this.numCols = numCols;
		return X;
	}
	
	private Matrix loadY(String filename) throws IOException {
		String line;
		String line2;
		int ctr = 0;
		ArrayList<String> classList = new ArrayList<String>();
		BufferedReader in = new BufferedReader(new FileReader(filename));
		line = in.readLine();
		try {
			while ((line = in.readLine()) != null) {
				String[] values = line.split(",");
				if (classList.size() == 0) {
					classList.add(values[numCols - 1]);
				} else {
					if (!classList.contains(values[numCols - 1])) {
						classList.add(values[numCols - 1]);
					}
				}
			}
		} catch(NullPointerException npe) {
			//
		} finally {
			in.close();
		}
		Matrix Y = new Matrix(numRows, classList.size());
		BufferedReader in2 = new BufferedReader(new FileReader(filename));
		line2 = in2.readLine();
		try {
			while ((line2 = in2.readLine()) != null) {
				String[] values = line2.split(",");
				int index = classList.indexOf(values[numCols - 1]);
				Y.set(ctr, index, 1);
				ctr++;
			}
		} catch(NullPointerException npe) {
			//
		} finally {
			in2.close();
		}
		/*
		for (int i = 0; i < classList.size(); i++) {
			System.out.println(classList.get(i));
		}
		*/
		return Y;
	}
	
	private Matrix scalefeatures(Matrix X) {
		Matrix scaledX = new Matrix(X.getRowDimension(), X.getColumnDimension());
		double scaledVal = 0;
		for (int i = 0; i < scaledX.getRowDimension(); i++) {
			for (int j = 0; j < scaledX.getColumnDimension(); j++) {
				if (j == 0) {
					scaledX.set(i, j, 1);
				} else {
					scaledVal = (X.get(i, j) - mean(X, j)) / standardDev(X, j);
					scaledX.set(i, j, scaledVal);
				}
			}
		}
		return scaledX;
	}
	
	private double mean(Matrix X, int colNum) {
		double mean = 0;
		for (int i = 0; i < X.getRowDimension(); i++) {
			mean += X.get(i, colNum);
		}
		mean = mean / X.getRowDimension();
		return mean;
	}
	
	private double standardDev(Matrix X, int colNum) {
		double sd = 0;
		double currSubMean;
		for (int i = 0; i < X.getRowDimension(); i++) {
			currSubMean = X.get(i, colNum) - mean(X, colNum);
			sd = sd + (Math.pow(currSubMean, 2));
		}
		sd = sd / X.getRowDimension();
		sd = Math.sqrt(sd);
		return sd;
	}
	
	public static void main(String[] args) {
		String inputFile = "irisflowers.csv";
		Requioma2 r = new Requioma2();
		try {
			Matrix X = r.load(inputFile);
			double mean = r.mean(X, 1);
			double sd = r.standardDev(X, 1);
			X.print(1, 1);
			System.out.println("mean: " + mean);
			System.out.println("sd: " + sd);
			Matrix scaledX = r.scalefeatures(X);
			scaledX.print(1, 1);
			Matrix Y = r.loadY(inputFile);
			Y.print(1, 1);
			//System.out.println(X.get(0, 2));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
