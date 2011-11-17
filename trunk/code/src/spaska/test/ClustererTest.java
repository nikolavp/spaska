package spaska.test;

import spaska.clusterers.ZeroClusterer;
import spaska.data.Dataset;
import spaska.data.readers.ARFFInputReader;


public class ClustererTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ARFFInputReader input = new ARFFInputReader(".\\data\\iris.arff");
//		input.addValidator(new TypeValidator());

		
		Dataset data = input.buildDataset();
		System.out.println("Intput :\n" + data + "\n\n");
		
		ZeroClusterer clusterer = new ZeroClusterer();
		clusterer.clusterize(data);
		
		System.out.println(clusterer.getStatistic());

	}

}
