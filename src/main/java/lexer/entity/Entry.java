package lexer.entity;

import fsm.FSM;

/**
 * Created by savetisyan on 11/10/16
 */
public class Entry {
    private String className;
    private FSM machine;
    private int priority;

    public Entry(String className, FSM machine, int priority) {
        this.className = className;
        this.machine = machine;
        this.priority = priority;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public FSM getMachine() {
        return machine;
    }

    public void setMachine(FSM machine) {
        this.machine = machine;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
