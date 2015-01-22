package org.algorithm.matrix.java;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;

public class MatrixPair implements WritableComparable<MatrixPair> {
	private Matrix m1;// 计算后的矩阵点(c)
	private Matrix m2;// 计算前的矩阵点(a/b)

	public MatrixPair() {
		this(new Matrix(), new Matrix());
	}

	public MatrixPair(Matrix m1, Matrix m2) {
		super();
		this.m1 = m1;
		this.m2 = m2;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		m1.readFields(in);
		m2.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		m1.write(out);
		m2.write(out);
	}

	public Matrix getM1() {
		return m1;
	}

	public void setM1(Matrix m1) {
		this.m1 = m1;
	}

	public Matrix getM2() {
		return m2;
	}

	public void setM2(Matrix m2) {
		this.m2 = m2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m1 == null) ? 0 : m1.hashCode());
		result = prime * result + ((m2 == null) ? 0 : m2.hashCode());
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
		MatrixPair other = (MatrixPair) obj;
		if (m1 == null) {
			if (other.m1 != null)
				return false;
		} else if (!m1.equals(other.m1))
			return false;
		if (m2 == null) {
			if (other.m2 != null)
				return false;
		} else if (!m2.equals(other.m2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MatrixPair [m1=" + m1 + ", m2=" + m2 + "]";
	}

	@Override
	/**
	 * 先按cc(ij)排序，然后按aa(ix)的j和bb(xj)的i排序，可以保证aa(ix)和bb(xj)在一起
	 */
	public int compareTo(MatrixPair o) {
		int result = m1.compareTo(o.getM1());
		if (result == 0) {
			if (m2.getFlag() == o.getM2().getFlag()) {
				return m2.compareTo(o.getM2());
			}
			IntWritable f1;
			IntWritable f2;
			if (m2.getFlag().get() == MatrixType.LEFT) {// aa
				f1 = m2.getScript2();// j
			} else if (m2.getFlag().get() == MatrixType.RIGHT) {// bb
				f1 = m2.getScript1();// i
			} else {
				f1 = new IntWritable(0);
			}
			if (o.getM2().getFlag().get() == MatrixType.LEFT) {// aa
				f2 = o.getM2().getScript2();// j
			} else if (o.getM2().getFlag().get() == MatrixType.RIGHT) {// bb
				f2 = o.getM2().getScript1();// i
			} else {
				f2 = new IntWritable(0);
			}
			return f1.compareTo(f2) == 0 ? m2.getFlag().compareTo(
					o.getM2().getFlag()) : f1.compareTo(f2);
		}

		return result;
	}
}
