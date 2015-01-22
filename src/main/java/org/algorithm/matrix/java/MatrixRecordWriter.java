package org.algorithm.matrix.java;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class MatrixRecordWriter extends RecordWriter<Matrix, IntWritable> {
	public static final Log log = LogFactory.getLog(MatrixRecordWriter.class);

	protected DataOutputStream out;
	private static final String utf8 = "UTF-8";
	private static final byte[] newline;// 换行符
	private final byte[] separator = "\t".getBytes();// 分隔符
	private Matrix last = null;// 保存上一个矩阵节点信息，判断是否需要换行

	static {
		try {
			newline = "\n".getBytes(utf8);
		} catch (UnsupportedEncodingException uee) {
			throw new IllegalArgumentException("can't find " + utf8
					+ " encoding");
		}
	}

	public MatrixRecordWriter(DataOutputStream out) {
		this.out = out;
	}

	/**
	 * 该方法将矩阵按顺序写入输出文件，同时确保能够适时的换行。
	 * 具体操作方法是每次调用该方法时都保存参数key，然后再下次调用该方法时判断key的行与保存的key的行是否相同，
	 * 不同则换行。输出结果以'\t'分割。
	 * 这里有一点特别需要注意：本来应该可以很简单的使用last=key来完成保存key的方法，但是实验发现，如果使用这种
	 * 方法保存key，则在下次调用该方法时last值自动变成了传入的参数key，即使我们每调用last=key。
	 * 我能想到的解释是这里的key使用的是引用传递，当我们调用last=key的时候就将last的引用指向key，所以每当key改变之后，
	 * last也就立即改变了，使得每次last的行号总是等于key的行号，无法实现换行。
	 * 最终解决办法是根据key的三个参数值来new一个Matrix对象，作为last，而之后每次调用该方法时只是将last对象的三个参数
	 * 值根据key的值进行更新操作，同时该方法也节约了不少内存的使用（少new了很多对象）
	 */
	public synchronized void write(Matrix key, IntWritable value)
			throws IOException {
		if (null != last) {
			if (key.getScript1().get() == last.getScript1().get()) {// 行标一样，不换行
				out.write(separator);
			} else {// 换行
				out.write(newline);
			}
		} else {
			try {
				last = (Matrix) key.clone();//该方法根据key的值，new一个Matrix对象
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		last.update(key);//根据key的值更新last对象的值
		out.write(String.valueOf(value.get()).getBytes());
	}

	public synchronized void close(TaskAttemptContext context)
			throws IOException {
		out.close();
	}
}
