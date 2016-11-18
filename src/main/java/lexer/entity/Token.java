package lexer.entity;

/**
 * Created by savetisyan on 11/10/16
 */
public class Token {
    private String className;
    private String value;

    public Token(String className, String value) {
        this.className = className;
        this.value = value;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("(%s: \"%s\")", className, value);
    }
}
