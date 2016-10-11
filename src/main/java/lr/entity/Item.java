package lr.entity;

import lexer.entity.Token;
import lr.LR1;

/**
 * Created by savetisyan on 23/10/16
 */
public class Item {
    private Rule rule;
    private int dotPosition;
    private String lookahead;

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public int getDotPosition() {
        return dotPosition;
    }

    public void setDotPosition(int dotPosition) {
        this.dotPosition = dotPosition;
    }

    public String getLookahead() {
        return lookahead;
    }

    public void setLookahead(String lookahead) {
        this.lookahead = lookahead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (dotPosition != item.dotPosition) return false;
        if (!rule.equals(item.rule)) return false;
        return lookahead.equals(item.lookahead);

    }

    @Override
    public int hashCode() {
        int result = rule.hashCode();
        result = 31 * result + dotPosition;
        result = 31 * result + lookahead.hashCode();
        return result;
    }


    public StringBuilder ruleWithDot() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(rule.getLeft());
        sb.append(" -> ");

        StringBuilder rightPart = new StringBuilder();
        for (Token token : rule.getRight()) {
            rightPart.append(token.getValue());
        }

        if (dotPosition < rule.getRight().size()) {
            rightPart.insert(dotPosition, ".");
        } else {
            rightPart.append(".");
        }
        sb.append(rightPart);
        return sb;
    }

    @Override
    public String toString() {
        StringBuilder sb = ruleWithDot();
        sb.append(", ").append(lookahead.equals(LR1.END_OF_FILE) ? "$" : lookahead).append("]");
        return sb.toString();
    }
}
