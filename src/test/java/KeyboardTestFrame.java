import inferno.user_interface.FullOnScreenKeyboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KeyboardTestFrame extends JFrame {

    private FullOnScreenKeyboard keyboard;
    private boolean isKeyboardVisible = false;

    public KeyboardTestFrame() {
        setTitle("On-Screen Keyboard Example");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        keyboard = new FullOnScreenKeyboard();
        add(keyboard, BorderLayout.SOUTH);
        keyboard.setVisible(false);

        JButton toggleKeyboardButton = new JButton("Toggle Keyboard");
        toggleKeyboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isKeyboardVisible = !isKeyboardVisible;
                keyboard.setVisible(isKeyboardVisible);
            }
        });

        add(toggleKeyboardButton, BorderLayout.NORTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new KeyboardTestFrame().setVisible(true);
        });
    }
}
