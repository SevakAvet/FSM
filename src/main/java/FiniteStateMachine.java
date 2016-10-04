import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;

import java.util.Collections;
import java.util.List;

/**
 * Created by savetisyan on 20/09/16
 */
public class FiniteStateMachine {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(FiniteStateMachineConfig.class, new MatrixAdapter())
            .create();

    private FiniteStateMachineConfig config;

    private String currentState;
    private String start;
    private int processed = 0;
    private int maxSuccess = 0;
    private boolean success = true;

    public FiniteStateMachine(FiniteStateMachineConfig config, String start) {
        this.config = config;
        this.start = start;
        this.currentState = start;
    }

    private FiniteStateMachine(FiniteStateMachineConfig config, String start, String currentState,
                               int processed, int maxSuccess, boolean success) {
        this.config = config;
        this.start = start;
        this.currentState = currentState;
        this.processed = processed;
        this.maxSuccess = maxSuccess;
        this.success = success;
    }

    public List<String> feed(String input) {
        if (!success) {
            return Collections.emptyList();
        }

        Pair<String, String> state = new Pair<>(currentState, input);
        List<String> nextState = config.getMatrix().getOrDefault(state, Collections.emptyList());

        if (nextState.isEmpty()) {
            success = false;
        } else {
            processed++;

            if (nextState.stream().anyMatch(x -> config.getFinish().contains(x))) {
                maxSuccess = processed;
            }
        }

        return nextState;
    }

    public FiniteStateMachineConfig getConfig() {
        return config;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public int getMaxSuccess() {
        return maxSuccess;
    }

    public String getCurrentState() {
        return currentState;
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
        return currentState.equals(that.currentState);

    }

    @Override
    public int hashCode() {
        int result = currentState.hashCode();
        result = 31 * result + processed;
        result = 31 * result + (success ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FiniteStateMachine{" +
                "start='" + start + '\'' +
                ", currentState='" + currentState + '\'' +
                ", processed=" + processed +
                ", success=" + success +
                ", max_success=" + maxSuccess +
                '}';
    }

    public FiniteStateMachine clone(String currentState) {
        return new FiniteStateMachine(config, start, currentState, processed, maxSuccess, success);
    }
}
