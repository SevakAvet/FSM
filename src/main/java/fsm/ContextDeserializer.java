package fsm;

import com.google.gson.*;
import util.Pair;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 20/09/16
 */
public class ContextDeserializer implements JsonDeserializer<FSM> {

    @Override
    public FSM deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        Set<String> start = context.deserialize(json.getAsJsonArray("start"), HashSet.class);
        Set<String> finish = context.deserialize(json.getAsJsonArray("finish"), HashSet.class);
        Map<String, String> inputs = context.deserialize(json.getAsJsonObject("inputs"), HashMap.class);

        Map<String, Map<String, List<String>>> jsonMatrix =
                context.deserialize(json.getAsJsonObject("matrix"), Map.class);

        Map<Boolean, Map<Pair<String, String>, Set<String>>> collect = jsonMatrix.entrySet().stream()
                .flatMap(x -> x.getValue().entrySet().stream()
                        .map(y -> new Pair<>(new Pair<>(x.getKey(), y.getKey()), new HashSet<>(y.getValue()))))
                .collect(Collectors.groupingBy(
                        x -> inputs != null && inputs.containsKey(x.getKey().getValue()),
                        Collectors.toMap(Pair::getKey, Pair::getValue)));

        FSM fsm = new FSM();
        fsm.setStarts(start);
        fsm.setFinishes(finish);
        fsm.updateInputs(inputs);
        fsm.setFunctionTransitions(collect.getOrDefault(true, Collections.emptyMap()));
        fsm.setCharTransitions(collect.getOrDefault(false, Collections.emptyMap()));

        return fsm;
    }
}
