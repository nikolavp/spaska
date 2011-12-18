package spaska.classifiers.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import spaska.data.NominalValue;
import spaska.test.DataSetResources;

public class DatasetServiceTest {

    @Test
    public void shouldProperlyRecognizeTheClassIndex() {
        DatasetService s = new DatasetService(
                DataSetResources.getDataSet("iris"));
        NominalValue nv = new NominalValue("no");
        NominalValue nv1 = new NominalValue("no");
        assertThat(nv.equals(nv1), is(true));
        assertThat(s.classIndex(), is(4));
    }
}
