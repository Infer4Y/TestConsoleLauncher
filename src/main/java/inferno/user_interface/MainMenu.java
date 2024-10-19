package inferno.user_interface;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import inferno.Main;
import inferno.user_interface.library.LibraryTabManager;
import inferno.user_interface.store.MicroStoreTab;
import inferno.user_settings.BrightnessControl;
import inferno.utilities.CartridgeListener;
import inferno.utilities.USBMonitor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.Locale;

import static inferno.Main.isKeyboardVisible;
import static inferno.Main.keyboard;

public class MainMenu {
    public JTabbedPane menuTabs;
    public JPanel homeTab;
    public JPanel libraryTab;
    public JPanel microStoreTab;
    public JPanel friendsTab;
    public JPanel settingsTab;
    public JPanel powerTab;
    public JPanel mainMenu;
    public JScrollPane homePane;
    public JButton restartButton;
    public JButton sleepButton;
    public JButton powerOffButton;
    public JTabbedPane settingsTabbedPane;
    public JPanel displaySettings;
    public JPanel soundsSettings;
    public JPanel internetSettings;
    public JTextArea internetConnectionsArea;
    public JTextField SSIDTextField;
    public JButton scanNetworksButton;
    public JButton connectButton;
    public JTextField passwordTextField;
    public JComboBox themeBox;
    public JSlider brightnessSlider;
    public JLabel themeLabel;
    public JLabel brightnessLabel;
    public JLabel ssidLabel;
    public JLabel passwordLabel;
    public JButton exitToDesktopButton;
    public JPanel updatePanel;
    public JButton keyboardToggle;
    public JCheckBox showKeyboardToggle;
    public JLabel keyboardEnableLabel;

