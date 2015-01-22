package com.mycanopy;

/**
 * Created by henriezhang on 2015/1/6.
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class CanopyDriver extends Configured implements Tool {
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new CanopyDriver(), args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Lack args");
            System.out.println("Usage: jar class inDataPath outBasePath attrNum T1 T2 [reduceNum]");
        }

        String inDataPath = args[0];
        String outBasePath = args[1];
        int attrNum = 16; // 数据属性的个数
        try {
            attrNum = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.out.print("获取数据属性个数错误");
            System.exit(2);
        }
        String T1 = args[3];
        String T2 = args[4];

        int reduceNum = 200;
        if (args.length >= 6) {
            try {
                reduceNum = Integer.parseInt(args[5]);
            } catch (Exception e) {
            }
        }

        Configuration conf = new Configuration();
        conf.set("canopy.attrnum", ""+attrNum);
        conf.set("canopy.t1", T1);
        conf.set("canopy.t2", T2);

        Job job = new Job(conf, "Canopy");//建立KMeans的MapReduce作业
        job.setJobName("Canopy");
        job.setJarByClass(CanopyDriver.class);//设定作业的启动类

        job.setMapperClass(CanopyMapper.class);
        job.setCombinerClass(CanopyCombiner.class);
        job.setReducerClass(CanopyReducer.class);
        job.setNumReduceTasks(reduceNum);

        //job.setReducerClass(Reducer.class);
        //job.setNumReduceTasks(0);

        // 设置Map输出的key和value的类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(CanopyItem.class);

        // 设置Reduce输出的key和value的类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // 设置输入路径
        FileSystem fs = FileSystem.get(conf);
        FileInputFormat.addInputPath(job, new Path(inDataPath));

        // 设置输出路径
        String outCenterPath = outBasePath + "/centers";
        fs.delete(new Path(outCenterPath), true); //fs.delete是将已存在的输出目录删除
        FileOutputFormat.setOutputPath(job, new Path(outCenterPath));

        if (job.waitForCompletion(true)) {
            System.err.println("run task failed");
            return 0;
        } else {
            System.err.println("run task failed");
            return 1;
        }
    }
}