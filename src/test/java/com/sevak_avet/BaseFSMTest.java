package com.sevak_avet;

import fsm.FSM;
import fsm.FSMContext;
import org.testng.annotations.Test;

/**
 * Created by savetisyan on 11/10/16
 */
@Test
public abstract class BaseFSMTest {
    protected FSM fsm;

    public BaseFSMTest(String fileName) {
        String file = FSM.class.getResource(fileName).getFile();
        FSMContext[] config = FSMContext.parse(file);
        fsm = new FSM(config);
    }
}
