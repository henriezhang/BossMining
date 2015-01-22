package com.mahout;

import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.canopy.CanopyDriver;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;

/**
 * Created by henriezhang on 2014/12/17.
 */
public class KmeansTest {
    public static void main(String[] args)
    {
        System.out.println("hello");
        Path input1 = new Path("testdata");
        Path output1 = new Path("output");
        try {
            CanopyDriver.run(input1, output1, new EuclideanDistanceMeasure(), (float) 3.1, (float) 2.1, false, (double) 0.6, true);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

        // now run the KMeansDriver job
        Path input2 = new Path("testdata");
        Path clustersIn = new Path("output/clusters-0");
        Path output2 = new Path("output");
        try {
            KMeansDriver.run(input2, clustersIn, output2, 0.5, 10, true, 0.3, true);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}
