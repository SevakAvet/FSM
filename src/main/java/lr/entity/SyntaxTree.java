package lr.entity;

import lexer.entity.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by savetisyan on 25/10/16
 */
public class SyntaxTree {
    private Node root;
    private List<Rule> appliedRules;

    public SyntaxTree(Node root) {
        this.root = root;
    }

    public SyntaxTree(Node root, List<Rule> appliedRules) {
        this(root);
        this.appliedRules = appliedRules;
    }

    public static class Node {
        private Token value;
        private int id;
        private List<Node> childs = new ArrayList<>();

        public Node(String value, int id) {
            this.value = new Token("", value);
            this.id = id;
        }

        public Node(Token value, int id) {
            this.value = value;
            this.id = id;
        }

        public Token getValue() {
            return value;
        }

        private void setValue(Token value) {
            this.value = value;
        }

        public List<Node> getChilds() {
            return childs;
        }

        public void addChild(Node node) {
            this.childs.add(node);
        }

        public void addChilds(List<Node> childs) {
            this.childs.addAll(childs);
        }

        public int getId() {
            return id;
        }
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public List<Rule> getAppliedRules() {
        return appliedRules;
    }

    public StringBuilder prettyPrint() {
        return render(root, 0, new StringBuilder(), false, new ArrayList<>());
    }

    private StringBuilder render(Node node, int level, StringBuilder sb, boolean isLast, List<Boolean> hierarchyTree) {
        indent(sb, level, isLast, hierarchyTree)
                .append(node.getValue().getValue())
                .append("\n");

        for (int i = 0; i < node.getChilds().size(); i++) {
            boolean last = ((i + 1) == node.getChilds().size());
            hierarchyTree.add(i != node.getChilds().size() - 1);
            render(node.getChilds().get(i), level + 1, sb, last, hierarchyTree);
            hierarchyTree.remove(hierarchyTree.size() - 1);
        }
        return sb;
    }


    private StringBuilder indent(StringBuilder sb, int level, boolean isLast, List<Boolean> hierarchyTree) {
        String indentContent = "\u2502  ";

        for (int i = 0; i < hierarchyTree.size() - 1; ++i) {
            if (hierarchyTree.get(i)) {
                sb.append(indentContent);
            } else {
                sb.append("   ");
            }
        }

        if (level > 0) {
            sb.append(isLast
                    ? "\u2514\u2500\u2500"
                    : "\u251c\u2500\u2500");
        }

        return sb;
    }
}