    public MainMenu() {
        $$$setupUI$$$();
        sleepButton.addActionListener(e -> {
            String os = System.getProperty("os.name").toLowerCase();

            try {
                if (os.contains("win")) {
                    // Windows
                    Runtime.getRuntime().exec("rundll32.exe powrprof.dll,SetSuspendState Sleep");
                } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                    // Unix/Linux/Mac
                    Runtime.getRuntime().exec("systemctl suspend");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        restartButton.addActionListener(e -> {
            try {
                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("win")) {
                    // Windows
                    //Runtime.getRuntime().exec("shutdown -r -t 0");
                } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                    // Unix/Linux/Mac
                    Runtime.getRuntime().exec("reboot");
                    ;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        exitToDesktopButton.addActionListener(e -> System.exit(0));

        powerOffButton.addActionListener(e -> {
            //System.exit(0);

            try {
                String[] shutdownCommand = null;
                String operatingSystem = System.getProperty("os.name");

                if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
                    shutdownCommand = new String[]{"shutdown", "-h", "now"};
                }
                // This will work on any version of windows including version 11
                //else if (operatingSystem.contains("Windows")) {
                //shutdownCommand = new String[]{"shutdown.exe","-s","-t","0"};
                //}
                Runtime.getRuntime().exec(shutdownCommand);
            } catch (IOException ex) {

            } finally {
                System.exit(0);
            }
        });

        SSIDTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                keyboard.setFocusedComponent(SSIDTextField);
                keyboard.setComponentFocus(SSIDTextField);
                Main.openKeyboard();
            }
        });

        passwordTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                keyboard.setFocusedComponent(passwordTextField);
                keyboard.setComponentFocus(passwordTextField);
                Main.openKeyboard();
            }
        });


        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ssid = SSIDTextField.getText();
                String password = passwordTextField.getText();
                Main.wifiManager.connectToNetwork(ssid, password);
            }
        });
        scanNetworksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.wifiManager.scanNetworks(internetConnectionsArea);
            }
        });
        themeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTheme = (String) themeBox.getSelectedItem();

                switch (selectedTheme) {
                    case "Light":
                        FlatLightLaf.setup();
                        break;
                    case "Dark":
                        FlatDarculaLaf.setup();
                        break;
                    case "Orange":
                        FlatArcOrangeIJTheme.setup();
                        break;
                    case "Blue":
                        FlatCarbonIJTheme.setup();
                        break;
                    case "Purple":
                        FlatDarkPurpleIJTheme.setup();
                        break;
                }

                SwingUtilities.updateComponentTreeUI(mainMenu);

            }
        });

        brightnessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int brightness = brightnessSlider.getValue();
                BrightnessControl.setBrightness(brightness);
            }
        });

        keyboardToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isKeyboardVisible = !isKeyboardVisible;
                keyboard.setVisible(isKeyboardVisible);
            }
        });


        menuTabs.setFont(Main.chakra_petch);
        libraryTab.setFont(Main.chakra_petch);
        microStoreTab.setFont(Main.chakra_petch);
        friendsTab.setFont(Main.chakra_petch);
        settingsTab.setFont(Main.chakra_petch);
        powerTab.setFont(Main.chakra_petch);
        mainMenu.setFont(Main.chakra_petch);
        restartButton.setFont(Main.chakra_petch);
        sleepButton.setFont(Main.chakra_petch);
        powerOffButton.setFont(Main.chakra_petch);
        exitToDesktopButton.setFont(Main.chakra_petch);
        settingsTabbedPane.setFont(Main.chakra_petch);
        displaySettings.setFont(Main.chakra_petch);
        soundsSettings.setFont(Main.chakra_petch);
        internetSettings.setFont(Main.chakra_petch);
        internetConnectionsArea.setFont(Main.chakra_petch);
        SSIDTextField.setFont(Main.chakra_petch);
        ssidLabel.setFont(Main.chakra_petch);
        scanNetworksButton.setFont(Main.chakra_petch);
        connectButton.setFont(Main.chakra_petch);
        passwordTextField.setFont(Main.chakra_petch);
        passwordLabel.setFont(Main.chakra_petch);
        themeBox.setFont(Main.chakra_petch);
        brightnessSlider.setFont(Main.chakra_petch);
        themeLabel.setFont(Main.chakra_petch);
        brightnessLabel.setFont(Main.chakra_petch);
        keyboardToggle.setFont(Main.chakra_petch);
        keyboardEnableLabel.setFont(Main.chakra_petch);

        themeBox.setSelectedItem("Purple");

        brightnessSlider.setValue(BrightnessControl.getBrightness());

        showKeyboardToggle.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                keyboardToggle.setVisible(showKeyboardToggle.isSelected());
            }
        });
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainMenu = new JPanel();
        mainMenu.setLayout(new BorderLayout(0, 0));
        Font mainMenuFont = this.$$$getFont$$$(null, -1, -1, mainMenu.getFont());
        if (mainMenuFont != null) mainMenu.setFont(mainMenuFont);
        mainMenu.setMaximumSize(new Dimension(1280, 800));
        mainMenu.setMinimumSize(new Dimension(0, 0));
        menuTabs = new JTabbedPane();
        mainMenu.add(menuTabs, BorderLayout.CENTER);
        menuTabs.addTab("Home", homeTab);
        menuTabs.addTab("microStore", microStoreTab);
        menuTabs.addTab("Library", libraryTab);
        friendsTab = new JPanel();
        friendsTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        menuTabs.addTab("Friends", friendsTab);
        settingsTab = new JPanel();
        settingsTab.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        menuTabs.addTab("Settings", settingsTab);
        settingsTabbedPane = new JTabbedPane();
        settingsTab.add(settingsTabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        displaySettings = new JPanel();
        displaySettings.setLayout(new GridLayoutManager(5, 3, new Insets(0, 0, 0, 0), -1, -1));
        settingsTabbedPane.addTab("Display", displaySettings);
        themeLabel = new JLabel();
        themeLabel.setText("Theme");
        displaySettings.add(themeLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(200, -1), null, 0, false));
        brightnessLabel = new JLabel();
        brightnessLabel.setText("Brightness");
        displaySettings.add(brightnessLabel, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        themeBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Dark");
        defaultComboBoxModel1.addElement("Light");
        defaultComboBoxModel1.addElement("Orange");
        defaultComboBoxModel1.addElement("Purple");
        defaultComboBoxModel1.addElement("Blue");
        themeBox.setModel(defaultComboBoxModel1);
        displaySettings.add(themeBox, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(200, -1), null, 0, false));
        brightnessSlider = new JSlider();
        brightnessSlider.setInverted(false);
        brightnessSlider.setMajorTickSpacing(10);
        brightnessSlider.setPaintLabels(true);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setSnapToTicks(true);
        brightnessSlider.setValueIsAdjusting(true);
        brightnessSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
        brightnessSlider.putClientProperty("Slider.paintThumbArrowShape", Boolean.FALSE);
        displaySettings.add(brightnessSlider, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        displaySettings.add(spacer1, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("");
        displaySettings.add(label1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showKeyboardToggle = new JCheckBox();
        showKeyboardToggle.setSelected(false);
        showKeyboardToggle.setText("");
        displaySettings.add(showKeyboardToggle, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(40, 40), null, 0, false));
        keyboardEnableLabel = new JLabel();
        keyboardEnableLabel.setName("");
        keyboardEnableLabel.setText("Show Keyboard");
        displaySettings.add(keyboardEnableLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        soundsSettings = new JPanel();
        soundsSettings.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        settingsTabbedPane.addTab("Sounds", soundsSettings);
        internetSettings = new JPanel();
        internetSettings.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        settingsTabbedPane.addTab("Internet", internetSettings);
        SSIDTextField = new JTextField();
        SSIDTextField.setText("SSID");
        internetSettings.add(SSIDTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        scanNetworksButton = new JButton();
        scanNetworksButton.setText("Scan Networks");
        internetSettings.add(scanNetworksButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        connectButton = new JButton();
        connectButton.setText("Connect");
        internetSettings.add(connectButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passwordTextField = new JTextField();
        passwordTextField.setText("password");
        internetSettings.add(passwordTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        ssidLabel = new JLabel();
        ssidLabel.setText("SSID");
        internetSettings.add(ssidLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passwordLabel = new JLabel();
        passwordLabel.setText("PASSWORD");
        internetSettings.add(passwordLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        internetSettings.add(scrollPane1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        internetConnectionsArea = new JTextArea();
        scrollPane1.setViewportView(internetConnectionsArea);
        settingsTabbedPane.addTab("Updates", updatePanel);
        powerTab = new JPanel();
        powerTab.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        menuTabs.addTab("Power", powerTab);
        sleepButton = new JButton();
        sleepButton.setText("Sleep");
        powerTab.add(sleepButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        restartButton = new JButton();
        restartButton.setText("Restart");
        powerTab.add(restartButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        powerOffButton = new JButton();
        powerOffButton.setText("Power Off");
        powerTab.add(powerOffButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitToDesktopButton = new JButton();
        exitToDesktopButton.setText("Exit To Desktop");
        powerTab.add(exitToDesktopButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        keyboardToggle = new JButton();
        keyboardToggle.setOpaque(true);
        keyboardToggle.setPreferredSize(new Dimension(140, 50));
        keyboardToggle.setText("Keyboard Toggle");
        keyboardToggle.setVisible(false);
        mainMenu.add(keyboardToggle, BorderLayout.NORTH);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainMenu;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        microStoreTab = new MicroStoreTab();
        libraryTab = new LibraryTabManager();
        homeTab = new HomePane();
        updatePanel = new UpdatePanel();

        USBMonitor monitor = new USBMonitor((CartridgeListener) homeTab);
        monitor.startScanning();
    }
}
