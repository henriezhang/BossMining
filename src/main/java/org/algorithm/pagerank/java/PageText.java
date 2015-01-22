package org.algorithm.pagerank.java;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * 用于存放Page信息
 * 
 * @author lk
 * 
 */
public class PageText implements WritableComparable<PageText> {
	public Text page; // 页面信息
	public FloatWritable rank;// 该页面的page rank值
	private IntWritable count;// 该页面的出链数量

	public PageText() {
		this(new Text(), new FloatWritable(), new IntWritable());
	}

	public PageText(String page, float rank, int count) {
		this(new Text(page), new FloatWritable(rank), new IntWritable(count));
	}

	public PageText(Text page, FloatWritable rank, IntWritable count) {
		super();
		this.page = page;
		this.rank = rank;
		this.count = count;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		page.readFields(in);
		rank.readFields(in);
		count.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		page.write(out);
		rank.write(out);
		count.write(out);
	}

	@Override
	public int compareTo(PageText arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
