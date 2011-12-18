package spaska.statistics;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.analysis.TStatisticsTest;
import spaska.analysis.TestType;

public class CompareStatisticsTest {
    private static final Logger LOG = LoggerFactory
            .getLogger(CompareStatisticsTest.class);
    @Test
    public void testBasicUsage() {
        CompareStatistics s = new CompareStatistics();
        double[] d1 = { 1.2, 3.4, 0.54, 0.78, 1.2, 3.4, 0.54, 0.78, 1.2, 3.4,
                0.54, 0.78 };
        double[] d2 = { 1.2, 3.4, 0.54, 0.78, 1.2, 3.4, 0.54, 0.78, 1.2, 3.4,
                0.54, 0.78 };
        long[] times = { 123456l, 123456l, 123456l, 123456l, 123456l, 123456l,
                123456l, 123456l, 123456l, 123456l, 123456l, 123456l };
        s.setFirstSamplePopulation(d1);
        s.setSecondSamplePopulation(d2);
        s.setTimes1(times);
        s.setTimes2(times);
        s.setFirstClassifierName("Foo");
        s.setSecondClassifierName("Bar");
        s.setTest(new TStatisticsTest(0.05, TestType.OneSidedLessThan));
        LOG.info(s.toString());
    }
}
