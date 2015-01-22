package com.mykmeans;

import com.mycanopy.CanopyItem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by henriezhang on 2015/1/6.
 */
//计算新的聚类中心
public class KmeansFinalReducer extends Reducer<Text, Text, Text, Text> {

    public void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        Iterator<Text> iter = values.iterator();
        while (iter.hasNext()) {
            context.write(key, iter.next());
        }
    }

}
