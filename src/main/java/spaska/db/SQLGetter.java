package spaska.db;

import java.util.ArrayList;

import spaska.data.Pair;
import spaska.data.Value;

public interface SQLGetter {
	public ArrayList<Value> getDomain(String tableName, String attributeName);

	public ArrayList<Pair<String, Integer>> getAttributes(String tableName);

	public ArrayList<String[]> getData(String tableName);
}
