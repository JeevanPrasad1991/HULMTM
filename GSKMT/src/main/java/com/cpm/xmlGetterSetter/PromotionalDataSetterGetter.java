package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class PromotionalDataSetterGetter {
	
	ArrayList<String> PROCESS_ID = new ArrayList<String>();
	ArrayList<String> KEY_ID = new ArrayList<String>();
	ArrayList<String> CATEGORY_ID = new ArrayList<String>();
	ArrayList<String> SKU_ID = new ArrayList<String>();
	ArrayList<String> PROMOTION = new ArrayList<String>();
	ArrayList<String> REGION_ID = new ArrayList<String>();
	ArrayList<String> STATE_ID = new ArrayList<String>();

	
	public ArrayList<String> getREGION_ID() {
		return REGION_ID;
	}
	public void setREGION_ID(String rEGION_ID) {
		this.REGION_ID.add(rEGION_ID);
	}
	String meta_data;

	public String getMeta_data() {
		return meta_data;
	}
	public void setMeta_data(String meta_data) {
		this.meta_data = meta_data;
	}
	ArrayList<String> ID = new ArrayList<String>();
	public ArrayList<String> getID() {
		return ID;
	}
	public void setID(String iD) {
		this.ID.add(iD);
	}
	public ArrayList<String> getPROCESS_ID() {
		return PROCESS_ID;
	}
	public void setPROCESS_ID(String pROCESS_ID) {
		this.PROCESS_ID.add(pROCESS_ID);
	}
	public ArrayList<String> getKEY_ID() {
		return KEY_ID;
	}
	public void setKEY_ID(String kEY_ID) {
		this.KEY_ID.add(kEY_ID);
	}
	public ArrayList<String> getCATEGORY_ID() {
		return CATEGORY_ID;
	}
	public void setCATEGORY_ID(String cATEGORY_ID) {
		this.CATEGORY_ID.add(cATEGORY_ID);
	}
	public ArrayList<String> getSKU_ID() {
		return SKU_ID;
	}
	public void setSKU_ID(String sKU_ID) {
		this.SKU_ID.add(sKU_ID);
	}
	public ArrayList<String> getPROMOTION() {
		return PROMOTION;
	}
	public void setPROMOTION(String pROMOTION) {
		this.PROMOTION.add(pROMOTION);
	}


	public ArrayList<String> getSTATE_ID() {
		return STATE_ID;
	}

	public void setSTATE_ID(String STATE_ID) {
		this.STATE_ID.add(STATE_ID);
	}
}
