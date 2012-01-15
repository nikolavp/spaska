package spaska.classifiers.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a node in tree based classifiers.
 * <p>
 * If you are implementing a hierarchical classifier this is the utility class
 * that should be used to represent a node in the resulting tree.
 * 
 * @author nikolavp
 * 
 */
public final class Node {

    private Object value;

    private List<Node> children;

    /**
     * Constructors for node with a value.
     * 
     * @param value
     *            the value that will be kept in this node
     */
    public Node(Object value) {
        this(value, new ArrayList<Node>());
    }

    /**
     * Constructor for node with a value and children.
     * 
     * @param value
     *            the value that will be kept in this node
     * @param list
     *            the children nodes for this node
     */
    public Node(Object value, List<Node> list) {
        this.value = value;
        this.children = list;
    } // Constructors

    /**
     * Get the children for this node.
     * 
     * @return the children for this node
     */
    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Adds a child node to this node.
     * 
     * @param node
     *            the new child for the current node object
     */
    public void addChild(Node node) {
        children.add(node);
    }

    /**
     * Get the value behind this node.
     * 
     * @return the value behind this node
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value behind this node.
     * 
     * @param newValue
     *            the new value behind this node
     */
    public void setValue(Object newValue) {
        value = newValue;
    }

    @Override
    public String toString() {
        return buildString(this, "");
    }

    private static String buildString(Node node, String indentation) {
        StringBuilder b = new StringBuilder();
        b.append(indentation.toString());
        b.append(node.getValue().toString());
        b.append('\n');
        for (Node child : node.getChildren()) {
            b.append(buildString(child, indentation + "|  "));
        }
        return b.toString();
    }

}
