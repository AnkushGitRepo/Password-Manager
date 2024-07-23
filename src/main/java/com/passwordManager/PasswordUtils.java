package com.passwordManager;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.kerberos.EncryptionKey;

public class PasswordUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static String ENCRYPTION_KEY = ""; // Replace with your actual key

    static {
        try {
            ENCRYPTION_KEY = KeyGeneratorUtil.generateKey();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String encrypt(String input) throws Exception {
        return Base64.getEncoder().encodeToString(doCrypto(Cipher.ENCRYPT_MODE, input));
    }

    public static String decrypt(String input) throws Exception {
        return new String(doCrypto(Cipher.DECRYPT_MODE, Base64.getDecoder().decode(input)));
    }

    private static byte[] doCrypto(int cipherMode, String input) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(ENCRYPTION_KEY), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(cipherMode, secretKey);
        return cipher.doFinal(input.getBytes());
    }

    private static byte[] doCrypto(int cipherMode, byte[] input) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(ENCRYPTION_KEY), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(cipherMode, secretKey);
        return cipher.doFinal(input);
    }

    public static String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = getSalt();
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] hash = Base64.getDecoder().decode(parts[1]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, 10000, 256);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        return java.security.MessageDigest.isEqual(hash, testHash);
    }
}
