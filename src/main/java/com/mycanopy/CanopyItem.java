package com.mycanopy;

import com.mykmeans.KmeansConst;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by henriezhang on 2015/1/6.
 */
public class CanopyItem implements Writable {
    private String id = "";

    private int vecLen = 0;

    private int relNum = 1; // 作为kmeans聚集的时候聚集个数

    private double vec[] = null;

    public CanopyItem() {

    }

    public CanopyItem(CanopyItem ki) {
        this.id = ki.id;
        this.vecLen = ki.getVecLen();
        this.relNum = ki.getRelNum();
        this.vec = new double[vecLen];
        double oldVec[] = ki.getVec();
        for (int i = 0; i < oldVec.length; i++) {
            vec[i] = oldVec[i];
        }
    }

    public boolean parseItem(String val, int attrNum) {
        this.vecLen = attrNum;
        this.relNum = 1;
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

    public int getRelNum() {
        return relNum;
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

    // 作为canopy中心添加一个元素
    public void addItem(CanopyItem ci) {
        if (vecLen != ci.vecLen) {
            return;
        }

        int thatRelNum = ci.getRelNum();
        double[] thatVec = ci.getVec();

        int newRelNum = relNum + thatRelNum;
        double[] newVec = new double[vecLen];

        for (int i = 0; i < vecLen; i++) {
            newVec[i] = (vec[i] * relNum + thatVec[i] * thatRelNum) / newRelNum;
        }

        this.vec = newVec;
        this.relNum = newRelNum;
    }

    public double distanceTo(CanopyItem ci) {
        double[] vecTmp = ci.getVec();
        double diff = 0.0, distance = 0.0;
        for(int i=0; i<this.vecLen; i++) {
            diff = vec[i] - vecTmp[i];
            distance += diff * diff;
        }
        return Math.sqrt(distance);
    }

    public String getVecString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vecLen; i++) {
            sb.append(vec[i]);
            sb.append(" ");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        sb.append("\t");
        for (int i = 0; i < vecLen; i++) {
            sb.append(vec[i]);
            sb.append(" ");
        }
        return sb.substring(0, sb.length() - 1);
    }
}