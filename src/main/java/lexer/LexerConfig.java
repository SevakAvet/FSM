package lexer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by savetisyan on 11/10/16
 */
public class LexerConfig {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LexerConfig.class, new LexerConfigDeserializer())
            .create();

    private List<LexerEntry> entries;

    public LexerConfig(List<LexerEntry> entries) {
        this.entries = entries;
    }

    public List<LexerEntry> getEntries() {
        return entries;
    }

    public static LexerConfig parse(String fileName) {
        try (FileReader json = new FileReader(fileName)) {
            return GSON.fromJson(json, LexerConfig.class);
        } catch (IOException e) {
            return null;
        }
    }
}


