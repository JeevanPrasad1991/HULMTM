package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class TDSGetterSetter {
	
	ArrayList<String> STORE_ID = new ArrayList<String>();
	ArrayList<String> CATEGORY_ID = new ArrayList<String>();
	ArrayList<String> DISPLAY_ID = new ArrayList<String>();
	ArrayList<String> TARGET_QTY = new ArrayList<String>();
	ArrayList<String> PROCESS_ID = new ArrayList<String>();
	ArrayList<String> BRAND_ID = new ArrayList<String>();
	
	ArrayList<String> TYPE = new ArrayList<String>();
	ArrayList<String> UID = new ArrayList<String>();
	
	
	
	
	
	public ArrayList<String> getUID() {
		return UID;
	}
	public void setUID(String uID) {
		this.UID.add(uID);
	}
	public ArrayList<String> getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		this.TYPE.add(tYPE);
	}
	public ArrayList<String> getBRAND_ID() {
		return BRAND_ID;
	}
	public void setBRAND_ID(String bRAND_ID) {
		this.BRAND_ID.add(bRAND_ID);
	}
	public ArrayList<String> getCATEGORY_ID() {
		return CATEGORY_ID;
	}
	public void setCATEGORY_ID(String cATEGORY_ID) {
		this.CATEGORY_ID.add(cATEGORY_ID);
	}
	
	
	public ArrayList<String> getPROCESS_ID() {
		return PROCESS_ID;
	}
	public void setPROCESS_ID(String pROCESS_ID) {
		this.PROCESS_ID.add(pROCESS_ID);
	}
	String meta_data;

	
	public String getMeta_data() {
		return meta_data;
	}
	public void setMeta_data(String meta_data) {
		this.meta_data = meta_data;
	}
	public ArrayList<String> getSTORE_ID() {
		return STORE_ID;
	}
	public void setSTORE_ID(String sTORE_ID) {
		this.STORE_ID.add(sTORE_ID);
	}
	
	public ArrayList<String> getDISPLAY_ID() {
		return DISPLAY_ID;
	}
	public void setDISPLAY_ID(String dISPLAY_ID) {
		this.DISPLAY_ID.add(dISPLAY_ID);
	}
	public ArrayList<String> getTARGET_QTY() {
		return TARGET_QTY;
	}
	public void setTARGET_QTY(String tARGET_QTY) {
		this.TARGET_QTY.add(tARGET_QTY);
	}
	
}
