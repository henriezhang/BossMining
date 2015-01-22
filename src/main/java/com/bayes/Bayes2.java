package com.bayes;

/**
 * Created by henriezhang on 2015/1/6.
 */
/**
 * 找到抽样用户的特征,得到每个特征的概率
 * 输入：属性1   属性2   属性3   属性4    类别
 * 命令：hadoop jar recommend_cf.jar com.funshion.machine.bayes.Bayes2 /dw/logs/user/xincl/bayes.txt /dw/logs/recommend/result/machine/Bayes2
 * 输出：
 * @author clxin
 *
 */

/*
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.IntPairWritable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Bayes2 extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Bayes2(), args);
        System.exit(exitCode);
    }

    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        JobConf jobConf = new JobConf(conf, Bayes2.class);
        jobConf.setJobName("Bayes2");
        jobConf.setNumReduceTasks(1);

        jobConf.setMapOutputKeyClass(StringSecondSortAsce.class);
        jobConf.setMapOutputValueClass(Text.class);

        jobConf.setOutputKeyClass(Text.class);
        jobConf.setOutputValueClass(Text.class);

        jobConf.setMapperClass(BayesMapper.class);
        jobConf.setReducerClass(BayesReducer.class);

        jobConf.setInputFormat(TextInputFormat.class);
        jobConf.setOutputFormat(TextOutputFormat.class);
        jobConf.setPartitionerClass(FirstPartitioner.class);
        jobConf.setOutputValueGroupingComparator(IntPairWritable.FirstGroupingComparator.class);

        FileInputFormat.addInputPath(jobConf, new Path(args[0]));
        FileOutputFormat.setOutputPath(jobConf, new Path(args[1]));

        JobClient.runJob(jobConf);
        return 0;
    }

    // 输出 ： mac media
    public static class BayesMapper extends MapReduceBase implements
            Mapper<LongWritable, Text, StringSecondSortAsce, Text> {
        StringSecondSortAsce tKey = new StringSecondSortAsce();
        Text tValue = new Text();

        @Override
        public void map(LongWritable key, Text value,
                        OutputCollector<StringSecondSortAsce, Text> output,
                        Reporter report) throws IOException {
            String[] strArr = value.toString().split("\t");
            for (int i = 0; i < strArr.length - 1; i++) {
                tKey.set(i + "\t" + strArr[strArr.length - 1], strArr[i]);
                tValue.set(strArr[i]);
                output.collect(tKey, tValue);
            }
        }
    }

    // 输入：mac    tag1         观影次数
    public static class BayesReducer extends MapReduceBase implements
            Reducer<StringSecondSortAsce, Text, Text, Text> {
        int count = 0;
        Text tKey = new Text();
        Text tValue = new Text();

        @Override
        public void reduce(StringSecondSortAsce key, Iterator<Text> values,
                           OutputCollector<Text, Text> output, Reporter report)
                throws IOException {
            int pCcount = 1;
            int pXcount = 1;
            Map xMap = new HashMap<String, String>();

            String tmpValue = values.next().toString();
            while (values.hasNext()) {
                pCcount++;
                String newValue = values.next().toString();
                if (!tmpValue.equals(newValue)) {
                    xMap.put(tmpValue, pXcount);
                    tmpValue = newValue;
                    pXcount = 1;
                } else {
                    pXcount++;
                }
            }
            xMap.put(tmpValue, pXcount);

            Set<Map.Entry<String, String>> sets = xMap.entrySet();
            for (Map.Entry<String, String> entry : sets) {

                String[] xValue = key.getFirst().split("\t");
                tKey.set("p(" + xValue[0] + "=" + entry.getKey() + "|" + "class=" + xValue[1] +
                        ")");
                Object ob = entry.getValue();
                tValue.set(String.valueOf(1.0 * Integer.parseInt(ob.toString()) / pCcount));

                output.collect(tKey, tValue);
            }
        }
    }

}*/
