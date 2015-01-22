package com.bak.kmeans;

/**
 * Created by henriezhang on 2015/1/6.
 */
/*==================cluster.txt===========================
A    2    2
B    2    4
C    4    2
D    4    4
E    6    6
F    6    8
G    8    6
H    8    8
==================cluster.center.conf===========================
K1    3    2
K2    6    2*/

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class Kmeans extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Kmeans(), args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        JobConf conf = new JobConf(getConf(), Kmeans.class);
        conf.setJobName("Kmeans");
        //conf.setNumMapTasks(200);

        // 设置Map输出的key和value的类型
        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(Text.class);

        // 设置Reduce输出的key和value的类型
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        // 设置Mapper和Reducer
        conf.setMapperClass(KmeansMapper.class);
        conf.setReducerClass(KmeansReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        // 设置输入输出目录
        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        JobClient.runJob(conf);
        return 0;
    }

    public static class KmeansMapper extends MapReduceBase implements
            Mapper<LongWritable, Text, Text, Text> {
        private DmRecordParser drp;
        private String clusterNode = "K";
        private DmRecord record0 = null;
        private DmRecord record1 = new DmRecord();
        private double Min_distance = 9999;
        private int tmpK = 0;
        private Text tKey = new Text();
        private Text tValue = new Text();

        //获取聚类中心坐标
        @Override
        public void configure(JobConf conf) {
            drp = new DmRecordParser();
            try {
                drp.initialize(new File("cluster.center.conf"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //根据聚类坐标，把文件中的点进行类别划分
        @Override
        public void map(LongWritable key, Text value,
                        OutputCollector<Text, Text> output, Reporter arg3)
                throws IOException {
            String[] strArr = value.toString().split("\t");

            for (int i = 1; i <= 2; i++) {
                record0 = drp.getUrlCode("K" + i);
                record1.setName(strArr[0]);
                record1.setXpoint(Double.parseDouble(strArr[1]));
                record1.setXpoint(Integer.parseInt(strArr[2]));

                if (record0.distance(record1) < Min_distance) {
                    tmpK = i;
                    Min_distance = record0.distance(record1);
                }
            }

            tKey.set("C" + tmpK);
            output.collect(tKey, value);
        }
    }

    //计算新的聚类中心
    public static class KmeansReducer extends MapReduceBase implements
            Reducer<Text, Text, Text, Text> {
        private Text tKey = new Text();
        private Text tValue = new Text();

        @Override
        public void reduce(Text key, Iterator<Text> value,
                           OutputCollector<Text, Text> output, Reporter arg3)
                throws IOException {
            double avgX = 0;
            double avgY = 0;
            double sumX = 0;
            double sumY = 0;
            int count = 0;
            String[] strValue = null;

            while (value.hasNext()) {
                count++;
                strValue = value.next().toString().split("\t");
                sumX = sumX + Integer.parseInt(strValue[1]);
                sumY = sumY + Integer.parseInt(strValue[1]);
            }

            avgX = sumX / count;
            avgY = sumY / count;
            tKey.set("K" + key.toString().substring(1, 2));
            tValue.set(avgX + "\t" + avgY);
            System.out.println("K" + key.toString().substring(1, 2) + "\t" + avgX + "\t" + avgY);
            output.collect(tKey, tValue);
        }
    }
}
