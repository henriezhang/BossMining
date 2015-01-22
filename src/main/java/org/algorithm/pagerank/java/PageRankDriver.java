package org.algorithm.pagerank.java;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;

public class PageRankDriver extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		if (args.length < 3) {
			System.err.println("parameter num is not right...");
			return -1;
		}

		Job job = new Job(getConf(), "pagerank job_" + args[0]);
		job.setJarByClass(this.getClass());

		Configuration conf = job.getConfiguration();

		Path input = new Path(args[1]);
		Path output = new Path(args[2]);

		// 输出路径存在，则删除
		FileSystem fs = FileSystem.get(conf);
		if (fs.exists(output)) {
			fs.delete(output, true);
		}

		KeyValueTextInputFormat.addInputPath(job, input);
		TextOutputFormat.setOutputPath(job, output);

		job.setInputFormatClass(KeyValueTextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		job.setMapperClass(PageRankMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setReducerClass(PageRankReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		return job.waitForCompletion(true) ? 1 : 0;
	}

}
