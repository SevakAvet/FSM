package lr;

import lexer.entity.Token;
import lr.entity.*;
import lr.entity.CanonicalTable.Action;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lr.entity.CanonicalTable.Action.Type.*;
import static lr.entity.SyntaxTree.Node;

/**
 * Created by savetisyan on 23/10/16
 */
public class LR1 {
    public static final String END_OF_FILE = "\n";
    private Map<String, Set<String>> firstCache = new HashMap<>();
    private CanonicalTable table;
    private Grammar grammar;
    private Set<Component> components;

    public LR1(Grammar grammar) {
        this.grammar = extend(grammar);
        this.table = buildCanonicalTable();
    }

    private Grammar extend(Grammar grammar) {
        String newStart = grammar.getStart() + "'";
        Rule startRule = new Rule(newStart, Collections.singletonList(new Token("NonTerminal", grammar.getStart())));
        grammar.setStart(newStart);
        grammar.getRules().add(startRule);
        return grammar;
    }

    public SyntaxTree buildTree(List<Token> inputs) {
        List<Rule> rules = rulesSequence(inputs);

        AtomicInteger nodeId = new AtomicInteger(0);
        SyntaxTree tree = new SyntaxTree();
        Node root = new Node(rules.get(0).getLeft(), nodeId.getAndIncrement());
        List<Token> childs = rules.get(0).getRight();
        tree.setRoot(root);

        List<Node> currentChilds = new ArrayList<>();
        for (Token child : childs) {
            Node node = new Node(child, nodeId.getAndIncrement());
            currentChilds.add(node);
            root.addChild(node);
        }

        int ruleId = 1;
        while (true) {
            List<Node> newChilds = new ArrayList<>();
            Rule rule = rules.get(ruleId);

            boolean stop = false;
            int stoppedOn = -1;
            for (int j = currentChilds.size() - 1; j >= 0; j--) {
                Node currentChild = currentChilds.get(j);

                if (currentChild.getValue().getValue().equals(rule.getLeft())) {
                    List<Node> childToAdd = rule.getRight()
                            .stream()
                            .map(x -> new Node(x, nodeId.getAndIncrement()))
                            .collect(Collectors.toList());

                    newChilds.addAll(childToAdd);
                    currentChild.addChilds(childToAdd);

                    stoppedOn = j;
                    if (ruleId + 1 >= rules.size()) {
                        stop = true;
                    } else {
                        ruleId++;
                    }

                    break;
                }
            }

            currentChilds.remove(stoppedOn);
            currentChilds.addAll(stoppedOn, newChilds);

            if (stop) {
                break;
            }
        }

        return tree;
    }

    public List<Rule> rulesSequence(List<Token> inputs) {
        if (!inputs.get(inputs.size() - 1).getValue().equals(END_OF_FILE)) {
            inputs.add(new Token("", END_OF_FILE));
        }

        Stack<Component> stack = new Stack<>();
        stack.push(getComponent(0));

        List<Rule> appliedRules = new ArrayList<>();

        int curPos = 0;
        boolean accept = false;
        while (!accept) {
            String symbol = inputs.get(curPos).getValue();

            Component peek = stack.peek();
            Action action = table.get(peek, symbol);

            switch (action.getActionType()) {
                case SHIFT:
                    stack.push(action.getTo());
                    curPos++;
                    break;
                case REDUCE:
                    Rule rule = action.getReduceRule();
                    appliedRules.add(rule);
                    int tokensToRemove = rule.getRight().size();
                    for (int i = 0; i < tokensToRemove; i++) {
                        stack.pop();
                    }

                    peek = stack.peek();
                    Action goTo = table.get(peek, rule.getLeft());
                    stack.push(goTo.getTo());
                    break;
                case ACCEPT:
                    accept = true;
                    break;
                case INVALID:
                    throw new IllegalArgumentException("Error state! Invalid input...");
            }
        }

        Collections.reverse(appliedRules);
        return appliedRules;
    }

    public CanonicalTable getTable() {
        return table;
    }

