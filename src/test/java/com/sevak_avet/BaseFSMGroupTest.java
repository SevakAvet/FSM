package com.sevak_avet;

import fsm.FSMGroup;
import fsm.FSM;
import org.testng.annotations.Test;

/**
 * Created by savetisyan on 11/10/16
 */
@Test
public abstract class BaseFSMGroupTest {
    protected FSMGroup fsmGroup;

    public BaseFSMGroupTest(String fileName) {
        String file = FSMGroup.class.getResource(fileName).getFile();
        FSM[] config = FSM.parse(file);
        fsmGroup = new FSMGroup(config);
    }
}
