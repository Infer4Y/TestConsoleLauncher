package inferno.user_settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class LinuxBrightnessControl {

    private static final String[] BRIGHTNESS_FILE_PATHS = {
            "/sys/class/backlight/intel_backlight/brightness",
            "/sys/class/backlight/acpi_video0/brightness",
            "/sys/class/backlight/thinkpad_screen/brightness" // Add or modify as needed
    };

    private static final String XRANDR_COMMAND = "xrandr";
    private static final String XRANDR_OUTPUT = "eDP-1"; // Replace with your output device name

    // Max brightness value
    private static final int MAX_BRIGHTNESS = 100; // Default, adjust as needed

    public static int getBrightness() throws UnsupportedOperationException {
        for (String path : BRIGHTNESS_FILE_PATHS) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                        int brightness = Integer.parseInt(reader.readLine().trim());
                        return (int) convertToPercentage(brightness, path);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Failed to read brightness from path: " + path);
            }
        }

        // If no path worked, attempt xrandr
        return (int) getBrightnessUsingXrandr();
    }

    private static double getBrightnessUsingXrandr() throws UnsupportedOperationException {
        try {
            ProcessBuilder pb = new ProcessBuilder(XRANDR_COMMAND, "--verbose");
            Process process = pb.start();
            process.waitFor();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Brightness:")) {
                        double brightness = Double.parseDouble(line.split(":")[1].trim());
                        return brightness * 100; // Normalize to 0-100 range
                    }
                }
            }
        } catch (IOException | InterruptedException | NumberFormatException e) {
            System.err.println("Failed to execute xrandr command");
            e.printStackTrace();
        }

        throw new UnsupportedOperationException("No suitable brightness retrieval method found.");
    }

    public static boolean setBrightness(int brightness) throws UnsupportedOperationException {
        if (brightness < 0 || brightness > 100) {
            throw new IllegalArgumentException("Brightness must be between 0 and 100.");
        }

        for (String path : BRIGHTNESS_FILE_PATHS) {
            try {
                if (writeBrightnessToFile(path, convertToSystemValue(brightness, path))) {
                    return true;
                }
            } catch (IOException e) {
                System.err.println("Failed to write brightness to path: " + path);
            }
        }

        // If no path worked, attempt xrandr
        return setBrightnessUsingXrandr(brightness);
    }

    private static boolean writeBrightnessToFile(String path, int brightness) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            System.err.println("Brightness file does not exist: " + path);
            return false;
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(String.valueOf(brightness));
        }
        return true;
    }

    private static boolean setBrightnessUsingXrandr(int brightness) {
        double normalizedBrightness = brightness / 100.0;
        try {
            ProcessBuilder pb = new ProcessBuilder(XRANDR_COMMAND, "--output", XRANDR_OUTPUT, "--brightness", String.valueOf(normalizedBrightness));
            Process process = pb.start();
            process.waitFor();
            if (process.exitValue() != 0) {
                System.err.println("xrandr command failed with exit code: " + process.exitValue());
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println(line);
                    }
                }
            }
            return process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to execute xrandr command");
            e.printStackTrace();
        }
        return false;
    }

    private static double convertToPercentage(int brightness, String path) {
        // Assuming max brightness value is read from the file
        int maxBrightness = getMaxBrightness(path);
        return (brightness / (double) maxBrightness) * 100;
    }

    private static int convertToSystemValue(int brightness, String path) {
        // Assuming max brightness value is read from the file
        int maxBrightness = getMaxBrightness(path);
        return (int) ((brightness / 100.0) * maxBrightness);
    }

    private static int getMaxBrightness(String path) {
        // Default maximum value if not read from the file
        // Adjust according to your system's maximum brightness value
        return MAX_BRIGHTNESS;
    }
}

