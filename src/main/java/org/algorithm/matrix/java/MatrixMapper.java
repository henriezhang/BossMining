package org.algorithm.matrix.java;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class MatrixMapper extends
		Mapper<LongWritable, Text, MatrixPair, IntWritable> {
	public static final Log log = LogFactory.getLog(MatrixMapper.class);
	public Matrix[][] cc = null;// 用于存放结果矩阵(c[n][n])

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
		/**
		 * 这一步我们用于实例化出所有的结果矩阵对象(c[n][n])， 用于跟a,b矩阵节点相结合作为reduce的输入
		 */
		// 矩阵的大小，即结果矩阵的大小
		int size = context.getConfiguration().getInt("mapred.matrix.size", 0);
		cc = new Matrix[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				cc[i][j] = new Matrix(MatrixType.RESULT, i + 1, j + 1);// c(ij)
			}
		}
	}

	/**
	 * 输入的key无意义，而value是文件中的一行。 行中每个数值以'\t'分割，且第一个必须为行号(从1开始)，
	 * 后面是每一行的矩阵点。对value按'\t'分割得到行号以及矩阵值， 据此构建矩阵对象a[i][j]或者b[x][y]。
	 * 输出的key是一对矩阵对象组成的对象，这个对象的第一个矩阵对象m1是 结果矩阵(c)，而第二个矩阵对象是初始矩阵(a/b)。
	 * 这里的设计思想是：c[i][j]=sum(a[i][x]*b[x][j])(x=1->n)，
	 * 所以我们将决定某个c[i][j]值的所有a[i][x]和b[x][j]同c[i][j]放到一起作为key传输到reduce，
	 * 然后在分组时我们只根据key的第一个矩阵对象(m1)来进行分组，
	 * 使得所有用来计算某一个c[i][j]值的所有a[i][x]和b[x][j]放到一起由reduce处理，
	 * 这样我们可以在reduce的一次遍历中计算出c
	 * [i][j]对象的值，这里使用到一个分组比较器(MatrixPairGroupComparator)。
	 * 这样虽然我们可以将计算c[i][j]值的所有需要的矩阵节点全部或得到了，但是计算相对还是比较麻烦的。因此我们还需要排序。
	 * 通过上面的计算公式，我们得知a[i][x]*b[x][j]即a的列=b的行，因此我们可以在排序时根据a的列和b的行进行排序，
	 * 这样得出的排序结果就是a[i][1],b[1][j],a[i][2],b[2][j],a[i][3],b[3][j]，
	 * 这样的排序结果我们只需要将前面一个乘上后面一个值求和即可得到c[i][j]的值。
	 * 想要实现这种排序我们需要自定义排序规则，这个规则定义在MatrixPair中的compareTo方法中，
	 * 在reduce进行sort时会自动调用该方法进行排序。 以上就是设计思路。
	 */
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		int flag;
		FileSplit sp = (FileSplit) context.getInputSplit();
		String s = sp.getPath().getName();
		// 我们需要区分是a矩阵和b矩阵
		// 区分规则是分片文件的文件名若包含'1'则说明是a矩阵，否则是b矩阵
		// 所以输出文件也需要有一定规则，即a矩阵的文件名需包含1，而b矩阵不可包含1
		if (s.contains("1")) {
			flag = MatrixType.LEFT;//a
		} else {
			flag = MatrixType.RIGHT;//b
		}

		String line = value.toString();
		String[] ms = line.split("\t");
		int i = Integer.parseInt(ms[0]);// 行号,每行的第一个值
		for (int j = 1; j < ms.length; j++) {// 列号
			int v = 0;// 值
			try {
				v = Integer.parseInt(ms[j]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.exit(1);
			}
			Matrix aa = new Matrix(flag, i, j);// a(ij)，获得一个矩阵对象
			for (int x = 1; x < ms.length; x++) {
				MatrixPair p = null;
				if (flag == MatrixType.LEFT) {//根据判断是a还是b以和不同的c相结合
					p = new MatrixPair(cc[i - 1][x - 1], aa);
				} else {
					p = new MatrixPair(cc[x - 1][j - 1], aa);
				}
				log.info(p + "\t" + v);
				context.write(p, new IntWritable(v));
			}
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		super.cleanup(context);
		cc = null;
	}
}
