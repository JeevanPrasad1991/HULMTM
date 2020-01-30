package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class catmanMapping {

	/*
	storeid
	categoryid
	displayid
	processid
	uid
	*/
	
	
	public String Table_Structure;
	
	public String getTable_Structure() {
		return Table_Structure;
	}
	public void setTable_Structure(String table_Structure) {
		this.Table_Structure=table_Structure;
	}
	public ArrayList<String> getStore_ID() {
		return store_ID;
	}
	public void setStore_ID(String store_ID) {
		this.store_ID.add(store_ID);
	}
	public ArrayList<String> getCategoryid() {
		return categoryid;
	}
	public void setCategoryid(String categoryid) {
		this.categoryid.add(categoryid);
	}
	public ArrayList<String> getDisplayid() {
		return displayid;
	}
	public void setDisplayid(String displayid) {
		this.displayid.add(displayid);
	}
	public ArrayList<String> getProcessid() {
		return processid;
	}
	public void setProcessid(String processid) {
		this.processid.add(processid);
	}
	public ArrayList<String> getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid.add(uid);
	}
	ArrayList<String>store_ID = new ArrayList<String>();
	ArrayList<String>categoryid = new ArrayList<String>();
	ArrayList<String>displayid = new ArrayList<String>();
	ArrayList<String>processid = new ArrayList<String>();
	ArrayList<String>uid = new ArrayList<String>();
	ArrayList<String>brand_id = new ArrayList<String>();

	public ArrayList<String> getBrand_id() {
		return brand_id;
	}
	public void setBrand_id(String brand_id) {
		this.brand_id.add(brand_id);
	}
	
	
	
	
	
}
