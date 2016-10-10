import javafx.util.Pair;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Created by savetisyan on 10/10/16
 */
public class FinalStringFSMTest {

    private FiniteStateMachine fsm;

    @BeforeSuite
    public void setup() {
        FiniteStateMachineConfig config =
                FiniteStateMachineConfig.builder()
                        .fileName(FiniteStateMachine.class.getResource("final_string.json").getFile())
                        .build();
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

        Assert.assertTrue(result.getValue());
        Assert.assertEquals(result.getKey().intValue(), 4);
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

        Assert.assertTrue(result.getValue());
        Assert.assertEquals(result.getKey().intValue(), 3);
    }
}
