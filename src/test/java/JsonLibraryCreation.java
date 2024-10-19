import java.io.FileWriter;
import java.io.IOException;

public class JsonLibraryCreation {
    public static void main(String[] args) {
        createExampleJsonFiles();
    }

    private static void createExampleJsonFiles() {
        try {
            FileWriter writer1 = new FileWriter("game1.json");
            writer1.write("{\n" +
                    "  \"title\": \"Game One\",\n" +
                    "  \"description\": \"An exciting adventure game where you explore vast landscapes and solve intricate puzzles.\",\n" +
                    "  \"developer\": \"Dev Studio\",\n" +
                    "  \"osPaths\": {\n" +
                    "    \"windows\": \"C:\\\\games\\\\game1\\\\executable1.exe\",\n" +
                    "    \"mac\": \"/Users/username/games/game1/executable1\",\n" +
                    "    \"linux\": \"/home/username/games/game1/executable1\"\n" +
                    "  },\n" +
                    "  \"libraryCardImage\": \"http://example.com/path/to/library/card/image1.png\",\n" +
                    "  \"bannerImage\": \"http://example.com/path/to/banner/image1.png\"\n" +
                    "}");
            writer1.close();

            FileWriter writer2 = new FileWriter("game2.json");
            writer2.write("{\n" +
                    "  \"title\": \"Game Two\",\n" +
                    "  \"description\": \"A thrilling puzzle game that challenges your mind with complex problems.\",\n" +
                    "  \"developer\": \"Puzzle Corp\",\n" +
                    "  \"osPaths\": {\n" +
                    "    \"windows\": \"C:\\\\games\\\\game2\\\\executable2.exe\",\n" +
                    "    \"mac\": \"/Users/username/games/game2/executable2\",\n" +
                    "    \"linux\": \"/home/username/games/game2/executable2\"\n" +
                    "  },\n" +
                    "  \"libraryCardImage\": \"http://example.com/path/to/library/card/image2.png\",\n" +
                    "  \"bannerImage\": \"http://example.com/path/to/banner/image2.png\"\n" +
                    "}");
            writer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
