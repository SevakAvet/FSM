import javafx.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by savetisyan on 20/09/16
 */
public class FiniteStateMachineConfig {
    private Set<String> start;
    private Set<String> finish;

    private Map<Pair<String, String>, List<String>> matrix;

    public FiniteStateMachineConfig() {
    }

    public Set<String> getStart() {
        return start;
    }

    public void setStart(Set<String> start) {
        this.start = start;
    }

    public Set<String> getFinish() {
        return finish;
    }

    public void setFinish(Set<String> finish) {
        this.finish = finish;
    }

    public Map<Pair<String, String>, List<String>> getMatrix() {
        return matrix;
    }

    public void setMatrix(Map<Pair<String, String>, List<String>> matrix) {
        this.matrix = matrix;
    }

    @Override
    public String toString() {
        return "FiniteStateMachineConfig{" +
                "start=" + start +
                ", finish=" + finish +
                ", matrix=" + matrix +
                '}';
    }
}
