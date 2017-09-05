import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
		theta.set(0, 0, 0);
		theta.set(1, 0, 0);
		theta.set(2, 0, 0);
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
	
	private double computeSummationModified(Matrix summH, int colNo, double alpha) {
		double summation = 0;
		double cost = 0;
		for (int i = 0; i < numRows; i++) {
			summation = summation + (summH.get(i, 0) * X.get(i, colNo));
			//System.out.println("last value:" + X.get(i, colNo));
		}
		cost = (summation * alpha) / numRows;
		return cost;
	}
	
	private void iter() {
		Matrix thetaHist = new Matrix(theta.getRowDimension(), theta.getRowDimension());
		Matrix holder;
		for (int i = 0; i < 4; i++) {
			holder = test(thetaHist);
			thetaHist = holder;
		}
	}
	
	private void gradientDescent(Matrix X,Matrix y, double alpha,int iters) {
		Matrix iterMatrix = new Matrix(iters, numCols);
		ArrayList<Double> costHist = new ArrayList<Double>();
		double cost = 0;
		double newValue = 0;
		for (int i = 0; i < iters; i++) {
			for (int j = 0; j < numCols; j++) {
				if (i == 0) {
					Matrix h = X.times(theta);
					Matrix summH = h.minus(Y);
					newValue = theta.get(j, 0) - (computeSummationModified(summH, j, 0.00000001));
					//System.out.println("newValue: " + newValue);
					iterMatrix.set(0, j, newValue);
				} else {
					Matrix h = X.times(iterMatrix.getMatrix(i - 1, i - 1, 0, numCols - 1).transpose());
					Matrix summH = h.minus(Y);
					newValue = iterMatrix.get(i - 1, j) - (computeSummationModified(summH, j, 0.00000001));
					//System.out.println("newValue: " + newValue);
					iterMatrix.set(i, j, newValue);
				}
				Matrix hPerRow = X.times(iterMatrix.getMatrix(i, i, 0, numCols - 1).transpose());
				Matrix xSubtrY = hPerRow.minus(Y);
				cost = computeSummation(xSubtrY);
				costHist.add(cost);
			}
		}
		iterMatrix.print(0, 0);
		for (int i = 0; i < costHist.size(); i++) {
			System.out.println("costHist at index "+ i + " " + costHist.get(i));
		}
	}
	
	private Matrix test(Matrix thetaHist) {
		Matrix copyOfTheta = theta.copy();
		//Matrix thetaHist = new Matrix(theta.getRowDimension(), theta.getRowDimension());
		double newValue = 0;
		for (int j = 0; j < thetaHist.getColumnDimension(); j++) {
			Matrix h = X.times(copyOfTheta);
			Matrix summH = h.minus(Y);
			for (int i = 0; i < thetaHist.getRowDimension(); i++) {
				if (j == 0) {
					newValue = theta.get(i, 0) - (computeSummationModified(summH, j, 0.00000001));
					copyOfTheta.set(i, 0, newValue);
					thetaHist.set(i, j, newValue);
					
				} else {
					newValue = thetaHist.get(i, j - 1) - (computeSummationModified(summH, j, 0.00000001));
					copyOfTheta.set(i, 0, newValue);
					thetaHist.set(i, j, newValue);
				}
			}
		}
		System.out.println("theta hist:");
		thetaHist.print(0, 0);
		copyOfTheta.print(0, 0);
		return thetaHist;
	}
	
	public static void main(String[] args) {
		Requioma r;
		try {
			r = new Requioma("HousePricingRelationship.in");
			
			//System.out.println("Number of r Rows: " + r.numRows);
			//System.out.println("Number of r Columns: " + r.numCols);
			//r.displayMatrix();
			r.load_data("HousePricingRelationship.in");
			//r.displayMatrix();
			Matrix h = r.computeH();
			Matrix summH = r.subH(h);
			//System.out.println("summH:");
			//System.out.println("------------------------------------");
			//summH.print(0, 0);
			double cost = r.computeSummation(summH);
			double cost2 = r.cost(r.X, r.Y, r.theta);
			r.gradientDescent(r.X, r.Y, 0.00000001, 100);
			System.out.println("cost: " + cost2);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
