package com.weka.dataset;

/**
 * Created by henriezhang on 2014/12/3.
 * ARFF文件转换为CSV文件
 */

import weka.core.Instances;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;

public class ARFF2CSV {
    public static void main(String[] args) {
        try {
            // 加载数据
            Instances data = new Instances(
                    DataSource.read("C:\\Program Files\\Weka-3-6\\data\\weather.nominal.arff"));
            System.out.println("完成加载数据");

            // 使用DataSink类，保存为CSV
            //DataSink.write("C:\\Program Files\\Weka-3-6\\data\\weather.nominal.arff.csv", data);
            //System.out.println("完成使用DataSink类保存数据");

            // 明确指定转换器，保存为CSV
            CSVSaver saver = new CSVSaver();
            saver.setInstances(data);
            saver.setFile(new File("C:\\Program Files\\Weka-3-6\\data\\weather.nominal.arff2.csv"));
            saver.writeBatch();
            System.out.println("finished transformation");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
