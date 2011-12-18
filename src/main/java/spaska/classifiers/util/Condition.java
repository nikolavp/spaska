package spaska.classifiers.util;

import spaska.data.Attribute;
import spaska.data.Value;

public class Condition {
    private Attribute attribute; // attribute to be tested
    private Value value; // threshold value
    private Sign sign; // sign of test (<, >, =)
    private Value effect; // result of test (if any)
    private int number; // number of instances, reaching this test
    private Value majorityClass;// majority class of parent condition
    private double reach;// portion of instances with known value satisfying
                         // this condition

    // Constructors
    public Condition(Attribute a, Value v, Sign s) {
        attribute = a;
        value = v;
        sign = s;
    }

    public Condition(Attribute a, Value v, Sign s, Value majority) {
        attribute = a;
        value = v;
        sign = s;
        majorityClass = majority;
    } // Constructors

    // Accessors & Mutators
    public Attribute getAttribute() {
        return attribute;
    }

    public Value getValue() {
        return value;
    }

    public Sign getSign() {
        return sign;
    }

    public void setEffect(Value val) {
        effect = val;
    }

    public Value getEffect() {
        return effect;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Value getMajorityClass() {
        return majorityClass;
    }

    public void setMajorityClass(Value majorityClass) {
        this.majorityClass = majorityClass;
    }

    public double getReach() {
        return this.reach;
    }

    public void setReach(double reach) {
        this.reach = reach;
    }

    // Accessors & Mutators

    // test if condition holds for Value val
    public boolean ifTrue(Value val) {
        switch (getSign()) {
        case EQ:
            return val.getValue().equals(getValue().getValue());
        case NEQ:
            return !val.getValue().equals(getValue().getValue());
        case LT:
            return (Double) val.getValue() < (Double) getValue().getValue();
        case LTE:
            return (Double) val.getValue() <= (Double) getValue().getValue();
        case GT:
            return (Double) val.getValue() > (Double) getValue().getValue();
        case GTE:
            return (Double) val.getValue() >= (Double) getValue().getValue();
        default:
            return false;
        }
    }

    public String toString() {
        String result = String.format("%s %s %s", getAttribute().getName(),
                getSign(), getValue());
        if (effect != null) {
            result = String.format("%s : %s (%d)", result, effect.getValue(),
                    getNumber());
        }
        return result;
    }

}
