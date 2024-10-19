package inferno.user_interface.library;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static GameMetadata parseMetadata(File jsonFile) throws IOException {
        return objectMapper.readValue(jsonFile, GameMetadata.class);
    }
}
