package com.mykmeans;

/**
 * Created by henriezhang on 2015/1/6.
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class KmeansDriver extends Configured implements Tool {
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new KmeansDriver(), args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Lack args");
            System.out.println("Usage: jar class centerPath inDataPath outBasePath attrNum [reduceNum]");
        }

        // 获取参数
        String centerPath = args[0];
        String inDataPath = args[1];
        String outBasePath = args[2];
        int attrNum = 0; // 数据属性的个数
        try {
            attrNum = Integer.parseInt(args[3]);
        } catch (Exception e) {
            System.out.print("获取数据属性个数错误");
            System.exit(2);
        }
        int reduceNum = 200;
        if (args.length >= 5) {
            try {
                reduceNum = Integer.parseInt(args[4]);
            } catch (Exception e) {
            }
        }
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        KmeansCenters centers = new KmeansCenters(attrNum);
        if (!centers.readCentersFromFile(centerPath)) {
            System.out.println("read mediums failed");
            System.exit(10);
        }

        // 迭代计算出聚类中心
        int step = 1, maxStep = 10;
        double shold = 0.1;//shold是阈值
        for (step = 1; step <= maxStep; step++) {
            String inCenterPath = centerPath;
            if (step > 1) {
                inCenterPath = outBasePath + "/step_" + (step - 1) + "/centers";
            }
            if (!centers.readCentersFromFile(inCenterPath)) {
                System.out.println("Read centers from init file failed!");
                return 1;
            }
            conf.set("kmeans.center", inCenterPath);
            conf.set("kmeans.attrnum", ""+attrNum);

            Job job = new Job(conf, "KMeans");//建立KMeans的MapReduce作业
            job.setJobName("Kmeans");
            job.setJarByClass(KmeansDriver.class);//设定作业的启动类
            job.setNumReduceTasks(reduceNum);

            job.setMapperClass(KmeansMapper.class);
            job.setReducerClass(KmeansReducer.class);

            // 设置Map输出的key和value的类型
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(KmeansItem.class);

            // 设置Reduce输出的key和value的类型
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);

            // 设置输入路径
            FileInputFormat.addInputPath(job, new Path(inDataPath));

            // 设置输出路径
            String outCenterPath = outBasePath + "/step_" + step + "/centers";
            fs.delete(new Path(outCenterPath), true);//fs.delete是将已存在的输出目录删除
            FileOutputFormat.setOutputPath(job, new Path(outCenterPath));

            // 运行作业并判断是否完成成功
            if (!job.waitForCompletion(true)) {
                //上两个中心点做比较，
                KmeansCenters newCenters = new KmeansCenters(attrNum);
                if (!newCenters.readCentersFromFile(outCenterPath)) {
                    System.out.println("Read centers from mid file failed! step=" + step);
                    return 1;
                }

                // 如果中心点之间的距离小于阈值就停止；如果距离大于阈值，就把最近的中心点作为新中心点
                double changing = centers.changing(newCenters);
                if (changing < shold) {
                    System.err.println("Changing is less then shold, break!");
                    break;
                } else { // 更新新中心点
                    System.err.println("Changing is bigger then shold, continue!");
                    centers = newCenters;
                }
            } else {
                System.err.println("run task failed");
                System.exit(11);
            }
        }

        // 稳定中心后对每个项目进行划分
        String inCenterPath = outBasePath + "/step_" + step + "/centers";
        conf.set("kmeans.center", inCenterPath);

        Job job = new Job(conf, "KMeansFinal");//建立KMeans的MapReduce作业
        job.setJobName("KmeansFinal");
        job.setJarByClass(KmeansDriver.class);//设定作业的启动类
        job.setNumReduceTasks(reduceNum);

        job.setMapperClass(KmeansFinalMapper.class);
        job.setReducerClass(KmeansFinalReducer.class);

        // 设置Map输出的key和value的类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        // 设置Reduce输出的key和value的类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // 设置输入路径
        FileInputFormat.addInputPath(job, new Path(inDataPath));

        // 设置输出路径
        String outCenterPath = outBasePath + "/step_final/clusters";
        fs.delete(new Path(outCenterPath), true);//fs.delete是将已存在的输出目录删除
        FileOutputFormat.setOutputPath(job, new Path(outCenterPath));

        // 运行作业并判断是否完成成功
        if (!job.waitForCompletion(true)) {
            System.out.println("Exec last kmeans failed!");
            return 11;
        } else {
            System.out.println("Exec last kmeans succeed!");
            return 0;
        }
    }
}