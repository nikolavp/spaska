package spaska.analysis;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class WilcoxonTest {
    @Test
    public void shouldWorkProperlyWithExampleData() {
        Wilcoxon wil = new Wilcoxon();
        double[] z1 = { 0.90, 0.91, 0.92, 0.93, 0.89, 0.911, 0.90, 0.92 };
        double[] z2 = { 0.90, 0.91, 0.92, 0.93, 0.90, 0.911, 0.90, 0.92 };

        assertThat(wil.shouldRejectNull(z1, z2), is(true));
    }
}
