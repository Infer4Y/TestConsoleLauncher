package inferno.utilities;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class FontInstaller {
    public static void installFont(String fontPath) {
        try (InputStream fontStream = FontInstaller.class.getResourceAsStream(fontPath)) {
            if (fontStream != null) {
                // Load the font from the input stream
                Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                // Register the font
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(font);
                System.out.println("Font registered successfully.");
            } else {
                System.err.println("Font file not found: " + fontPath);
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }
}
