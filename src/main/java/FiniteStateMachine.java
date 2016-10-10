import javafx.util.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 20/09/16
 */
public class FiniteStateMachine {
    private FiniteStateMachineConfig[] configs;

    private Set<String> currentStates;
    private String start;

    private int processed = 0;
    private int maxSuccess = 0;
    private boolean success = false;

    public FiniteStateMachine(FiniteStateMachineConfig config) {
        this.configs = new FiniteStateMachineConfig[]{config};
        this.currentStates = new HashSet<>();
    }

    public FiniteStateMachine(FiniteStateMachineConfig config, String start) {
        this(config);
        this.start = start;
        this.currentStates.add(start);
    }

    public FiniteStateMachine(FiniteStateMachineConfig[] configs) {
        this.configs = configs;
    }

    public FiniteStateMachine(FiniteStateMachineConfig[] configs, String start) {
        this(configs);
        this.start = start;
    }

    public Pair<Integer, Boolean> max(String input) {
        return max(input, 0);
    }

    public Pair<Integer, Boolean> max(String input, int skip) {
        return Arrays.stream(configs).map(config ->
                config.getStarts().stream()
                        .map(start -> new FiniteStateMachine(config, start))
                        .map(x -> x.feed(config, input, skip))
                        .max(Comparator.comparing(Pair::getKey))
                        .orElse(new Pair<>(0, false)))
                .max(Comparator.comparing(Pair::getKey))
                .orElse(new Pair<>(0, false));
    }

    private Pair<Integer, Boolean> feed(FiniteStateMachineConfig config, String input, int skip) {
        for (int i = Math.min(skip, input.length()); i < input.length(); i++) {
            feed(config, String.valueOf(input.charAt(i)));
        }

        return maxSuccess != 0 ? new Pair<>(maxSuccess, true) : new Pair<>(0, false);
    }

    private void feed(FiniteStateMachineConfig config, String input) {
        Set<String> nextStates = currentStates.stream()
                .map(state -> new Pair<>(state, input))
                .flatMap(state -> config.nextState(state).stream())
                .filter(states -> !states.isEmpty())
                .collect(Collectors.toSet());

        if (!nextStates.isEmpty()) {
            success = true;
            processed++;

            if (nextStates.stream().anyMatch(x -> config.getFinishes().contains(x))) {
                maxSuccess = processed;
            }

            currentStates = nextStates;
        } else {
            currentStates.clear();
        }
    }

    public FiniteStateMachineConfig[] getConfigs() {
        return configs;
    }

    public int getMaxSuccess() {
        return maxSuccess;
    }

    public int getProcessed() {
        return processed;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FiniteStateMachine that = (FiniteStateMachine) o;

        if (processed != that.processed) return false;
        if (success != that.success) return false;
        return currentStates.equals(that.currentStates);

    }

    @Override
    public int hashCode() {
        int result = currentStates.hashCode();
        result = 31 * result + processed;
        result = 31 * result + (success ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FiniteStateMachine{" +
                "start='" + start + '\'' +
                ", currentStates='" + currentStates + '\'' +
                ", processed=" + processed +
                ", success=" + success +
                ", max_success=" + maxSuccess +
                '}';
    }
}
