package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class StoreWise_Pss {
	
	public String Table_Structure;
	
	public ArrayList<String> store_id = new ArrayList<String>();
	public ArrayList<String> visit_date = new ArrayList<String>();
	public ArrayList<String> category_id = new ArrayList<String>();
	public ArrayList<String> sos = new ArrayList<String>();
	public ArrayList<String> tot = new ArrayList<String>();
	public ArrayList<String> paid = new ArrayList<String>();
	public ArrayList<String> additional = new ArrayList<String>();
	public ArrayList<String> pss = new ArrayList<String>();
	public ArrayList<String> process_id = new ArrayList<String>();
	public ArrayList<String> period = new ArrayList<String>();
	/////changesssss
	public ArrayList<String> STOCK_SCORE = new ArrayList<String>();
	public ArrayList<String> ASSET_SCORE = new ArrayList<String>();
	public ArrayList<String> PROMO_SCORE = new ArrayList<String>();

	public ArrayList<String> getSTOCK_SCORE() {
		return STOCK_SCORE;
	}

	public void setSTOCK_SCORE(String STOCK_SCORE) {
		this.STOCK_SCORE.add(STOCK_SCORE);
	}

	public ArrayList<String> getASSET_SCORE() {
		return ASSET_SCORE;
	}

	public void setASSET_SCORE(String ASSET_SCORE) {
		this.ASSET_SCORE.add(ASSET_SCORE);
	}

	public ArrayList<String> getPROMO_SCORE() {
		return PROMO_SCORE;
	}

	public void setPROMO_SCORE(String PROMO_SCORE) {
		this.PROMO_SCORE.add(PROMO_SCORE);
	}


	
	public ArrayList<String> getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period.add(period);
	}
	public ArrayList<String> getProcess_id() {
		return process_id;
	}
	public void setProcess_id(String process_id) {
		this.process_id.add(process_id);
	}
	public String getTable_Structure() {
		return Table_Structure;
	}
	public void setTable_Structure(String table_Structure) {
		Table_Structure = table_Structure;
	}
	public ArrayList<String> getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id.add(store_id);
	}
	public ArrayList<String> getVisit_date() {
		return visit_date;
	}
	public void setVisit_date(String visit_date) {
		this.visit_date.add(visit_date);
	}
	public ArrayList<String> getCategory_id() {
		return category_id;
	}
	public void setCategory_id(String category_id) {
		this.category_id.add(category_id);
	}
	public ArrayList<String> getSos() {
		return sos;
	}
	public void setSos(String sos) {
		this.sos.add(sos);
	}
	public ArrayList<String> getTot() {
		return tot;
	}
	public void setTot(String tot) {
		this.tot.add(tot);
	}
	public ArrayList<String> getPaid() {
		return paid;
	}
	public void setPaid(String paid) {
		this.paid.add(paid);
	}
	public ArrayList<String> getAdditional() {
		return additional;
	}
	public void setAdditional(String additional) {
		this.additional.add(additional);
	}
	public ArrayList<String> getPss() {
		return pss;
	}
	public void setPss(String pss) {
		this.pss.add(pss);
	}

	
	

}
