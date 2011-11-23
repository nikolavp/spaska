package spaska.data.readers;

import spaska.data.Dataset;
import spaska.gui.Parametrable;

/**
 * 
 * @author Vesko Georgiev
 */
public interface Validator extends Parametrable {

	public void setDataset(Dataset dataset);

	public boolean validate();
}
