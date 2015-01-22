package com.svm;

import com.svm.utils.svm_predict;
import com.svm.utils.svm_train;

/**
 * Created by henriezhang on 2014/12/18.
 */
public class HelloSVM {
    public static void main(String[] args) throws Exception {
        // 训练集文件, 644个样本
        String trainArgs[] = {"breast-cancer_sc-train","breast-cancer.model"};
        svm_train.main(trainArgs);// 训练分类model(分类超平面)

        // 测试, 39个测试样本
        String testArgs[] = {"breast-cancer_sc-test", "breast-cancer.model", "breast-cancer.result"};
        svm_predict.main(testArgs);// 测试
    }
}
