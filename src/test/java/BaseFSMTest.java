/**
 * Created by savetisyan on 11/10/16
 */
public abstract class BaseFSMTest {
    protected FiniteStateMachine fsm;

    public BaseFSMTest(String fileName) {
        FiniteStateMachineConfig[] config =
                FiniteStateMachineConfig.parse(
                        FiniteStateMachine.class.getResource(fileName).getFile());
        fsm = new FiniteStateMachine(config);
    }
}
