package com.mycanopy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by henriezhang on 2015/1/6.
 */
public class CanopyMapper extends Mapper<LongWritable, Text, Text, CanopyItem> {
    private int attrNum = 0;

    //获取聚类中心坐标
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        Configuration conf = context.getConfiguration();
        String numTmp = conf.get("canopy.attrnum");
        try {
            attrNum = Integer.parseInt(numTmp);
        } catch (Exception e) {
            System.err.println("read cattrNum failed");
            System.exit(12);
        }
    }

    //根据聚类坐标，把文件中的点进行类别划分
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {

        String val = value.toString();
        CanopyItem ci = new CanopyItem();
        if (ci.parseItem(val, this.attrNum)) {
            context.write(new Text("canopy"), ci);
        }
    }
}