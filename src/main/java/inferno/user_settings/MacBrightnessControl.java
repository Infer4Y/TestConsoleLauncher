package inferno.user_settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MacBrightnessControl {

    public static void setBrightness(int brightness) throws IOException {
        // Convert brightness value to the range expected by the `brightness` command
        double normalizedBrightness = brightness / 100.0;
        ProcessBuilder processBuilder = new ProcessBuilder("brightness", String.valueOf(normalizedBrightness));
        processBuilder.inheritIO().start();
    }

    public static int getBrightness() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("brightness");
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            double brightness = Double.parseDouble(reader.readLine().trim());
            return (int) (brightness * 100);
        }
    }
}
