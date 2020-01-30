package com.cpm.xmlGetterSetter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MappingWellnessSos {
	
	public String Table_Structure;
	public ArrayList<String> PROCESS_ID = new ArrayList<String>();
	public ArrayList<String> REGION_ID = new ArrayList<String>();
	public ArrayList<String> STORETYPE_ID = new ArrayList<String>();
	public ArrayList<String> BRAND_ID = new ArrayList<String>();
	public ArrayList<String> SHELF_ID = new ArrayList<String>();
	
	
	public String getTable_Structure() {
		return Table_Structure;
	}
	public void setTable_Structure(String table_Structure) {
		Table_Structure = table_Structure;
	}
	public ArrayList<String> getPROCESS_ID() {
		return PROCESS_ID;
	}
	public void setPROCESS_ID(String pROCESS_ID) {
		this.PROCESS_ID.add(pROCESS_ID);
	}
	public ArrayList<String> getREGION_ID() {
		return REGION_ID;
	}
	public void setREGION_ID(String rEGION_ID) {
		this.REGION_ID.add(rEGION_ID);
	}
	public ArrayList<String> getSTORETYPE_ID() {
		return STORETYPE_ID;
	}
	public void setSTORETYPE_ID(String sTORETYPE_ID) {
		this.STORETYPE_ID.add(sTORETYPE_ID);
	}
	public ArrayList<String> getBRAND_ID() {
		return BRAND_ID;
	}
	public void setBRAND_ID(String bRAND_ID) {
		BRAND_ID.add(bRAND_ID);
	}
	public ArrayList<String> getSHELF_ID() {
		return SHELF_ID;
	}
	public void setSHELF_ID(String sHELF_ID) {
		this.SHELF_ID.add(sHELF_ID);
	}

	

}
