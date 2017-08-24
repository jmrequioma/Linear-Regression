import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Jama.Matrix;

public class Requioma {
	final String IN_FILE = "HousePricingRelationship.in";
	int numRows;
	int numCols;
	Matrix X;
	
	public Requioma() throws IOException {
		int numRows = 0;
		int numCols = 0;
		String line;
		BufferedReader in = new BufferedReader(new FileReader(IN_FILE));
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
		System.out.println("Number of Rows: " + numRows);
		System.out.println("Number of Columns: " + numCols);
		X = new Matrix(numRows, numCols);
	}
	
	private void load_data() throws IOException {
		String line;
		int ctr = 0;
		for (int i = 0; i < numRows; i++) {
			X.set(i, 0, 1);
		}
		
		BufferedReader in = new BufferedReader(new FileReader(IN_FILE));
		try {
			while ((line = in.readLine()) != null) {
				String[] values = line.split(",");
				for (int j = 0; j < numRows; j++) {
					if (ctr > 0) {
						line = in.readLine();
						values = line.split(",");
					}
					ctr++;
					for (int i = 1; i < numCols; i++) {
						int intVal = Integer.parseInt(values[i - 1]);
						X.set(j, i, intVal);
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
	}
	
	
	public static void main(String[] args) {
		Requioma r;
		try {
			r = new Requioma();
			
			System.out.println("Number of r Rows: " + r.numRows);
			System.out.println("Number of r Columns: " + r.numCols);
			//r.displayMatrix();
			r.load_data();
			r.displayMatrix();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
