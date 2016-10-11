package lr.entity;

import lexer.entity.Token;

import java.util.List;

public class ItemRightPart {
    private List<Token> alpha;
    private Token B;
    private List<Token> betta;
    private String a;

    public ItemRightPart(List<Token> alpha, Token b, List<Token> betta, String a) {
        this.alpha = alpha;
        B = b;
        this.betta = betta;
        this.a = a;
    }

    public List<Token> getAlpha() {
        return alpha;
    }

    public void setAlpha(List<Token> alpha) {
        this.alpha = alpha;
    }

    public Token getB() {
        return B;
    }

    public void setB(Token b) {
        B = b;
    }

    public List<Token> getBetta() {
        return betta;
    }

    public void setBetta(List<Token> betta) {
        this.betta = betta;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    @Override
    public String toString() {
        return String.format("[α = %s, B = %s, β = %s, a = %s]", alpha, B, betta, a);
    }
}