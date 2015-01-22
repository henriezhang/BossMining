package com.bak.kmeans;

/**
 * Created by henriezhang on 2015/1/6.
 */
//二维坐标的点
public class DmRecord {
    private String name;
    private double xpodouble;
    private double ypodouble;

    public DmRecord() {

    }
    public DmRecord(String name, double x, double y) {
        this.name = name;
        this.xpodouble = x;
        this.ypodouble = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getXpoint() {
        return xpodouble;
    }

    public void setXpoint(double xpodouble) {
        this.xpodouble = xpodouble;
    }

    public double getYpoint() {
        return ypodouble;
    }

    public void setYpoint(double ypodouble) {
        this.ypodouble = ypodouble;
    }

    public double distance(DmRecord record) {
        return Math.sqrt(Math.pow(this.xpodouble - record.xpodouble, 2) + Math.pow(this.ypodouble - record.ypodouble, 2));
    }
}
