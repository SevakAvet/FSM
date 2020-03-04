package lexer.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import fsm.FSMRegex;
import fsm.FSMGroup;
import fsm.FSM;
import lexer.Config;
import lexer.entity.Entry;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 11/10/16
 */
public class ConfigDeserializer implements JsonDeserializer<Config> {
    @Override
    public Config deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        Map<String, Map<String, Object>> json = context.deserialize(jsonElement, Map.class);

        final FSMRegex fsmRegex = new FSMRegex();
        List<Entry> entries = json.entrySet().stream()
                .map(x -> {
                    Map<String, Object> params = x.getValue();
                    int priority = (int) ((double) params.get("priority"));

                    if(params.containsKey("regex")) {
                        String regex = (String) params.get("regex");
                        Map<String, String> inputs = (Map<String, String>) params.getOrDefault("inputs", Collections.emptyMap());

                        FSM config = fsmRegex.parse(regex, inputs);
                        config.updateInputs(inputs);
                        fsmRegex.resetState();
                        return new Entry(x.getKey(), new FSMGroup(config), priority);
                    }

                    String configPath = ConfigDeserializer.class.getResource((String) params.get("path")).getFile();
                    FSM[] config = FSM.parse(configPath);
                    FSMGroup machine = new FSMGroup(config);

                    return new Entry(x.getKey(), machine, priority);
                }).collect(Collectors.toList());

        return new Config(entries);
    }
}
