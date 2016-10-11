package fsm;

import com.google.gson.*;
import javafx.util.Pair;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 20/09/16
 */
public class ContextDeserializer implements JsonDeserializer<FiniteStateMachineContext> {

    @Override
    public FiniteStateMachineContext deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        Set<String> start = context.deserialize(json.getAsJsonArray("start"), HashSet.class);
        Set<String> finish = context.deserialize(json.getAsJsonArray("finish"), HashSet.class);
        Map<String, String> inputs = context.deserialize(json.getAsJsonObject("inputs"), HashMap.class);

        Map<String, Map<String, List<String>>> jsonMatrix =
                context.deserialize(json.getAsJsonObject("matrix"), Map.class);

        Map<Boolean, Map<Pair<String, String>, List<String>>> collect = jsonMatrix.entrySet().stream()
                .flatMap(x -> x.getValue().entrySet().stream()
                        .map(y -> new Pair<>(new Pair<>(x.getKey(), y.getKey()), y.getValue())))
                .collect(Collectors.groupingBy(
                        x -> inputs != null && inputs.keySet().contains(x.getKey().getValue()),
                        Collectors.toMap(Pair::getKey, Pair::getValue)));

        return FiniteStateMachineContext.builder()
                .starts(start)
                .finishes(finish)
                .inputs(inputs)
                .functions(collect.getOrDefault(true, Collections.emptyMap()))
                .chars(collect.getOrDefault(false, Collections.emptyMap()))
                .build();
    }
}
