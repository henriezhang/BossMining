package com.mykmeans;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by henriezhang on 2015/1/6.
 */
public class KmeansItem implements Writable {
    private String id = "";

    private int vecLen = 0;

    private int relNum = 1; // 作为kmeans聚集的时候聚集个数

    private double vec[] = null;

    public KmeansItem() {

    }

    public KmeansItem(KmeansItem ki) {
        this.id = ki.id;
        this.vecLen = ki.getVecLen();
        this.vec = new double[vecLen];
        double oldVec[] = ki.getVec();
        for (int i = 0; i < oldVec.length; i++) {
            vec[i] = oldVec[i];
        }
    }

    public boolean parseItem(String val, int attrNum) {
        this.vecLen = attrNum;
        vec = new double[vecLen];
        String fields[] = val.split(KmeansConst.SEP_ASC_1);
        id = fields[0];
        for (int i = 0; i < vecLen && i <= fields.length; i++) {
            try {
                vec[i] = Double.parseDouble(fields[i + 2]);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public String getId() {
        return id;
    }

    public int getVecLen() {
        return vecLen;
    }

    public double[] getVec() {
        return vec;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeInt(vecLen);
        out.writeInt(relNum);
        for (int i = 0; i < vecLen; i++) {
            out.writeDouble(vec[i]);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        id = in.readUTF();
        vecLen = in.readInt();
        relNum = in.readInt();
        vec = new double[vecLen];
        for (int i = 0; i < vecLen; i++) {
            vec[i] = in.readDouble();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        sb.append("\u0001");
        for (int i = 0; i < vecLen; i++) {
            sb.append(vec[i]);
            sb.append(" ");
        }
        return sb.substring(0,sb.length()-2);
    }
}