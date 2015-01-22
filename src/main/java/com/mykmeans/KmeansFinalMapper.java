package com.mykmeans;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by henriezhang on 2015/1/6.
 */
public class KmeansFinalMapper extends Mapper<LongWritable, Text, Text, Text> {

    private KmeansCenters centers = null;

    private int attrNum = 0;

    //获取聚类中心坐标
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = new Configuration();
        String centerPath = conf.get("kmeans.center");
        String numTmp = conf.get("kmeans.attrnum");
        try {
            attrNum = Integer.parseInt(numTmp);
            centers = new KmeansCenters(attrNum);
            centers.readCentersFromFile(centerPath);
        } catch (Exception e) {
            System.err.println("read center files failed");
            System.exit(12);
        }
    }

    //根据聚类坐标，把文件中的点进行类别划分
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {
        String val = value.toString();
        KmeansItem ki = new KmeansItem();
        if (ki.parseItem(val, this.attrNum)) {
            String nearstCenter = this.centers.statNearstCenter(ki);
            context.write(new Text(nearstCenter), new Text(ki.toString()));
        }
    }
}