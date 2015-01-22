package com.weka.serialization;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * 模型序列化和反序列化示例
 */
public class ModelSerialization {
    public static void main(String[] args) throws Exception {
        // 加载数据
        Instances inst = DataSource
                .read("C:\\Program Files\\Weka-3-6\\data\\weather.numeric.arff");
        inst.setClassIndex(inst.numAttributes() - 1);
        // 训练J48
        Classifier cls = new J48();
        cls.buildClassifier(inst);
        System.out.println(cls);

        // 序列化模型
        SerializationHelper.write("C:\\Program Files\\Weka-3-6\\data\\j48.model", cls);
        System.out.println("serialize ！\n");

        // 反序列化模型
        Classifier cls2 = (Classifier) SerializationHelper
                .read("C:\\Program Files\\Weka-3-6\\data\\j48.model");
        System.out.println("serialize succeed！");
        System.out.println("serialize failed：");
        System.out.println(cls2);
    }
}
