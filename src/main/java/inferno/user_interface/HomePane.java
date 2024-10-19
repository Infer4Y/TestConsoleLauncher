package inferno.user_interface;

import inferno.user_interface.library.GameMetadata;
import inferno.utilities.CartridgeListener;

import javax.swing.*;

public class HomePane extends JPanel implements CartridgeListener {

    private JLabel statusLabel;

    public HomePane() {
        statusLabel = new JLabel("No cartridge detected.");
        add(statusLabel);
    }

    @Override
    public void onCartridgeDetected(GameMetadata metadata) {
        statusLabel.setText("Cartridge detected: " + metadata.getTitle());
        // Update UI with metadata details
    }

    @Override
    public void onCartridgeRemoved() {
        statusLabel.setText("No cartridge detected.");
        // Clear UI details
    }
}

