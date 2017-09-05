import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import Jama.Matrix;

public class Requioma extends ApplicationFrame {
	//final String IN_FILE = "HousePricingRelationship.in";
	int numRows;
	int numCols;
	Matrix X;
	Matrix Y;
	Matrix theta;
	double alpha;
	int iters;
	
	public Requioma(String filename, String applicationTitle , String chartTitle, double alpha, int iters) throws IOException {
		super(applicationTitle);
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
		this.alpha = alpha;
		this.iters = iters;
		//System.out.println("Number of Rows: " + numRows);
		//System.out.println("Number of Columns: " + numCols);
		X = new Matrix(numRows, numCols);
		Y = new Matrix(numRows, 1);
		theta = new Matrix(numCols, 1);
		//for (int i = 0; i < numCols; i++) {
		theta.set(0, 0, 0);
		theta.set(1, 0, 0);
		theta.set(2, 0, 0);
		JFreeChart lineChart = ChartFactory.createLineChart(
		         chartTitle,
		         "Iterations","Cost",
		         createDataset(),
		         PlotOrientation.VERTICAL,
		         true,true,false);
		      ChartPanel chartPanel = new ChartPanel( lineChart );
		      chartPanel.setPreferredSize( new java.awt.Dimension( 1000 , 600 ) );
		      setContentPane( chartPanel );
		//}
	}
	
	private DefaultCategoryDataset createDataset() throws IOException {
		load_data("HousePricingRelationship.in");
		System.out.println("hi");
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		ArrayList<Double> costHist = gradientDescent(X, Y, alpha, iters);
		for (int i = 0; i < iters; i++) {
			dataset.addValue((Number) costHist.get(i) , "cost" , Integer.toString(i));
			System.out.println(costHist.get(i));
			System.out.println("hello");
		}
		return dataset;
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
	
	private ArrayList<Double> gradientDescent(Matrix X,Matrix y, double alpha,int iters) {
		Matrix iterMatrix = new Matrix(iters, numCols);
		ArrayList<Double> costHist = new ArrayList<Double>();
		double cost = 0;
		double newValue = 0;
		for (int i = 0; i < iters; i++) {
			for (int j = 0; j < numCols; j++) {
				if (i == 0) {
					Matrix h = X.times(theta);
					Matrix summH = h.minus(Y);
					newValue = theta.get(j, 0) - (computeSummationModified(summH, j, alpha));
					iterMatrix.set(0, j, newValue);
				} else {
					Matrix h = X.times(iterMatrix.getMatrix(i - 1, i - 1, 0, numCols - 1).transpose());
					Matrix summH = h.minus(Y);
					newValue = iterMatrix.get(i - 1, j) - (computeSummationModified(summH, j, alpha));
					//System.out.println("newValue: " + newValue);
					iterMatrix.set(i, j, newValue);
				}
				Matrix hPerRow = X.times(iterMatrix.getMatrix(i, i, 0, numCols - 1).transpose());
				Matrix xSubtrY = hPerRow.minus(Y);
				cost = computeSummation(xSubtrY);
				costHist.add(cost);
			}
		}
		//iterMatrix.print(0, 0);
		for (int i = 0; i < costHist.size(); i++) {
			System.out.println("costHist at index "+ i + " " + costHist.get(i));
		}
		return costHist;
	}
	
	public static void main(String[] args) {
		Requioma r;
		try {
			r = new Requioma("HousePricingRelationship.in", "Iterations Vs Cost", "Iterations Vs Cost", 0.00000001, 100);
			
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
			r.pack();
			RefineryUtilities.centerFrameOnScreen(r);
			r.setVisible(true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
