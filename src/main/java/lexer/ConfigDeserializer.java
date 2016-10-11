package lexer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import fsm.FiniteStateMachine;
import fsm.FiniteStateMachineContext;
import lexer.entity.Entry;

import java.lang.reflect.Type;
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

        List<Entry> entries = json.entrySet().stream()
                .map(x -> {
                    Map<String, Object> params = x.getValue();
                    String configPath = ConfigDeserializer.class.getResource((String) params.get("path")).getFile();
                    int priority = (int) ((double) params.get("priority"));

                    FiniteStateMachineContext[] config = FiniteStateMachineContext.parse(configPath);
                    FiniteStateMachine machine = new FiniteStateMachine(config);

                    return new Entry(x.getKey(), machine, priority);
                }).collect(Collectors.toList());

        return new Config(entries);
    }
}
