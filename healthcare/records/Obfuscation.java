package healthcare.records;

import java.io.Console;
import java.io.IOException;
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

    /*
     * public static String getPasswordInput(String prompt) {
     * Console console = System.console();
     * if (console == null) {
     * throw new RuntimeException("Console not available.");
     * }
     * char[] passwordChars = console.readPassword(prompt);
     * return new String(passwordChars);
     * }
     */

    public static String getPasswordInput(String prompt) {
        System.out.print(prompt);
        StringBuilder password = new StringBuilder();

        try {
            // Disable echo on the console
            while (true) {
                char ch = (char) System.in.read();
                // Enter key (ASCII code 13 or 10 for newline)
                if (ch == '\n' || ch == '\r') {
                    System.out.println(); // Move to the next line after password input
                    break;
                } else if (ch == '\b' && password.length() > 0) { // Handle backspace
                    password.deleteCharAt(password.length() - 1);
                    System.out.print("\b \b"); // Remove last '*' from the display
                } else {
                    password.append(ch);
                    System.out.print('*'); // Print '*' for each character
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading input", e);
        }

        return password.toString();
    }

}
