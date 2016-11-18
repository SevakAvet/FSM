package fsm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by savetisyan on 20/09/16
 */
public class FSMContext {
    public static final Gson GSON_GRAPH = new GsonBuilder()
            .registerTypeAdapter(FSMContext.class, new ContextDeserializer())
            .create();

    public static final Map<String, String> DEFAULT_INPUTS = new HashMap<>();
    private static final ScriptEngine SCRIPT_ENGINE = new NashornScriptEngineFactory().getScriptEngine();

    static {
        DEFAULT_INPUTS.put("\\d", "x >= '0' && x <= '9'");
        DEFAULT_INPUTS.put("\\w", "(x >= 'a' && x <= 'z') || (x >= 'A' && x <= 'Z')");
        DEFAULT_INPUTS.put("\\$", "x == ''");
        DEFAULT_INPUTS.put("\\s", "x == ' ' || x == '\\n' || x == '\\r' || x == '\\t'");
        DEFAULT_INPUTS.put("\\+", "x == '+'");
        DEFAULT_INPUTS.put("\\*", "x == '+'");
        DEFAULT_INPUTS.put("\\e", "x == 'e' || x == 'E'");
    }

    private Set<String> starts;
    private Set<String> finishes;
    private Map<String, String> inputs;
    private Map<Pair<String, String>, Set<String>> charTransitions;
    private Map<Pair<String, String>, Set<String>> functionTransitions;

    public FSMContext() {
        starts = new HashSet<>();
        finishes = new HashSet<>();
        inputs = new HashMap<>(DEFAULT_INPUTS);
        charTransitions = new HashMap<>();
        functionTransitions = new HashMap<>();
    }

    public void setStarts(Set<String> starts) {
        this.starts = starts;
    }

    public void setFinishes(Set<String> finishes) {
        this.finishes = finishes;
    }

    public void updateInputs(Map<String, String> inputs) {
        if(inputs != null) {
            this.inputs.putAll(inputs);
        }
    }

    public void setCharTransitions(Map<Pair<String, String>, Set<String>> charTransitions) {
        this.charTransitions = charTransitions;
    }

    public Map<Pair<String, String>, Set<String>> getFunctionTransitions() {
        return functionTransitions;
    }

