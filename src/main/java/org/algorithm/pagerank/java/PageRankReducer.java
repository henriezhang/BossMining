package org.algorithm.pagerank.java;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PageRankReducer extends Reducer<Text, Text, Text, Text> {
	private static final Log log = LogFactory.getLog(PageRankReducer.class);

	public static float factor = 0.85f;// 阻尼因子

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		// 获取阻尼因子值，默认0.85
		factor = context.getConfiguration().getFloat("mapred.pagerank.factor",
				0.85f);
	}

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		log.info(key.toString());
		float rank = 1 - factor;// PageRank值
		String[] str;
		Text outLinks = new Text();// 记录该链接的所有出链信息
		// 集合的数据位key的所有入链链接的page,rank,count值，以及key的所有出链信息
		for (Text t : values) {
			// 入链信息以';'分割，出链信息以','分割，以此区别
			str = t.toString().split(";");
			if (str.length == 3) {
				// 计算key的rank值=(1-d)+d*key的入链rank值/其出链数
				rank += Float.parseFloat(str[1]) / Integer.parseInt(str[2])
						* factor;
			} else {
				outLinks.set(t.toString());
			}
		}
		context.write(new Text(key.toString() + "," + rank), outLinks);
	}
}
