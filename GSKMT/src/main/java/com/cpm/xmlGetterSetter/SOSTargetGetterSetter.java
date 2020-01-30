package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class SOSTargetGetterSetter {
	
	ArrayList<String> store_id = new ArrayList<String>();
	ArrayList<String> category_id = new ArrayList<String>();
	ArrayList<String> process_id = new ArrayList<String>();
	ArrayList<String> target = new ArrayList<String>();
	String meta_data;
	
	public ArrayList<String> getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id.add(store_id);
	}
	public ArrayList<String> getCategory_id() {
		return category_id;
	}
	public void setCategory_id(String category_id) {
		this.category_id.add(category_id);
	}
	public ArrayList<String> getProcess_id() {
		return process_id;
	}
	public void setProcess_id(String process_id) {
		this.process_id.add(process_id);
	}
	public ArrayList<String> getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target.add(target);
	}
	public String getMeta_data() {
		return meta_data;
	}
	public void setMeta_data(String meta_data) {
		this.meta_data = meta_data;
	}


}
