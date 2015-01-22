package com.bak.kmeans;

/**
 * Created by henriezhang on 2015/1/6.
 */

import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class DmRecordParser {
    private Map<String, DmRecord> urlMap = new HashMap<String, DmRecord>();

    /**
     * 读取配置文件记录，生成对象
     */
    public void initialize(File file) throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = in.readLine()) != null) {
                String[] strKey = line.split("\t");
                urlMap.put(strKey[0], parse(line));
            }
        } finally {
            IOUtils.closeStream(in);
        }
    }

    /**
     * 生成坐标对象
     */
    public DmRecord parse(String line) {
        String[] strPlate = line.split("\t");
        DmRecord Dmurl = new DmRecord(strPlate[0], Integer.parseInt(strPlate[1]), Integer.parseInt(strPlate[2]));
        return Dmurl;
    }

    /**
     * 获取分类中心坐标
     */
    public DmRecord getUrlCode(String cluster) {
        DmRecord returnCode = null;
        DmRecord dmUrl = (DmRecord) urlMap.get(cluster);
        if (dmUrl == null) {
            returnCode = null;
        } else {
            returnCode = dmUrl;
        }
        return returnCode;
    }
}
