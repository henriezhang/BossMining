package com.mykmeans;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.LineReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by henriezhang on 2015/1/6.
 */
public class KmeansCenters {
    private Map<String, double[]> centers = null;

    private int attrNum = 0;

    public Map<String, double[]> getCenters() {
        return centers;
    }

    public KmeansCenters(int attrNum) {
        this.attrNum = attrNum;
        this.centers = new HashMap<String, double[]>();
    }

    public KmeansCenters(int attrNum, Map<String, double[]> centers) {
        this.centers = centers;
    }

    // 从文件读取中心点数据
    public boolean readCentersFromFile(String path) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        FileStatus[] stats = null;
        FSDataInputStream in = null;
        LineReader reader = null;

        System.out.println(path);
        Path pDir = new Path(path);
        try {
            stats = fs.listStatus(pDir);
        } catch (Exception e) {
            System.err.println("list learn data failed");
            System.exit(12);
        }
        for (int i = 0; i < stats.length && !stats[i].isDir(); i++) {
            Path fPath = stats[i].getPath();
            try { // 打开文件
                in = fs.open(fPath);
            } catch (Exception e) {
                System.err.println("open file failed file=" + fPath.toString());
                continue;
            }
            reader = new LineReader(in);
            Text test = new Text();
            try { // 读取测试数据
                while (reader.readLine(test) > 0) {
                    //DeepNNCover cInfo = new DeepNNCover();
                    //cInfo.setVec(test.toString());
                    //this.coversInfo.put(cInfo.getCid(), cInfo);
                }
            } catch (Exception e) {
                System.err.println("read learn data file failed file=" + fPath.toString());
                continue;
            }
        }
        return true;
    }

    // 计算两组中心点之间的差异
    public double changing(KmeansCenters newCenters) {
        Map<String, double[]> nCenters = newCenters.getCenters();
        if (nCenters.size() != centers.size()) {
            return 1.0;
        }

        double diff = 0.0, sum = 0.0;
        Iterator iter = this.centers.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            Object thisValue = this.centers.get(key);
            Object thatValue = nCenters.get(key);

            double[] thisArr = (double[]) thisValue;
            double[] thatArr = (double[]) thatValue;

            for (int i = 0; i < thisArr.length; i++) {
                diff = thisArr[i] - thatArr[i];
                sum += diff * diff;
            }
        }

        return sum;
    }

    // 计算两个向量的欧几里得距离
    private double euclideanDistance(double[] vec1, double[] vec2) {
        if (vec1.length != vec2.length) {
            return Double.MAX_VALUE;
        }

        double diff = 0.0, sum = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            diff = vec1[i] - vec2[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    // 计算数据的最近聚类中心
    public String statNearstCenter(KmeansItem ki) {
        Iterator iter = this.centers.keySet().iterator();
        String minId = "";
        double minDistance = Double.MAX_VALUE;
        while (iter.hasNext()) {
            Object key = iter.next();
            Object thisValue = this.centers.get(key);
            double[] thisArr = (double[]) thisValue;

            double distance = euclideanDistance(thisArr, ki.getVec());
            if (distance > minDistance) {
                minId = key.toString();
                minDistance = distance;
            }
        }
        return minId;
    }
}