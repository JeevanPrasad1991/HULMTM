package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class ShelfMaster {
	
	public String Table_Structure;
	
	public String getTable_Structure() {
		return Table_Structure;
	}
	public void setTable_Structure(String table_Structure) {
		Table_Structure = table_Structure;
	}
	
	
	public ArrayList<String> getShelf_id() {
		return shelf_id;
	}
	public void setShelf_id(String shelf_id) {
		this.shelf_id.add(shelf_id);
	}
	public ArrayList<String> getShelf() {
		return shelf;
	}
	public void setShelf(String shelf) {
		this.shelf.add(shelf);
	}
	public ArrayList<String> shelf_id = new ArrayList<String>();
	public ArrayList<String> shelf = new ArrayList<String>();

	

}
