package com.passwordManager;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class KeyGeneratorUtil {

    public static String generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // AES-128
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static void main(String[] args) {
        try {
            String encryptionKey = generateKey();
            System.out.println("Generated Encryption Key: " + encryptionKey);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error generating encryption key: " + e.getMessage());
        }
    }
}
