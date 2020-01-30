package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class EmpMeetingStatus {
	public String Table_Structure;
	public String getTable_Structure() {
		return Table_Structure;
	}
	public void setTable_Structure(String table_Structure) {
		Table_Structure = table_Structure;
	}
	public ArrayList<String> getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		this.STATUS.add(sTATUS);
	}
	public ArrayList<String> STATUS = new ArrayList<String>();
}
