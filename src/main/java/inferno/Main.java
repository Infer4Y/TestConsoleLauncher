package inferno;

import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import inferno.user_interface.FullOnScreenKeyboard;
import inferno.user_interface.MainMenu;
import inferno.user_settings.WifiManager;
import inferno.utilities.FontInstaller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {
    public static WifiManager wifiManager = new WifiManager();

    public static JFrame frame;

    public static Font chakra_petch;

    public static FullOnScreenKeyboard keyboard;
    public static boolean isKeyboardVisible = false;


    public static void main(String[] args) {
        FontInstaller.installFont("/fonts/chakra_petch/ChakraPetch-Regular.ttf");
        chakra_petch = new Font("Chakra Petch", Font.PLAIN, 20);
        FlatDarkPurpleIJTheme.setup();
        MainMenu mainMenu = new MainMenu();

        frame = new JFrame("MainMenu");
        frame.setContentPane(mainMenu.mainMenu);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setSize(1600, 900);
        frame.setVisible(true);

        keyboard = new FullOnScreenKeyboard();
        frame.add(keyboard, BorderLayout.SOUTH);
        keyboard.setVisible(false);

        System.out.println("Hello world!");
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (keyboard.getFocusedComponent() != null) {
                    int keyCode = e.getKeyCode();
                    String keyText = KeyEvent.getKeyText(keyCode);
                    if (keyText.length() > 0) {
                        keyboard.insertText(keyText);
                    }
                }
            }
        });
    }

    public static void openKeyboard(){
        isKeyboardVisible = true;
        keyboard.setVisible(isKeyboardVisible);
    }

    public static String getVersion() {
        return "1.0.0";
    }
}