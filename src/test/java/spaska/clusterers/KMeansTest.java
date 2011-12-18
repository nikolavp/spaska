package spaska.clusterers;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.data.Dataset;
import spaska.data.readers.NormalizeValidator;
import spaska.test.DataSetResources;

public class KMeansTest {

    private static final Logger LOG = LoggerFactory.getLogger(KMeansTest.class);
    
    @Test
    public void shouldWorkForBasicUsageOfKMeans() {
        Dataset data = new DataSetResources("iris").withValidator(
                new NormalizeValidator()).getDataSet();
        IClusterer clusterer = new SimpleKMeans();
        clusterer.clusterize(data);

        LOG.info(clusterer.getStatistic().toString());

    }

}
