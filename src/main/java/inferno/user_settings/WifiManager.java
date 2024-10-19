package inferno.user_settings;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WifiManager {
    public void scanNetworks(JTextArea outputArea) {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder builder;
        if (os.contains("win")) {
            builder = new ProcessBuilder("cmd.exe", "/c", "netsh wlan show networks");
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            builder = new ProcessBuilder("nmcli", "device", "wifi", "list");
        } else {
            throw new UnsupportedOperationException("Unsupported operating system");
        }

        String output = executeCommand(builder);
        outputArea.setText(output);
    }

    public void connectToNetwork(String ssid, String password) {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder builder;
        if (os.contains("win")) {
            builder = new ProcessBuilder("cmd.exe", "/c", "netsh wlan connect ssid=" + ssid + " name=" + ssid);
            createProfile(ssid, password); // Create profile if needed
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            builder = new ProcessBuilder("nmcli", "device", "wifi", "connect", ssid, "password", password);
        } else {
            throw new UnsupportedOperationException("Unsupported operating system");
        }

        executeCommand(builder);
    }

    private void createProfile(String ssid, String password) {
        String profile = "<?xml version=\"1.0\"?><WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\"><name>"
                + ssid
                + "</name><SSIDConfig><SSID><name>"
                + ssid
                + "</name></SSID></SSIDConfig><connectionType>ESS</connectionType><connectionMode>auto</connectionMode><MSM><security><authEncryption><authentication>WPA2PSK</authentication><encryption>AES</encryption><useOneX>false</useOneX></authEncryption><sharedKey><keyType>passPhrase</keyType><protected>false</protected><keyMaterial>"
                + password
                + "</keyMaterial></sharedKey></security></MSM></WLANProfile>";
        try {
            Files.write(Paths.get(ssid + ".xml"), profile.getBytes());
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "netsh wlan add profile filename=\"" + ssid + ".xml\"");
            executeCommand(builder);
            Files.delete(Paths.get(ssid + ".xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayConnectionStatus(JTextArea outputArea) {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder builder;
        if (os.contains("win")) {
            builder = new ProcessBuilder("cmd.exe", "/c", "netsh wlan show interfaces");
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            builder = new ProcessBuilder("nmcli", "-t", "-f", "active,ssid", "dev", "wifi");
        } else {
            throw new UnsupportedOperationException("Unsupported operating system");
        }

        String output = executeCommand(builder);
        outputArea.setText(output);
    }

    private String executeCommand(ProcessBuilder builder) {
        StringBuilder output = new StringBuilder();
        try {
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
}
