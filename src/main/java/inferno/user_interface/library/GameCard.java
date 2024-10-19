package inferno.user_interface.library;

import inferno.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class GameCard extends JPanel {

    private JLabel titleLabel;
    private JLabel descriptionLabel;
    private JLabel developerLabel;
    private JButton quickPlayButton;
    private JButton showDetailsButton;
    private JLabel imageLabel;
    private GameMetadata metadata;
    private LibraryTabManager libraryTabManager;

    public GameCard(GameMetadata metadata, String libraryCardImageUrl, LibraryTabManager libraryTabManager) {
        this.metadata = metadata;
        this.libraryTabManager = libraryTabManager;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setPreferredSize(new Dimension(300, 150));

        // Set font for all components
        setFont(Main.chakra_petch);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        titleLabel = new JLabel(metadata.getTitle());
        titleLabel.setFont(Main.chakra_petch.deriveFont(Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // Description
        descriptionLabel = new JLabel("<html>" + metadata.getDescription() + "</html>");
        descriptionLabel.setFont(Main.chakra_petch);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(descriptionLabel, gbc);

        // Developer
        developerLabel = new JLabel("Developer: " + metadata.getDeveloper());
        developerLabel.setFont(Main.chakra_petch);
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(developerLabel, gbc);

        // Image
        imageLabel = new JLabel();
        try {
            BufferedImage image = loadImage(libraryCardImageUrl);
            if (image != null) {
                imageLabel.setIcon(new ImageIcon(image));
            } else {
                imageLabel.setIcon(new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB))); // Blank image
            }
        } catch (IOException e) {
            e.printStackTrace();
            imageLabel.setIcon(new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB))); // Blank image
        }
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        add(imageLabel, gbc);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        quickPlayButton = new JButton("Quick Play");
        quickPlayButton.setFont(Main.chakra_petch);
        buttonPanel.add(quickPlayButton);

        showDetailsButton = new JButton("Show Details");
        showDetailsButton.setFont(Main.chakra_petch);
        buttonPanel.add(showDetailsButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(buttonPanel, gbc);

        // Add button functionalities
        quickPlayButton.addActionListener(e -> {
            String gameFilePath = metadata.getOsPaths().get(System.getProperty("os.name").toLowerCase());
            if (gameFilePath != null) {
                // Handle game execution here
                System.out.println("Quick Play: " + gameFilePath);
            } else {
                System.out.println("No executable path for current OS.");
            }
        });

        showDetailsButton.addActionListener(e -> showGameDetails());
    }

    private BufferedImage loadImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        return ImageIO.read(url);
    }

    private void showGameDetails() {
        if (libraryTabManager != null) {
            libraryTabManager.showGameDetails(metadata);
        } else {
            System.out.println("LibraryTabManager not found.");
        }
    }
}
