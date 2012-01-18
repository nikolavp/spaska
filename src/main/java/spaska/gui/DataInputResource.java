package spaska.gui;

import java.io.File;

public class DataInputResource {
	
	private String type = null;
	private String tableName = null;
	private File openedFile = null;
	
	public DataInputResource(String tableName) {
		this.tableName = tableName;
		this.type = "table";
	}
	
	public DataInputResource(File file) {
		this.openedFile = file;
		this.type = "file";
	}
	
	public Object getResource() {
		if (this.type == "table") {
			return this.tableName;
		} else if (this.type == "file") {
			return this.openedFile;
		}
		return null;
	}

}
