package spaska.data.readers;

import spaska.data.Dataset;
import spaska.gui.Parametrable;

/**
 * 
 * @author Vesko Georgiev
 */
public interface Validator extends Parametrable {

	void setDataset(Dataset dataset);

	boolean validate();
}
