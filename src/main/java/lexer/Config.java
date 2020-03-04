package lexer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lexer.deserializer.ConfigDeserializer;
import lexer.entity.Entry;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by savetisyan on 11/10/16
 */
public class Config {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Config.class, new ConfigDeserializer())
            .create();

    private final List<Entry> entries;

    public Config(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public static Config parse(String fileName) {
        try (FileReader json = new FileReader(fileName)) {
            return GSON.fromJson(json, Config.class);
        } catch (IOException e) {
            return null;
        }
    }
}


