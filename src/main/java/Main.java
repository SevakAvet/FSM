import javafx.util.Pair;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.max;

/**
 * Created by savetisyan on 27/09/16
 */
public class Main {
    public static void main(String[] args) throws IOException {
        FileReader json = new FileReader("/Users/savetisyan/study/FSM-string-final/src/main/resources/config.json");
        FiniteStateMachineConfig config = FiniteStateMachine.GSON.fromJson(json, FiniteStateMachineConfig.class);

        int skip = 2;
        String inputStr = "sevaksevak";

        //vak
        /**    v    a     k     s     e
         * q0 -> q3 -> q0 -> q5 -> q2 -> null | 4
         *       q1 -> q0
         * q2 -> null | 0
         * q4 -> q2 -> q2 -> null | 2
         */

        Set<FiniteStateMachine> machines = config.getStart().stream()
                .map(start -> new FiniteStateMachine(config, start))
                .collect(Collectors.toSet());

        inputStr = inputStr.substring(Math.min(skip, inputStr.length()));

        Set<FiniteStateMachine> failedMachines = new HashSet<>();
        for (int i = 0; i < inputStr.length(); i++) {
            String input = String.valueOf(inputStr.charAt(i));
            Set<FiniteStateMachine> newMachines = new HashSet<>();

            for (FiniteStateMachine machine : machines) {
                List<String> nextStates = machine.feed(input);

                if (machine.isSuccess()) {
                    newMachines.addAll(nextStates.stream()
                            .map(machine::clone)
                            .collect(Collectors.toList()));
                } else {
                    failedMachines.add(machine);
                }
            }

            machines = newMachines;
        }

//        Stream.concat(machines.stream(), failedMachines.stream()).forEach(System.out::println);

        Pair<Integer, Boolean> result = Stream.concat(machines.stream(), failedMachines.stream())
                .filter(x -> x.getProcessed() > 0)
                .max((x, y) -> max(x.getMaxSuccess(), y.getMaxSuccess()))
                .map(x -> new Pair<>(x.getMaxSuccess(), true))
                .orElse(new Pair<>(0, false));

        System.out.printf("(%d, %b)\n", result.getKey(), result.getValue());
    }
}
