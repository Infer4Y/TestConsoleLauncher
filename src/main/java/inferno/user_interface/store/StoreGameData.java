package inferno.user_interface.store;

public class StoreGameData {
    private String id;
    private String title;
    private String imageUrl;
    private String detailedImageUrl;
    private String description;
    private String url;
    private String currency;
    private int originalPrice;
    private int finalPrice;
    private int discountPercent;

    // Constructor
    public StoreGameData(String id, String title, String imageUrl, String detailedImageUrl,
                         String description, String url, String currency,
                         int originalPrice, int finalPrice, int discountPercent) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.detailedImageUrl = detailedImageUrl;
        this.description = description;
        this.url = url;
        this.currency = currency;
        this.originalPrice = originalPrice;
        this.finalPrice = finalPrice;
        this.discountPercent = discountPercent;
    }

    public StoreGameData() {

    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDetailedImageUrl() {
        return detailedImageUrl;
    }

    public void setDetailedImageUrl(String detailedImageUrl) {
        this.detailedImageUrl = detailedImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }
}
