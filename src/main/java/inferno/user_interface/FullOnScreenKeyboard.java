package inferno.user_interface;
import inferno.Main;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class FullOnScreenKeyboard extends JPanel {

    private boolean shiftActive = false;
    private boolean capsActive = false;
    private JComponent focusedComponent; // Component to receive key events

    // Define key mappings
    private final String[] normalKeys = {
            "`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=", "Backspace",
            "Tab", "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "[", "]", "\\",
            "a", "s", "d", "f", "g", "h", "j", "k", "l", ";", "'", "Enter",
            "Shift", "z", "x", "c", "v", "b", "n", "m", ",", ".", "/", "Shift",
            "Space", "Ctrl", "Alt", "Cmd", "Alt", "Ctrl", "Delete", "Close Keyboard"
    };

    private final String[] shiftedKeys = {
            "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "Backspace",
            "Tab", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "{", "}", "|",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", ":", "\"", "Enter",
            "Shift", "Z", "X", "C", "V", "B", "N", "M", "<", ">", "?", "Shift",
            "Space", "Ctrl", "Alt", "Cmd", "Alt", "Ctrl", "Delete", "Close Keyboard"
    };

    public FullOnScreenKeyboard() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Initialize the keyboard layout
        int keyIndex = 0;
        for (int row = 0; row < 6; row++) {
            gbc.gridy = row;
            gbc.gridwidth = 1;
            for (int col = 0; col < 14; col++) {
                if (keyIndex < normalKeys.length) {
                    addKeyButton(normalKeys[keyIndex], shiftedKeys[keyIndex], gbc);
                    keyIndex++;
                }
            }
        }
    }

    private void addKeyButton(String normalKey, String shiftedKey, GridBagConstraints gbc) {
        JButton button = new JButton(normalKey);
        switch (normalKey) {
            case "Shift":
            case "Backspace":
            case "Delete":
            case "Enter":
            case "Space":
            case "Ctrl":
            case "Alt":
            case "Cmd":
            case "Close Keyboard":
                // Implement special key functionality if needed
                break;
            default:
                button.setPreferredSize(new Dimension(60, 40));
                break;
        }
        button.setFont(Main.chakra_petch); // Set font to "chakra_petch"
        button.addActionListener(new KeyActionListener(normalKey, shiftedKey, button));
        add(button, gbc);
    }

    public Component getFocusedComponent() {
        return focusedComponent;
    }

    public void insertText(String text) {
        if (focusedComponent instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) focusedComponent;
            int caretPosition = textComponent.getCaretPosition();
            textComponent.setText(
                    textComponent.getText().substring(0, caretPosition) +
                            text +
                            textComponent.getText().substring(caretPosition)
            );
            textComponent.setCaretPosition(caretPosition + text.length());
        }
    }

    private class KeyActionListener implements ActionListener {
        private final String normalKey;
        private final String shiftedKey;
        private final JButton button;

        public KeyActionListener(String normalKey, String shiftedKey, JButton button) {
            this.normalKey = normalKey;
            this.shiftedKey = shiftedKey;
            this.button = button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (focusedComponent != null) {
                String keyToSend = shiftActive ? shiftedKey : normalKey;

                switch (keyToSend) {
                    case "Shift":
                        shiftActive = !shiftActive;
                        updateShiftedKeys();
                        break;
                    case "Backspace":
                        deleteLastCharacter();
                        break;
                    case "Delete":
                        clearText();
                        break;
                    case "Enter":
                        insertText("\n");
                        break;
                    case "Space":
                        insertText(" ");
                        break;
                    case "Close Keyboard":
                        Main.isKeyboardVisible = false;
                        Main.keyboard.setVisible(false);
                        break;
                    case "Ctrl":
                    case "Alt":
                    case "Cmd":
                        // Implement special key functionality if needed
                        break;
                    default:
                        insertText(keyToSend);
                        break;
                }
            }
        }

        public void insertText(String text) {
            if (focusedComponent instanceof JTextComponent) {
                JTextComponent textComponent = (JTextComponent) focusedComponent;
                int caretPosition = textComponent.getCaretPosition();
                textComponent.setText(
                        textComponent.getText().substring(0, caretPosition) +
                                text +
                                textComponent.getText().substring(caretPosition)
                );
                textComponent.setCaretPosition(caretPosition + text.length());
            }
        }

        private void deleteLastCharacter() {
            if (focusedComponent instanceof JTextComponent) {
                JTextComponent textComponent = (JTextComponent) focusedComponent;
                int caretPosition = textComponent.getCaretPosition();
                if (caretPosition > 0) {
                    String text = textComponent.getText();
                    textComponent.setText(
                            text.substring(0, caretPosition - 1) +
                                    text.substring(caretPosition)
                    );
                    textComponent.setCaretPosition(caretPosition - 1);
                }
            }
        }

        private void clearText() {
            if (focusedComponent instanceof JTextComponent) {
                JTextComponent textComponent = (JTextComponent) focusedComponent;
                textComponent.setText("");
            }
        }

        private void updateShiftedKeys() {
            Component[] components = getComponents();
            for (Component component : components) {
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    String text = button.getText();
                    if (text.length() > 0 && !text.equals("Shift") && !text.equals("Caps Lock")) {
                        String newText = shiftActive ? getShiftedKey(text) : getNormalKey(text);
                        button.setText(newText);
                    }
                }
            }
        }

        private String getShiftedKey(String key) {
            for (int i = 0; i < normalKeys.length; i++) {
                if (normalKeys[i].equals(key)) {
                    return shiftedKeys[i];
                }
            }
            return key;
        }

        private String getNormalKey(String key) {
            for (int i = 0; i < shiftedKeys.length; i++) {
                if (shiftedKeys[i].equals(key)) {
                    return normalKeys[i];
                }
            }
            return key;
        }
    }

    public void setFocusedComponent(JComponent component) {
        this.focusedComponent = component;
    }

    public void setComponentFocus(JComponent component) {
        if (component != null) {
            component.requestFocusInWindow();
        }
    }
}
