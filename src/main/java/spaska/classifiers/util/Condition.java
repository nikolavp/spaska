package spaska.classifiers.util;

import spaska.data.Attribute;
import spaska.data.Value;

/**
 * A condition class that is used when building tree classifiers.
 */
public final class Condition {
    private Attribute attribute; // attribute to be tested
    private Value value; // threshold value
    private Sign sign; // sign of test (<, >, =)
    private Value effect; // result of test (if any)
    private int number; // number of instances, reaching this test
    private Value majorityClass; // majority class of parent condition
    private double reach; // portion of instances with known value satisfying
                          // this condition

    /**
     * Constructs a condition for the attribute with the given value and sing.
     * 
     * @param a
     *            the attribute that will code this attribute
     * @param v
     *            the value will be used as a threshold
     * @param s
     *            the sign of the test
     */
    public Condition(Attribute a, Value v, Sign s) {
        attribute = a;
        value = v;
        sign = s;
    }

    /**
     * Constructs a condition for the attribute with the given value, sing and
     * majority class of the parent condition.
     * 
     * @param a
     *            the attribute that will code this attribute
     * @param v
     *            the value will be used as a threshold
     * @param s
     *            the sign of the test
     * @param majority
     *            the majority class of the parent condition
     */

    public Condition(Attribute a, Value v, Sign s, Value majority) {
        attribute = a;
        value = v;
        sign = s;
        majorityClass = majority;
    }

    /**
     * Get the attribute for this condition.
     * 
     * @return the attribute for this condition
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * Get the value for this condition.
     * 
     * @return the value for this condition
     */
    public Value getValue() {
        return value;
    }

    /**
     * Get the sign for this condition.
     * 
     * @return the value for this condition
     */
    public Sign getSign() {
        return sign;
    }

    /**
     * Set the result of this condition.
     * 
     * @param val
     *            the new result of this condition
     */
    public void setEffect(Value val) {
        effect = val;
    }

    /**
     * Get the effect(if any) for this condition.
     * 
     * @return the effect for this condition
     */
    public Value getEffect() {
        return effect;
    }

    /**
     * Get the number of instances that reached this condition.
     * 
     * @return the number of instances that reached this condition
     */
    public int getNumber() {
        return number;
    }

    /**
     * Set the number of instances that reached this condition.
     * 
     * @param number
     *            the new number of instances that reached this condition
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Get the majority class of the parent condition.
     * 
     * @return the majority class of the parent condition
     */
    public Value getMajorityClass() {
        return majorityClass;
    }

    /**
     * Set the majority class of the parent condition.
     * 
     * @param majorityClass
     *            the new majority class of the parent condition
     */
    public void setMajorityClass(Value majorityClass) {
        this.majorityClass = majorityClass;
    }

    /**
     * Get the portion of the instances that satisfied this condition.
     * 
     * @return the portion of the instances that satisfied this condition
     */
    public double getReach() {
        return this.reach;
    }

    /**
     * Set the portion of the instances that satisfied this condition.
     * 
     * @param reach
     *            the new portion of the instances that satisfied this condition
     */
    public void setReach(double reach) {
        this.reach = reach;
    }

    /**
     * Tests if the condition is true for the provided value.
     * 
     * @param val
     *            the value to be tested
     * @return true if the condition holds for Value val and false otherwise.
     */
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

    @Override
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
