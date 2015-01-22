package org.algorithm.pagerank.java;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * PageRank的Mapper类，用于实现从输入文件读入所有网站的链接信息
 * 
 * @author lk
 * 
 */
public class PageRankMapper extends Mapper<Text, Text, Text, Text> {
	private static final Log log = LogFactory.getLog(PageRankMapper.class);

	public static float factor = 0.85f;// 阻尼因子

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		// 获取阻尼因子值，默认0.85
		factor = context.getConfiguration().getFloat("mapred.pagerank.factor",
				0.85f);
	}

	@Override
	protected void map(Text key, Text value, Context context)
			throws IOException, InterruptedException {
		log.info(key.toString() + ";" + value.toString());
		// 输入文件格式为：A b,c,d,...
		//这里判断该连接是否存在出链，不存在出链则直接跳过
		if (value.toString().length() == 0) {
			return;
		}
		// 即key为目标网站，value为A所有的出链，并以','分割
		String[] outLinks = value.toString().split(",");
		// 分割key，获取key的rank值，以','分割
		String[] link = key.toString().split(",");
		float rank = factor;
		if (link.length > 1) {
			rank = Float.parseFloat(link[1]);// 存在rank值，则取得，不存在则设为默认值：阻尼因子
		}
		int outLinkLen = outLinks.length;// A的出链数量
		// 遍历A所有的出链，输出格式：key-A的各个出链(b,c,d...);value-[A+','+rank+','+outLinkLen]
		// 得到每个链接的所有入链的PR值以及链接到该链接的链接的出链数
		for (String s : outLinks) {
			context.write(new Text(s), new Text(link[0] + ";" + rank + ";"
					+ outLinkLen));
		}
		// 还需要输出A的所有出链信息，以便进行下一次mapreduce任务的处理
		context.write(new Text(link[0]), value);
	}
}
