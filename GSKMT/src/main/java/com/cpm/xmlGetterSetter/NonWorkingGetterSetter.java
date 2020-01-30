package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class NonWorkingGetterSetter {
	
	ArrayList<String> REASON_ID = new ArrayList<String>();
	ArrayList<String> REASON = new ArrayList<String>();
	ArrayList<String> SUB_REASON_ID = new ArrayList<String>();
	ArrayList<String> SUB_REASON = new ArrayList<String>();
	ArrayList<String> ENTRY_ALLOW = new ArrayList<String>();
	
	public ArrayList<String> getENTRY_ALLOW() {
		return ENTRY_ALLOW;
	}
	public void setENTRY_ALLOW(String eNTRY_ALLOW) {
		this.ENTRY_ALLOW.add(eNTRY_ALLOW);
	}
	String reason_meta_data, subreason_meta_data;
	
	public String getReason_meta_data() {
		return reason_meta_data;
	}
	public void setReason_meta_data(String reason_meta_data) {
		this.reason_meta_data = reason_meta_data;
	}
	public String getSubreason_meta_data() {
		return subreason_meta_data;
	}
	public void setSubreason_meta_data(String subreason_meta_data) {
		this.subreason_meta_data = subreason_meta_data;
	}
	public ArrayList<String> getREASON_ID() {
		return REASON_ID;
	}
	public void setREASON_ID(String rEASON_ID) {
		this.REASON_ID.add(rEASON_ID);
	}
	public ArrayList<String> getREASON() {
		return REASON;
	}
	public void setREASON(String rEASON) {
		this.REASON.add(rEASON);
	}
	public ArrayList<String> getSUB_REASON_ID() {
		return SUB_REASON_ID;
	}
	public void setSUB_REASON_ID(String sUB_REASON_ID) {
		this.SUB_REASON_ID.add(sUB_REASON_ID);
	}
	public ArrayList<String> getSUB_REASON() {
		return SUB_REASON;
	}
	public void setSUB_REASON(String sUB_REASON) {
		this.SUB_REASON.add(sUB_REASON);
	}
	

}
