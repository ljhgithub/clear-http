package com.pysun.clearhttp.util;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * Created by Administrator on 2017/3/29.
 */

public class MD5 {

    public static String getStringMD5(String value) {
        if (value == null || value.trim().length() < 1) {
            return null;
        }
        try {
            return getMD5(value.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    public static String getMD5(byte[] source) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return HexDump.toHex(md5.digest(source));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
