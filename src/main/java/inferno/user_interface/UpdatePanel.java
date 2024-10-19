package inferno.user_interface;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inferno.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class UpdatePanel extends JPanel {
    private static final String VERSION_URL = "https://example.com/version"; // URL to check for updates
    private static final String DOWNLOAD_URL = "https://example.com/download"; // Base URL to download the JAR

    private final JLabel statusLabel;
    private final JButton checkUpdateButton;

    public UpdatePanel() {
        setLayout(new BorderLayout());

        statusLabel = new JLabel("Check for updates");
        checkUpdateButton = new JButton("Check for Updates");

        add(statusLabel, BorderLayout.CENTER);
        add(checkUpdateButton, BorderLayout.SOUTH);

        checkUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> checkForUpdates()).start();
            }
        });

        // Check for updates on program start
        checkForUpdates();
    }

    private void checkForUpdates() {
        try {
            // Fetch current version from Main class
            String currentVersion = Main.getVersion(); // Replace with actual method to get version

            // Fetch the latest version from the server
            HttpURLConnection connection = (HttpURLConnection) new URL(VERSION_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            InputStream responseStream = connection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(responseStream);

            String latestVersion = responseJson.get("version").asText();
            String downloadUrl = responseJson.get("url").asText();

            if (isNewVersionAvailable(currentVersion, latestVersion)) {
                statusLabel.setText("Update available. Downloading...");
                downloadAndUpdate(downloadUrl);
            } else {
                statusLabel.setText("No updates available.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error checking for updates.");
        }
    }

    private boolean isNewVersionAvailable(String currentVersion, String latestVersion) {
        // Simple version comparison; you may need a more robust implementation
        return !Objects.equals(currentVersion, latestVersion);
    }

    private void downloadAndUpdate(String downloadUrl) {
        try {
            // Download the new JAR file
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            InputStream inputStream = connection.getInputStream();
            File tempFile = File.createTempFile("update", ".jar");
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // Replace the existing JAR file
            File currentJar = new File(UpdatePanel.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Files.move(tempFile.toPath(), currentJar.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Update .bashrc on Linux
            if (System.getProperty("os.name").toLowerCase().contains("nix") ||
                    System.getProperty("os.name").toLowerCase().contains("nux") ||
                    System.getProperty("os.name").toLowerCase().contains("mac")) {
                updateBashrc();
            }

            statusLabel.setText("Update completed. Please restart the application.");

        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error updating the application.");
        }
    }

    private void updateBashrc() {
        try {
            String bashrcPath = System.getProperty("user.home") + "/.bashrc";
            String newPath = "path/to/your/new/executable"; // Update this as needed

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(bashrcPath, true))) {
                writer.write("export PATH=" + newPath + ":$PATH");
                writer.newLine();
            }

            // Inform the user to source the .bashrc file or restart the terminal
            statusLabel.setText("Updated .bashrc. Please restart your terminal or source the file.");

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error updating .bashrc.");
        }
    }
}
