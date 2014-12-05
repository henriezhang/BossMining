package com.webdev.application;

/**
 * 将简短的文本信息分类为两个类别的Java程序
 */

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class MessageClassifier implements Serializable {

    private static final long serialVersionUID = -6705084686587638940L;

    /* 迄今收集训练数据 */
    private Instances m_Data = null;

    /* 用于生成单词计数的过滤器 */
    private StringToWordVector m_Filter = new StringToWordVector();

    /* 实际的分类器 */
    private Classifier m_Classifier = new J48();

    /* 模型是否为最新 */
    private boolean m_UpToDate;

    /**
     * 构建空训练集
     */
    public MessageClassifier() throws Exception {

        String nameOfDataset = "MessageClassificationProblem";

        // 创建的属性列表
        List<Attribute> attributes = new ArrayList<Attribute>();

        // 添加属性以保存文本信息
        attributes.add(new Attribute("Message", (List<String>) null));

        // 添加类别属性
        List<String> classValues = new ArrayList<String>();
        classValues.add("miss");
        classValues.add("hit");
        attributes.add(new Attribute("Class", classValues));

        // 创建初始容量为100的数据集
        m_Data = new Instances(nameOfDataset, (ArrayList<Attribute>) attributes, 100);
        // 设置类别索引
        m_Data.setClassIndex(m_Data.numAttributes() - 1);
    }

    /**
     * 使用给定的训练文本信息更新模型
     */
    public void updateData(String message, String classValue) throws Exception {

        // 把文本信息转换为实例
        Instance instance = makeInstance(message, m_Data);

        // 为实例设置类别值
        instance.setClassValue(classValue);

        // 添加实例到训练数据
        m_Data.add(instance);
        m_UpToDate = false;

        // 输出提示信息
        System.err.println("更新模型成功！");
    }

    /**
     * 分类给定的文本消息
     */
    public void classifyMessage(String message) throws Exception {

        // 检查是否已构建分类器
        if (m_Data.numInstances() == 0) {
            throw new Exception("没有分类器可用。");
        }

        // 检查是否分类器和过滤器为最新
        if (!m_UpToDate) {

            // 初始化过滤器，并告知输入格式
            m_Filter.setInputFormat(m_Data);

            // 从训练数据生成单词计数
            Instances filteredData = Filter.useFilter(m_Data, m_Filter);

            // 重建分类器
            m_Classifier.buildClassifier(filteredData);
            m_UpToDate = true;
        }

        // 形成单独的小测试集，所以该文本信息不会添加到m_Data的字符串属性中
        Instances testset = m_Data.stringFreeStructure();

        // 使文本信息成为测试实例
        Instance instance = makeInstance(message, testset);

        // 过滤实例
        m_Filter.input(instance);
        Instance filteredInstance = m_Filter.output();

        // 获取预测类别值的索引
        double predicted = m_Classifier.classifyInstance(filteredInstance);

        // 输出类别值
        System.err.println("文本信息分类为 ："
                + m_Data.classAttribute().value((int) predicted));
    }

    /**
     * 将文本信息转换为实例的方法
     */
    private Instance makeInstance(String text, Instances data) {

        // 创建一个属性数量为2，权重为1，全部值都为缺失的实例
        Instance instance = new DenseInstance(2);

        // 设置文本信息属性的值
        Attribute messageAtt = data.attribute("Message");
        instance.setValue(messageAtt, messageAtt.addStringValue(text));

        // 让实例能够访问数据集中的属性信息
        instance.setDataset(data);
        return instance;
    }

    /**
     * 主方法
     * 可以识别下列参数：
     * -E
     *   文本是否为英文。默认为中文，省略该参数
     * -m 文本信息文件
     *  指向一个文件，其中包含待分类的文本信息，或用于更新模型的文本信息。
     * -c 类别标签
     *  如果要更新模型，文本信息的类别标签。省略表示需要对文本信息进行分类。
     * -t 模型文件
     *  包含模型的文件。如果不存在该文件，就会自动创建。
     *
     *  @param args 命令行选项
     */
    public static void main(String[] options) {

        try {

            // 读入文本信息文件，存储为字符串
            String messageName = Utils.getOption('m', options);
            if (messageName.length() == 0) {
                throw new Exception("必须提供文本信息文件的名称。");
            }
            FileReader m = new FileReader(messageName);
            StringBuffer message = new StringBuffer();
            int l;
            while ((l = m.read()) != -1) {
                message.append((char) l);
            }
            m.close();

            // 检查是否文本为英文
            boolean isEnglish = Utils.getFlag('E', options);
            if(! isEnglish) {
                // 只有汉字需要进行中文分词
                Analyzer ikAnalyzer = new IKAnalyzer();
                Reader reader = new StringReader(message.toString());
                TokenStream stream = (TokenStream)ikAnalyzer.tokenStream("", reader);
                CharTermAttribute termAtt  = (CharTermAttribute)stream.addAttribute(CharTermAttribute.class);
                message = new StringBuffer();
                while(stream.incrementToken()){
                    message.append(termAtt.toString() + " ");
                }
            }

            // 检查是否已给定类别值
            String classValue = Utils.getOption('c', options);

            // 如果模型文件存在，则读入，否则创建新的模型文件
            String modelName = Utils.getOption('t', options);
            if (modelName.length() == 0) {
                throw new Exception("必须提供模型文件的名称。");
            }
            MessageClassifier messageCl;
            try {
                ObjectInputStream modelInObjectFile = new ObjectInputStream(
                        new FileInputStream(modelName));
                messageCl = (MessageClassifier) modelInObjectFile.readObject();
                modelInObjectFile.close();
            } catch (FileNotFoundException e) {
                messageCl = new MessageClassifier();
            }

            // 处理文本信息
            if (classValue.length() != 0) {
                messageCl.updateData(message.toString(), classValue);
            } else {
                messageCl.classifyMessage(message.toString());
            }

            // 保存文本信息分类器对象
            ObjectOutputStream modelOutObjectFile = new ObjectOutputStream(
                    new FileOutputStream(modelName));
            modelOutObjectFile.writeObject(messageCl);
            modelOutObjectFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
