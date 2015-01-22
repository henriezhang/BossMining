package com.bak.kmeans2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.StringTokenizer;

public class KMapper extends Mapper<LongWritable, Text, Text, Text> {
    private String[] center;

    //读取3.txt中更新的中心点坐标，并将坐标存入center数组中
    protected void setup(Context context) throws IOException, InterruptedException  //read centerlist, and save to center[]
    {
        String centerlist = "hdfs://localhost:9000/home/administrator/hadoop/kmeans/input2/3.txt"; //center文件
        Configuration conf1 = new Configuration();
        conf1.set("hadoop.job.ugi", "hadoop-user,hadoop-user");
        FileSystem fs = FileSystem.get(URI.create(centerlist), conf1);
        FSDataInputStream in = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            in = fs.open(new Path(centerlist));
            IOUtils.copyBytes(in, out, 100, false);
            center = out.toString().split(" ");
        } finally {
            IOUtils.closeStream(in);
        }
    }

    //从hadoop接收的数据在2.txt中保存
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer itr = new StringTokenizer(value.toString());
        //从2.txt读入数据，以空格为分割符，一个一个处理
        while (itr.hasMoreTokens())//用于判断所要分析的字符串中，是否还有语言符号，如果有则返回true，反之返回false
        {

            //计算第一个坐标跟第一个中心的距离min
            String outValue = new String(itr.nextToken());//逐个获取以空格为分割符的字符串(2,3) (10,30) (34,40) (1,1)
            String[] list = outValue.replace("(", "").replace(")", "").split(",");
            String[] c = center[0].replace("(", "").replace(")", "").split(",");
            float min = 0;
            int pos = 0;
            for (int i = 0; i < list.length; i++) {
                System.out.println(i + "list:" + list[i]);
                System.out.println(i + "c:" + c[i]);
                min += (float) Math.pow((Float.parseFloat(list[i]) - Float.parseFloat(c[i])), 2);//求欧式距离，为加根号
            }

            for (int i = 0; i < center.length; i++) {
                String[] centerStrings = center[i].replace("(", "").replace(")", "").split(",");
                float distance = 0;
                for (int j = 0; j < list.length; j++)
                    distance += (float) Math.pow((Float.parseFloat(list[j]) - Float.parseFloat(centerStrings[j])), 2);
                if (min > distance) {
                    min = distance;
                    pos = i;
                }
            }
            context.write(new Text(center[pos]), new Text(outValue));//输出：中心点，对应的坐标
            System.out.println("中心点" + center[pos] + "对应坐标" + outValue);
            System.out.println("Mapper输出：" + center[pos] + " " + outValue);
        }
    }
}
