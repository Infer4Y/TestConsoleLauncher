package inferno.user_interface.library;

import inferno.Main;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class LibraryTabManager extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private JPanel gamesPanel;
    private JPanel detailsPanel;
    private GameMetadata currentMetadata;

    public LibraryTabManager() {
        setLayout(new BorderLayout());

        // Initialize CardLayout and panel container
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);

        // Panel for game cards
        gamesPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        JScrollPane scrollPane = new JScrollPane(gamesPanel);
        cardsPanel.add(scrollPane, "Library");

        // Panel for details
        detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.add(new JLabel("Select a game to see details."), BorderLayout.CENTER);

        cardsPanel.add(detailsPanel, "Details");

        add(cardsPanel, BorderLayout.CENTER);

        // Set font for the whole panel
        setFont(Main.chakra_petch);

        loadGamesFromDirectory();
    }

    public void showGameDetails(GameMetadata metadata) {
        this.currentMetadata = metadata;
        detailsPanel.removeAll();
        detailsPanel.setLayout(new BorderLayout());

        JTextArea detailsTextArea = new JTextArea();
        detailsTextArea.setText("Title: " + metadata.getTitle() + "\n" +
                "Description: " + metadata.getDescription() + "\n" +
                "Developer: " + metadata.getDeveloper() + "\n" +
                "OS Paths: " + metadata.getOsPaths());
        detailsTextArea.setFont(Main.chakra_petch);
        detailsTextArea.setEditable(false);
        detailsPanel.add(new JScrollPane(detailsTextArea), BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton returnButton = new JButton("Return to Library");
        returnButton.setFont(Main.chakra_petch);
        returnButton.addActionListener(e -> cardLayout.show(cardsPanel, "Library"));
        buttonPanel.add(returnButton);

        JButton playButton = new JButton("Play");
        playButton.setFont(Main.chakra_petch);
        playButton.addActionListener(e -> playCurrentGame());
        buttonPanel.add(playButton);

        detailsPanel.add(buttonPanel, BorderLayout.SOUTH);

        cardLayout.show(cardsPanel, "Details");
    }

    private void playCurrentGame() {
        if (currentMetadata != null) {
            String gameFilePath = currentMetadata.getOsPaths().get(System.getProperty("os.name").toLowerCase());
            if (gameFilePath != null) {
                // Handle game execution here
                System.out.println("Playing game: " + gameFilePath);
            } else {
                System.out.println("No executable path for current OS.");
            }
        } else {
            System.out.println("No game selected.");
        }
    }

    public void loadGamesFromDirectory() {
        String workingDirPath = System.getProperty("user.dir");
        File dir = new File(workingDirPath);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try {
                        GameMetadata metadata = JsonUtils.parseMetadata(file);
                        String libraryCardImageUrl = metadata.getLibraryCardImage();
                        GameCard gameCard = new GameCard(metadata, libraryCardImageUrl, this);
                        gamesPanel.add(gameCard);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                revalidate();
                repaint();
            }
        }
    }
}
