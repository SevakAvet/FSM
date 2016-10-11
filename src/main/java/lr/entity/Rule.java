package lr.entity;

import lexer.entity.Token;

import java.util.List;
import java.util.StringJoiner;

/**
 * Created by savetisyan on 23/10/16
 */
public class Rule {
    private String left;
    private List<Token> right;

    public Rule(String left, List<Token> right) {
        this.left = left;
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public List<Token> getRight() {
        return right;
    }

    public void setRight(List<Token> right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rule rule = (Rule) o;

        if (!left.equals(rule.left)) return false;
        return right.equals(rule.right);

    }

    @Override
    public int hashCode() {
        int result = left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("");
        for (Token token : right) {
            joiner.add(token.getValue());
        }

        return new StringBuilder("[")
                .append(left)
                .append(" -> ")
                .append(joiner.toString())
                .append("]")
                .toString();
    }
}
