package org.algorithm.matrix.java;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class MatrixReducer extends
		Reducer<MatrixPair, IntWritable, Matrix, IntWritable> {
	public static final Log log = LogFactory.getLog(MatrixReducer.class);

	@Override
	/**
	 * 由于分组比较器和排序比较器的功劳，使得到reduce这一步，很简单。
	 * 首先我们只需要对value进行遍历，每逢双数就将前面一个值同改值进行相乘，最后求和即可。
	 * 而通过获得key的m1对象，即可获得最后的矩阵节点信息。
	 * 然后使用我们自定义的MatrixRecordWriter将结果写入文件中。
	 * 因为我们在排序时已按照了c的行和列进行排序，使得我们可以直接按顺序写入输出文件，
	 * 而同时可以保证结果矩阵的顺序不变，但是有一点是我们不知道如何换行，这里我自定义了一个MatrixRecordWriter对象，
	 * 用于完成换行操作。
	 */
	protected void reduce(MatrixPair key, Iterable<IntWritable> values,
			Context context) throws IOException, InterruptedException {
		int v = 0;
		int temp = 0;
		int index = 0;
		for (IntWritable i : values) {
			if (index % 2 == 1) {
				v += temp * i.get();
			} else {
				temp = i.get();
			}
			index++;
		}
		context.write(key.getM1(), new IntWritable(v));
	}

}
