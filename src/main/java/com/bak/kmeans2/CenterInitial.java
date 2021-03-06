package com.bak.kmeans2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;


public class CenterInitial {
    public void run(String[] args) throws IOException
    {
        String[] clist;//用于保存中心点
        int k = 2;//中心点选取个数
        String string = "";//保存各个中心点在同一个字符串string中
        String inpath = args[0]+"/4.txt";  //cluster数据集放在2.txt中
        String outpath = args[1]+"/input2/3.txt";  //center新选取的中心点放进3.txt中保存
        Configuration conf1 = new Configuration(); //读取hadoop文件系统的配置
        conf1.set("hadoop.job.ugi", "hadoop,hadoop"); //配置信息设置
        FileSystem fs = FileSystem.get(URI.create(inpath),conf1); //FileSystem是用户操作HDFS的核心类，它获得URI对应的HDFS文件系统
        FSDataInputStream in = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try{
            in = fs.open( new Path(inpath) );
            IOUtils.copyBytes(in,out,50,false);  //用Hadoop的IOUtils工具方法来让这个文件的指定字节复制到标准输出流上
            //把in读到的数据 复制到out上
            clist = out.toString().split(" ");//将out以空格为分割符转换成数组在clist中保存
        } finally {
            IOUtils.closeStream(in);
        }

        FileSystem filesystem = FileSystem.get(URI.create(outpath), conf1); //获得URI对应的HDFS文件系统

        for(int i=0;i<k;i++)
        {
            int j=(int) (Math.random()*100) % clist.length;//选取0到clist.lenth-1的随机数
            if(string.contains(clist[j]))  // 如果选取的是同一个随机数
            {
                k++;
                continue;
            }
            string = string + clist[j].replace(" ", "") + " ";//将得到的k个随机点的坐标用一个字符串保存
        }
        OutputStream out2 = filesystem.create(new Path(outpath) );
        IOUtils.copyBytes(new ByteArrayInputStream(string.getBytes()), out2, 4096,true); //把随机点坐标字符串out2中
        System.out.println("初始化过程："+string);
    }

}

