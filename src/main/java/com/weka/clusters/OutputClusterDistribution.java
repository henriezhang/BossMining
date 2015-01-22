package com.weka.clusters;

/**
 * Created by henriezhang on 2014/12/4.
 */
import weka.clusterers.EM;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * 本例展示在训练集上构建EM聚类器，然后在测试集上预测簇并输出簇的隶属度
 */
public class OutputClusterDistribution {

    public static void main(String[] args) throws Exception {
        // 加载数据
        Instances train = DataSource
                .read("C:\\Program Files\\Weka-3-6\\data\\segment-challenge.arff");
        Instances test = DataSource.read("C:\\Program Files\\Weka-3-6\\data\\segment-test.arff");
        if (!train.equalHeaders(test))
            throw new Exception("train set is not suit test set：");

        // 构建聚类器
        EM clusterer = new EM();
        clusterer.buildClusterer(train);

        // 输出预测
        System.out.println("No -cluster  \t-\t distribution");
        for (int i = 0; i < test.numInstances(); i++) {
            int cluster = clusterer.clusterInstance(test.instance(i));
            double[] dist = clusterer.distributionForInstance(test.instance(i));
            System.out.print((i + 1));
            System.out.print(" - ");
            System.out.print(cluster);
            System.out.print(" - ");
            System.out.print(Utils.arrayToString(dist));
            System.out.println();
        }
    }
}
