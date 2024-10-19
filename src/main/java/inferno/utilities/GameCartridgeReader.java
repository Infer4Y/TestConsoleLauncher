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

public class GameCartridgeReader {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    // Example Master Key (replace with your secure key management approach)
    private static final String MASTER_KEY_STRING = "YOUR_MASTER_KEY_STRING";

    // Method to get AES key from a string (not Base64 encoded)
    private static SecretKey getKeyFromString(String keyString) {
        byte[] keyBytes = keyString.getBytes();
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    // Method to generate a new AES key for game data
    public static SecretKey generateGameKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(128); // AES key size
        return keyGenerator.generateKey();
    }

    // Method to encrypt the game key with the device's serial number
    public static String encryptGameKey(SecretKey gameKey, String serialNumber) throws Exception {
        SecretKey serialKey = getKeyFromString(serialNumber);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, serialKey);
        byte[] encryptedKey = cipher.doFinal(gameKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedKey);
    }

    // Method to decrypt the game key using the device's serial number
    public static SecretKey decryptGameKey(String encryptedKey, String serialNumber) throws Exception {
        SecretKey serialKey = getKeyFromString(serialNumber);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, serialKey);
        byte[] decryptedKey = cipher.doFinal(Base64.getDecoder().decode(encryptedKey));
        return new SecretKeySpec(decryptedKey, ALGORITHM);
    }

    // Method to save the game key with metadata
    public static void saveKeyWithMetadata(String filePath, SecretKey gameKey) throws Exception {
        String serialNumber = SerialNumberManager.decryptSerialNumberFromFile("serial_number_encrypted.hex");
        String encryptedKey = encryptGameKey(gameKey, serialNumber);
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println(encryptedKey);
        }
    }

    // Method to load the game key
    public static SecretKey loadKeyWithMetadata(String filePath) throws Exception {
        String serialNumber = SerialNumberManager.decryptSerialNumberFromFile("serial_number_encrypted.hex");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String encryptedKey = reader.readLine();
            return decryptGameKey(encryptedKey, serialNumber);
        }
    }

    public static void main(String[] args) {
        try {
            // Example usage
            SecretKey gameKey = generateGameKey();
            String filePath = "game_key_metadata.txt";
            saveKeyWithMetadata(filePath, gameKey);

            // Later, when loading
            SecretKey loadedGameKey = loadKeyWithMetadata(filePath);
            System.out.println("Game key loaded and valid.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