    private CanonicalTable buildCanonicalTable() {
        final CanonicalTable canonicalTable = new CanonicalTable(grammar);
        this.components = items();

        for (Component component : components) {
            for (Item item : component.getItems()) {
                ItemRightPart itemRightPart = parseRightPart(item);

                if (itemRightPart.getB() == null) {
                    List<Token> betta = itemRightPart.getBetta();
                    for (Token token : betta) {
                        if (token.getClassName().equals("")) {
                            continue;
                        }

                        if (grammar.getTerms().contains(token.getValue())) {
                            Set<Item> newComponent = goTo(component, token.getValue());
                            Component to = getComponent(this.components, newComponent);
                            if (to != null) {
                                canonicalTable.add(component, token.getValue(), new Action(SHIFT, to));
                            }
                        }

                        break;
                    }
                }


                if (item.getDotPosition() == item.getRule().getRight().size()) {
                    if (!item.getRule().getLeft().equals(grammar.getStart())) {
                        canonicalTable.add(component, item.getLookahead(), new Action(REDUCE, item.getRule()));
                    } else {
                        canonicalTable.add(component, END_OF_FILE, new Action(ACCEPT));
                    }
                }
            }

            for (String nonterm : grammar.getNonterms()) {
                Set<Item> newComponent = goTo(component, nonterm);
                Component to = getComponent(this.components, newComponent);
                if (to != null) {
                    canonicalTable.add(component, nonterm, new Action(GOTO, to));
                }
            }
        }

        return canonicalTable;
    }

    private Component getComponent(Set<Component> components, Set<Item> newComponent) {
        Component to = null;
        for (Component c : components) {
            if (c.getItems().equals(newComponent)) {
                to = c;
                break;
            }
        }
        return to;
    }

    private Component getComponent(int componentId) {
        return components.stream().filter(x -> x.getId() == componentId).findFirst().get();
    }

    public Set<Item> closure(Set<Item> items) {
        while (true) {
            List<Item> newItems = new ArrayList<>();
            for (Item item : items) {
                ItemRightPart rightPart = parseRightPart(item);
                if (rightPart.getB() == null) {
                    continue;
                }

                List<String> tokens = rightPart.getBetta()
                        .stream()
                        .map(Token::getValue)
                        .collect(Collectors.toList());
                tokens.add(rightPart.getA());
                if (tokens.isEmpty()) {
                    tokens.add("");
                }

                List<String> terminals = first(tokens).stream()
                        .filter(x -> grammar.getTerms().contains(x) || x.equals(END_OF_FILE))
                        .collect(Collectors.toList());

                if (!terminals.isEmpty()) {
                    for (Rule rule : grammar.getRules()) {
                        if (!rule.getLeft().equals(rightPart.getB().getValue())) {
                            continue;
                        }

                        for (String terminal : terminals) {
                            Item newItem = new Item();
                            newItem.setDotPosition(0);
                            newItem.setRule(rule);
                            newItem.setLookahead(terminal);
                            newItems.add(newItem);
                        }
                    }
                }
            }

            if (!items.addAll(newItems)) {
                break;
            }
        }

        return items;
    }

    public Set<Item> goTo(Component component, String x) {
        Set<Item> gotoItems = new HashSet<>();
        for (Item item : component.getItems()) {
            List<Token> right = item.getRule().getRight();
            String symbol = "";
            if (item.getDotPosition() < right.size()) {
                symbol = right.get(item.getDotPosition()).getValue();
            }

            if (symbol.equals(x)) {
                gotoItems.add(incrementAndGet(item));
            }
        }

        return closure(gotoItems);
    }

