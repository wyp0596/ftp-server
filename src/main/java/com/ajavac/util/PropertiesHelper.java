package com.ajavac.util;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 读取config下的配置文件
 * Created by wyp0596 on 8/12/2016.
 */
public class PropertiesHelper {

    /**
     * 通过Properties文件获取Properties对象
     *
     * @param inputFilePath 输入Properties文件完整路径
     * @return Properties对象
     */
    public static Properties getProperties(String inputFilePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(inputFilePath)) {
            properties.load(fileInputStream);
        }
        return properties;
    }

    /**
     * 通过Properties对象写入Properties文件
     *
     * @param properties     Properties对象
     * @param outputFilePath 输出Properties文件完整路径
     */
    public static void saveProperties(Properties properties, String outputFilePath) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {
            properties.store(fileOutputStream);
        }
    }
}
