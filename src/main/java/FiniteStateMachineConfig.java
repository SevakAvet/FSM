import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 20/09/16
 */
public class FiniteStateMachineConfig {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(FiniteStateMachineConfig.class, new ConfigDeserializer())
            .create();

    private static final ScriptEngine SCRIPT_ENGINE = new NashornScriptEngineFactory().getScriptEngine();

    private Set<String> starts;
    private Set<String> finishes;
    private Map<String, String> inputs;

    private Map<Pair<String, String>, List<String>> charTransition;
    private Map<Pair<String, String>, List<String>> functionTransitions;

    private FiniteStateMachineConfig() {
    }

    public static Builder builder() {
        return new FiniteStateMachineConfig().new Builder();
    }

    public class Builder {
        private Builder() {
        }


        public Builder starts(Set<String> starts) {
            FiniteStateMachineConfig.this.starts = starts;
            return this;
        }

        public Builder finishes(Set<String> finishes) {
            FiniteStateMachineConfig.this.finishes = finishes;
            return this;
        }

        public Builder inputs(Map<String, String> inputs) {
            FiniteStateMachineConfig.this.inputs = inputs;
            return this;
        }

        public Builder chars(Map<Pair<String, String>, List<String>> chars) {
            FiniteStateMachineConfig.this.charTransition = chars;
            return this;
        }

        public Builder functions(Map<Pair<String, String>, List<String>> functions) {
            FiniteStateMachineConfig.this.functionTransitions = functions;
            return this;
        }

        public FiniteStateMachineConfig build() {
            return FiniteStateMachineConfig.this;
        }
    }

    public static FiniteStateMachineConfig[] parse(String fileName) {
        try (FileReader json = new FileReader(fileName)) {
            Type itemsArrType = new TypeToken<FiniteStateMachineConfig[]>() {}.getType();
            return GSON.fromJson(json, itemsArrType);
        } catch (IOException e) {
            return null;
        }
    }

    public List<String> nextState(Pair<String, String> state) {
        List<String> nextStates = charTransition.getOrDefault(state, Collections.emptyList());
        if (!nextStates.isEmpty()) {
            return nextStates;
        }

        nextStates = functionTransitions.entrySet().stream()
                .filter(x -> x.getKey().getKey().equals(state.getKey()))
                .filter(x -> {
                    try {
                        SCRIPT_ENGINE.put("x", state.getValue());
                        return (Boolean) SCRIPT_ENGINE.eval(inputs.get(x.getKey().getValue()));
                    } catch (ScriptException e) {
                        throw new IllegalArgumentException(e);
                    }
                }).flatMap(x -> x.getValue().stream())
                .collect(Collectors.toList());

        if (!nextStates.isEmpty()) {
            return nextStates;
        }

        return nextStates;
    }

    public Set<String> getStarts() {
        return starts;
    }

    public Set<String> getFinishes() {
        return finishes;
    }

    public Map<String, String> getInputs() {
        return inputs;
    }

    public Map<Pair<String, String>, List<String>> getCharTransition() {
        return charTransition;
    }

    @Override
    public String toString() {
        return "FiniteStateMachineConfig{" +
                "starts=" + starts +
                ", finishes=" + finishes +
                ", inputs=" + inputs +
                ", charTransition=" + charTransition +
                '}';
    }
}
