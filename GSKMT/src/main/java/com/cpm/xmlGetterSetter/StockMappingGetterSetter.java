package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class StockMappingGetterSetter {

	String stockmapping_table;
	ArrayList<String>store_cd=new ArrayList<>();
	ArrayList<String>sku_id=new ArrayList<>();
	ArrayList<String>brand_cd=new ArrayList<>();
	ArrayList<String>sku_sequence=new ArrayList<>();
	ArrayList<String>brand_sequnence=new ArrayList<>();

	public String getStockmapping_table() {
		return stockmapping_table;
	}

	public void setStockmapping_table(String stockmapping_table) {
		this.stockmapping_table = stockmapping_table;
	}

	public ArrayList<String> getStore_cd() {
		return store_cd;
	}

	public void setStore_cd(String store_cd) {
		this.store_cd.add(store_cd);
	}

	public ArrayList<String> getSku_id() {
		return sku_id;
	}

	public void setSku_id(String sku_id) {
		this.sku_id.add(sku_id);
	}

	public ArrayList<String> getBrand_cd() {
		return brand_cd;
	}

	public void setBrand_cd(String brand_cd) {
		this.brand_cd.add(brand_cd);
	}

	public ArrayList<String> getSku_sequence() {
		return sku_sequence;
	}

	public void setSku_sequence(String sku_sequence) {
		this.sku_sequence.add(sku_sequence);
	}

	public ArrayList<String> getBrand_sequnence() {
		return brand_sequnence;
	}

	public void setBrand_sequnence(String brand_sequnence) {
		this.brand_sequnence.add(brand_sequnence);
	}

	public ArrayList<String> getProcess_id() {
		return process_id;
	}

	public void setProcess_id(String process_id) {
		this.process_id.add(process_id);
	}

	ArrayList<String>process_id=new ArrayList<>();
}
