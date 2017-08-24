import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Jama.Matrix;

public class Requioma {
	final String IN_FILE = "HousePricingRelationship.in";
	int numRows;
	int numCols;
	Matrix X = new Matrix(numRows, numCols);
	
	public Requioma() throws IOException {
		int numRows = 0;
		int numCols = 0;
		String line;
		BufferedReader in = new BufferedReader(new FileReader(IN_FILE));
		try {
			while ((line = in.readLine()) != null) {
				numRows++;
				String[] values = line.split(",");
				numCols = (values.length - 1);   // number of columns for matrix X
			}
		} catch(NullPointerException npe) {
			//
		} finally {
			in.close();
		}
		this.numRows = numRows;
		this.numCols = numCols;
		System.out.println("Number of Rows: " + numRows);
		System.out.println("Number of Columns: " + numCols);
	}
	
	private void load_data() throws IOException {
		int numRows = 0;
		int numCols = 0;
		String line;
		BufferedReader in = new BufferedReader(new FileReader(IN_FILE));
		try {
			while ((line = in.readLine()) != null) {
				String[] values = line.split(",");
				numCols = values.length;   // number of Columns
				//X.set(arg0, arg1, values[0]);
				numRows++;
			}
		} catch(NullPointerException npe) {
			//
		} finally {
			in.close();
		}
		System.out.println("Number of Rows: " + numRows);
		System.out.println("Number of Columns: " + numCols);
	}
	
	private Matrix makeX(int numRows, int numCols) {
		Matrix X = new Matrix(numRows, numCols);
		return X;
	}
	
	public static void main(String[] args) {
		Requioma r;
		try {
			r = new Requioma();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
