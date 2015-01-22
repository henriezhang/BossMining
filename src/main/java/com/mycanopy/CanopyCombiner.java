package com.mycanopy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by henriezhang on 2015/1/6.
 */
//计算新的聚类中心
public class CanopyCombiner extends Reducer<Text, CanopyItem, Text, CanopyItem> {
    private int attrNum = 0;
    private double T1 = 3.0;
    private double T2 = 100.0;

    //获取聚类中心坐标
    protected void setup(Reducer.Context context) throws IOException, InterruptedException {
        super.setup(context);
        Configuration conf = context.getConfiguration();
        String numTmp = conf.get("canopy.attrnum");
        try {
            attrNum = Integer.parseInt(numTmp);
        } catch (Exception e) {
            System.err.println("Get attrnum failed!");
            System.exit(11);
        }

        try {
            T1 = Double.parseDouble(conf.get("canopy.t1"));
            System.err.println("T1="+T1);
        } catch (Exception e) {

        }

        try {
            T2 = Double.parseDouble(conf.get("canopy.t2"));
            System.err.println("T2="+T2);
        } catch (Exception e) {

        }

    }

    public void reduce(Text key, Iterator<CanopyItem> value,
                       OutputCollector<Text, CanopyItem> output, Reporter arg3)
            throws IOException {

        List<CanopyItem> canopies = new Vector<CanopyItem>();

        while (value.hasNext()) {
            CanopyItem ci = new CanopyItem(value.next());
            if(canopies.size()==0) {
                canopies.add(ci);
            } else {
                boolean isAdded = false;
                boolean isSplit = true;
                for(int i=0; i<canopies.size(); i++) {
                    CanopyItem cset = canopies.get(i);
                    double distance = cset.distanceTo(ci);
                    // 只要与一个canopy的距离小于T1则将此项加入canopy
                    if(distance < T1) {
                        cset.addItem(ci);
                        isAdded = true;
                        break;
                    }

                    // 只要与一个canopy的距离小于T2则丢弃此项
                    if(distance < T2) {
                        isSplit = false;
                        break;
                    }
                }

                // 如果项目与所有canopy的距离都大于T2则独立出一个canopy
                if(isAdded != true && isSplit == true) {
                    canopies.add(ci);
                }
            }
        }

        // 如果聚类有元素
        if (canopies.size() > 0) {
            for(int i=0; i<canopies.size(); i++) {
                CanopyItem cset = canopies.get(i);
                output.collect(new Text("canopy"), cset);
            }
        }
    }
}
