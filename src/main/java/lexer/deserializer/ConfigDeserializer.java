package lexer.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import fsm.FSMGenerator;
import fsm.FSM;
import fsm.FSMContext;
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

        final FSMGenerator fsmGenerator = new FSMGenerator();
        List<Entry> entries = json.entrySet().stream()
                .map(x -> {
                    Map<String, Object> params = x.getValue();
                    int priority = (int) ((double) params.get("priority"));

                    if(params.containsKey("regex")) {
                        String regex = (String) params.get("regex");
                        Map<String, String> inputs = (Map<String, String>) params.getOrDefault("inputs", Collections.emptyMap());

                        FSMContext config = fsmGenerator.parse(regex, inputs);
                        config.updateInputs(inputs);
                        fsmGenerator.resetState();
                        return new Entry(x.getKey(), new FSM(config), priority);
                    }

                    String configPath = ConfigDeserializer.class.getResource((String) params.get("path")).getFile();
                    FSMContext[] config = FSMContext.parse(configPath);
                    FSM machine = new FSM(config);

                    return new Entry(x.getKey(), machine, priority);
                }).collect(Collectors.toList());

        return new Config(entries);
    }
}
