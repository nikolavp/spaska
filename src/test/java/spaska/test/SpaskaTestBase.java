package spaska.test;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Provides some nice matchers that are not found in the standard hamcrest
 * matchers toolkit.
 * 
 * @author nikolavp
 * 
 */
public abstract class SpaskaTestBase {
    protected static Matcher<Double> between(final double begin, final double end) {
        if (end < begin) {
            throw new IllegalArgumentException("Invalid interval!");
        }
        return new TypeSafeMatcher<Double>() {
            @Override
            public void describeTo(Description arg0) {
                arg0.appendText("number in [").appendValue(begin).
                appendText(",").appendValue(end).appendText("]");
            }

            @Override
            protected boolean matchesSafely(Double value) {
                return value >= begin && value <= end;
            }
        };
    }
}
