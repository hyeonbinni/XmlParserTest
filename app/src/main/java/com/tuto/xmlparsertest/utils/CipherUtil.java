package com.tuto.xmlparsertest.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtil {
    public static byte[] encryptAES(byte[] src) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = "xmlutilencrypted".getBytes();
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        return cipher.doFinal(src);
    }

    public static byte[] encryptAES(String src) throws Exception {
        return encryptAES(src.getBytes("UTF-8"));
    }

    public static String decryptAES(byte[] src) throws Exception {
        return new String(decryptAESToByte(src), "UTF-8");
    }

    public static byte[] decryptAESToByte(byte[] src) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = "xmlutilencrypted".getBytes();
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        return cipher.doFinal(src);
    }
}
