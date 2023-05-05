package com.example.STARTapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Encriptia parolei
 */
public class PasswordHash {

    public static byte[] Salt;
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * genereaza encryption salt
     *
     * @param pass parola
     * @param salt salt pentru encriptie
     * @return intoarce functia pentru generarea unui hash
     * @throws NoSuchAlgorithmException
     */
    public static String generate(String pass, byte[] salt) throws NoSuchAlgorithmException {
        String algorithm = "SHA-256";
        if (salt == null) {
            salt = createSalt();
        }
        Salt = salt;
        return generateHash(pass, algorithm, salt);
    }

    /**
     * Genereaza un hash
     *
     * @param data      parola
     * @param algorithm algoritmul de encriptie
     * @param salt      salt pentru encriptie
     * @return intoarce valoarea hashului
     * @throws NoSuchAlgorithmException
     */
    private static String generateHash(String data, String algorithm, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.reset();
        digest.update(salt);
        byte[] hash = digest.digest(data.getBytes());
        return bytesToStringHex(hash);
    }

    private static String bytesToStringHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Genereaza un salt
     *
     * @return intoarce saltul
     */
    private static byte[] createSalt() {
        byte[] bytes = new byte[20];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }
}
