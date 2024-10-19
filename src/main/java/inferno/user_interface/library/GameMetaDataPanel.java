package inferno.user_interface.library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class GameMetaDataPanel extends JPanel {

    private JLabel titleLabel;
    private JTextArea descriptionArea;
    private JLabel developerLabel;
    private JLabel libraryCardImageLabel;
    private JLabel bannerImageLabel;
    private JButton launchButton;

    private GameMetadata gameMetadata;

    public GameMetaDataPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Game Metadata"));

        // Title Label
        titleLabel = new JLabel("No game available to launch", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Description Area
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        // Developer Label
        developerLabel = new JLabel();
        add(developerLabel, BorderLayout.SOUTH);

        // Library Card Image
        libraryCardImageLabel = new JLabel();
        libraryCardImageLabel.setPreferredSize(new Dimension(200, 300));
        add(libraryCardImageLabel, BorderLayout.WEST);

        // Banner Image
        bannerImageLabel = new JLabel();
        bannerImageLabel.setPreferredSize(new Dimension(800, 200));
        add(bannerImageLabel, BorderLayout.NORTH);

        // Launch Button
        launchButton = new JButton("Launch Game");
        launchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchGame();
            }
        });
        add(launchButton, BorderLayout.SOUTH);
    }

    public void updateGameMetadata(GameMetadata gameMetadata) {
        this.gameMetadata = gameMetadata;
        if (gameMetadata == null) {
            titleLabel.setText("No game available to launch");
            descriptionArea.setText("");
            developerLabel.setText("");
            libraryCardImageLabel.setIcon(null);
            bannerImageLabel.setIcon(null);
        } else {
            titleLabel.setText(gameMetadata.getTitle());
            descriptionArea.setText(gameMetadata.getDescription());
            developerLabel.setText("Developer: " + gameMetadata.getDeveloper());

            try {
                URL libraryCardImageUrl = new URL(gameMetadata.getLibraryCardImage());
                ImageIcon libraryCardImage = new ImageIcon(libraryCardImageUrl);
                libraryCardImageLabel.setIcon(libraryCardImage);
            } catch (Exception e) {
                e.printStackTrace();
                libraryCardImageLabel.setText("Library Card Image not available");
            }

            try {
                URL bannerImageUrl = new URL(gameMetadata.getBannerImage());
                ImageIcon bannerImage = new ImageIcon(bannerImageUrl);
                bannerImageLabel.setIcon(bannerImage);
            } catch (Exception e) {
                e.printStackTrace();
                bannerImageLabel.setText("Banner Image not available");
            }
        }
    }

    private void launchGame() {
        if (gameMetadata != null) {
            // Get the OS-specific path based on the current OS
            String osName = System.getProperty("os.name").toLowerCase();
            String path = gameMetadata.getOsPaths().get(osName);

            if (path != null) {
                try {
                    // Launch the game using the path from metadata
                    Runtime.getRuntime().exec(path);
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to launch the game.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No launch path available for your OS.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No game metadata available.");
        }
    }
}
