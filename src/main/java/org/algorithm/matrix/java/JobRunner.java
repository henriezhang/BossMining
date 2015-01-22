package org.algorithm.matrix.java;

import org.apache.hadoop.util.ToolRunner;

public class JobRunner {
	public static void main(String[] args) {
		try {
			ToolRunner.run(new MatrixDriver(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
