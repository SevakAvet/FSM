package com.sevak_avet;

import fsm.FiniteStateMachine;
import fsm.FiniteStateMachineContext;

/**
 * Created by savetisyan on 11/10/16
 */
public abstract class BaseFSMTest {
    protected FiniteStateMachine fsm;

    public BaseFSMTest(String fileName) {
        String file = FiniteStateMachine.class.getResource(fileName).getFile();
        FiniteStateMachineContext[] config = FiniteStateMachineContext.parse(file);
        fsm = new FiniteStateMachine(config);
    }
}
