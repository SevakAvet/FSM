package lexer.entity;

import fsm.FSMGroup;

/**
 * Created by savetisyan on 11/10/16
 */
public class Entry {
    private String className;
    private FSMGroup machine;
    private int priority;

    public Entry(String className, FSMGroup machine, int priority) {
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

    public FSMGroup getMachine() {
        return machine;
    }

    public void setMachine(FSMGroup machine) {
        this.machine = machine;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
