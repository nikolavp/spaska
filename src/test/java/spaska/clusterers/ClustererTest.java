package spaska.clusterers;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.data.Dataset;
import spaska.statistics.ClustererStatistics;
import spaska.test.DataSetResources;

public class ClustererTest {

    private static final Logger LOG = LoggerFactory
            .getLogger(ClustererTest.class);

    @Test
    public void testing() {
        Dataset data = DataSetResources.getDataSet("iris");
        ZeroClusterer clusterer = new ZeroClusterer();
        clusterer.clusterize(data);

        ClustererStatistics statistic = clusterer.getStatistic();
        LOG.info(statistic.toString());
    }

}
