import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 20/09/16
 */
public class FiniteStateMachineContext {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(FiniteStateMachineContext.class, new ContextDeserializer())
            .create();

    private static final ScriptEngine SCRIPT_ENGINE = new NashornScriptEngineFactory().getScriptEngine();

    private Set<String> starts;
    private Set<String> finishes;
    private Map<String, String> inputs;

    private Map<Pair<String, String>, List<String>> charTransition;
    private Map<Pair<String, String>, List<String>> functionTransitions;

    private FiniteStateMachineContext() {
    }

    public static Builder builder() {
        return new FiniteStateMachineContext().new Builder();
    }

    public class Builder {
        private Builder() {
        }

        public Builder starts(Set<String> starts) {
            FiniteStateMachineContext.this.starts = starts;
            return this;
        }

        public Builder finishes(Set<String> finishes) {
            FiniteStateMachineContext.this.finishes = finishes;
            return this;
        }

        public Builder inputs(Map<String, String> inputs) {
            FiniteStateMachineContext.this.inputs = inputs;
            return this;
        }

        public Builder chars(Map<Pair<String, String>, List<String>> chars) {
            FiniteStateMachineContext.this.charTransition = chars;
            return this;
        }

        public Builder functions(Map<Pair<String, String>, List<String>> functions) {
            FiniteStateMachineContext.this.functionTransitions = functions;
            return this;
        }

        public FiniteStateMachineContext build() {
            return FiniteStateMachineContext.this;
        }
    }

    public static FiniteStateMachineContext[] parse(String fileName) {
        try (FileReader json = new FileReader(fileName)) {
            return GSON.fromJson(json, FiniteStateMachineContext[].class);
        } catch (IOException e) {
            return new FiniteStateMachineContext[0];
        }
    }

    public List<String> nextState(Pair<String, String> state) {
        List<String> nextStates = charTransition.getOrDefault(state, Collections.emptyList());
        if (!nextStates.isEmpty()) {
            return nextStates;
        }

        nextStates = functionTransitions.entrySet().stream()
                .filter(x -> x.getKey().getKey().equals(state.getKey()))
                .filter(x -> eval(state.getValue(), inputs.get(x.getKey().getValue())))
                .flatMap(x -> x.getValue().stream())
                .collect(Collectors.toList());

        if (!nextStates.isEmpty()) {
            return nextStates;
        }

        return nextStates;
    }

    private boolean eval(String var, String expression) {
        try {
            SCRIPT_ENGINE.put("x", var);
            return (Boolean) SCRIPT_ENGINE.eval(expression);
        } catch (ScriptException e) {
            throw new IllegalArgumentException(e);
        }
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
