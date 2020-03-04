package lr.entity;

import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.V2_AsciiTableRenderer;
import de.vandermeer.asciitable.v2.render.WidthAbsoluteEven;
import de.vandermeer.asciitable.v2.themes.V2_E_TableThemes;
import util.Pair;
import lr.Grammar;
import lr.entity.CanonicalTable.Action.Type;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by savetisyan on 25/10/16
 */
public class CanonicalTable {
    private static final SimpleEntry<Pair<Component, String>, Action> INVALID =
            new SimpleEntry<>(null, new Action(Type.INVALID));

    private Grammar grammar;
    private Map<Pair<Component, String>, Action> table = new HashMap<>();

    public CanonicalTable(Grammar grammar) {
        this.grammar = grammar;
    }

    public void add(Component component, String input, Action action) {
        this.table.put(new Pair<>(component, input), action);
    }

    public Action get(Component component, String input) {
        return table.entrySet()
                .stream()
                .filter(x -> x.getKey().getKey().equals(component) && x.getKey().getValue().equals(input))
                .findFirst()
                .orElse(INVALID)
                .getValue();
    }

    public String prettyPrint() {
        final V2_AsciiTable table = new V2_AsciiTable();
        table.addRule();

        Object[] header = new Object[2 + grammar.getNonterms().size() + grammar.getTerms().size()];
        header[0] = "State";
        header[grammar.getTerms().size() + 1] = "Action";
        header[header.length - 1] = "Goto";
        table.addRow(header);
        table.addRule();

        final Map<String, Integer> columnMapping = new HashMap<>();

        Object[] row = new Object[header.length];
        row[0] = "";
        row[grammar.getTerms().size() + 1] = "$";
        columnMapping.put("$", grammar.getTerms().size() + 1);
        columnMapping.put("\n", grammar.getTerms().size() + 1);
        columnMapping.put("END", grammar.getTerms().size() + 1);

        for (int i = 1; i <= grammar.getTerms().size(); i++) {
            row[i] = grammar.getTerms().get(i - 1);
            columnMapping.put(grammar.getTerms().get(i - 1), i);
        }

        int j = 0;
        for (int i = grammar.getTerms().size() + 2; i < row.length; i++) {
            row[i] = grammar.getNonterms().get(j);
            columnMapping.put(grammar.getNonterms().get(j++), i);
        }

        table.addRow(row);
        table.addRule();

        this.table.entrySet().stream()
                .collect(Collectors.groupingBy(x -> x.getKey().getKey())).entrySet()
                .stream()
                .sorted((x, y) -> x.getKey().getId().compareTo(y.getKey().getId()))
                .forEach(e -> {
                    Object[] data = new Object[header.length];
                    Arrays.fill(data, "");
                    data[0] = e.getKey().getId();
                    for (Map.Entry<Pair<Component, String>, Action> entry : e.getValue()) {
                        Integer column = columnMapping.get(entry.getKey().getValue());
                        data[column] = entry.getValue().toString(grammar);
                    }
                    table.addRow(data);
                    table.addRule();
                });

        V2_AsciiTableRenderer rend = new V2_AsciiTableRenderer();
        rend.setTheme(V2_E_TableThemes.UTF_LIGHT.get());
        rend.setWidth(new WidthAbsoluteEven(300));
        return rend.render(table).toString();
    }

    public static class Action {
        public enum Type {
            SHIFT, REDUCE, GOTO, ACCEPT, INVALID
        }

        private Type actionType;
        private Component to;
        private Rule reduceRule;

        public Action(Type type) {
            this.actionType = type;
        }

        public Action(Type actionType, Component to) {
            this.actionType = actionType;
            this.to = to;
        }

        public Action(Type actionType, Rule rule) {
            this.actionType = actionType;
            this.reduceRule = rule;
        }

        public Component getTo() {
            return to;
        }

        public Type getActionType() {
            return actionType;
        }

        public Rule getReduceRule() {
            return reduceRule;
        }

        public String toString(Grammar grammar) {
            switch (actionType) {
                case SHIFT:
                    return "s(" + to.getId() + ")";
                case REDUCE:
                    return "r(" + (grammar.getRules().indexOf(reduceRule) + 1) + ")";
                case ACCEPT:
                    return "acc";
                case GOTO:
                    return String.valueOf(to.getId());
            }

            return "";
        }
    }
}
