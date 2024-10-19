package inferno.user_settings;

import java.io.IOException;

public class BrightnessControl {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static void setBrightness(int brightness) {
        try {
            if (OS.contains("win")) {
                WindowsBrightnessControl.setBrightness(brightness);
            } else if (OS.contains("nix") || OS.contains("nux")) {
                LinuxBrightnessControl.setBrightness(brightness);
            } else if (OS.contains("mac")) {
                MacBrightnessControl.setBrightness(brightness);
            } else {
                throw new UnsupportedOperationException("Unsupported OS: " + OS);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getBrightness() {
        try {
            if (OS.contains("win")) {
                return WindowsBrightnessControl.getBrightness();
            } else if (OS.contains("nix") || OS.contains("nux")) {
                return LinuxBrightnessControl.getBrightness();
            } else if (OS.contains("mac")) {
                return MacBrightnessControl.getBrightness();
            } else {
                throw new UnsupportedOperationException("Unsupported OS: " + OS);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 50; // Return a default value if there is an error
        }
    }
}