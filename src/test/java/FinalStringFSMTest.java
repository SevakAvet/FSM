import javafx.util.Pair;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by savetisyan on 10/10/16
 */
@Test
public class FinalStringFSMTest {

    private FiniteStateMachine fsm;

    @BeforeSuite
    public void setup() {
        FiniteStateMachineConfig[] config =
                FiniteStateMachineConfig.parse(
                        FiniteStateMachine.class.getResource("final_string.json").getFile());
        fsm = new FiniteStateMachine(config);
    }

    @Test
    public void testPositiveDoubleSevak() throws Exception {
        /*
         *    v     a     k     s     e
         * q0 -> q3 -> q0 -> q5 -> q2 -> null | 4
         *       q1 -> q0
         * q2 -> null | 0
         * q4 -> q2 -> q2 -> null | 2
         */
        Pair<Integer, Boolean> result = fsm.max("sevaksevak", 2);
        System.out.printf("(%d, %b)\n", result.getKey(), result.getValue());

        assertTrue(result.getValue());
        assertEquals(result.getKey().intValue(), 4);
    }

    @Test
    public void testPositiveSevak() throws Exception {
        /*
         *    v     a     k
         * q0 -> q3 -> q0 -> q5 | 3
         *       q1 -> q0
         * q2 -> null | 0
         * q4 -> q2 -> q2 -> null | 2
         */
        Pair<Integer, Boolean> result = fsm.max("sevak", 2);
        System.out.printf("(%d, %b)\n", result.getKey(), result.getValue());

        assertTrue(result.getValue());
        assertEquals(result.getKey().intValue(), 3);
    }
}
