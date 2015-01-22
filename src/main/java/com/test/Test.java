package com.test;

import java.util.List;
import java.util.Vector;

/**
 * Created by henriezhang on 2015/1/20.
 */
public class Test {
    public static void main(String[] args) {
        List<String> canopies = new Vector<String>();
        canopies.add("12345");
        canopies.add("asdfg");

        for(int i=0; i<canopies.size(); i++) {
            System.out.println(canopies.get(i));
        }
    }
}
