package com.ecolim.app.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad de hash criptográfico MD5/SHA-256 para contraseñas locales.
 * app/src/main/java/com/ecolim/app/util/HashUtil.java
 */
public class HashUtil {

    public static String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xFF & b);
                while (hex.length() < 2) hex = "0" + hex;
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return s;
        }
    }
}
