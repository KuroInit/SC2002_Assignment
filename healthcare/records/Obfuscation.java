package healthcare.records;

import java.io.Console;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class Obfuscation {

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                hashString.append(String.format("%02x", b));
            }
            return hashString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available.");
        }
    }

    public static int compareHash(String hash, String plainText) {
        String hashedPlainText = hashPassword(plainText);
        return hash.equals(hashedPlainText) ? 1 : 0;
    }

    public static String getPasswordInput(String prompt) {
        Console console = System.console();
        if (console == null) {
            throw new RuntimeException("Console not available.");
        }
        char[] passwordChars = console.readPassword(prompt);
        return new String(passwordChars);
    }

}
