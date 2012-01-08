package spaska.classifiers.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.data.Attribute;
import spaska.data.Attribute.ValueType;
import spaska.data.Factory;
import spaska.data.Value;

public class ConditionTest {
    private static final Logger LOG = LoggerFactory
            .getLogger(ConditionTest.class);

    @Test
    public void testBasicUsage() {
        Attribute a = new Attribute("petal-width", ValueType.Numeric);
        Value v = Factory.createValue("2.3");
        Condition cond = new Condition(a, v, Sign.LT);
        LOG.info("{}\n", cond);
        Node tree = new Node(cond);
        tree.addChild(new Node(cond));
        tree.addChild(new Node(cond));
        tree.addChild(new Node(cond));
        tree.getChildren().get(0).addChild(new Node(cond));
        tree.getChildren().get(0).addChild(new Node(cond));
        LOG.info(tree.toString());
    }

}
