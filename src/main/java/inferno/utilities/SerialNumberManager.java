package inferno.utilities;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Base64;

public class SerialNumberManager {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    // Example Master Key (replace with your secure key management approach)
    private static final String MASTER_KEY_STRING = "YOUR_MASTER_KEY_STRING";

    // Method to get AES key from a string (not Base64 encoded)
    private static SecretKey getKeyFromString(String keyString) {
        byte[] keyBytes = keyString.getBytes();
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    // Method to encrypt the serial number with the master key
    public static void encryptSerialNumberToFile(String serialNumber, String filePath) throws Exception {
        SecretKey masterKey = getKeyFromString(MASTER_KEY_STRING);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, masterKey);
        byte[] encryptedSerial = cipher.doFinal(serialNumber.getBytes());
        Files.write(Paths.get(filePath), encryptedSerial);
    }

    // Method to decrypt the serial number from the file
    public static String decryptSerialNumberFromFile(String filePath) throws Exception {
        SecretKey masterKey = getKeyFromString(MASTER_KEY_STRING);
        byte[] encryptedSerial = Files.readAllBytes(Paths.get(filePath));
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, masterKey);
        byte[] decryptedSerial = cipher.doFinal(encryptedSerial);
        return new String(decryptedSerial);
    }

    public static void main(String[] args) {
        try {
            // Example usage
            String serialNumber = "DEVICE_SERIAL_NUMBER";
            String filePath = "serial_number_encrypted.hex";
            encryptSerialNumberToFile(serialNumber, filePath);
            String decryptedSerial = decryptSerialNumberFromFile(filePath);
            System.out.println("Decrypted Serial Number: " + decryptedSerial);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
