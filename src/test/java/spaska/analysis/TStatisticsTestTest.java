package spaska.analysis;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import spaska.test.SpaskaTestBase;

public class TStatisticsTestTest extends SpaskaTestBase {

    @Test
    public void shouldProperlyCalculateTheMeanOfAnArray() throws Exception {
        double mean = EmpiricalTest.getMean(new double[] { 1, 2, 3 });
        assertThat(mean, is(2.0));
    }

    @Test
    public void shouldProperlyCalculateTheDeviationOfAnArray() throws Exception {
        double standardDeviation = EmpiricalTest
                .getStandardDeviation(new double[] { 1, 2, 3 });
        assertThat(standardDeviation, is(1.0));
    }

    @Test
    public void shouldWorkProperlyWithExampleData() {
        double[] pizzaA = new double[] { 12.9, 5.7, 16, 14.3, 2.4, 1.6, 14.6,
                10.2, 4.3, 6.6 };
        double[] pizzaB = new double[] { 16, 7.5, 16, 15.7, 13.2, 5.4, 15.5,
                11.3, 15.4, 10.6 };
        double[] crossfertilized = new double[] { 23.5, 12.0, 21.0, 22, 19.1,
                21.5, 22.1, 20.4, 18.3, 21.6, 23.3, 21, 22.1, 23, 12 };
        double[] selffertilized = new double[] { 17.4, 20.4, 20, 20, 18.4,
                18.6, 18.6, 15.3, 16.5, 18, 16.3, 18, 12.8, 15.5, 18 };
        TStatisticsTest ptt = new TStatisticsTest(0.05,
                TestType.OneSidedLessThan);
        assertThat(ptt.shouldRejectNull(pizzaA, pizzaB), is(true));
        ptt = new TStatisticsTest(0.05, TestType.OneSidedGreaterThan);
        assertThat(ptt.shouldRejectNull(pizzaA, pizzaB), is(false));
        ptt = new TStatisticsTest(0.05, TestType.TwoSided);
        assertThat(ptt.shouldRejectNull(crossfertilized, selffertilized),
                is(false));
        // ptt = new PairedTTest(0.05, TestType.OneWayGreaterThan);
        assertThat(ptt.shouldRejectNull(crossfertilized, selffertilized),
                is(false));
        assertThat(ptt.getPValue(crossfertilized, selffertilized),
                between(0.05, 0.058));
    }
}
