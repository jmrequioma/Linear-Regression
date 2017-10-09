import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Jama.Matrix;

public class Requioma2 {
	int numRows;
	int numCols;
	int numFeatures;
	int degMatrixRow;
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
		numFeatures = numCols - 1;
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
	
	private Matrix degreeMatrix(int degree) {
		int ctr = 1;
		int ctr2 = 1;
		int base = degree + 1;
		int inc = 0;
		Matrix dMatrix = new Matrix((int) Math.pow(base, numFeatures), numFeatures);
		for (int j = numFeatures - 1; j >= 0; j--) {
			for (int i = 0; i < (int) Math.pow(base, numFeatures); i++) {
				System.out.println("ctr: " + ctr);
				dMatrix.set(i, j, inc);
				if (ctr2 == ctr) {
					if (inc == degree) {
						inc = 0;
					} else {
						inc++;
					}
					ctr2 = 0;
				}
				ctr2++;
			}
			ctr = ctr * (degree + 1);
		}
		degMatrixRow = (int) (Math.pow(base, numFeatures));
		return dMatrix;
	}
	
	private ArrayList<Double> degContainer(Matrix degMatrix, int rowSeq) {
		ArrayList<Double> valHolder = new ArrayList<Double>();
		for (int i = 0; i < numFeatures - 1; i++) {
			valHolder.add(degMatrix.get(rowSeq, i));
		}
		return valHolder;
	}
	
	private Matrix engineerPolynomials(Matrix X,int degree) {
		double value = 1;
		Matrix degreeMatrix = degreeMatrix(degree);
		Matrix polyX = new Matrix(X.getRowDimension(), degreeMatrix.getRowDimension());
		for (int h = 0; h < degreeMatrix.getRowDimension(); h++) {
			//ArrayList<Double> valHolder = degContainer(degreeMatrix, h);
			for (int i = 0; i < X.getRowDimension(); i++) {
				for (int j = 1; j < X.getColumnDimension(); j++) {
					value *= Math.pow(X.get(i, j), degreeMatrix.get(h, j - 1));
				}
				polyX.set(i, h, value);
				value = 1;
			}
		}
		return polyX;
	}
	
	private Matrix regularizedCost(Matrix polyX, Matrix Y, Matrix theta, double lambda) {
		double cost = 0;
		double cost2 = 0;
		Matrix prodMat = polyX.times(theta);
		Matrix sigmoidMat = sigmoid(prodMat);
		sigmoidMat.print(8, 8);
		Matrix regularizedCost = new Matrix(1, sigmoidMat.getColumnDimension());
		for (int k = 0; k < sigmoidMat.getColumnDimension(); k++) {
			for (int i = 0; i < sigmoidMat.getRowDimension(); i++) {
				//for (int j = 0; j < sigmoidMat.getColumnDimension(); j++) {
				Double check = Math.log((1 - sigmoidMat.get(i, k)));
				if (check.isNaN() || check.isInfinite()) {
					check = (double) 0;
				}
					cost += (Y.get(i, k) * Math.log(sigmoidMat.get(i, k))) + ((1 - Y.get(i, k)) * check);
					//System.out.println("loop number " + k + ": " + cost);
				//}
			}
			//System.out.println("k: " + k);
			cost = cost / (-1 * sigmoidMat.getRowDimension());
			//System.out.println("final1: " + cost);
			regularizedCost.set(0, k, cost);
			cost = 0;
		}
		Matrix finCost = regularizedCost.plus(secondHalf(theta, lambda));
		return finCost;
	}
	
	private Matrix secondHalf(Matrix theta, double lambda) {
		double val = 0;
		Matrix secondHalf = new Matrix(1, theta.getColumnDimension());
		double cost = 0;
		for (int k = 0; k < theta.getColumnDimension(); k++) {
			for (int i = 0; i < theta.getRowDimension(); i++) {
				//for (int j = 0; j < sigmoidMat.getColumnDimension(); j++) {
					cost += Math.pow(theta.get(i, k), 2);
					//System.out.println(cost);
				//}
			}
			cost = (cost * lambda) / (2 * theta.getRowDimension());
			//System.out.println("final: " + cost);
			secondHalf.set(0, k, cost);
			cost = 0;
		}
		secondHalf.print(1, 1);
		return secondHalf;
	}
	
	private Matrix sigmoid(Matrix prodMat) {
		double value = 0;
		Matrix sigmoidMat = new Matrix(prodMat.getRowDimension(), prodMat.getColumnDimension());
		for (int i = 0; i < prodMat.getRowDimension(); i++) {
			for (int j = 0; j < prodMat.getColumnDimension(); j++) {
				value = 1 / (1 + Math.pow(Math.E, (prodMat.get(i, j) * -1)));
				sigmoidMat.set(i, j, value);
			}
		}
		return sigmoidMat;
	}
	
	public static void main(String[] args) {
		String inputFile = "irisflowers.csv";
		Requioma2 r = new Requioma2();
		try {
			Matrix X = r.load(inputFile);
			double mean = r.mean(X, 1);
			double sd = r.standardDev(X, 1);
			//X.print(1, 1);
			System.out.println("mean: " + mean);
			System.out.println("sd: " + sd);
			Matrix scaledX = r.scalefeatures(X);
			Matrix polyX = r.engineerPolynomials(scaledX, 1);
			//polyX.print(8, 8);
			//scaledX.print(1, 1);
			Matrix Y = r.loadY(inputFile);
			Matrix degreeMatrix = r.degreeMatrix(1);
			//degreeMatrix.print(1, 1);
			Y.print(1, 1);
			Matrix theta = new Matrix(r.degMatrixRow, Y.getColumnDimension());
			for (int i = 0; i < theta.getRowDimension(); i++) {
				for (int j = 0; j < theta.getColumnDimension(); j++) {
					theta.set(i, j, 1);
				}
			}
			Matrix prodMat = r.regularizedCost(polyX, Y, theta, 0.001);
			//System.out.println(X.get(0, 2));
			prodMat.print(8, 8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
