package com.sevak_avet;

import fsm.FiniteStateMachine;
import fsm.FiniteStateMachineContext;

/**
 * Created by savetisyan on 11/10/16
 */
public abstract class BaseFSMTest {
    protected FiniteStateMachine fsm;

    public BaseFSMTest(String fileName) {
        FiniteStateMachineContext[] config =
                FiniteStateMachineContext.parse(FiniteStateMachine.class.getResource(fileName).getFile());
        fsm = new FiniteStateMachine(config);
    }
}
