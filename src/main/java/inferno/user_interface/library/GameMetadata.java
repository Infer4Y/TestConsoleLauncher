package inferno.user_interface.library;

import java.util.Map;

public class GameMetadata {
    private String title;
    private String description;
    private String developer;
    private Map<String, String> osPaths;
    private String libraryCardImage;
    private String bannerImage;

    // Getters and setters for all fields

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public Map<String, String> getOsPaths() {
        return osPaths;
    }

    public void setOsPaths(Map<String, String> osPaths) {
        this.osPaths = osPaths;
    }

    public String getLibraryCardImage() {
        return libraryCardImage;
    }

    public void setLibraryCardImage(String libraryCardImage) {
        this.libraryCardImage = libraryCardImage;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    @Override
    public String toString() {
        return "GameMetadata{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", developer='" + developer + '\'' +
                ", osPaths=" + osPaths +
                ", libraryCardImage='" + libraryCardImage + '\'' +
                ", bannerImage='" + bannerImage + '\'' +
                '}';
    }
}



