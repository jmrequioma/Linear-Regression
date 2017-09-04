import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Jama.Matrix;

public class Requioma {
	//final String IN_FILE = "HousePricingRelationship.in";
	int numRows;
	int numCols;
	Matrix X;
	Matrix Y;
	Matrix theta;
	
	public Requioma(String filename) throws IOException {
		int numRows = 0;
		int numCols = 0;
		String line;
		BufferedReader in = new BufferedReader(new FileReader(filename));
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
		this.numRows = numRows;
		this.numCols = numCols;
		//System.out.println("Number of Rows: " + numRows);
		//System.out.println("Number of Columns: " + numCols);
		X = new Matrix(numRows, numCols);
		Y = new Matrix(numRows, 1);
		theta = new Matrix(numCols, 1);
		//for (int i = 0; i < numCols; i++) {
		theta.set(0, 0, 100);
		theta.set(1, 0, 231);
		theta.set(2, 0, 698);
		//}
	}
	
	private void load_data(String filename) throws IOException {
		String line;
		int ctr = 0;
		for (int i = 0; i < numRows; i++) {
			X.set(i, 0, 1);
		}
		
		BufferedReader in = new BufferedReader(new FileReader(filename));
		try {
			while ((line = in.readLine()) != null) {
				String[] values = line.split(",");
				for (int j = 0; j < numRows; j++) {
					if (ctr > 0) {
						line = in.readLine();
						values = line.split(",");
					}
					ctr++;
					for (int i = 1; i <= numCols; i++) {
						int intVal = Integer.parseInt(values[i - 1]);
						if (i == numCols) {
							Y.set(j, 0, intVal);
						} else {
							X.set(j, i, intVal);
						}
					}
				}
			}
		} catch(NullPointerException npe) {
			//
		} finally {
			in.close();
		}
	}
	
	private Matrix makeX(int numRows, int numCols) {
		Matrix X = new Matrix(numRows, numCols);
		return X;
	}
	
	private void displayMatrix() {
		X.print(0, 0);
		Y.print(0, 0);
	}
	
	private Matrix computeH() {
		Matrix h = X.times(theta);
		return h;
	}
	
	private double cost(Matrix X, Matrix y, Matrix theta) {
		Matrix h = X.times(theta);
		Matrix summH = h.minus(y);
		double cost = computeSummation(summH);
		return cost;
	}
	private Matrix subH(Matrix h) {
		Matrix summH = h.minus(Y);
		return summH;
	}
	
	private double computeSummation(Matrix summH) {
		double summation = 0;
		double cost = 0;
		for (int i = 0; i < numRows; i++) {
			summation = summation + (Math.pow(summH.get(i, 0), 2));
		}
		cost = summation / (2 * numRows);
		return cost;
	}
	
	private double computeSummationModified(Matrix summH, int colNo, int alpha) {
		double summation = 0;
		double cost = 0;
		for (int i = 0; i < numRows; i++) {
			summation = summation + (summH.get(i, 0) * X.get(i, colNo));
		}
		cost = (summation * alpha) / numRows;
		return cost;
	}
	
	private void iter() {
		for (int i = 0; i < 4; i++) {
			test();
		}
	}
	
	private Matrix test() {
		Matrix h = X.times(theta);
		Matrix summH = h.minus(Y);
		Matrix copyOfTheta = theta.copy();
		copyOfTheta.print(0, 0);
		double newValue = 0;
		for (int i = 0; i < copyOfTheta.getRowDimension(); i++) {
			newValue = copyOfTheta.get(i, 0) - (computeSummationModified(summH, i, 6));
			copyOfTheta.set(i, 0, newValue);
			theta.set(i, 0, newValue);
		}
		copyOfTheta.print(0, 0);
		return copyOfTheta;
	}
	
	public static void main(String[] args) {
		Requioma r;
		try {
			r = new Requioma("HousePricingRelationship.in");
			
			//System.out.println("Number of r Rows: " + r.numRows);
			//System.out.println("Number of r Columns: " + r.numCols);
			//r.displayMatrix();
			r.load_data("HousePricingRelationship.in");
			r.displayMatrix();
			Matrix h = r.computeH();
			Matrix summH = r.subH(h);
			//System.out.println("summH:");
			//System.out.println("------------------------------------");
			//summH.print(0, 0);
			double cost = r.computeSummation(summH);
			double cost2 = r.cost(r.X, r.Y, r.theta);
			System.out.println("r.theta:");
			System.out.println("------------------------------------");
			r.theta.print(0, 0);
			System.out.println("copy of theta:");
			Matrix copy = r.test();
			copy.print(0, 0);
			r.iter();
			System.out.println("theta after iter:");
			System.out.println("------------------------------------");
			r.theta.print(0, 0);
			//System.out.println("cost: " + cost);
			System.out.println("cost: " + cost2);
			//h.print(0, 0);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