    public void setFunctionTransitions(Map<Pair<String, String>, Set<String>> functionTransitions) {
        this.functionTransitions = functionTransitions;
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

    public Map<Pair<String, String>, Set<String>> getCharTransitions() {
        return charTransitions;
    }

    public void addCharTransition(String from, Set<String> to, String by) {
        addTransition(charTransitions, from, to, by);
    }

    public void addFunctionTransition(String from, Set<String> to, String by) {
        addTransition(functionTransitions, from, to, by);
    }

    public void addCharTransition(String from, String to, String by) {
        addTransition(charTransitions, from, to, by);
    }

    public void addFunctionTransition(String from, String to, String by) {
        addTransition(functionTransitions, from, to, by);
    }

    private void addTransition(Map<Pair<String, String>, Set<String>> transition, String from, Set<String> to, String by) {
        Pair<String, String> key = new Pair<>(from, by);
        Set<String> toList = transition.getOrDefault(key, new HashSet<>());

        if (toList.isEmpty()) {
            transition.put(key, to);
        } else {
            toList.addAll(to);
        }
    }

    private void addTransition(Map<Pair<String, String>, Set<String>> transition, String from, String to, String by) {
        Pair<String, String> key = new Pair<>(from, by);
        Set<String> toList = transition.getOrDefault(key, new HashSet<>());

        if (toList.isEmpty()) {
            toList.add(to);
            transition.put(key, toList);
        } else {
            toList.add(to);
        }
    }

    public static FSMContext[] parse(String fileName) {
        try (FileReader json = new FileReader(fileName)) {
            return GSON_GRAPH.fromJson(json, FSMContext[].class);
        } catch (IOException e) {
            return new FSMContext[0];
        }
    }

    public Set<String> nextState(Pair<String, String> state) {
        Set<String> nextStates = charTransitions.getOrDefault(state, Collections.emptySet());
        if (!nextStates.isEmpty()) {
            return nextStates;
        }

        nextStates = functionTransitions.entrySet().stream()
                .filter(x -> x.getKey().getKey().equals(state.getKey()))
                .filter(x -> eval(state.getValue(), inputs.get(x.getKey().getValue())))
                .flatMap(x -> x.getValue().stream())
                .collect(Collectors.toSet());

        Set<String> nexts = functionTransitions.getOrDefault(emptyTransition(state), Collections.emptySet());
        for (String next : nexts) {
            nextStates.addAll(nextState(new Pair<>(next, state.getValue())));
        }
        return nextStates;

    }

    private Pair<String, String> emptyTransition(Pair<String, String> state) {
        return new Pair<>(state.getKey(), "\\$");
    }

    private boolean eval(String var, String expression) {
        try {
            SCRIPT_ENGINE.put("x", var);
            return (Boolean) SCRIPT_ENGINE.eval(expression);
        } catch (ScriptException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void join(FSMContext fsm) {
        this.getFinishes().addAll(fsm.getFinishes());

        for (String originalStart : this.starts) {
            fsm.getCharTransitions().forEach((k, v) -> {
                if (fsm.getStarts().contains(k.getKey())) {
                    this.addCharTransition(originalStart, v, k.getValue());
                } else {
                    this.addCharTransition(k.getKey(), v, k.getValue());
                }
            });

            fsm.getFunctionTransitions().forEach((k, v) -> {
                if (fsm.getStarts().contains(k.getKey())) {
                    this.addFunctionTransition(originalStart, v, k.getValue());
                } else {
                    this.addFunctionTransition(k.getKey(), v, k.getValue());
                }
            });
        }
    }

    public void iterate() {
        Map<Pair<String, String>, Set<String>> newCharTransitions = new HashMap<>();
        Map<Pair<String, String>, Set<String>> newFunctionTransitions = new HashMap<>();

        for (String finish : finishes) {
            charTransitions.entrySet()
                    .stream()
                    .filter(x -> starts.contains(x.getKey().getKey()))
                    .forEach(e -> addTransition(newCharTransitions, finish, e.getValue(), e.getKey().getValue()));
            functionTransitions.entrySet()
                    .stream()
                    .filter(x -> starts.contains(x.getKey().getKey()))
                    .forEach(e -> addTransition(newFunctionTransitions, finish, e.getValue(), e.getKey().getValue()));
        }

        finishes.addAll(starts);
        charTransitions.putAll(newCharTransitions);
        functionTransitions.putAll(newFunctionTransitions);
    }

    public void concat(FSMContext fsm, boolean isEmptyTransition) {
        for (String start : fsm.getStarts()) {
            fsm.getCharTransitions().forEach((k, v) -> {
                String from = k.getKey();
                String by = k.getValue();
                if (from.equals(start)) {
                    this.finishes.forEach(finish -> addCharTransition(finish, v, by));
                } else {
                    addCharTransition(from, v, by);
                }
            });

            fsm.getFunctionTransitions().forEach((k, v) -> {
                String from = k.getKey();
                String by = k.getValue();

                if (from.equals(start)) {
                    this.finishes.forEach(finish -> addFunctionTransition(finish, v, by));
                } else {
                    addFunctionTransition(from, v, by);
                }
            });
        }

        boolean startAndFinish = Stream.concat(fsm.getCharTransitions().keySet().stream(),
                fsm.getFunctionTransitions().keySet().stream())
                .filter(x -> fsm.getStarts().contains(x.getKey())
                        && fsm.getFinishes().contains(x.getKey()))
                .findAny()
                .isPresent();

        if (startAndFinish || isEmptyTransition) {
            finishes.addAll(fsm.getFinishes());
        } else {
            finishes = fsm.getFinishes();
        }
        finishes.removeAll(fsm.getStarts());
    }

    @Override
    public String toString() {
        return "FiniteStateMachineConfig{" +
                "starts=" + starts +
                ", finishes=" + finishes +
                ", inputs=" + inputs +
                ", charTransitions=" + charTransitions +
                ", functionTransition=" + functionTransitions +
                '}';
    }
}
