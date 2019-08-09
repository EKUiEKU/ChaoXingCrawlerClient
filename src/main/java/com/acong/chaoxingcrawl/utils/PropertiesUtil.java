package com.acong.chaoxingcrawl.utils;

import java.io.*;
import java.util.Properties;


/**
 *
 * @author
 *
 */
public class PropertiesUtil {

    private String properiesName = "config.properties";

    public PropertiesUtil() {

    }

    /**
     * 按key获取值
     * @param key
     * @return
     */
    public String readProperty(String key) {
        String value = "";
        InputStream is = null;
        File file = new File("", properiesName);
        if (file.exists() == false)
            return null;
        try {
            is = new FileInputStream(file);
            Properties p = new Properties();
            p.load(is);
            value = p.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * 获取整个配置信息
     * @return
     */
    public Properties getProperties() {
        Properties p = new Properties();
        InputStream is = null;
        File file = new File("", properiesName);

        if (file.exists() == false)
            return null;
        try {
            is = new FileInputStream(file);
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return p;
    }

    /**
     * key-value写入配置文件
     * @param key
     * @param value
     */
    public void writeProperty(String key, String value) {
        InputStream is = null;
        OutputStream os = null;
        Properties p = new Properties();
        File file = new File("", properiesName);
        try {
            if (file.exists())
                p.load(new FileInputStream(file));

            os = new FileOutputStream(file);
            p.setProperty(key, value);
            p.store(os, key);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is)
                    is.close();
                if (null != os)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}