package spaska.classifiers.util;

import java.util.ArrayList;
import java.util.List;

public final class Node {

    private Object value;

    private List<Node> children;

    // Constructors
    public Node(Object value) {
        this(value, Node.createChildren());
    }

    public Node(Object value, List<Node> list) {
        this.value = value;
        this.children = list;
    } // Constructors

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node node) {
        children.add(node);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object newValue) {
        value = newValue;
    }

    public String toString() {
        return buildString(this, "");
    }

    public String buildString(Node node, String indentation) {
        StringBuilder b = new StringBuilder();
        b.append(indentation.toString());
        b.append(node.getValue().toString());
        b.append('\n');
        for (Node child : node.getChildren()) {
            b.append(buildString(child, indentation + "|  "));
        }
        return b.toString();
    }

    public static List<Node> createChildren() {
        return new ArrayList<Node>();
    }

}
