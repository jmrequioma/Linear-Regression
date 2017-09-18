import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Jama.Matrix;

public class Requioma2 {
	private Matrix load(String filename) throws IOException {
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
		Matrix X = new Matrix(numRows, numCols);
		return X;
	}
}
