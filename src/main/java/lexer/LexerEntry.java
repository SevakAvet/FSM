package lexer;

import fsm.FiniteStateMachine;

/**
 * Created by savetisyan on 11/10/16
 */
public class LexerEntry {
    private String className;
    private FiniteStateMachine machine;
    private int priority;

    public LexerEntry(String className, FiniteStateMachine machine, int priority) {
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

    public FiniteStateMachine getMachine() {
        return machine;
    }

    public void setMachine(FiniteStateMachine machine) {
        this.machine = machine;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
