package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class StockMappingGetterSetter {
	
	public String meta_data;
	
	public String getMeta_data() {
		return meta_data;
	}
	public void setMeta_data(String meta_data) {
		this.meta_data = meta_data;
	}
	ArrayList<String> brand_id = new ArrayList<String>();
	public ArrayList<String> getBrand_id() {
		return brand_id;
	}
	public void setBrand_id(String brand_id) {
		this.brand_id.add(brand_id);
	}
	public ArrayList<String> getSku_id() {
		return sku_id;
	}
	public void setSku_id(String sku_id) {
		this.sku_id.add(sku_id);
	}
	public ArrayList<String> getSku_sequence() {
		return sku_sequence;
	}
	public void setSku_sequence(String sku_sequence) {
		this.sku_sequence.add(sku_sequence);
	}
	public ArrayList<String> getBrand_sequence() {
		return brand_sequence;
	}
	public void setBrand_sequence(String brand_sequence) {
		this.brand_sequence.add(brand_sequence);
	}
	public ArrayList<String> getProcess_id() {
		return process_id;
	}
	public void setProcess_id(String process_id) {
		this.process_id.add(process_id);
	}
	ArrayList<String> sku_id = new ArrayList<String>();
	ArrayList<String> sku_sequence = new ArrayList<String>();
	ArrayList<String> brand_sequence = new ArrayList<String>();
	ArrayList<String> process_id = new ArrayList<String>();
	ArrayList<String> KEY_ID = new ArrayList<String>();

	public ArrayList<String> getSTATE_ID() {
		return STATE_ID;
	}

	public void setSTATE_ID(String STATE_ID) {
		this.STATE_ID.add(STATE_ID);
	}

	ArrayList<String> STATE_ID = new ArrayList<String>();
	
	public ArrayList<String> getSTORETYPE_ID() {
		return STORETYPE_ID;
	}
	public void setSTORETYPE_ID(String sTORETYPE_ID) {
		this.STORETYPE_ID.add(sTORETYPE_ID);
	}
	ArrayList<String> STORETYPE_ID = new ArrayList<String>();

	public ArrayList<String> getCLASS_ID() {
		return CLASS_ID;
	}

	public void setCLASS_ID(String CLASS_ID) {
		this.CLASS_ID.add(CLASS_ID);
	}

	ArrayList<String> CLASS_ID = new ArrayList<String>();

	public ArrayList<String> getKEY_ID() {
		return KEY_ID;
	}
	public void setKEY_ID(String KEY_ID) {
		this.KEY_ID.add(KEY_ID);
	}

}
