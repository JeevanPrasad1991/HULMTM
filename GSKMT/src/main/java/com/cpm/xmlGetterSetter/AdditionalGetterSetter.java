package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class AdditionalGetterSetter {
	
	
	ArrayList<String> store_type_id = new ArrayList<String>();
	ArrayList<String> category_id = new ArrayList<String>();
	ArrayList<String> display_id = new ArrayList<String>();
	ArrayList<String> process_id = new ArrayList<String>();
	public ArrayList<String> getProcess_id() {
		return process_id;
	}
	public void setProcess_id(String process_id) {
		this.process_id.add(process_id);
	}
	String meta_data;
	
	public String getMeta_data() {
		return meta_data;
	}
	public void setMeta_data(String meta_data) {
		this.meta_data = meta_data;
	}
	public ArrayList<String> getStore_type_id() {
		return store_type_id;
	}
	public void setStore_type_id(String store_type_id) {
		this.store_type_id.add(store_type_id);
	}
	public ArrayList<String> getCategory_id() {
		return category_id;
	}
	public void setCategory_id(String category_id) {
		this.category_id.add(category_id);
	}
	public ArrayList<String> getDisplay_id() {
		return display_id;
	}
	public void setDisplay_id(String display_id) {
		this.display_id.add(display_id);
	}
;

}
