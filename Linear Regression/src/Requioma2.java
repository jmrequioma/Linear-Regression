import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import Jama.Matrix;

public class Requioma2 {
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
		return X;
	}
	
	public static void main(String[] args) {
		String inputFile = "irisflowers.csv";
		Requioma2 r = new Requioma2();
		try {
			Matrix X = r.load(inputFile);
			X.print(0, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
