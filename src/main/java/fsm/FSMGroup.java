package fsm;

import util.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 20/09/16
 */
public class FSMGroup {
    private final FSM[] fsms;

    private Set<String> currentStates;
    private String start;

    private int processed;
    private int maxSuccess;
    private boolean success;

    public FSMGroup(FSM fsm) {
        this(new FSM[]{fsm});
    }

    public FSMGroup(FSM[] fsms) {
        if (fsms.length == 0) {
            throw new IllegalArgumentException("Empty array of FSMs passed to constructor");
        }

        this.currentStates = new HashSet<>();
        this.fsms = fsms;
        this.processed = 0;
        this.maxSuccess = 0;
        this.success = false;
    }

    public FSMGroup(FSM config, String start) {
        this(config);
        this.start = start;
        this.currentStates.add(start);
    }

    public Pair<Integer, Boolean> max(String input) {
        return max(input, 0);
    }

    public Pair<Integer, Boolean> max(String input, int skip) {
        final Pair<Integer, Boolean> zero = Pair.of(0, false);

        return Arrays.stream(fsms).map(config ->
                config.getStarts().stream()
                        .map(start -> new FSMGroup(config, start))
                        .map(x -> x.feed(config, input, skip))
                        .max(Comparator.comparing(Pair::getKey))
                        .orElse(zero))
                .max(Comparator.comparing(Pair::getKey))
                .orElse(zero);
    }

    private Pair<Integer, Boolean> feed(FSM config, String input, int skip) {
        for (int i = Math.min(skip, input.length()); i < input.length(); i++) {
            feed(config, String.valueOf(input.charAt(i)));
        }

        return new Pair<>(maxSuccess, maxSuccess != 0);
    }

    private void feed(FSM config, String input) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FSMGroup that = (FSMGroup) o;

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
        return "fsm.FiniteStateMachine{" +
                "start='" + start + '\'' +
                ", currentStates='" + currentStates + '\'' +
                ", processed=" + processed +
                ", success=" + success +
                ", max_success=" + maxSuccess +
                '}';
    }
}
