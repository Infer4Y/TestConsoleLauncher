package inferno.user_interface.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inferno.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static inferno.Main.keyboard;

public class MicroStoreTab extends JPanel {
    private static final String STEAM_FEATURED_URL = "https://store.steampowered.com/api/featuredcategories";
    private static final String SEARCH_URL = "https://store.steampowered.com/api/storesearch/?term=";
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel detailPanel;
    private JPanel topBar;

    public MicroStoreTab() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Top bar (search bar and navigation)
        topBar = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField("Search...");
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                keyboard.setFocusedComponent(searchField);
                keyboard.setComponentFocus(searchField);
                Main.openKeyboard();
            }
        });
        searchField.setFont(Main.chakra_petch);
        JButton searchButton = new JButton("Search");
        searchButton.setFont(Main.chakra_petch);
        searchButton.addActionListener(e -> searchGames(searchField.getText()));
        topBar.add(searchField, BorderLayout.CENTER);
        topBar.add(searchButton, BorderLayout.EAST);

        JButton showFeaturedButton = new JButton("Show Featured");
        showFeaturedButton.setFont(Main.chakra_petch);
        showFeaturedButton.addActionListener(e -> showMainView());
        topBar.add(showFeaturedButton, BorderLayout.WEST);

        mainPanel.add(topBar, BorderLayout.NORTH);

        // Content area for featured games
        JPanel featuredContent = new JPanel();
        JScrollPane scrollPane = new JScrollPane(featuredContent);
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        scrollPane.getViewport().putClientProperty("EnableWindowBlit", Boolean.TRUE);
        scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        featuredContent.setLayout(new GridLayout(0, 3, 10, 10));

        // Fetch and display games
        SwingUtilities.invokeLater(() -> {
            try {
                Map<String, List<StoreGameData>> categories = fetchCategories();
                if (categories != null && !categories.isEmpty()) {
                    for (Map.Entry<String, List<StoreGameData>> entry : categories.entrySet()) {
                        List<StoreGameData> storeGameDataList = entry.getValue();

                        for (StoreGameData storeGameData : storeGameDataList) {
                            JPanel gamePanel = new JPanel();
                            gamePanel.setLayout(new BorderLayout());

                            System.out.println("Loading image from URL: " + storeGameData.getImageUrl());

                            ImageIcon gameIcon = createImageIcon(storeGameData.getImageUrl());
                            if (gameIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                                System.err.println("Failed to load image from URL: " + storeGameData.getImageUrl());
                            }

                            JLabel gameLabel = new JLabel(storeGameData.getTitle(), gameIcon, JLabel.CENTER);
                            gamePanel.add(gameLabel, BorderLayout.CENTER);
                            gameLabel.setFont(Main.chakra_petch);

                            JLabel priceLabel = new JLabel(String.format("%s %.2f (%d%% off)", storeGameData.getCurrency(), storeGameData.getFinalPrice() / 100.0, storeGameData.getDiscountPercent()));
                            priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
                            priceLabel.setFont(Main.chakra_petch);

                            JButton detailsButton = new JButton("View Details");
                            detailsButton.addActionListener(e -> showDetails(storeGameData));
                            detailsButton.setFont(Main.chakra_petch);

                            JPanel bottomPanel = new JPanel(new FlowLayout());
                            bottomPanel.add(priceLabel);
                            bottomPanel.add(detailsButton);
                            gamePanel.add(bottomPanel, BorderLayout.SOUTH);

                            gamePanel.setMinimumSize(new Dimension(200, 300));
                            gameLabel.setPreferredSize(new Dimension(200, 200));
                            detailsButton.setPreferredSize(new Dimension(200, 50));

                            featuredContent.add(gamePanel);
                        }
                        featuredContent.revalidate();
                        featuredContent.repaint();
                    }
                } else {
                    JLabel noContentLabel = new JLabel("No content available.");
                    featuredContent.add(noContentLabel);
                }

                mainPanel.add(scrollPane, BorderLayout.CENTER);
                revalidate();
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load content.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Detail panel
        detailPanel = new JPanel();
        detailPanel.setLayout(new BorderLayout());

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showMainView());
        backButton.setFont(Main.chakra_petch);
        detailPanel.add(backButton, BorderLayout.NORTH);

        add(mainPanel, "MAIN_VIEW");
        add(detailPanel, "DETAIL_VIEW");

        cardLayout.show(this, "MAIN_VIEW");

        scrollPane.setMaximumSize(mainPanel.getSize());
    }

    private void showDetails(StoreGameData storeGameData) {
        detailPanel.removeAll();

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());

        JLabel titleLabel = new JLabel(storeGameData.getTitle(), createImageIcon(storeGameData.getDetailedImageUrl()), JLabel.CENTER);
        titleLabel.setVerticalTextPosition(JLabel.BOTTOM);
        titleLabel.setHorizontalTextPosition(JLabel.CENTER);
        titleLabel.setFont(Main.chakra_petch);
        titlePanel.add(titleLabel);

        JTextArea descriptionArea = new JTextArea(storeGameData.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setFont(Main.chakra_petch);
        detailPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        String priceText = String.format("%s %.2f (Discount: %d%%)", storeGameData.getCurrency(), storeGameData.getFinalPrice() / 100.0, storeGameData.getDiscountPercent());
        JLabel priceLabel = new JLabel(priceText);
        priceLabel.setFont(Main.chakra_petch);
        titlePanel.add(priceLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton backButton = new JButton("Back to Featured");
        backButton.addActionListener(e -> showMainView());
        backButton.setFont(Main.chakra_petch);
        buttonPanel.add(backButton);

        JButton purchaseButton = new JButton("Purchase");
        purchaseButton.addActionListener(e -> openGamePage(storeGameData.getUrl()));
        purchaseButton.setFont(Main.chakra_petch);
        buttonPanel.add(purchaseButton);

        detailPanel.add(titlePanel, BorderLayout.NORTH);
        detailPanel.add(buttonPanel, BorderLayout.SOUTH);

        cardLayout.show(this, "DETAIL_VIEW");
        revalidate();
        repaint();
    }

    private void showMainView() {
        cardLayout.show(this, "MAIN_VIEW");
        mainPanel.removeAll();
        mainPanel.add(topBar, BorderLayout.NORTH);
        JPanel featuredContent = new JPanel();
        JScrollPane scrollPane = new JScrollPane(featuredContent);
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        scrollPane.getViewport().putClientProperty("EnableWindowBlit", Boolean.TRUE);
        scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        featuredContent.setLayout(new GridLayout(0, 3, 10, 10));

        // Fetch and display games
        SwingUtilities.invokeLater(() -> {
            try {
                Map<String, List<StoreGameData>> categories = fetchCategories();
                if (categories != null && !categories.isEmpty()) {
                    for (Map.Entry<String, List<StoreGameData>> entry : categories.entrySet()) {
                        List<StoreGameData> storeGameDataList = entry.getValue();

                        for (StoreGameData storeGameData : storeGameDataList) {
                            JPanel gamePanel = new JPanel();
                            gamePanel.setLayout(new BorderLayout());

                            System.out.println("Loading image from URL: " + storeGameData.getImageUrl());

                            ImageIcon gameIcon = createImageIcon(storeGameData.getImageUrl());
                            if (gameIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                                System.err.println("Failed to load image from URL: " + storeGameData.getImageUrl());
                            }

                            JLabel gameLabel = new JLabel(storeGameData.getTitle(), gameIcon, JLabel.CENTER);
                            gamePanel.add(gameLabel, BorderLayout.CENTER);
                            gameLabel.setFont(Main.chakra_petch);

                            JLabel priceLabel = new JLabel(String.format("%s %.2f (%d%% off)", storeGameData.getCurrency(), storeGameData.getFinalPrice() / 100.0, storeGameData.getDiscountPercent()));
                            priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
                            priceLabel.setFont(Main.chakra_petch);

                            JButton detailsButton = new JButton("View Details");
                            detailsButton.addActionListener(e -> showDetails(storeGameData));
                            detailsButton.setFont(Main.chakra_petch);

                            JPanel bottomPanel = new JPanel(new FlowLayout());
                            bottomPanel.add(priceLabel);
                            bottomPanel.add(detailsButton);
                            gamePanel.add(bottomPanel, BorderLayout.SOUTH);

                            gamePanel.setMinimumSize(new Dimension(200, 300));
                            gameLabel.setPreferredSize(new Dimension(200, 200));
                            detailsButton.setPreferredSize(new Dimension(200, 50));

                            featuredContent.add(gamePanel);
                        }
                        featuredContent.revalidate();
                        featuredContent.repaint();
                    }
                } else {
                    JLabel noContentLabel = new JLabel("No content available.");
                    featuredContent.add(noContentLabel);
                }

                mainPanel.add(scrollPane, BorderLayout.CENTER);
                revalidate();
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load content.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        revalidate();
        repaint();
    }

    private void openGamePage(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ImageIcon createImageIcon(String path) {
        // Check if the path is null or empty
        if (path == null || path.trim().isEmpty()) {
            System.err.println("Image path is null or empty");
            return new ImageIcon(); // Return a default or empty icon
        }

        try {
            // Ensure the path is an absolute URL; if not, prepend "https://"
            if (!path.startsWith("http://") && !path.startsWith("https://")) {
                path = "https://" + path;
            }

            // Create a URL object from the path
            URL imageURL = new URL(path);
            ImageIcon icon = new ImageIcon(imageURL);

            // Check if the image was loaded correctly
            if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                System.err.println("Image failed to load: " + path);
            }

            return icon;
        } catch (Exception e) {
            e.printStackTrace();
            return new ImageIcon(); // Return an empty icon if there's an error
        }
    }


    private Map<String, List<StoreGameData>> fetchCategories() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(STEAM_FEATURED_URL))
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.body());

        Map<String, List<StoreGameData>> categories = new HashMap<>();

        for (Iterator<String> it = rootNode.fieldNames(); it.hasNext(); ) {
            String key = it.next();
            JsonNode categoryNode = rootNode.path(key);
            JsonNode itemsNode = categoryNode.path("items");

            List<StoreGameData> storeGameDataList = new ArrayList<>();
            if (itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    StoreGameData storeGameData = new StoreGameData();
                    storeGameData.setId(itemNode.path("id").asText());
                    storeGameData.setTitle(itemNode.path("name").asText());
                    storeGameData.setImageUrl(itemNode.path("header_image").asText());
                    storeGameData.setDetailedImageUrl(itemNode.path("large_capsule_image").asText());
                    storeGameData.setDescription(itemNode.path("description").asText());
                    storeGameData.setUrl(itemNode.path("url").asText());
                    storeGameData.setCurrency(itemNode.path("currency").asText());
                    storeGameData.setOriginalPrice(itemNode.path("original_price").asInt());
                    storeGameData.setFinalPrice(itemNode.path("final_price").asInt());
                    storeGameData.setDiscountPercent(itemNode.path("discount_percent").asInt());

                    storeGameDataList.add(storeGameData);
                }
                categories.put(key, storeGameDataList);
            }
        }
        return categories;
    }

    private void searchGames(String query) {
        if (query == null || query.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                HttpResponse<String> response;
                try (HttpClient httpClient = HttpClient.newHttpClient()) {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(SEARCH_URL + query))
                            .build();
                    response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                }

                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.body());
                JsonNode itemsNode = rootNode.path("items");

                JPanel searchResultsPanel = new JPanel();
                searchResultsPanel.setLayout(new GridLayout(0, 3, 10, 10));
                JScrollPane scrollPane = new JScrollPane(searchResultsPanel);
                scrollPane.getVerticalScrollBar().setUnitIncrement(50);
                scrollPane.getViewport().putClientProperty("EnableWindowBlit", Boolean.TRUE);
                scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);

                if (itemsNode.isArray() && itemsNode.size() > 0) {
                    for (JsonNode itemNode : itemsNode) {
                        StoreGameData storeGameData = new StoreGameData();
                        storeGameData.setId(itemNode.path("id").asText());
                        storeGameData.setTitle(itemNode.path("name").asText());
                        storeGameData.setImageUrl(itemNode.path("header_image").asText());
                        storeGameData.setDetailedImageUrl(itemNode.path("large_capsule_image").asText());
                        storeGameData.setDescription(itemNode.path("description").asText());
                        storeGameData.setUrl(itemNode.path("url").asText());
                        storeGameData.setCurrency(itemNode.path("currency").asText());
                        storeGameData.setOriginalPrice(itemNode.path("original_price").asInt());
                        storeGameData.setFinalPrice(itemNode.path("final_price").asInt());
                        storeGameData.setDiscountPercent(itemNode.path("discount_percent").asInt());

                        JPanel gamePanel = new JPanel();
                        gamePanel.setLayout(new BorderLayout());

                        ImageIcon gameIcon = createImageIcon(storeGameData.getImageUrl());
                        if (gameIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                            System.err.println("Failed to load image from URL: " + storeGameData.getImageUrl());
                        }

                        JLabel gameLabel = new JLabel(storeGameData.getTitle(), gameIcon, JLabel.CENTER);
                        gamePanel.add(gameLabel, BorderLayout.CENTER);
                        gameLabel.setFont(Main.chakra_petch);

                        JPanel bottomPanel = getBottomPanel(storeGameData);
                        gamePanel.add(bottomPanel, BorderLayout.SOUTH);

                        searchResultsPanel.add(gamePanel);
                    }
                } else {
                    JLabel noResultsLabel = new JLabel("No results found.");
                    searchResultsPanel.add(noResultsLabel);
                }

                mainPanel.removeAll();
                mainPanel.add(topBar, BorderLayout.NORTH);
                mainPanel.add(scrollPane, BorderLayout.CENTER);
                revalidate();
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to search games.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JPanel getBottomPanel(StoreGameData storeGameData) {
        JLabel priceLabel = new JLabel(String.format("%s %.2f (%d%% off)", storeGameData.getCurrency(), storeGameData.getFinalPrice() / 100.0, storeGameData.getDiscountPercent()));
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceLabel.setFont(Main.chakra_petch);

        JButton detailsButton = new JButton("View Details");
        detailsButton.addActionListener(e -> showDetails(storeGameData));
        detailsButton.setFont(Main.chakra_petch);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(priceLabel);
        bottomPanel.add(detailsButton);
        return bottomPanel;
    }
}
