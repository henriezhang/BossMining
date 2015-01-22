package org.algorithm.matrix.java;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;

/**
 * 矩阵点
 * 
 * @author lk
 * 
 */
public class Matrix implements WritableComparable<Matrix>, Cloneable {
	private IntWritable flag;// 矩阵标示：1-left;2-right;3-result
	private IntWritable script1;// 行下标（从1开始）
	private IntWritable script2;// 列下标（从1开始）

	public Matrix() {
		this(new IntWritable(), new IntWritable(), new IntWritable());
	}

	public Matrix(int flag, int script1, int script2) {
		this(new IntWritable(flag), new IntWritable(script1), new IntWritable(
				script2));
	}

	public Matrix(IntWritable flag, IntWritable script1, IntWritable script2) {
		super();
		this.flag = flag;
		this.script1 = script1;
		this.script2 = script2;
	}

	public IntWritable getFlag() {
		return flag;
	}

	public void setFlag(IntWritable flag) {
		this.flag = flag;
	}

	public IntWritable getScript1() {
		return script1;
	}

	public void setScript1(IntWritable script1) {
		this.script1 = script1;
	}

	public IntWritable getScript2() {
		return script2;
	}

	public void setScript2(IntWritable script2) {
		this.script2 = script2;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		flag.readFields(in);
		script1.readFields(in);
		script2.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		flag.write(out);
		script1.write(out);
		script2.write(out);
	}

	@Override
	public int compareTo(Matrix o) {
		if (flag.equals(o.getFlag())) {
			if (script1.get() > o.getScript1().get()) {
				return 1;
			} else if (script1.get() < o.getScript1().get()) {
				return -1;
			} else {
				if (script2.get() > o.getScript2().get()) {
					return 1;
				} else if (script2.get() < o.getScript2().get()) {
					return -1;
				} else {
					return 0;
				}
			}
		} else {
			return flag.compareTo(o.getFlag());
		}
	}

	@Override
	public String toString() {
		return "Matrix [name=" + flag.get() + ", script1=" + script1.get()
				+ ", script2=" + script2.get() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flag == null) ? 0 : flag.hashCode());
		result = prime * result + ((script1 == null) ? 0 : script1.hashCode());
		result = prime * result + ((script2 == null) ? 0 : script2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Matrix other = (Matrix) obj;
		if (flag == null) {
			if (other.flag != null)
				return false;
		} else if (!flag.equals(other.flag))
			return false;
		if (script1 == null) {
			if (other.script1 != null)
				return false;
		} else if (!script1.equals(other.script1))
			return false;
		if (script2 == null) {
			if (other.script2 != null)
				return false;
		} else if (!script2.equals(other.script2))
			return false;
		return true;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Matrix(this.flag.get(), this.script1.get(),
				this.script2.get());
	}

	public void update(Matrix m) {
		flag.set(m.getFlag().get());
		script1.set(m.getScript1().get());
		script2.set(m.getScript2().get());
	}
}
