import javafx.util.Pair;

import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 27/09/16
 */
public class Main {
    public static void main(String[] args) throws IOException {
        FileReader json = new FileReader("/Users/savetisyan/study/FSM-string-final/src/main/resources/config.json");
        FiniteStateMachineConfig config = FiniteStateMachine.GSON.fromJson(json, FiniteStateMachineConfig.class);

        /**    v    a     k     s     e
         * q0 -> q3 -> q0 -> q5 -> q2 -> null | 4
         *       q1 -> q0
         * q2 -> null | 0
         * q4 -> q2 -> q2 -> null | 2
         */

        int skip = 2;
        String inputStr = "sevaksevak";

        FiniteStateMachine fsm = new FiniteStateMachine(config);
        Pair<Integer, Boolean> result = fsm.max(inputStr, skip);

        System.out.printf("(%d, %b)\n", result.getKey(), result.getValue());
    }
}
