package com;

/**
 * Created by henriezhang on 2014/12/18.
 */
public class RandomTest {
    public static void main(String args[]) {
        for(int i=0; i<20; i++) {
            double x1 = Math.random();
            double x2 = Math.random();
            double x3 = Math.random();
            double x4 = Math.random();
            double x5 = Math.random();
            double x6 = Math.random();
            double x7 = Math.random();
            double x8 = Math.random();
            double x9 = Math.random();
            double x10 = Math.random();

            double y = 3 + 2*x1 + 3*x2 + 4*x3 + 5*x4 + 6*x5 + 7*x6 + 8*x7 + 9*x8 + 10*x9 + 5*x10 + 0.5*Math.random();

            System.out.print(""+x1+" ");
            System.out.print(""+x2+" ");
            System.out.print(""+x3+" ");
            System.out.print(""+x4+" ");
            System.out.print(""+x5+" ");
            System.out.print(""+x6+" ");
            System.out.print(""+x7+" ");
            System.out.print(""+x8+" ");
            System.out.print(""+x9+" ");
            System.out.print(""+x10+" ");
            System.out.print(""+y+"\n");
        }
    }
}
