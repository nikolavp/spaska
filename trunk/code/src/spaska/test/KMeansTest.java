package spaska.test;

import spaska.clusterers.IClusterer;
import spaska.clusterers.SimpleKMeans;
import spaska.data.Dataset;
import spaska.data.readers.ARFFInputReader;
import spaska.data.readers.NormalizeValidator;

public class KMeansTest {
	
	public static void main(String[] args) {
		ARFFInputReader input = new ARFFInputReader(".\\data\\contact-lenses.arff");
		input.addValidator(new NormalizeValidator());

		
		Dataset data = input.buildDataset();
		//System.out.println("Intput :\n" + data + "\n\n");
		
		IClusterer clusterer = new SimpleKMeans();
		clusterer.clusterize(data);
		
		System.out.println(clusterer.getStatistic());

	}

}
