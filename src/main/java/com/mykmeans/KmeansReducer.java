package com.mykmeans;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by henriezhang on 2015/1/6.
 */
//计算新的聚类中心
public class KmeansReducer extends Reducer<Text, KmeansItem, Text, Text> {
    private int attrNum = 0;

    //获取聚类中心坐标
    protected void setup(Mapper.Context context) throws IOException, InterruptedException {
        Configuration conf = new Configuration();
        String numTmp = conf.get("kmeans.attrnum");
        try {
            attrNum = Integer.parseInt(numTmp);
        } catch (Exception e) {

        }
    }

    public void reduce(Text key, Iterable<KmeansItem> kis, Context context)
        throws IOException, InterruptedException {

        double vec[] = new double[this.attrNum];

        // 累加各维数据
        int count = 0;
        Iterator<KmeansItem> value = kis.iterator();
        while (value.hasNext()) {
            count++;
            KmeansItem ki = new KmeansItem(value.next());
            double[] tmpVec = ki.getVec();
            for (int i = 0; i < this.attrNum; i++) {
                vec[i] += tmpVec[i];
            }
        }

        // 如果聚类有元素
        if (count > 0) {
            // 求均值
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.attrNum; i++) {
                sb.append(KmeansConst.SEP_ASC_1);
                sb.append(vec[i] / count);
            }

            // 输出结果
            context.write(key, new Text(sb.substring(1)));
        }
    }
}
