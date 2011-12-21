package spaska.classifiers.util;

import static org.junit.Assert.assertThat;

import org.junit.Test;

import spaska.test.SpaskaTestBase;

public class InformationTest extends SpaskaTestBase {
    @Test
    public void shouldGiveProperEntropyForExampleData() {
        assertThat(Information.entropy(new int[] { 2, 3 }, 5), between(0.9, 1));
        assertThat(Information.entropy(new int[] { 2, 3, 4 }, 9),
                between(1.5, 1.6));
        assertThat(Information.entropy(new int[] { 9, 5 }, 14), between(0.9, 1));
        assertThat(Information.entropy(new int[] { 2, 2 }, 4), between(0.9, 1));
        assertThat(Information.entropy(new double[] { 0.4, 0.6 }),
                between(0.9, 1));
    }

}
