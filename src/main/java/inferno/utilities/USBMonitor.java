package inferno.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fazecast.jSerialComm.SerialPort;
import inferno.user_interface.HomePane;
import inferno.user_interface.library.GameMetadata;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;

public class USBMonitor {
    private static final int SCAN_INTERVAL = 1000; // 1 second
    private static final String MASTER_KEY = "M6D4nIEqtoDlOOAAIbYoTWB1WLtcH6Vl"; // Must match the key used in CircuitPython
    private static final String HANDSHAKE_COMMAND = "RP2040_HANDSHAKE";
    private static final String HANDSHAKE_RESPONSE = "RP2040_ACK";

    private final Timer timer;
    private final ObjectMapper objectMapper;
    private final CartridgeListener listener;
    private GameMetadata cachedMetadata;
    private boolean cartridgeDetected;

    public USBMonitor(CartridgeListener listener) {
        this.listener = listener;
        this.timer = new Timer(SCAN_INTERVAL, new ScanActionListener());
        this.objectMapper = new ObjectMapper();
        this.cachedMetadata = null;
        this.cartridgeDetected = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HomePane homePane = new HomePane();
            USBMonitor monitor = new USBMonitor(homePane);
            monitor.startScanning();

            JFrame frame = new JFrame("Game Interface");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(homePane);
            frame.setSize(400, 200);
            frame.setVisible(true);
        });
    }

    public void startScanning() {
        timer.start();
    }

    private void scanForDevices() {
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            System.out.println("Detected port: " + port.getSystemPortName());
            if (isRP2040(port)) {
                System.out.println("RP2040 detected on port: " + port.getSystemPortName());
                handleDevice(port);
                return;
            }
        }
        if (cartridgeDetected) {
            cartridgeDetected = false;
            cachedMetadata = null;
            listener.onCartridgeRemoved();
        }
    }

    private boolean isRP2040(SerialPort port) {
        // Attempt handshake to verify the device
        return performHandshake(port);
    }

    private boolean performHandshake(SerialPort port) {
        try {
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 5000, 0); // 5 seconds timeout
            if (port.openPort()) {
                OutputStream out = port.getOutputStream();
                InputStream in = port.getInputStream();

                // Send handshake command
                out.write(HANDSHAKE_COMMAND.getBytes(StandardCharsets.UTF_8));
                out.flush();

                // Read handshake response
                byte[] buffer = new byte[256];
                int bytesRead = in.read(buffer);
                String response = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8).trim();
                System.out.println("Received response: " + response);

                // Check if response contains the expected handshake response
                if (response.contains(HANDSHAKE_RESPONSE)) {
                    // Send confirmation message
                    out.write("ACKNOWLEDGED".getBytes(StandardCharsets.UTF_8));
                    out.flush();
                    System.out.println("Sent confirmation message");

                    // Wait for confirmation from the device
                    bytesRead = in.read(buffer);
                    response = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8).trim();
                    System.out.println("Received confirmation: " + response);
                    return "ACKNOWLEDGED".equals(response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            port.closePort();
        }
        return false;
    }

    public void handleDevice(SerialPort port) {
        try {
            if (port.openPort()) {
                // Wait a bit to ensure data has time to arrive
                Thread.sleep(500);


                OutputStream out = port.getOutputStream();
                InputStream in = port.getInputStream();


                out.write("READ_METADATA".getBytes(StandardCharsets.UTF_8));
                out.flush();

                Thread.sleep(500);

                // Check if bytes are available
                if (port.bytesAvailable() > 0) {
                    byte[] data = new byte[port.bytesAvailable()];
                    int bytesRead = port.readBytes(data, data.length);

                    System.out.println("Received Encrypted Data: " + (data));

                    // Decrypt data
                    String decryptedData = decryptData(data);

                    System.out.println(decryptedData);

                    // Check if the decrypted data is empty
                    if (decryptedData.isEmpty()) {
                        System.err.println("Error: Received empty data after decryption.");
                    } else {
                        // Parse JSON data
                        try {
                            GameMetadata metadata = objectMapper.readValue(decryptedData, GameMetadata.class);
                            cachedMetadata = metadata;
                            cartridgeDetected = true;
                            listener.onCartridgeDetected(metadata);
                        } catch (MismatchedInputException e) {
                            System.err.println("JSON Parsing Error: " + e.getMessage());
                        } catch (IOException e) {
                            System.err.println("I/O Error: " + e.getMessage());
                        }
                    }
                } else {
                    System.err.println("No data available to read.");
                }
            } else {
                System.err.println("Failed to open port.");
            }
        } catch (Exception e) {
            System.err.println("Error handling device: " + e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (port.isOpen()) {
                port.closePort();
            }
        }
    }

    public String decryptData(byte[] encryptedData) {
        try {
            // Ensure proper Base64 decoding
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = decrypt(decodedData);
            return new String(decryptedBytes, StandardCharsets.UTF_8).trim();
        } catch (IllegalArgumentException e) {
            System.err.println("Base64 decoding error: " + e.getMessage());
            return "";
        } catch (Exception e) {
            System.err.println("Decryption Error: " + e.getMessage());
            return "";
        }
    }

    public byte[] decrypt(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        byte[] iv = "uWAvj0Siy74SV4cC".getBytes(StandardCharsets.UTF_8);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(MASTER_KEY.getBytes(StandardCharsets.UTF_8), "AES"), ivSpec);
        return cipher.doFinal(encryptedData);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private class ScanActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            scanForDevices();
        }
    }
}
