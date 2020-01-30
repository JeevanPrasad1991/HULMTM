package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class CompetitionGetterSetter {
	
	ArrayList<String> company_id = new ArrayList<String>();
	ArrayList<String> company = new ArrayList<String>();
	
	public ArrayList<String> getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company.add(company);
	}

	ArrayList<String> isCompetitor = new ArrayList<String>();
	
	public String meta_data;
	
	public ArrayList<String> getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id.add(company_id);
	}

	public ArrayList<String> getIsCompetitor() {
		return isCompetitor;
	}

	public void setIsCompetitor(String isCompetitor) {
		this.isCompetitor.add(isCompetitor);
	}

	public String getMeta_data() {
		return meta_data;
	}

	public void setMeta_data(String meta_data) {
		this.meta_data = meta_data;
	}

	

}