    public Set<Component> items() {
        Set<Component> components = new HashSet<>();

        Set<Item> items = new HashSet<>();
        Item item = new Item();
        item.setDotPosition(0);
        item.setLookahead(END_OF_FILE);

        // Rule with left part equals to Start symbol must be present in set of rules.
        Rule firstRule = grammar.getRules()
                .stream()
                .filter(x -> x.getLeft().equals(grammar.getStart()))
                .findFirst()
                .get();

        item.setRule(firstRule);
        items.add(item);
        components.add(new Component(0, closure(items)));

        final AtomicInteger id = new AtomicInteger(1);
        while (true) {
            Set<Component> newComponents = new HashSet<>();
            for (Component component : components) {
                Stream.concat(grammar.getNonterms().stream(), grammar.getTerms().stream())
                        .forEach(x -> {
                            Set<Item> newItems = goTo(component, x);
                            if (!newItems.isEmpty()) {
                                Component e = new Component(id.get(), newItems);
                                if (!components.contains(e)) {
                                    newComponents.add(e);
                                    id.getAndIncrement();
                                }
                            }
                        });
            }

            if (!components.addAll(newComponents)) {
                break;
            }
        }

        return components;
    }

    private Item incrementAndGet(Item item) {
        Item newItem = new Item();
        newItem.setDotPosition(item.getDotPosition() + 1);
        newItem.setLookahead(item.getLookahead());
        newItem.setRule(item.getRule());
        return newItem;
    }

    private ItemRightPart parseRightPart(Item item) {
        Rule rule = item.getRule();
        List<Token> right = rule.getRight();
        int dotPosition = item.getDotPosition();

        if (right.size() == dotPosition) {
            return new ItemRightPart(right, null, Collections.emptyList(), item.getLookahead());
        }

        List<Token> alpha = right.subList(0, dotPosition);
        if (alpha.isEmpty()) {
            alpha = new ArrayList<>();
            alpha.add(new Token("", ""));
        }

        Token B = right.get(dotPosition);
        if (!grammar.getNonterms().contains(B.getValue())) {
            B = null;
        }

        int bettaStart = B == null ? dotPosition : dotPosition + 1;
        List<Token> betta;
        if (bettaStart < right.size()) {
            betta = right.subList(bettaStart, right.size());
        } else {
            betta = new ArrayList<>();
            betta.add(new Token("", ""));
        }

        return new ItemRightPart(alpha, B, betta, item.getLookahead());
    }

    private Set<String> first(List<String> symbols) {
        if (Utils.trueForAllElements(symbols, x -> x.equals("") || x.equals(END_OF_FILE))) {
            return first("");
        }

        int i = Utils.findFirstIndex(symbols, x -> !x.equals("") && !x.equals(END_OF_FILE));
        String firstSymbol = symbols.get(i);
        Set<String> first = first(firstSymbol);
        boolean previousContainsEmptySymbol = first.contains("");
        first.remove("");

        ++i;
        for (; i < symbols.size(); i++) {
            if (previousContainsEmptySymbol) {
                firstSymbol = symbols.get(i);
                Set<String> firstSet = first(firstSymbol);
                previousContainsEmptySymbol = firstSet.contains("");
                firstSet.remove("");
                first.addAll(firstSet);
            }
        }

        return first;
    }

    private Set<String> first(String symbol) {
        if (firstCache.containsKey(symbol)) {
            return firstCache.get(symbol);
        }

        if (symbol.isEmpty()) {
            HashSet<String> strings = new HashSet<>();
            strings.add(END_OF_FILE);
            return strings;
        }

        Set<String> first = new HashSet<>();
        if (grammar.getTerms().contains(symbol)) {
            first.add(symbol);

            firstCache.put(symbol, first);
            return first;
        }

        grammar.getRules().stream()
                .filter(rule -> rule.getLeft().equals(symbol))
                .forEach(rule -> {
                    List<Token> right = rule.getRight();
                    Set<String> firstSet = first(right.get(0).getValue());
                    firstSet.remove("");
                    first.addAll(firstSet);

                    int i = 0;
                    while (i < right.size() - 1 && first(right.get(i).getValue()).contains("")) {
                        if (i == right.size() - 2 && first(right.get(i + 1).getValue()).contains("")) {
                            first.add("");
                            break;
                        }

                        firstSet = first(right.get(i + 1).getValue());
                        firstSet.remove("");
                        first.addAll(firstSet);
                        ++i;
                    }
                });

        firstCache.put(symbol, first);
        return first;
    }

}
