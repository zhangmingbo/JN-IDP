package com.egoo.idp.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
//import java.util.Base64;

/**
 * @Author: liuyi on 2019/10/9
 * @Description:
 */
@Slf4j
public class EncryptBaseUtil {

    private static final String SALT = "#qms#";
    private static final int REPEAT = 5;

//    /**
//     * 加密操作
//     *
//     * @param pwd 需要加密的字符串,与盐值整合
//     * @return 加密后的数据
//     */
//    public static String encode(String pwd) {
//        if (pwd == null) {
//            return null;
//        }
//        String temp = pwd + SALT;
//        byte[] data = new byte[0];
//        try {
//            data = temp.getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        //重复加密
//        for (int i = 0; i < REPEAT; i++) {
////            data = Base64.getEncoder().encode(data);
//        }
//        return new String(data);
//    }
//
//
//    /**
//     * 解密操作
//     *
//     * @param encode 加密的字符
//     * @return 解密后的字符
//     */
//    public static String decode(String encode) {
//        if (encode == null) {
//            return null;
//        }
//        try {
//            byte data[] = encode.getBytes("UTF-8");
//            for (int i = 0; i < REPEAT; i++) {
////                data = Base64.getDecoder().decode(data);
//            }
//            return new String(data).replaceAll("#[^#]*#", "");
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(e + " {*****错误的密钥****}");
//        }
//        return null;
//    }
//
//
//
    public static String decode(String encode) {
        if (encode == null) {
            return null;
        }
        try {
            byte data[] = encode.getBytes("UTF-8");
            data = Base64.decodeBase64(data);
            return new String(data).replaceAll("#[^#]*#", "");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e + " {*****错误的密钥****}");
        }
        return null;
    }

    public static String encode(String pwd) {
        if (pwd == null) {
            return null;
        }
        byte[] data = null;
        try {
            data = pwd.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        data = Base64.encodeBase64(data);
        return new String(data);
    }

    public static void main(String[] args) {
        String qwEtMTExMTEh = decode("UmVASXMlQTQzIQ==");
        System.out.println("UmVASXMlQTQzIQ== = " + qwEtMTExMTEh);
        String encode = encode("Myegoo#3466");
        System.out.println("encode===" + encode);
    }
}
