package lr;

import lr.entity.Rule;

import java.util.List;

/**
 * Created by savetisyan on 23/10/16
 */
public class Grammar {
    private List<String> nonterms;
    private List<String> terms;
    private List<Rule> rules;
    private String start;

    public List<String> getNonterms() {
        return nonterms;
    }

    public void setNonterms(List<String> nonterms) {
        this.nonterms = nonterms;
    }

    public List<String> getTerms() {
        return terms;
    }

    public void setTerms(List<String> terms) {
        this.terms = terms;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Grammar grammar = (Grammar) o;

        if (!nonterms.equals(grammar.nonterms)) return false;
        if (!terms.equals(grammar.terms)) return false;
        if (!rules.equals(grammar.rules)) return false;
        return start.equals(grammar.start);

    }

    @Override
    public int hashCode() {
        int result = nonterms.hashCode();
        result = 31 * result + terms.hashCode();
        result = 31 * result + rules.hashCode();
        result = 31 * result + start.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "G = {\n" +
                "N: " + nonterms + "\n" +
                "E: " + terms + "\n" +
                "S: " + start + "\n" +
                "P: " + rules + "\n" +
                "}";
    }
}
