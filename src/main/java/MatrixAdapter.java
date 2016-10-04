import com.google.gson.*;
import javafx.util.Pair;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 20/09/16
 */
public class MatrixAdapter implements JsonDeserializer<FiniteStateMachineConfig> {

    @Override
    public FiniteStateMachineConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        Set<String> start = context.deserialize(json.getAsJsonArray("start"), HashSet.class);
        Set<String> finish = context.deserialize(json.getAsJsonArray("finish"), HashSet.class);
        Map<String, Map<String, List<String>>> jsonMatrix = context.deserialize(json.getAsJsonObject("matrix"), Map.class);

        Map<Pair<String, String>, List<String>> matrix = jsonMatrix.entrySet().stream()
                .flatMap(entry ->
                        entry.getValue().entrySet().stream()
                                .map(x -> new Pair<>(new Pair<>(entry.getKey(), x.getKey()), x.getValue())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        FiniteStateMachineConfig config = new FiniteStateMachineConfig();
        config.setStart(start);
        config.setFinish(finish);
        config.setMatrix(matrix);
        return config;
    }
}
